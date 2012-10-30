package org.siraya.rent.filter;

import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import org.siraya.rent.pojo.Session;
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
    	Session session = this.user.getSession();
    	
    	if (session == null) {
    		logger.debug("session is null no allow role "+role);
    		return false;
    	}
    	boolean ret =  session.isUserInRole(checkRole);
    	if (ret == false) {
    		logger.info("user "+this.user.getUserId()+" not in role "+role);
    	}
    	return ret;
    }

    public boolean isSecure() {
        return "https".equals(user.getRequest().getRequestUri().getScheme());
    }

    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}
