package org.pentaho.platform.spring.security.saml.ss2.proxies.creators.savedrequest;

import java.util.Map;

import org.pentaho.platform.proxy.api.IProxyCreator;
import org.springframework.security.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S2SamlSavedRequestProxyCreator implements IProxyCreator<Map.Entry<String,String>> {

  private Logger logger = LoggerFactory.getLogger( getClass() );

  public S2SamlSavedRequestProxyCreator() {
    logger.info( "S2SamlSavedRequestProxyCreator ready;" );
  }

  @Override
  public boolean supports( Class aClass ) {
    /*
     * Supports SavedRequest; Note that this is a spring-security-saml specific SavedRequest implementation
     * and there is no spring-security 2.x counterpart to this object ( spring-security-saml requires spring-security 3.x )
     */
    return "org.springframework.security.ui.savedrequest.SavedRequest".equals( aClass.getName() );
  }

  @Override
  public S2SamlSavedRequestProxy create( Object o ) {
    return new S2SamlSavedRequestProxy( o );
  }
}
