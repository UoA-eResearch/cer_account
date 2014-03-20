package signup.pojo;

public class Survey {

	private String motivation;
	private String currentEnv;
	private Limitations limitations;

	public String getMotivation() {
		return motivation;
	}

	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}

	public Limitations getLimitations() {
		return limitations;
	}

	public void setLimitations(Limitations limitations) {
		this.limitations = limitations;
	}

	public String getCurrentEnv() {
		return currentEnv;
	}

	public void setCurrentEnv(String currentEnv) {
		this.currentEnv = currentEnv;
	}

}
