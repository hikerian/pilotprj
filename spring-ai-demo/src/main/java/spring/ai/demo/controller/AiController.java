package spring.ai.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import spring.ai.demo.service.AiServiceByChatClient;


@RestController
@RequestMapping("/ai")
public class AiController {
//	@Autowired
//	private AiService aiService;
	
	@Autowired
	private AiServiceByChatClient aiService;
	

	// ##### 요청 매핑 메소드 #####
	@PostMapping(
			value = "/chat",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.TEXT_PLAIN_VALUE)
	public String chat(@RequestParam("question") String question) {
		return "아직 모델과 연결되지 않았습니다.";
	}
	
	@PostMapping(
			value="/chat-model",
			consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces=MediaType.TEXT_PLAIN_VALUE
	)
	public String chatModel(@RequestParam("question") String question) {
		String answerText = this.aiService.generateText(question);
		return answerText;
	}
	
	@PostMapping(
			value="/chat-model-stream",
			consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces=MediaType.APPLICATION_NDJSON_VALUE
	)
	public Flux<String> chatModelStream(@RequestParam("question") String question) {
		Flux<String> answerStreamText = this.aiService.generateStreamText(question);
		return answerStreamText;
	}


}
