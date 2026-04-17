package spring.ai.demo.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Flux;
import spring.ai.demo.service.AiService;
import spring.ai.demo.service.AiServiceByChatClient;

@RestController
@RequestMapping("/ai")
public class AiController {
	// @Autowired
	// private AiService aiService;

	@Autowired
	private AiServiceByChatClient aiService;

	// ##### 요청 매핑 메소드 #####
	@PostMapping(value = "/chat", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String chat(@RequestParam("question") String question) {
		return "아직 모델과 연결되지 않았습니다.";
	}

	@PostMapping(value = "/chat-model", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String chatModel(@RequestParam("question") String question) {
		String answerText = this.aiService.generateText(question);
		return answerText;
	}

	@PostMapping(value = "/chat-model-stream", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<String> chatModelStream(@RequestParam("question") String question) {
		Flux<String> answerStreamText = this.aiService.generateStreamText(question);
		return answerStreamText;
	}

	/* Chapter 05 */
	@Autowired
	private AiService aiService2;

	@PostMapping(value = "/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String stt(@RequestParam("speech") MultipartFile speech) throws IOException {
		String originalFileName = speech.getOriginalFilename();
		byte[] bytes = speech.getBytes();
		String text = this.aiService2.stt(originalFileName, bytes);

		return text;
	}

	@PostMapping(value = "/tts", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public byte[] tts(@RequestParam("text") String text) {
		byte[] bytes = this.aiService2.tts(text);
		return bytes;
	}

	@PostMapping(value = "/chat-text", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> chatText(@RequestParam("question") String question) {
		Map<String, String> response = this.aiService2.chatText(question);
		return response;
	}

	@PostMapping(value = "/chat-voice-stt-llm-tts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void chatVoiceSttLlmTts(@RequestParam("question") MultipartFile question,
			HttpServletResponse response) throws Exception {
		// 비동기 음성 데이터를 Flux<byte[]>을 얻기
		Flux<byte[]> flux = this.aiService2.chatVoiceSttLlmTts(question.getBytes());

		// 음성 데이터를 응답 본문으로 스트림 출력
		OutputStream outputStream = response.getOutputStream();
		for (byte[] chunk : flux.toIterable()) {
			outputStream.write(chunk);
			outputStream.flush();
		}
	}

	@PostMapping(value = "/chat-voice-one-model", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public byte[] chatVoiceOneModel(
			@RequestParam("question") MultipartFile question,
			HttpServletResponse response) throws Exception {
		byte[] bytes = this.aiService2.chatVoiceOneModel(question.getBytes(), question.getContentType());
		return bytes;
	}

	/* Chapter6 */
	@PostMapping(value = "/image-analysis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<String> imageAnalysis(
			@RequestParam("question") String question,
			@RequestParam("attach") MultipartFile attach) throws IOException {
		// 이미지가 업로드 되지 않았을 경우
		if (attach == null || !attach.getContentType().contains("image/")) {
			Flux<String> response = Flux.just("이미지를 올려주세요");
			return response;
		}

		Flux<String> flux = this.aiService2.imageAnalysis(question, attach.getContentType(), attach.getBytes());

		return flux;
	}

	@PostMapping(value = "/image-generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String imageGenerate(@RequestParam("description") String description) {
		try {
			String b64Json = this.aiService2.generateImage(description);
			return b64Json;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

	@PostMapping(value = "/image-edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String imageEdit(@RequestParam("description") String description,
			@RequestParam("originalImage") MultipartFile originalImage,
			@RequestParam("maskImage") MultipartFile maskImage) {
		try {
			String b64Json = this.aiService2.editImage(description, originalImage.getBytes(), maskImage.getBytes());
			return b64Json;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

}
