package es.codeurjc.webapp17.restcontroller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import es.codeurjc.webapp17.restcontroller.ChatRestController.ChatRequest;
import org.springframework.ai.ollama.OllamaChatClient;

@RestController
@RequestMapping("/chatbot")
public class ChatRestController {

    // DTO para mapear el JSON entrante
    public static class ChatRequest {
        private List<Message> history;
        public List<Message> getHistory() { return history; }
        public void setHistory(List<Message> history) { this.history = history; }
    }

    public static class Message {
        private String role;
        private String content;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    private final OllamaChatClient chatClient;

    public ChatRestController(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> handleChat(@RequestBody ChatRequest request) {
        List<Message> history = request.getHistory();

        // Convertir historial a String
        String historyStr = history.stream()
            .map(msg -> msg.getRole() + ": " + msg.getContent())
            .collect(Collectors.joining("\n"));

        // Enviar el historial como String al método stream
        String response = chatClient.call(historyStr);

        return Map.of("response", response);
    }
}