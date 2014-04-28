package nz.ac.auckland.cer.account.validation;

import nz.ac.auckland.cer.account.pojo.AccountRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RequestAccountValidator implements Validator {

    @Override
    public boolean supports(
            Class<?> clazz) {

        return AccountRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(
            Object accountRequest,
            Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullName", "account.fullname.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institution", "account.affiliation.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "account.phone.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionalRoleId", "account.institutionalrole.required");
        AccountRequest ar = (AccountRequest) accountRequest;
        if (ar.getInstitution() != null && ar.getInstitution().toLowerCase().equals("other")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherInstitution", "account.affiliation.required");
        }
        EmailValidator ev = EmailValidator.getInstance(false);
        if (!ev.isValid(ar.getEmail())) {
            errors.rejectValue("email", "account.email.invalid");
        }
    }

}
