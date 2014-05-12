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
@ContextConfiguration(locations = { "classpath:AccountControllerTest-context.xml", "classpath:root-context.xml" })
@WebAppConfiguration
public class AccountControllerTest {

    @Autowired private WebApplicationContext wac;
    @Autowired private ProjectDatabaseDao projectDatabaseDao;
    @Autowired private AffiliationUtil affiliationUtilMock;
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;
    private MockMvc mockMvc;
    private Person person;

    @Before
    public void setup() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        this.person = new Person();
        person.setIsResearcher(true);
        person.setFullName("Jane Doe");
        person.setId(42);
        person.setEmail("jane@doe.org");
        person.setInstitutionalRoleId(1);
    }

    @Test
    public void redirectOnGetIfNotRegistered() throws Exception {

        RequestBuilder rb = get("/view_account").requestAttr("hasPersonRegistered", false);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isFound()).andExpect(view().name("redirect:request_account_info"));
        verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
        verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
        verify(this.emailUtil, times(0)).sendAccountDetailsChangeRequestRequestEmail((Person) any(), (Person) any());
    }

    @DirtiesContext
    @Test
    public void postEditAccountResearcherSuccess() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        RequestBuilder rb = post("/edit_account").requestAttr("person", this.person).param("fullName", "Jane Doe")
                .param("institutionalRoleId", "42").param("email", "test@test.org").param("phone", "12345")
                .param("institution", "Some institution");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("view_account"))
                .andExpect(model().attributeHasNoErrors("formData"));
        verify(this.projectDao, times(1)).updateResearcher((Researcher) any());
        verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
        verify(this.emailUtil, times(1)).sendAccountDetailsChangeRequestRequestEmail((Person) any(), (Person) any());
    }

    @DirtiesContext
    @Test
    public void postEditAccountAdviserSuccess() throws Exception {

        this.person.setIsResearcher(false);
        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        RequestBuilder rb = post("/edit_account").requestAttr("person", this.person).param("fullName", "Jane Doe")
                .param("institutionalRoleId", "42").param("email", "test@test.org").param("phone", "12345")
                .param("institution", "Some institution");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("view_account"))
                .andExpect(model().attributeHasNoErrors("formData"));
        verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
        verify(this.projectDao, times(1)).updateAdviser((Adviser) any());
        verify(this.emailUtil, times(1)).sendAccountDetailsChangeRequestRequestEmail((Person) any(), (Person) any());
    }

    @Test
    public void postEditAccountMissingInstitution() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        RequestBuilder rb = post("/edit_account").param("fullName", "Jane Doe").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("edit_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "institution"));
        verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
        verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
        verify(this.emailUtil, times(0)).sendAccountDetailsChangeRequestRequestEmail((Person) any(), (Person) any());
    }

    @Test
    public void postEditAccountMissingOtherInstitution() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        when(affiliationUtilMock.getAffiliationStrings((List<Affiliation>) anyObject())).thenReturn(
                new LinkedList<String>());
        RequestBuilder rb = post("/edit_account").param("fullName", "Jane Doe").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345").param("institution", "OTHER");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("edit_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "otherInstitution"));
        verify(this.projectDao, times(0)).updateResearcher((Researcher) any());
        verify(this.projectDao, times(0)).updateAdviser((Adviser) any());
        verify(this.emailUtil, times(0)).sendAccountDetailsChangeRequestRequestEmail((Person) any(), (Person) any());
    }

}
