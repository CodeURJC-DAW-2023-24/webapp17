package es.codeurjc.webapp17.controller;

import java.util.Map;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;

@Controller
public class ChatController {
    
    
    private final OllamaChatClient chatClient;

    
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

    @GetMapping("/ai/generate/{prompt}")
    public String response(@PathVariable("prompt") String prompt) {
        System.out.println("PROMT: " + prompt);
        System.out.println("RESPONSE: " + chatClient.call(prompt));
        return "redirect:/";
    } 


}