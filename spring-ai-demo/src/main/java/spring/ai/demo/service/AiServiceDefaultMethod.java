package spring.ai.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class AiServiceDefaultMethod {
    private ChatClient chatClient;

    public AiServiceDefaultMethod(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("적절한 감탄사, 웃음 등을 넣어서 친절하게 대화해 주세요.")
                .defaultOptions(ChatOptions.builder()
                        .temperature(1.0D)
                        .maxTokens(300).build())
                .build();
    }

    public Flux<String> defaultMethod(String question) {
        Flux<String> response = this.chatClient.prompt()
                .user(question)
                .stream()
                .content();

        return response;
    }

}
