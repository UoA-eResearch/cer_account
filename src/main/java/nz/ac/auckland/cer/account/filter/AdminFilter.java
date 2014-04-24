package nz.ac.auckland.cer.account.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Researcher;

public class AdminFilter implements Filter {

	private ProjectDatabaseDao projectDatabaseDao;
	private Logger log = Logger.getLogger(AdminFilter.class.getName());
	
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc) 
		throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			String sharedToken = (String) request.getAttribute("shared-token");
			Researcher r = this.projectDatabaseDao.getResearcherForTuakiriSharedToken(sharedToken);
			Adviser a = this.projectDatabaseDao.getAdviserForTuakiriSharedToken(sharedToken);
			boolean isUserAdviser = (a == null) ? false : true;
			boolean isUserResearcher = (r == null) ? false : true;
			boolean hasUserRegistered = (a == null && r == null) ? false : true;
			request.setAttribute("hasUserRegistered", hasUserRegistered);
			request.setAttribute("isUserAdviser", isUserAdviser);
			request.setAttribute("isUserResearcher", isUserResearcher);
			request.setAttribute("adviser", a);
			request.setAttribute("researcher", r);
		} catch (final Exception e) {
			log.error(e);
			return;
		}
		fc.doFilter(req, resp);
	}

	public void init(FilterConfig fc) throws ServletException {
	}

	public void destroy() {
	}

	public void setProjectDatabaseDao(ProjectDatabaseDao projectDatabaseDao) {
		this.projectDatabaseDao = projectDatabaseDao;
	}

}
