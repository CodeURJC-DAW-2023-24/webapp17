package es.codeurjc.webapp17.restcontroller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.service.PostService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class ChatRestController {
    
    private final OllamaChatClient chatClient;
    private final String postGeneratorPrompt = "Write an article about: ";

    @Autowired
    private PostService postService;

    private static final String LLM_IMAGE_PATH = "/images/LLM.png";
    
    public ChatRestController(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String response = chatClient.call(message);
        return ResponseEntity.ok(Map.of("generation", response));
    }

    @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(message);
        return chatClient.stream(prompt);
    }

    @PostMapping("/generate-post")
    public ResponseEntity<Post> generatePost(@RequestParam("tag") String inputString) {
        String prompt = this.postGeneratorPrompt.concat(inputString);
        LocalDateTime now = LocalDateTime.now();
        String title = "LLM Post about " + inputString; 
        String content = chatClient.call(prompt);
        String tag = inputString;

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(now);
        post.setTag(tag);
        post.setImage(LLM_IMAGE_PATH);
        //post.setUsr(LLM);

        postService.addPost(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    } 
}

