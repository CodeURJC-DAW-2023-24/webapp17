package es.codeurjc.webapp17.restcontroller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.webapp17.dto.IssueDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

/**
 * REST controller to manage issue-related operations.
 */
@RestController
@RequestMapping("/api/v1/issues")
@Tag(name = "Issues", description = "API for managing user issues and support requests")
public class IssueRestController {

    @Autowired
    private IssueService issueService;

    /**
     * Retrieves all issues (Admin only).
     *
     * @param session HTTP session with authenticated user
     * @return List of IssueDto objects
     */
    @GetMapping
    @Operation(summary = "Get all issues", description = "Retrieves all issues (Admin access required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Issues retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<IssueDto>> getAllIssues(HttpSession session) {
        
        // Check authentication
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<IssueDto> issues = issueService.getAllIssues();
        return ResponseEntity.ok(issues);
    }

    /**
     * Retrieves a specific issue by ID (Admin only).
     *
     * @param id      Issue ID
     * @param session HTTP session
     * @return IssueDto if found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get issue by ID", description = "Retrieves a specific issue by its ID (Admin access required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Issue found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    public ResponseEntity<IssueDto> getIssue(@PathVariable Long id, HttpSession session) {
        
        // Check authentication and authorization
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        IssueDto issue = issueService.getIssueById(id);
        
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(issue);
    }

    /**
     * Creates a new issue.
     *
     * @param request Issue creation request
     * @return Created issue
     */
    @PostMapping
    @Operation(summary = "Create new issue", description = "Creates a new support issue or request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Issue created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<IssueDto> createIssue(@RequestBody CreateIssueRequest request) {
        
        // Validate request data
        if (request.name() == null || request.name().trim().isEmpty() ||
            request.email() == null || request.email().trim().isEmpty() ||
            request.content() == null || request.content().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Create issue DTO
        IssueDto issueDto = new IssueDto(
            null, // ID will be generated
            request.name().trim(),
            request.email().trim(),
            request.content().trim(),
            LocalDateTime.now()
        );

        // Save issue
        IssueDto savedIssue = issueService.addIssue(issueDto);

        // Build location URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedIssue.id())
                .toUri();

        return ResponseEntity.created(location).body(savedIssue);
    }

    /**
     * Updates an existing issue (Admin only).
     *
     * @param id      Issue ID
     * @param request Update request
     * @param session HTTP session
     * @return Updated issue
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update issue", description = "Updates an existing issue (Admin access required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Issue updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    public ResponseEntity<IssueDto> updateIssue(
            @PathVariable Long id,
            @RequestBody UpdateIssueRequest request,
            HttpSession session) {

        // Check authentication and authorization
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Get existing issue
        IssueDto existingIssue = issueService.getIssueById(id);
        if (existingIssue == null) {
            return ResponseEntity.notFound().build();
        }

        // Validate request data
        if (request.name() == null || request.name().trim().isEmpty() ||
            request.email() == null || request.email().trim().isEmpty() ||
            request.content() == null || request.content().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Create updated issue DTO
        IssueDto updatedIssueDto = new IssueDto(
            existingIssue.id(),
            request.name().trim(),
            request.email().trim(),
            request.content().trim(),
            existingIssue.date() // Keep original date
        );

        // Update issue
        IssueDto savedIssue = issueService.addIssue(updatedIssueDto);

        return ResponseEntity.ok(savedIssue);
    }

    /**
     * Deletes an issue by ID (Admin only).
     *
     * @param id      Issue ID
     * @param session HTTP session
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete issue", description = "Deletes an issue by its ID (Admin access required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Issue deleted successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id, HttpSession session) {
        
        // Check authentication and authorization
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Check if issue exists
        IssueDto issue = issueService.getIssueById(id);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }

        // Delete issue
        boolean deleted = issueService.deleteIssue(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Request DTOs for API operations

    /**
     * Request DTO for creating an issue.
     */
    public record CreateIssueRequest(
        String name,
        String email,
        String content
    ) {}

    /**
     * Request DTO for updating an issue.
     */
    public record UpdateIssueRequest(
        String name,
        String email,
        String content
    ) {}
}
