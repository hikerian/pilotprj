package spring.ai.demo.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import spring.ai.demo.dto.Hotel;

@Service
public class AiServiceBeanOutputConverter {
    private ChatClient chatClient;

    public AiServiceBeanOutputConverter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Hotel beanOutputConverterLowLevel(String city) {
        BeanOutputConverter<Hotel> beanOutputConverter = new BeanOutputConverter<>(Hotel.class);

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
                .build();

        Prompt prompt = promptTemplate.create(Map.of("city", city, "format", beanOutputConverter.getFormat()));

        String json = this.chatClient.prompt(prompt)
                .call()
                .content();

        Hotel hotel = beanOutputConverter.convert(json);

        return hotel;
    }

    public Hotel beanOutputConverterHighLevel(String city) {
        Hotel hotel = this.chatClient.prompt()
                .user("%s에서 유명한 호텔 목록 5개를 출력하세요.".formatted(city))
                .call()
                .entity(Hotel.class);

        return hotel;
    }

}
