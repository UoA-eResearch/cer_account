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
import nz.ac.auckland.cer.account.util.EmailUtil;
import nz.ac.auckland.cer.account.util.Util;
import nz.ac.auckland.cer.account.validation.RequestAccountValidator;
import nz.ac.auckland.cer.common.util.TemplateUtil;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.AffiliationUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controller for cluster account requests
 */
@Controller
public class RequestAccountController {

    @Autowired private ProjectDatabaseDao pdDao;
    @Autowired private AffiliationUtil affUtil;
    @Autowired private EmailUtil emailUtil;
    @Autowired private Util util;
    @Autowired private TemplateUtil templateUtil;
    private Logger log = Logger.getLogger(RequestAccountController.class.getName());
    private String dnTemplate;
    private String defaultPictureUrl;
    private String projectRequestUrl;
    private String membershipRequestUrl;
    private Integer initialResearcherStatusId;

    @RequestMapping(value = "request_account_info", method = RequestMethod.GET)
    public String showAccountRequestInfo(
            Model m,
            HttpServletRequest request) throws Exception {

        try {
            if ((Boolean) request.getAttribute("hasPersonRegistered")) {
                m.addAttribute("message", "You already have an account.");
                return "view_account";
            }
        } catch (Exception e) {
            log.error("An unexpected error happened", e);
        }
        return "request_account_info";
    }

    /**
     * Render cluster account request form
     */
    @RequestMapping(value = "request_account", method = RequestMethod.GET)
    public String showAccountRequestForm(
            Model m,
            HttpServletRequest request) throws Exception {

        try {
            if ((Boolean) request.getAttribute("hasPersonRegistered")) {
                m.addAttribute("message", "You already have an account.");
                return "view_account";
            }
            this.augmentModel(m);
            AccountRequest ar = new AccountRequest();
            ar.setFullName((String) request.getAttribute("cn"));
            ar.setEmail((String) request.getAttribute("mail"));
            m.addAttribute("formData", ar);
        } catch (Exception e) {
            log.error("An unexpected error happened", e);
        }
        return "request_account";
    }

    /**
     * Process cluster account request form submission
     */
    @RequestMapping(value = "request_account", method = RequestMethod.POST)
    public String processAccountRequestForm(
            Model m,
            @Valid @ModelAttribute("formData") AccountRequest ar,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        try {
            if (bResult.hasErrors()) {
                this.augmentModel(m);
                return "request_account";
            }
            this.preprocessAccountRequest(ar);
            String tuakiriSharedToken = (String) request.getAttribute("shared-token");
            String eppn = (String) request.getAttribute("eppn");
            String userDN = this.createUserDn(request);
            if (userDN == null) {
                userDN = "N/A";
            }
            userDN = userDN.trim();
            Researcher r = this.createResearcherFromFormData(ar);
            String accountName = util.createAccountName(eppn, r.getFullName());
            r.setId(this.pdDao.createResearcher(r));
            this.pdDao.createPropertyForResearcher(1, r.getId(), "tuakiriSharedToken", tuakiriSharedToken);
            this.pdDao.createPropertyForResearcher(1, r.getId(), "DN", userDN);
            if (eppn != null && !eppn.isEmpty()) {
            	this.pdDao.createPropertyForResearcher(1, r.getId(), "eppn", eppn);
            }
            this.emailUtil.sendAccountRequestEmail(ar, r.getId(), accountName);
            m.addAttribute("projectRequestUrl", this.projectRequestUrl);
            m.addAttribute("membershipRequestUrl", this.membershipRequestUrl);
        } catch (Exception e) {
            log.error("Failed to process account request", e);
            bResult.addError(new ObjectError(bResult.getObjectName(), "Internal Error: " + e.getMessage()));
            this.augmentModel(m);
            return "request_account";
        }
        return "request_account_success";
    }

    /**
     * Fetch institutional roles and affiliations, and add them to the model. If
     * an error occurs, add error message to the model.
     */
    private void augmentModel(
            Model m) {

        String errorMessage = "";
        List<InstitutionalRole> ir = null;
        List<Affiliation> af = null;
        HashMap<Integer, String> institutionalRoles = new LinkedHashMap<Integer, String>();

        try {
            ir = this.pdDao.getInstitutionalRoles();
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
            af = this.pdDao.getAffiliations();
            if (af == null || af.size() == 0) {
                throw new Exception();
            }
            m.addAttribute("affiliations", this.affUtil.getAffiliationStrings(af));
        } catch (Exception e) {
            errorMessage += "Internal Error: Failed to load affiliations. ";
        }

        if (errorMessage.trim().length() > 0) {
            // m.addAttribute("unexpected_error", errorMessage);
        }
    }

    /**
     * Set division and department from the institution string The validator has
     * already verified that institution is not null.
     */
    private void preprocessAccountRequest(
            AccountRequest ar) throws Exception {

        String inst = ar.getInstitution();
        if (inst.toLowerCase().equals("other")) {
            ar.setInstitution(ar.getOtherInstitution());
            ar.setDivision(ar.getOtherDivision());
            ar.setDepartment(ar.getOtherDepartment());
            this.emailUtil.sendOtherAffiliationEmail(ar.getInstitution(), ar.getDivision(), ar.getDepartment());
        } else {
            ar.setInstitution(this.affUtil.getInstitutionFromAffiliationString(inst));
            ar.setDivision(this.affUtil.getDivisionFromAffiliationString(inst));
            ar.setDepartment(this.affUtil.getDepartmentFromAffiliationString(inst));
        }
    }

    /**
     * Create researcher object from account request form data
     */
    private Researcher createResearcherFromFormData(
            AccountRequest ar) throws Exception {

        Researcher tmp = new Researcher();
        tmp.setFullName(ar.getFullName());
        tmp.setPreferredName(ar.getPreferredName());
        tmp.setEmail(ar.getEmail());
        tmp.setPhone(ar.getPhone());
        tmp.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        tmp.setPictureUrl(this.defaultPictureUrl);
        tmp.setInstitution(ar.getInstitution());
        tmp.setDivision(ar.getDivision());
        tmp.setDepartment(ar.getDepartment());
        tmp.setStatusId(this.initialResearcherStatusId);
        Integer instRoleId = ar.getInstitutionalRoleId();
        tmp.setInstitutionalRoleId(instRoleId);
        return tmp;
    }
    
    private String createUserDn(HttpServletRequest request) {
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("{o}", (String)request.getAttribute("o"));
    	m.put("{cn}", (String)request.getAttribute("cn"));
    	m.put("{shared_token}", (String)request.getAttribute("shared-token"));
    	return this.templateUtil.replace(this.dnTemplate, m);
    }
    
    /**
     * Configure validator for cluster account request form
     */
    @InitBinder
    protected void initBinder(
            WebDataBinder binder) {

        binder.setValidator(new RequestAccountValidator());
    }

    public void setDnTemplate(
            String dnTemplate) {

        this.dnTemplate = dnTemplate;
    }

    public void setDefaultPictureUrl(
            String defaultPictureUrl) {

        this.defaultPictureUrl = defaultPictureUrl;
    }

    public void setProjectRequestUrl(
            String projectRequestUrl) {

        this.projectRequestUrl = projectRequestUrl;
    }

    public void setMembershipRequestUrl(
            String membershipRequestUrl) {

        this.membershipRequestUrl = membershipRequestUrl;
    }

    public void setInitialResearcherStatusId(
            String initialResearcherStatusId) {

        this.initialResearcherStatusId = Integer.valueOf(initialResearcherStatusId);
    }

}
