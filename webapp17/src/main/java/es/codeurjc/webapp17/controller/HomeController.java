package es.codeurjc.webapp17.controller;

import java.util.ArrayList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.webapp17.entity.Post;

import es.codeurjc.webapp17.service.PostService;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
        // Creamos algunos posts de prueba
        postService.addPost(new Post(null, null, null, null, "Java vs C", "July 15, 2006", "Andreas", "El debate entre Java y C...", null, null));
        postService.addPost(new Post(null, null, null, null, "Open source", "November 22, 2005", "Adrian", "El movimiento de código abierto...", null, null));
    }
    
    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Post> posts = (ArrayList<Post>) postService.getAllPost();
        model.addAttribute("posts", posts);
        return "index"; // Devuelve la plantilla index.html
    }
}