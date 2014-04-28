package nz.ac.auckland.cer.account.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.common.util.TemplateEmail;

public class EmailUtil {

    @Autowired private TemplateEmail templateEmail;
    private Resource accountRequestEmailBodyResource;
    private String accountRequestEmailSubject;
    private String emailFrom;
    private String emailTo;
    private String adviserBaseUrl;
    private String researcherBaseUrl;

    /**
     * Send e-mail to notify us about the new account request
     */
    public void sendAccountRequestEmail(
            AccountRequest ar,
            Integer dbAccountId,
            String dn) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__DN__", dn);
        templateParams.put("__NAME__", ar.getFullName());
        templateParams.put("__EMAIL__", ar.getEmail());
        templateParams.put("__PHONE__", ar.getPhone());
        if (ar.getInstitution() == null || ar.getInstitution().isEmpty()) {
            templateParams.put("__INSTITUTION__", ar.getOtherInstitution());
            templateParams.put("__DIVISION__", "-");
            templateParams.put("__DEPARTMENT__", "-");
        } else {
            templateParams.put("__INSTITUTION__", ar.getInstitution());
            templateParams.put("__DIVISION__", ar.getDivision());
            templateParams.put("__DEPARTMENT__", ar.getDepartment());
        }
        if (ar.getIsNesiStaff()) {
            templateParams.put("__LINK__", this.adviserBaseUrl + "?id=" + dbAccountId);
        } else {
            templateParams.put("__LINK__", this.researcherBaseUrl + "?id=" + dbAccountId);
        }
        this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null, this.accountRequestEmailSubject,
                this.accountRequestEmailBodyResource, templateParams);
    }

    public void setAccountRequestEmailBodyResource(
            Resource accountRequestEmailBodyResource) {
    
        this.accountRequestEmailBodyResource = accountRequestEmailBodyResource;
    }

    public void setAdviserBaseUrl(
            String adviserBaseUrl) {
    
        this.adviserBaseUrl = adviserBaseUrl;
    }

    public void setResearcherBaseUrl(
            String researcherBaseUrl) {
    
        this.researcherBaseUrl = researcherBaseUrl;
    }

    public void setEmailFrom(
            String emailFrom) {
    
        this.emailFrom = emailFrom;
    }

    public void setEmailTo(
            String emailTo) {
    
        this.emailTo = emailTo;
    }

    public void setAccountRequestEmailSubject(
            String accountRequestEmailSubject) {
    
        this.accountRequestEmailSubject = accountRequestEmailSubject;
    }

    
}
