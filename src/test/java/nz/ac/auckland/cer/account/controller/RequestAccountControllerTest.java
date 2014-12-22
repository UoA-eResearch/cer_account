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
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.AffiliationUtil;

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
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:RequestAccountControllerTest-context.xml", "classpath:root-context.xml" })
@WebAppConfiguration
public class RequestAccountControllerTest {

    @Autowired private WebApplicationContext wac;
    @Autowired private ProjectDatabaseDao projectDatabaseDao;
    @Autowired private AffiliationUtil affiliationUtilMock;
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;
    private GreenMail smtpServer;
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {

        this.smtpServer = new GreenMail(ServerSetupTest.SMTP);
        this.smtpServer.start();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @After
    public void tearDown() throws Exception {
        smtpServer.stop();        
    }
    
    @DirtiesContext
    @Test
    public void postAccountRequestResearcherSuccess() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account_success"))
                .andExpect(model().attributeHasNoErrors("formData"));
        // .andDo(print())
        verify(this.projectDao, times(1)).createResearcher((Researcher) any());
        //verify(this.emailUtil, times(0)).sendOtherAffiliationEmail("Test Inst", null, null);
        //verify(this.emailUtil, times(1)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

    @DirtiesContext
    @Test
    public void postAccountRequestResearcherOtherInstitutionSuccess() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe").param("institution", "other")
                .param("otherInstitution", "Test Inst").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account_success"))
                .andExpect(model().attributeHasNoErrors("formData"));
        // .andDo(print())
        verify(this.projectDao, times(1)).createResearcher((Researcher) any());
        //verify(this.emailUtil, times(1)).sendOtherAffiliationEmail("Test Inst", null, null);
        //verify(this.emailUtil, times(1)).sendAccountRequestEmail((AccountRequest) any(), anyInt(), anyString());
    }

    @Test
    public void postAccountRequestMissingFullName() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("phone", "12345").param("email", "test@test.org")
                .param("institution", "Test Inst").param("institutionalRoleId", "42");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "fullName"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        assert(smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void postAccountRequestMissingEmail() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Inst").param("institutionalRoleId", "42").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "email"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        assert(smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void postAccountRequestMissingInstitution() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe").param("institutionalRoleId", "42")
                .param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "institution"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        assert(smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void postAccountRequestMissingOtherInstitution() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe").param("institution", "Other")
                .param("institutionalRoleId", "42").param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "otherInstitution"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        assert(smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void postAccountRequestMissingInstitutionalRoleId() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("email", "test@test.org").param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "institutionalRoleId"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        assert(smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void postAccountRequestInvalidEmail() throws Exception {

        when(projectDatabaseDao.getInstitutionalRoles()).thenReturn(new LinkedList<InstitutionalRole>());
        RequestBuilder rb = post("/request_account").param("fullName", "Jane Doe")
                .param("institution", "Test Institution").param("institutionalRoleId", "42").param("email", "test")
                .param("phone", "12345");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_account"))
                .andExpect(model().attributeErrorCount("formData", 1))
                .andExpect(model().attributeHasFieldErrors("formData", "email"));
        verify(this.projectDao, times(0)).createResearcher((Researcher) any());
        assert(smtpServer.getReceivedMessages().length == 0);
    }

}
