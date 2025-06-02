package service;

import model.Post;
import model.Topic;
import model.User;
import repository.PostRepository;
import java.sql.SQLException;
import java.util.List;

public class PostService {
    private PostRepository postRepository = new PostRepository();
    private AuditService auditService = new AuditService();

    public Post createPost(String title, String content, User author, Topic topic) {
        try {
            Post post = new Post(0, title, content, author, topic);
            Post savedPost = postRepository.save(post);
            if (savedPost != null) {
                auditService.logUserAction(author.getUsername(), "CREATED_POST_" + title);
            }
            return savedPost;
        } catch (SQLException e) {
            auditService.log("POST_CREATION_FAILED_" + author.getUsername());
            e.printStackTrace();
            return null;
        }
    }

    public Post getPostById(int id) {
        try {
            auditService.logSystemAction("GET_POST_BY_ID_" + id);
            return postRepository.findById(id);
        } catch (SQLException e) {
            auditService.log("GET_POST_BY_ID_FAILED_" + id);
            e.printStackTrace();
            return null;
        }
    }

    public List<Post> getAllPosts() {
        try {
            auditService.logSystemAction("GET_ALL_POSTS");
            return postRepository.findAll();
        } catch (SQLException e) {
            auditService.log("GET_ALL_POSTS_FAILED");
            e.printStackTrace();
            return null;
        }
    }

    public List<Post> getPostsByTopic(int topicId) {
        try {
            auditService.logSystemAction("GET_POSTS_BY_TOPIC_" + topicId);
            return postRepository.findByTopicId(topicId);
        } catch (SQLException e) {
            auditService.log("GET_POSTS_BY_TOPIC_FAILED_" + topicId);
            e.printStackTrace();
            return null;
        }
    }

    public List<Post> getPostsByAuthor(int authorId) {
        try {
            auditService.logSystemAction("GET_POSTS_BY_AUTHOR_" + authorId);
            return postRepository.findByAuthorId(authorId);
        } catch (SQLException e) {
            auditService.log("GET_POSTS_BY_AUTHOR_FAILED_" + authorId);
            e.printStackTrace();
            return null;
        }
    }

    public boolean updatePost(Post post) {
        try {
            postRepository.update(post);
            auditService.logUserAction(post.getAuthor().getUsername(), "UPDATED_POST_" + post.getTitle());
            return true;
        } catch (SQLException e) {
            auditService.log("POST_UPDATE_FAILED_" + post.getId());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePost(int id) {
        try {
            Post post = postRepository.findById(id);
            if (post != null) {
                postRepository.delete(id);
                auditService.logUserAction(post.getAuthor().getUsername(), "DELETED_POST_" + post.getTitle());
                return true;
            }
            return false;
        } catch (SQLException e) {
            auditService.log("POST_DELETE_FAILED_" + id);
            e.printStackTrace();
            return false;
        }
    }
}
