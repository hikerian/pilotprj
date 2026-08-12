package spring.ai.ollama.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import spring.ai.ollama.dto.Hotel;
import spring.ai.ollama.dto.ReviewClassification;


@Service
public class OutputConverterService {
	private final Logger log = LoggerFactory.getLogger(OutputConverterService.class);
	
	private final ChatClient chatClient;
	
	
	public OutputConverterService(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)).build();
	}
	
	/*
	 * ListOutputConverter
	 */
	public List<String> listOutputConverterLowLevel(String city) {
		// 구조화된 출력 변환기 생성
		ListOutputConverter converter = new ListOutputConverter();
		
		// 프롬프트 템플릿 생성
		PromptTemplate promptTemplate = PromptTemplate.builder()
				.template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
				.build();
		
		// 프롬프트 생성
		Prompt prompt = promptTemplate.create(
				Map.of("city", city, "format", converter.getFormat())
				);
		
		// LLM의 쉼표로 구분된 텍스트 출력 얻기
		String commaSeparatedString = this.chatClient.prompt(prompt)
				.call()
				.content();
		
		this.log.debug("LLM 응답: {}", commaSeparatedString);
		
		// List<String>으로 변환
		List<String> hotelList = converter.convert(commaSeparatedString);
		
		this.log.debug("변환된 값: {}", hotelList);
		
		return hotelList;
	
	}
	
	public List<String> listOutputConverterHighLevel(String city) {
		List<String> hotelList = this.chatClient.prompt()
				.user("%s에서 유명한 호텔 목록 5개를 출력하세요.".formatted(city))
				.call()
				.entity(new ListOutputConverter());
		
		return hotelList;
	}
	
	
	/*
	 * BeanOutputConverter
	 */
	public Hotel beanOutputConverterLowLevel(String city) {
		// 구조화된 출력 변환기 생성
		BeanOutputConverter<Hotel> beanOutputConverter = new BeanOutputConverter<>(Hotel.class);
		
		this.log.debug("beanOutputConverter의 format은={}", beanOutputConverter.getFormat());
		
		// 프롬프트 템플릿 생성
		PromptTemplate promptTemplate = PromptTemplate.builder()
				.template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
				.build();
		
		// 프롬프트 생성
		Prompt prompt = promptTemplate.create(
				Map.of("city", city, "format", beanOutputConverter.getFormat())
				);
		
		// LLM의 JSON 출력 얻기
		String json = this.chatClient.prompt(prompt)
				.call()
				.content();
		
		// JSON을 Hotel로 매핑해서 변환
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
	
	
	/*
	 * MapOutputConverter
	 */
	public Map<String, Object> mapOutputConverterLowLevel(String hotel) {
		// 구조화된 출력 변환기 생성
		MapOutputConverter mapOutputConverter = new MapOutputConverter();
		
		// 프롬프트 템플릿 생성
		PromptTemplate promptTemplate = new PromptTemplate("호텔 {hotel}에 대해 정보를 알려주세요. {format}");
		
		// 프롬프트 생성
		Prompt prompt = promptTemplate.create(
				Map.of("hotel", hotel, "format", mapOutputConverter.getFormat())
				);
		
		// LLM의 JSON 출력 얻기
		String json = this.chatClient.prompt(prompt)
				.call()
				.content();
		
		// Map<String, Object>로 변환
		Map<String, Object> hotelInfo = mapOutputConverter.convert(json);
		
		return hotelInfo;
	}
	public Map<String, Object> mapOutputConverterHighLevel(String hotel) {
		Map<String, Object> hotelInfo = this.chatClient.prompt()
				.user("호텔 %s에 대해 정보를 알려주세요.".formatted(hotel))
				.call()
				.entity(new MapOutputConverter());
		
		return hotelInfo;
	}

	
	/*
	 * with SystemMessage
	 */
	public ReviewClassification classifyReview(String review) {
		ReviewClassification reviewClassification = this.chatClient.prompt()
				.system("""
						영화 리뷰를 [POSITIVE, NEUTRAL, NEGATIVE] 중에서 하나로 분류하고,
						유효한 JSON을 반환하세요.
						""")
				.user("%s".formatted(review))
				.options(ChatOptions.builder().temperature(0.0))
				.call()
				.entity(ReviewClassification.class);
		
		return reviewClassification;
	}


}
