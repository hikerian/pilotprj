package spring.ai.ollama.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import spring.ai.ollama.mcp.tool.BoomBarrierTool;
import spring.ai.ollama.mcp.tool.CarCheckTool;
import spring.ai.ollama.mcp.tool.DateTimeTool;
import spring.ai.ollama.mcp.tool.FileSystemTool;

@SpringBootApplication
public class SpringAiOllamaMcpWebmvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiOllamaMcpWebmvcApplication.class, args);
	}
	
	@Bean
	ToolCallbackProvider getToolCallbackProvider(BoomBarrierTool boomBarrierTool,
			CarCheckTool carCheckTool,
			DateTimeTool dateTimeTool,
			FileSystemTool fileSystemTool) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(
						boomBarrierTool, carCheckTool, dateTimeTool, fileSystemTool
				).build();
	}

}
