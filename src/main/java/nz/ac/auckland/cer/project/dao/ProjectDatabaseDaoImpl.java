package nz.ac.auckland.cer.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.ac.auckland.cer.common.util.SSLCertificateValidation;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.Person;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

public class ProjectDatabaseDaoImpl extends SqlSessionDaoSupport implements ProjectDatabaseDao {

    private String restBaseUrl;
    private RestTemplate restTemplate;
    private String restAdminUser;
    private String restAuthzHeader;
    private Logger log = Logger.getLogger(ProjectDatabaseDaoImpl.class.getName());

    public ProjectDatabaseDaoImpl() {

        // disable host certificate validation until run in production,
        // because the test REST service uses a self-signed certificate.
        // FIXME in production: enable host certificate validation again.
        SSLCertificateValidation.disable();
    }

    @Override
    public List<Affiliation> getAffiliations() throws Exception {

        return getSqlSession().selectList("getAffiliations");
        /*
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
        */
    }

    @Override
    public List<InstitutionalRole> getInstitutionalRoles() throws Exception {
        return getSqlSession().selectList("getInstitutionalRoles");
        /*
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
        */
    }
    
    @Override
    public Integer createAdviser(
            Adviser a) throws Exception {

        String url = restBaseUrl + "advisers/";
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(gson.toJson(a), this.setupHeaders());
            HttpEntity<String> he = restTemplate.postForEntity(url, request, String.class);
            return new Integer((String) he.getBody());
        } catch (HttpStatusCodeException hsce) {
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error(e);
            throw new Exception("An unexpected error occured.", e);
        }
    }

    @Override
    public void updateAdviser(
            Adviser a) throws Exception {

        String url = restBaseUrl + "advisers/" + a.getId();
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(gson.toJson(a), this.setupHeaders());
            restTemplate.postForEntity(url, request, String.class);
        } catch (HttpStatusCodeException hsce) {
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error(e);
            throw new Exception("An unexpected error occured.", e);
        }
    }

    @Override
    public Integer createResearcher(
            Researcher r) throws Exception {

        String url = restBaseUrl + "researchers/";
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(gson.toJson(r), this.setupHeaders());
            HttpEntity<String> he = restTemplate.postForEntity(url, request, String.class);
            return new Integer((String) he.getBody());
        } catch (HttpStatusCodeException hsce) {
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error(e);
            throw new Exception("An unexpected error occured.", e);
        }
    }

    @Override
    public void updateResearcher(
            Researcher r) throws Exception {

        String url = restBaseUrl + "researchers/" + r.getId();
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(gson.toJson(r), this.setupHeaders());
            restTemplate.postForEntity(url, request, String.class);
        } catch (HttpStatusCodeException hsce) {
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error(e);
            throw new Exception("An unexpected error occured.", e);
        }
    }


    public Adviser getAdviserForTuakiriSharedToken(
            String sharedToken) throws Exception {

        List<Adviser> list =  getSqlSession().selectList("getAdviserForTuakiriSharedToken", sharedToken);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one adviser in database with shared token " + sharedToken);
            }
            return list.get(0);
        }
        return null;
    }

    public Researcher getResearcherForTuakiriSharedToken(
            String sharedToken) throws Exception {

        List<Researcher> list = getSqlSession().selectList("getResearcherForTuakiriSharedToken", sharedToken);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one researcher in database with shared token " + sharedToken);
            }
            return list.get(0);
        }
        return null;
    }

    public List<String> getAccountNamesForPerson(
            Person p) throws Exception {

        if (p.isResearcher()) {
            return getSqlSession().selectList("getAccountNamesForResearcherId", p.getId());            
        } else {
            return getSqlSession().selectList("getAccountNamesForAdviserId", p.getId());            
        }
    }

    public List<String> getAccountNamesForAdviserId(
            Integer adviserId) throws Exception {

        return getSqlSession().selectList("getAccountNamesForAdviserId", adviserId);
    }

    public void createTuakiriSharedTokenPropertyForResearcher(
            Researcher r, 
            String tuakiriSharedToken) throws Exception {
        
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("id", r.getId());
        m.put("tuakiriSharedToken", tuakiriSharedToken);
        getSqlSession().insert("createTuakiriSharedTokenPropertyForResearcher", m);
    }

    public String getInstitutionalRoleName(
            Integer roleId) throws Exception {
        
        return getSqlSession().selectOne("getInstitutionalRoleName", roleId);
    }

    public String getResearcherStatusName(
            Integer statusId) throws Exception {
        
        return getSqlSession().selectOne("getResearcherStatusName", statusId);
    }

    public void setRestTemplate(
            RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    public void setRestBaseUrl(
            String restBaseUrl) {

        this.restBaseUrl = restBaseUrl;
    }

    public void setRestAdminUser(
            String restAdminUser) {
    
        this.restAdminUser = restAdminUser;
    }

    public void setRestAuthzHeader(
            String restAuthzHeader) {

        this.restAuthzHeader = restAuthzHeader;
    }

    private HttpHeaders setupHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Proxy-REMOTE-USER", this.restAdminUser);
        headers.set("Authorization", this.restAuthzHeader);
        return headers;
    }


}
