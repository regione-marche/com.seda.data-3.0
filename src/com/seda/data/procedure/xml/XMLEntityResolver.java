package com.seda.data.procedure.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.seda.commons.resource.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Offline entity resolver for the DTDs
 */
public class XMLEntityResolver implements EntityResolver {

  private static final Map<String, String> doctypeMap = new HashMap<String, String>();

  private static final String PROCEDURE_SYSTEM_DOCTYPE = "-//procedure.org//DTD System 1.0//EN".toUpperCase(Locale.ENGLISH);
  private static final String PROCEDURE_SYSTEM_URL = "http://procedure.org/dtd/procedure-1-system.dtd".toUpperCase(Locale.ENGLISH);

  private static final String PROCEDURE_PACKAGE_DOCTYPE = "-//procedure.org//DTD Package 1.0//EN".toUpperCase(Locale.ENGLISH);
  private static final String PROCEDURE_PACKAGE_URL = "http://procedure.org/dtd/procedure-1-package.dtd".toUpperCase(Locale.ENGLISH);

  private static final String PROCEDURE_SYSTEM_DTD = "com/seda/data/procedure/xml/procedure-1-system.dtd";
  private static final String PROCEDURE_PACKAGE_DTD = "com/seda/data/procedure/xml/procedure-1-package.dtd";

  static {
    doctypeMap.put(PROCEDURE_SYSTEM_URL, PROCEDURE_SYSTEM_DTD);
    doctypeMap.put(PROCEDURE_SYSTEM_DOCTYPE, PROCEDURE_SYSTEM_DTD);

    doctypeMap.put(PROCEDURE_PACKAGE_URL, PROCEDURE_PACKAGE_DTD);
    doctypeMap.put(PROCEDURE_PACKAGE_DOCTYPE, PROCEDURE_PACKAGE_DTD);
  }

  /**
   * Converts a public DTD into a local one
   *
   * @param publicId Unused but required by EntityResolver interface
   * @param systemId The DTD that is being requested
   * @return The InputSource for the DTD
   * @throws org.xml.sax.SAXException If anything goes wrong
   */
  public InputSource resolveEntity(String publicId, String systemId)
      throws SAXException {

    if (publicId != null) publicId = publicId.toUpperCase(Locale.ENGLISH);
    if (systemId != null) systemId = systemId.toUpperCase(Locale.ENGLISH);

    InputSource source = null;
    try {
      String path = doctypeMap.get(publicId);
      source = getInputSource(path, source);
      if (source == null) {
        path = doctypeMap.get(systemId);
        source = getInputSource(path, source);
      }
    } catch (Exception e) {
      throw new SAXException(e.toString());
    }
    return source;
  }

  private InputSource getInputSource(String path, InputSource source) {
    if (path != null) {
      InputStream in;
      try {
        in = ResourceManager.getResourceAsStream(path);
        source = new InputSource(in);
      } catch (IOException e) {
        // ignore, null is ok
      }
    }
    return source;
  }

}