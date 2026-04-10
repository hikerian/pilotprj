package spring.ai.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import spring.ai.demo.dto.Hotel;

@Service
public class AiServiceParameterizedTypeReference {
    private ChatClient chatClient;

    public AiServiceParameterizedTypeReference(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<Hotel> genericBeanOutputConverterLowLevel(String cities) {
        BeanOutputConverter<List<Hotel>> beanOutputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<Hotel>>() {
                });

        PromptTemplate promptTemplate = new PromptTemplate("""
                        다음 도시들에서 유명한 호텔 3개를 출력하세요.
                        {cities}
                        {format}
                """);

        Prompt prompt = promptTemplate.create(Map.of("cities", cities, "format", beanOutputConverter.getFormat()));

        String json = this.chatClient.prompt(prompt)
                .call()
                .content();

        List<Hotel> hotelList = beanOutputConverter.convert(json);

        return hotelList;
    }

    public List<Hotel> generixBeanOutputConverterHighLevel(String cities) {
        List<Hotel> hotelList = this.chatClient.prompt()
                .user("""
                        다음 도시들에서 유명한 호텔 3개를 출력하세요.
                        %s
                        """.formatted(cities))
                .call()
                .entity(new ParameterizedTypeReference<List<Hotel>>() {
                });

        return hotelList;
    }

}
