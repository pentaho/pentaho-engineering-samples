package org.pentaho.platform.spring.security.saml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;

public class PentahoSamlAuthenticationProvider extends SAMLAuthenticationProvider {

  private static final Logger logger = LoggerFactory.getLogger( PentahoSamlAuthenticationProvider.class );

  @Override
  public Authentication authenticate( Authentication auth ) throws AuthenticationException {

    ExpiringUsernameAuthenticationToken token = ( ExpiringUsernameAuthenticationToken ) super.authenticate( auth );

    if( token != null && token.getDetails() != null && token.getDetails() instanceof UserDetails ) {
      Utils.getUserMap().put( token.getPrincipal().toString() , ( UserDetails ) token.getDetails() );
    }

    return token;
  }
}
