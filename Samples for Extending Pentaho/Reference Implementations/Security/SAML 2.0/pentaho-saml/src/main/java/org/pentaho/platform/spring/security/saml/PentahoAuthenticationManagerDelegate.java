package org.pentaho.platform.spring.security.saml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.proxy.api.IProxyFactory;
import org.pentaho.platform.proxy.impl.ProxyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class PentahoAuthenticationManagerDelegate implements AuthenticationManager {

  private static final Log logger = LogFactory.getLog( PentahoAuthenticationManagerDelegate.class );

  AuthenticationManager delegate;

  public PentahoAuthenticationManagerDelegate( AuthenticationManager delegate ){
    setDelegate( delegate );
  }

  public PentahoAuthenticationManagerDelegate( boolean requireProxyWrapping ) {

    AuthenticationManager manager = null;

    if( !requireProxyWrapping ) {

      manager = PentahoSystem.get( AuthenticationManager.class, null );

    } else if( PentahoSystem.get( AuthenticationManager.class, null ) != null ) {

      try {

        IProxyFactory factory = PentahoSystem.get( IProxyFactory.class );
        manager = factory.createProxy( PentahoSystem.get( AuthenticationManager.class, null ) );

      } catch ( ProxyException e ) {
        logger.error( e.getMessage(), e );
      }
    }

    setDelegate( manager );
  }

  @Override
  public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
    return getDelegate().authenticate( authentication );
  }

  public AuthenticationManager getDelegate() {
    return delegate;
  }

  public void setDelegate( AuthenticationManager delegate ) {
    this.delegate = delegate;
  }
}
