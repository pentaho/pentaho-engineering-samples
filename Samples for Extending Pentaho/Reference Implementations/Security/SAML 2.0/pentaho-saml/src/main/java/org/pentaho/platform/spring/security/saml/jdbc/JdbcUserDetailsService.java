package org.pentaho.platform.spring.security.saml.jdbc;

import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class JdbcUserDetailsService implements UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger( JdbcUserDetailsService.class );
  
  UserDetailsService service;
  ITenantedPrincipleNameResolver tenantedPrincipleNameResolver;

  public JdbcUserDetailsService( UserDetailsService service,
      ITenantedPrincipleNameResolver tenantedPrincipleNameResolver ){
    this.service = service;
    this.tenantedPrincipleNameResolver = tenantedPrincipleNameResolver;
  }

  @Override public UserDetails loadUserByUsername( String user ) throws UsernameNotFoundException {

    if( user != null ) {
      UserDetails userDetails = service.loadUserByUsername( tenantedPrincipleNameResolver.getPrincipleName( user ) );
      if ( userDetails == null ) {
        logger.error( "No user found for Username '" + user
            + "' in your Jdbc service. Please verify that the user exists in your users database table" );
      }
      return userDetails;
    }

    return null;
  }
}
