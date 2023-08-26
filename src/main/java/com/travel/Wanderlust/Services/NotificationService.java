package com.travel.Wanderlust.Services;

import com.travel.Wanderlust.Entities.Notification;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.NotificationRepository;
import com.travel.Wanderlust.Util.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void createFollowNotification(User recipient, User sender) {
        Notification notification = new Notification();
        notification.setType(NotificationType.FOLLOW);
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public void createLikeNotification(User recipient, User sender, Long postId) {
        Notification notification = new Notification();
        notification.setType(NotificationType.LIKE);
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setPostId(postId);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public void createCommentNotification(User recipient, User sender, Long postId) {
        Notification notification = new Notification();
        notification.setType(NotificationType.COMMENT);
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setPostId(postId);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(User recipient) {
        return notificationRepository.findByRecipientAndReadOrderByCreatedAtDesc(recipient, false);
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

}