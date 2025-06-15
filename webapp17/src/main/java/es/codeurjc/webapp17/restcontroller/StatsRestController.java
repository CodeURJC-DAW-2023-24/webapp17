package es.codeurjc.webapp17.restcontroller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Statistics", description = "Operations related to users, posts, and tags statistics.")
public class StatsRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private UsrService userService;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class UserPostCountDTO {
        private String user;
        private int postCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class PostCommentCountDTO {
        private String title;
        private int commentCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TagCountDTO {
        private String tag;
        private long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ErrorResponseDTO {
        private String error;
        private String redirect;
    }

    @Operation(summary = "Users with most posts")
    @GetMapping("users")
    @ApiResponse(responseCode = "200", description = "Users with most posts")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    public ResponseEntity<?> usersWithMostPosts(HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body(new ErrorResponseDTO("No right permissions", "/no_admin"));
        }

        List<UserPostCountDTO> result = userService.getAllUsrs().stream()
                .map(u -> new UserPostCountDTO(u.getUsername(), u.getPosts() != null ? u.getPosts().size() : 0))
                .sorted(Comparator.comparingInt(UserPostCountDTO::getPostCount).reversed())
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Posts with most comments")
    @GetMapping("posts")
    @ApiResponse(responseCode = "200", description = "Posts with most comments")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    public ResponseEntity<?> postsWithMostComments(HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body(new ErrorResponseDTO("No right permissions", "/no_admin"));
        }

        List<PostCommentCountDTO> result = postService.getAllPosts().stream()
                .map(p -> new PostCommentCountDTO(p.getTitle(), p.getComments() != null ? p.getComments().size() : 0))
                .sorted(Comparator.comparingInt(PostCommentCountDTO::getCommentCount).reversed())
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Tags with most posts")
    @GetMapping("tags")
    @ApiResponse(responseCode = "200", description = "Tags with most posts")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    public ResponseEntity<?> tagsWithMostPosts(HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body(new ErrorResponseDTO("No right permissions", "/no_admin"));
        }

        Map<String, Long> tagCounts = postService.getAllPosts().stream()
                .filter(p -> p.getTag() != null)
                .collect(Collectors.groupingBy(Post::getTag, Collectors.counting()));

        List<TagCountDTO> result = tagCounts.entrySet().stream()
                .map(e -> new TagCountDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(TagCountDTO::getCount).reversed())
                .toList();

        return ResponseEntity.ok(result);
    }
}
