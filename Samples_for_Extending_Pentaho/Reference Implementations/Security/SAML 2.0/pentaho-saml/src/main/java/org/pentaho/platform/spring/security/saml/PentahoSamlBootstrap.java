package org.pentaho.platform.spring.security.saml;

import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.security.BasicSecurityConfiguration;
import org.opensaml.xml.signature.SignatureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLBootstrap;

public class PentahoSamlBootstrap extends DefaultBootstrap {

  private static Logger logger = LoggerFactory.getLogger( PentahoSamlBootstrap.class );

  private String signatureDigestAlgorithm;
  
  public PentahoSamlBootstrap() {
    
  }
  
  public void setSignatureDigestAlgorithm( String signatureDigestAlgorithm ) {
    this.signatureDigestAlgorithm = signatureDigestAlgorithm;
  }
  
  public String getSignatureDigestAlgorithm() {
    return this.signatureDigestAlgorithm;
  }
 
  public void afterPropertiesBootstrap() throws ConfigurationException {
    bootstrap();
    
    try {
      
      DigestAlgorithm alg = DigestAlgorithm.valueOf( getSignatureDigestAlgorithm().trim().toUpperCase() );
      
      logger.info( String.format( "Registering %s algorithm for signature digest method...", alg.toString() ) );
      
      BasicSecurityConfiguration config = (BasicSecurityConfiguration) Configuration.getGlobalSecurityConfiguration();
      config.registerSignatureAlgorithmURI( alg.getAlgorithmName(), alg.getAlgorithmUri() );
      config.setSignatureReferenceDigestMethod( alg.getDigestMethod() );
      
    } catch( Exception e ) {
      
      logger.info( "Using default RSA-SHA1 algorithm for signature digest method..." );
    }

  }

  public static synchronized void bootstrap() throws ConfigurationException {
    new SAMLBootstrap().postProcessBeanFactory( null );
  }
  
  private static enum DigestAlgorithm {
    
    SHA256( "RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, SignatureConstants.ALGO_ID_DIGEST_SHA256 ),
    SHA384( "RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384, SignatureConstants.ALGO_ID_DIGEST_SHA384 ), 
    SHA512( "RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512, SignatureConstants.ALGO_ID_DIGEST_SHA512 );
    
    private final String algorithmName;
    private final String algorithmUri;
    private final String digestMethod;
    
    private DigestAlgorithm( String algorithmName, String algorithmUri, String digestMethod ) {
      this.algorithmName = algorithmName;
      this.algorithmUri = algorithmUri;
      this.digestMethod = digestMethod;
    }
    
    public String getAlgorithmName() {
      return algorithmName;
    }
    
    public String getAlgorithmUri() {
      return algorithmUri;
    }
    
    public String getDigestMethod() {
      return digestMethod;
    }
    
    
  }

}
