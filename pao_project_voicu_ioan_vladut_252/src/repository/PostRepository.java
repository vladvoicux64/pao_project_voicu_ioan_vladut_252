package repository;

import model.Post;
import model.User;
import model.Topic;
import model.Tag;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostRepository {
    private UserRepository userRepository = new UserRepository();
    private TopicRepository topicRepository = new TopicRepository();
    private TagRepository tagRepository = new TagRepository();
    
    public Post save(Post post) throws SQLException {
        String sql = "INSERT INTO posts (title, content, author_id, topic_id, creation_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, post.getAuthor().getId());
            stmt.setInt(4, post.getTopic().getId());
            stmt.setTimestamp(5, new Timestamp(post.getCreationDate().getTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int postId = generatedKeys.getInt(1);
                    savePostTags(postId, post.getTags());
                    return new Post(postId, post.getTitle(), post.getContent(), post.getAuthor(), post.getTopic());
                }
            }
        }
        return post;
    }
    
    private void savePostTags(int postId, Set<Tag> tags) throws SQLException {
        String sql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Tag tag : tags) {
                stmt.setInt(1, postId);
                stmt.setInt(2, tag.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    public Post findById(int id) throws SQLException {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Topic topic = topicRepository.findById(rs.getInt("topic_id"));
                Post post = new Post(rs.getInt("id"), rs.getString("title"), 
                                   rs.getString("content"), author, topic);
                
                Set<Tag> tags = findTagsByPostId(id);
                for (Tag tag : tags) {
                    post.addTag(tag);
                }
                
                return post;
            }
        }
        return null;
    }
    
    private Set<Tag> findTagsByPostId(int postId) throws SQLException {
        Set<Tag> tags = new HashSet<>();
        String sql = "SELECT t.* FROM tags t JOIN post_tags pt ON t.id = pt.tag_id WHERE pt.post_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tags.add(new Tag(rs.getInt("id"), rs.getString("name")));
            }
        }
        return tags;
    }
    
    public List<Post> findByTopicId(int topicId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE topic_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, topicId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Topic topic = topicRepository.findById(rs.getInt("topic_id"));
                Post post = new Post(rs.getInt("id"), rs.getString("title"), 
                                   rs.getString("content"), author, topic);
                
                Set<Tag> tags = findTagsByPostId(post.getId());
                for (Tag tag : tags) {
                    post.addTag(tag);
                }
                
                posts.add(post);
            }
        }
        return posts;
    }
    
    public List<Post> findAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Topic topic = topicRepository.findById(rs.getInt("topic_id"));
                Post post = new Post(rs.getInt("id"), rs.getString("title"), 
                                   rs.getString("content"), author, topic);
                
                Set<Tag> tags = findTagsByPostId(post.getId());
                for (Tag tag : tags) {
                    post.addTag(tag);
                }
                
                posts.add(post);
            }
        }
        return posts;
    }
    
    public void update(Post post) throws SQLException {
        String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, post.getId());
            
            stmt.executeUpdate();
            
            deletePostTags(post.getId());
            savePostTags(post.getId(), post.getTags());
        }
    }
    
    private void deletePostTags(int postId) throws SQLException {
        String sql = "DELETE FROM post_tags WHERE post_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        deletePostTags(id);
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public List<Post> findByAuthorId(int authorId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE author_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User author = userRepository.findById(rs.getInt("author_id"));
                Topic topic = topicRepository.findById(rs.getInt("topic_id"));
                Post post = new Post(rs.getInt("id"), rs.getString("title"), 
                                   rs.getString("content"), author, topic);
                
                Set<Tag> tags = findTagsByPostId(post.getId());
                for (Tag tag : tags) {
                    post.addTag(tag);
                }
                
                posts.add(post);
            }
        }
        return posts;
    }
}


