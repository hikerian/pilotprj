package spring.ai.ollama.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.core.Ordered;

import reactor.core.publisher.Flux;


public class AdvisorC implements CallAdvisor, StreamAdvisor {
	private final Logger log = LoggerFactory.getLogger(AdvisorC.class);

	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 3;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		this.log.info("[전처리]");
		
		ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);
		
		this.log.info("[후처리]");
		
		return response;
	}
	
	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
			StreamAdvisorChain streamAdvisorChain) {
		this.log.info("[전처리]");
		
		Flux<ChatClientResponse> response = streamAdvisorChain.nextStream(chatClientRequest);
		response.blockLast();
		
		this.log.info("[후처리]");
		
		return response;
	}

}
