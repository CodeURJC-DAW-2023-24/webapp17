package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.dto.PostDto;
import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class MyPosts {

    @Autowired
    private PostService postService;

    @Autowired
    private UsrService usrService;

    /**
     * Displays the user's posts page with a paginated list of their posts.
     *
     * @param page    the current page number (default is 0)
     * @param size    the number of posts per page (default is 3)
     * @param model   the model to pass data to the view
     * @param session the current HTTP session, used to check for a logged-in user
     * @return the name of the view template ("myposts")
     */
    @GetMapping("/myposts")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model,
            HttpSession session) {

        // Check if user is logged in using new session management
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");

        if (userId == null || userRole == null) {
            return "redirect:/log_in"; // Redirect to login if no user in session
        }

        // Get user basic information for display
        UsrBasicDto user = usrService.findUsrBasicById(userId);
        if (user == null) {
            return "redirect:/log_in"; // Redirect if user not found
        }

        // Set user attributes in model
        if (userRole == Usr.Role.ADMIN) {
            model.addAttribute("ADMIN", true); // Set admin flag
            model.addAttribute("currentUser", true); // Pass current user info
            model.addAttribute("name", user.username()); // Set username
        } else {
            model.addAttribute("ADMIN", false);
            model.addAttribute("currentUser", true);
            model.addAttribute("name", user.username());
        }

        // Get user's posts using the new DTO-based method
        Page<PostDto> posts = postService.getPostsByUsr(page, size, userId);

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

        model.addAttribute("isOwner", true); // User is viewing their own posts
        return "myposts"; // Return myposts view
    }
}
