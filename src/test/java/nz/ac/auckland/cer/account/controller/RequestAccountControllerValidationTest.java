package nz.ac.auckland.cer.account.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import nz.ac.auckland.cer.account.pojo.AccountRequest;
import nz.ac.auckland.cer.account.util.EmailUtil;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.Person;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/RequestAccountControllerValidationTest-context.xml", "/root-context.xml" })
@WebAppConfiguration
public class RequestAccountControllerValidationTest {

    @Autowired private WebApplicationContext wac;
    @Autowired private ProjectDatabaseDao projectDatabaseDao;
    @Autowired private AffiliationUtil affiliationUtilMock;
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @DirtiesContext
    @Test
    public void postAccountRequestResearcherSuccess() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        when(affiliationUtilMock.getInstitutionFromAffiliationString("Test Institution"))
                .thenReturn("Test Institution");
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account_success"))
                .andExpect(model().attributeHasNoErrors("requestaccount"));
        // .andDo(print())
        verify(this.projectDao, times(0)).createAdviser((Adviser) any());
        verify(this.projectDao, times(1)).createResearcher((Researcher) any());
        verify(this.emailUtil, times(1)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

    @DirtiesContext
    @Test
    public void postAccountRequestAdviserSuccess() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        when(affiliationUtilMock.getInstitutionFromAffiliationString("Test Institution"))
                .thenReturn("Test Institution");
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345").param("isNesiStaff", "true");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account_success"))
                .andExpect(model().attributeHasNoErrors("requestaccount"));
        // .andDo(print())
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        verify(this.projectDao, times(1)).createAdviser((Adviser) any());
        verify(this.emailUtil, times(1)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

    @Test
    public void postAccountRequestMissingFullName() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        when(affiliationUtilMock.getInstitutionFromAffiliationString("Test Institution"))
                .thenReturn("Test Institution");
        RequestBuilder rb = post("/request_account").param("phone", "12345").param("email", "test@test.org")
                .param("institution", "Test Institution").param("institutionalRoleId", "42");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("requestaccount", 1))
                .andExpect(model().attributeHasFieldErrors("requestaccount", "fullName"));
    }

    @Test
    public void postAccountRequestMissingEmail() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        when(affiliationUtilMock.getInstitutionFromAffiliationString("Test Institution"))
                .thenReturn("Test Institution");
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("institutionalRoleId", "42").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("requestaccount", 1))
                .andExpect(model().attributeHasFieldErrors("requestaccount", "email"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        verify(this.projectDao, times(0)).createAdviser((Adviser) any());
        verify(this.emailUtil, times(0)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

    @Test
    public void postAccountRequestMissingInstitution() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("requestaccount", 1))
                .andExpect(model().attributeHasFieldErrors("requestaccount", "institution"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        verify(this.projectDao, times(0)).createAdviser((Adviser) any());
        verify(this.emailUtil, times(0)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

    @Test
    public void postAccountRequestMissingInstitutionalRoleId() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("requestaccount", 1))
                .andExpect(model().attributeHasFieldErrors("requestaccount", "institutionalRoleId"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        verify(this.projectDao, times(0)).createAdviser((Adviser) any());
        verify(this.emailUtil, times(0)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

    @Test
    public void postAccountRequestInvalidEmail() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        when(affiliationUtilMock.getInstitutionFromAffiliationString("Test Institution"))
                .thenReturn("Test Institution");
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("institutionalRoleId", "42").param("email", "test")
                .param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("requestaccount", 1))
                .andExpect(model().attributeHasFieldErrors("requestaccount", "email"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        verify(this.projectDao, times(0)).createAdviser((Adviser) any());
        verify(this.emailUtil, times(0)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

}
