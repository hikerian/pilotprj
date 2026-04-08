package spring.ai.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import spring.ai.demo.service.AiServicePromptTemplate;

@RestController
@RequestMapping("/ai")
public class AiControllerPromptTemplate {
    @Autowired
    private AiServicePromptTemplate aiService;

    @PostMapping(value = "/prompt-template", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> promptTemplate(
            @RequestParam("statement") String statement,
            @RequestParam("language") String language) {
        Flux<String> response = this.aiService.promptTemplate1(statement, language);
        // Flux<String> response = this.aiService.promptTemplate2(statement, language);
        // Flux<String> response = this.aiService.promptTemplate3(statement, language);
        // Flux<String> response = this.aiService.promptTemplate4(statement, language);

        return response;
    }

}
