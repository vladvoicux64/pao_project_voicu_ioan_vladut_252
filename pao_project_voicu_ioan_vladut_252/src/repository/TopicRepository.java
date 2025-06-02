package repository;

import model.Topic;
import model.User;
import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TopicRepository {
    private UserRepository userRepository = new UserRepository();
    
    public Topic save(Topic topic) throws SQLException {
        String sql = "INSERT INTO topics (title, description, creator_id, category_id, creation_date, locked) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, topic.getTitle());
            stmt.setString(2, topic.getDescription());
            stmt.setInt(3, topic.getCreator().getId());
            stmt.setInt(4, topic.getCategory().getId());
            stmt.setTimestamp(5, new Timestamp(topic.getCreationDate().getTime()));
            stmt.setBoolean(6, topic.isLocked());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Topic(generatedKeys.getInt(1), topic.getTitle(), topic.getDescription(), topic.getCreator(), topic.getCategory());
                }
            }
        }
        return topic;
    }
    
    public Topic findById(int id) throws SQLException {
        String sql = "SELECT * FROM topics WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User creator = userRepository.findById(rs.getInt("creator_id"));
                Category category = new CategoryRepository().findById(rs.getInt("category_id"));
                Topic topic = new Topic(rs.getInt("id"), rs.getString("title"), 
                                      rs.getString("description"), creator, category);
                topic.setLocked(rs.getBoolean("locked"));
                return topic;
            }
        }
        return null;
    }
    
    public List<Topic> findByCategoryId(int categoryId) throws SQLException {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topics WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User creator = userRepository.findById(rs.getInt("creator_id"));
                Category category = new Category(categoryId, "", "");
                Topic topic = new Topic(rs.getInt("id"), rs.getString("title"), 
                                      rs.getString("description"), creator, category);
                topic.setLocked(rs.getBoolean("locked"));
                topics.add(topic);
            }
        }
        return topics;
    }
    
    public List<Topic> findAll() throws SQLException {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topics";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User creator = userRepository.findById(rs.getInt("creator_id"));
                Category category = new CategoryRepository().findById(rs.getInt("category_id"));
                Topic topic = new Topic(rs.getInt("id"), rs.getString("title"), 
                                      rs.getString("description"), creator, category);
                topic.setLocked(rs.getBoolean("locked"));
                topics.add(topic);
            }
        }
        return topics;
    }
    
    public void update(Topic topic) throws SQLException {
        String sql = "UPDATE topics SET title = ?, description = ?, locked = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, topic.getTitle());
            stmt.setString(2, topic.getDescription());
            stmt.setBoolean(3, topic.isLocked());
            stmt.setInt(4, topic.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM topics WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}


