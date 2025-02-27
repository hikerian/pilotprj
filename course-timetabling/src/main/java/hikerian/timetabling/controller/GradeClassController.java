package hikerian.timetabling.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hikerian.timetabling.dto.GradeClass;
import hikerian.timetabling.mapper.GradeClassMapper;


@Controller
@RequestMapping("/grade-class")
public class GradeClassController {
	private final Logger log = LoggerFactory.getLogger(GradeClassController.class);
	
	private final GradeClassMapper gradeClassMapper;
	
	
	public GradeClassController(GradeClassMapper gradeClassMapper) {
		this.gradeClassMapper = gradeClassMapper;
	}
	
	@GetMapping("/all")
	public String all(Model model) {
		this.log.debug("all");
		
		List<GradeClass> gradeClassList = this.gradeClassMapper.getAllGradeClass();
		
		model.addAttribute("gradeClassList", gradeClassList);
		
		return "grade-class";
	}

}
