package nz.ac.auckland.cer.project.dao;

import java.util.List;

import nz.ac.auckland.cer.common.util.SSLCertificateValidation;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.pojo.ResearcherProperty;
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
    private String restRemoteUserHeaderName;
    private String restRemoteUserHeaderValue;
    private String restAuthzHeaderValue;
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
    public void updateAdviser(
            Adviser a) throws Exception {

        String url = restBaseUrl + "advisers/" + a.getId();
        Gson gson = new Gson();
        try {
            HttpEntity<byte[]> request = new HttpEntity<byte[]>(gson.toJson(a).getBytes("UTF-8"), this.setupHeaders());
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
            HttpEntity<byte[]> request = new HttpEntity<byte[]>(gson.toJson(r).getBytes("UTF-8"), this.setupHeaders());
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
            HttpEntity<byte[]> request = new HttpEntity<byte[]>(gson.toJson(r).getBytes("UTF-8"), this.setupHeaders());
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

    public Adviser getAdviserForEppn(
            String eppn) throws Exception {

        List<Adviser> list =  getSqlSession().selectList("getAdviserForEppn", eppn);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one adviser in database with eppn " + eppn);
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

    public Researcher getResearcherForEppn(
            String eppn) throws Exception {

        List<Researcher> list = getSqlSession().selectList("getResearcherForEppn", eppn);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one researcher in database with eppn " + eppn);
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

    public void createPropertyForResearcher(
            Integer siteId, Integer researcherId, String propname, String propvalue) throws Exception {

        String url = restBaseUrl + "/researchers/" + researcherId + "/prop";
        Gson gson = new Gson();
        ResearcherProperty rp = new ResearcherProperty();
        rp.setSiteId(siteId);
        rp.setResearcherId(researcherId);
        rp.setPropname(propname);
        rp.setPropvalue(propvalue);
        JSONObject json = new JSONObject();
        try {
            HttpEntity<byte[]> request = new HttpEntity<byte[]>(gson.toJson(rp).getBytes("UTF-8"), this.setupHeaders());
            restTemplate.put(url, request);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
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

    public void setRestAuthzHeaderValue(
            String restAuthzHeaderValue) {

        this.restAuthzHeaderValue = restAuthzHeaderValue;
    }

    public void setRestRemoteUserHeaderName(
            String restRemoteUserHeaderName) {

        this.restRemoteUserHeaderName = restRemoteUserHeaderName;
    }

    public void setRestRemoteUserHeaderValue(
            String restRemoteUserHeaderValue) {

        this.restRemoteUserHeaderValue = restRemoteUserHeaderValue;
    }

    private HttpHeaders setupHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(this.restRemoteUserHeaderName, this.restRemoteUserHeaderValue);
        headers.set("Authorization", this.restAuthzHeaderValue);
        return headers;
    }


}
