package signup.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pm.dao.ProjectDao;
import signup.pojo.Survey;

@Controller
public class SurveyController {
	
	private Logger log = Logger.getLogger(SurveyController.class.getName());
	@Autowired
	private ProjectDao projectDao;
	private String tuakiriTokenAttribName;
	private String idpUrlAttribName;

	@RequestMapping(value = "survey", method = RequestMethod.GET)
	public String edit(Model m) throws Exception {
		m.addAttribute("survey", new Survey());
		return "survey";
	}

    @RequestMapping(value="survey", method=RequestMethod.POST)
    public String onSubmit(Model m, @Valid @ModelAttribute("survey") Survey survey,
    	BindingResult result, HttpServletRequest request) throws Exception {
		return "redirect:survey";
    }
        
}
