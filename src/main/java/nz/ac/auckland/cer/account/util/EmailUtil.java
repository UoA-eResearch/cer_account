package nz.ac.auckland.cer.account.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.common.util.TemplateEmail;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.Person;

public class EmailUtil {

    private Logger log = Logger.getLogger(EmailUtil.class.getName());
    @Autowired private TemplateEmail templateEmail;
    @Autowired private AffiliationUtil affUtil;
    @Autowired private ProjectDatabaseDao dbDao;
    private Resource accountRequestEmailBodyResource;
    private Resource accountChangeRequestEmailBodyResource;
    private Resource accountDeletionRequestEmailBodyResource;
    private Resource otherAffiliationEmailBodyResource;
    private String accountRequestEmailSubject;
    private String accountChangeRequestEmailSubject;
    private String accountDeletionRequestEmailSubject;
    private String otherAffiliationEmailSubject;
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
            String accountName) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__NAME__", ar.getFullName());
        templateParams.put("__EMAIL__", ar.getEmail());
        templateParams.put("__PHONE__", ar.getPhone());
        templateParams.put("__ACCOUNT_NAME__", accountName);
        if (ar.getInstitution() == null || ar.getInstitution().isEmpty()) {
            templateParams.put("__INSTITUTION__", ar.getOtherInstitution() + "(not yet in database)");
            templateParams.put("__DIVISION__", "-");
            templateParams.put("__DEPARTMENT__", "-");
        } else {
            templateParams.put("__INSTITUTION__", ar.getInstitution());
            templateParams.put("__DIVISION__", ar.getDivision());
            templateParams.put("__DEPARTMENT__", ar.getDepartment());
        }
        templateParams.put("__RESEARCHER_DB_ID__", String.valueOf(dbAccountId));
        templateParams.put("__LINK__", this.researcherBaseUrl + dbAccountId);
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.accountRequestEmailSubject, this.accountRequestEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send account request email", e);
            throw new Exception("Failed to notify CeR staff about the new account request");
        }
    }

    public void sendAccountDetailsChangeRequestRequestEmail(
            Person op,
            Person np) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__OLD_INSTITUTION__", op.getInstitution());
        templateParams.put("__OLD_DIVISION__", op.getDivision());
        templateParams.put("__OLD_DEPARTMENT__", op.getDepartment());
        templateParams.put("__OLD_INSTITUTIONAL_ROLE__",
                dbDao.getInstitutionalRoleName(op.isResearcher() ? op.getInstitutionalRoleId() : -1));
        templateParams.put("__OLD_FULL_NAME__", op.getFullName());
        templateParams.put("__OLD_PREFERRED_NAME__", op.getPreferredName());
        templateParams.put("__OLD_EMAIL__", op.getEmail());
        templateParams.put("__OLD_PHONE__", op.getPhone());
        templateParams.put("__NEW_FULL_NAME__", np.getFullName());
        templateParams.put("__NEW_PREFERRED_NAME__", np.getPreferredName());
        templateParams.put("__NEW_EMAIL__", np.getEmail());
        templateParams.put("__NEW_PHONE__", np.getPhone());
        templateParams.put("__NEW_INSTITUTION__", np.getInstitution());
        templateParams.put("__NEW_DIVISION__", np.getDivision());
        templateParams.put("__NEW_DEPARTMENT__", np.getDepartment());
        templateParams.put("__NEW_INSTITUTIONAL_ROLE__", dbDao.getInstitutionalRoleName(np.getInstitutionalRoleId()));

        String link = np.isResearcher() ? (this.researcherBaseUrl + np.getId())
                : (this.adviserBaseUrl + np.getId());
        templateParams.put("__LINK__", link);

        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.accountChangeRequestEmailSubject, this.accountChangeRequestEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send account details change request email", e);
            throw new Exception("Failed to notify CeR staff about the account details change request");
        }
    }

    public void sendAccountDeletionRequestEmail(
            Person p) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__FULL_NAME__", p.getFullName());
        String link = p.isResearcher() ? (this.researcherBaseUrl + p.getId())
                : (this.adviserBaseUrl + p.getId());
        templateParams.put("__LINK__", link);
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.accountDeletionRequestEmailSubject, this.accountDeletionRequestEmailBodyResource,
                    templateParams);
        } catch (Exception e) {
            log.error("Failed to send account deletion request email.", e);
            throw new Exception("Failed to notify CeR staff about the account deletion request.");
        }
    }

    public void sendOtherAffiliationEmail(
            String institution,
            String division,
            String department) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__INSTITUTION__", institution);
        templateParams.put("__DIVISION__", division);
        templateParams.put("__DEPARTMENT__", department);
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.otherAffiliationEmailSubject, this.otherAffiliationEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send other institution email.", e);
            throw new Exception("Failed to notify CeR staff about the other institution.");
        }
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

    public void setAccountChangeRequestEmailBodyResource(
            Resource accountChangeRequestEmailBodyResource) {

        this.accountChangeRequestEmailBodyResource = accountChangeRequestEmailBodyResource;
    }

    public void setAccountChangeRequestEmailSubject(
            String accountChangeRequestEmailSubject) {

        this.accountChangeRequestEmailSubject = accountChangeRequestEmailSubject;
    }

    public void setAccountDeletionRequestEmailBodyResource(
            Resource accountDeletionRequestEmailBodyResource) {

        this.accountDeletionRequestEmailBodyResource = accountDeletionRequestEmailBodyResource;
    }

    public void setAccountDeletionRequestEmailSubject(
            String accountDeletionRequestEmailSubject) {

        this.accountDeletionRequestEmailSubject = accountDeletionRequestEmailSubject;
    }

    public void setOtherAffiliationEmailBodyResource(
            Resource otherAffiliationEmailBodyResource) {

        this.otherAffiliationEmailBodyResource = otherAffiliationEmailBodyResource;
    }

    public void setOtherAffiliationEmailSubject(
            String otherAffiliationEmailSubject) {

        this.otherAffiliationEmailSubject = otherAffiliationEmailSubject;
    }

}
