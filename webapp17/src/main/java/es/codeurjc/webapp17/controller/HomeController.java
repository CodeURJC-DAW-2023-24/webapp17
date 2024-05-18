package es.codeurjc.webapp17.controller;

import java.util.ArrayList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.entity.Post;

import es.codeurjc.webapp17.service.PostService;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
        
    }
    
    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Post> posts = (ArrayList<Post>) postService.getAllPost();
        model.addAttribute("posts", posts);
        return "index"; // Devuelve la plantilla index.html
    }

    
}