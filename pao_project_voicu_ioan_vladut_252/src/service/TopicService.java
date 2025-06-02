package service;

import model.Topic;
import model.User;
import model.Category;
import repository.TopicRepository;
import java.sql.SQLException;
import java.util.List;

public class TopicService {
    private TopicRepository topicRepository = new TopicRepository();
    
    public Topic createTopic(String title, String description, User creator, Category category) {
        try {
            Topic topic = new Topic(0, title, description, creator, category);
            return topicRepository.save(topic);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Topic getTopicById(int id) {
        try {
            return topicRepository.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Topic> getTopicsByCategory(int categoryId) {
        try {
            return topicRepository.findByCategoryId(categoryId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Topic> getAllTopics() {
        try {
            return topicRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateTopic(Topic topic) {
        try {
            topicRepository.update(topic);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteTopic(int id) {
        try {
            topicRepository.delete(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


