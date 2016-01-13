package org.pentaho.platform.spring.security.saml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PentahoSamlUserRoleDetailsService implements IUserRoleListService {

  private static final Log logger = LogFactory.getLog( PentahoSamlUserRoleDetailsService.class );

  private static final String PROVIDER_NAME = "providerName";

  private IUserRoleListService service;
  String selectedAuthorizationProvider;

  public PentahoSamlUserRoleDetailsService( IUserRoleListService service ) {
    this.service = service;
  }

  public PentahoSamlUserRoleDetailsService( String selectedAuthorizationProvider ) {

    setSelectedAuthorizationProvider( selectedAuthorizationProvider );
    Assert.notNull( selectedAuthorizationProvider );
  }

  protected void initUserRoleListService() {

    Map<String, String> props = new HashMap<>();
    props.put( PROVIDER_NAME, getSelectedAuthorizationProvider() );

    IUserRoleListService userRoleListService;

    if ( ( userRoleListService = PentahoSystem.get( IUserRoleListService.class, null, props ) ) != null ) {

      setService( userRoleListService );

    } else {
      logger.error( "No IUserRoleListService found for providerName '" + getSelectedAuthorizationProvider() + "'" );
    }
  }

  public IUserRoleListService getService() {

    if( service == null ){
      initUserRoleListService();
    }

    return service;
  }

  public void setService( IUserRoleListService service ) {
    this.service = service;
  }

  public String getSelectedAuthorizationProvider() {
    return selectedAuthorizationProvider;
  }

  public void setSelectedAuthorizationProvider( String selectedAuthorizationProvider ) {
    this.selectedAuthorizationProvider = selectedAuthorizationProvider;
  }

  @Override public List<String> getAllRoles() {
    return getService().getAllRoles();
  }

  @Override public List<String> getSystemRoles() {
    return getService().getSystemRoles();
  }

  @Override public List<String> getAllRoles( ITenant tenant ) {
    return getService().getAllRoles( tenant );
  }

  @Override public List<String> getAllUsers() {
    return getService().getAllUsers();
  }

  @Override public List<String> getAllUsers( ITenant tenant ) {
    return getService().getAllUsers( tenant );
  }

  @Override public List<String> getUsersInRole( ITenant tenant, String role ) {
    return getService().getUsersInRole( tenant, role );
  }

  @Override public List<String> getRolesForUser( ITenant tenant, String user ) {
    return getService().getRolesForUser( tenant, user );
  }


}
