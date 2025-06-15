package es.codeurjc.webapp17.restcontroller;

import java.net.URI;
import java.time.LocalDateTime;

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
import lombok.Data;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comment Controller", description = "REST API for managing comments")
public class CommentRestController {

    private final PostService postService;
    private final CommentService commentService;

    public CommentRestController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * Adds a comment to a post.
     *
     * @param session HTTP session with logged-in user.
     * @param postId  Post ID.
     * @param DTO     Comment DTO.
     * @return 204 No Content on success.
     */
    @PostMapping("/post/{postId}")
    @Operation(summary = "Add a comment to a post")
    public ResponseEntity<Void> addComment(HttpSession session, @PathVariable Long postId,
            @RequestBody CommentDTO commentDto) {

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
        comment.setText(commentDto.getText());
        comment.setPost(post);
        comment.setDate(LocalDateTime.now());
        comment.setUsr(user);

        // Save the comment (assumes the comment ID is set after saving)
        commentService.addComment(comment);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{commentId}")
                .buildAndExpand(comment.getId())
                .toUri();

        // Return 201 Created with Location header (no body)
        return ResponseEntity.created(location).build();
    }

    @Data
    public class CommentResponseDTO {
        private Long Id;
        private String Text;
        private Long UserId;

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a comment by ID")
    public ResponseEntity<?> getComment(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        CommentResponseDTO response = new CommentResponseDTO();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setUserId(comment.getUsr().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment by ID")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, HttpSession session) {
        // Retrieve the currently authenticated user
        Usr user = (Usr) session.getAttribute("user");

        if (user == null) {
            // User is not authenticated
            return ResponseEntity.status(401).body("Not authenticated.");
        }

        // Retrieve the comment by its ID
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if the user is neither the owner nor an Administrator
        if (!comment.getUsr().equals(user) && user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("No right permissions.");
        }

        // Delete the comment
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DTO for submitting a comment.
     */
    @Data
    public static class CommentDTO {

        private String text;
    }

    /**
     * Updates a comment by its ID.
     * 
     * @param id         Comment ID.
     * @param commentDto DTO with new comment text.
     * @return 200 OK if updated, 403 if no permission, 404 if not found.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a comment by ID")
    @ApiResponse(responseCode = "403", description = "No permission to modify this comment")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
            @RequestBody CommentDTO commentDto,
            HttpSession session) {

        // Retrieve the currently authenticated user
        Usr user = (Usr) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        // Retrieve the comment by its ID
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if the authenticated user is the owner or an admin
        if (!comment.getUsr().equals(user) && user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission to modify this comment.");
        }

        // Update the comment text
        comment.setText(commentDto.getText());

        // Save the updated comment
        commentService.addComment(comment);

        return ResponseEntity.ok().body(commentDto);
    }

}
