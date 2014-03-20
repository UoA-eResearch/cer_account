package pm.dao;

import pm.pojo.Affiliation;
import pm.pojo.InstitutionalRole;
import pm.pojo.Project;
import pm.pojo.ProjectFacility;
import pm.pojo.ProjectWrapper;
import pm.pojo.RPLink;
import pm.pojo.Researcher;
import signup.pojo.ProjectRequest;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import common.util.SSLCertificateValidation;

public class RestProjectDao implements ProjectDao {

	private String baseUrl;
	private RestTemplate restTemplate;

	@Override
	public Affiliation[] getAffiliations() throws Exception {
	    Affiliation[] affiliations = new Affiliation[0];
		String url = baseUrl + "advisers/affil";
		Gson gson = new Gson();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			affiliations = gson.fromJson(response.getBody(), Affiliation[].class);
		} catch (HttpStatusCodeException hsce) {
			String tmp = hsce.getResponseBodyAsString();
			JSONObject json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An unexpected error occured.", e);
		}
		return affiliations;
	}

	@Override
	public InstitutionalRole[] getInstitutionalRoles() throws Exception {
		InstitutionalRole[] iRoles = new InstitutionalRole[0];
		String url = baseUrl + "researchers/iroles";
		Gson gson = new Gson();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			iRoles = gson.fromJson(response.getBody(), InstitutionalRole[].class);
		} catch (HttpStatusCodeException hsce) {
			String tmp = hsce.getResponseBodyAsString();
			JSONObject json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An unexpected error occured.", e);
		}
		return iRoles;
	}

	@Override
	public Integer createResearcher(Researcher r, String adminUser) throws Exception {
		String url = baseUrl + "researchers/";
		Gson gson = new Gson();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("RemoteUser", adminUser);
			HttpEntity<String> request = new HttpEntity<String>(gson.toJson(r), headers);
			HttpEntity<String> he = restTemplate.postForEntity(url, request, String.class);
			return new Integer((String)he.getBody());
		} catch (HttpStatusCodeException hsce) {
			String tmp = hsce.getResponseBodyAsString();
			JSONObject json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An unexpected error occured.", e);
		}
	}

	@Override
	public Project createProject(ProjectRequest pr, String hostInstitution, String adminUser) throws Exception {
		String url = baseUrl + "projects/";
		Gson gson = new Gson();
		JSONObject json = new JSONObject();
		try {
			Project p = new Project();
			ProjectFacility pf = new ProjectFacility(1);
			p.setName(pr.getProjectTitle());
			p.setDescription(pr.getProjectDescription());
			if (hostInstitution != null) {
				p.setHostInstitution(hostInstitution);
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("RemoteUser", adminUser);
			ProjectWrapper pw = new ProjectWrapper(p, new ProjectFacility[] {pf});
			HttpEntity<String> request = new HttpEntity<String>(gson.toJson(pw), headers);
			HttpEntity<String> he = restTemplate.postForEntity(url, request, String.class);
			p.setId(new Integer(he.getBody()));
			return p;
		} catch (HttpStatusCodeException hsce) {
			String tmp = hsce.getResponseBodyAsString();
			json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An unexpected error occured.", e);
		}
	}

	public Project getProjectForCode(String projectCode) throws Exception {
		String url = baseUrl + "projects/" + projectCode;
		Gson gson = new Gson();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			String body = response.getBody();
			JSONObject projectWrapper = new JSONObject(body);
			JSONObject project = projectWrapper.getJSONObject("project");
			return gson.fromJson(project.toString(), Project.class);
		} catch (HttpStatusCodeException hsce) {
			String tmp = hsce.getResponseBodyAsString();
			JSONObject json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e3) {
			e3.printStackTrace();
			throw new Exception("An unexpected error occured.", e3);
		}		
	}

	public void addResearcherToProject(RPLink rpl, String adminUser) throws Exception {
		String url = baseUrl + "projects/rp";
		Gson gson = new Gson();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("RemoteUser", adminUser);
			HttpEntity<String> entity = new HttpEntity<String>(gson.toJson(rpl), headers);
			restTemplate.put(url, entity);
		} catch (HttpStatusCodeException hsce) {
			hsce.printStackTrace();
			String tmp = hsce.getResponseBodyAsString();
			JSONObject json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e3) {
			e3.printStackTrace();
			throw new Exception("An unexpected error occured.", e3);
		}		
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		SSLCertificateValidation.disable();
		this.restTemplate = restTemplate;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
