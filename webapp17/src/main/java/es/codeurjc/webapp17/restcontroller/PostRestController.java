package es.codeurjc.webapp17.restcontroller;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Post Controller", description = "REST API for managing posts ")
public class PostRestController {

    private static final String DEFAULT_IMAGE_PATH = "/images/entryphoto.png";

    private final PostService postService;
    private final UsrService usrService;

    @Value("${upload.path}")
    private String uploadPath;

    public PostRestController(PostService postService, UsrService usrService) {
        this.postService = postService;
        this.usrService = usrService;
    }

    @GetMapping
    @Operation(summary = "Get paginated posts")
    public ResponseEntity<Page<PostResponseDTO>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Post> posts = postService.getPosts(page, size);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Transform into DTOs
        Page<PostResponseDTO> postResponseDTOS = posts.map(this::convertToDto);

        return ResponseEntity.ok(postResponseDTOS);
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
        return ResponseEntity.created(location).body(DTO);
    }

    /**
     * Deletes a comment by ID.
     *
     * @param id Comment ID.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post by ID")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpSession session) {

        // Retrieve the post by its ID
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve the currently authenticated user
        Usr user = (Usr) session.getAttribute("user");

        if (user == null) {
            // User is not authenticated
            return ResponseEntity.status(401).build();
        }

        // Check if the user is neither the owner nor an Administrator
        if (!post.getUsr().equals(user) && user.getRole() != Usr.Role.ADMIN) {
            // User is not authorized to delete this post
            return ResponseEntity.status(403).build();
        }

        // Delete the post
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a post by ID")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @ModelAttribute PostDTO DTO, HttpSession session)
            throws IOException {

        // Retrieve the currently authenticated user
        Usr user = (Usr) session.getAttribute("user");

        if (user == null) {
            // User is not authenticated
            return ResponseEntity.status(401).body("Not authenticated.");
        }

        // Retrieve the post by its ID
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if the authenticated user is not the owner of the post
        if (!post.getUsr().equals(user)) {
            // User is not authorized to update this post
            return ResponseEntity.status(403).body("Not authorized.");
        }

        // Update the post fields
        post.setTitle(DTO.getTitle());
        post.setContent(DTO.getContent());
        post.setTag(DTO.getTag());

        // Save the updated post
        postService.updatePost(post);

        // Build the Location header for the updated post
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        // Return 200 OK with Location header
        return ResponseEntity.ok()
                .location(location)
                .body(DTO);
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostResponseDTO {

        private Long id;
        private String title;
        private String content;
        private String tag;
        private LocalDateTime date;
        private String image;
        private Long userId;
        private String username;

        private int totalComments;

    }

    private PostResponseDTO convertToDto(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getTag(),
                post.getDate(),
                post.getImage(),
                post.getUsr().getId(),
                post.getUsr().getUsername(),
                post.getComments().size());
    }

    /**
     * Get paginated posts of the currently authenticated user.
     * 
     * @param session The current HTTP session.
     * @param page    The page number (defaults to 0).
     * @param size    The size of the page (defaults to 10).
     * @return A paginated list of posts for the authenticated user.
     */
    @GetMapping("/me")
    @Operation(summary = "Get paginated posts of the currently authenticated user")
    public ResponseEntity<Page<PostResponseDTO>> getMyPosts(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Retrieve the currently authenticated user
        Usr user = (Usr) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Retrieve the user's posts
        Page<Post> posts = postService.getPostsbyUsr(page, size, user);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Transform into DTOs
        Page<PostResponseDTO> postResponseDTOS = posts.map(this::convertToDto);

        return ResponseEntity.ok(postResponseDTOS);
    }

    /**
     * Get paginated posts by a specified user.
     * 
     * @param userId The user's ID.
     * @param page   The page number (defaults to 0).
     * @param size   The size of the page (defaults to 10).
     * @return A paginated list of posts by the specified user.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get paginated posts by a specified user")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Retrieve the user by ID
        Usr user = usrService.findUsrById(userId);

        // Retrieve the user's posts
        Page<Post> posts = postService.getPostsbyUsr(page, size, user);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Transform into DTOs
        Page<PostResponseDTO> postResponseDTOS = posts.map(this::convertToDto);

        return ResponseEntity.ok(postResponseDTOS);
    }

}
