package signup.pojo;

public class ProjectRequest {

	private String choice;
	private String projectCode;
	private String projectTitle;
	private String projectDescription;
	private Boolean askForSuperviser;
	private String superviserName;
	private String superviserEmail;
	private String superviserPhone;

	public String getSuperviserName() {
		return superviserName;
	}

	public void setSuperviserName(String superviserName) {
		this.superviserName = superviserName;
	}

	public String getSuperviserEmail() {
		return superviserEmail;
	}

	public void setSuperviserEmail(String superviserEmail) {
		this.superviserEmail = superviserEmail;
	}

	public String getSuperviserPhone() {
		return superviserPhone;
	}

	public void setSuperviserPhone(String superviserPhone) {
		this.superviserPhone = superviserPhone;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public Boolean getAskForSuperviser() {
		return askForSuperviser;
	}

	public void setAskForSuperviser(Boolean askForSuperviser) {
		this.askForSuperviser = askForSuperviser;
	}

}
