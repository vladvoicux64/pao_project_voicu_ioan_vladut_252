package service;

import model.*;
import repository.ReactionRepository;
import java.sql.SQLException;

public class ReactionService {
    private ReactionRepository reactionRepository = new ReactionRepository();
    private NotificationService notificationService = new NotificationService();

    public Reaction addReactionToPost(User user, Post post, Reaction.ReactionType type) {
        try {
            Reaction reaction = new Reaction(0, user, type);
            Reaction savedReaction = reactionRepository.save(reaction, post.getId(), null);
            if (savedReaction != null) {
                createReactionNotification(user, post.getAuthor(), post, type);
            }
            return savedReaction;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Reaction addReactionToComment(User user, Comment comment, Reaction.ReactionType type) {
        try {
            Reaction reaction = new Reaction(0, user, type);
            Reaction savedReaction = reactionRepository.save(reaction, 0, comment.getId());
            if (savedReaction != null) {
                createReactionNotification(user, comment.getAuthor(), comment.getPost(), type);
            }
            return savedReaction;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createReactionNotification(User reactor, User recipient, Post post, Reaction.ReactionType type) {
        if (!reactor.equals(recipient)) {
            String message = reactor.getUsername() + " reacted with " + type.toString() + " to your post: " + post.getTitle();
            notificationService.createNotification(recipient, message, Notification.NotificationType.NEW_REACTION);
        }
    }

    public void createReactionNotification(User reactor, User recipient, Comment comment, Reaction.ReactionType type) {
        if (!reactor.equals(recipient)) {
            String message = reactor.getUsername() + " reacted with " + type.toString() + " to your comment";
            notificationService.createNotification(recipient, message, Notification.NotificationType.NEW_REACTION);
        }
    }
}
