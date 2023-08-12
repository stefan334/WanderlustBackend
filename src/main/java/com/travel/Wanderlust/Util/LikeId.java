package com.travel.Wanderlust.Util;

import java.io.Serializable;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class LikeId implements Serializable {

    private Long post;
    private Long user;

    // Constructors, getters, and setters
}
