package org.pentaho.platform.spring.security.saml.resources;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.util.resource.Resource;
import org.opensaml.util.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * factory that builds an appropriate org.opensaml.util.resource.Resource implementation
 */
public class MetadataResourceFactory {

  private static final Logger logger = LoggerFactory.getLogger( MetadataResourceFactory.class );

  private Map<String, String> resourceMap = new HashMap<String, String>();

  // if resourceMap is empty, we apply this fallback resource ( contained in the bundle's classpath )
  private String classpathResourceFallback;

  public MetadataResourceFactory( Map<String, String> resourceMap , String classpathResourceFallback ) {

    setResourceMap( resourceMap );
    setClasspathResourceFallback( classpathResourceFallback );

    if( getResourceMap() == null || getResourceMap().size() == 0 ){
      logger.error( "Empty orderedResourceMap! This will cause errors when creating required metadata resources!" );
    }
  }

  public Resource factoryResource() {

    Resource resource = null;

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

        Class resourceClazz = Class.forName( resourceEntry.getKey() );
        String resourcePath = resourceEntry.getValue();

        resource = ( Resource ) resourceClazz.getDeclaredConstructor( String.class ).newInstance( resourcePath );

        if ( resource.exists() ){
          break;
        } else {
          logger.warn( "Invalid/non-existent metadata resource path: '" + resourcePath + "'" );
        }

      } catch ( ClassNotFoundException | InvocationTargetException | NoSuchMethodException
          | InstantiationException | IllegalAccessException | ResourceException e ) {
        logger.warn(
            "Could not create resource class '" + resourceEntry.getKey() + "' for metadata value '" + resourceEntry
                .getValue() + "' with cause: " + e.getMessage() );
        continue;
      }
    }

    if( resource == null ) {

      try {
        logger.info( "No resource found, applying fallback in classpath: '" + getClasspathResourceFallback() + "' " );
        resource = new org.opensaml.util.resource.ClasspathResource( getClasspathResourceFallback() );

      } catch ( ResourceException e ) {
        logger.error( e.getMessage(), e );
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
