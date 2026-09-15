package spring.ai.ollama.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import spring.ai.ollama.service.ChatMemoryJdbcService;
import spring.ai.ollama.service.ChatMemoryService;
import spring.ai.ollama.service.ChatMemoryVectorDBService;


@RestController
@RequestMapping("/chatmemory")
public class ChatMemoryController {
	private final Logger log = LoggerFactory.getLogger(ChatMemoryController.class);
	
	private final ChatMemoryService chatMemoryService;
	private final ChatMemoryVectorDBService chatMemoryVectorDBService;
	private final ChatMemoryJdbcService chatMemoryJdbcService;
	
	
	public ChatMemoryController(ChatMemoryService chatMemoryService,
			ChatMemoryVectorDBService chatMemoryVectorDBService,
			ChatMemoryJdbcService chatMemoryJdbcService) {
		
		this.chatMemoryService = chatMemoryService;
		this.chatMemoryVectorDBService = chatMemoryVectorDBService;
		this.chatMemoryJdbcService = chatMemoryJdbcService;
	}
	
	@GetMapping("/chat")
	public String inMemoryChat(HttpSession session) {
		this.log.debug("inMemoryChat");
		
		final String[] questions = {
				"지금부터 너의 이름을 자비스라고 부를께",
				"안녕 자비스! 나는 토니야!",
				"아이스 아메리카노를 마시기 위해 스타벅스에 가고 있어",
				"오늘 오후엔 비가 온다고 하는데, 우산을 안들고 왔네",
				"내가 조금 전에 어디 간다고 했지?",
				"내가 안들고 온건 뭘까?",
				"똑똑한데, 너의 이름은 뭐야?",
				"너하고 대화하고 있는 사람의 이름은 뭐지?"
		};
		
		StringBuilder msgs = new StringBuilder();
		
		for(String q : questions) {
			msgs.append(q).append("\n");
			
			String answer = this.chatMemoryService.chat(q, session.getId());
			
			msgs.append(answer).append("\n");
		}

		return msgs.toString();
	}

	@GetMapping("/vector")
	public String vectorDBChat(HttpSession session) {
		this.log.debug("vectorDBChat");
		
		final String[] questions = {
				"지금부터 너의 이름을 자비스라고 부를께",
				"안녕 자비스! 나는 토니야!",
				"아이스 아메리카노를 마시기 위해 스타벅스에 가고 있어",
				"오늘 오후엔 비가 온다고 하는데, 우산을 안들고 왔네",
				"내가 조금 전에 어디 간다고 했지?",
				"내가 안들고 온건 뭘까?",
				"똑똑한데, 너의 이름은 뭐야?",
				"너하고 대화하고 있는 사람의 이름은 뭐지?"
		};
		
		StringBuilder msgs = new StringBuilder();
		
		for(String q : questions) {
			msgs.append(q).append("\n");
			
			String answer = this.chatMemoryVectorDBService.chat(q, session.getId());
			
			msgs.append(answer).append("\n");
		}

		return msgs.toString();
	}
	
	@GetMapping("/jdbc")
	public String jdbcChat(HttpSession session) {
		this.log.debug("vectorDBChat");
		
		final String[] questions = {
				"지금부터 너의 이름을 자비스라고 부를께",
				"안녕 자비스! 나는 토니야!",
				"아이스 아메리카노를 마시기 위해 스타벅스에 가고 있어",
				"오늘 오후엔 비가 온다고 하는데, 우산을 안들고 왔네",
				"내가 조금 전에 어디 간다고 했지?",
				"내가 안들고 온건 뭘까?",
				"똑똑한데, 너의 이름은 뭐야?",
				"너하고 대화하고 있는 사람의 이름은 뭐지?"
		};
		
		StringBuilder msgs = new StringBuilder();
		
		for(String q : questions) {
			msgs.append(q).append("\n");
			
			String answer = this.chatMemoryJdbcService.chat(q, session.getId());
			
			msgs.append(answer).append("\n");
		}

		return msgs.toString();
	}
	
	
	

}
