package org.pentaho.platform.spring.security.saml;

import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.proxy.api.IProxyFactory;
import org.pentaho.platform.proxy.impl.ProxyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Utils {

  public final static String SPRING_2_SECURITY_CTX_KEY = "SPRING_SECURITY_CONTEXT";

  /**
   * roles considered to be the basic ones and that should be immediately assigned to any SAML authenticated user;
   */
  static GrantedAuthority defaultRole;
  
  public static Authentication getAuthenticationFromRequest( ServletRequest request ) throws NoSuchMethodException,
      InvocationTargetException, IllegalAccessException, ProxyException {

    if ( request != null && request instanceof HttpServletRequest
        && ( (HttpServletRequest) request ).getSession( false ) != null
        && ( (HttpServletRequest) request ).getSession( false ).getAttribute( SPRING_2_SECURITY_CTX_KEY ) != null ) {

      // step 1 - get spring 2 SecurityContext object stored in the HttpRequest
      Object s2SecurityContextObj =
          ( (HttpServletRequest) request ).getSession( false ).getAttribute( SPRING_2_SECURITY_CTX_KEY );

        // step 2 - grab spring 2 SecurityContext's getAuthentication() method
      Method getAuthenticationMethod = s2SecurityContextObj.getClass().getMethod( "getAuthentication" /* no-args */ );

      if ( getAuthenticationMethod != null ) {

        // to ensure no IllegalAccessException occurs
        getAuthenticationMethod.setAccessible( true );

        // step 3 - get spring 2 Authentication object
        Object s2AuthenticationObj = getAuthenticationMethod.invoke( s2SecurityContextObj );

        // step 4 - proxy wrap spring 2 Authentication object into a spring 4 one
        IProxyFactory factory = PentahoSystem.get( IProxyFactory.class );
        Object s4AuthenticationProxy = factory.createProxy( s2AuthenticationObj );

        if ( s4AuthenticationProxy != null && s4AuthenticationProxy instanceof Authentication ) {
          return (Authentication) s4AuthenticationProxy;
        }
      }
    }

    return null;
  }

  public static Map<String, UserDetails> getUserMap() {
    Map<String, String> props = new HashMap();
    props.put( "name", "saml.user.map" );
    return PentahoSystem.get( Map.class, null, props );
  }
  
  public static GrantedAuthority getDefaultRole() {
    if ( defaultRole == null ) {
      String defaultRoleAsString = PentahoSystem.get( String.class, "defaultRole", null );
      if ( defaultRoleAsString != null && defaultRoleAsString.length() > 0 ) {
        defaultRole = new SimpleGrantedAuthority( defaultRoleAsString );
      }
    }
    return defaultRole;
  }
}
