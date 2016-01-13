package org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class S2ExpiringUsernameAuthenticationProxy extends S2SamlAuthenticationProxy {

  private Method getTokenExpirationMethod;
  private Method setTokenExpirationMethod;

  public S2ExpiringUsernameAuthenticationProxy( Object target ) {
    super( target );

    getTokenExpirationMethod = ReflectionUtils.findMethod( target.getClass(), "getTokenExpiration" );
    setTokenExpirationMethod = ReflectionUtils.findMethod( target.getClass(), "setTokenExpiration", Date.class );
  }

  public Date getTokenExpiration() {

    try {
      return ( Date ) getTokenExpirationMethod.invoke( target );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return null;
  }

  public void setTokenExpiration( Date tokenExpiration ) {

    try {
      setTokenExpirationMethod.invoke( target, tokenExpiration );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }
  }
}
