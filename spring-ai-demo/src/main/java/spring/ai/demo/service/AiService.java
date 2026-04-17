package spring.ai.demo.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.AudioParameters;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AiService {
	private final Logger log = LoggerFactory.getLogger(AiService.class);

	@Autowired
	private ChatModel chatModel;

	public String generateText(String question) {
		// 시스템 메시지 생성
		SystemMessage systemMessage = SystemMessage.builder().text("사용자 질문에 대해 한국어로 답변을 해야 합니다.").build();

		// 사용자 메시지 생성
		UserMessage userMessage = UserMessage.builder().text(question).build();

		// 대화 옵션 설정
		ChatOptions chatOptions = ChatOptions.builder()
				.model("gpt-4o-mini")
				.temperature(0.3D)
				.maxTokens(1000)
				.build();

		// 프롬프트 생성
		Prompt prompt = Prompt.builder()
				.messages(systemMessage, userMessage)
				.chatOptions(chatOptions)
				.build();

		// LLM에게 요청하고 응답받기
		ChatResponse chatResponse = this.chatModel.call(prompt);

		AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
		String answer = assistantMessage.getText();

		return answer;
	}

	public Flux<String> generateStreamText(String question) {
		// 시스템 메시지 생성
		SystemMessage systemMessage = SystemMessage.builder().text("사용자 질문에 대해 한국어로 답변을 해야 합니다.").build();

		// 사용자 메시지 생성
		UserMessage userMessage = UserMessage.builder().text(question).build();

		// 대화 옵션 설정
		ChatOptions chatOptions = ChatOptions.builder()
				.model("gpt-4o-mini")
				.temperature(0.3D)
				.maxTokens(1000)
				.build();

		// 프롬프트 생성
		Prompt prompt = Prompt.builder()
				.messages(systemMessage, userMessage)
				.chatOptions(chatOptions)
				.build();

		// LLM에게 요청하고 응답받기
		Flux<ChatResponse> fluxResponse = chatModel.stream(prompt);
		Flux<String> fluxString = fluxResponse.map(chatResponse -> {
			AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
			String chunk = assistantMessage.getText();
			if (chunk == null)
				chunk = "";
			return chunk;
		});

		return fluxString;
	}

	/* Chapter 05 */
	private ChatClient chatClient;
	private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
	private OpenAiAudioSpeechModel openAiAudioSpeechModel;

	public AiService(ChatClient.Builder chatClientBuilder,
			OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel,
			OpenAiAudioSpeechModel openAiAudioSpeechModel) {
		this.chatClient = chatClientBuilder.build();
		this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
		this.openAiAudioSpeechModel = openAiAudioSpeechModel;
	}

	public String stt(String fileName, byte[] bytes) {
		Resource audioResource = new ByteArrayResource(bytes) {
			@Override
			public String getFilename() {
				return fileName;
			}
		};

		// 모델 옵션 설정
		OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
				.model("whisper-1")
				.language("ko")
				.build();

		// 프롬프트 생성
		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);

		// 모델을 호출하고 응답받기
		AudioTranscriptionResponse response = this.openAiAudioTranscriptionModel.call(prompt);
		String text = response.getResult().getOutput();

		return text;
	}

	public byte[] tts(String text) {
		// 모델 옵션 설정
		OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
				.model("gpt-4o-mini-tts")
				.voice(SpeechRequest.Voice.ALLOY)
				.responseFormat(SpeechRequest.AudioResponseFormat.MP3)
				.speed(1.0D)
				.build();

		// 프롬프트 생성
		TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);

		// 모델을 호출하고 응답받기
		TextToSpeechResponse response = this.openAiAudioSpeechModel.call(prompt);
		byte[] bytes = response.getResult().getOutput();

		return bytes;
	}

	// 텍스트로 같이 출력되는 음성 대화
	public Map<String, String> chatText(String question) {
		// LLM으로 요청하고, 텍스트 응답 얻기
		String textAnswer = chatClient.prompt()
				.system("50자 이내로 한국어로 답변해주세요.")
				.user(question)
				.call()
				.content();

		// TTS 모델로 요청하고 응답으로 받은 음성 데이터를 base64 문자열로 변환
		byte[] audio = this.tts(textAnswer);
		String base64Audio = Base64.getEncoder().encodeToString(audio);

		// 텍스트 답변과 음성 답변을 Map에 저장
		Map<String, String> response = new HashMap<>();
		response.put("text", textAnswer);
		response.put("audio", base64Audio);

		return response;
	}

	public Flux<byte[]> ttsFlux(String text) {
		// 모델 옵션 설정
		OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
				.model("gpt-4o-mini-tts")
				.voice(SpeechRequest.Voice.ALLOY)
				.responseFormat(AudioResponseFormat.MP3)
				.speed(1.0D)
				.build();

		// 프롬프트 생성
		TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);

		// 모델로 요청하고 응답받기
		Flux<TextToSpeechResponse> response = this.openAiAudioSpeechModel.stream(prompt);
		Flux<byte[]> flux = response.map(speechResponse -> speechResponse.getResult().getOutput());

		return flux;
	}

	// 순수 음성 대화 구현 (방법1)
	public Flux<byte[]> chatVoiceSttLlmTts(byte[] audioBytes) {
		// STT를 이용해서 음성 질문을 텍스트 질문으로 변환
		String textQuestion = this.stt("speech.mp3", audioBytes);

		// 텍스트 질문으로 LLM에 요청하고, 텍스트 응답 얻기
		String textAnswer = chatClient.prompt()
				.system("50자 이내로 답변주세요.")
				.user(textQuestion)
				.call()
				.content();

		// TTS를 이용해서 비동기 음성 데이터 얻기
		Flux<byte[]> flux = this.ttsFlux(textAnswer);
		return flux;
	}

	// 순수 음성 대화 구현 (방법2)
	public byte[] chatVoiceOneModel(byte[] audioBytes, String mimeType) throws Exception {
		// 음성데이터를 Resource로 생성
		Resource resource = new ByteArrayResource(audioBytes);

		// 사용자 메시지 생성
		UserMessage userMessage = UserMessage.builder()
				// 빈문자열이라도 제공해야 함
				.text("제공되는 음성에 맞는 자연스러운 대화로 이어주세요.")
				.media(new Media(MimeType.valueOf(mimeType), resource))
				.build();

		// 모델 옵션 설정
		ChatOptions chatOptions = OpenAiChatOptions.builder()
				.model(OpenAiApi.ChatModel.GPT_4_O_MINI_AUDIO_PREVIEW)
				.outputModalities(List.of("text", "audio"))
				.outputAudio(new AudioParameters(
						ChatCompletionRequest.AudioParameters.Voice.ALLOY,
						ChatCompletionRequest.AudioParameters.AudioResponseFormat.MP3))
				.build();

		// gpt-4o-mini-audio 모델은 스트림을 지원하지 않기 때문에 동기 방식 사용
		// 모델로 요청하고 응답받기
		ChatResponse response = this.chatClient.prompt()
				.system("50자 이내로 답변해주세요.")
				.messages(userMessage)
				.options(chatOptions)
				.call()
				.chatResponse();

		// AI 메시지 얻기
		AssistantMessage assistantMessage = response.getResult().getOutput();

		// 텍스트 답변 얻기
		String textAnswer = assistantMessage.getText();

		this.log.debug("텍스트 응답: {}", textAnswer);

		// 오디오 답변 얻기
		byte[] audioAnswer = assistantMessage.getMedia().get(0).getDataAsByteArray();

		return audioAnswer;
	}

	/* Chapter6 */
	@Autowired
	private ImageModel imageModel;

	public Flux<String> imageAnalysis(String question, String contentType, byte[] bytes) {
		// 시스템 메시지 생성
		SystemMessage systemMessage = SystemMessage.builder()
				.text("""
								당신은 이미지 분석 전문가입니다.
								사용자 질문에 맞게 이미지를 분석하고 한국어로 답변하세요.
						""")
				.build();

		// 미디어 생성
		Media media = Media.builder()
				.mimeType(MimeType.valueOf(contentType))
				.data(new ByteArrayResource(bytes))
				.build();

		// 사용자 메시지 생성
		UserMessage userMessage = UserMessage.builder()
				.text(question)
				.media(media)
				.build();

		// 프롬프트 생성
		Prompt prompt = Prompt.builder()
				.messages(systemMessage, userMessage)
				.build();

		// LLM에 요청하고 응답받기
		Flux<String> flux = this.chatClient.prompt(prompt)
				.stream()
				.content();

		return flux;
	}

	private String koToEn(String text) {
		String question = """
				당신은 번역사입니다. 아래 한글 문장을 영어 문장으로 번역해 주세요.
				%s
				""".formatted(text);

		// UserMessage 생성
		UserMessage userMessage = UserMessage.builder()
				.text(question)
				.build();

		// Prompt 생성
		Prompt prompt = Prompt.builder()
				.messages(userMessage)
				.build();

		// LLM을 호출하고 텍스트 답변 얻기
		String englishDescription = this.chatClient.prompt(prompt).call().content();

		return englishDescription;
	}

	public String generateImage(String description) {
		// 한글 질문을 영어 질문으로 번역
		String englishDescription = this.koToEn(description);

		// 이미지 설명을 포함하는 ImageMessage 생성
		ImageMessage imageMessage = new ImageMessage(englishDescription);

		// gpt-image-1 옵션 설정
		OpenAiImageOptions imageOptions = OpenAiImageOptions.builder()
				.model("gpt-image-1")
				.quality("low")
				.width(1536)
				.height(1024)
				.N(1)
				.build();

		// 프롬프트 생성
		List<ImageMessage> imageMessageList = List.of(imageMessage);
		ImagePrompt imagePrompt = new ImagePrompt(imageMessageList, imageOptions);

		// 모델 호출 및 응답받기
		ImageResponse imageResponse = this.imageModel.call(imagePrompt);

		// base64로 인코딩된 이미지 문자열 얻기
		String b64Json = imageResponse.getResult().getOutput().getB64Json();

		return b64Json;
	}

	public String editImage(String description, byte[] originalImage, byte[] maskImage) {
		// 한글 질문을 영어 질문으로 번역
		String englishDescription = this.koToEn(description);

		// 원본 이미지를 ByteArrayResource로 생성
		ByteArrayResource originalRes = new ByteArrayResource(originalImage) {
			@Override
			public String getFilename() {
				return "image.png";
			}
		};

		// 마스크 이미지를 ByteArrayResource로 생성
		ByteArrayResource maskRes = new ByteArrayResource(maskImage) {
			@Override
			public String getFilename() {
				return "mask.png";
			}
		};

		// 이미지 모델 옵션 설정
		MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
		form.add("model", "gpt-image-1");
		form.add("image", originalRes);
		form.add("mask", maskRes);
		form.add("prompt", englishDescription);
		form.add("n", 1);
		form.add("size", "1536x1024");
		form.add("quality", "low");

		// WebClient 생성
		WebClient webClient = WebClient.builder()
				// 이미지 편집을 위한 요청 URL
				.baseUrl("https://api.openai.com/v1/images/edits")
				// 인증헤더 설정
				.defaultHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
				// base64로 인코딩된 이미지 데이터를 처리하기 위해 메모리 사이즈 늘림(약 15M)
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(10 * 1536 * 1024)).build())
				.build();

		// 비동기 단일값(OpenAIImageEditResponse) 스트림 Mono 얻기
		Mono<OpenAIImageEditResponse> mono = webClient.post()
				// multipart/form-data 형식으로 전송
				.contentType(MediaType.MULTIPART_FORM_DATA)
				// 요청 본문에 form 데이터 삽입
				.body(BodyInserters.fromMultipartData(form))
				.retrieve()
				.bodyToMono(OpenAIImageEditResponse.class);

		OpenAIImageEditResponse response = mono.block();

		String b64Json = response.data().get(0).b64_json();
		return b64Json;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record OpenAIImageEditResponse(List<Image> data) {
		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Image(
				String url,
				String b64_json) {

		}
	}

}
