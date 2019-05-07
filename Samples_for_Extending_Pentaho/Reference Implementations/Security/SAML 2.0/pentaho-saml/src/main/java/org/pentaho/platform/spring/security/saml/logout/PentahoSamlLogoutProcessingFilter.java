package org.pentaho.platform.spring.security.saml.logout;

import org.pentaho.platform.proxy.impl.ProxyException;
import org.pentaho.platform.spring.security.saml.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class PentahoSamlLogoutProcessingFilter extends SAMLLogoutProcessingFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger( PentahoSamlLogoutProcessingFilter.class );

  private boolean requireProxyWrapping = true;

  public PentahoSamlLogoutProcessingFilter( String logoutSuccessUrl, LogoutHandler... handlers ) {
    super( logoutSuccessUrl, handlers );
  }

  public PentahoSamlLogoutProcessingFilter( LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers ) {
    super( logoutSuccessHandler, handlers );
  }

  @Override
  public void processLogout( HttpServletRequest request, HttpServletResponse response, FilterChain chain )
    throws IOException, ServletException {

    if ( requiresLogout( request, response ) ) {

      try {

        Authentication auth = Utils.getAuthenticationFromRequest( request, isRequireProxyWrapping() );

        if ( auth != null ) {
          SecurityContextHolder.getContext().setAuthentication( auth );
        }

      } catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException | ProxyException e ) {
        LOGGER.error( e.getMessage(), e );
      }
    }

    super.processLogout( request, response, chain );

  }

  public void setRequireProxyWrapping( boolean requireProxyWrapping ) {
    this.requireProxyWrapping = requireProxyWrapping;
  }

  public boolean isRequireProxyWrapping() {
    return requireProxyWrapping;
  }

}
