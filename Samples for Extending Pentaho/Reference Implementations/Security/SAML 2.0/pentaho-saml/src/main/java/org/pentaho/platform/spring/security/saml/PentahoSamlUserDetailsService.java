package org.pentaho.platform.spring.security.saml;

import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.proxy.api.IProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Pentaho's implementation of SAMLUserDetailsService interface
 * @ see https://github.com/spring-projects/spring-security-saml/blob/1.0.1.RELEASE/core/src/main/java/org/springframework/security/saml/userdetails/SAMLUserDetailsService.java
 */
public class PentahoSamlUserDetailsService implements SAMLUserDetailsService, UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger( PentahoSamlUserDetailsService.class );

  private static final String PROVIDER_NAME = "providerName";

  UserDetailsService userDetailsService;
  String selectedAuthorizationProvider;

  // if the selectedAuthorizationProvider is our own ( i.e. samlId ), we'll directly use our own
  UserDetailsService samlUserDetailsService;
  String samlId;

  public PentahoSamlUserDetailsService( UserDetailsService userDetailsService ){
    setUserDetailsService( userDetailsService );
    Assert.notNull( userDetailsService );
  }

  public PentahoSamlUserDetailsService( String selectedAuthorizationProvider ) {
    setSelectedAuthorizationProvider( selectedAuthorizationProvider );
    Assert.notNull( selectedAuthorizationProvider );
  }

  protected void initUserDetailsService() {

    if( getSelectedAuthorizationProvider().equals( getSamlId() ) ){
      // if the selectedAuthorizationProvider is our own ( i.e. samlId ), we'll directly use our own
      setUserDetailsService( getSamlUserDetailsService() );
      return;
    }

    Map<String, String> props = new HashMap();
    props.put( PROVIDER_NAME , getSelectedAuthorizationProvider() );

    Object userDetailsServiceObj = null;

    try {

      // is it a spring-security 4 UserDetailsService ?
      userDetailsServiceObj = PentahoSystem.get( UserDetailsService.class, null, props );
      setUserDetailsService ( ( UserDetailsService ) userDetailsServiceObj );

    } catch ( ClassCastException cce ) {

      // nope;
      // is it a spring-security 2 UserDetailsService ?

      // we cannot access spring-security 2 classes directly;
      Class ss2UserDetailsServiceClass = getSS2UserDetailsServiceClass( userDetailsServiceObj );

      if( ss2UserDetailsServiceClass != null ) {

        Object ss2UserDetailsService = PentahoSystem.get( ss2UserDetailsServiceClass, null, props );

        if( ss2UserDetailsService != null ) {

          try {

            IProxyFactory factory = PentahoSystem.get( IProxyFactory.class );
            setUserDetailsService( ( UserDetailsService ) factory.createProxy( ss2UserDetailsService ) );

          } catch ( Exception e ) {

            // it's neither a spring-security 4 nor a spring-security 2..
            logger.error( e.getMessage(), e );
            logger.error(  "No UserDetailsService found for providerName '" + getSelectedAuthorizationProvider() + "' " );
          }
        }
      }
    }
  }

  @Override
  public Object loadUserBySAML( SAMLCredential credential ) throws UsernameNotFoundException {

    if( getUserDetailsService() instanceof SAMLUserDetailsService ) {
      // inner UserDetailsService is also an implementation of SAMLUserDetailsService ? Great!
      // In that case we can also delegate any incoming loadUserBySAML() calls
      return ( ( SAMLUserDetailsService ) getUserDetailsService() ).loadUserBySAML( credential );
    }

    // default UserDetail build, using as reference the passed SAMLCredential

    if( credential == null || credential.getNameID() == null || credential.getNameID().getValue() == null ){
      throw new UsernameNotFoundException( "invalid/null SAMLCredential" );
    }

    String username = credential.getNameID().getValue();

    UserDetails user = loadUserByUsername( username );

    //If the loadUserByUsername method returns null
    if ( user == null ) {
      throw new UsernameNotFoundException( "No user found for Username '" + username + "' in UserDetailsService '"
          + getSelectedAuthorizationProvider()
          + "'. Please verify that the user exists in the used service and confirm that your configurations are correct." );
    }

    //Ensure that any authenticated user gets the DefaultRole, usually "Authenticated"
    Collection<? extends GrantedAuthority> authorities = ensureDefaultRole(user.getAuthorities());

    return new User( user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(),
        user.isCredentialsNonExpired(), user.isAccountNonExpired(), authorities );
  }

  @Override public UserDetails loadUserByUsername( String s ) throws UsernameNotFoundException {
    return getUserDetailsService().loadUserByUsername( s );
  }

  private Collection<? extends GrantedAuthority> ensureDefaultRole(
      Collection<? extends GrantedAuthority> userAuthorities ) {

    GrantedAuthority defaultRole = Utils.getDefaultRole();
    if ( userAuthorities.contains( defaultRole ) )
      return userAuthorities;

    ArrayList<GrantedAuthority> newUserAuthorities = new ArrayList<GrantedAuthority>( userAuthorities );
    newUserAuthorities.add( defaultRole );
    
    //This sort is needed because the collection of GrantedAuthorities has to be sorted by natural order, as stated in the description of getAuthority() method
    Collections.sort( newUserAuthorities, new Comparator<GrantedAuthority>() {
      public int compare( GrantedAuthority grantedAuthority1, GrantedAuthority grantedAuthority2 ) {
        return grantedAuthority1.getAuthority().compareTo( grantedAuthority2.getAuthority() );
      }
    } );
    return newUserAuthorities;
  }
  
  public UserDetailsService getUserDetailsService() {

    if( userDetailsService == null ){
      initUserDetailsService();
    }

    return userDetailsService;
  }

  public void setUserDetailsService( UserDetailsService userDetailsService ) {
    this.userDetailsService = userDetailsService;
  }

  public String getSelectedAuthorizationProvider() {
    return selectedAuthorizationProvider;
  }

  public void setSelectedAuthorizationProvider( String selectedAuthorizationProvider ) {
    this.selectedAuthorizationProvider = selectedAuthorizationProvider;
  }

  public UserDetailsService getSamlUserDetailsService() {
    return samlUserDetailsService;
  }

  public void setSamlUserDetailsService( UserDetailsService samlUserDetailsService ) {
    this.samlUserDetailsService = samlUserDetailsService;
  }

  public String getSamlId() {
    return samlId;
  }

  public void setSamlId( String samlId ) {
    this.samlId = samlId;
  }

  private Class getSS2UserDetailsServiceClass( Object ss2UserDetailsServiceImpl ) {

    // we cannot access spring-security 2 classes directly;

    if( ss2UserDetailsServiceImpl != null ) {

      for( Class clazz : ss2UserDetailsServiceImpl.getClass().getInterfaces() ) {

        // we'll search for an interface class with last name 'UserDetailsService'
        if ( clazz.getName().endsWith( UserDetailsService.class.getSimpleName() ) ) {

          return clazz;
        }
      }
    }

    return null;
  }
}
