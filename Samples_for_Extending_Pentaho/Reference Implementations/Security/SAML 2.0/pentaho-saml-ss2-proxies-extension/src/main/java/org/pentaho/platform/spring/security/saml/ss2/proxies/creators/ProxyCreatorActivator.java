package org.pentaho.platform.spring.security.saml.ss2.proxies.creators;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.pentaho.platform.proxy.api.IProxyCreator;
import org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication.S2ExpiringUsernameAuthenticationProxyCreator;
import org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication.S2SamlAuthenticationProxyCreator;

public class ProxyCreatorActivator implements BundleActivator {

  ServiceRegistration s2SamlAuthenticationProxyServiceRegistration;
  ServiceRegistration s2ExpiringUsernameAuthenticationServiceRegistration;

  @Override public void start( BundleContext bundleContext ) throws Exception {

    s2SamlAuthenticationProxyServiceRegistration =
        bundleContext.registerService( IProxyCreator.class, new S2SamlAuthenticationProxyCreator(), null );

    s2ExpiringUsernameAuthenticationServiceRegistration =
        bundleContext.registerService( IProxyCreator.class, new S2ExpiringUsernameAuthenticationProxyCreator(), null );
  }

  @Override public void stop( BundleContext bundleContext ) throws Exception {

    if( s2SamlAuthenticationProxyServiceRegistration != null ) {
      s2SamlAuthenticationProxyServiceRegistration.unregister();
    }

    if( s2ExpiringUsernameAuthenticationServiceRegistration != null ) {
      s2ExpiringUsernameAuthenticationServiceRegistration.unregister();
    }
  }
}
