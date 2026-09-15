package spring.ai.ollama.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;


public class MaxCharLengthAdvisor implements CallAdvisor {
	private final Logger log = LoggerFactory.getLogger(MaxCharLengthAdvisor.class);
	
	public static final String MAX_CHAR_LENGTH = "maxCharLength";
	private final int maxCharLength = 300; // 기본 최대 문자수
	private final int order;
	
	
	public MaxCharLengthAdvisor(int order) {
		this.order = order;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		this.log.debug("adviseCall");
		
		// 전처리 작업: 사용자 메시지가 강화된 ChatClientRequest 얻기
		ChatClientRequest mutatedRequest = this.augmentPrompt(chatClientRequest);
		
		// 다음 Advisor 호출 또는 LLM으로 요청
		ChatClientResponse response = callAdvisorChain.nextCall(mutatedRequest);
		
		return response;
	}
	
	// 사용자 메시지 강화
	private ChatClientRequest augmentPrompt(ChatClientRequest request) {
		// 추가할 사용자 텍스트 얻기
		String userText = this.maxCharLength + "자 이내로 답변해 주세요.";
		Integer maxCharLength = (Integer)request.context().get(MaxCharLengthAdvisor.MAX_CHAR_LENGTH);
		if(maxCharLength != null) {
			userText = maxCharLength + "자 이내로 답변해 주세요.";
		}
		String finalUserText = userText;
		
		// 사용자 메시지를 강화한 Prompt 얻기
		Prompt originalPrompt = request.prompt();
		Prompt augmentedPrompt = originalPrompt.augmentUserMessage(
				userMessage -> UserMessage.builder()
				.text(userMessage.getText() + " " + finalUserText)
				.build()
				);
		
		// 수정된 ChatClientRequest 얻기
		ChatClientRequest mutatedRequest = request.mutate()
				.prompt(augmentedPrompt)
				.build();
		
		return mutatedRequest;
	}




}
