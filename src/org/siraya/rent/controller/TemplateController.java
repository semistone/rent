package org.siraya.rent.controller;

import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TemplateController {
	@Autowired
	private IApplicationConfig applicationConfig;

	@RequestMapping(value = "index", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ModelAndView index() {
		String viewPage = "index";
		java.util.Map<String, String> model = new java.util.HashMap<String, String>();
		model.put("cdn", (String) applicationConfig.get("web").get("cdn"));
		return new ModelAndView(viewPage, model);
	}
}
