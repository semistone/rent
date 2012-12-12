package org.siraya.rent.controller;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
@Controller
public class TemplateController {
	
    @RequestMapping(value = "index", method= RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
	public String index(){
		return "index";
	}
}
