package es.codeurjc.webapp17.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.service.CommentService;
import es.codeurjc.webapp17.service.PostService;

@Controller
public class PostController {

    private static final String DEFAULT_IMAGE_PATH = "/images/entryphoto.png";

    @Autowired
    private PostService postService;

     @Autowired
    private CommentService commentService;

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/create-post")
    public String createPost(@RequestParam String title, @RequestParam String content, @RequestParam String tag, @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        LocalDateTime now = LocalDateTime.now();

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(now);
        post.setTag(tag);

        if (image != null && !image.isEmpty()) {
            String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path filepath = Paths.get(uploadPath, filename);
            
            // Asegúrate de que el directorio existe
            if (!Files.exists(filepath.getParent())) {
                Files.createDirectories(filepath.getParent());
            }

            Files.write(filepath, image.getBytes());
            post.setImage("/images/" + filename); // Ruta de la imagen en el repositorio de activos
        }else {
            // Establecer la ruta de la imagen por defecto si no se selecciona ninguna
            post.setImage(DEFAULT_IMAGE_PATH);
        }

        postService.addPost(post);

        return "redirect:/";
    }

    @PostMapping("/{postId}/comment")
    public String addComment(@PathVariable Long postId, @RequestParam String comment) {
        Post post = postService.getPostById(postId);
        if (post != null) {
            Comment newComment = new Comment();
            newComment.setText(comment);
            newComment.setPost(post);
            
            // newComment.setUsr(user); 
            commentService.addComment(newComment);
        }
        return "redirect:/";
    }
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/";
    }
}
