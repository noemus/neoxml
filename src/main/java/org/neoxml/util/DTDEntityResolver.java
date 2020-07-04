package org.neoxml.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;

public class DTDEntityResolver implements EntityResolver
{
  @Override
  public InputSource resolveEntity(String publicId, String systemId) {
    if (systemId != null) {
      if (systemId.indexOf("http:") != -1) {
        return processDtdStream(publicId, systemId.substring(systemId.lastIndexOf('/')+1));
      }
      else {
        return processDtdStream(publicId, systemId);
      }
    }
    
    // use the default behaviour
    return null;
  }
  
  @SuppressWarnings("resource")
  private InputSource processDtdStream(String publicId, String systemId) {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final InputStream dtdStream = classLoader.getResourceAsStream(systemId);
    
    if (dtdStream != null) {
      final InputSource source = new InputSource(dtdStream);
      
      source.setPublicId(publicId);
      source.setSystemId(systemId);
      
      return source;
    }
    
    return null;
  }
}







