package signup.validation;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import signup.pojo.ProjectRequest;

public class ProjectRequestValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ProjectRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object projectRequest, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "choice", "project.choice.required");
		ProjectRequest pr = (ProjectRequest) projectRequest;
		if (pr.getChoice() != null) {
		    if (pr.getChoice().equals("JOIN_PROJECT")) {
			    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectCode", "project.code.required");
   		    } else if (pr.getChoice().equals("CREATE_PROJECT")) {
			    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", "project.title.required");
		  	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescription", "project.description.required");
			    if (pr.getAskForSuperviser()) {
				    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserName", "project.superviser.name.required");
				    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserPhone", "project.superviser.phone.required");
				    EmailValidator ev = EmailValidator.getInstance(false);
				    if (!ev.isValid(pr.getSuperviserEmail())) {
				    	errors.rejectValue("superviserEmail","project.superviser.email.invalid");
				    }
			    }
   		    } else {
   		    	errors.rejectValue("choice", "project.choice.invalid");
   		    }
		}
	}

}
