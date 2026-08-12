package spring.ai.ollama.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.ai.ollama.dto.Hotel;
import spring.ai.ollama.dto.ReviewClassification;
import spring.ai.ollama.service.OutputConverterService;


@RestController
@RequestMapping("/conv")
public class ConverterController {
	private final Logger log = LoggerFactory.getLogger(ConverterController.class);
	
	
	private final OutputConverterService outputConverterService;
	
	
	public ConverterController(OutputConverterService listOutputConverterService) {
		this.outputConverterService = listOutputConverterService;
	}
	
	@GetMapping("/list-l")
	public List<String> listOutputConverterLowLevel() {
		this.log.debug("listOutputConverterLowLevel");
		
		String city = "서울";
		
		List<String> hotelList = this.outputConverterService.listOutputConverterLowLevel(city);
		
		return hotelList;
	}
	
	@GetMapping("/list-h")
	public List<String> listOutputConverterHighLevel() {
		this.log.debug("listOutputConverterHighLevel");
		
		String city = "서울";
		
		List<String> hotelList = this.outputConverterService.listOutputConverterHighLevel(city);
		
		return hotelList;
	}
	
	@GetMapping("/bean-l")
	public Hotel beanOutputConverterLowLevel() {
		this.log.debug("beanOutputConverterLowLevel");
		
		String city = "경주";
		
		Hotel hotel = this.outputConverterService.beanOutputConverterLowLevel(city);
		
		return hotel;
	}
	
	@GetMapping("/bean-h")
	public Hotel beanOutputConverterHighLevel() {
		this.log.debug("beanOutputConverterHighLevel");
		
		String city = "경주";
		
		Hotel hotel = this.outputConverterService.beanOutputConverterHighLevel(city);
		
		return hotel;
	}
	
	@GetMapping("/map-l")
	public Map<String, Object> mapOutputConverterLowLevel() {
		this.log.debug("mapOutputConverterLowLevel");
		
		String hotel = "신라호텔";
		
		Map<String, Object> hotelInfo = this.outputConverterService.mapOutputConverterLowLevel(hotel);
		
		return hotelInfo;
	}
	
	@GetMapping("/map-h")
	public Map<String, Object> mapOutputConverterHighLevel() {
		this.log.debug("mapOutputConverterHighLevel");
		
		String hotel = "신라호텔";
		
		Map<String, Object> hotelInfo = this.outputConverterService.mapOutputConverterHighLevel(hotel);
		
		return hotelInfo;
	}
	
	@GetMapping("/sys-m")
	public ReviewClassification beanOutputConverter() {
//		String review = "영화 \"Her\"는 AI와 사람의 관계가 어떻게 발전할지 보여주네요. 소재가 적절했습니다.";
		String review = "영화 \"Her\"는 AI와 사람의 관계가 어떻게 발전할지 보여주네요. 감독이 상상력을 너무 발휘해서 자칫 사람이 AI에 의해 잠식될 수도 있다는 우려를 낳게 합니다.";
		
		ReviewClassification reviewClassification = this.outputConverterService.classifyReview(review);
		
		return reviewClassification;
	}
	

}
