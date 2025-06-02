package repository;

import model.Comment;
import model.User;
import model.Post;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {
    private UserRepository userRepository = new UserRepository();
    private PostRepository postRepository = new PostRepository();
    
    public Comment save(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (content, author_id, post_id, creation_date, parent_comment_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, comment.getContent());
            stmt.setInt(2, comment.getAuthor().getId());
            stmt.setInt(3, comment.getPost().getId());
            stmt.setTimestamp(4, new Timestamp(comment.getCreationDate().getTime()));
            if (comment.getParentComment() != null) {
                stmt.setInt(5, comment.getParentComment().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Comment(generatedKeys.getInt(1), comment.getContent(), 
                                     comment.getAuthor(), comment.getPost(), comment.getParentComment());
                }
            }
        }
        return comment;
    }
    
    public Comment findById(int id) throws SQLException {
        String sql = "SELECT * FROM comments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Post post = postRepository.findById(rs.getInt("post_id"));
                Comment parentComment = null;
                if (rs.getInt("parent_comment_id") != 0) {
                    parentComment = findById(rs.getInt("parent_comment_id"));
                }
                
                Comment comment = new Comment(rs.getInt("id"), rs.getString("content"), 
                                            author, post, parentComment);
                
                List<Comment> replies = findReplies(id);
                for (Comment reply : replies) {
                    comment.addReply(reply);
                }
                
                return comment;
            }
        }
        return null;
    }
    
    private List<Comment> findReplies(int parentCommentId) throws SQLException {
        List<Comment> replies = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE parent_comment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, parentCommentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Post post = postRepository.findById(rs.getInt("post_id"));
                Comment reply = new Comment(rs.getInt("id"), rs.getString("content"), author, post);
                replies.add(reply);
            }
        }
        return replies;
    }
    
    public List<Comment> findByPostId(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ? AND parent_comment_id IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Post post = postRepository.findById(rs.getInt("post_id"));
                Comment comment = new Comment(rs.getInt("id"), rs.getString("content"), author, post);
                
                List<Comment> replies = findReplies(comment.getId());
                for (Comment reply : replies) {
                    comment.addReply(reply);
                }
                
                comments.add(comment);
            }
        }
        return comments;
    }
    
    public List<Comment> findAll() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Post post = postRepository.findById(rs.getInt("post_id"));
                Comment parentComment = null;
                if (rs.getInt("parent_comment_id") != 0) {
                    parentComment = findById(rs.getInt("parent_comment_id"));
                }
                
                Comment comment = new Comment(rs.getInt("id"), rs.getString("content"), 
                                            author, post, parentComment);
                comments.add(comment);
            }
        }
        return comments;
    }
    
    public void update(Comment comment) throws SQLException {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, comment.getContent());
            stmt.setInt(2, comment.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}


