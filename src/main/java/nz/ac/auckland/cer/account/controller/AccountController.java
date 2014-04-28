package nz.ac.auckland.cer.account.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Researcher;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for cluster accounts
 */
@Controller
public class AccountController {

    private Logger log = Logger.getLogger(AccountController.class.getName());
    @Autowired private ProjectDatabaseDao pdDao;

    @RequestMapping(value = "view_account", method = RequestMethod.GET)
    public String showAccount(
            HttpServletRequest request,
            ModelMap mm) throws Exception {

        try {
            if (!(Boolean) request.getAttribute("hasUserRegistered")) {
                return "redirect:request_account_info";
            } else {
                List<String> clusterAccounts = new LinkedList<String>();
                if ((Boolean) request.getAttribute("isUserResearcher")) {
                    Researcher r = (Researcher) request.getAttribute("researcher");
                    clusterAccounts = this.pdDao.getAccountNamesForResearcherId(r.getId());
                    mm.addAttribute("fullName", r.getFullName())
                            .addAttribute("preferredName", r.getPreferredName())
                            .addAttribute("institution", r.getInstitution())
                            .addAttribute("division", r.getDivision())
                            .addAttribute("department", r.getDepartment())
                            .addAttribute("phone", r.getPhone())
                            .addAttribute("email", r.getEmail())
                            .addAttribute("institutionalRoleName",
                                    this.pdDao.getInstitutionalRoleName(r.getInstitutionalRoleId()))
                            .addAttribute("accountStatus", this.pdDao.getResearcherStatusName(r.getStatusId()));
                } else {
                    Adviser a = (Adviser) request.getAttribute("adviser");
                    clusterAccounts = this.pdDao.getAccountNamesForAdviserId(a.getId());
                    mm.addAttribute("fullName", a.getFullName()).addAttribute("institution", a.getInstitution())
                            .addAttribute("division", a.getDivision()).addAttribute("department", a.getDepartment())
                            .addAttribute("phone", a.getPhone()).addAttribute("email", a.getEmail());
                }
                mm.addAttribute("clusterAccounts", clusterAccounts);
            }
        } catch (Exception e) {
            log.error("An unexpected error happened", e);
        }
        return "view_account";
    }

}
