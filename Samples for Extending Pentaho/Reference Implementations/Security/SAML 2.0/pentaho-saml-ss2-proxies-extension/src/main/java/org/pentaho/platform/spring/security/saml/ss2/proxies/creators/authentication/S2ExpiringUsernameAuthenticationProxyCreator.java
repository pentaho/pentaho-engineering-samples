package org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication;

import org.pentaho.platform.proxy.api.IProxyCreator;
import org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication.S2ExpiringUsernameAuthenticationProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;


public class S2ExpiringUsernameAuthenticationProxyCreator implements IProxyCreator<Authentication> {

    private Logger logger = LoggerFactory.getLogger( getClass() );

    public S2ExpiringUsernameAuthenticationProxyCreator() {
    logger.info( "S2ExpiringUsernameAuthenticationProxyCreator ready;" );
  }

    @Override
    public boolean supports( Class aClass ) {
      /*
       * Supports ExpiringUsernameAuthenticationToken; Note that this is a spring-security-saml specific
       * Authentication implementation and there is no spring-security 2.x counterpart to this object
       * ( spring-security-saml requires spring-security 3.x )
       */
      return "org.springframework.security.providers.ExpiringUsernameAuthenticationToken".equals( aClass.getName() );
    }

    @Override
    public Authentication create( Object o ) {
      return new S2ExpiringUsernameAuthenticationProxy( o );
    }
}
