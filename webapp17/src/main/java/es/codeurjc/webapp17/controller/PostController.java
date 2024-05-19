package es.codeurjc.webapp17.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.webapp17.entity.Post;

import es.codeurjc.webapp17.service.PostService;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public String getPosts(Model model) {
        List<Post> posts = postService.getAllPost();
        
        model.addAttribute("posts", posts);
        return "posts :: postList";
    }


    @PostMapping("/create-post")
    public String createPost(@RequestParam String title, @RequestParam String content, @RequestParam String tag, @RequestParam(value = "image", required = false)MultipartFile image) throws IOException {
        
        LocalDateTime now = LocalDateTime.now();

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(now);
        post.setTag(tag);

        if (image != null && !image.isEmpty()) {
            post.setImage(image.getBytes());
        }

        postService.addPost(post);
    
        
        return "redirect:/";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/";
    }
}
