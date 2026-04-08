package spring.ai.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import spring.ai.demo.service.AiServiceDefaultMethod;

@RestController
@RequestMapping("/ai")
public class AiControllerDefaultMethod {
    @Autowired
    private AiServiceDefaultMethod aiService;

    @PostMapping(value = "/default-method", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> defaultMethod(@RequestParam("question") String question) {
        Flux<String> response = this.aiService.defaultMethod(question);
        return response;
    }

}
