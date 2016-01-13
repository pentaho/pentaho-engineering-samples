package org.pentaho.platform.spring.security.saml.groups;

import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.spring.security.saml.Utils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PentahoSamlUserGroupUserRoleDetailsService implements IUserRoleListService {

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

  @Override public List<String> getRolesForUser( ITenant iTenant, String user ) {

    /* iTenant is not supported */

    List<String> roles = new ArrayList<String>();

    UserDetails userDetails = null;

    if( ( userDetails = getUserDetailsService().loadUserByUsername( user ) ) != null  ){

      for( GrantedAuthority authority : userDetails.getAuthorities() ) {
        roles.add( authority.getAuthority() );
      }
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
