package nz.ac.auckland.cer.account.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Researcher;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for cluster account request form *
 */
@Controller
public class AccountController {

    private Logger log = Logger.getLogger(AccountController.class.getName());
    private ProjectDatabaseDao projectDatabaseDao;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String showAccount(
            HttpServletRequest request,
            ModelMap mm) throws Exception {

        if (!(Boolean) request.getAttribute("hasUserRegistered")) {
            return "redirect:info";
        } else {
            List<String> clusterAccounts = new LinkedList<String>();
            if ((Boolean) request.getAttribute("isUserResearcher")) {
                Researcher r = (Researcher) request.getAttribute("researcher");
                clusterAccounts = this.projectDatabaseDao.getAccountNamesForResearcherId(r.getId());
                mm.addAttribute("fullName", r.getFullName());
                mm.addAttribute("institution", r.getInstitution());
                mm.addAttribute("division", r.getDivision());
                mm.addAttribute("department", r.getDepartment());
                mm.addAttribute("phone", r.getPhone());
                mm.addAttribute("email", r.getEmail());
                mm.addAttribute("institutionalRole", r.getInstitutionalRoleName());
            } else {
                Adviser a = (Adviser) request.getAttribute("adviser");
                clusterAccounts = this.projectDatabaseDao.getAccountNamesForAdviserId(a.getId());
                mm.addAttribute("fullName", a.getFullName());
                mm.addAttribute("institution", a.getInstitution());
                mm.addAttribute("division", a.getDivision());
                mm.addAttribute("department", a.getDepartment());
                mm.addAttribute("phone", a.getPhone());
                mm.addAttribute("email", a.getEmail());
                mm.addAttribute("institutionalRole", a.getInstitutionalRoleName());
            }
            mm.addAttribute("clusterAccounts", clusterAccounts);
            return "viewaccount";
        }
    }

    public void setProjectDatabaseDao(
            ProjectDatabaseDao projectDatabaseDao) {

        this.projectDatabaseDao = projectDatabaseDao;
    }

}
