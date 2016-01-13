package org.pentaho.platform.spring.security.saml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.ISecurityHelper;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.api.repository2.unified.IBackingRepositoryLifecycleManager;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.proxy.api.IProxyFactory;
import org.pentaho.platform.proxy.impl.ProxyException;
import org.pentaho.platform.repository2.unified.jcr.JcrTenantUtils;
import org.pentaho.platform.spring.security.saml.responsewrapper.SamlOnRedirectUpdateSessionResponseWrapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class PentahoSamlAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  private static final Log logger = LogFactory.getLog( PentahoSamlAuthenticationSuccessHandler.class );

  public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication ) throws ServletException, IOException {

    try {

      if( authentication instanceof ExpiringUsernameAuthenticationToken ){
        // since no expiration date is passed, this token's behaviour is the same as UsernamePasswordAuthenticationToken
        // also, we would have a hard time supporting a supporting a saml-specific token like this
        // ExpiringUsernameAuthenticationToken in ss2-proxies / ss4-proxies
        //
        // http://docs.spring.io/spring-security-saml/docs/current/api/org/springframework/security/providers/ExpiringUsernameAuthenticationToken.html
        
        authentication = new UsernamePasswordAuthenticationToken( authentication.getPrincipal(),
            authentication.getCredentials(), authentication.getAuthorities() );

        SecurityContextHolder.getContext().setAuthentication( authentication );
      }


      // legacy spring ( i.e. non-osgi spring.framework ) SecurityContext storing

      IProxyFactory factory = PentahoSystem.get( IProxyFactory.class );

      Object securityContextProxy = factory.createProxy( SecurityContextHolder.getContext() );

      request.setAttribute( SPRING_SECURITY_CONTEXT_KEY, securityContextProxy );

      // pentaho auth storing

      logger.info( "synchronizing current IPentahoSession with SecurityContext" ); //$NON-NLS-1$

      IPentahoSession pentahoSession = PentahoSessionHolder.getSession();
      Assert.notNull( pentahoSession, "PentahoSessionHolder doesn't have a session" );
      pentahoSession.setAuthenticated( authentication.getName() );

      // Note: spring-security 2 expects an *array* of GrantedAuthorities ( ss4 uses a list )
      pentahoSession.setAttribute( IPentahoSession.SESSION_ROLES,
          proxyGrantedAuthorities( factory, authentication.getAuthorities() ) );

      // time to create this user's home folder
      createUserHomeFolder( authentication.getName() );

      super.onAuthenticationSuccess( request, new SamlOnRedirectUpdateSessionResponseWrapper( response, request, true,
          0, securityContextProxy, authentication ), authentication );

    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
    }
  }


  private Object proxyGrantedAuthorities( IProxyFactory factory, Collection<? extends GrantedAuthority> s4Authorities )
      throws ProxyException {

    List s2Authorities = new ArrayList();

    if( factory != null && s4Authorities != null ){

      for( GrantedAuthority s4Authority : s4Authorities ) {
        s2Authorities.add( factory.createProxy( s4Authority ) ); // proxying S4 GrantedAuthorities to s2 ones
      }
    }

    return proxyArray( s2Authorities );
  }

  private <T> T[] proxyArray( List<T> s2Authorities ) {

    T[] arr = ( T[] ) Array.newInstance( s2Authorities.get( 0 ).getClass() , s2Authorities.size() );

    for( int i = 0 ; i < s2Authorities.size() ; i++ ){
      arr[i] = s2Authorities.get( i );
    }

    return arr;
  }

  private void createUserHomeFolder( final String username ) {

    final ITenant tenantName =  JcrTenantUtils.getTenant( username, true );

    final ISecurityHelper securityHelper = PentahoSystem.get( ISecurityHelper.class ) != null ?
        PentahoSystem.get( ISecurityHelper.class ) : SecurityHelper.getInstance();
    final IBackingRepositoryLifecycleManager lifecycleManager = PentahoSystem.get( IBackingRepositoryLifecycleManager.class );

    if( tenantName == null || securityHelper == null || lifecycleManager == null ) {
      logger.error( "null " + ( tenantName == null ? "ITenant" :
          securityHelper == null ? "ISecurityHelper" : "IBackingRepositoryLifecycleManager" ) );
      return;
    }

    try {
      securityHelper.runAsSystem( new Callable<Void>() {
        @Override public Void call() throws Exception {
          // Execute new tenant with the tenant id from the logged in user
          lifecycleManager.newTenant( tenantName );
          return null;
        }
      } );
    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
    }

    try {
      securityHelper.runAsSystem( new Callable<Void>() {
        @Override public Void call() throws Exception {
          // Execute new tenant with the tenant id from the logged in user
          lifecycleManager.newUser( tenantName, username );
          return null;
        }
      } );
    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
    }

    try {
      // The newTenant() call should be executed as the system (or more correctly the tenantAdmin)
      securityHelper.runAsSystem( new Callable<Void>() {
        @Override public Void call() throws Exception {
          lifecycleManager.newTenant();
          return null;
        }
      } );
    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
    }

    try {
      // run as user to populate SecurityContextHolder and PentahoSessionHolder since Spring Security events are
      // fired before SecurityContextHolder is set
      securityHelper.runAsUser( username, new Callable<Void>() {
        @Override public Void call() throws Exception {
          lifecycleManager.newUser();
          return null;
        }
      } );

    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
    }
  }
}
