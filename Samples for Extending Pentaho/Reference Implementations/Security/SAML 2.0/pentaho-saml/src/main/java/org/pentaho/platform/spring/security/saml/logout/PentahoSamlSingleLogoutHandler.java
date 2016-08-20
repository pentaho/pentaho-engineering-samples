package org.pentaho.platform.spring.security.saml.logout;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PentahoSamlSingleLogoutHandler implements LogoutHandler {

  private static Logger logger = LoggerFactory.getLogger( PentahoSamlSingleLogoutHandler.class );

  public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

  @Override public void logout( HttpServletRequest request, HttpServletResponse response, Authentication auth ) {

    if( request != null && request.getAttribute( SPRING_SECURITY_CONTEXT_KEY ) != null ){
      logger.info( "Removing SPRING_SECURITY_CONTEXT_KEY from HttpServletRequest" );
      request.removeAttribute( SPRING_SECURITY_CONTEXT_KEY );
    }

    IPentahoSession pentahoSession = PentahoSessionHolder.getSession();

    if( pentahoSession != null ) {

      if( pentahoSession.getAttribute( IPentahoSession.SESSION_ROLES ) != null ) {
        logger.info( "Removing IPentahoSession.SESSION_ROLES from IPentahoSession" );
        pentahoSession.removeAttribute( IPentahoSession.SESSION_ROLES );
      }

      logger.info( "Marking IPentahoSession as *not* authenticated" );
      pentahoSession.setNotAuthenticated();
    }
  }
}
