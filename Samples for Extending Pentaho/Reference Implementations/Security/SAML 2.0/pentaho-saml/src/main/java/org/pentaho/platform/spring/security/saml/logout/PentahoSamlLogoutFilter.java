package org.pentaho.platform.spring.security.saml.logout;

import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.pentaho.platform.proxy.impl.ProxyException;
import org.pentaho.platform.spring.security.saml.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class PentahoSamlLogoutFilter extends SAMLLogoutFilter {

  private static final Logger logger = LoggerFactory.getLogger( PentahoSamlLogoutFilter.class );

  /**
   * Local Logout: terminates only the local session and doesn't affect neither session at IDP, nor sessions at
   * other SPs where user logged in using single sign-on.
   * <p />
   * Global Logout (a.k.a. Single Logout): terminates both session at the current SP, the IDP session and sessions
   * at other SPs connected to the same IDP session. Global logout (a.k.a. Single Logout) can be initialized from
   * any of the participating SPs or from the IDP itself.
   *
   * Important Note: not all IdPs support Global Logout (a.k.a. Single Logout)! Please check with your chosen IdP
   * if this is a supported feature.
   * Examples: SSOCircle.com supports Single Logout; Okta.com does not (http://developer.okta.com/docs/guides/oan_guidance.html)
   */
  private boolean useGlobalLogoutStrategy; /* defaults to 'false' */

  public PentahoSamlLogoutFilter( String successUrl, LogoutHandler[] localHandler, LogoutHandler[] globalHandlers ) {
    super( successUrl, localHandler, globalHandlers );
  }

  public PentahoSamlLogoutFilter( LogoutSuccessHandler logoutSuccessHandler, LogoutHandler[] localHandler, LogoutHandler[] globalHandlers ) {
    super( logoutSuccessHandler, localHandler, globalHandlers );
  }

  @Override
  public void processLogout(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
      IOException, ServletException {

    if ( requiresLogout( request, response ) ) {

      if( isUseGlobalLogoutStrategy() && !idpContainsGlobalLogoutEndpoint( request, response ) ){
        logger.error( "User misconfiguration: SAML's 'use.global.logout.strategy' is set to 'true', but the selected "
            + "IdP does not declare a Global Logout endpoint. We will switch to a 'Local logout' behaviour." );
        setUseGlobalLogoutStrategy( false );
      }

      try {

        Authentication auth = Utils.getAuthenticationFromRequest( request );

        if( auth != null ) {
          SecurityContextHolder.getContext().setAuthentication( auth );
        }

      } catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException | ProxyException e ) {
        logger.error( e.getMessage(), e );
      }
    }

    super.processLogout( request, response, chain );
  }

  /**
   * Set in the bundle's .cfg file the 'use.global.logout.strategy' property to 'true'
   * ( otherwise a local logout strategy will be applied )
   *
   * This overrides spring-security-saml's default mechanism, which was calculating by checking the existence
   * of a 'local' request parameter( /Logout?local=true )
   */
  @Override
  public boolean isGlobalLogout( HttpServletRequest request, Authentication auth ) {
    return isUseGlobalLogoutStrategy();
  }

  public boolean isUseGlobalLogoutStrategy() { return useGlobalLogoutStrategy; }

  public void setUseGlobalLogoutStrategy( boolean useGlobalLogoutStrategy ) {
    this.useGlobalLogoutStrategy = useGlobalLogoutStrategy;
  }

  private boolean idpContainsGlobalLogoutEndpoint( HttpServletRequest request, HttpServletResponse response ) {

    try {

      SAMLMessageContext ctx = contextProvider.getLocalAndPeerEntity(request, response);
      String binding = SAMLUtil.getLogoutBinding( (IDPSSODescriptor) ctx.getPeerEntityRoleMetadata() ,
          (SPSSODescriptor) ctx.getLocalEntityRoleMetadata() );

      return ( binding != null && !binding.isEmpty() );

    } catch ( MetadataProviderException e ) {
      logger.error( e.getMessage(), e );
    }

    return false;
  }
}
