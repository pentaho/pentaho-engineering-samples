package org.pentaho.platform.spring.security.saml.ss4.proxies.creators.savedrequest;

import org.pentaho.platform.proxy.api.IProxyCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;


public class S4SamlSavedRequestProxyCreator implements IProxyCreator<Map.Entry<String,String>> {

	private Logger logger = LoggerFactory.getLogger( getClass() );

	public S4SamlSavedRequestProxyCreator() {
	    logger.info( "S4SamlSavedRequestProxyCreator ready" );
	}

	public boolean supports( Class aClass ) {
	    /*
	     * Supports the *custom* counterpart of spring-security-saml's SamlAuthenticationToken; Note that this
	     * is a spring-security-saml specific Authentication implementation and there is no direct spring-security 2.x
	     * counterpart to this object ( spring-security-saml requires spring-security 3.x )
	     */
	    return "org.pentaho.platform.spring.security.saml.ss2.proxies.creators.savedrequest.S2SamlSavedRequestProxy".equals( aClass.getName() );
	}

	public Map.Entry<String,String> create( Object o ) {

	    try {

	    	Method getKeyMethod = ReflectionUtils.findMethod( o.getClass(), "getKey" );
	    	Method getValueMethod = ReflectionUtils.findMethod( o.getClass(), "getValue");

	    	return new AbstractMap.SimpleEntry<String, String>((String) getKeyMethod.invoke( o ),(String) getValueMethod.invoke( o ));

	    } catch ( IllegalAccessException | InvocationTargetException e ) {
	    	logger.error( e.getMessage(), e );
	    }
	    
	    return null;
	}
	
}
