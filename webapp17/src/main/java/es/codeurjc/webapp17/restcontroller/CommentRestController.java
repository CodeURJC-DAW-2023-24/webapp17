package es.codeurjc.webapp17.restcontroller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.webapp17.dto.CommentDto;
import es.codeurjc.webapp17.dto.PostDto;
import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.CommentService;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comments", description = "API for managing comments")
public class CommentRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final UsrService usrService;

    public CommentRestController(PostService postService, CommentService commentService, UsrService usrService) {
        this.postService = postService;
        this.commentService = commentService;
        this.usrService = usrService;
    }

    /**
     * Retrieves all comments.
     *
     * @return List of CommentDto objects
     */
    @GetMapping
    @Operation(summary = "Get all comments", description = "Retrieves a list of all comments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved comments")
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    /**
     * Retrieves a comment by its ID.
     *
     * @param id Comment ID
     * @return CommentDto if found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID", description = "Retrieves a specific comment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment found"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<CommentDto> getComment(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentById(id);
        
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(comment);
    }

    /**
     * Creates a new comment on a specific post.
     *
     * @param session HTTP session with authenticated user
     * @param postId  Post ID to comment on
     * @param request Comment creation request
     * @return Created comment
     */
    @PostMapping("/posts/{postId}")
    @Operation(summary = "Create comment", description = "Creates a new comment on a specific post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment created successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<CommentDto> createComment(
            HttpSession session, 
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request) {

        // Check authentication
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Verify post exists
        PostDto post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        // Verify user exists
        UsrBasicDto user = usrService.findUsrBasicById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Create comment DTO
        CommentDto commentDto = new CommentDto(
            null, // ID will be generated
            userId,
            user.username(),
            postId,
            LocalDateTime.now(),
            request.text()
        );

        // Save comment
        CommentDto savedComment = commentService.addComment(commentDto);

        // Build location URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/../{commentId}")
                .buildAndExpand(savedComment.id())
                .toUri();

        return ResponseEntity.created(location).body(savedComment);
    }

    /**
     * Updates an existing comment.
     *
     * @param id      Comment ID
     * @param request Update request
     * @param session HTTP session
     * @return Updated comment
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update comment", description = "Updates an existing comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "No permission to modify this comment"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequest request,
            HttpSession session) {

        // Check authentication
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get existing comment
        CommentDto existingComment = commentService.getCommentById(id);
        if (existingComment == null) {
            return ResponseEntity.notFound().build();
        }

        // Check permissions (owner or admin)
        if (!existingComment.userId().equals(userId) && userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Create updated comment DTO
        CommentDto updatedCommentDto = new CommentDto(
            existingComment.id(),
            existingComment.userId(),
            existingComment.username(),
            existingComment.postId(),
            existingComment.date(),
            request.text()
        );

        // Update comment
        CommentDto savedComment = commentService.addComment(updatedCommentDto);

        return ResponseEntity.ok(savedComment);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id      Comment ID
     * @param session HTTP session
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Deletes a comment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "No permission to delete this comment"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, HttpSession session) {
        
        // Check authentication
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get existing comment
        CommentDto comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        // Check permissions (owner or admin)
        if (!comment.userId().equals(userId) && userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Delete comment
        boolean deleted = commentService.deleteComment(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Request/Response DTOs for API operations

    /**
     * Request DTO for creating a comment.
     */
    public record CreateCommentRequest(String text) {}

    /**
     * Request DTO for updating a comment.
     */
    public record UpdateCommentRequest(String text) {}
}
