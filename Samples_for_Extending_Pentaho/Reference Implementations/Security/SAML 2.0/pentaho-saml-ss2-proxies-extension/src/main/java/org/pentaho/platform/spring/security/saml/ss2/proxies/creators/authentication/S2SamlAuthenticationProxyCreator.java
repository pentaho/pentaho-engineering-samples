package org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication;

import org.pentaho.platform.proxy.api.IProxyCreator;
import org.springframework.security.Authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S2SamlAuthenticationProxyCreator implements IProxyCreator<Authentication> {

  private Logger logger = LoggerFactory.getLogger( getClass() );

  public S2SamlAuthenticationProxyCreator() {
    logger.info( "S2SamlAuthenticationProxyCreator ready;" );
  }

  @Override
  public boolean supports( Class aClass ) {
    /*
     * Supports SamlAuthenticationToken; Note that this is a spring-security-saml specific Authentication implementation
     * and there is no spring-security 2.x counterpart to this object ( spring-security-saml requires spring-security 3.x )
     */
    return "org.springframework.security.saml.SAMLAuthenticationToken".equals( aClass.getName() );
  }

  @Override
  public Authentication create( Object o ) {
    return new S2SamlAuthenticationProxy( o );
  }
}
