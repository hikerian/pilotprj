package spring.ai.ollama.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.ollama.service.AdvisorServiceA;


@RestController
@RequestMapping("/advisor")
public class AdvisorController {
	private final Logger log = LoggerFactory.getLogger(AdvisorController.class);
	
	private final AdvisorServiceA advisorServiceA;

	
	public AdvisorController(AdvisorServiceA advisorServiceA) {
		this.advisorServiceA = advisorServiceA;
	}
	
	@GetMapping("/chain")
	public String advisorChain() {
		final String question = "고구려의 역대 왕의 이름을 나열해줘.";
		
		String response = this.advisorServiceA.advisorChain1(question);
		
		return response;
	}

}
