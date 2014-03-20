package signup.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SignupController {
	
	private Logger log = Logger.getLogger(AccountRequestController.class.getName()); 

	@RequestMapping(value = "signup", method = RequestMethod.GET)
	public String edit(ModelMap modelMap) throws Exception {
		return "info";
	}

}
