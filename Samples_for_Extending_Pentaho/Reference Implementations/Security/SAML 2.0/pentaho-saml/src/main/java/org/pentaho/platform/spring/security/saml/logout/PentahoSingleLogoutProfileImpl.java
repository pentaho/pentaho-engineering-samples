package org.pentaho.platform.spring.security.saml.logout;

import org.opensaml.common.SAMLException;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.xml.encryption.DecryptionException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.SAMLStatusException;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;

public class PentahoSingleLogoutProfileImpl extends SingleLogoutProfileImpl {

  private static final Logger logger = LoggerFactory.getLogger( PentahoSingleLogoutProfileImpl.class );
  
  @Override
  public boolean processLogoutRequest( SAMLMessageContext context, SAMLCredential credential ) throws SAMLException {
    
    try {
      return super.processLogoutRequest( context, credential );
    } catch ( SAMLStatusException e ) {
      if ( e.getStatusCode().equals( StatusCode.UNKNOWN_PRINCIPAL_URI ) && credential == null ) {
        
        logger.debug( "It appears a back channel logout is being requested" );
        
        /*
         * There is no credential, and the parent impl threw an unknown principle exception,
         * which normally results in a "No user is logged in" error message. However, if an
         * IdP initiated a back-channel SLO, regardless of binding, the direct communication
         * from IdP->SP does not have the session id of the server. If we have a Spring
         * SessionRegistry setup, we can invalidate the user session.
         */
        
        //Obtain the user's credentials
        String logoutUserId = null;
        try {
          LogoutRequest logoutRequest = (LogoutRequest) context.getInboundSAMLMessage();
          NameID nameId = getNameID(context, logoutRequest);
          logoutUserId = nameId.getValue();
          
          logger.info( "Back channel logout of " + logoutUserId + " requested" );
          
        } catch ( DecryptionException de ) {
            throw new SAMLStatusException(StatusCode.RESPONDER_URI, "The NameID can't be decrypted", de);
        }

        //Expire the user in the SessionRegistry
        if( logoutUserId != null ) {
          Object sessionRegistryObj = PentahoSystem.get( SessionRegistry.class );
          if ( sessionRegistryObj != null ) {
            SessionRegistry sessionRegistry = (SessionRegistry) sessionRegistryObj;
            for ( SessionInformation info : sessionRegistry.getAllSessions( logoutUserId, false ) ) {
              logger.info( "Expiring session information " + info );
              info.expireNow();
            }
            
            return true;
            
          } else {
            throw e;
          }
        } else {
          throw e;
        }
        
        
      } else {
        //re-throw the exception, because something other than what we were expecting occurred
        throw e;
      }
    }
    
  }

}
