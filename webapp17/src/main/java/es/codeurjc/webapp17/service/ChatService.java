
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

    private final OllamaChatClient chatClient;
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
  
   

    public ChatService(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
        
    }

   
}