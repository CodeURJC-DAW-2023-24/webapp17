package es.codeurjc.webapp17.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.service.IssueService;

@Controller
public class AdminController {

    @Autowired
    private IssueService issuesService;
    
    @GetMapping("/admin")
    public String adminPage(Model model) {
        // Aquí verificas si el usuario es administrador
        boolean isAdmin = true;
        ArrayList<Issue> issues = (ArrayList<Issue>) issuesService.getAllIssues();
        model.addAttribute("issues", issues);


        if (isAdmin) {
            // Si el usuario es administrador, se muestra la página de administración
            return "admin";
        } else {
            // Si el usuario no es administrador, se redirige a la página de error
            return "redirect:/error-no-admin";
        }
    }

    @GetMapping("/error-no-admin")
    public String errorNoAdminPage() {
        // Página de error para usuarios que no son administradores
        return "error-no-admin";
    }
}
