package service;

import model.Category;
import repository.CategoryRepository;
import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private CategoryRepository categoryRepository = new CategoryRepository();
    
    public Category createCategory(String name, String description) {
        try {
            Category category = new Category(0, name, description);
            return categoryRepository.save(category);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Category getCategoryById(int id) {
        try {
            return categoryRepository.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateCategory(Category category) {
        try {
            categoryRepository.update(category);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCategory(int id) {
        try {
            categoryRepository.delete(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


