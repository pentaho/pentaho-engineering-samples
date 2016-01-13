package org.pentaho.platform.spring.security.saml.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * factory that builds an appropriate org.springframework.core.io.Resource implementation
 */
public class KeystoreResourceFactory {

  private static final Logger logger = LoggerFactory.getLogger( MetadataResourceFactory.class );

  private Map<String, String> resourceMap = new HashMap<String, String>();

  // if resourceMap is empty, we apply this fallback resource ( contained in the bundle's classpath )
  private String classpathResourceFallback;

  public KeystoreResourceFactory( Map<String, String> resourceMap , String classpathResourceFallback ) {

    setResourceMap( resourceMap );
    setClasspathResourceFallback( classpathResourceFallback );

    if( getResourceMap() == null || getResourceMap().size() == 0 ){
      logger.error( "Empty orderedResourceMap! This will cause errors when creating required keystore resource!" );
    }
  }

  public Resource factoryResource() {

    Resource resource = null;

    ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();

    for ( Map.Entry<String, String> resourceEntry : getResourceMap().entrySet() ) {

      resource = null; // reset

      if ( resourceEntry.getKey() == null || resourceEntry.getKey().isEmpty() ) {
        logger.info( "resourceClass is empty. Skipping.." );
        continue;
      } else if ( resourceEntry.getValue() == null || resourceEntry.getValue().isEmpty() ) {
        logger.info( "resourcePath for resourceClass '" + resourceEntry.getKey() + "' + is empty. Skipping.." );
        continue;
      }

      try {

        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );

        Class resourceClazz = Class.forName( resourceEntry.getKey()  );
        String resourcePath = resourceEntry.getValue();

        resource = ( Resource ) resourceClazz.getDeclaredConstructor( String.class ).newInstance( resourcePath );

        if ( resource.exists() && resource.isReadable() ){
          break;
        } else {
          logger.warn( "Invalid/non-existent keystore resource path: '" + resourcePath + "'" );
        }

      } catch ( ClassNotFoundException | InvocationTargetException | NoSuchMethodException
          | InstantiationException | IllegalAccessException e ) {
        logger.warn(
            "Could not create resource class '" + resourceEntry.getKey() + "' for keystore value '" + resourceEntry
                .getValue() + "' with cause: " + e.getMessage() );
        continue;

      } finally {

        if( origClassLoader != null ){
          Thread.currentThread().setContextClassLoader( origClassLoader );
        }
      }
    }

    if( resource == null ) {

      logger.info( "No resource found, applying fallback in classpath: '" + getClasspathResourceFallback() + "' " );

      try {

        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );
        resource = new org.springframework.core.io.ClassPathResource( getClasspathResourceFallback() );
      } finally {

        if( origClassLoader != null ){
          Thread.currentThread().setContextClassLoader( origClassLoader );
        }
      }
    }

    return resource;
  }


  public Map<String, String> getResourceMap() {
    return resourceMap;
  }

  public void setResourceMap( Map<String, String> resourceMap ) {
    this.resourceMap = resourceMap;
  }

  public String getClasspathResourceFallback() {
    return classpathResourceFallback;
  }

  public void setClasspathResourceFallback( String classpathResourceFallback ) {
    this.classpathResourceFallback = classpathResourceFallback;
  }
}
