package spring.ai.ollama.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import spring.ai.ollama.advisor.AdvisorA;
import spring.ai.ollama.advisor.AdvisorB;
import spring.ai.ollama.advisor.AdvisorC;


@Service
public class AdvisorServiceA {
	private final Logger log = LoggerFactory.getLogger(AdvisorServiceA.class);
	
	private final ChatClient chatClient;
	
	public AdvisorServiceA(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.defaultAdvisors(
				new AdvisorA(),
				new AdvisorB()
				).build();
	}
	
	public String advisorChain1(String question) {
		this.log.debug("advisorChain1");
		
		String response = this.chatClient.prompt()
				.advisors(new AdvisorC())
				.user(question)
				.call()
				.content();
		return response;
	}
	
	public Flux<String> advisorChain2(String question) {
		this.log.debug("advisorChain2");
		
		Flux<String> response = this.chatClient.prompt()
				.advisors(new AdvisorC())
				.user(question)
				.stream()
				.content();
		return response;
	}

}
