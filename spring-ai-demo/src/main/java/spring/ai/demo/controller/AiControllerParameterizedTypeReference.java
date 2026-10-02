package spring.ai.demo.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.demo.dto.Hotel;
import spring.ai.demo.service.AiServiceParameterizedTypeReference;


@RestController
@RequestMapping("/ai")
public class AiControllerParameterizedTypeReference {
    private final AiServiceParameterizedTypeReference aiService;

    
    public AiControllerParameterizedTypeReference(AiServiceParameterizedTypeReference aiService) {
        this.aiService = aiService;
    }

    @PostMapping(value = "/generic-bean-output-converter", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Hotel> genericBeanOutputConverter(@RequestParam("cities") String cities) {
        List<Hotel> hotelList = this.aiService.genericBeanOutputConverterLowLevel(cities);
        // List<Hotel> hotelList =
        // this.aiService.generixBeanOutputConverterHighLevel(cities);

        return hotelList;
    }

}
