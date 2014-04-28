package nz.ac.auckland.cer.account.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.account.slcs.SLCS;
import nz.ac.auckland.cer.account.validation.RequestAccountValidator;
import nz.ac.auckland.cer.common.util.TemplateEmail;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.AffiliationUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for cluster account request form *
 */
@Controller
public class RequestAccountController {

    private Logger log = Logger.getLogger(RequestAccountController.class.getName());
    private Integer researcherStatusId;
    private String defaultPictureUrl;
    private String adminUser;
    private String emailSubject;
    private String emailFrom;
    private String emailTo;
    private String adviserBaseUrl;
    private String researcherBaseUrl;
    private String projectRequestUrl;
    private Resource emailBodyResource;
    @Autowired private ProjectDatabaseDao projectDatabaseDao;
    @Autowired private AffiliationUtil affiliationUtil;
    @Autowired private TemplateEmail templateEmail;
    @Autowired private SLCS slcs;

    @RequestMapping(value = "info", method = RequestMethod.GET)
    public String showAccountRequestInfo(
            HttpServletRequest request) throws Exception {

        if ((Boolean) request.getAttribute("hasUserRegistered")) {
            return "redirect:view";
        }
        return "info";
    }

    /**
     * Render cluster account request form
     */
    @RequestMapping(value = "requestaccount", method = RequestMethod.GET)
    public String showAccountRequestForm(
            Model m,
            HttpServletRequest request) throws Exception {

        this.augmentModel(m);
        AccountRequest ar = new AccountRequest();
        ar.setFullName((String) request.getAttribute("cn"));
        ar.setEmail((String) request.getAttribute("mail"));
        m.addAttribute("requestaccount", ar);
        return "requestaccount";
    }

    /**
     * Process cluster account request form submission
     */
    @RequestMapping(value = "requestaccount", method = RequestMethod.POST)
    public String processAccountRequestForm(
            Model m,
            @Valid @ModelAttribute("requestaccount") AccountRequest ar,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        if (bResult.hasErrors()) {
            this.augmentModel(m);
            return "requestaccount";
        }
        try {
            this.preprocessAccountRequest(ar);
            String tuakiriIdpUrl = (String) request.getAttribute("Shib-Identity-Provider");
            String tuakiriSharedToken = (String) request.getAttribute("shared-token");
            String userDN = this.slcs.createUserDn(tuakiriIdpUrl, ar.getFullName(), tuakiriSharedToken);
            Integer databaseId = null;
            if (ar.getIsNesiStaff()) {
                Adviser a = this.createAdviserFromFormData(ar);
                databaseId = this.projectDatabaseDao.createAdviser(a, this.adminUser);
                this.projectDatabaseDao.createTuakiriSharedTokenPropertyForAdviser(databaseId,
                        tuakiriSharedToken);
            } else {
                Researcher r = this.createResearcherFromFormData(ar);
                databaseId = this.projectDatabaseDao.createResearcher(r, this.adminUser);
                this.projectDatabaseDao.createTuakiriSharedTokenPropertyForResearcher(databaseId,
                        tuakiriSharedToken);
            }
            this.sendEmailNotification(ar, databaseId, userDN);
        } catch (Exception e) {
            log.error("Failed to process account request", e);
            bResult.addError(new ObjectError(bResult.getObjectName(), "Internal Error: " + e.getMessage()));
            this.augmentModel(m);
            return "requestaccount";
        }
        
        m.addAttribute("projectRequestUrl", this.projectRequestUrl);
        return "accountrequestsuccess";
    }

    /**
     * Configure validator for cluster account request form
     */
    @InitBinder
    protected void initBinder(
            WebDataBinder binder) {

        binder.setValidator(new RequestAccountValidator());
    }

    /**
     * Fetch institutional roles and affiliations, and add them to the model. If
     * an error occurs, add error message to the model.
     */
    private void augmentModel(
            Model m) throws Exception {

        String errorMessage = "";
        List<InstitutionalRole> ir = null;
        List<Affiliation> af = null;
        HashMap<Integer, String> institutionalRoles = new LinkedHashMap<Integer, String>();

        try {
            ir = this.projectDatabaseDao.getInstitutionalRoles();
            if (ir == null || ir.size() == 0) {
                throw new Exception();
            }
            for (InstitutionalRole role : ir) {
                institutionalRoles.put(role.getId(), role.getName());
            }
            m.addAttribute("institutionalRoles", institutionalRoles);
        } catch (Exception e) {
            errorMessage += "Internal Error: Failed to load institutional roles. ";
        }

        try {
            af = this.projectDatabaseDao.getAffiliations();
            if (af == null || af.size() == 0) {
                throw new Exception();
            }
            m.addAttribute("affiliations", this.affiliationUtil.getAffiliationStrings(af));
        } catch (Exception e) {
            errorMessage += "Internal Error: Failed to load affiliations. ";
        }

        if (errorMessage.trim().length() > 0) {
            m.addAttribute("unexpected_error", errorMessage);
        }
    }

