package org.siraya.rent.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class TemplateController {
	
	@RequestMapping("/index")
	public String index(){
		return "index";
	}
}
