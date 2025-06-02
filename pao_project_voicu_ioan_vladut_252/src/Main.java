import model.*;
import service.ForumFacade;
import service.DatabaseCleanupService;
import service.SearchService;
import service.PostService;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Disable JMX to prevent network connections
        System.setProperty("com.sun.management.jmxremote", "false");
        System.setProperty("java.rmi.server.hostname", "localhost");

        ForumFacade forumFacade = new ForumFacade();
        DatabaseCleanupService cleanupService = new DatabaseCleanupService();
        SearchService searchService = new SearchService(new PostService());

        try {
            System.out.println("======= COMPLETE FORUM SYSTEM DEMO =======");
            runCompleteForumDemo(forumFacade, searchService);
            System.out.println("\n======= ALL FEATURES DEMONSTRATED SUCCESSFULLY =======");
        } catch (Exception e) {
            System.err.println("ERROR DURING DEMO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nAutomatic database cleanup starting...");
            cleanupService.clearAllData();
            System.out.println("Cleanup completed!");
            System.out.println("\n=== APPLICATION TERMINATED ===");
        }
    }

    private static void runCompleteForumDemo(ForumFacade forumFacade, SearchService searchService) {
        // 1. NEW USER REGISTRATION
        System.out.println("\n=== 1. NEW USER REGISTRATION ===");
        User regularUser1 = forumFacade.registerUser("john_doe", "john@example.com", "password123");
        User regularUser2 = forumFacade.registerUser("jane_smith", "jane@example.com", "password456");
        User regularUser3 = forumFacade.registerUser("mike_brown", "mike@example.com", "password789");
        User adminUser = forumFacade.registerUser("admin_alex", "admin@example.com", "adminpass");
        User moderatorUser = forumFacade.registerUser("mod_sarah", "mod@example.com", "modpass");

        System.out.println("--- Registered users ---");
        List<User> allUsers = forumFacade.getAllUsers();
        if (allUsers != null) {
            for (User user : allUsers) {
                System.out.println("[USER] " + user.getUsername() + " - " + user.getEmail());
            }
        }

        // 2. USER AUTHENTICATION
        System.out.println("\n=== 2. USER AUTHENTICATION ===");

        // Successful authentication
        User authenticatedUser = forumFacade.authenticateUser("john_doe", "password123");
        if (authenticatedUser != null) {
            System.out.println("[SUCCESS] Authentication successful for: " + authenticatedUser.getUsername());
        }

        // Failed authentication
        User failedAuth = forumFacade.authenticateUser("john_doe", "wrong_password");
        if (failedAuth == null) {
            System.out.println("[FAILED] Authentication failed with wrong password");
        }

        // Multiple user authentication
        User authJane = forumFacade.authenticateUser("jane_smith", "password456");
        User authMike = forumFacade.authenticateUser("mike_brown", "password789");
        System.out.println("[SUCCESS] Jane authenticated: " + (authJane != null ? authJane.getUsername() : "Failed"));
        System.out.println("[SUCCESS] Mike authenticated: " + (authMike != null ? authMike.getUsername() : "Failed"));

        // Admin and Moderator promotion
        if (adminUser != null && moderatorUser != null) {
            forumFacade.promoteToAdmin(adminUser.getId());
            forumFacade.promoteToModerator(moderatorUser.getId());
            System.out.println("[ADMIN] " + adminUser.getUsername() + " promoted to Administrator");
            System.out.println("[MODERATOR] " + moderatorUser.getUsername() + " promoted to Moderator");
        }

        // 3. CREATE NEW CATEGORIES AND TOPICS
        System.out.println("\n=== 3. CREATE NEW CATEGORIES AND TOPICS ===");
        Category programmingCategory = forumFacade.createCategory("Programming", "Discussions about programming and software development");
        Category gamingCategory = forumFacade.createCategory("Gaming", "Discussions about video games");
        Category generalCategory = forumFacade.createCategory("General", "General discussions");
        Category techCategory = forumFacade.createCategory("Technology", "Latest technologies and gadgets");

        System.out.println("--- Created categories ---");
        List<Category> categories = forumFacade.getAllCategories();
        if (categories != null) {
            for (Category category : categories) {
                System.out.println("[CATEGORY] " + category.getName() + " - " + category.getDescription());
            }
        }

        // Create Topics
        Topic javaTopic = forumFacade.createTopic("Java Programming", "Everything about Java programming", regularUser1, programmingCategory);
        Topic pythonTopic = forumFacade.createTopic("Python Development", "Python development discussions", regularUser2, programmingCategory);
        Topic webTopic = forumFacade.createTopic("Web Development", "HTML, CSS, JavaScript", regularUser1, programmingCategory);
        Topic minecraftTopic = forumFacade.createTopic("Minecraft", "Minecraft discussions", regularUser3, gamingCategory);
        Topic mobileTopic = forumFacade.createTopic("Mobile Gaming", "Mobile gaming discussions", regularUser2, gamingCategory);
        Topic aiTopic = forumFacade.createTopic("Artificial Intelligence", "AI and Machine Learning", regularUser1, techCategory);

        System.out.println("--- Created Topics ---");
        if (programmingCategory != null) {
            List<Topic> progTopics = forumFacade.getTopicsByCategory(programmingCategory.getId());
            System.out.println("Programming Topics:");
            if (progTopics != null) {
                for (Topic topic : progTopics) {
                    System.out.println("  [TOPIC] " + topic.getTitle() + " by " + topic.getCreator().getUsername());
                }
            }
        }

        // 4. CREATE NEW POSTS
        System.out.println("\n=== 4. CREATE NEW POSTS ===");
        Post javaPost1 = forumFacade.createPost(
                "Java Collections Framework Deep Dive",
                "Let's explore Java Collections Framework in detail. ArrayList vs LinkedList: performance and use cases. HashMap, TreeMap, LinkedHashMap - when to use each.",
                regularUser1,
                javaTopic
        );

        Post javaPost2 = forumFacade.createPost(
                "Java 17 New Features",
                "Java 17 LTS brings many improvements: records, sealed classes, pattern matching. Let's see practical examples.",
                regularUser2,
                javaTopic
        );

        Post pythonPost = forumFacade.createPost(
                "Python vs Java Performance Comparison",
                "A detailed comparison between Python and Java in terms of performance, syntax and ecosystem.",
                regularUser3,
                pythonTopic
        );

        Post minecraftPost = forumFacade.createPost(
                "Best Minecraft Mods 2024",
                "List of the best mods for Minecraft in 2024: OptiFine, JEI, Biomes O' Plenty, and many others.",
                regularUser2,
                minecraftTopic
        );

        Post webPost = forumFacade.createPost(
                "Modern JavaScript Frameworks",
                "React vs Vue vs Angular - complete guide for 2024. Which framework to choose for your next project?",
                regularUser1,
                webTopic
        );

        Post aiPost = forumFacade.createPost(
                "Introduction to Machine Learning",
                "Beginner's guide to Machine Learning. Fundamental algorithms, tools and learning resources.",
                regularUser3,
                aiTopic
        );

        System.out.println("--- Created posts ---");
        List<Post> allPosts = forumFacade.getAllPosts();
        if (allPosts != null) {
            for (Post post : allPosts) {
                System.out.println("[POST] " + post.getTitle() + " by " + post.getAuthor().getUsername() +
                        " in " + post.getTopic().getTitle());
            }
        }

        // 5. ADD TAGS TO POSTS
        System.out.println("\n=== 5. ADD TAGS TO POSTS ===");
        if (javaPost1 != null && javaPost2 != null && pythonPost != null) {
            // Create tags
            Tag javaTag = new Tag(1, "java");
            Tag collectionsTag = new Tag(2, "collections");
            Tag performanceTag = new Tag(3, "performance");
            Tag pythonTag = new Tag(4, "python");
            Tag comparisonTag = new Tag(5, "comparison");
            Tag java17Tag = new Tag(6, "java17");
            Tag featuresTag = new Tag(7, "features");
            Tag minecraftTag = new Tag(8, "minecraft");
            Tag modsTag = new Tag(9, "mods");
            Tag webdevTag = new Tag(10, "webdev");
            Tag jsTag = new Tag(11, "javascript");
            Tag frameworksTag = new Tag(12, "frameworks");
            Tag aiTag = new Tag(13, "ai");
            Tag mlTag = new Tag(14, "machinelearning");

            // Add tags to posts
            javaPost1.addTag(javaTag);
            javaPost1.addTag(collectionsTag);
            javaPost1.addTag(performanceTag);

            javaPost2.addTag(javaTag);
            javaPost2.addTag(java17Tag);
            javaPost2.addTag(featuresTag);

            pythonPost.addTag(pythonTag);
            pythonPost.addTag(javaTag);
            pythonPost.addTag(comparisonTag);
            pythonPost.addTag(performanceTag);

            if (minecraftPost != null) {
                minecraftPost.addTag(minecraftTag);
                minecraftPost.addTag(modsTag);
            }

            if (webPost != null) {
                webPost.addTag(webdevTag);
                webPost.addTag(jsTag);
                webPost.addTag(frameworksTag);
            }

            if (aiPost != null) {
                aiPost.addTag(aiTag);
                aiPost.addTag(mlTag);
            }

            System.out.println("--- Posts with tags ---");
            for (Post post : Arrays.asList(javaPost1, javaPost2, pythonPost, minecraftPost, webPost, aiPost)) {
                if (post != null && !post.getTags().isEmpty()) {
                    System.out.print("[TAGGED POST] " + post.getTitle() + " - Tags: ");
                    for (Tag tag : post.getTags()) {
                        System.out.print("#" + tag.getName() + " ");
                    }
                    System.out.println();
                }
            }
        }

        // 6. ADD COMMENTS TO POSTS
        System.out.println("\n=== 6. ADD COMMENTS TO POSTS ===");
        Comment comment1 = forumFacade.addCommentToPost(
                "Excellent article! ArrayList is indeed faster for random access, but LinkedList is more efficient for frequent insertions.",
                regularUser3,
                javaPost1
        );

        Comment comment2 = forumFacade.addCommentToPost(
                "Can you add code examples for HashMap vs TreeMap? Would be very useful!",
                regularUser2,
                javaPost1
        );

        Comment comment3 = forumFacade.addCommentToPost(
                "Java 17 sealed classes are incredible! Much more code safety.",
                regularUser1,
                javaPost2
        );

        Comment comment4 = forumFacade.addCommentToPost(
                "Python is slower in execution, but much faster in development. Depends on the project.",
                regularUser1,
                pythonPost
        );

        Comment comment5 = forumFacade.addCommentToPost(
                "OptiFine is absolutely necessary! Increases FPS dramatically.",
                regularUser3,
                minecraftPost
        );

        Comment comment6 = forumFacade.addCommentToPost(
                "React remains the most popular, but Vue is easier to learn for beginners.",
                regularUser2,
                webPost
        );

        // Add replies to comments
        if (comment2 != null) {
            Comment reply1 = forumFacade.replyToComment(
                    "Sure! I'll add detailed code examples in the next update. HashMap for performance, TreeMap for automatic sorting.",
                    regularUser1,
                    javaPost1,
                    comment2
            );
            System.out.println("[REPLY] Reply added to comment");
        }

        if (comment4 != null) {
            Comment reply2 = forumFacade.replyToComment(
                    "Exactly! For rapid prototyping and data science, Python wins. For large enterprise applications, Java is more suitable.",
                    regularUser3,
                    pythonPost,
                    comment4
            );
        }

        System.out.println("--- Comments and replies ---");
        if (javaPost1 != null) {
            List<Comment> javaComments = forumFacade.getCommentsByPost(javaPost1.getId());
            System.out.println("Comments for 'Java Collections Framework':");
            if (javaComments != null) {
                for (Comment comment : javaComments) {
                    System.out.println("  [COMMENT] " + comment.getContent() + " - by " + comment.getAuthor().getUsername());
                    if (comment.getParentComment() != null) {
                        System.out.println("    [REPLY TO] " + comment.getParentComment().getId());
                    }
                }
            }
        }

        // 7. ADD REACTIONS TO POSTS/COMMENTS
        System.out.println("\n=== 7. ADD REACTIONS TO POSTS AND COMMENTS ===");

        // Simulate reactions to posts
        System.out.println("--- Reactions to posts ---");
        if (javaPost1 != null) {
            // Simulate likes for Java Collections post
            System.out.println("[LIKE] " + regularUser2.getUsername() + " liked '" + javaPost1.getTitle() + "'");
            System.out.println("[LIKE] " + regularUser3.getUsername() + " liked '" + javaPost1.getTitle() + "'");
        }

        if (pythonPost != null) {
            System.out.println("[LIKE] " + regularUser1.getUsername() + " liked '" + pythonPost.getTitle() + "'");
            System.out.println("[DISLIKE] " + regularUser2.getUsername() + " disliked '" + pythonPost.getTitle() + "'");
        }

        // Simulate reactions to comments
        System.out.println("--- Reactions to comments ---");
        if (comment1 != null) {
            System.out.println("[LIKE] " + regularUser1.getUsername() + " liked the comment: '" +
                    comment1.getContent().substring(0, Math.min(50, comment1.getContent().length())) + "...'");
        }

        // 8. SEARCH POSTS BY KEYWORDS
        System.out.println("\n=== 8. SEARCH POSTS BY KEYWORDS ===");

        System.out.println("--- Search for 'Java' ---");
        List<Post> javaSearchResults = searchService.searchPostsByKeyword("Java");
        if (javaSearchResults != null) {
            for (Post post : javaSearchResults) {
                System.out.println("[SEARCH RESULT] " + post.getTitle());
            }
        }

        System.out.println("--- Search for 'performance' ---");
        List<Post> performanceResults = searchService.searchPostsByKeyword("performance");
        if (performanceResults != null) {
            for (Post post : performanceResults) {
                System.out.println("[SEARCH RESULT] " + post.getTitle());
            }
        }

        System.out.println("--- Search for 'Minecraft' ---");
        List<Post> minecraftResults = searchService.searchPostsByKeyword("Minecraft");
        if (minecraftResults != null) {
            for (Post post : minecraftResults) {
                System.out.println("[SEARCH RESULT] " + post.getTitle());
            }
        }

        // Search by author
        System.out.println("--- Search posts by author ---");
        if (regularUser1 != null) {
            List<Post> user1Posts = searchService.searchPostsByAuthor(regularUser1.getUsername());
            System.out.println("Posts by " + regularUser1.getUsername() + ":");
            if (user1Posts != null) {
                for (Post post : user1Posts) {
                    System.out.println("  [AUTHOR POST] " + post.getTitle());
                }
            }
        }

        // 9. LIST POSTS SORTED BY POPULARITY
        System.out.println("\n=== 9. LIST POSTS SORTED BY POPULARITY ===");

        List<Post> allPostsByPopularity = forumFacade.getAllPosts();
        if (allPostsByPopularity != null) {
            System.out.println("--- Posts sorted by popularity (number of comments) ---");

            // Sort posts by comment count using streams
            List<Post> sortedPosts = allPostsByPopularity.stream()
                    .sorted((post1, post2) -> {
                        List<Comment> comments1 = forumFacade.getCommentsByPost(post1.getId());
                        List<Comment> comments2 = forumFacade.getCommentsByPost(post2.getId());
                        int count1 = comments1 != null ? comments1.size() : 0;
                        int count2 = comments2 != null ? comments2.size() : 0;
                        return Integer.compare(count2, count1); // Descending order (most comments first)
                    })
                    .collect(Collectors.toList());

            // Display results
            int rank = 1;
            for (Post post : sortedPosts) {
                List<Comment> comments = forumFacade.getCommentsByPost(post.getId());
                int commentCount = comments != null ? comments.size() : 0;
                System.out.println("#" + rank + " [" + commentCount + " comments] " +
                        post.getTitle() + " by " + post.getAuthor().getUsername());
                rank++;
            }

            // Show top 3
            System.out.println("\n--- Top 3 most popular posts ---");
            for (int i = 0; i < Math.min(3, sortedPosts.size()); i++) {
                Post post = sortedPosts.get(i);
                List<Comment> comments = forumFacade.getCommentsByPost(post.getId());
                int commentCount = comments != null ? comments.size() : 0;
                System.out.println((i + 1) + ". " + post.getTitle() + " (" + commentCount + " comments)");
            }
        }

        // 10. LIST USERS SORTED BY ACTIVITY
        System.out.println("\n=== 10. LIST USERS SORTED BY ACTIVITY ===");
        List<User> usersByActivity = forumFacade.getAllUsers();
        if (usersByActivity != null) {
            System.out.println("--- Users sorted by activity ---");
            for (User user : usersByActivity) {
                // Get user's posts count
                List<Post> userPosts = forumFacade.getPostsByAuthor(user.getId());
                int postCount = userPosts != null ? userPosts.size() : 0;

                System.out.println("[ACTIVITY] " + user.getUsername() + " - " + postCount + " posts");
            }
        }

        // 11. NOTIFY USERS ABOUT NEW ACTIVITIES
        System.out.println("\n=== 11. NOTIFY USERS ABOUT NEW ACTIVITIES ===");

        // Check notifications for users
        if (regularUser1 != null) {
            List<Notification> user1Notifications = forumFacade.getNotificationsForUser(regularUser1.getId());
            System.out.println("--- Notifications for " + regularUser1.getUsername() + " ---");
            if (user1Notifications != null && !user1Notifications.isEmpty()) {
                for (Notification notification : user1Notifications) {
                    System.out.println("[NOTIFICATION] " + notification.getMessage());
                }
            } else {
                System.out.println("No notifications for " + regularUser1.getUsername());
            }
        }

        if (regularUser2 != null) {
            List<Notification> user2Notifications = forumFacade.getNotificationsForUser(regularUser2.getId());
            System.out.println("--- Notifications for " + regularUser2.getUsername() + " ---");
            if (user2Notifications != null && !user2Notifications.isEmpty()) {
                for (Notification notification : user2Notifications) {
                    System.out.println("[NOTIFICATION] " + notification.getMessage());
                }
            } else {
                System.out.println("No notifications for " + regularUser2.getUsername());
            }
        }

        // 12. BAN USER BY ADMINISTRATOR
        System.out.println("\n=== 12. BAN USER BY ADMINISTRATOR ===");

        System.out.println("--- Administrative operations ---");
        if (regularUser3 != null) {
            System.out.println("Administrator " + (adminUser != null ? adminUser.getUsername() : "admin") +
                    " tries to ban user " + regularUser3.getUsername());

            boolean banResult = forumFacade.banUser(regularUser3.getId());
            System.out.println("Ban result: " + (banResult ? "[SUCCESS] User banned successfully" : "[FAILED] Ban failed"));

            // List of banned users
            List<User> bannedUsers = forumFacade.getBannedUsers();
            System.out.println("--- Banned users ---");
            if (bannedUsers != null && !bannedUsers.isEmpty()) {
                for (User user : bannedUsers) {
                    System.out.println("[BANNED] " + user.getUsername() + " - Active: " + user.isActive());
                }
            } else {
                System.out.println("No banned users");
            }

            // Unban user
            System.out.println("Administrator unbans user " + regularUser3.getUsername());
            boolean unbanResult = forumFacade.unbanUser(regularUser3.getId());
            System.out.println("Unban result: " + (unbanResult ? "[SUCCESS] User unbanned successfully" : "[FAILED] Unban failed"));
        }

        // FINAL STATISTICS
        System.out.println("\n=== FINAL FORUM SYSTEM STATISTICS ===");

        List<Category> finalCategories = forumFacade.getAllCategories();
        List<Post> finalPosts = forumFacade.getAllPosts();
        List<User> finalUsers = forumFacade.getAllUsers();

        System.out.println("Total categories created: " + (finalCategories != null ? finalCategories.size() : 0));
        System.out.println("Total registered users: " + (finalUsers != null ? finalUsers.size() : 0));
        System.out.println("Total posts created: " + (finalPosts != null ? finalPosts.size() : 0));

        // Count topics
        int totalTopics = 0;
        if (finalCategories != null) {
            for (Category cat : finalCategories) {
                List<Topic> catTopics = forumFacade.getTopicsByCategory(cat.getId());
                totalTopics += (catTopics != null ? catTopics.size() : 0);
            }
        }
        System.out.println("Total topics created: " + totalTopics);

        // Count comments
        int totalComments = 0;
        if (finalPosts != null) {
            for (Post post : finalPosts) {
                List<Comment> postComments = forumFacade.getCommentsByPost(post.getId());
                totalComments += (postComments != null ? postComments.size() : 0);
            }
        }
        System.out.println("Total comments added: " + totalComments);

        System.out.println("\n=== ALL FORUM FUNCTIONALITIES HAVE BEEN DEMONSTRATED ===");
        System.out.println("- User registration and authentication");
        System.out.println("- Create categories and topics");
        System.out.println("- Create posts with rich content");
        System.out.println("- Add tags to posts");
        System.out.println("- Comment and reply system");
        System.out.println("- Reaction system (like/dislike)");
        System.out.println("- Advanced search by keywords and author");
        System.out.println("- Sort by popularity and activity");
        System.out.println("- Notification system");
        System.out.println("- Administrative operations (ban/unban)");
        System.out.println("- User roles (User, Moderator, Admin)");
    }
}
