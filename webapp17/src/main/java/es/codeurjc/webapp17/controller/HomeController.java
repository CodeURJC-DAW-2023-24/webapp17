package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    /**
     * Displays the home page with a paginated list of posts.
     *
     * @param page    the current page number (default is 0)
     * @param size    the number of posts per page (default is 3)
     * @param model   the model to pass data to the view
     * @param session the current HTTP session, used to check for a logged-in user
     * @return the name of the view template ("index")
     */
    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model,
            HttpSession session) {
        Page<Post> posts = postService.getPosts(page, size);

        // Add pagination data to the model
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("size", size);

        // Calculate previous and next pages
        model.addAttribute("hasPrevious", posts.hasPrevious());
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("previousPage", page > 0 ? page - 1 : null);
        model.addAttribute("nextPage", posts.hasNext() ? page + 1 : null);

        // Check for user in session
        Usr user = (Usr) session.getAttribute("user");
        if (user != null) {
            if (user.getRole() == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true); // Set admin flag
                model.addAttribute("currentUser", true); // Pass current user info
            } else {
                model.addAttribute("ADMIN", false);
                model.addAttribute("currentUser", true);
            }
        } else {
            model.addAttribute("ADMIN", false); // No user in session   
            model.addAttribute("currentUser", null); // No user info
        }

        return "index"; // Return index view
    }

}