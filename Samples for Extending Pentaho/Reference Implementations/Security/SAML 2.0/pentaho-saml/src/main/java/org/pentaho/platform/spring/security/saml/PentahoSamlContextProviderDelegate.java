package org.pentaho.platform.spring.security.saml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.util.Assert;

public class PentahoSamlContextProviderDelegate implements SAMLContextProvider, InitializingBean {

  private static final Logger logger = LoggerFactory.getLogger( PentahoSamlContextProviderDelegate.class );
  
  // The impl provider, which is not behind a load balancer
  private SAMLContextProviderImpl implProvider;
  // The load balancing context provider, which is behind a load balancer
  private SAMLContextProviderLB lbProvider;

  // The actual delegate, set internally afterPropertiesSet()
  private SAMLContextProvider delegate;

  // Boolean value indicating if the internal Load Balance provider should be used
  private boolean useLoadBalanceProvider;

  // By default, work like the sample always has (no load balancer)
  public PentahoSamlContextProviderDelegate() {
    this( false );
  }

  // Constructor allowing load balancing provider to be used
  public PentahoSamlContextProviderDelegate( boolean useLoadBalanceProvider ) {
    this.useLoadBalanceProvider = useLoadBalanceProvider;
  }

  @Override
  public SAMLMessageContext getLocalEntity( HttpServletRequest request, HttpServletResponse response )
    throws MetadataProviderException {
    return delegate.getLocalEntity( request, response );
  }

  @Override
  public SAMLMessageContext getLocalAndPeerEntity( HttpServletRequest request, HttpServletResponse response )
    throws MetadataProviderException {
    return delegate.getLocalAndPeerEntity( request, response );
  }

  @Override
  public void afterPropertiesSet() throws Exception {

    Assert.notNull( implProvider );
    Assert.notNull( lbProvider );

    
    
    if ( isUseLoadBalanceProvider() ) {
      logger.info( "Configuring Context Provider Delegate for Load Balancer"  );
      delegate = lbProvider;
    } else {
      logger.info( "Configuring Context Provider Delegate with Standard Impl "  );
      delegate = implProvider;
    }

  }

  public SAMLContextProviderImpl getImplProvider() {
    return implProvider;
  }

  public void setImplProvider( SAMLContextProviderImpl implProvider ) {
    this.implProvider = implProvider;
  }

  public SAMLContextProviderLB getLbProvider() {
    return lbProvider;
  }

  public void setLbProvider( SAMLContextProviderLB lbProvider ) {
    this.lbProvider = lbProvider;
  }

  public boolean isUseLoadBalanceProvider() {
    return useLoadBalanceProvider;
  }

  public void setUseLoadBalanceProvider( boolean useLoadBalanceProvider ) {
    this.useLoadBalanceProvider = useLoadBalanceProvider;
  }

}