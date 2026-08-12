package spring.ai.ollama.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class RAGService {
	private final Logger log = LoggerFactory.getLogger(RAGService.class);
	
	private final ChatClient chatClient;
	private final VectorStore vectorStore;
	private final JdbcTemplate jdbcTemplate;
	
	private final ChatModel chatModel;
	private final ChatMemory chatMemory;
	
	
	public RAGService(ChatClient.Builder chatClientBuilder
			, VectorStore vectorStore
			, JdbcTemplate jdbcTemplate
			, ChatModel chatModel
			, ChatMemory chatMemory
			) {
		this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)).build();
		this.vectorStore = vectorStore;
		this.jdbcTemplate = jdbcTemplate;
		
		this.chatModel = chatModel;
		this.chatMemory = chatMemory;
	}
	
	/**
	 * 벡터 저장소의 모든 데이터를 삭제.
	 */
	public void clearVectorStore() {
		this.log.debug("clearVectorStore");
		
		this.jdbcTemplate.update("truncate table vector_store;");
	}
	
	
	/*
	 * QuestionAnswerAdvisor
	 */
	/**
	 * QuestionAnswerAdvisor를 이용한 RAG Chat.
	 * @param question
	 * @param score
	 * @param source
	 * @return
	 */
	public String ragChat(String question, double score, String source) {
		this.log.debug("ragChat");
		
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

	
	/*
	 * RetrievalAugmentationAdvisor
	 */
	/**
	 * CompressionQueryTransformer를 생성하는 메소드
	 * @return
	 */
	private CompressionQueryTransformer createCompressionQueryTransformer() {
		// 새로운 ChatClient를 생성하는 빌더 생성
		ChatClient.Builder chatClientBuilder = ChatClient.builder(this.chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1));
		
		// 압축 쿼리 변환기 생성
		CompressionQueryTransformer compressionQueryTransformer = CompressionQueryTransformer.builder()
				.chatClientBuilder(chatClientBuilder)
				.build();
		
		return compressionQueryTransformer;
	}
	
	/**
	 * VectorStoreDocumentRetriever를 생성하고 변환하는 메소드.
	 * @param score
	 * @param source
	 * @return
	 */
	private VectorStoreDocumentRetriever createVectorStoreDocumentRetriever(double score, String source) {
		VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
				.vectorStore(this.vectorStore)
				.similarityThreshold(score)
				.topK(3)
				.filterExpression(() -> {
					FilterExpressionBuilder builder = new FilterExpressionBuilder();
					if(StringUtils.hasText(source)) {
						return builder.eq("source", source).build();
					} else {
						return null;
					}
				})
				.build();
		return vectorStoreDocumentRetriever;
	}
	
	/**
	 * RetrievalAugmentationAdvisor를 이용하여 LLM과 대화하는 메소드
	 * @param question
	 * @param score
	 * @param source
	 * @param conversationId
	 * @return
	 */
	public String chatWithCompression(String question, double score, String source, String conversationId) {
		// RetrievalAugmentationAdvisor 생성
		RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
				.queryTransformers(this.createCompressionQueryTransformer())
				.documentRetriever(this.createVectorStoreDocumentRetriever(score, source))
				.build();
		
		// 프롬프트를 LLM으로 전송하고 응답을 받는 코드
		String answer = this.chatClient.prompt()
				.user(question)
				.advisors(MessageChatMemoryAdvisor.builder(this.chatMemory).build(),
						retrievalAugmentationAdvisor)
				.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
				.call()
				.content();
		
		return answer;
	}
	
	
	/*
	 * RewriteQueryTransformer
	 */
	/**
	 * RewriteQueryTransformer를 생성하고 반환하는 메소드.
	 * @return
	 */
	private RewriteQueryTransformer createRewriteQueryTransformer() {
		// 새로운 ChatClient를 생성하는 빌더 생성
		ChatClient.Builder chatClientBuilder = ChatClient.builder(this.chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1));
		
		// 질문 재작성기 생성
		RewriteQueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
				.chatClientBuilder(chatClientBuilder)
				.build();
		
		return rewriteQueryTransformer;
	}
	
	/**
	 * LLM과대화하는 메소드
	 * @param question
	 * @param score
	 * @param source
	 * @return
	 */
	public String chatWithRewriteQuery(String question, double score, String source) {
		// RetrievalAugmentationAdvisor 생성
		RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
				.queryTransformers(this.createRewriteQueryTransformer())
				.documentRetriever(this.createVectorStoreDocumentRetriever(score, source))
				.build();
		
		// 프롬프트를 LLM으로 전송하고 응답을 받는 코드
		String answer = this.chatClient.prompt()
				.user(question)
				.advisors(retrievalAugmentationAdvisor)
				.call()
				.content();
		
		return answer;
	}
	
	
	/*
	 * TranslationQueryTransformer
	 */
	/**
	 * TranslationQueryTransformer를 생성하고 변환하는 메소드
	 * @return
	 */
	private TranslationQueryTransformer createTranslationQueryTransformer() {
		// 새로운 ChatClient를 생성하는 빌더 생성
		ChatClient.Builder chatClientBuilder = ChatClient.builder(this.chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1));
		
		// 질문 번역시 생성
		TranslationQueryTransformer translationQueryTransformer = TranslationQueryTransformer.builder()
				.chatClientBuilder(chatClientBuilder)
				.targetLanguage("korean")
				.build();
		
		return translationQueryTransformer;
	}
	
	/**
	 * LLM과 대화하는 메소드
	 * @param question
	 * @param score
	 * @param source
	 * @return
	 */
	public String chatWithTranslation(String question, double score, String source) {
		// RetrievalAugmentationAdvisor 생성
		RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
				.queryTransformers(this.createTranslationQueryTransformer())
				.documentRetriever(this.createVectorStoreDocumentRetriever(score, source))
				.build();
		
		// 프롬프트를 LLM으로 전공하고 응답을 받는 코드
		String answer = this.chatClient.prompt()
				.user(question)
				.advisors(retrievalAugmentationAdvisor)
				.call()
				.content();
		
		return answer;
	}
	
	
	/*
	 * MultiQueryExpander
	 */
	/**
	 * MultiQueryExpander를 생성하고 반환하는 메소드.
	 * @return
	 */
	private MultiQueryExpander createMultiQueryExpander() {
		// 새로운 ChatClient 빌더 생성
		ChatClient.Builder chatClientBuilder = ChatClient.builder(this.chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1));
		
		// 질문 확장기 생성
		MultiQueryExpander multiQueryExpander = MultiQueryExpander.builder()
				.chatClientBuilder(chatClientBuilder)
				.includeOriginal(true)
				.numberOfQueries(4)
				.build();
		
		return multiQueryExpander;
	}
	
	/**
	 * LLM과 대화하는 메소드
	 * @param question
	 * @param score
	 * @param source
	 * @return
	 */
	public String chatWithMultiQuery(String question, double score, String source) {
		// RetrievalAugmentationAdvisor 생성
		RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
				.queryExpander(this.createMultiQueryExpander())
				.documentRetriever(this.createVectorStoreDocumentRetriever(score, source))
				.build();
		
		// 프롬프트를 LLLM으로 전송하고 응답을 받는 코드
		String answer = this.chatClient.prompt()
				.user(question)
				.advisors(retrievalAugmentationAdvisor)
				.call()
				.content();
		
		return answer;
	}














}
