package org.pentaho.platform.spring.security.saml.groups;

import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.spring.security.saml.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PentahoSamlUserGroupUserRoleDetailsService implements IUserRoleListService {

  private static final Logger logger = LoggerFactory.getLogger( PentahoSamlUserGroupUserRoleDetailsService.class );
  
  private List<String> systemRoles;

  private PentahoSamlUserGroupsDetailsService userDetailsService;

  public PentahoSamlUserGroupUserRoleDetailsService( PentahoSamlUserGroupsDetailsService userDetailsService ){
    Assert.notNull( userDetailsService );
    setUserDetailsService( userDetailsService );
  }

  @Override public List<String> getAllRoles() {
    return null; /* not supported */
  }

  @Override public List<String> getSystemRoles() {
    if ( systemRoles == null ) {
      systemRoles = PentahoSystem.get( ArrayList.class, "singleTenantSystemAuthorities", null );
    }
    return systemRoles;
  }

  @Override public List<String> getAllRoles( ITenant iTenant ) {
    return null; /* not supported */
  }

  @Override public List<String> getAllUsers() {
    return null; /* not supported */
  }

  @Override public List<String> getAllUsers( ITenant iTenant ) {
    return null; /* not supported */
  }

  @Override public List<String> getUsersInRole( ITenant iTenant, String role ) {

    /* iTenant is not supported */

    List<String> usersInRole = new ArrayList<String>();

    Set<String> usernameSet = Utils.getUserMap().keySet();

    for( String username : usernameSet ){

      List<String> userRoles = null;

      if( ( userRoles = getRolesForUser( null, username ) ) != null ) {

        if( userRoles.contains( role ) ) {
          usersInRole.add( username );
        }
      }
    }

    return usersInRole;
  }

  @Override public List<String> getRolesForUser( ITenant iTenant, String user ) throws UsernameNotFoundException {

    /* iTenant is not supported */

    List<String> roles = new ArrayList<String>();

    try {
      UserDetails userDetails = getUserDetailsService().loadUserByUsername( user );

      if ( userDetails == null ) {
        logger.warn( "Got a null from calling the method loadUserByUsername( String username ) of UserDetailsService: "
            + getUserDetailsService()
            + ". This is an interface violation beacuse it is specified that loadUserByUsername method should never return null. Throwing a UsernameNotFoundException." );
        throw new UsernameNotFoundException( user );
      }

      Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

      if ( authorities != null ) {
        for ( GrantedAuthority authority : authorities ) {
          roles.add( authority.getAuthority() );
        }
      } else {
        logger.warn( "Got a null from calling the method getAuthorities() of UserDetails: " + userDetails
            + ". This is an interface violation beacuse it is specified that getAuthorities() method should never return null. Considered no GrantedAuthorities for username "
            + user );
      }

    } catch ( UsernameNotFoundException usernameNotFoundException ) {
      // The user does not exist in the UserDetailsService. Do nothing and return an empty list of roles
    }
    return roles;
  }

  public PentahoSamlUserGroupsDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  public void setUserDetailsService( PentahoSamlUserGroupsDetailsService userDetailsService ) {
    this.userDetailsService = userDetailsService;
  }
}
