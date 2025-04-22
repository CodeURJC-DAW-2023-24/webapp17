package es.codeurjc.webapp17.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.annotations.Comments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.PostRepository;
import es.codeurjc.webapp17.service.CommentService;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class EstadisticasController {


    @Autowired
    private PostService postService;
    @Autowired
    private UsrService userService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model, HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user != null) {
            if (user.getRole() == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true);
            } else {
                model.addAttribute("ADMIN", false);
                return "redirect:/index";
            }
        } else {
            model.addAttribute("ADMIN", false);
            return "redirect:/log_in";
        }
        return "estadisticas";
        }

    // Usuarios con más posts
    @GetMapping("/users-with-most-posts")
    @ResponseBody
    public List<Map<String, Object>> usersWithMostPosts() {
        List<Usr> users = userService.getAllUsrs();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Usr user : users) {
            int count = user.getPosts() != null ? user.getPosts().size() : 0;
            result.add(Map.of("user", user.getUsername(), "postCount", count));
        }

        // Ordenar descendente por cantidad de posts
        result.sort((a, b) -> ((Integer)b.get("postCount")).compareTo((Integer)a.get("postCount")));
        return result;
    }

    // Posts con más comentarios
    @GetMapping("/posts-with-most-comments")
    @ResponseBody
    public List<Map<String, Object>> postsWithMostComments() {
        List<Post> posts = postService.getAllPosts();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Post post : posts) {
            int count = post.getComments() != null ? post.getComments().size() : 0;
            result.add(Map.of("title", post.getTitle(), "commentCount", count));
        }

        result.sort((a, b) -> ((Integer)b.get("commentCount")).compareTo((Integer)a.get("commentCount")));
        return result;
    }

    // Tags con más posts
    @GetMapping("/tags-with-most-posts")
    @ResponseBody
    public List<Map<String, Object>> tagsWithMostPosts() {
    // Paso 1: Obtener todos los posts
    List<Post> posts = postService.getAllPosts();

    // Paso 2: Contar la cantidad de posts por tag
    Map<String, Long> tagCounts = new HashMap<>();
    for (Post post : posts) {
        String tag = post.getTag();
        tagCounts.put(tag, tagCounts.getOrDefault(tag, 0L) + 1);
    }

    // Paso 3: Convertir el mapa en una lista de mapas con clave/valor
    List<Map<String, Object>> result = new ArrayList<>();
    for (Map.Entry<String, Long> entry : tagCounts.entrySet()) {
        Map<String, Object> data = new HashMap<>();
        data.put("tag", entry.getKey());
        data.put("count", entry.getValue());
        result.add(data);
    }

    // Paso 4: Ordenar la lista por cantidad (de mayor a menor)
    result.sort((a, b) -> ((Long) b.get("count")).compareTo((Long) a.get("count")));

    // Paso 5: Devolver el resultado
    return result;
}

}
