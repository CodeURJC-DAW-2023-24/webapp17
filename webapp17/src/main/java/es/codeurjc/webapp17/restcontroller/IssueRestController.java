package es.codeurjc.webapp17.restcontroller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST controller to manage issue-related operations.
 */
@RestController
@Tag(name = "Issues Controller", description = "REST API for managing issues")
@RequestMapping("/api/v1/issues")
public class IssueRestController {

    @Autowired
    private IssueService issueService;

    /**
     * Creates a new issue and stores it in the database.
     *
     * @param DTO the data transfer object containing the issue information
     * @return confirmation message
     */
    @PostMapping
    @Operation(summary = "Create a new issue")
    @ApiResponse(responseCode = "201", description = "Issue successfully created")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    public ResponseEntity<?> createIssue(@RequestBody IssueDTO DTO) {
        // Create and populate the Issue object
        Issue issue = new Issue();
        issue.setName(DTO.getName());
        issue.setEmail(DTO.getEmail());
        issue.setContent(DTO.getContent());
        issue.setDate(LocalDateTime.now());

        // Save the issue (assumes addIssue sets the ID)
        issueService.addIssue(issue);

        // Build the URI to the newly created issue
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(issue.getId())
                .toUri();

        // Return 201 Created with Location header
        return ResponseEntity.created(location).body("Issue successfully created.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete issue")
    public ResponseEntity<?> deleteIssue(@PathVariable Long id, HttpSession session) {
        // Check if user is an admin
        Usr user = (Usr) session.getAttribute("user");

        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        issueService.deleteIssue(id);
        return ResponseEntity.ok("Issue successfully deleted.");
    }

    /**
     * Retrieves all issues if the user has the ADMIN role.
     *
     * @param session the current HTTP session
     * @return a list of issues, or 403 if access is denied
     */
    @Operation(summary = "Get all issues")
    @ApiResponse(responseCode = "200", description = "Issues successfully loaded")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/issues")
    public ResponseEntity<?> getAllIssues(HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("Access denied");
        }

        List<Issue> issues = issueService.getAllIssues();
        return ResponseEntity.ok(issues);
    }

    /**
     * DTO for creating an issue.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueDTO {

        private String name;

        private String email;

        private String content;
    }

}
