package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.service.IssueService;

@Controller
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping("/issue")
    public String createIssue(@RequestParam String name, @RequestParam String email, @RequestParam String content, Model model) {
        
        issueService.createIssue(content, name, email);
        return "/contacto"; // Redirige a la página principal después de crear el issue
    }

}
