package spring.ai.demo.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AiServiceMultiMessage {
    private ChatClient chatClient;

    public AiServiceMultiMessage(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String multiMessage(String question, List<Message> chatMemory) {
        SystemMessage systemMessage = SystemMessage.builder()
                .text("""
                            당신은 AI 비서입니다.
                            제공되는 지난 대화 내용을 보고 우선적으로 답변해 주세요.
                        """).build();

        if (chatMemory.size() == 0) {
            chatMemory.add(systemMessage);
        }

        System.out.println("chatMemory: " + chatMemory);

        ChatResponse chatResponse = this.chatClient.prompt()
                .messages(chatMemory)
                .user(question)
                .call()
                .chatResponse();

        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .build();
        chatMemory.add(userMessage);

        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        chatMemory.add(assistantMessage);

        String text = assistantMessage.getText();

        return text;

    }

}
