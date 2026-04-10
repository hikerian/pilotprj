package spring.ai.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.demo.service.AiServiceListOutputConverter;

@RestController
@RequestMapping("/ai")
public class AiControllerListOutputConverter {
    @Autowired
    private AiServiceListOutputConverter aiService;

    @PostMapping(value = "/list-output-converter", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listOutputConverter(@RequestParam("city") String city) {
        List<String> hotelList = this.aiService.listOutputConverterLowLevel(city);
        // List<String> hotelList = this.aiService.listOutputConverterHighLevel(city);
        return hotelList;
    }

}
