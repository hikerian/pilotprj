package spring.ai.ollama.controller;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.ollama.etl.ETLService;
import spring.ai.ollama.rag.RAGService;


@RestController
@RequestMapping("/rag")
public class RagController {
	private final Logger log = LoggerFactory.getLogger(RagController.class);
	
	private final ETLService etlService;
	private final RAGService ragService;
	
	
	public RagController(ETLService etlService, RAGService ragService) {
		this.etlService = etlService;
		this.ragService = ragService;
	}
	
	@GetMapping("/txt-pdf-docx-etl")
	public String txtPdfDocxEtl() throws Exception {
		String title = "대한민국헌법";
		String author = "법제처";
//		File file = new File("E:\\work\\workspaces\\pilotprj\\.git\\pilotprj\\spring-ai-ollama-demo\\data\\대한민국헌법.docx");
		File file = new File("C:\\work\\dev\\workspaces\\pilotprj\\git\\spring-ai-ollama-demo\\data\\대한민국헌법.docx");
		
		this.log.debug("File {}", file.getName());
		
		String result = this.etlService.etlFromFile(title, author, file);
		
		this.log.debug("result {}", result);
		
		return result;

	}
	
	@GetMapping("/rag-chat")
	public String ragEtl() {
		String answer = this.ragService.ragChat("대통령의 임기는 몇 년이지?", 0.0, "대한민국헌법.docx");
		
		return answer;
	}

}
