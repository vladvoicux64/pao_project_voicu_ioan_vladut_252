package repository;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModeratorRepository {
    private CategoryRepository categoryRepository = new CategoryRepository();
    
    public void addModeratedCategory(int moderatorId, int categoryId) throws SQLException {
        String sql = "INSERT INTO moderator_categories (moderator_id, category_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, moderatorId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }
    
    public void removeModeratedCategory(int moderatorId, int categoryId) throws SQLException {
        String sql = "DELETE FROM moderator_categories WHERE moderator_id = ? AND category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, moderatorId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }
    
    public List<Category> findModeratedCategories(int moderatorId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.* FROM categories c JOIN moderator_categories mc ON c.id = mc.category_id WHERE mc.moderator_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, moderatorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Category category = new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                categories.add(category);
            }
        }
        return categories;
    }
}


