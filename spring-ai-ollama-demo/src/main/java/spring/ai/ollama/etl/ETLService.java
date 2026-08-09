package spring.ai.ollama.etl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ETLService {
	private final Logger log = LoggerFactory.getLogger(ETLService.class);
	
	private final ChatModel chatModel;
	private final VectorStore vectorStore;
	
	
	public ETLService(ChatModel chatModel, VectorStore vectorStore) {
		this.chatModel = chatModel;
		this.vectorStore = vectorStore;
	}
	
	// 303 page
	/**
	 * 업로드된 파일을 가지고 ETL 과정을 처리하는 메소드.
	 * 이 기능을 Ollama + MariaDB로 변환...
	 * @param title
	 * @param author
	 * @param attach
	 * @return
	 * @throws IOException
	 */
	public String etlFromFile(String title, String author, MultipartFile attach) throws IOException {
		// E: 추출하기
		List<Document> documents = this.extractFromFile(attach);
		if(documents == null) {
			return ".txt, .pdf, .doc, .docx 파일 중에 하나를 올려주세요.";
		}
		this.log.info("추출된 Document 수: {} 개", documents.size());
		
		// T: 메타데이터에 공통 정보 추가하기
		for(Document doc : documents) {
			Map<String, Object> metadata = doc.getMetadata();
			metadata.putAll(Map.of(
					"title", title,
					"author", author,
					"source", attach.getOriginalFilename()
					));
		}
		
		// T: 작은 사이즈로 분할하기
		documents = this.transform(documents);
		this.log.info("변환된 Documents 수: {} 개", documents.size());
		
		// L: 적재하기
		this.vectorStore.add(documents);
		
		
		return "올린 문서를 추출-변환-적재 완료했습니다.";
	}
	
	/**
	 * 업로드된 파일로 부터 텍스트를 추출하는 메소드
	 * @param attach
	 * @return
	 * @throws IOException
	 */
	private List<Document> extractFromFile(MultipartFile attach) throws IOException {
		// 바이트 배열을 Resource로 생성
		Resource resource = new ByteArrayResource(attach.getBytes());
		
		List<Document> documents = null;
		if(attach.getContentType().equals("text/plain")) {
			// Text(.txt) 파일일 경우
			DocumentReader reader = new TextReader(resource);
			documents = reader.read();
		} else if(attach.getContentType().equals("application/pdf")) {
			// PDF(.pdf) 파일일 경우
			DocumentReader reader = new PagePdfDocumentReader(resource);
			documents = reader.read();
		} else if(attach.getContentType().contains("wordprocessingml")) {
			// Word(.doc, docx) 파일일 경우
			DocumentReader reader = new TikaDocumentReader(resource);
			documents = reader.read();
		}
		
		return documents;
	}
	
	/**
	 * 작은 크기로 분할하고 키워드 메타데이터를 추가하는 메소드
	 * @param documents
	 * @return
	 */
	private List<Document> transform(List<Document> documents) {
		List<Document> transformedDocuments = null;
		
		// 작게 분할하기
		TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder().build();
		transformedDocuments = tokenTextSplitter.apply(documents);
		
		// 메타데이터에 키워드 추가하기(이 부분은 LLM을 사용하므로 비용과 시간이 증가합니다.)
		KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatModel, 5);
		transformedDocuments = keywordMetadataEnricher.apply(transformedDocuments);
		
		return transformedDocuments;
	}
	
	



}
