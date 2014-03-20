package pm.pojo;

public class ProjectWrapper {

	private Project project;
	private ProjectFacility[] projectFacilities;

	public ProjectWrapper() {
		
	}
	
	public ProjectWrapper(Project project, ProjectFacility[] pfs) {
		this.project = project;
		this.projectFacilities = pfs;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ProjectFacility[] getProjectFacilities() {
		return projectFacilities;
	}

	public void setProjectFacilities(ProjectFacility[] projectFacilities) {
		this.projectFacilities = projectFacilities;
	}

}
