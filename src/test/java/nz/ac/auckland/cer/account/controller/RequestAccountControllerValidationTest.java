package nz.ac.auckland.cer.account.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.LinkedList;

import static org.mockito.Mockito.*;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.util.AffiliationUtil;

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


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/RequestAccountControllerValidationTest-context.xml", "/root-context.xml"})
@WebAppConfiguration
public class RequestAccountControllerValidationTest {

	  @Autowired
	  private WebApplicationContext wac;
	  @Autowired
	  private ProjectDatabaseDao projectDatabaseDao;
	  @Autowired
	  private AffiliationUtil affiliationUtilMock;
	  
	  private MockMvc mockMvc;

	  @Before
	  public void setup() throws Exception {
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	  }

	  @Test
	  public void postAccountRequestSuccess() throws Exception {
		  when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount").param("institution", "Test Institution")
	        .param("institutionalRoleId", "42").param("email", "test@test.org").param("phone", "12345"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("accountrequestsuccess"))
	        .andExpect(model().attributeHasNoErrors("requestaccount"));
	        //.andDo(print())
	  }

	  @Test
	  public void postAccountRequestMissingEmail() throws Exception {
		  when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount").param("institution", "Test Institution")
	        .param("institutionalRoleId", "42").param("phone", "12345"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("requestaccount"))
	        .andExpect(model().attributeErrorCount("requestaccount", 1))
	        .andExpect(model().attributeHasFieldErrors("requestaccount", "email"));
	  }

	  @Test
	  public void postAccountRequestMissingInstitution() throws Exception {
		  when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount")
	        .param("institutionalRoleId", "42").param("email", "test@test.org").param("phone", "12345"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("requestaccount"))
	        .andExpect(model().attributeErrorCount("requestaccount", 1))
	        .andExpect(model().attributeHasFieldErrors("requestaccount", "institution"));
	  }

	  @Test
	  public void postAccountRequestMissingInstitutionalRoleId() throws Exception {
		  when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount").param("institution", "Test Institution")
	        .param("email", "test@test.org").param("phone", "12345"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("requestaccount"))
	        .andExpect(model().attributeErrorCount("requestaccount", 1))
	        .andExpect(model().attributeHasFieldErrors("requestaccount", "institutionalRoleId"));
	  }

	  @Test
	  public void postAccountRequestInvalidEmail() throws Exception {
		  when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new InstitutionalRole[0]);
		  when(affiliationUtilMock.getAffiliationStrings((Affiliation[]) anyObject())).thenReturn(new LinkedList<String>());
	      this.mockMvc.perform(post("/requestaccount").param("institution", "Test Institution")
	        .param("institutionalRoleId", "42").param("email", "test").param("phone", "12345"))
	        .andExpect(status().isOk())
	        .andExpect(view().name("requestaccount"))
	        .andExpect(model().attributeErrorCount("requestaccount", 1))
	        .andExpect(model().attributeHasFieldErrors("requestaccount", "email"));
	  }

}
