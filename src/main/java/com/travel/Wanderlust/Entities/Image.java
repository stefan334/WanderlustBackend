package com.travel.Wanderlust.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "images", uniqueConstraints={@UniqueConstraint(columnNames={"image_id"})})
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id;
    @Column(name = "name")
    private String name;
    @Column(name = "location")
    private String location;
    @ManyToOne
    @JoinColumn(name="id")
    private User user_id;



}
