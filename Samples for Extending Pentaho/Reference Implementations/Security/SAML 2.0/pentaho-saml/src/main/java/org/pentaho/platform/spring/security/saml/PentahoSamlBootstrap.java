package org.pentaho.platform.spring.security.saml;

import java.lang.Exception;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.springframework.security.saml.SAMLBootstrap;

public class PentahoSamlBootstrap extends DefaultBootstrap {

  final String ESAPI_SYS_PROP_KEY = "org.owasp.esapi.SecurityConfiguration";

  public PentahoSamlBootstrap(){

    String esapi = System.getProperty( ESAPI_SYS_PROP_KEY );

    if( esapi == null || esapi.isEmpty() ) {
      // opensaml's DefaultBootstrap.initializeESAPI() sets ESAPI's SysProp value with it's own *if none exists yet*
      System.setProperty( ESAPI_SYS_PROP_KEY, org.owasp.esapi.reference.DefaultSecurityConfiguration.class.getName() );
    }

  }

  public static synchronized void bootstrap() throws ConfigurationException {

    new SAMLBootstrap().postProcessBeanFactory( null );

  }

}
