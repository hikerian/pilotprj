package spring.ai.ollama.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.Ordered;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class RAGService {
	private final Logger log = LoggerFactory.getLogger(RAGService.class);
	
	private final ChatClient chatClient;
	private final VectorStore vectorStore;
//	private final JdbcTemplate jdbcTemplate;
	
	
	public RAGService(ChatClient.Builder chatClientBuilder
			, VectorStore vectorStore
//			, JdbcTemplate jdbcTemplate
			) {
		this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)).build();
		this.vectorStore = vectorStore;
//		this.jdbcTemplate = jdbcTemplate;
	}
	
	public String ragChat(String question, double score, String source) {
		// 벡터 저장소 검색 조건 생성
		SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
				.similarityThreshold(score)
				.topK(3);
		
		if(StringUtils.hasText(source)) {
			searchRequestBuilder.filterExpression("source == '%s'".formatted(source));
		}
		SearchRequest searchRequest = searchRequestBuilder.build();
		
		// QuestionAnswerAdvisor 생성
		QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(this.vectorStore)
				.searchRequest(searchRequest)
				.build();
		
		// 프롬프트를 LLM으로 전송하고 응답을 받는 코드
		String answer = this.chatClient.prompt()
				.user(question)
				.advisors(questionAnswerAdvisor)
				.call()
				.content();
		
		return answer;
		
	}

}
