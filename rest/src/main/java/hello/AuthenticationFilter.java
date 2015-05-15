package hello;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		//we must obtain the user from the context.
		//CatalinaRequestAuthenticator saves the security context as request attribute
		try{
			SecurityContextHolder.getContext().setAuthentication(
					KeycloakAuthentication.KeycloakAuthenticationBuilder.buildAuthentication((HttpServletRequest)request));
			chain.doFilter(request, response);
		} finally {
			SecurityContextHolder.clearContext();
		}
	}

	
	@Override
	public void destroy() {
	}

}
