package pm.dao;

import pm.pojo.Affiliation;
import pm.pojo.InstitutionalRole;
import pm.pojo.Project;
import pm.pojo.RPLink;
import pm.pojo.Researcher;
import signup.pojo.ProjectRequest;

public interface ProjectDao {

	public Affiliation[] getAffiliations() throws Exception;
	public InstitutionalRole[] getInstitutionalRoles() throws Exception;
	public Integer createResearcher(Researcher r, String adminUser) throws Exception;
	public Project createProject(ProjectRequest pr, String adminUser) throws Exception;
	public Project getProjectForCode(String projectCode) throws Exception;
	public void addResearcherToProject(RPLink rpl, String adminUser) throws Exception;
}
