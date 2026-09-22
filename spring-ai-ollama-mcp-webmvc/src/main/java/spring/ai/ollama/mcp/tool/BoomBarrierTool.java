package spring.ai.ollama.mcp.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;


@Component
public class BoomBarrierTool {
	private final Logger log = LoggerFactory.getLogger(BoomBarrierTool.class);
	
	
	public BoomBarrierTool() {
	}
	
	@Tool(description="차단 봉을 올립니다.")
	public void boomBarrierUp() {
		this.log.info("차단 봉을 올립니다.");
	}
	
	@Tool(description="차단 봉을 내립니다.")
	public void boomBarrierDown() {
		this.log.info("차단 봉을 내립니다.");
	}

}
