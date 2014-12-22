package nz.ac.auckland.cer.account.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.account.util.EmailUtil;
import nz.ac.auckland.cer.account.validation.RequestAccountValidator;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.Person;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for cluster accounts
 */
@Controller
public class AccountController {

    private Logger log = Logger.getLogger(AccountController.class.getName());
    @Autowired private ProjectDatabaseDao pdDao;
    @Autowired private AffiliationUtil affUtil;
    @Autowired private EmailUtil emailUtil;

    @RequestMapping(value = "view_account", method = RequestMethod.GET)
    public String viewAccount(
            HttpServletRequest request,
            ModelMap mm) throws Exception {

        try {
            if (!(Boolean) request.getAttribute("hasPersonRegistered")) {
                return "redirect:request_account_info";
            } else {
                Person p = (Person) request.getAttribute("person");
                List<String> clusterAccounts = this.pdDao.getAccountNamesForPerson(p);
                mm.addAttribute("formData", new AccountRequest());
                mm.addAttribute("person", p);
                mm.addAttribute("clusterAccounts", clusterAccounts);
                mm.addAttribute("institutionalRoleName",
                        this.pdDao.getInstitutionalRoleName(p.getInstitutionalRoleId()));
            }
        } catch (Exception e) {
            log.error("An unexpected error happened", e);
        }
        return "view_account";
    }

    @RequestMapping(value = "edit_account", method = RequestMethod.GET)
    public String showEditAccount(
            HttpServletRequest request,
            Model m) throws Exception {

        try {
            if (!(Boolean) request.getAttribute("hasPersonRegistered")) {
                return "redirect:request_account_info";
            } else {
                Person p = (Person) request.getAttribute("person");
                AccountRequest ar = new AccountRequest();
                ar.setFullName(p.getFullName());
                ar.setPreferredName(p.getPreferredName());
                String instString = this.affUtil.createAffiliationString(p.getInstitution(), p.getDivision(),
                        p.getDepartment());
                List<String> affils = affUtil.getAffiliationStrings(this.pdDao.getAffiliations());
                if (affils.contains(instString)) {
                    ar.setInstitution(instString);                    
                } else {
                    ar.setInstitution("Other");
                    ar.setOtherInstitution(p.getInstitution());
                    ar.setOtherDivision(p.getDivision());
                    ar.setOtherDepartment(p.getDepartment());
                }
                ar.setEmail(p.getEmail());
                ar.setPhone(p.getPhone());
                ar.setInstitutionalRoleId(p.getInstitutionalRoleId());
                m.addAttribute("formData", ar);
                this.augmentModel(m);
            }
        } catch (Exception e) {
            log.error("An unexpected error happened", e);
        }
        return "edit_account";
    }

    /**
     * Process cluster account request form submission
     */
    @RequestMapping(value = "edit_account", method = RequestMethod.POST)
    public String processEditAccountForm(
            Model m,
            @Valid @ModelAttribute("formData") AccountRequest ar,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        Person oldPerson = (Person) request.getAttribute("person");
        try {
            if (bResult.hasErrors()) {
                this.augmentModel(m);
                return "edit_account";
            }
            Person newPerson = this.updatePerson(oldPerson, ar);
            this.updatePersonInDatabase(newPerson);
            this.emailUtil.sendAccountDetailsChangeRequestRequestEmail(oldPerson, newPerson);
            List<String> clusterAccounts = this.pdDao.getAccountNamesForPerson(newPerson);
            m.addAttribute("person", newPerson);
            m.addAttribute("clusterAccounts", clusterAccounts);
            if (newPerson.isResearcher()) {
                m.addAttribute("accountStatus", this.pdDao.getResearcherStatusName(newPerson.getStatusId()));
                m.addAttribute("institutionalRoleName", this.pdDao.getInstitutionalRoleName(newPerson.getInstitutionalRoleId()));
            }
            if (!newPerson.getEmail().equals(oldPerson.getEmail())) {
                m.addAttribute("message", "A Centre for eResearch staff member has to change "
                        + "your registered e-mail address.<br>" + "An e-mail has been sent to the Centre "
                        + "for eResearch. Your e-mail address will be updated shortly.");
            }
        } catch (Exception e) {
            log.error("Failed to edit account", e);
            m.addAttribute("formData", ar);
            m.addAttribute("unexpected_error", "Internal Error: " + e.getMessage());
            this.augmentModel(m);
            return "edit_account";
        }
        return "view_account";
    }

