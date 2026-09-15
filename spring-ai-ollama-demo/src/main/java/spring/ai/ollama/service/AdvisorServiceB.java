package spring.ai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import spring.ai.ollama.advisor.MaxCharLengthAdvisor;


@Service
public class AdvisorServiceB {
	private final ChatClient chatClient;
	
	
	public AdvisorServiceB(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder
				.defaultAdvisors(new MaxCharLengthAdvisor(Ordered.HIGHEST_PRECEDENCE))
				.build();
	}
	
	public String advisorContext(String question) {
		String response = this.chatClient.prompt()
				.advisors(advisorSpec -> {
					advisorSpec.param(MaxCharLengthAdvisor.MAX_CHAR_LENGTH, 100);
				})
				.user(question)
				.call()
				.content();
		
		return response;
	}

}
