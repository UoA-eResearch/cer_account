package nz.ac.auckland.cer.project.dao;

import java.util.List;

import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.Person;

public interface ProjectDatabaseDao {

    public List<Affiliation> getAffiliations() throws Exception;

    public List<InstitutionalRole> getInstitutionalRoles() throws Exception;

    public Integer createPerson(
            Person p,
            String adminUser) throws Exception;
    
    public Adviser getAdviserForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public Researcher getResearcherForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public List<String> getAccountNamesForPerson(
            Person person) throws Exception;

    public void createTuakiriSharedTokenPropertyForPerson(
            Person p,
            String tuakiriSharedToken) throws Exception;

    public String getInstitutionalRoleName(
            Integer roleId) throws Exception;

    public String getResearcherStatusName(
            Integer statusId) throws Exception;


}