    /**
     * Request for the cluster account to be closed
     */
    @RequestMapping(value = "request_account_deletion", method = RequestMethod.GET)
    public String requestDeleteAccount(
            HttpServletRequest request,
            ModelMap mm) throws Exception {

        return "request_account_deletion";
    }

    /**
     * Confirm closure of cluster account
     */
    @RequestMapping(value = "confirm_account_deletion", method = RequestMethod.GET)
    public ModelAndView confirmDeleteAccount(
            HttpServletRequest request) throws Exception {

        Person p;
        ModelAndView mav = new ModelAndView("account_deletion_retrieved");
        try {
            p = (Person) request.getAttribute("person");
            this.emailUtil.sendAccountDeletionRequestEmail(p);
            mav.addObject("message", "An e-mail with your account deletion request has been sent "
                    + "to the Centre for eResearch.<br>Your account will be closed shortly.");
        } catch (Exception e) {
        	e.printStackTrace();
        	String message = e.getMessage() == null ? "An unexpected error occured" : e.getMessage();
            mav.addObject("error_message", message);
        }
        return mav;
    }

    /**
     * Configure validator for cluster account request form
     */
    @InitBinder("formData")
    protected void initBinder(
            WebDataBinder binder) {

        binder.setValidator(new RequestAccountValidator());
    }

    /*
     * This does not include changes to e-mail address, because the e-mail
     * address has to be changed by staff, because it ties into system concerns
     */
    private Person updatePerson(
            Person op,
            AccountRequest ar) throws Exception {

        Person np = new Person(op);
        np.setFullName(ar.getFullName());
        String affil = ar.getInstitution();
        if (affil.toLowerCase().equals("other")) {
            np.setInstitution(ar.getOtherInstitution());
            np.setDivision(ar.getOtherDivision());
            np.setDepartment(ar.getOtherDepartment());
            this.emailUtil.sendOtherAffiliationEmail(np.getInstitution(), np.getDivision(), np.getDepartment());
        } else {
            np.setInstitution(this.affUtil.getInstitutionFromAffiliationString(affil));
            np.setDivision(this.affUtil.getDivisionFromAffiliationString(affil));
            np.setDepartment(this.affUtil.getDepartmentFromAffiliationString(affil));
        }
        np.setPhone(ar.getPhone());
        if (np.isResearcher()) {
            np.setPreferredName(ar.getPreferredName());
            np.setInstitutionalRoleId(ar.getInstitutionalRoleId());
        }
        return np;
    }

    /*
     * This does not include changes to e-mail address, because the e-mail
     * address has to be changed by staff, because it ties into system concerns
     */
    private void updatePersonInDatabase(
            Person p) throws Exception {

        if (p.isResearcher()) {
            this.pdDao.updateResearcher(p.getResearcher());
        } else {
            this.pdDao.updateAdviser(p.getAdviser());
        }
    }

    private void augmentModel(
            Model m) throws Exception {

        List<InstitutionalRole> iRolesTmp = this.pdDao.getInstitutionalRoles();
        Map<Integer, String> iRoles = new LinkedHashMap<Integer, String>();
        if (iRolesTmp != null) {
            for (final InstitutionalRole ir : iRolesTmp) {
                iRoles.put(ir.getId(), ir.getName());
            }
        }
        m.addAttribute("institutionalRoles", iRoles);
        m.addAttribute("affiliations", affUtil.getAffiliationStrings(this.pdDao.getAffiliations()));
    }

}
