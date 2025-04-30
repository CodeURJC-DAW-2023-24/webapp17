package es.codeurjc.webapp17.restcontroller;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for chatbot interaction and AI-generated post creation.
 */
@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatRestController {

    @Autowired
    private PostService postService;

    private final OllamaChatClient chatClient;
    private static final String LLM_IMAGE_PATH = "/images/LLM.png";
    private static final String postGeneratorPrompt = "Write an article about: ";

    public ChatRestController(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * DTO representing a chat request containing message history.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequest {

        private List<Message> history;
    }

    /**
     * DTO representing a single chat message with role and content.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {

        private String role;

        private String content;
    }

    /**
     * DTO representing a single user message to be sent to the AI model.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleMessageDTO {

        private String message;
    }

    /**
     * Handles a chat request and returns the AI-generated response.
     * 
     * @param request The chat request containing the message history.
     * @return A map with the response from the AI model.
     */
    @Operation(summary = "Get AI response with message history")
    @ApiResponse(responseCode = "200", description = "Successful response")
    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> handleChat(@RequestBody ChatRequest request) {
        List<Message> history = request.getHistory();

        String historyStr = history.stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));

        String response = chatClient.call(historyStr);

        return Map.of("response", response);
    }

    /**
     * Generates a blog post using AI and stores it in the database.
     * 
     * @param session The HTTP session used to identify the logged-in user.
     * @param tag     The topic tag for the AI-generated post.
     * @return HTTP response indicating success or failure.
     */
    @Operation(summary = "Generate blog post using AI and save to DB")
    @ApiResponse(responseCode = "201", description = "Post created")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    @PostMapping(value = "/generate-post")
    public ResponseEntity<String> generatePost(HttpSession session, @RequestBody SimpleMessageDTO tag) {

        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("No right permissions");
        }

        String prompt = postGeneratorPrompt + tag;
        String title = "LLM Post about " + tag;
        String content = chatClient.call(prompt);
        LocalDateTime now = LocalDateTime.now();

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(now);
        post.setTag(tag.getMessage());
        post.setImage(LLM_IMAGE_PATH);
        post.setUsr(user);

        postService.addPost(post);

        // Build URI like /posts/{id}
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/posts/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).body("Post created successfully.");
    }

}
