package repository;

import model.Reaction;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReactionRepository {
    private UserRepository userRepository = new UserRepository();
    
    public Reaction save(Reaction reaction, int postId, Integer commentId) throws SQLException {
        String sql = "INSERT INTO reactions (user_id, type, creation_date, post_id, comment_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reaction.getUser().getId());
            stmt.setString(2, reaction.getType().name());
            stmt.setTimestamp(3, new Timestamp(reaction.getCreationDate().getTime()));
            stmt.setInt(4, postId);
            if (commentId != null) {
                stmt.setInt(5, commentId);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Reaction(generatedKeys.getInt(1), reaction.getUser(), reaction.getType());
                }
            }
        }
        return reaction;
    }
    
    public List<Reaction> findByPostId(int postId) throws SQLException {
        List<Reaction> reactions = new ArrayList<>();
        String sql = "SELECT * FROM reactions WHERE post_id = ? AND comment_id IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = userRepository.findById(rs.getInt("user_id"));
                Reaction.ReactionType type = Reaction.ReactionType.valueOf(rs.getString("type"));
                reactions.add(new Reaction(rs.getInt("id"), user, type));
            }
        }
        return reactions;
    }
    
    public List<Reaction> findByCommentId(int commentId) throws SQLException {
        List<Reaction> reactions = new ArrayList<>();
        String sql = "SELECT * FROM reactions WHERE comment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = userRepository.findById(rs.getInt("user_id"));
                Reaction.ReactionType type = Reaction.ReactionType.valueOf(rs.getString("type"));
                reactions.add(new Reaction(rs.getInt("id"), user, type));
            }
        }
        return reactions;
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}


