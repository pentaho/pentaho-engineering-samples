package org.pentaho.platform.spring.security.saml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.spring.security.saml.groups.PentahoSamlNativeUserRoleListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class PentahoSamlUserRoleListService implements IUserRoleListService {

  private static Logger logger = LoggerFactory.getLogger( PentahoSamlUserRoleListService.class );

  private static final String PROVIDER_NAME = "providerName";

  //Delegate user role list service
  private IUserRoleListService service;
  private String selectedAuthorizationProvider;

  private PentahoSamlNativeUserRoleListService samlUserRoleListService;
  private String samlId;

  public PentahoSamlUserRoleListService( String selectedAuthorizationProvider ) {
    Assert.notNull( selectedAuthorizationProvider );
    setSelectedAuthorizationProvider( selectedAuthorizationProvider );
  }

  protected void initUserRoleListService() {
    
    //If SAML is selected, default to the native user role list service
    if( getSelectedAuthorizationProvider().equals( getSamlId() ) ) {
      setService( getSamlUserRoleListService() );
      return;
    }

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

    if ( service == null ) {
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

  @Override
  public List<String> getAllRoles() {
    return getService().getAllRoles();
  }

  @Override
  public List<String> getSystemRoles() {
    return getService().getSystemRoles();
  }

  @Override
  public List<String> getAllRoles( ITenant tenant ) {
    return getService().getAllRoles( tenant );
  }

  @Override
  public List<String> getAllUsers() {
    return getService().getAllUsers();
  }

  @Override
  public List<String> getAllUsers( ITenant tenant ) {
    return getService().getAllUsers( tenant );
  }

  @Override
  public List<String> getUsersInRole( ITenant tenant, String role ) {
    return getService().getUsersInRole( tenant, role );
  }

  @Override
  public List<String> getRolesForUser( ITenant tenant, String user ) {
    return getService().getRolesForUser( tenant, user );
  }

  public PentahoSamlNativeUserRoleListService getSamlUserRoleListService() {
    return samlUserRoleListService;
  }

  public void setSamlUserRoleListService( PentahoSamlNativeUserRoleListService samlUserRoleListService ) {
    this.samlUserRoleListService = samlUserRoleListService;
  }

  public String getSamlId() {
    return samlId;
  }

  public void setSamlId( String samlId ) {
    this.samlId = samlId;
  }
  
}
