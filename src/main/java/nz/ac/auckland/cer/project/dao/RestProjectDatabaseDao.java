package nz.ac.auckland.cer.project.dao;


import nz.ac.auckland.cer.common.util.SSLCertificateValidation;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

public class RestProjectDatabaseDao implements ProjectDatabaseDao {

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
	
	public void setRestTemplate(RestTemplate restTemplate) {
		SSLCertificateValidation.disable();
		this.restTemplate = restTemplate;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
