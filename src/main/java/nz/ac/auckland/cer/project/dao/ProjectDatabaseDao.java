package nz.ac.auckland.cer.project.dao;

import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;

public interface ProjectDatabaseDao {

	public Affiliation[] getAffiliations() throws Exception;
	public InstitutionalRole[] getInstitutionalRoles() throws Exception;
	public Integer createResearcher(Researcher r, String adminUser) throws Exception;
}
