package es.codeurjc.webapp17.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.service.IssueService;

@Controller
public class ContactoController {

    


    @GetMapping("/contacto")
    public String contacto(Model model) {
        
        return "contacto"; // Devuelve la plantilla index.html
    }

    
    
    }

