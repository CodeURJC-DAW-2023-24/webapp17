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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.webapp17.dto.CommentDto;
import es.codeurjc.webapp17.dto.PostDto;
import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.CommentService;
import es.codeurjc.webapp17.service.PostService;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {

    private static final String DEFAULT_IMAGE_PATH = "/images/entryphoto.png";

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UsrService usrService;

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

        // Check if user is logged in using new session management
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/log_in"; // Redirect if not authenticated
        }

        // Get user information
        UsrBasicDto user = usrService.findUsrBasicById(userId);
        if (user == null) {
            return "redirect:/log_in"; // Redirect if user not found
        }

        // Handle image upload if present
        String imagePath = DEFAULT_IMAGE_PATH; // Default image
        if (image != null && !image.isEmpty()) {
            String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path filepath = Paths.get(uploadPath, filename);

            // Ensure the directory exists
            if (!Files.exists(filepath.getParent())) {
                Files.createDirectories(filepath.getParent());
            }

            Files.write(filepath, image.getBytes());
            imagePath = "/files/" + filename; // Image path in the asset repository
        }

        // Create PostDto
        PostDto postDto = new PostDto(
            null, // ID will be generated
            userId,
            user.username(),
            title,
            imagePath,
            content,
            LocalDateTime.now(),
            tag,
            java.util.List.of() // Empty comments for new post
        );

        // Add the post to the database
        postService.addPost(postDto);

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
        
        // Check if user is logged in
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/log_in"; // Redirect to login if not authenticated
        }

        // Get user information
        UsrBasicDto user = usrService.findUsrBasicById(userId);
        if (user == null) {
            return "redirect:/log_in";
        }

        // Verify post exists
        PostDto post = postService.getPostById(postId);
        if (post != null) {
            // Create CommentDto
            CommentDto newCommentDto = new CommentDto(
                null, // ID will be generated
                userId,
                user.username(),
                postId,
                LocalDateTime.now(),
                comment
            );

            commentService.addComment(newCommentDto);
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

    /**
     * Shows the edit form for a post.
     *
     * @param id      the ID of the post to edit
     * @param model   the model to pass data to the view
     * @param session the HTTP session
     * @return the edit form view or redirect to homepage
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        
        // Check if user is logged in
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/log_in";
        }

        // Get post
        PostDto post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/";
        }

        // Check if user is the owner of the post
        if (!post.userId().equals(userId)) {
            return "redirect:/"; // Redirect if not the owner
        }

        model.addAttribute("post", post);
        return "editpost";
    }

    /**
     * Updates an existing post.
     *
     * @param id      the ID of the post to update
     * @param title   the new title
     * @param content the new content
     * @param tag     the new tag
     * @param image   optional new image
     * @return redirect to my posts page
     * @throws IOException if there is an issue saving the uploaded image
     */
    @PostMapping("/{id}/edit")
    public String updatePost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String tag,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        
        // Get existing post
        PostDto existingPost = postService.getPostById(id);
        if (existingPost == null) {
            System.out.println("Post not found");
            return "redirect:/";
        }

        // Handle image upload if present
        String imagePath = existingPost.image(); // Keep existing image by default
        if (image != null && !image.isEmpty()) {
            String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path filepath = Paths.get(uploadPath, filename);
            
            if (!Files.exists(filepath.getParent())) {
                Files.createDirectories(filepath.getParent());
            }
            
            Files.write(filepath, image.getBytes());
            imagePath = "/files/" + filename; // Update the image path
        }

        // Create updated PostDto
        PostDto updatedPostDto = new PostDto(
            existingPost.id(),
            existingPost.userId(),
            existingPost.username(),
            title,
            imagePath,
            content,
            existingPost.date(), // Keep original creation date
            tag,
            existingPost.comments() // Keep existing comments
        );

        postService.updatePost(updatedPostDto);
        return "redirect:/myposts";
    }
}
