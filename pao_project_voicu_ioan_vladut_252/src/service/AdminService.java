package service;

import model.User;
import repository.UserRepository;
import java.sql.SQLException;
import java.util.List;

public class AdminService {
    private UserRepository userRepository = new UserRepository();
    private AuditService auditService = new AuditService();

    public boolean banUser(int userId) {
        try {
            User user = userRepository.findById(userId);
            if (user != null) {
                user.setActive(false);
                userRepository.update(user);
                auditService.logUserAction(user.getUsername(), "BANNED");
                return true;
            }
        } catch (SQLException e) {
            auditService.log("BAN_USER_FAILED_" + userId);
            e.printStackTrace();
        }
        return false;
    }

    public boolean unbanUser(int userId) {
        try {
            User user = userRepository.findById(userId);
            if (user != null) {
                user.setActive(true);
                userRepository.update(user);
                auditService.logUserAction(user.getUsername(), "UNBANNED");
                return true;
            }
        } catch (SQLException e) {
            auditService.log("UNBAN_USER_FAILED_" + userId);
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getBannedUsers() {
        try {
            auditService.logSystemAction("GET_BANNED_USERS");
            return userRepository.findBannedUsers();
        } catch (SQLException e) {
            auditService.log("GET_BANNED_USERS_FAILED");
            e.printStackTrace();
            return null;
        }
    }

    public boolean promoteToModerator(int userId) {
        try {
            User user = userRepository.findById(userId);
            if (user != null) {
                userRepository.updateUserType(userId, "MODERATOR");
                auditService.logUserAction(user.getUsername(), "PROMOTED_TO_MODERATOR");
                return true;
            }
        } catch (SQLException e) {
            auditService.log("PROMOTE_TO_MODERATOR_FAILED_" + userId);
            e.printStackTrace();
        }
        return false;
    }

    public boolean promoteToAdmin(int userId) {
        try {
            User user = userRepository.findById(userId);
            if (user != null) {
                userRepository.updateUserType(userId, "ADMIN");
                auditService.logUserAction(user.getUsername(), "PROMOTED_TO_ADMIN");
                return true;
            }
        } catch (SQLException e) {
            auditService.log("PROMOTE_TO_ADMIN_FAILED_" + userId);
            e.printStackTrace();
        }
        return false;
    }
}
