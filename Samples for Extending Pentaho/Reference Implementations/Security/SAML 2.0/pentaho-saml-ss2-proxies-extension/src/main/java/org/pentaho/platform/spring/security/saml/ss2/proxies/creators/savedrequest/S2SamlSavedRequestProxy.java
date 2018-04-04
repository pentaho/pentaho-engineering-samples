package org.pentaho.platform.spring.security.saml.ss2.proxies.creators.savedrequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.security.ui.savedrequest.SavedRequest;

public class S2SamlSavedRequestProxy implements Map.Entry<String,String> {
	
	protected Logger logger = LoggerFactory.getLogger( getClass() );
	protected Object target;
	
	protected Method getRequestUrlMethod;
	protected Method getServletPathMethod;
	protected Method getQueryStringMethod;

	public S2SamlSavedRequestProxy( Object target ) {

	    Assert.notNull( target );

	    this.target = target;

	    getRequestUrlMethod = ReflectionUtils.findMethod( target.getClass(), "getRequestUrl" );
	    getServletPathMethod = ReflectionUtils.findMethod( target.getClass(), "getServletPath" );
	    getQueryStringMethod = ReflectionUtils.findMethod( target.getClass(), "getQueryString" );
	}

	public boolean equals(Object o) {
	    try {
	      return ((String) getRequestUrlMethod.invoke( target )).equals((String) getRequestUrlMethod.invoke( o ))
	    		  && ((String) getQueryStringMethod.invoke( target )).equals((String) getQueryStringMethod.invoke( o ));

	    } catch ( IllegalAccessException | InvocationTargetException e ) {
	      logger.error( e.getMessage(), e );
	    }

	    return false;
	}
	
	public String getKey() {
		return "redirectURL";
	}
	
	public String getValue() {
		int index = getRequestUrl().indexOf(getServletPath());
		String value = getRequestUrl().substring(index);
		return value;
	}
	
	public int hascode() {
		return getValue().hashCode();
	}
	
	public String setValue(String value) {
		logger.error( "Cannot set value", new Exception());
		return null;
	}
	
	public String getRequestUrl() {
	    try {
	      return (String) getRequestUrlMethod.invoke( target );

	    } catch ( IllegalAccessException | InvocationTargetException e ) {
	      logger.error( e.getMessage(), e );
	    }

	    return null;
	}
	
	public String getServletPath() {
	    try {
	      return (String) getServletPathMethod.invoke( target );

	    } catch ( IllegalAccessException | InvocationTargetException e ) {
	      logger.error( e.getMessage(), e );
	    }

	    return null;
	}
	
	public String getQueryString() {
	    try {
	      return (String) getQueryStringMethod.invoke( target );

	    } catch ( IllegalAccessException | InvocationTargetException e ) {
	      logger.error( e.getMessage(), e );
	    }

	    return null;
	}
}
