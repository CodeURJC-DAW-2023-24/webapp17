package es.codeurjc.webapp17.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.webapp17.entity.Issues;
import es.codeurjc.webapp17.service.ForumService;

@Controller
public class HomeController {

    @Autowired
    private ForumService forumService;
    
    @GetMapping("/")
    public String index() {
        ArrayList<Issues> forum;
        return "index"; // Devuelve la plantilla index.html
    }
}