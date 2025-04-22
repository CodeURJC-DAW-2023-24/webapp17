package es.codeurjc.webapp17.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.PostService;
import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Flux;

@Controller
public class ChatController {
    
    
    private final OllamaChatClient chatClient;
    private final String postGeneratorPrompt = "Write an article about: ";

     @Autowired
    private PostService postService;


    private static final String LLM_IMAGE_PATH = "/images/LLM.png";




    
    public ChatController(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatClient.call(message));
    }

    @GetMapping("/ai/generateStream")
	public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(message);
        return chatClient.stream(prompt);

    }

    @PostMapping("/generate-post")
    public String response(HttpSession session,@RequestParam("tag") String inputString) {
        String prompt  = this.postGeneratorPrompt.concat(inputString);
        LocalDateTime now = LocalDateTime.now();
        String title =  "LLM Post about "+ inputString; 
        String content =  chatClient.call(prompt);
        String tag = inputString;

          

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(now);
        post.setTag(tag);
        post.setImage(LLM_IMAGE_PATH);
        Usr user = (Usr) session.getAttribute("user"); // Obtener el usuario de la sesión
            if (user == null) {
                // Manejar el caso en que el usuario no está autenticado
                return "redirect:/log_in"; // Redirigir a la página de inicio de sesión
            }
            post.setUsr(user);


        postService.addPost(post);


        return "redirect:/";
    } 
    @PostMapping("/chatbot/send")
public Map<String, String> handleChat(@RequestParam("message") String message) {
    String response = chatClient.call(message);
    return Map.of("response", response);
}



}