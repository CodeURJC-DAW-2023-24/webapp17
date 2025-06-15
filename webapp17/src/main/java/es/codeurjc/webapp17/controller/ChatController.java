package es.codeurjc.webapp17.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    /**
     * Handles GET requests to generate a text response using the AI model.
     *
     * @param message the prompt to send to the AI (default is "Tell me a joke")
     * @return a map containing the generated response
     */
    @GetMapping("/ai/generate")
    public Map<String, String> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatClient.call(message));
    }

    /**
     * Handles GET requests to stream AI-generated responses in real time.
     *
     * @param message the prompt to send to the AI (default is "Tell me a joke")
     * @return a Flux stream of ChatResponse objects
     */
    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(message);
        return chatClient.stream(prompt);
    }

    /**
     * Handles POST requests to generate a blog post using the AI model and saves
     * it.
     *
     * This method uses a base prompt with a user-provided tag, generates the post
     * content,
     * and stores the post in the database associated with the logged-in user.
     *
     * @param session     the current HTTP session, used to retrieve the logged-in
     *                    user
     * @param inputString the user-provided tag or topic for the post
     * @return a redirect to the home page if successful, or to login if the user is
     *         not authenticated
     */
    @PostMapping("ai-post")
    public String response(HttpSession session, @RequestParam("tag") String inputString) {
        String prompt = this.postGeneratorPrompt.concat(inputString);
        LocalDateTime now = LocalDateTime.now();
        String title = "LLM Post about " + inputString;
        String content = chatClient.call(prompt);
        String tag = inputString;

        // Create and populate the new post
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(now);
        post.setTag(tag);
        post.setImage(LLM_IMAGE_PATH);

        // Retrieve the user from the session
        Usr user = (Usr) session.getAttribute("user");
        if (user == null) {
            // Redirect to login if the user is not authenticated
            return "redirect:/log_in";
        }
        post.setUsr(user);

        // Save the post
        postService.addPost(post);

        // Redirect to home page
        return "redirect:/";
    }

    /**
     * Handles POST requests to send a message to the chatbot and get a response.
     *
     * @param message the message sent by the user
     * @return a map containing the chatbot's response
     */
    @PostMapping("/chatbot/messages")
    public Map<String, String> handleChat(@RequestParam("message") String message) {
        String response = chatClient.call(message);
        return Map.of("response", response);
    }

}