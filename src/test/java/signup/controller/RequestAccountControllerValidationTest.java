package signup.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.LinkedList;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pm.pojo.Affiliation;
import pm.pojo.InstitutionalRole;
import pm.util.AffiliationUtil;
import pm.dao.ProjectDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/RequestAccountControllerValidationTest-context.xml", "/root-context.xml"})
@WebAppConfiguration
public class RequestAccountControllerValidationTest {

	  @Autowired
	  private WebApplicationContext wac;
	  @Autowired
	  private ProjectDao projectDaoMock;
	  @Autowired
	  private AffiliationUtil affiliationUtilMock;
	  
	  private MockMvc mockMvc;

	  @Before
	  public void setup() throws Exception {
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	  }

	  @Test
	  public void postAccountRequestSuccess() throws Exception {
		  when(projectDaoMock.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount").param("institution", "Test Institution")
	        .param("institutionalRoleId", "42").param("email", "test@test.org"))
	        .andExpect(status().isMovedTemporarily())
	        .andExpect(redirectedUrl("requestproject"))
	        .andExpect(model().attributeHasNoErrors("requestaccount"));
	        //.andDo(print())
	  }

	  @Test
	  public void postAccountRequestMissingEmail() throws Exception {
		  when(projectDaoMock.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount").param("institution", "Test Institution")
	        .param("institutionalRoleId", "42"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("requestaccount"))
	        .andExpect(model().attributeHasFieldErrors("requestaccount", "email"));
	  }

	  @Test
	  public void postAccountRequestInvalidEmail() throws Exception {
		  when(projectDaoMock.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount").param("institution", "Test Institution")
	        .param("institutionalRoleId", "42").param("email", "test"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("requestaccount"))
	        .andExpect(model().attributeHasFieldErrors("requestaccount", "email"));
	  }

	  @Test
	  public void postAccountRequest() throws Exception {
		  when(projectDaoMock.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("requestaccount"))
	        .andExpect(model().attributeHasFieldErrors("requestaccount",
	        	"institution", "institutionalRoleId", "email"));
	  }
}
