package spring.ai.ollama.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;


@Service
public class ChatMemoryJdbcService {
	private final Logger log = LoggerFactory.getLogger(ChatMemoryJdbcService.class);
	
	private final ChatClient chatClient;
	
	
	public ChatMemoryJdbcService(
			JdbcChatMemoryRepository chatMemoryRepository,
			ChatClient.Builder chatClientBuilder
			) {
		ChatMemory chatMemory = MessageWindowChatMemory.builder()
				.chatMemoryRepository(chatMemoryRepository)
				.maxMessages(100)
				.build();
		
		this.chatClient = chatClientBuilder
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(chatMemory).build(),
						new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
						)
				.build();
	}
	
	public String chat(String userText, String conversationId) {
		this.log.debug("UserText: {}, ConversationId: {}", userText, conversationId);
		
		String answer = this.chatClient.prompt()
				.user(userText)
				.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
				.call()
				.content();
		
		return answer;
	}
	

}
