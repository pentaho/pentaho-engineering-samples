package org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class S2SamlAuthenticationProxy implements Authentication  {

  protected Logger logger = LoggerFactory.getLogger( getClass() );

  protected Object target;

  protected Method getNameMethod;
  protected Method getDetailsMethod;
  protected Method getCredentialsMethod;
  protected Method getPrincipalMethod;
  protected Method isAuthenticatedMethod;
  protected Method setAuthenticatedMethod;
  protected Method getAuthoritiesMethod;

  public S2SamlAuthenticationProxy( Object target ) {

    Assert.notNull( target );

    this.target = target;

    getNameMethod = ReflectionUtils.findMethod( target.getClass(), "getName" );
    getDetailsMethod = ReflectionUtils.findMethod( target.getClass(), "getDetails" );
    getCredentialsMethod = ReflectionUtils.findMethod( target.getClass(), "getCredentials" );
    getPrincipalMethod = ReflectionUtils.findMethod( target.getClass(), "getPrincipal" );
    isAuthenticatedMethod = ReflectionUtils.findMethod( target.getClass(), "isAuthenticated" );
    setAuthenticatedMethod = ReflectionUtils.findMethod( target.getClass(), "setAuthenticated" );
    getAuthoritiesMethod = ReflectionUtils.findMethod( target.getClass(), "getAuthorities" );
  }

  @Override
  public String getName() {

    try {
      return (String) getNameMethod.invoke( target );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return null;
  }

  @Override
  public Object getDetails() {

    try {
      return getDetailsMethod.invoke( target );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return null;
  }

  @Override
  public Object getCredentials() {

    try {
      return getCredentialsMethod.invoke( target );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return null;
  }

  @Override
  public Object getPrincipal() {

    try {
      return getPrincipalMethod.invoke( target );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return null;
  }

  @Override
  public boolean isAuthenticated() {

    try {
      return (Boolean) isAuthenticatedMethod.invoke( target );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return false;

  }

  @Override
  public void setAuthenticated( boolean b ) throws IllegalArgumentException {

    try {
      setAuthenticatedMethod.invoke( target, b );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }
  }

  @Override
  public GrantedAuthority[] getAuthorities() {

    try {

      Object authoritiesObj = getAuthoritiesMethod.invoke( target );

      List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();

      if ( authoritiesObj != null && authoritiesObj instanceof Collection ) {

        for ( Object authorityObj : (Collection) authoritiesObj ) {

          Method getAuthority = ReflectionUtils.findMethod( authorityObj.getClass(), "getAuthority" );
          Object authority = getAuthority.invoke( authorityObj );
          if ( authority != null ) {
            authorityList.add( new GrantedAuthorityImpl( authority.toString() ) );
          }
        }
      }

      return authorityList.toArray( new GrantedAuthority[authorityList.size()] );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      logger.error( e.getMessage(), e );
    }

    return new GrantedAuthority[] {};
  }
}
