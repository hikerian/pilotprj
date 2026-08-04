package spring.ai.ollama.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.ollama.tool.IanTools;



@RestController
@RequestMapping("/ollama")
public class ChatController {
	private final Logger log = LoggerFactory.getLogger(ChatController.class);
	
	private final ChatClient chatClient;
	
	
	public ChatController(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}
	
	@PostMapping("/chat")
	public String chat(@RequestParam String question) {
		this.log.debug("question: {}", question);
		
		String answerText = this.chatClient.prompt(new Prompt(
				question,
				OllamaChatOptions.builder()
				.model("gemma4:e2b")
				.temperature(0.4)
				.build()
			))
			.tools(new IanTools())
			.call()
			.content();
		
		this.log.debug("answer: {}", answerText);
		
		return answerText;
	}
	

}
