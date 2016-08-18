package org.pentaho.platform.spring.security.saml.responsewrapper;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper that is applied to every request to update the <code>HttpSession<code> with
 * the <code>SecurityContext</code> when a <code>sendError()</code> or <code>sendRedirect</code>
 * happens. See SEC-398. The class contains the fields needed to call
 * <code>storeSecurityContextInSession()</code>
 */
public class SamlOnRedirectUpdateSessionResponseWrapper extends HttpServletResponseWrapper {

    private static Logger logger = LoggerFactory.getLogger( SamlOnRedirectUpdateSessionResponseWrapper.class );

    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    HttpServletRequest request;
    boolean httpSessionExistedAtStartOfRequest;
    int contextHashBeforeChainExecution;

    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    private boolean allowSessionCreation = true;

    // Used to ensure storeSecurityContextInSession() is only
    // called once.
    boolean sessionUpdateDone = false;

    Object securityContext;

    Authentication authentication;

    /**
     * Takes the parameters required to call <code>storeSecurityContextInSession()</code> in
     * addition to the response object we are wrapping.
     */
    public SamlOnRedirectUpdateSessionResponseWrapper( HttpServletResponse response, HttpServletRequest request,
        boolean httpSessionExistedAtStartOfRequest, int contextHashBeforeChainExecution, Object securityContext,
        Authentication authentication ) {
      super( response );
      this.request = request;
      this.httpSessionExistedAtStartOfRequest = httpSessionExistedAtStartOfRequest;
      this.contextHashBeforeChainExecution = contextHashBeforeChainExecution;
      this.securityContext = securityContext;
      this.authentication = authentication;
    }

    /**
     * Makes sure the session is updated before calling the
     * superclass <code>sendError()</code>
     */
    public void sendError(int sc) throws IOException {
      doSessionUpdate();
      super.sendError( sc );
    }

    /**
     * Makes sure the session is updated before calling the
     * superclass <code>sendError()</code>
     */
    public void sendError(int sc, String msg) throws IOException {
      doSessionUpdate();
      super.sendError( sc, msg );
    }

    /**
     * Makes sure the session is updated before calling the
     * superclass <code>sendRedirect()</code>
     */
    public void sendRedirect(String location) throws IOException {
      doSessionUpdate();
      super.sendRedirect( location );
    }

    /**
     * Calls <code>storeSecurityContextInSession()</code>
     */
    private void doSessionUpdate() {
      if (sessionUpdateDone) {
        return;
      }
      storeSecurityContextInSession( securityContext, request, httpSessionExistedAtStartOfRequest,
          contextHashBeforeChainExecution );
      sessionUpdateDone = true;
    }

    /**
     * Tells if the response wrapper has called
     * <code>storeSecurityContextInSession()</code>.
     */
    public boolean isSessionUpdateDone() {
      return sessionUpdateDone;
    }

    /**
     * Stores the supplied security context in the session (if available) and if it has changed since it was
     * set at the start of the request. If the AuthenticationTrustResolver identifies the current user as
     * anonymous, then the context will not be stored.
     *
     * @param securityContext the context object obtained from the SecurityContextHolder after the request has
     *        been processed by the filter chain. SecurityContextHolder.getContext() cannot be used to obtain
     *        the context as it has already been cleared by the time this method is called.
     * @param request the request object (used to obtain the session, if one exists).
     * @param httpSessionExistedAtStartOfRequest indicates whether there was a session in place before the
     *        filter chain executed. If this is true, and the session is found to be null, this indicates that it was
     *        invalidated during the request and a new session will now be created.
     * @param contextHashBeforeChainExecution the hashcode of the context before the filter chain executed.
     *        The context will only be stored if it has a different hashcode, indicating that the context changed
     *        during the request.
     *
     */
    private void storeSecurityContextInSession( Object securityContext, HttpServletRequest request,
        boolean httpSessionExistedAtStartOfRequest, int contextHashBeforeChainExecution ) {
      HttpSession httpSession = safeGetSession(request, false);

      if (httpSession == null) {
        if (httpSessionExistedAtStartOfRequest) {
          if (logger.isDebugEnabled()) {
            logger.debug("HttpSession is now null, but was not null at start of request; "
                + "session was invalidated, so do not create a new session");
          }
        } else {
          // Generate a HttpSession only if we need to

          if (!allowSessionCreation) {
            if (logger.isDebugEnabled()) {
              logger.debug("The HttpSession is currently null, and the "
                  + "HttpSessionContextIntegrationFilter is prohibited from creating an HttpSession "
                  + "(because the allowSessionCreation property is false) - SecurityContext thus not "
                  + "stored for next request");
            }
          } else {
            if (logger.isDebugEnabled()) {
              logger.debug("HttpSession is null, but SecurityContextHolder has not changed from default: ' "
                  + securityContext
                  + "'; not creating HttpSession or storing SecurityContextHolder contents");
            }
          }
        }
      }

      // If HttpSession exists, store current SecurityContextHolder contents but only if
      // the SecurityContext has actually changed (see JIRA SEC-37)
      if (httpSession != null && securityContext.hashCode() != contextHashBeforeChainExecution) {
        // See SEC-766
        if (authenticationTrustResolver.isAnonymous( authentication )) {
          if (logger.isDebugEnabled()) {
            logger.debug("SecurityContext contents are anonymous - context will not be stored in HttpSession. ");
          }
        } else {
          httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);

          if (logger.isDebugEnabled()) {
            logger.debug("SecurityContext stored to HttpSession: '" + securityContext + "'");
          }
        }
      }
    }

    private HttpSession safeGetSession(HttpServletRequest request, boolean allowCreate) {
      try {
        return request.getSession(allowCreate);
      }
      catch (IllegalStateException ignored) {
        return null;
      }
    }
}
