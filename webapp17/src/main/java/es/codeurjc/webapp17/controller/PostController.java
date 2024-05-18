package es.codeurjc.webapp17.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public String getPosts(Model model) {
        List<Post> posts = postService.getAllPost();
        
        model.addAttribute("posts", posts);
        return "posts :: postList";
    }


    @PostMapping("/create-post")
    public String createPost(@ModelAttribute Post post, Usr principal) {
        // Establecer automáticamente la fecha actual
        post.setDate(LocalDateTime.now());

        // Establecer el autor basado en el usuario autenticado
        post.setUsr(principal);

        // Guardar el post en la base de datos
        postService.addPost(post);

        // Redireccionar a una página de confirmación u otra página apropiada
        return "redirect:/";
    }
}
