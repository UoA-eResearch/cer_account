package nz.ac.auckland.cer.account.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.account.slcs.SLCS;
import nz.ac.auckland.cer.account.validation.RequestAccountValidator;
import nz.ac.auckland.cer.common.util.TemplateEmail;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.AffiliationUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
	private String researcherBaseUrl;
	private Resource emailBodyResource;
	@Autowired private ProjectDatabaseDao projectDatabaseDao;
	@Autowired private AffiliationUtil affiliationUtil;
	@Autowired private TemplateEmail templateEmail;
	@Autowired private SLCS slcs;
	
	@RequestMapping(value = "info", method = RequestMethod.GET)
	public String showAccountRequestInfo(ModelMap modelMap) throws Exception {
		return "info";
	}

    /**
     * Render cluster account request form
     */
	@RequestMapping(value = "requestaccount", method = RequestMethod.GET)
	public String showAccountRequestForm(Model m, HttpServletRequest request)  throws Exception {
		this.augmentModel(m);
		AccountRequest ar = new AccountRequest();
		ar.setFullName((String)request.getAttribute("cn"));
		m.addAttribute("requestaccount", ar);
		return "requestaccount";
	}

	/**
	 * Handle cluster account request form submission
	 */
	@RequestMapping(value = "requestaccount", method = RequestMethod.POST)
	public String processAccountRequestForm(Model m, @Valid @ModelAttribute("requestaccount") AccountRequest requestAccount,
			BindingResult bResult, HttpServletRequest request) throws Exception {
		if (bResult.hasErrors()) {
			this.augmentModel(m);
			return "requestaccount";
		}
		try {
			String tuakiriIdpUrl = (String) request.getAttribute("Shib-Identity-Provider");
			String tuakiriSharedToken = (String) request.getAttribute("shared-token");
			String userDN = this.slcs.createUserDn(tuakiriIdpUrl, requestAccount.getFullName(), tuakiriSharedToken);
			Researcher r = this.createResearcherFromFormData(requestAccount);
			Integer researcherDatabaseId = this.projectDatabaseDao.createResearcher(r, this.adminUser);
			this.sendEmailNotification(r, researcherDatabaseId, userDN);
			HttpSession s = request.getSession();
		    s.setAttribute("institutionalRoleId", requestAccount.getInstitutionalRoleId());
		    s.setAttribute("researcherName", requestAccount.getFullName());
		    s.setAttribute("researcherDatabaseId", researcherDatabaseId);
		    s.setAttribute("hostInstitution", r.getInstitution());
		} catch (Exception e) {
			e.printStackTrace();
			bResult.addError(new ObjectError(bResult.getObjectName(), "Internal Error: " + e.getMessage()));
			this.augmentModel(m);
			return "requestaccount";
		}
		return "accountrequestsuccess";
	}

	/**
	 * Configure validator for cluster account request form
	 */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new RequestAccountValidator());
    }

	/**
	 * Fetch institutional roles and affiliations, and add them to the model.
	 * If an error occurs, an error message is added to the model.
	 */
	private void augmentModel(Model m) throws Exception {
		InstitutionalRole[] ir = null;
		HashMap<Integer, String> institutionalRoles = new LinkedHashMap<Integer, String>();
		Affiliation[] af = null;
		String errorMessage = "";
		
		try {
		  ir = this.projectDatabaseDao.getInstitutionalRoles();
		  if (ir == null || ir.length == 0) {
			  throw new Exception();
		  } else {
			for (InstitutionalRole role : ir) {
			  institutionalRoles.put(role.getId(), role.getName());
			}
 		  }
		} catch (Exception e) {
			errorMessage += "Internal Error: Failed to load institutional roles. ";
		}
		
		try {
		  af = this.projectDatabaseDao.getAffiliations();
		  if (af == null || af.length == 0) {
			  throw new Exception();
		  }
		} catch (Exception e) {
			errorMessage += "Internal Error: Failed to load affiliations. ";
		}
		
		if (errorMessage.trim().length() > 0) {
			m.addAttribute("unexpected_error", errorMessage);
		}
		m.addAttribute("institutionalRoles", institutionalRoles);
		m.addAttribute("affiliations", this.affiliationUtil.getAffiliationStrings(af));
	}

	/**
	 * Create researcher object from account request form data
	 */
	private Researcher createResearcherFromFormData(AccountRequest ra) {
		Researcher r = new Researcher();
		r.setFullName(ra.getFullName());
		r.setPreferredName(ra.getPreferredName());
		r.setStatusId(this.researcherStatusId);
		r.setEmail(ra.getEmail());
		r.setPhone(ra.getPhone());
		r.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		r.setPictureUrl(this.defaultPictureUrl);
		String notes = "";
		String specifiedInst = ra.getInstitution();
		if (specifiedInst != null && !specifiedInst.isEmpty() && !specifiedInst.equals("Other")) {
			r.setInstitution(this.affiliationUtil.getInstitutionFromAffiliationString(specifiedInst));
			r.setDivision(this.affiliationUtil.getDivisionFromAffiliationString(specifiedInst));
			r.setDepartment(this.affiliationUtil.getDepartmentFromAffiliationString(specifiedInst));
		} else {
			notes += "Other Affiliation: " + ra.getOtherInstitution() + "<br/>";
		}
		Integer instRoleId = ra.getInstitutionalRoleId();
		if (instRoleId != null) {
			r.setInstitutionalRoleId(instRoleId);
		} else {
			notes += "Other Institutional Role: " + ra.getOtherInstitutionalRole() + "<br/>";
		}
		r.setNotes(notes);
		return r;
	}

	/**
	 * Send e-mail to notify us about the new account request
	 */
	private void sendEmailNotification(Researcher r, Integer researcherDatabaseId, String dn) throws Exception {
	    Map<String,String> templateParams = new HashMap<String,String>();
	    templateParams.put("__DN__", dn);
	    templateParams.put("__NAME__", r.getFullName());
	    templateParams.put("__EMAIL__", r.getEmail());
	    templateParams.put("__PHONE__", r.getPhone());
	    if (r.getInstitution() == null || r.getInstitution().isEmpty()) {
		    templateParams.put("__INSTITUTION__", "Other");	    	
		    templateParams.put("__DIVISION__", "Other");
		    templateParams.put("__DEPARTMENT__", "Other");
	    } else {
	    	templateParams.put("__INSTITUTION__", r.getInstitution());
	    	templateParams.put("__DIVISION__", r.getDivision());
	    	templateParams.put("__DEPARTMENT__", r.getDepartment());
	    }
	    templateParams.put("__LINK__", this.researcherBaseUrl + "?id=" + researcherDatabaseId);
		this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
			this.emailSubject, this.emailBodyResource, templateParams);
	}

	public void setDefaultPictureUrl(String defaultPictureUrl) {
		this.defaultPictureUrl = defaultPictureUrl;
	}

	public void setAffiliationUtil(AffiliationUtil affiliationUtil) {
		this.affiliationUtil = affiliationUtil;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public void setEmailBodyResource(Resource emailBodyResource) {
		this.emailBodyResource = emailBodyResource;
	}

	public void setResearcherStatusId(String researcherStatusId) {
		this.researcherStatusId = Integer.valueOf(researcherStatusId);
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public void setResearcherBaseUrl(String researcherBaseUrl) {
		this.researcherBaseUrl = researcherBaseUrl;
	}
}
