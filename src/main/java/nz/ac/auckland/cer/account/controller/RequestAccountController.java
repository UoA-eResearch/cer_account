package nz.ac.auckland.cer.account.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.account.slcs.SLCS;
import nz.ac.auckland.cer.account.util.EmailUtil;
import nz.ac.auckland.cer.account.validation.RequestAccountValidator;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.Person;

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
    @Autowired private SLCS slcs;
    private Logger log = Logger.getLogger(RequestAccountController.class.getName());
    private String defaultPictureUrl;
    private String projectRequestUrl;
    private Integer initialResearcherStatusId;

    @RequestMapping(value = "request_account_info", method = RequestMethod.GET)
    public String showAccountRequestInfo(
            HttpServletRequest request) throws Exception {

        try {
            if ((Boolean) request.getAttribute("hasPersonRegistered")) {
                return "redirect:view_account";
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
            this.augmentModel(m);
            AccountRequest ar = new AccountRequest();
            ar.setFullName((String) request.getAttribute("cn"));
            ar.setEmail((String) request.getAttribute("mail"));
            m.addAttribute("requestaccount", ar);
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
            @Valid @ModelAttribute("requestaccount") AccountRequest ar,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        try {
            if (bResult.hasErrors()) {
                this.augmentModel(m);
                return "request_account";
            }
            this.preprocessAccountRequest(ar);
            String tuakiriIdpUrl = (String) request.getAttribute("Shib-Identity-Provider");
            String tuakiriSharedToken = (String) request.getAttribute("shared-token");
            String userDN = this.slcs.createUserDn(tuakiriIdpUrl, ar.getFullName(), tuakiriSharedToken);
            Person p = this.createPersonFromFormData(ar);
            if (p.isResearcher()) {
                this.pdDao.createResearcher(p.getResearcher());
            } else {
                this.pdDao.createAdviser(p.getAdviser());
            }
            this.pdDao.createTuakiriSharedTokenPropertyForPerson(p, tuakiriSharedToken);
            this.emailUtil.sendAccountRequestEmail(ar, p.getId(), userDN);
            m.addAttribute("projectRequestUrl", this.projectRequestUrl);
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
            //m.addAttribute("unexpected_error", errorMessage);
        }
    }

    /**
     * Set division and department from the institution string
     * The validator has already verified that institution is not null.
     */
    private void preprocessAccountRequest(
            AccountRequest ar) {

        String inst = ar.getInstitution();
        if (inst.toLowerCase().equals("other")) {
            ar.setInstitution(ar.getOtherInstitution());
            ar.setDivision("");
            ar.setDepartment("");
        } else {
            ar.setInstitution(this.affUtil.getInstitutionFromAffiliationString(inst));
            ar.setDivision(this.affUtil.getDivisionFromAffiliationString(inst));
            ar.setDepartment(this.affUtil.getDepartmentFromAffiliationString(inst));
        }
    }

    /**
     * Create researcher object from account request form data
     */
    private Person createPersonFromFormData(
            AccountRequest ar) {

        String notes = "";
        Person p = new Person();
        p.setFullName(ar.getFullName());
        p.setPreferredName(ar.getPreferredName());
        p.setInstitution(ar.getInstitution());
        p.setDivision(ar.getDivision());
        p.setDepartment(ar.getDepartment());
        p.setEmail(ar.getEmail());
        p.setPhone(ar.getPhone());
        p.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        p.setPictureUrl(this.defaultPictureUrl);
        if (ar.getInstitution().equals("Other")) {
            notes += "Other Affiliation: " + ar.getOtherInstitution() + "<br/>";
        }
        p.setIsResearcher(!ar.getIsNesiStaff());
        if (p.isResearcher()) {
            p.setStatusId(this.initialResearcherStatusId);
            Integer instRoleId = ar.getInstitutionalRoleId();
            if (instRoleId != null) {
                p.setInstitutionalRoleId(instRoleId);
            } else {
                notes += "Other Institutional Role: " + ar.getOtherInstitutionalRole() + "<br/>";
            }            
        }
        p.setNotes(notes);
        return p;
    }

    /**
     * Configure validator for cluster account request form
     */
    @InitBinder
    protected void initBinder(
            WebDataBinder binder) {

        binder.setValidator(new RequestAccountValidator());
    }

    public void setDefaultPictureUrl(
            String defaultPictureUrl) {

        this.defaultPictureUrl = defaultPictureUrl;
    }

    public void setProjectRequestUrl(
            String projectRequestUrl) {

        this.projectRequestUrl = projectRequestUrl;
    }

    public void setInitialResearcherStatusId(
            String initialResearcherStatusId) {

        this.initialResearcherStatusId = Integer.valueOf(initialResearcherStatusId);
    }

}
