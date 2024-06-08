import sys
import psycopg2
from annoy import AnnoyIndex
from scipy.sparse import dok_matrix

target_user_id = int(sys.argv[1])
connection = psycopg2.connect(host='postgres://ivan334:oHdxWo45goydVzKTsTBDUVfGfRSQdlao@dpg-cpibumi1hbls73bfmen0-a.oregon-postgres.render.com/wanderlust_254l', port='5432', database='wanderlust', user='ivan334', password='oHdxWo45goydVzKTsTBDUVfGfRSQdlao')
cursor = connection.cursor()
cursor.execute("SELECT u.id, array_agg(l.id) FROM users u JOIN user_visited_locations uv ON u.id = uv.user_id JOIN locations l ON uv.location_id = l.id GROUP BY u.id")
user_vectors = cursor.fetchall()
target_user_visited_locations = None
for user_id, visited_locations in user_vectors:
    if user_id == target_user_id:
        target_user_visited_locations = visited_locations
        break
NUMBER_OF_USERS = 40
MAX_RECOMMENDED_LOCATIONS = 5
max_location_id = max(max(visited_locations), max(location_id for (_, visited_locations) in user_vectors for location_id in visited_locations))

user_vectors_sparse = dok_matrix((NUMBER_OF_USERS, max_location_id + 1), dtype=float)

for i, (user_id, visited_locations) in enumerate(user_vectors):
    for location_id in visited_locations:
        user_vectors_sparse[user_id, location_id] = 1.0

annoy_index = AnnoyIndex(user_vectors_sparse.shape[1], 'angular')

for i, (user_id, visited_locations) in enumerate(user_vectors):
    annoy_index.add_item(user_id, user_vectors_sparse[i, :].toarray().flatten())


annoy_index.build(n_trees=1000)

nearest_neighbors = annoy_index.get_nns_by_item(target_user_id, n=5)

recommended_locations = []
for neighbor_user_id in nearest_neighbors:
    cursor.execute("SELECT location_id FROM user_visited_locations l WHERE user_id = %s", (neighbor_user_id,))
    neighbor_visited_locations = cursor.fetchall()

    new_recommendations = [location_id for (location_id,) in neighbor_visited_locations if location_id not in recommended_locations]
    recommended_locations.extend(new_recommendations[:MAX_RECOMMENDED_LOCATIONS - len(recommended_locations) + 1])
    recommended_locations = list(set(recommended_locations) - set(target_user_visited_locations))

    if len(recommended_locations) >= 5:


        print(recommended_locations)

        cursor.close()
        connection.close()
        quit()