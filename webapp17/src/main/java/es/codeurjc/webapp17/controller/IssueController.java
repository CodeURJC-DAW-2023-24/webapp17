package es.codeurjc.webapp17.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.IssueService;

@Controller
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping("/issue")
    public String createIssue(@RequestParam String name, @RequestParam String email, @RequestParam String content, Model model) {
        LocalDateTime now = LocalDateTime.now();

        Issue issue = new Issue();
        issue.setName(name);
        issue.setEmail(email);
        issue.setContent(content);
        issue.setDate(now);

        issueService.addIssue(issue);

       
        return "/contacto"; // Redirige a la página principal después de crear el issue
    }

     @PostMapping("issue/{id}/delete")
    public String deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return "redirect:/admin";
    }


}
