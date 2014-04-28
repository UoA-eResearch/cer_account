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
import nz.ac.auckland.cer.project.pojo.Adviser;
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

    private Logger log = Logger.getLogger(RequestAccountController.class.getName());
    private Integer researcherStatusId;
    private String defaultPictureUrl;
    private String adminUser;
    private String projectRequestUrl;
    @Autowired private ProjectDatabaseDao pdDao;
    @Autowired private AffiliationUtil affUtil;
    @Autowired private EmailUtil emailUtil;
    @Autowired private SLCS slcs;

    @RequestMapping(value = "request_account_info", method = RequestMethod.GET)
    public String showAccountRequestInfo(
            HttpServletRequest request) throws Exception {

        try {
            if ((Boolean) request.getAttribute("hasUserRegistered")) {
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
            Integer dbAccountId = null;
            if (ar.getIsNesiStaff()) {
                Adviser a = this.createAdviserFromFormData(ar);
                dbAccountId = this.pdDao.createAdviser(a, this.adminUser);
                this.pdDao.createTuakiriSharedTokenPropertyForAdviser(dbAccountId, tuakiriSharedToken);
            } else {
                Researcher r = this.createResearcherFromFormData(ar);
                dbAccountId = this.pdDao.createResearcher(r, this.adminUser);
                this.pdDao.createTuakiriSharedTokenPropertyForResearcher(dbAccountId, tuakiriSharedToken);
            }
            this.emailUtil.sendAccountRequestEmail(ar, dbAccountId, userDN);
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
     */
    private void preprocessAccountRequest(
            AccountRequest ar) {

        String inst = ar.getInstitution();
        if (inst != null && !inst.isEmpty() && !inst.equals("Other")) {
            ar.setInstitution(this.affUtil.getInstitutionFromAffiliationString(inst));
            ar.setDivision(this.affUtil.getDivisionFromAffiliationString(inst));
            ar.setDepartment(this.affUtil.getDepartmentFromAffiliationString(inst));
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

    public void setResearcherStatusId(
            String researcherStatusId) {

        this.researcherStatusId = Integer.valueOf(researcherStatusId);
    }

    public void setAdminUser(
            String adminUser) {

        this.adminUser = adminUser;
    }

}
