package org.pentaho.platform.spring.security.saml;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class PentahoSAMLProcessingFilter extends SAMLProcessingFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger( PentahoSAMLProcessingFilter.class );

  public boolean sessionAuthenticationStrategyFromPentaho = false;
  protected volatile boolean hasSessionAuthenticationStrategySet = false;

  public boolean isSessionAuthenticationStrategyFromPentaho() {
    return sessionAuthenticationStrategyFromPentaho;
  }

  public void setSessionAuthenticationStrategyFromPentaho( boolean sessionAuthenticationStrategyFromPentaho ) {
    this.sessionAuthenticationStrategyFromPentaho = sessionAuthenticationStrategyFromPentaho;
  }

  @Override
  public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
    throws IOException, ServletException {

    if ( !hasSessionAuthenticationStrategySet && isSessionAuthenticationStrategyFromPentaho() ) {

      synchronized ( this ) {

        if ( !hasSessionAuthenticationStrategySet ) {
          LOGGER.debug( "Setting sessionAuthenticationStrategy from Pentaho" );
          SessionAuthenticationStrategy sas = PentahoSystem.get( SessionAuthenticationStrategy.class );
          if ( sas != null ) {
            LOGGER.debug( "Set sessionAuthenticationStrategy from Pentaho to " + sas );
            setSessionAuthenticationStrategy( sas );
          }
          hasSessionAuthenticationStrategySet = true;
        }
      }
    }

    super.doFilter( req, res, chain );
  }

}
