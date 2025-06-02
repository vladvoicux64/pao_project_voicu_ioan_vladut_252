package service;

import model.*;
import java.util.List;


public class ForumFacade {
    private final UserService userService;
    private final CategoryService categoryService;
    private final TopicService topicService;
    private final PostService postService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final AdminService adminService;
    private final ModeratorService moderatorService;

    public ForumFacade() {
        this.userService = new UserService();
        this.categoryService = new CategoryService();
        this.topicService = new TopicService();
        this.postService = new PostService();
        this.commentService = new CommentService();
        this.notificationService = new NotificationService();
        this.adminService = new AdminService();
        this.moderatorService = new ModeratorService();
    }

    public User registerUser(String username, String email, String password) {
        return userService.createUser(username, email, password);
    }

    public User authenticateUser(String username, String password) {
        if (userService.authenticateUser(username, password)) {
            return userService.getUserByUsername(username);
        }
        return null;
    }

    public boolean banUser(int userId) {
        return adminService.banUser(userId);
    }

    public boolean unbanUser(int userId) {
        return adminService.unbanUser(userId);
    }

    public List<User> getBannedUsers() {
        return adminService.getBannedUsers();
    }

    public Category createCategory(String name, String description) {
        return categoryService.createCategory(name, description);
    }

    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public Topic createTopic(String title, String description, User creator, Category category) {
        return topicService.createTopic(title, description, creator, category);
    }

    public List<Topic> getTopicsByCategory(int categoryId) {
        return topicService.getTopicsByCategory(categoryId);
    }

    public Post createPost(String title, String content, User author, Topic topic) {
        return postService.createPost(title, content, author, topic);
    }

    public Post getPostById(int id) {
        return postService.getPostById(id);
    }

    public List<Post> getPostsByTopic(int topicId) {
        return postService.getPostsByTopic(topicId);
    }

    public List<Post> getPostsByAuthor(int authorId) {
        return postService.getPostsByAuthor(authorId);
    }

    public boolean deletePost(int postId) {
        return postService.deletePost(postId);
    }

    public Comment addCommentToPost(String content, User author, Post post) {
        Comment comment = commentService.createComment(content, author, post);
        if (comment != null) {
            notificationService.notifyNewComment(comment);
        }
        return comment;
    }

    public Comment replyToComment(String content, User author, Post post, Comment parentComment) {
        return commentService.createReply(content, author, post, parentComment);
    }

    public List<Comment> getCommentsByPost(int postId) {
        return commentService.getCommentsByPost(postId);
    }

    public boolean deleteComment(int commentId) {
        return commentService.deleteComment(commentId);
    }

    public Notification createNotification(User recipient, String message, Notification.NotificationType type) {
        return notificationService.createNotification(recipient, message, type);
    }

    public List<Notification> getNotificationsForUser(int userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    public boolean markNotificationAsRead(int notificationId) {
        return notificationService.markNotificationAsRead(notificationId);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public User getUserById(int id) {
        return userService.getUserById(id);
    }

    public User getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    public boolean updateUser(User user) {
        return userService.updateUser(user);
    }

    public boolean deleteUser(int id) {
        return userService.deleteUser(id);
    }

    public Category getCategoryById(int id) {
        return categoryService.getCategoryById(id);
    }

    public boolean updateCategory(Category category) {
        return categoryService.updateCategory(category);
    }

    public boolean deleteCategory(int id) {
        return categoryService.deleteCategory(id);
    }

    public Topic getTopicById(int id) {
        return topicService.getTopicById(id);
    }

    public boolean updateTopic(Topic topic) {
        return topicService.updateTopic(topic);
    }

    public boolean deleteTopic(int id) {
        return topicService.deleteTopic(id);
    }

    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    public boolean updatePost(Post post) {
        return postService.updatePost(post);
    }

    public Comment getCommentById(int id) {
        return commentService.getCommentById(id);
    }

    public boolean updateComment(Comment comment) {
        return commentService.updateComment(comment);
    }

    public boolean addModeratedCategory(int moderatorId, int categoryId) {
        return moderatorService.addModeratedCategory(moderatorId, categoryId);
    }

    public boolean removeModeratedCategory(int moderatorId, int categoryId) {
        return moderatorService.removeModeratedCategory(moderatorId, categoryId);
    }

    public List<Category> getModeratedCategories(int moderatorId) {
        return moderatorService.getModeratedCategories(moderatorId);
    }

    public boolean promoteToModerator(int userId) {
        return adminService.promoteToModerator(userId);
    }

    public boolean promoteToAdmin(int userId) {
        return adminService.promoteToAdmin(userId);
    }
}
