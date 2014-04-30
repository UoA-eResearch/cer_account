package nz.ac.auckland.cer.account.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.account.util.EmailUtil;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
                p.setInstitution(this.affUtil.createAffiliationString(p.getInstitution(), p.getDivision(),
                        p.getDepartment()));
                m.addAttribute("person", p);
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
            @Valid @ModelAttribute("editaccount") AccountRequest ar,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        try {
            if (bResult.hasErrors()) {
                this.augmentModel(m);
                return "edit_account";
            }
            Person p = (Person) request.getAttribute("person");
            this.emailUtil.sendAccountDetailsChangeRequestRequestEmail(p, ar);
            List<String> clusterAccounts = this.pdDao.getAccountNamesForPerson(p);
            m.addAttribute("person", p);
            m.addAttribute("clusterAccounts", clusterAccounts);
            m.addAttribute("institutionalRoleName", this.pdDao.getInstitutionalRoleName(p.getInstitutionalRoleId()));
            if (p.isResearcher()) {
                m.addAttribute("accountStatus", this.pdDao.getResearcherStatusName(p.getStatusId()));
            }
        } catch (Exception e) {
            log.error("Failed to edit account", e);
            bResult.addError(new ObjectError(bResult.getObjectName(), "Internal Error: " + e.getMessage()));
            this.augmentModel(m);
            return "edit_account";
        }
        String message = "An e-mail with requested changes has been sent to the Centre for eResearch."
                + "<br>Your details will be updated shortly.";
        m.addAttribute("message", message);
        return "view_account";
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
