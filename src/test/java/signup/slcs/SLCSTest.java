package signup.slcs;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/SLCSTest-context.xml"})
public class SLCSTest {

	@Autowired
	private SLCS slcs;
	
	@Test
	public void testGetIdpDnMap() throws Exception {
		Map<String, String> m = this.slcs.getIdpDnMap();
		assertNotNull(m);
		assertTrue(m.size() > 0);
	}
	
	@Test
	public void testGetDn() throws Exception {
		Map<String, String> m = this.slcs.getIdpDnMap();		
	    assertTrue(m.containsKey("http://iam.auckland.ac.nz/idp"));
	    assertEquals("/DC=nz/DC=org/DC=bestgrid/DC=slcs/O=The University of Auckland", m.get("http://iam.auckland.ac.nz/idp"));
	    assertNull(m.get("invalid_url"));
	}

	@Test(expected = UnknownHostException.class)
	@DirtiesContext
	public void testBadHost() throws Exception {
		this.slcs.setSlcsMapUrl("http://some.bogus.host/idp");
		this.slcs.getIdpDnMap();
	}

	@Test(expected = Exception.class)
	@DirtiesContext
	public void testBadUrl() throws Exception {
		this.slcs.setSlcsMapUrl("http://iam.auckland.ac.nz/does/not/exist");
		this.slcs.getIdpDnMap();
	}

	@Test
	public void testCreateUser() throws Exception {
	    String idpUrl = "http://iam.auckland.ac.nz/idp";
	    String baseDn = "/DC=nz/DC=org/DC=bestgrid/DC=slcs/O=The University of Auckland";
	    String fullName = "Test User";
	    String token = "1234";
	    String expectedDn = baseDn + "/CN=" + fullName + " " + token;
	    assertEquals(expectedDn, this.slcs.createUserDn(idpUrl, fullName, token));
	}
	
	@Test(expected = Exception.class)
	public void testCreateUserDnUnknownIdp() throws Exception {
		this.slcs.createUserDn("http://iam.auckland.ac.nz/does/not/exist", "Test User", "1234");
	}
	
}
