package es.codeurjc.webapp17.restcontroller;

import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    /**
     * Deletes an issue based on its ID.
     *
     * @param id the ID of the issue
     * @return confirmation message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete issue")
    public ResponseEntity<?> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.status(201).body("Issue correctly deleted.");
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
