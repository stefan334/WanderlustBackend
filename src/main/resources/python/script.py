import sys
import psycopg2
from annoy import AnnoyIndex
from scipy.sparse import dok_matrix

# Load the target user ID and visited locations from command-line arguments
target_user_id = int(sys.argv[1])

# Connect to the database and retrieve user data
connection = psycopg2.connect(host='localhost', port='5432', database='wanderlust', user='ivan334', password='stefan334')
cursor = connection.cursor()
cursor.execute("SELECT u.id, array_agg(l.id) FROM users u JOIN user_visited_locations uv ON u.id = uv.user_id JOIN locations l ON uv.location_id = l.id GROUP BY u.id")
user_vectors = cursor.fetchall()
target_user_visited_locations = None
for user_id, visited_locations in user_vectors:
    if user_id == target_user_id:
        target_user_visited_locations = visited_locations
        break

# Determine the maximum location ID
max_location_id = max(max(visited_locations), max(location_id for (_, visited_locations) in user_vectors for location_id in visited_locations))

# Create a sparse matrix to store the user vectors
user_vectors_sparse = dok_matrix((40, max_location_id + 1), dtype=float)

# Add vectors of users to the sparse matrix
for i, (user_id, visited_locations) in enumerate(user_vectors):
    for location_id in visited_locations:
        user_vectors_sparse[user_id, location_id] = 1.0

# Load the pre-built Annoy index
annoy_index = AnnoyIndex(user_vectors_sparse.shape[1], 'angular')  # Assuming locations are represented as high-dimensional vectors

# Add vectors of users to the Annoy index
for i, (user_id, visited_locations) in enumerate(user_vectors):
    annoy_index.add_item(user_id, user_vectors_sparse[i, :].toarray().flatten())


# Build the Annoy index
annoy_index.build(n_trees=1000)

# Example usage: Find the nearest neighbors to the target user
nearest_neighbors = annoy_index.get_nns_by_item(target_user_id, n=10)  # Increase the number of neighbors to consider

# Retrieve the recommended locations from the nearest neighbors
recommended_locations = []
for neighbor_user_id in nearest_neighbors:
    # Fetch the visited locations of the neighbor user from the database
    cursor.execute("SELECT location_id FROM user_visited_locations l WHERE user_id = %s", (neighbor_user_id,))
    neighbor_visited_locations = cursor.fetchall()

    # Add unique locations from the neighbor to the recommendations
    recommended_locations.extend(
        [location_id for (location_id,) in neighbor_visited_locations if location_id not in recommended_locations]
    )

    # Stop when we have at least 5 recommended locations
    if len(recommended_locations) >= 5:
        break

# Remove visited locations from the recommendations
recommended_locations = list(set(recommended_locations) - set(target_user_visited_locations))

print(recommended_locations)

# Close the database connection
cursor.close()
connection.close()
