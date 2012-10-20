package org.siraya.rent.filter;

import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
public class Authorizer implements SecurityContext {
    private static Logger logger = LoggerFactory.getLogger(Authorizer.class);
    private UserAuthorizeData user;
    private Principal principal;

	
    public Authorizer(final UserAuthorizeData user) {
        this.user = user;
        if (user.getUserId() == null){
        	this.user.setUserId("anonymous");
        }
        this.principal = new Principal() {

            public String getName() {
                return user.getUserId();
            }
        };
    }

    public Principal getUserPrincipal() {
        return this.principal;
    }

    public boolean isUserInRole(String role) {
    	int checkRole = UserRole.getRoleId(role);
    	int userRole = this.user.getRoleId();
    	return checkRole <= userRole;

    }

    public boolean isSecure() {
        return "https".equals(user.request.getRequestUri().getScheme());
    }

    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}
