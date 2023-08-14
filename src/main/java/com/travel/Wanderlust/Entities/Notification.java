package com.travel.Wanderlust.Entities;

import com.travel.Wanderlust.Util.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne
    private User recipient; // The user who receives the notification

    @ManyToOne
    private User sender; // The user who triggered the action

    private Long postId; // ID of the relevant post (for likes and comments)

    private boolean read; // Whether the notification has been read

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
}

