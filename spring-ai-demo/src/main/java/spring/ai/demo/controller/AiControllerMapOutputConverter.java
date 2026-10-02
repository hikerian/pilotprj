package spring.ai.demo.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.ai.demo.service.AiServiceMapOutputConverter;


@Controller
@RequestMapping("/ai")
public class AiControllerMapOutputConverter {
    private final AiServiceMapOutputConverter aiService;

    
    public AiControllerMapOutputConverter(AiServiceMapOutputConverter aiService) {
        this.aiService = aiService;
    }

    @PostMapping(value = "/map-output-converter", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> mapOutputConverter(@RequestParam("hotel") String hotel) {
        Map<String, Object> hotelInfo = this.aiService.mapOutputConverterLowLevel(hotel);
        // Map<String, Object> hotelInfo =
        // this.aiService.mapOutputConverterHighLevel(hotel);

        return hotelInfo;

    }

}
