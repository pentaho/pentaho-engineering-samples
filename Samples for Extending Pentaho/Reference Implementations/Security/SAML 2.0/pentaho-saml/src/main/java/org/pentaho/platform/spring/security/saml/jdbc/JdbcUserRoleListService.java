package org.pentaho.platform.spring.security.saml.jdbc;


import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.mt.ITenant;
import org.springframework.context.ApplicationContextException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserRoleListService extends JdbcDaoSupport implements IUserRoleListService {

  private List<String> systemRoles;

  private UserDetailsService userDetailsService;

  private String allAuthoritiesQuery;
  private String allUsernamesQuery;
  private String allUsernamesInRoleQuery;

  protected MappingSqlQuery allAuthoritiesMapping;
  protected MappingSqlQuery allUsernamesMapping;
  protected MappingSqlQuery allUsernamesInRoleMapping;

  public JdbcUserRoleListService( UserDetailsService userDetailsService ){
    Assert.notNull( userDetailsService );
    setUserDetailsService( userDetailsService );
  }

  @Override public List<String> getAllRoles() {
    return authsToRoles( allAuthoritiesMapping.execute() );
  }

  @Override public List<String> getAllRoles( ITenant iTenant ) {
    return getAllRoles();
  }

  @Override public List<String> getSystemRoles() {
    return systemRoles;
  }

  @Override public List<String> getAllUsers() {
    return allUsernamesMapping.execute();
  }

  @Override public List<String> getAllUsers( ITenant iTenant ) {
    return getAllUsers();
  }

  @Override public List<String> getUsersInRole( ITenant iTenant, String role ) {
    return allUsernamesInRoleMapping.execute( role );
  }

  @Override public List<String> getRolesForUser( ITenant iTenant, String username ) {

    List<String> roles = new ArrayList<String>();

    UserDetails user = userDetailsService.loadUserByUsername( username );

    if( user != null && user.getAuthorities() != null ) {

      for ( GrantedAuthority role : user.getAuthorities() ) {
        roles.add( role.getAuthority() );
      }
    }

    return roles;
  }

  @Override
  public void initDao() throws ApplicationContextException {
    this.allAuthoritiesMapping = new AllAuthoritiesMapping( getDataSource() );
    this.allUsernamesInRoleMapping = new AllUserNamesInRoleMapping( getDataSource() );
    this.allUsernamesMapping = new AllUserNamesMapping( getDataSource() );
  }

  public UserDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  public void setUserDetailsService( UserDetailsService userDetailsService ) {
    this.userDetailsService = userDetailsService;
  }

  public String getAllAuthoritiesQuery() {
    return allAuthoritiesQuery;
  }

  public void setAllAuthoritiesQuery( String allAuthoritiesQuery ) {
    this.allAuthoritiesQuery = allAuthoritiesQuery;
  }

  public String getAllUsernamesQuery() {
    return allUsernamesQuery;
  }

  public void setAllUsernamesQuery( String allUsernamesQuery ) {
    this.allUsernamesQuery = allUsernamesQuery;
  }

  public String getAllUsernamesInRoleQuery() {
    return allUsernamesInRoleQuery;
  }

  public void setAllUsernamesInRoleQuery( String allUsernamesInRoleQuery ) {
    this.allUsernamesInRoleQuery = allUsernamesInRoleQuery;
  }

  public void setSystemRoles( List<String> systemRoles ) {
    this.systemRoles = systemRoles;
  }

  /**
   * Query object to look up all users.
   */
  protected class AllUserNamesMapping extends MappingSqlQuery {
    protected AllUserNamesMapping( final DataSource ds ) {
      super( ds, allUsernamesQuery );
      compile();
    }

    @Override
    protected Object mapRow( final ResultSet rs, final int rownum ) throws SQLException {
      return rs.getString( 1 );
    }
  }

  /**
   * Query object to look up users in a role.
   */
  protected class AllUserNamesInRoleMapping extends MappingSqlQuery {
    protected AllUserNamesInRoleMapping( final DataSource ds ) {
      super( ds, allUsernamesInRoleQuery );
      declareParameter( new SqlParameter( Types.VARCHAR ) );
      compile();
    }

    @Override
    protected Object mapRow( final ResultSet rs, final int rownum ) throws SQLException {
      return rs.getString( 1 );
    }
  }

  /**
   * Query object to look up all authorities.
   */
  protected class AllAuthoritiesMapping extends MappingSqlQuery {
    protected AllAuthoritiesMapping( final DataSource ds ) {
      super( ds, allAuthoritiesQuery );
      compile();
    }

    @Override
    protected Object mapRow( final ResultSet rs, final int rownum ) throws SQLException {
      return new GrantedAuthorityImpl( "" + rs.getString( 1 ) ); //$NON-NLS-1$
    }
  }

  private List<String> authsToRoles( List<GrantedAuthority> auths ) {

    List<String> roles = new ArrayList<String>();

    if( auths != null ) {

      for ( GrantedAuthority role : auths ) {
        roles.add( role.getAuthority() );
      }
    }

    return roles;
  }
}
