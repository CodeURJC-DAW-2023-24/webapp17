package es.codeurjc.webapp17.controller;

import java.util.ArrayList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    
    @GetMapping("/")
    public String index(Model model,HttpSession session) {
        ArrayList<Post> posts = (ArrayList<Post>) postService.getAllPost();
        model.addAttribute("posts", posts);
        Usr user = (Usr) session.getAttribute("user");
        if (user != null) {
            if (user.getRole() == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true);
                return "index";
            }
            }else {
                model.addAttribute("ADMIN", false);
            
        }
        return "index"; // Devuelve la plantilla index.html
    }


    
}