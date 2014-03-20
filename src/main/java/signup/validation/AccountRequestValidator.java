package signup.validation;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import signup.pojo.AccountRequest;

public class AccountRequestValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return AccountRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object accountRequest, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institution", "account.affiliation.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "account.phone.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionalRoleId", "account.institutionalrole.required");
		AccountRequest ar = (AccountRequest) accountRequest;
		EmailValidator ev = EmailValidator.getInstance(false);
		if (!ev.isValid(ar.getEmail())) {
			errors.rejectValue("email","account.email.invalid");
		}
	}

}
