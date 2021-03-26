package org.pentaho.platform.spring.security.saml;

import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.security.BasicSecurityConfiguration;
import org.opensaml.xml.signature.SignatureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLBootstrap;

public class PentahoSamlBootstrap {

  private static Logger logger = LoggerFactory.getLogger( PentahoSamlBootstrap.class );

  private String signatureAlgorithm = "SHA1";

  public PentahoSamlBootstrap() {
  }

  public synchronized void bootstrap() throws ConfigurationException {

    new SAMLBootstrap().postProcessBeanFactory( null );

    if ( signatureAlgorithm != null ) {
      String sigAlg = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
      String digAlg = SignatureConstants.ALGO_ID_DIGEST_SHA1;
      
      switch ( signatureAlgorithm.toUpperCase() ) {
        case "SHA256":
          sigAlg = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;
          digAlg = SignatureConstants.ALGO_ID_DIGEST_SHA256;
          break;
        case "SHA384":
          sigAlg = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384;
          digAlg = SignatureConstants.ALGO_ID_DIGEST_SHA384;
          break;
        case "SHA512":
          sigAlg = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512;
          digAlg = SignatureConstants.ALGO_ID_DIGEST_SHA512;
          break;
      }

      logger.debug( String.format( "Registering Signature Algorithm(%s) and Digest Algorithm(%s)", sigAlg, digAlg ) );
      BasicSecurityConfiguration config = (BasicSecurityConfiguration) Configuration.getGlobalSecurityConfiguration();
      config.registerSignatureAlgorithmURI( "RSA", sigAlg );
      config.setSignatureReferenceDigestMethod( digAlg );

    }

  }

  public String getSignatureAlgorithm() {
    return signatureAlgorithm;
  }

  public void setSignatureAlgorithm( String signatureAlgorithm ) {
    this.signatureAlgorithm = signatureAlgorithm;
  }

}
