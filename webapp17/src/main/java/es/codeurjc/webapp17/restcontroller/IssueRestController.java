package es.codeurjc.webapp17.restcontroller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.service.IssueService;

@RestController
@RequestMapping("/api/issues")
public class IssueRestController {

    @Autowired
    private IssueService issueService;

    @PostMapping
    public ResponseEntity<Issue> createIssue(@RequestBody IssueRequest issueRequest) {
        Issue issue = new Issue();
        issue.setName(issueRequest.getName());
        issue.setEmail(issueRequest.getEmail());
        issue.setContent(issueRequest.getContent());
        issue.setDate(LocalDateTime.now());

        issueService.addIssue(issue);
        return new ResponseEntity<>(issue, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Clase interna para manejar la solicitud de creación de Issue
    private static class IssueRequest {
        private String name;
        private String email;
        private String content;

        // Getters y setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
