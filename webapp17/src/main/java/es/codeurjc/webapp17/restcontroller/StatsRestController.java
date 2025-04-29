package es.codeurjc.webapp17.restcontroller;


import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

/**
 * REST controller that provides various statistics about users, posts, and tags.
 */
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Operations related to users, posts, and tags statistics.")
public class StatsRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private UsrService userService;

    /**
     * Checks if the current user is logged in and has admin role.
     *
     * @param session the current HTTP session
     * @return a map containing admin status or redirection/error info
     */
    @Operation(summary = "Check if user is admin",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Authentication and role status",
                                content = @Content(schema = @Schema(implementation = Map.class)))
               })
    @GetMapping("")
    public Map<String, Object> checkAdmin(HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("error", "Not logged in");
            response.put("redirect", "/log_in");
        } else if (user.getRole() != Usr.Role.ADMIN) {
            response.put("error", "Not authorized");
            response.put("redirect", "/no_admin");
        } else {
            response.put("ADMIN", true);
        }

        return response;
    }

    /**
     * Returns a list of users with the number of posts each has created, sorted descending.
     *
     * @return list of maps with "user" and "postCount"
     */
    @Operation(summary = "Users with most posts",
               description = "Returns a list of users and how many posts each has written, sorted descending.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "List of users and their post counts",
                                content = @Content(array = @ArraySchema(schema = @Schema(implementation = Map.class))))
               })
    @GetMapping("/users-with-most-posts")
    public List<Map<String, Object>> usersWithMostPosts() {
        List<Usr> users = userService.getAllUsrs();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Usr user : users) {
            int count = user.getPosts() != null ? user.getPosts().size() : 0;
            result.add(Map.of("user", user.getUsername(), "postCount", count));
        }

        result.sort((a, b) -> ((Integer) b.get("postCount")).compareTo((Integer) a.get("postCount")));
        return result;
    }

    /**
     * Returns a list of posts with the number of comments each has, sorted descending.
     *
     * @return list of maps with "title" and "commentCount"
     */
    @Operation(summary = "Posts with most comments",
               description = "Returns a list of posts and how many comments each has, sorted descending.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "List of posts and their comment counts",
                                content = @Content(array = @ArraySchema(schema = @Schema(implementation = Map.class))))
               })
    @GetMapping("/posts-with-most-comments")
    public List<Map<String, Object>> postsWithMostComments() {
        List<Post> posts = postService.getAllPosts();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Post post : posts) {
            int count = post.getComments() != null ? post.getComments().size() : 0;
            result.add(Map.of("title", post.getTitle(), "commentCount", count));
        }

        result.sort((a, b) -> ((Integer) b.get("commentCount")).compareTo((Integer) a.get("commentCount")));
        return result;
    }

    /**
     * Returns a list of tags with the number of associated posts, sorted descending.
     *
     * @return list of maps with "tag" and "count"
     */
    @Operation(summary = "Tags with most posts",
               description = "Returns a list of tags with how many posts are associated with each, sorted descending.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "List of tags and their post counts",
                                content = @Content(array = @ArraySchema(schema = @Schema(implementation = Map.class))))
               })
    @GetMapping("/tags-with-most-posts")
    public List<Map<String, Object>> tagsWithMostPosts() {
        List<Post> posts = postService.getAllPosts();
        Map<String, Long> tagCounts = new HashMap<>();

        for (Post post : posts) {
            String tag = post.getTag();
            if (tag != null) {
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0L) + 1);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : tagCounts.entrySet()) {
            result.add(Map.of("tag", entry.getKey(), "count", entry.getValue()));
        }

        result.sort((a, b) -> ((Long) b.get("count")).compareTo((Long) a.get("count")));
        return result;
    }
}
 
