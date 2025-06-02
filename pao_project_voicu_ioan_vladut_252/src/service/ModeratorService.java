package service;

import model.Category;
import repository.ModeratorRepository;
import repository.PostRepository;
import repository.CommentRepository;
import java.sql.SQLException;
import java.util.List;

public class ModeratorService {
    private ModeratorRepository moderatorRepository = new ModeratorRepository();
    private PostRepository postRepository = new PostRepository();
    private CommentRepository commentRepository = new CommentRepository();
    
    public boolean addModeratedCategory(int moderatorId, int categoryId) {
        try {
            moderatorRepository.addModeratedCategory(moderatorId, categoryId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeModeratedCategory(int moderatorId, int categoryId) {
        try {
            moderatorRepository.removeModeratedCategory(moderatorId, categoryId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Category> getModeratedCategories(int moderatorId) {
        try {
            return moderatorRepository.findModeratedCategories(moderatorId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean deletePost(int postId) {
        try {
            postRepository.delete(postId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteComment(int commentId) {
        try {
            commentRepository.delete(commentId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

