package service;

import model.Comment;
import model.User;
import model.Post;
import model.Reaction;
import repository.CommentRepository;
import repository.ReactionRepository;
import java.sql.SQLException;
import java.util.List;

public class CommentService {
    private CommentRepository commentRepository = new CommentRepository();
    private ReactionRepository reactionRepository = new ReactionRepository();
    
    public Comment createComment(String content, User author, Post post) {
        try {
            Comment comment = new Comment(0, content, author, post);
            return commentRepository.save(comment);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Comment createReply(String content, User author, Post post, Comment parentComment) {
        try {
            Comment reply = new Comment(0, content, author, post, parentComment);
            return commentRepository.save(reply);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Comment getCommentById(int id) {
        try {
            Comment comment = commentRepository.findById(id);
            if (comment != null) {
                List<Reaction> reactions = reactionRepository.findByCommentId(id);
                for (Reaction reaction : reactions) {
                    comment.addReaction(reaction);
                }
            }
            return comment;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Comment> getCommentsByPost(int postId) {
        try {
            return commentRepository.findByPostId(postId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateComment(Comment comment) {
        try {
            commentRepository.update(comment);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteComment(int id) {
        try {
            commentRepository.delete(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addReactionToComment(Comment comment, Reaction reaction) {
        try {
            reactionRepository.save(reaction, 0, comment.getId());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


