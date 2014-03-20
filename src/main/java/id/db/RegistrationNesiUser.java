package id.db;

public class RegistrationNesiUser {

	private String tuakiriUniqueId;
	private String tuakiriSharedToken;
	private String identityProvider;
	private String email;

	public RegistrationNesiUser() { 
		
	}

	public RegistrationNesiUser(String tuakiriUniqueId, String tuakiriSharedToken, String identityProvider, String email) {
		this.tuakiriUniqueId = tuakiriUniqueId;
		this.tuakiriSharedToken = tuakiriSharedToken;
		this.identityProvider = identityProvider;
		this.email = email;
	}
	
	public String getTuakiriUniqueId() {
		return tuakiriUniqueId;
	}

	public void setTuakiriUniqueId(String tuakiriUniqueId) {
		this.tuakiriUniqueId = tuakiriUniqueId;
	}

	public String getTuakiriSharedToken() {
		return tuakiriSharedToken;
	}

	public void setTuakiriSharedToken(String tuakiriSharedToken) {
		this.tuakiriSharedToken = tuakiriSharedToken;
	}

	public String getIdentityProvider() {
		return identityProvider;
	}

	public void setIdentityProvider(String identityProvider) {
		this.identityProvider = identityProvider;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
