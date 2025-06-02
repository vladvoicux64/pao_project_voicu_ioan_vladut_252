package service;

import model.User;
import repository.UserRepository;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserRepository userRepository = new UserRepository();
    private AuditService auditService = new AuditService();

    public User createUser(String username, String email, String password) {
        try {
            User user = new User(0, username, email, password);
            User savedUser = userRepository.save(user);
            if (savedUser != null) {
                auditService.logUserAction(username, "REGISTERED");
            }
            return savedUser;
        } catch (SQLException e) {
            auditService.log("USER_REGISTRATION_FAILED_" + username);
            e.printStackTrace();
            return null;
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            User user = userRepository.findByUsername(username);
            if (user != null && user.getPassword().equals(password) && user.isActive()) {
                auditService.logUserAction(username, "LOGIN_SUCCESS");
                return true;
            } else {
                auditService.logUserAction(username, "LOGIN_FAILED");
                return false;
            }
        } catch (SQLException e) {
            auditService.log("LOGIN_ERROR_" + username);
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        try {
            auditService.logSystemAction("GET_ALL_USERS");
            return userRepository.findAll();
        } catch (SQLException e) {
            auditService.log("GET_ALL_USERS_FAILED");
            e.printStackTrace();
            return null;
        }
    }

    public User getUserById(int id) {
        try {
            auditService.logSystemAction("GET_USER_BY_ID_" + id);
            return userRepository.findById(id);
        } catch (SQLException e) {
            auditService.log("GET_USER_BY_ID_FAILED_" + id);
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByUsername(String username) {
        try {
            auditService.logSystemAction("GET_USER_BY_USERNAME_" + username);
            return userRepository.findByUsername(username);
        } catch (SQLException e) {
            auditService.log("GET_USER_BY_USERNAME_FAILED_" + username);
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateUser(User user) {
        try {
            userRepository.update(user);
            auditService.logUserAction(user.getUsername(), "UPDATED");
            return true;
        } catch (SQLException e) {
            auditService.log("USER_UPDATE_FAILED_" + user.getUsername());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        try {
            User user = userRepository.findById(id);
            if (user != null) {
                userRepository.delete(id);
                auditService.logUserAction(user.getUsername(), "DELETED");
                return true;
            }
            return false;
        } catch (SQLException e) {
            auditService.log("USER_DELETE_FAILED_" + id);
            e.printStackTrace();
            return false;
        }
    }
}
