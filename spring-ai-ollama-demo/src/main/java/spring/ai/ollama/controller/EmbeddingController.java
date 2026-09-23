package spring.ai.ollama.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.ollama.service.EmbeddingService;


@RestController
@RequestMapping("/embedding")
public class EmbeddingController {
	private final Logger log = LoggerFactory.getLogger(EmbeddingController.class);
	
	private final EmbeddingService embeddingService;
	
	
	public EmbeddingController(EmbeddingService embeddingService) {
		this.embeddingService = embeddingService;
	}
	
	@GetMapping("/text")
	public String textEmbedding() {
		this.log.debug("textEmbedding");
		
		String question = """
LLM은 인간처럼 사실관계를 논리적으로 사고하거나 ‘지식’을 이해해서 답변하는 것이 아닙니다.
대신 방대한 텍스트데이터를 학습하여, “주어진 단어 다음에 올 가장 확률이 높은 단어”를 통계적으로 예측하여 문장을 생성합니다.
마치 스마트폰 키보드의 ’자동완성’ 기능이 고도로 발전한 형태라고 볼 수 있습니다.
				""";
		
		this.embeddingService.textEmbedding(question);
		
		return "서버 터미널(콘솔)을 확인하세요.";
	}
	
	@GetMapping("/add-doc")
	public String addDocument() {
		this.embeddingService.addDocument();
		
		return "벡터 저장소에 Document가 저장되었습니다.";
	}
	
	@GetMapping("/del-doc")
	public String delDocument() {
		this.embeddingService.deleteDocument();
		
		return "Document가 삭제되었습니다.";
	}
	
	@GetMapping("/search1")
	public String searchDocument1() {
		String question = "대통령은 얼마동안 근무해?";
		
		List<Document> documents = this.embeddingService.searchDocument1(question);
		
		StringBuilder res = new StringBuilder();
		for(Document doc : documents) {
			res.append("유사도 점수: %f".formatted(doc.getScore())).append("<br/>");
			res.append("%s(%s)".formatted(doc.getText(), doc.getMetadata().get("year")));
			res.append("<br/>");
		}
		
		return res.toString();
	}
	
	@GetMapping("/search2")
	public String searchDocument2() {
		String question = "대통령은 얼마동안 근무해?";
		
		List<Document> documents = this.embeddingService.searchDocument2(question);
		
		StringBuilder res = new StringBuilder();
		for(Document doc : documents) {
			res.append("유사도 점수: %f".formatted(doc.getScore())).append("<br/>");
			res.append("%s(%s)".formatted(doc.getText(), doc.getMetadata().get("year")));
			res.append("<br/>");
		}
		
		return res.toString();
	}
	
	

}