    /**
     * Set division and department from the institution string
     */
    private void preprocessAccountRequest(AccountRequest ar) {
        String inst = ar.getInstitution();
        if (inst != null && !inst.isEmpty() && !inst.equals("Other")) {
            ar.setInstitution(this.affiliationUtil.getInstitutionFromAffiliationString(inst));
            ar.setDivision(this.affiliationUtil.getDivisionFromAffiliationString(inst));
            ar.setDepartment(this.affiliationUtil.getDepartmentFromAffiliationString(inst));
        }
    }
    
    /**
     * Create researcher object from account request form data
     */
    private Researcher createResearcherFromFormData(
            AccountRequest ar) {

        Researcher r = new Researcher();
        r.setFullName(ar.getFullName());
        r.setPreferredName(ar.getPreferredName());
        r.setInstitution(ar.getInstitution());
        r.setDivision(ar.getDivision());
        r.setDepartment(ar.getDepartment());
        r.setStatusId(this.researcherStatusId);
        r.setEmail(ar.getEmail());
        r.setPhone(ar.getPhone());
        r.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        r.setPictureUrl(this.defaultPictureUrl);
        String notes = "";
        String specifiedInst = ar.getInstitution();
        if (specifiedInst.equals("Other")) {
            notes += "Other Affiliation: " + ar.getOtherInstitution() + "<br/>";
        }
        Integer instRoleId = ar.getInstitutionalRoleId();
        if (instRoleId != null) {
            r.setInstitutionalRoleId(instRoleId);
        } else {
            notes += "Other Institutional Role: " + ar.getOtherInstitutionalRole() + "<br/>";
        }
        r.setNotes(notes);
        return r;
    }
    
    /**
     * Create researcher object from account request form data
     */
    private Adviser createAdviserFromFormData(
            AccountRequest ar) {

        Adviser a = new Adviser();
        a.setFullName(ar.getFullName());
        a.setEmail(ar.getEmail());
        a.setPhone(ar.getPhone());
        a.setInstitution(ar.getInstitution());
        a.setDivision(ar.getDivision());
        a.setDepartment(ar.getDepartment());
        a.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        a.setPictureUrl(this.defaultPictureUrl);
        String notes = "";
        String specifiedInst = ar.getInstitution();
        if (specifiedInst.equals("Other")) {
            notes += "Other Affiliation: " + ar.getOtherInstitution() + "<br/>";
        }
        a.setNotes(notes);
        return a;
    }

    /**
     * Send e-mail to notify us about the new account request
     */
    private void sendEmailNotification(
            AccountRequest ar,
            Integer databaseId,
            String dn) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__DN__", dn);
        templateParams.put("__NAME__", ar.getFullName());
        templateParams.put("__EMAIL__", ar.getEmail());
        templateParams.put("__PHONE__", ar.getPhone());
        if (ar.getInstitution() == null || ar.getInstitution().isEmpty()) {
            templateParams.put("__INSTITUTION__", "Other");
            templateParams.put("__DIVISION__", "Other");
            templateParams.put("__DEPARTMENT__", "Other");
        } else {
            templateParams.put("__INSTITUTION__", ar.getInstitution());
            templateParams.put("__DIVISION__", ar.getDivision());
            templateParams.put("__DEPARTMENT__", ar.getDepartment());
        }
        if (ar.getIsNesiStaff()) {
            templateParams.put("__LINK__", this.adviserBaseUrl + "?id=" + databaseId);            
        } else {
            templateParams.put("__LINK__", this.researcherBaseUrl + "?id=" + databaseId);            
        }
        this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null, this.emailSubject,
                this.emailBodyResource, templateParams);
    }

    public void setDefaultPictureUrl(
            String defaultPictureUrl) {

        this.defaultPictureUrl = defaultPictureUrl;
    }

    public void setAffiliationUtil(
            AffiliationUtil affiliationUtil) {

        this.affiliationUtil = affiliationUtil;
    }

    public void setEmailFrom(
            String emailFrom) {

        this.emailFrom = emailFrom;
    }

    public void setEmailTo(
            String emailTo) {

        this.emailTo = emailTo;
    }

    public void setEmailSubject(
            String emailSubject) {

        this.emailSubject = emailSubject;
    }

    public void setProjectRequestUrl(
            String projectRequestUrl) {

        this.projectRequestUrl = projectRequestUrl;
    }

    public void setEmailBodyResource(
            Resource emailBodyResource) {

        this.emailBodyResource = emailBodyResource;
    }

    public void setResearcherStatusId(
            String researcherStatusId) {

        this.researcherStatusId = Integer.valueOf(researcherStatusId);
    }

    public void setAdminUser(
            String adminUser) {

        this.adminUser = adminUser;
    }

    public void setAdviserBaseUrl(
            String adviserBaseUrl) {

        this.adviserBaseUrl = adviserBaseUrl;
    }

    public void setResearcherBaseUrl(
            String researcherBaseUrl) {

        this.researcherBaseUrl = researcherBaseUrl;
    }
}
