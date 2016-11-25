package org.pentaho.platform.spring.security.saml;

import java.lang.Exception;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.springframework.security.saml.SAMLBootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PentahoSamlBootstrap extends DefaultBootstrap {

  private static Logger logger = LoggerFactory.getLogger( PentahoSamlBootstrap.class );

  public PentahoSamlBootstrap(){
  }

  public static synchronized void bootstrap() throws ConfigurationException {

    new SAMLBootstrap().postProcessBeanFactory( null );

  }

}
