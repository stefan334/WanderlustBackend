package com.travel.Wanderlust.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.travel.Wanderlust.Util.LikeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "post_likes")
@IdClass(LikeId.class)
public class Like {

    @Id
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
