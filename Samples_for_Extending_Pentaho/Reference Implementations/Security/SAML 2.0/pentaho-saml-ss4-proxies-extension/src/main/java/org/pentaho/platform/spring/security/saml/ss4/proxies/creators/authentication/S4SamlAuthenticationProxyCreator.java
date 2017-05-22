package org.pentaho.platform.spring.security.saml.ss4.proxies.creators.authentication;

import org.pentaho.platform.proxy.api.IProxyCreator;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.util.ReflectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class S4SamlAuthenticationProxyCreator implements IProxyCreator<Authentication> {

  private Logger logger = LoggerFactory.getLogger( getClass() );

  public S4SamlAuthenticationProxyCreator() {
    logger.info( "S4SamlAuthenticationProxyCreator ready" );
  }

  @Override
  public boolean supports( Class aClass ) {
    /*
     * Supports the *custom* counterpart of spring-security-saml's SamlAuthenticationToken; Note that this
     * is a spring-security-saml specific Authentication implementation and there is no direct spring-security 2.x
     * counterpart to this object ( spring-security-saml requires spring-security 3.x )
     */
    return "org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication.S2SamlAuthenticationProxy".equals( aClass.getName() );
  }

  @Override
  public Authentication create( Object o ) {

    try {

      Method getDetailsMethod = ReflectionUtils.findMethod( o.getClass(), "getDetails" );
      Method getCredentialsMethod = ReflectionUtils.findMethod( o.getClass(), "getCredentials" );
      Method isAuthenticatedMethod = ReflectionUtils.findMethod( o.getClass(), "isAuthenticated" );

      SAMLAuthenticationToken auth = new SAMLAuthenticationToken( (SAMLMessageContext) getCredentialsMethod.invoke( o ) );
      auth.setAuthenticated( ( Boolean ) isAuthenticatedMethod.invoke( o ) );
      auth.setDetails( getDetailsMethod.invoke( o ) );

      return auth;

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return null;
  }
}
