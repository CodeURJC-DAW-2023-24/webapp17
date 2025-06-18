package es.codeurjc.webapp17.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.CommentService;
import es.codeurjc.webapp17.service.PostService;
import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {

    private static final String DEFAULT_IMAGE_PATH = "/images/entryphoto.png";

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Creates a new post. If an image is uploaded, it saves it in the specified
     * directory, otherwise, a default image is used.
     * 
     * @param session the HTTP session containing the logged-in user's data
     * @param title   the title of the post
     * @param content the content of the post
     * @param tag     the tag associated with the post
     * @param image   an optional image file to attach to the post
     * @return a redirect to the homepage after the post is created
     * @throws IOException if there is an issue saving the uploaded image
     */
    @PostMapping("/post")
    public String createPost(HttpSession session, @RequestParam String title, @RequestParam String content,
            @RequestParam String tag, @RequestParam(value = "image", required = false) MultipartFile image)
            throws IOException {
        LocalDateTime now = LocalDateTime.now();

        // Create a new Post object
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(now);
        post.setTag(tag);

        // Get the current user from the session
        Usr user = (Usr) session.getAttribute("user");
        post.setUsr(user);

        // Handle image upload if present
        if (image != null && !image.isEmpty()) {
            String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path filepath = Paths.get(uploadPath, filename);

            // Ensure the directory exists
            if (!Files.exists(filepath.getParent())) {
                Files.createDirectories(filepath.getParent());
            }

            Files.write(filepath, image.getBytes());
            post.setImage("uploads/" + filename); // Image path in the asset repository
        } else {
            // Use default image if no image is uploaded
            post.setImage(DEFAULT_IMAGE_PATH);
        }

        // Add the post to the database
        postService.addPost(post);

        return "redirect:/"; // Redirect to the homepage after the post is created
    }

    /**
     * Adds a comment to a post. If the user is not authenticated, they will be
     * redirected to the login page.
     *
     * @param session the HTTP session containing the logged-in user's data
     * @param postId  the ID of the post to comment on
     * @param comment the content of the comment
     * @return a redirect to the homepage after the comment is added
     */
    @PostMapping("/{postId}/comment")
    public String addComment(HttpSession session, @PathVariable Long postId, @RequestParam String comment) {
        Post post = postService.getPostById(postId);
        if (post != null) {
            Comment newComment = new Comment();
            newComment.setText(comment);
            newComment.setPost(post);
            newComment.setDate(LocalDateTime.now());

            Usr user = (Usr) session.getAttribute("user"); // Get the user from the session
            if (user == null) {
                // Redirect to login if the user is not authenticated
                return "redirect:/log_in"; // Redirect to the login page
            }

            newComment.setUsr(user); // Associate the comment with the user
            commentService.addComment(newComment);
        }
        return "redirect:/"; // Redirect to the homepage after adding the comment
    }

    /**
     * Deletes a post by its ID.
     *
     * @param id the ID of the post to delete
     * @return a redirect to the homepage after the post is deleted
     */
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/"; // Redirect to the homepage after deleting the post
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id the ID of the comment to delete
     * @return a redirect to the homepage after the comment is deleted
     */
    @PostMapping("/{id}/delete_comment")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return "redirect:/"; // Redirect to the homepage after deleting the comment
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        Post post = postService.getPostById(id);
        Usr user = (Usr) session.getAttribute("user");
        if (post == null) {
            return "redirect:/";
        } else if (user == null || !(post.getUsr().getId() == user.getId())) {
            return "redirect:/";
        }
        model.addAttribute("post", post);
        return "editpost";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String tag,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        Post post = postService.getPostById(id);
        if (post == null) {
            System.out.println("Post not found");
            return "redirect:/";
        }

        post.setTitle(title);
        post.setContent(content);
        post.setTag(tag);

        // (Optional) Handle image upload if present
        if (image != null && !image.isEmpty()) {
            String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path filepath = Paths.get(uploadPath, filename);
            if (!Files.exists(filepath.getParent())) {
                Files.createDirectories(filepath.getParent());
            }
            Files.write(filepath, image.getBytes());
            post.setImage("uploads/" + filename); // Update the image path
        }

        postService.updatePost(post);
        return "redirect:/myposts";
    }

}
