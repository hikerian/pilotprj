package spring.ai.ollama.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.ollama.service.AdvisorServiceA;
import spring.ai.ollama.service.AdvisorServiceB;


@RestController
@RequestMapping("/advisor")
public class AdvisorController {
	private final Logger log = LoggerFactory.getLogger(AdvisorController.class);
	
	private final AdvisorServiceA advisorServiceA;
	private final AdvisorServiceB advisorServiceB;

	
	public AdvisorController(AdvisorServiceA advisorServiceA, AdvisorServiceB advisorServiceB) {
		this.advisorServiceA = advisorServiceA;
		this.advisorServiceB = advisorServiceB;
	}
	
	@GetMapping("/chain")
	public String advisorChain() {
		this.log.debug("advisorChain");
		
		final String question = "고구려의 역대 왕의 이름을 나열해줘.";
		
		String response = this.advisorServiceA.advisorChain1(question);
		
		return response;
	}
	
	@GetMapping("/maxchar")
	public String maxChar() {
		this.log.debug("maxChar");
		
		final String question = "Spring AI에 대해서 설명해줘";
		
		String response = this.advisorServiceB.advisorContext(question);
		
		return response;
	}

}
