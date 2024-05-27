
package es.codeurjc.webapp17.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final OllamaChatClient chatClient;
   

    public ChatService(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
        
    }

    public String establishChat() {
        String chatId = UUID.randomUUID().toString();
        logger.debug("Establishing chat with chatId: {}", chatId);
       
        return chatId;
    }

    public Flux<ChatResponse> chat(String chatId, String message) {
        
        UserMessage userMessage = new UserMessage(message);
       
        logger.debug("Chatting with chatId: {} and message: {}", chatId, message);
        Prompt prompt = new Prompt(List.of(userMessage));
        return chatClient.stream(prompt);
    }
}