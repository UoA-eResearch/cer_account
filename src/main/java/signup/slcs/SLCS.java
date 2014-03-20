package signup.slcs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class SLCS {

	private String slcsMapUrl;

	/**
	 * Download the SLCS IdP DN map, generate a Map from it and return the
	 * generated map
	 * 
	 * @return Map
	 * @throws Exception
	 */
	public Map<String, String> getIdpDnMap() throws Exception {
		Map<String, String> m = new HashMap<String, String>();
		URL url = new URL(this.slcsMapUrl);
		URLConnection c = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
		if (br != null) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(",");
				if (tokens.length != 2) {
					throw new Exception("Bad SLCS URL, or format error in SLCS Idp Map " + this.slcsMapUrl);
				}
				m.put(tokens[0].trim(), tokens[1].trim());
			}
		}
		return m;
	}

	/**
	 * Look up a DN
	 * 
	 * @param idpUrl
	 * @return DN that maps to the specified IdP
	 * @throws Exception
	 */
	public String getDn(String idpUrl) throws Exception {
		Map<String, String> m = this.getIdpDnMap();
		if (m.containsKey(idpUrl)) {
			return m.get(idpUrl);
		} else {
			return null;
		}
	}

	/**
	 * Create a DN of a user as the DN of a credential generated by SLCS
	 * 
	 * @param idpUrl
	 * @param fullName
	 * @param token
	 * @return user DN
	 * @throws Exception
	 */
	public String createUserDn(String idpUrl, String fullName, String token) throws Exception {
		String baseDn = this.getDn(idpUrl);
		if (idpUrl.contains("iam.auckland.ac.nz/idp")) {
			// Handle alternative IdP URL for Auckland
			baseDn = "/DC=nz/DC=org/DC=bestgrid/DC=slcs/O=The University of Auckland";
		}
		if (baseDn == null || baseDn.isEmpty()) {
			throw new Exception("Failed to create user DN: idpUrl not found in ACL map.");
		}
		return baseDn + "/CN=" + fullName + " " + token;
	}

	public void setSlcsMapUrl(String slcsMapUrl) {
		this.slcsMapUrl = slcsMapUrl;
	}
}