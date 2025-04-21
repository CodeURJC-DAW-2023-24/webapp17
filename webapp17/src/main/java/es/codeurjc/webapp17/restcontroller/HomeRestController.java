package es.codeurjc.webapp17.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeRestController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestHeader(value = "User-Role", required = false) String userRole,
            @RequestHeader(value = "User-Id", required = false) Long userId
    ) {
        Page<Post> posts = postService.getPosts(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("currentPage", page);
        response.put("totalPages", posts.getTotalPages());
        response.put("size", size);
        response.put("hasPrevious", posts.hasPrevious());
        response.put("hasNext", posts.hasNext());
        response.put("previousPage", page > 0 ? page - 1 : null);
        response.put("nextPage", posts.hasNext() ? page + 1 : null);

        if (userRole != null && userId != null) {
            boolean isAdmin = Usr.Role.ADMIN.name().equals(userRole);
            response.put("isAdmin", isAdmin);
            if (isAdmin) {
                response.put("currentUserId", userId);
            }
        }

        return ResponseEntity.ok(response);
    }
}
