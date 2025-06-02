package repository;

import model.Tag;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {
    
    public Tag save(Tag tag) throws SQLException {
        String sql = "INSERT INTO tags (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tag.getName());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Tag(generatedKeys.getInt(1), tag.getName());
                }
            }
        }
        return tag;
    }
    
    public Tag findById(int id) throws SQLException {
        String sql = "SELECT * FROM tags WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Tag(rs.getInt("id"), rs.getString("name"));
            }
        }
        return null;
    }
    
    public Tag findByName(String name) throws SQLException {
        String sql = "SELECT * FROM tags WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Tag(rs.getInt("id"), rs.getString("name"));
            }
        }
        return null;
    }
    
    public List<Tag> findAll() throws SQLException {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM tags";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tags.add(new Tag(rs.getInt("id"), rs.getString("name")));
            }
        }
        return tags;
    }
    
    public void update(Tag tag) throws SQLException {
        String sql = "UPDATE tags SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tag.getName());
            stmt.setInt(2, tag.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM tags WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}


