package com.travel.Wanderlust.Repositories;

import com.travel.Wanderlust.Entities.Notification;
import com.travel.Wanderlust.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndReadOrderByCreatedAtDesc(User recipient, boolean b);
}
