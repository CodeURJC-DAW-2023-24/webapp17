package es.codeurjc.webapp17.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.service.IssueService;

@Controller
public class IssueController {

    @Autowired
    private IssueService issueService;

    /**
     * Creates a new issue with the provided details and saves it to the database.
     *
     * @param name    the name of the person reporting the issue
     * @param email   the email of the person reporting the issue
     * @param content the content describing the issue
     * @param model   the model to pass data to the view (though not used in this
     *                case)
     * @return a redirect to the "contacto" page after the issue is created
     */
    @PostMapping("/issue")
    public String createIssue(@RequestParam String name, @RequestParam String email, @RequestParam String content,
            Model model) {
        LocalDateTime now = LocalDateTime.now();

        Issue issue = new Issue();
        issue.setName(name);
        issue.setEmail(email);
        issue.setContent(content);
        issue.setDate(now);

        issueService.addIssue(issue);

        // Redirect to the contact page after creating the issue
        return "/contacto"; // Redirect to the contact page after the issue is created
    }

    /**
     * Deletes an issue by its ID.
     *
     * @param id the ID of the issue to be deleted
     * @return a redirect to the admin page after the issue is deleted
     */
    @PostMapping("issue/{id}/delete")
    public String deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        // Redirect to the admin page after deleting the issue
        return "redirect:/admin";
    }

}
