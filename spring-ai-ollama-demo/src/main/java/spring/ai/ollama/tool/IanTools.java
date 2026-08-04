package spring.ai.ollama.tool;

import java.time.LocalDate;

import org.springframework.ai.tool.annotation.Tool;


public class IanTools {
	
	@Tool(description="광장동에 사는 조동일의 아들 조이안이 현재 다니고 있는 학교의 이름을 가져옵니다.")
	String getSchool() {
		return "양진초등학교";
	}
	
	@Tool(description="광장동에 사는 조동일의 아들 조이안의 생년월일을 반홥합니다.")
	LocalDate getIanBirthday() {
		return LocalDate.of(2015, 2, 17);
	}
	

}
