package nz.ac.auckland.cer.project.dao;

import java.util.List;

import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;

public interface ProjectDatabaseDao {

    public List<Affiliation> getAffiliations() throws Exception;

    public List<InstitutionalRole> getInstitutionalRoles() throws Exception;

    public Integer createAdviser(
            Adviser a,
            String adminUser) throws Exception;

    public Integer createResearcher(
            Researcher r,
            String adminUser) throws Exception;

    public Adviser getAdviserForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public Researcher getResearcherForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public List<String> getAccountNamesForResearcherId(
            Integer researcherId) throws Exception;

    public List<String> getAccountNamesForAdviserId(
            Integer adviserId) throws Exception;
    
    public void createTuakiriSharedTokenPropertyForResearcher(
            Integer researcherId, 
            String tuakiriSharedToken) throws Exception;

    public void createTuakiriSharedTokenPropertyForAdviser(
            Integer adviserId, 
            String tuakiriSharedToken) throws Exception;

    public String getInstitutionalRoleName(
            Integer roleId) throws Exception;

    public String getResearcherStatusName(
            Integer statusId) throws Exception;


}