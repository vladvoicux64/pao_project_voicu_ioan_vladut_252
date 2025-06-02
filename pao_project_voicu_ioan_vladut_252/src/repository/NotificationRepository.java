package repository;

import model.Notification;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private UserRepository userRepository = new UserRepository();
    
    public Notification save(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (recipient_id, message, type, creation_date, is_read) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, notification.getRecipient().getId());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getType().name());
            stmt.setTimestamp(4, new Timestamp(notification.getCreationDate().getTime()));
            stmt.setBoolean(5, notification.isRead());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Notification(generatedKeys.getInt(1), notification.getRecipient(), 
                                          notification.getMessage(), notification.getType());
                }
            }
        }
        return notification;
    }
    
    public List<Notification> findByRecipientId(int recipientId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? ORDER BY creation_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, recipientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User recipient = userRepository.findById(rs.getInt("recipient_id"));
                Notification.NotificationType type = Notification.NotificationType.valueOf(rs.getString("type"));
                Notification notification = new Notification(rs.getInt("id"), recipient, 
                                                           rs.getString("message"), type);
                notification.setRead(rs.getBoolean("is_read"));
                notifications.add(notification);
            }
        }
        return notifications;
    }
    
    public void markAsRead(int id) throws SQLException {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}


