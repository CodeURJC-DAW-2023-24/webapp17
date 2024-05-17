package es.codeurjc.webapp17.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.webapp17.entity.Post;

@Controller
public class PostController {

    @GetMapping("/posts")
    public String getPosts(Model model) {
        List<Post> posts = new ArrayList<>();

        /*Post post1 = new Post("Java vs C", "ejemplo.html", "../images/entryphoto.png", "El debate entre Java y C es emblemático...",
                "July 15, 2006", "Design, XHTML, CSS", 19);

        Post post2 = new Post("Open source", "#", null, "El movimiento de código abierto es indudablemente una fuerza poderosa...",
                "November 22, 2005", "Information, Themes", 0);

        posts.add(post1);
        posts.add(post2);*/

        model.addAttribute("posts", posts);
        return "posts :: postList";
    }
}
