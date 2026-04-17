package spring.ai.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import spring.ai.demo.dto.ReviewClassification;

@Service
public class AiServiceSystemMessage {
    private ChatClient chatClient;

    public AiServiceSystemMessage(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ReviewClassification classifyReview(String review) {
        ReviewClassification reviewClassification = this.chatClient.prompt()
                .system("""
                                영화 리뷰를 [POSITIVE, NEUTRAL, NEGATIVE] 중에서 하나로 분류하고,
                                유효한 JSON을 반환하세요.
                        """)
                .user("%s".formatted(review))
                .options(ChatOptions.builder().temperature(0.0).build())
                .call()
                .entity(ReviewClassification.class);

        return reviewClassification;
    }

}
