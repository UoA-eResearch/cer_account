package nz.ac.auckland.cer.account.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.LinkedList;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import nz.ac.auckland.cer.account.util.EmailUtil;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:AccountControllerTest-context.xml",
		"classpath:root-context.xml" })
@WebAppConfiguration
public class AccountControllerTest {

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private ProjectDatabaseDao projectDatabaseDao;
	@Autowired
	private AffiliationUtil affiliationUtilMock;
	@Autowired
	private ProjectDatabaseDao projectDao;
	@Autowired
	private EmailUtil eu;
	private MockMvc mockMvc;
	private Person person;
	private GreenMail smtpServer;

	@Before
	public void setup() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		this.person = new Person();
		person.setIsResearcher(true);
		person.setFullName("John Doe");
		person.setPreferredName("Johnie");
		person.setInstitution("University of Auckland");
		person.setDivision("Faculty of Science");
		person.setDepartment("Department of Physics");
		person.setPhone("09 123 2345 ext 123");
		person.setId(42);
		person.setEmail("john@doe.org");
		person.setInstitutionalRoleId(1);

		this.smtpServer = new GreenMail(ServerSetupTest.SMTP);
		this.smtpServer.start();
	}

	@After
	public void tearDown() throws Exception {
		smtpServer.stop();
	}

	@Test
	public void redirectOnGetIfNotRegistered() throws Exception {

		RequestBuilder rb = get("/view_account").requestAttr(
				"hasPersonRegistered", false);
		ResultActions ra = this.mockMvc.perform(rb);
		ra.andExpect(status().isFound()).andExpect(
				view().name("redirect:request_account_info"));
		verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
		verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
		assert(smtpServer.getReceivedMessages().length == 0);
	}

	@Test
	public void postEditAccountMissingInstitution() throws Exception {

		when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(
				new LinkedList<InstitutionalRole>());
		RequestBuilder rb = post("/edit_account").param("fullName", "Jane Doe")
				.param("institutionalRoleId", "42")
				.param("email", "test@test.org").param("phone", "12345");
		ResultActions ra = this.mockMvc.perform(rb);
		ra.andExpect(status().isOk())
				.andExpect(view().name("edit_account"))
				.andExpect(model().attributeErrorCount("formData", 1))
				.andExpect(
						model().attributeHasFieldErrors("formData",
								"institution"));
		verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
		verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
		assert(smtpServer.getReceivedMessages().length == 0);
	}

	@Test
	public void postEditAccountMissingOtherInstitution() throws Exception {

		when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(
				new LinkedList<InstitutionalRole>());
		RequestBuilder rb = post("/edit_account").param("fullName", "Jane Doe")
				.param("institutionalRoleId", "42")
				.param("email", "test@test.org").param("phone", "12345")
				.param("institution", "OTHER");
		ResultActions ra = this.mockMvc.perform(rb);
		ra.andExpect(status().isOk())
				.andExpect(view().name("edit_account"))
				.andExpect(model().attributeErrorCount("formData", 1))
				.andExpect(
						model().attributeHasFieldErrors("formData",
								"otherInstitution"));
		verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
		verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
		assert(smtpServer.getReceivedMessages().length == 0);
	}

	@DirtiesContext
	@Test
	public void postEditAccountResearcherSuccess() throws Exception {

		when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(
				new LinkedList<InstitutionalRole>());
		when(projectDatabaseDao.getInstitutionalRoleName(anyInt())).thenReturn("Boss");
		RequestBuilder rb = post("/edit_account")
				.requestAttr("person", this.person)
				.param("fullName", "Jane Doe")
				.param("preferredName", "Jane")
				.param("institutionalRoleId", "42")
				.param("email", "test@test.org").param("phone", "12345")
				.param("institution", "Some institution");
		ResultActions ra = this.mockMvc.perform(rb);
		ra.andExpect(status().isOk()).andExpect(view().name("view_account"))
				.andExpect(model().attributeHasNoErrors("formData"));
		verify(this.projectDao, times(1)).updateResearcher((Researcher) any());
		verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
		assert(smtpServer.getReceivedMessages().length == 1);
	    Message m = smtpServer.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getAccountChangeRequestEmailSubject().equals(m.getSubject()));
        assert (body.contains(person.getFullName() + " requested a change in his/her cluster account details"));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
        // TODO: More checks on email
	}

	@DirtiesContext
	@Test
	public void postEditAccountAdviserSuccess() throws Exception {

		this.person.setIsResearcher(false);
		when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(
				new LinkedList<InstitutionalRole>());
		when(projectDatabaseDao.getInstitutionalRoleName(anyInt())).thenReturn("Boss");
		RequestBuilder rb = post("/edit_account")
				.requestAttr("person", this.person)
				.param("fullName", "Jane Doe")
				.param("institutionalRoleId", "42")
				.param("email", "test@test.org").param("phone", "12345")
				.param("institution", "Some institution");
		ResultActions ra = this.mockMvc.perform(rb);
		ra.andExpect(status().isOk()).andExpect(view().name("view_account"))
				.andExpect(model().attributeHasNoErrors("formData"));
		verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
		verify(this.projectDao, times(1)).updateAdviser((Adviser) any());
		assert(smtpServer.getReceivedMessages().length == 1);
	    Message m = smtpServer.getReceivedMessages()[0];
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getAccountChangeRequestEmailSubject().equals(m.getSubject()));
        String body = GreenMailUtil.getBody(m);
        assert (body.contains(person.getFullName() + " requested a change in his/her cluster account details"));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
        // TODO: More checks on email
	}

	@DirtiesContext
    @Test
    public void requestAccountDeletion_Success() throws Exception {
        RequestBuilder rb = get("/confirm_account_deletion").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("account_deletion_retrieved"))
          .andExpect(model().attributeExists("message"));
        verify(projectDao, times(0)).updateResearcher((Researcher)any());
        verify(projectDao, times(0)).updateAdviser((Adviser)any());
		assert(smtpServer.getReceivedMessages().length == 1);
	    Message m = smtpServer.getReceivedMessages()[0];
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getAccountDeletionRequestEmailSubject().equals(m.getSubject()));
    }

	@DirtiesContext
    @Test
    public void requestAccountDeletion_Error() throws Exception {
		// Don't set request attribute to trigger error
		RequestBuilder rb = get("/confirm_account_deletion");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("account_deletion_retrieved"))
          .andExpect(model().attributeExists("error_message"));
        verify(projectDao, times(0)).updateResearcher((Researcher)any());
        verify(projectDao, times(0)).updateAdviser((Adviser)any());
		assert(smtpServer.getReceivedMessages().length == 0);
    }

}
