package nz.ac.auckland.cer.project.dao;

import java.util.List;

import nz.ac.auckland.cer.common.util.SSLCertificateValidation;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

public class ProjectDatabaseDaoImpl extends SqlSessionDaoSupport implements ProjectDatabaseDao {

	private String restBaseUrl;
	private RestTemplate restTemplate;
	private Logger log = Logger.getLogger(ProjectDatabaseDaoImpl.class.getName());

	public ProjectDatabaseDaoImpl() {
		// disable host certificate validation until run in production,
		// because the test REST service uses a self-signed certificate.
		// FIXME in production: enable host certificate validation again.
		SSLCertificateValidation.disable();
	}
	
	@Override
	public Affiliation[] getAffiliations() throws Exception {
	    Affiliation[] affiliations = new Affiliation[0];
		String url = restBaseUrl + "advisers/affil";
		Gson gson = new Gson();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			affiliations = gson.fromJson(response.getBody(), Affiliation[].class);
		} catch (HttpStatusCodeException hsce) {
			String tmp = hsce.getResponseBodyAsString();
			JSONObject json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e) {
			log.error(e);
			throw new Exception("An unexpected error occured.", e);
		}
		return affiliations;
	}

	@Override
	public InstitutionalRole[] getInstitutionalRoles() throws Exception {
		InstitutionalRole[] iRoles = new InstitutionalRole[0];
		String url = restBaseUrl + "researchers/iroles";
		Gson gson = new Gson();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			iRoles = gson.fromJson(response.getBody(), InstitutionalRole[].class);
		} catch (HttpStatusCodeException hsce) {
			String tmp = hsce.getResponseBodyAsString();
			JSONObject json = new JSONObject(tmp);
			throw new Exception(json.getString("message"));
		} catch (Exception e) {
			log.error(e);
			throw new Exception("An unexpected error occured.", e);
		}
		return iRoles;
	}

	@Override
	public Integer createResearcher(Researcher r, String adminUser) throws Exception {
		String url = restBaseUrl + "researchers/";
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
			log.error(e);
			throw new Exception("An unexpected error occured.", e);
		}
	}
	
	public Adviser getAdviserForTuakiriSharedToken(String sharedToken) throws Exception {
		return getSqlSession().selectOne("getAdviserForTuakiriSharedToken", sharedToken);
	}
	public Researcher getResearcherForTuakiriSharedToken(String sharedToken) throws Exception {
		return getSqlSession().selectOne("getResearcherForTuakiriSharedToken", sharedToken);
	}

	public List<String> getAccountNamesForResearcherId(Integer researcherId) throws Exception {
		return getSqlSession().selectList("getAccountNamesForResearcherId", researcherId);
	}
	
	public List<String> getAccountNamesForAdviserId(Integer adviserId) throws Exception {
		return getSqlSession().selectList("getAccountNamesForAdviserId", adviserId);
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setRestBaseUrl(String restBaseUrl) {
		this.restBaseUrl = restBaseUrl;
	}

}
