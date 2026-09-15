package spring.ai.ollama.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.mariadb.MariaDBVectorStore;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class ChatMemoryVectorDBService {
	private final Logger log = LoggerFactory.getLogger(ChatMemoryVectorDBService.class);

	private final ChatClient chatClient;


	public ChatMemoryVectorDBService(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel,
			ChatClient.Builder chatClientBuilder) {
		VectorStore vectorStore = MariaDBVectorStore.builder(jdbcTemplate, embeddingModel)
//				.initializeSchema(true).schemaName("ollama").vectorTableName("chat_memory_vector_store").dimensions(768)
				.build();

		this.chatClient = chatClientBuilder

				.defaultAdvisors(VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),
						new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
				.build();
	}
	
	public String chat(String userText, String conversationId) {
		this.log.debug("chat");
		
		String answer = this.chatClient.prompt()
				.user(userText)
				.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
				.call()
				.content();
		
		return answer;
	}

}
