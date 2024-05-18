package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.service.IssueService;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    @Autowired
    private IssueService issueService;

    @GetMapping
    public String mostrarFormularioContacto() {
        return "contacto";
    }

    @PostMapping("/contact")
    public String createIssue(@ModelAttribute("issueForm") IssueForm issueForm) {
        issueService.createIssue(issueForm.getContent(), issueForm.getName(), issueForm.getEmail());
        return "/contacto"; // Redirige a la página principal después de crear el issue
    }
    public static class IssueForm {
        private String content;
        private String name;
        private String email;

        // Getters y setters

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
    }

