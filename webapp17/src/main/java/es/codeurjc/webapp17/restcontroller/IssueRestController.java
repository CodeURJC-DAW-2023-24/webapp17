package es.codeurjc.webapp17.restcontroller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST controller to manage issue-related operations.
 */
@RestController
@RequestMapping("/api/issues")
public class IssueRestController {

    @Autowired
    private IssueService issueService;

    /**
     * Creates a new issue and stores it in the database.
     *
     * @param dto the data transfer object containing the issue information
     * @return confirmation message
     */
    @PostMapping
    @Operation(summary = "Create a new issue")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    public ResponseEntity<?> createIssue(@RequestBody IssueDTO dto) {
        Issue issue = new Issue();
        issue.setName(dto.getName());
        issue.setEmail(dto.getEmail());
        issue.setContent(dto.getContent());
        issue.setDate(LocalDateTime.now());

        issueService.addIssue(issue);
        return ResponseEntity.status(201).body("Issue successfully created.");
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
