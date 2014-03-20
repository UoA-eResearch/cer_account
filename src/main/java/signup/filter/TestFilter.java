package signup.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class TestFilter implements Filter {

	private String remoteUser = "mfel395@auckland.ac.nz";
	private String sharedToken = "f3eVuWzrD6afmQq3O3mQdUzokM0";
	private String shibIdentityProvider = "http://iam.auckland.ac.nz/idp";
	private String cn = "Joe Blogs";

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException,
			ServletException {

		try {
			HttpServletRequest request = (HttpServletRequest) req;
			request.setAttribute("X-Proxy-REMOTE-USER", this.remoteUser);
			request.setAttribute("X-Proxy-TUAKIRI-TOKEN", this.sharedToken);
			request.setAttribute("X-Proxy-IDP-URL", this.shibIdentityProvider);
			request.setAttribute("X-Proxy-CN", this.cn);
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
