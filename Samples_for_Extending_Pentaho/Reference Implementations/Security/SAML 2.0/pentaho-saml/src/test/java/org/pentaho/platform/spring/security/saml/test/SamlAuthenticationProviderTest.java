/*!
* PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
*
* Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
*
* NOTICE: All information including source code contained herein is, and
* remains the sole property of Pentaho and its licensors. The intellectual
* and technical concepts contained herein are proprietary and confidential
* to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
* patents, or patents in process, and are protected by trade secret and
* copyright laws. The receipt or possession of this source code and/or related
* information does not convey or imply any rights to reproduce, disclose or
* distribute its contents, or to manufacture, use, or sell anything that it
* may describe, in whole or in part. Any reproduction, modification, distribution,
* or public display of this information without the express written authorization
* from Pentaho is strictly prohibited and in violation of applicable laws and
* international treaties. Access to the source code contained herein is strictly
* prohibited to anyone except those individuals and entities who have executed
* confidentiality and non-disclosure agreements or other agreements with Pentaho,
* explicitly covering such access.
*/
package org.pentaho.platform.spring.security.saml.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.NameID;
import org.pentaho.platform.spring.security.saml.PentahoSamlUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.log.SAMLEmptyLogger;
import org.springframework.security.saml.storage.SAMLMessageStorage;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SamlAuthenticationProviderTest {

  private final String VALID_USERNAME = "samlUser";

  protected SAMLAuthenticationProvider samlAuthProvider;

  protected WebSSOProfileConsumer mockWebSSOProfileConsumer;
  protected SAMLUserDetailsService mockSAMLUserDetailsService;
  protected SAMLMessageStorage mockSAMLMessageStorage;
  protected NameID mockNameID;
  protected Assertion mockAssertion;


  @Before
  public void setUp() throws Exception {

    mockWebSSOProfileConsumer = mock( WebSSOProfileConsumer.class );
    mockSAMLMessageStorage =  mock( SAMLMessageStorage.class );
    mockNameID = mock( NameID.class );
    mockAssertion = mock( Assertion.class );

    mockSAMLUserDetailsService = mock( PentahoSamlUserDetailsService.class );

    samlAuthProvider = mock( SAMLAuthenticationProvider.class, CALLS_REAL_METHODS );
    samlAuthProvider.setUserDetails( mockSAMLUserDetailsService );
    samlAuthProvider.setConsumer( mockWebSSOProfileConsumer );
    samlAuthProvider.setSamlLogger( new SAMLEmptyLogger() );
    samlAuthProvider.setForcePrincipalAsString( true ); // TODO
  }

  @Test
  public void testInvalidAuthenticationToken() {

    when( samlAuthProvider.supports( UsernamePasswordAuthenticationToken.class ) ).thenReturn( false );

    UsernamePasswordAuthenticationToken invalidToken =
        new UsernamePasswordAuthenticationToken( VALID_USERNAME, "password" );

    try{
      samlAuthProvider.authenticate( invalidToken );

      // it should have thrown a IllegalArgumentException ...
      fail( "It should have thrown a IllegalArgumentException" );

    } catch( IllegalArgumentException ex ){
      // this is expected to happen

    } catch ( Exception e ){
      fail( "It should have thrown a IllegalArgumentException, not " + e.toString() );
    }
  }



  @After
  public void tearDown() throws Exception {

    mockWebSSOProfileConsumer = null;
    mockSAMLMessageStorage = null;
    mockNameID = null;
    mockAssertion = null;
    samlAuthProvider = null;
  }
}
