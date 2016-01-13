package org.pentaho.platform.spring.security.saml.logout;

import org.pentaho.platform.spring.security.saml.Utils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PentahoSamlSuccessLogoutHandler extends SimpleUrlLogoutSuccessHandler {

  public void onLogoutSuccess( HttpServletRequest request, HttpServletResponse response, Authentication auth ) throws
      IOException, ServletException {

    if( auth != null ) {
      Utils.getUserMap().remove( auth.getPrincipal().toString() );
    }

    super.handle( request, response, auth );
  }
}
