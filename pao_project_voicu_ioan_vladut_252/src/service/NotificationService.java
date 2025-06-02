package service;

import model.Notification;
import model.User;
import model.Comment;
import model.Post;
import model.Reaction;
import repository.NotificationRepository;
import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private NotificationRepository notificationRepository = new NotificationRepository();

    public Notification createNotification(User recipient, String message, Notification.NotificationType type) {
        try {
            Notification notification = new Notification(0, recipient, message, type);
            return notificationRepository.save(notification);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Notification> getNotificationsForUser(int userId) {
        try {
            return notificationRepository.findByRecipientId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean markNotificationAsRead(int notificationId) {
        try {
            notificationRepository.markAsRead(notificationId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteNotification(int id) {
        try {
            notificationRepository.delete(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void notifyNewComment(Comment comment) {
        try {
            User postAuthor = comment.getPost().getAuthor();
            if (!postAuthor.equals(comment.getAuthor())) {
                String message = comment.getAuthor().getUsername() + " commented on your post: " + comment.getPost().getTitle();
                createNotification(postAuthor, message, Notification.NotificationType.NEW_COMMENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyNewReaction(Reaction reaction, Post post) {
        try {
            User postAuthor = post.getAuthor();
            if (!postAuthor.equals(reaction.getUser())) {
                String message = reaction.getUser().getUsername() + " reacted to your post: " + post.getTitle();
                createNotification(postAuthor, message, Notification.NotificationType.NEW_REACTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
