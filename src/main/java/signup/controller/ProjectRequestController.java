package signup.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import common.util.TemplateEmail;

import pm.dao.ProjectDao;
import pm.pojo.Project;
import pm.pojo.RPLink;
import signup.pojo.ProjectRequest;
import signup.validation.ProjectRequestValidator;

@Controller
public class ProjectRequestController {
	
	private Logger log = Logger.getLogger(ProjectRequestController.class.getName());
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private TemplateEmail templateEmail;
	private String adminUser;
	private Integer initialResearcherRoleOnProject;
	private String emailFrom;
	private String emailTo;
	private String emailMembershipSubject;
	private String emailNewProjectSubject;
	private String researcherBaseUrl;
	private String projectBaseUrl;
	private Resource emailBodyMembershipRequest;
	private Resource emailBodyProjectRequestNoSuperviser;
	private Resource emailBodyProjectRequestWithSuperviser;

	@RequestMapping(value = "requestproject", method = RequestMethod.GET)
	public String edit(Model m, HttpServletRequest request) throws Exception {
		ProjectRequest pr = new ProjectRequest();
		pr.setAskForSuperviser(this.askForSuperviser(request));
		m.addAttribute("requestproject", pr);
		return "requestproject";
	}

    @RequestMapping(value="requestproject", method=RequestMethod.POST)
    public String onSubmit(Model m, @Valid @ModelAttribute("requestproject") ProjectRequest pr,
    	BindingResult bResult, HttpServletRequest request) throws Exception {
		if (bResult.hasErrors()) {
			m.addAttribute("requestproject", pr);
			return "requestproject";
		}
		try {
    		String researcherName = (String) request.getSession().getAttribute("researcherName");
    		Integer researcherDatabaseId = (Integer) request.getSession().getAttribute("researcherDatabaseId");				
			if (pr.getChoice().equals("JOIN_PROJECT")) {
				Project p = projectDao.getProjectForCode(pr.getProjectCode());
				this.addResearcherToProject(researcherDatabaseId, p);
	    		this.sendMembershipRequestEmail(p, researcherName, researcherDatabaseId);
			} else {
				Project p = this.createProject(request, pr);
				this.addResearcherToProject(researcherDatabaseId, p);
	    		this.sendNewProjectRequestEmail(p, pr, researcherName, researcherDatabaseId, this.askForSuperviser(request));
			}			
			return "redirect:survey";
		} catch (Exception e) {
			e.printStackTrace();
			bResult.addError(new ObjectError(bResult.getObjectName(), e.getMessage()));
			return "requestproject";
		}
    }
    
    private Project createProject(HttpServletRequest request, ProjectRequest pr) throws Exception {
    	String hostInstitution = (String) request.getSession().getAttribute("hostInstitution");
		Project p = this.projectDao.createProject(pr, hostInstitution, this.adminUser);
    	return p;
    }
    
    private void addResearcherToProject(Integer researcherDatabaseId, Project p) throws Exception {
    	try {
        	RPLink rpl = new RPLink(p.getId(), researcherDatabaseId, this.initialResearcherRoleOnProject);
    		this.projectDao.addResearcherToProject(rpl, this.adminUser);
    	} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
    	}
    }
    
	/**
	 * Send e-mail to notify us about the new project membership request
	 */
	private void sendMembershipRequestEmail(Project p, String researcherName, Integer researcherDatabaseId) throws Exception {
	    Map<String,String> templateParams = new HashMap<String,String>();
	    templateParams.put("__RESEARCHER_NAME__", researcherName);
	    templateParams.put("__PROJECT_TITLE__", p.getName());
	    templateParams.put("__PROJECT_CODE__", p.getProjectCode());
	    templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + "?id=" + p.getId());
	    templateParams.put("__RESEARCHER_LINK__", this.researcherBaseUrl + "?id=" + researcherDatabaseId);
		this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
			this.emailMembershipSubject, this.emailBodyMembershipRequest, templateParams);
	}

	/**
	 * Send e-mail to notify us about the new project membership request
	 */
	private void sendNewProjectRequestEmail(Project p, ProjectRequest pr, String researcherName, Integer researcherDatabaseId, Boolean askForSuperviser) throws Exception {
	    Map<String,String> templateParams = new HashMap<String,String>();
	    Resource resource = this.emailBodyProjectRequestNoSuperviser;
	    templateParams.put("__RESEARCHER_NAME__", researcherName);
	    templateParams.put("__PROJECT_TITLE__", p.getName());
	    templateParams.put("__PROJECT_DESCRIPTION__", p.getDescription());
	    templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + "?id=" + p.getId());
	    templateParams.put("__RESEARCHER_LINK__", this.researcherBaseUrl + "?id=" + researcherDatabaseId);
	    if (askForSuperviser) {
		    templateParams.put("__SUPERVISER_NAME__", pr.getSuperviserName());
		    templateParams.put("__SUPERVISER_EMAIL__", pr.getSuperviserEmail());
		    templateParams.put("__SUPERVISER_PHONE__", pr.getSuperviserPhone());
		    resource = this.emailBodyProjectRequestWithSuperviser;
	    }
		this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
			this.emailNewProjectSubject, resource, templateParams);
	}
	
	/**
	 * Check whether we need to ask for superviser information or not.
	 */
	private boolean askForSuperviser(HttpServletRequest request) throws Exception {
		if((Integer)request.getSession().getAttribute("institutionalRoleId") > 1) {
			return true;
		}
		return false;
	}

	/**
	 * Configure validator for cluster account request form
	 */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new ProjectRequestValidator());
    }

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public void setInitialResearcherRoleOnProject(Integer initialResearcherRoleOnProject) {
		this.initialResearcherRoleOnProject = initialResearcherRoleOnProject;
	}

	public void setTemplateEmail(TemplateEmail templateEmail) {
		this.templateEmail = templateEmail;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public void setEmailNewProjectSubject(String emailNewProjectSubject) {
		this.emailNewProjectSubject = emailNewProjectSubject;
	}

	public void setEmailMembershipSubject(String emailMembershipSubject) {
		this.emailMembershipSubject = emailMembershipSubject;
	}

	public void setResearcherBaseUrl(String researcherBaseUrl) {
		this.researcherBaseUrl = researcherBaseUrl;
	}

	public void setProjectBaseUrl(String projectBaseUrl) {
		this.projectBaseUrl = projectBaseUrl;
	}

	public void setEmailBodyMembershipRequest(Resource emailBodyMembershipRequest) {
		this.emailBodyMembershipRequest = emailBodyMembershipRequest;
	}

	public void setEmailBodyProjectRequestNoSuperviser(Resource emailBodyProjectRequestNoSuperviser) {
		this.emailBodyProjectRequestNoSuperviser = emailBodyProjectRequestNoSuperviser;
	}

	public void setEmailBodyProjectRequestWithSuperviser(Resource emailBodyProjectRequestWithSuperviser) {
		this.emailBodyProjectRequestWithSuperviser = emailBodyProjectRequestWithSuperviser;
	}

}
