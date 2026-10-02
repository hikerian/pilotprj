package spring.ai.ollama.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.ollama.service.MCPService;


@RestController
@RequestMapping("/mcp")
public class McpController {
	private final Logger log = LoggerFactory.getLogger(McpController.class);
	
	private final MCPService mcpService;
	
	
	public McpController(MCPService mcpService) {
		this.mcpService = mcpService;
	}
	
	@GetMapping("/chat")
	public String chat() {
		StringBuilder res = new StringBuilder();
		
		String question = "현재 날짜와 시간을 알려줘.";
		String answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		question = "도구를 사용해서 차량 번호가 '533나4567'인 차량이 등록되어 있는지 확인하고 등록되었을 경우 도구를 사용해서 차단봉을 올려주고 등록되지 않았을 경우 차량봉을 내려주세요. 그리고 결과를 알려주세요.";
		answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		question = "지금부터 2시간 뒤에 알람이 울리도록 설정해줘.";
		answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		question = "Spring AI에 대해 50자 이내로 설명해주고, 파일로 저장해줘.";
		answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		question = "파일을 복사하는 자바코드를 작성해주고, CopyFile.java 파일로 저장해줘.";
		answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		question = "java 폴더를 생성하고, CopyFile.java 파일을 이동해줘.";
		answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		question = "전체 파일과 디렉토리를 구분해서 알려줘.";
		answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		question = "오늘 삼성전자 주가와 거래량을 알려줘.";
		answer = this.mcpService.chat(question);
		res.append(answer).append("\n");
		
		
		return res.toString();
	}
	
	@GetMapping("/barrier")
	public String boomBarrier() {
		final boolean requiredWebp = false;
		
		final String filePath = "E:/work/workspaces/pilotprj/.git/pilotprj/spring-ai-ollama-demo/data/img/car3.jpg";
		
		try {
			byte[] image = Files.readAllBytes(Paths.get(filePath));
			
			if(requiredWebp) {
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));
				
				final String webpFilePath = "E:/work/workspaces/pilotprj/.git/pilotprj/spring-ai-ollama-demo/data/img/car2.webp";
				
				File webp = new File(webpFilePath);
				if(webp.exists()) {
					webp.delete();
				}
				ImageIO.write(img, "JPG", webp);
				
				image = Files.readAllBytes(Paths.get(webpFilePath));
				
				return this.mcpService.boomBarrier("image/webp", image);
			} else {
				// ollama의 버그 - https://github.com/ollama/ollama/issues/16532
				return this.mcpService.boomBarrier("image/jpeg", image);
			}
			
		} catch (IOException e) {
			this.log.error("File IO Exception", e);
			throw new RuntimeException(e);
		}
	}
	
	




}
