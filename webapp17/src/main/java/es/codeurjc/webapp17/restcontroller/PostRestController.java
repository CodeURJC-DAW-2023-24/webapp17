package es.codeurjc.webapp17.restcontroller;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.CommentService;
import es.codeurjc.webapp17.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Post Controller", description = "REST API for managing posts and comments")
public class PostRestController {

    private static final String DEFAULT_IMAGE_PATH = "/images/entryphoto.png";

    private final PostService postService;
    private final CommentService commentService;

    @Value("${upload.path}")
    private String uploadPath;

    public PostRestController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * Creates a new post.
     *
     * @param session HTTP session with logged-in user.
     * @param DTO     DTO containing post data and optional image.
     * @return The created post.
     * @throws IOException If file upload fails.
     */
    @PostMapping
    @Operation(summary = "Create a new post")
    public ResponseEntity<?> createPost(HttpSession session, @ModelAttribute PostDTO DTO) throws IOException {
        LocalDateTime now = LocalDateTime.now();

        Post post = new Post();
        post.setTitle(DTO.getTitle());
        post.setContent(DTO.getContent());
        post.setDate(now);
        post.setTag(DTO.getTag());

        // Get the logged-in user from the session
        Usr user = (Usr) session.getAttribute("user");
        if (user == null) {
            // If not logged in, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }
        post.setUsr(user);

        post.setImage(DEFAULT_IMAGE_PATH);

        // Save the post (the ID will be populated after saving)
        postService.addPost(post);

        // Build the URI for the created post (e.g., /posts/{id})
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        // Return 201 Created with Location header and the created post as body
        return ResponseEntity.created(location).body(post);
    }

    /**
     * Adds a comment to a post.
     *
     * @param session HTTP session with logged-in user.
     * @param postId  Post ID.
     * @param DTO     Comment DTO.
     * @return 204 No Content on success.
     */
    @PostMapping("/{postId}/comments")
    @Operation(summary = "Add a comment to a post")
    public ResponseEntity<Void> addComment(HttpSession session, @PathVariable Long postId,
            @RequestBody CommentDTO DTO) {

        // Find the post by its ID
        Post post = postService.getPostById(postId);
        if (post == null)
            return ResponseEntity.notFound().build(); // 404 if post doesn't exist

        // Check if user is logged in
        Usr user = (Usr) session.getAttribute("user");
        if (user == null)
            return ResponseEntity.status(401).build(); // 401 Unauthorized

        // Create and populate the comment
        Comment comment = new Comment();
        comment.setText(DTO.getText());
        comment.setPost(post);
        comment.setDate(LocalDateTime.now());
        comment.setUsr(user);

        // Save the comment (assumes the comment ID is set after saving)
        commentService.addComment(comment);

        // Build Location URI: e.g., /posts/{postId}/comments/{commentId}
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{commentId}")
                .buildAndExpand(comment.getId())
                .toUri();

        // Return 201 Created with Location header (no body)
        return ResponseEntity.created(location).build();
    }

    /**
     * Deletes a post by ID.
     *
     * @param id Post ID.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post by ID")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a comment by ID.
     *
     * @param id Comment ID.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/comments/{id}")
    @Operation(summary = "Delete a comment by ID")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("No right permissions");
        }
        commentService.deleteComment(id);
        return ResponseEntity.status(201).body("Deletion completed");
    }

    /**
     * Updates a post with new data.
     *
     * @param id  Post ID.
     * @param DTO DTO containing new post data and optional image.
     * @return The updated post.
     * @throws IOException If file upload fails.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a post by ID")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @ModelAttribute PostDTO DTO, HttpSession session)
            throws IOException {

        // Get the logged-in user
        Usr user = (Usr) session.getAttribute("user");

        // Only ADMIN users are allowed to update posts
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("No right permissions");
        }

        // Fetch the post by ID
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        } else {
            // Update the post fields
            post.setTitle(DTO.getTitle());
            post.setContent(DTO.getContent());
            post.setTag(DTO.getTag());

            // Save the updated post
            postService.updatePost(post);

            // Build the URI to the updated resource (e.g., /posts/{id})
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .build()
                    .toUri();

            // Return 200 OK with the Location header
            return ResponseEntity.ok()
                    .location(location)
                    .body(post);
        }
    }

    /**
     * DTO for creating a post.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostDTO {

        private String title;

        private String content;

        private String tag;
    }

    /**
     * DTO for submitting a comment.
     */
    @Data
    public static class CommentDTO {

        private String text;
    }

}
