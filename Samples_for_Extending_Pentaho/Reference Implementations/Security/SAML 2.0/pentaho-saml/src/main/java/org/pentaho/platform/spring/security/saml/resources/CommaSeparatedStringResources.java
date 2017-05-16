package org.pentaho.platform.spring.security.saml.resources;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommaSeparatedStringResources {

  private final static String SEPARATOR = ",";

  private String commaSeparatedStringResource;

  /*
   * A char used as a separator inside each of the comma-separated resources.
   * Example: if the delimiter char would be '=', then the commaSeparatedStringResource would be something like:
   * admin=Administrator,pat=Report Author,tiffany=ceo, ...
   */
  private String inResourceDelimiterChar;

  public CommaSeparatedStringResources( String commaSeparatedStringResource ) {
    this.commaSeparatedStringResource = commaSeparatedStringResource;
    Assert.notNull( getCommaSeparatedStringResource() );
  }


  public Map<String,String> toResourceMap() {

    Assert.notNull( getInResourceDelimiterChar() );

    List<String> resourceList = toResourceList();

    Map<String, String> resourceMap = new HashMap<>();

    for( String resource : resourceList ) {

      if( resource.contains( getInResourceDelimiterChar() ) ) {
        String[] inResourceArray = resource.split( getInResourceDelimiterChar() );
        resourceMap.put( inResourceArray[0] , (  inResourceArray.length > 1 ? inResourceArray[1] : "" ) );
      } else {
        resourceMap.put( resource, "" );
      }
    }

    return resourceMap;
  }

  public List<String> toResourceList() {

    String[] rawResourceArray = getCommaSeparatedStringResource().split( SEPARATOR );

    List<String> sanitizedResourceList = new ArrayList<>();

    if( rawResourceArray != null && rawResourceArray.length > 0 ){

      for( String rawResource : rawResourceArray ){
        if( rawResource != null && !rawResource.isEmpty() ){
          sanitizedResourceList.add( rawResource.trim() );
        }
      }
    }

    return sanitizedResourceList;
  }

  public String getInResourceDelimiterChar() {
    return inResourceDelimiterChar;
  }

  public void setInResourceDelimiterChar( String inResourceDelimiterChar ) {
    this.inResourceDelimiterChar = inResourceDelimiterChar;
  }

  public String getCommaSeparatedStringResource() {
    return commaSeparatedStringResource;
  }

  public void setCommaSeparatedStringResource( String commaSeparatedStringResource ) {
    this.commaSeparatedStringResource = commaSeparatedStringResource;
  }
}
