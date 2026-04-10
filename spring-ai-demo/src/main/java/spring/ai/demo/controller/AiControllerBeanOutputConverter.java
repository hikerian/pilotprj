package spring.ai.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.demo.dto.Hotel;
import spring.ai.demo.service.AiServiceBeanOutputConverter;

@RestController
@RequestMapping("/ai")
public class AiControllerBeanOutputConverter {
    @Autowired
    private AiServiceBeanOutputConverter aiService;

    @PostMapping(value = "/bean-output-converter", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Hotel beanOutputConverter(@RequestParam("city") String city) {
        Hotel hotel = this.aiService.beanOutputConverterLowLevel(city);
        // Hotel hotel = this.aiService.beanOutputConverterHighLevel(city);

        return hotel;
    }

}
