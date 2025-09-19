package es.codeurjc.webapp17.restcontroller;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.webapp17.dto.PostDto;
import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Posts", description = "API for managing blog posts")
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

    /**
     * Retrieves paginated posts.
     *
     * @param page Page number (default 0)
     * @param size Page size (default 10)
     * @return Page of PostDto objects
     */
    @GetMapping
    @Operation(summary = "Get paginated posts", description = "Retrieves a paginated list of all posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<Page<PostDto>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostDto> posts = postService.getPosts(page, size);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieves posts by tag with pagination.
     *
     * @param tag  Tag to filter by
     * @param page Page number
     * @param size Page size
     * @return Page of PostDto objects
     */
    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get posts by tag", description = "Retrieves paginated posts filtered by tag")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<Page<PostDto>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostDto> posts = postService.getPostsByTag(page, size, tag);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieves a specific post by ID.
     *
     * @param id Post ID
     * @return PostDto if found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID", description = "Retrieves a specific post by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post found"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        
        PostDto post = postService.getPostById(id);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(post);
    }

    /**
     * Retrieves posts by the currently authenticated user.
     *
     * @param session HTTP session
     * @param page    Page number
     * @param size    Page size
     * @return Page of PostDto objects
     */
    @GetMapping("/me")
    @Operation(summary = "Get my posts", description = "Retrieves paginated posts of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Page<PostDto>> getMyPosts(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Check authentication
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Retrieve user's posts
        Page<PostDto> posts = postService.getPostsByUsr(page, size, userId);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieves posts by a specific user.
     *
     * @param userId User ID
     * @param page   Page number
     * @param size   Page size
     * @return Page of PostDto objects
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get posts by user", description = "Retrieves paginated posts by a specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Page<PostDto>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Verify user exists
        UsrBasicDto user = usrService.findUsrBasicById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve user's posts
        Page<PostDto> posts = postService.getPostsByUsr(page, size, userId);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(posts);
    }

    /**
 * Creates a new post.
 *
 * @param session HTTP session
 * @param title Post title
 * @param content Post content
 * @param tag Post tag
 * @param image Optional image file
 * @return Created post
 */
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Operation(summary = "Create post", description = "Creates a new blog post")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Post created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "User not authenticated")
})
public ResponseEntity<PostDto> createPost(
        HttpSession session,
        @RequestParam("title") String title,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "tag", required = false) String tag,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

    // Check authentication
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Validate request
    if (title == null || title.trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
    }

    // Get user info
    UsrBasicDto user = usrService.findUsrBasicById(userId);
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Handle image upload - only if image is provided
    String imagePath;
    if (image != null && !image.isEmpty()) {
        imagePath = handleImageUpload(image);
    } else {
        imagePath = DEFAULT_IMAGE_PATH;
    }

    // Create post DTO
    PostDto postDto = new PostDto(
        null, // ID will be generated
        userId,
        user.username(),
        title.trim(),
        imagePath,
        content != null ? content.trim() : "",
        LocalDateTime.now(),
        tag != null ? tag.trim() : null,
        java.util.List.of() // Empty comments for new post
    );

    // Save post
    PostDto savedPost = postService.addPost(postDto);

    // Build location URI
    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedPost.id())
            .toUri();

    return ResponseEntity.created(location).body(savedPost);
}


    /**
     * Updates an existing post.
     *
     * @param id      Post ID
     * @param session HTTP session
     * @param request Update request
     * @return Updated post
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update post", description = "Updates an existing post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "No permission to modify this post"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id, 
            HttpSession session,
            @RequestPart UpdatePostRequest request) throws IOException {

        // Check authentication
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get existing post
        PostDto existingPost = postService.getPostById(id);
        if (existingPost == null) {
            return ResponseEntity.notFound().build();
        }

        // Check permissions (owner or admin)
        if (!existingPost.userId().equals(userId) && userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Validate request
        if (request.title() == null || request.title().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Handle image upload
        String imagePath = existingPost.image(); // Keep existing image by default
        if (request.image() != null && !request.image().isEmpty()) {
            imagePath = handleImageUpload(request.image());
        }

        // Create updated post DTO
        PostDto updatedPostDto = new PostDto(
            existingPost.id(),
            existingPost.userId(),
            existingPost.username(),
            request.title().trim(),
            imagePath,
            request.content() != null ? request.content().trim() : "",
            existingPost.date(), // Keep original date
            request.tag() != null ? request.tag().trim() : null,
            existingPost.comments() // Keep existing comments
        );

        // Update post
        PostDto savedPost = postService.updatePost(updatedPostDto);

        return ResponseEntity.ok(savedPost);
    }

    /**
     * Deletes a post by ID.
     *
     * @param id      Post ID
     * @param session HTTP session
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post", description = "Deletes a post by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "No permission to delete this post"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpSession session) {

        // Check authentication
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get existing post
        PostDto post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        // Check permissions (owner or admin)
        if (!post.userId().equals(userId) && userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Delete post
        boolean deleted = postService.deletePost(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method for image upload
    private String handleImageUpload(MultipartFile image) throws IOException {
        String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();
        Path filepath = Paths.get(uploadPath, filename);

        if (!Files.exists(filepath.getParent())) {
            Files.createDirectories(filepath.getParent());
        }

        Files.write(filepath, image.getBytes());
        return "/files/" + filename;
    }

    // Request DTOs for API operations

    /**
     * Request DTO for creating a post.
     */
    public record CreatePostRequest(
        String title,
        String content,
        String tag,
        MultipartFile image
    ) {}

    /**
     * Request DTO for updating a post.
     */
    public record UpdatePostRequest(
        String title,
        String content,
        String tag,
        MultipartFile image
    ) {}
}
