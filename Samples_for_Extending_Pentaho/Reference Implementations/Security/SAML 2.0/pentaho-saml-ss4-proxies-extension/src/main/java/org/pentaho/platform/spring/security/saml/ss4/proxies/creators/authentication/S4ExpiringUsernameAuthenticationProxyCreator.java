package org.pentaho.platform.spring.security.saml.ss4.proxies.creators.authentication;

import org.pentaho.platform.proxy.api.IProxyCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class S4ExpiringUsernameAuthenticationProxyCreator implements IProxyCreator<Authentication> {

  private Logger logger = LoggerFactory.getLogger( getClass() );

  @Override
  public boolean supports( Class aClass ) {
    /*
     * Supports the *custom* counterpart of spring-security-saml's SamlAuthenticationToken; Note that this
     * is a spring-security-saml specific Authentication implementation and there is no direct spring-security 2.x
     * counterpart to this object ( spring-security-saml requires spring-security 3.x )
     */
    return "org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication.S2ExpiringUsernameAuthenticationProxy".equals( aClass.getName() );
  }

  @Override
  public Authentication create( Object o ) {

    try {

      Method getDetailsMethod = ReflectionUtils.findMethod( o.getClass(), "getDetails" );
      Method getCredentialsMethod = ReflectionUtils.findMethod( o.getClass(), "getCredentials" );
      Method isAuthenticatedMethod = ReflectionUtils.findMethod( o.getClass(), "isAuthenticated" );
      Method getTokenExpirationMethod = ReflectionUtils.findMethod( o.getClass(), "getTokenExpiration" );
      Method getPrincipalMethod = ReflectionUtils.findMethod( o.getClass(), "getPrincipal" );
      Method getAuthoritiesMethod = ReflectionUtils.findMethod( o.getClass(), "getAuthorities" );

      Date tokenExpiration = ( Date ) getTokenExpirationMethod.invoke( o );
      Object principal = getPrincipalMethod.invoke( o );
      Object credentials = getCredentialsMethod.invoke( o );

      Object authoritiesObj = getAuthoritiesMethod.invoke( o );

      Collection<SimpleGrantedAuthority> s4Authorities = new ArrayList<SimpleGrantedAuthority>();

      if( authoritiesObj != null && authoritiesObj instanceof Object[] ){

        for( Object authorityObj : ( Object[] ) authoritiesObj ){

          Method getAuthority = ReflectionUtils.findMethod( authorityObj.getClass(), "getAuthority" );
          Object authority = getAuthority.invoke( authorityObj );
          if( authority != null ) {
            s4Authorities.add( new SimpleGrantedAuthority( authority.toString() ) );
          }
        }
      }

      ExpiringUsernameAuthenticationToken auth =
          new ExpiringUsernameAuthenticationToken( tokenExpiration, principal, credentials, s4Authorities );
      auth.setDetails( getDetailsMethod.invoke( o ) );

      return auth;

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return null;
  }
}
