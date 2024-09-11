package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
public String index(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "3") int size,
    Model model,
    HttpSession session
) {
    Page<Post> posts = postService.getPosts(page, size);
    model.addAttribute("posts", posts);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", posts.getTotalPages());
    model.addAttribute("size", size);

    // Calcular las páginas anterior y siguiente
    model.addAttribute("hasPrevious", posts.hasPrevious());
    model.addAttribute("hasNext", posts.hasNext());
    model.addAttribute("previousPage", page > 0 ? page - 1 : null);
    model.addAttribute("nextPage", posts.hasNext() ? page + 1 : null);

    Usr user = (Usr) session.getAttribute("user");
    if (user != null) {
        if (user.getRole() == Usr.Role.ADMIN) {
            model.addAttribute("ADMIN", true);
            model.addAttribute("currentUser", user);
        } else {
            model.addAttribute("ADMIN", false);
        }
    }
    return "index"; // Devuelve la plantilla index.html
}
}