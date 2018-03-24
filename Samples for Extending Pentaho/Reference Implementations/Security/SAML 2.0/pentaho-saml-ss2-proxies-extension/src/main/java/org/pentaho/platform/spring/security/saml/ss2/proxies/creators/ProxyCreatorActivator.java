package org.pentaho.platform.spring.security.saml.ss2.proxies.creators;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.pentaho.platform.proxy.api.IProxyCreator;
import org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication.S2ExpiringUsernameAuthenticationProxyCreator;
import org.pentaho.platform.spring.security.saml.ss2.proxies.creators.authentication.S2SamlAuthenticationProxyCreator;
import org.pentaho.platform.spring.security.saml.ss2.proxies.creators.savedrequest.S2SamlSavedRequestProxyCreator;

public class ProxyCreatorActivator implements BundleActivator {

  ServiceRegistration s2SamlAuthenticationProxyServiceRegistration;
  ServiceRegistration s2ExpiringUsernameAuthenticationServiceRegistration;
  ServiceRegistration s2SamlSavedRequestServiceRegistration;

  @Override public void start( BundleContext bundleContext ) throws Exception {

    s2SamlAuthenticationProxyServiceRegistration =
        bundleContext.registerService( IProxyCreator.class, new S2SamlAuthenticationProxyCreator(), null );
    
    s2SamlSavedRequestServiceRegistration =
            bundleContext.registerService( IProxyCreator.class, new S2SamlSavedRequestProxyCreator(), null );

    s2ExpiringUsernameAuthenticationServiceRegistration =
        bundleContext.registerService( IProxyCreator.class, new S2ExpiringUsernameAuthenticationProxyCreator(), null );
  }

  @Override public void stop( BundleContext bundleContext ) throws Exception {

    if( s2SamlAuthenticationProxyServiceRegistration != null ) {
      s2SamlAuthenticationProxyServiceRegistration.unregister();
    }
    
    if( s2SamlSavedRequestServiceRegistration != null ) {
    	s2SamlSavedRequestServiceRegistration.unregister();
      }

    if( s2ExpiringUsernameAuthenticationServiceRegistration != null ) {
      s2ExpiringUsernameAuthenticationServiceRegistration.unregister();
    }
  }
}
