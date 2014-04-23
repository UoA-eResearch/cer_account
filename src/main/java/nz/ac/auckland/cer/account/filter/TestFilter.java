package nz.ac.auckland.cer.account.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class TestFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException,
			ServletException {

		try {
			HttpServletRequest request = (HttpServletRequest) req;
			request.setAttribute("eppn", "mfel395@auckland.ac.nz");
			request.setAttribute("shared-token", "f3eVuWzrD6afmQq3O3mQdUzokM0");
			request.setAttribute("Shib-Identity-Provider", "http://iam.auckland.ac.nz/idp");
			request.setAttribute("cn", "Joe Blogs");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		filterChain.doFilter(req, resp);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
	}

}
