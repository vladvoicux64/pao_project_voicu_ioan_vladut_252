package service;

import model.Post;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SearchService {
    private PostService postService;

    public SearchService(PostService postService) {
        this.postService = postService;
    }

    public List<Post> searchPostsByKeyword(String keyword) {
        List<Post> allPosts = postService.getAllPosts();
        if (allPosts == null) {
            return new ArrayList<>();
        }

        return allPosts.stream()
                .filter(post -> post.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        post.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Post> searchPostsByTag(String tagName) {
        List<Post> allPosts = postService.getAllPosts();
        if (allPosts == null) {
            return new ArrayList<>();
        }

        return allPosts.stream()
                .filter(post -> post.getTags().stream()
                        .anyMatch(tag -> tag.getName().equalsIgnoreCase(tagName)))
                .collect(Collectors.toList());
    }

    public List<Post> searchPostsByAuthor(String authorUsername) {
        List<Post> allPosts = postService.getAllPosts();
        if (allPosts == null) {
            return new ArrayList<>();
        }

        return allPosts.stream()
                .filter(post -> post.getAuthor().getUsername().equalsIgnoreCase(authorUsername))
                .collect(Collectors.toList());
    }

    public List<Post> getPosts() {
        return postService.getAllPosts();
    }
}
