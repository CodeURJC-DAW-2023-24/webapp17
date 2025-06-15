package es.codeurjc.webapp17.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;

@Controller
public class StatsController {

    @Autowired
    private PostService postService;
    @Autowired
    private UsrService userService;

    /**
     * Controller for statistics-related endpoints.
     */
    @GetMapping("/estadisticas")
    public String showStats(Model model, HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");

        if (user != null) {
            if (user.getRole() == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true); // User is admin
            } else {
                model.addAttribute("ADMIN", false); // User is not admin
                return "/no_admin"; // Redirect non-admin users to index
            }
        } else {
            model.addAttribute("ADMIN", false); // No user in session
            return "redirect:/log_in"; // Redirect to login if user is not logged in
        }

        return "estadisticas"; // Return the statistics view
    }

    /**
     * Returns a list of users with their corresponding number of posts, sorted
     * descending.
     *
     * @return a list of maps with "user" and "postCount"
     */
    @Hidden
    @GetMapping("users")
    @ResponseBody
    public List<Map<String, Object>> usersWithMostPosts() {
        List<Usr> users = userService.getAllUsrs();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Usr user : users) {
            int count = user.getPosts() != null ? user.getPosts().size() : 0;
            result.add(Map.of("user", user.getUsername(), "postCount", count));
        }

        // Sort descending by post count
        result.sort((a, b) -> ((Integer) b.get("postCount")).compareTo((Integer) a.get("postCount")));
        return result;
    }

    /**
     * Returns a list of posts with their corresponding number of comments, sorted
     * descending.
     *
     * @return a list of maps with "title" and "commentCount"
     */
    @Hidden
    @GetMapping("posts")
    @ResponseBody
    public List<Map<String, Object>> postsWithMostComments() {
        List<Post> posts = postService.getAllPosts();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Post post : posts) {
            int count = post.getComments() != null ? post.getComments().size() : 0;
            result.add(Map.of("title", post.getTitle(), "commentCount", count));
        }

        // Sort descending by comment count
        result.sort((a, b) -> ((Integer) b.get("commentCount")).compareTo((Integer) a.get("commentCount")));
        return result;
    }

    /**
     * Returns a list of tags with the number of posts associated with each, sorted
     * descending.
     *
     * @return a list of maps with "tag" and "count"
     */
    @Hidden
    @GetMapping("tags")
    @ResponseBody
    public List<Map<String, Object>> tagsWithMostPosts() {
        // Step 1: Get all posts
        List<Post> posts = postService.getAllPosts();

        // Step 2: Count posts per tag
        Map<String, Long> tagCounts = new HashMap<>();
        for (Post post : posts) {
            String tag = post.getTag();
            tagCounts.put(tag, tagCounts.getOrDefault(tag, 0L) + 1);
        }

        // Step 3: Convert to list of maps
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : tagCounts.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("tag", entry.getKey());
            data.put("count", entry.getValue());
            result.add(data);
        }

        // Step 4: Sort descending by count
        result.sort((a, b) -> ((Long) b.get("count")).compareTo((Long) a.get("count")));

        // Step 5: Return result
        return result;
    }

}
