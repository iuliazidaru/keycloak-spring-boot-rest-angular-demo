package hello;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Wrapper for Spring authentication for keycloak data
 * @author iulia
 *
 */
@SuppressWarnings("serial")
public class KeycloakAuthentication implements Authentication{
	private boolean authenticated;
	private String keycloackId;
	
	
	public KeycloakAuthentication(String keycloackId) {
		super();
		this.authenticated = true;
		this.keycloackId = keycloackId;
	}

	public KeycloakAuthentication(boolean authenticated) {
		super();
		this.authenticated = authenticated;
	}



	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return keycloackId;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
		
	}
	
	public static class KeycloakAuthenticationBuilder {
		
		public static KeycloakAuthentication buildAuthentication(HttpServletRequest request) {
			if(request.getUserPrincipal() != null){
				return new KeycloakAuthentication(request.getUserPrincipal().getName());
			} else {
				return new KeycloakAuthentication(false);
			}
		}
	}

	@Override
	public String getName() {
		return keycloackId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

}
