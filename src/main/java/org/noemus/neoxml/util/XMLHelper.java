package org.noemus.neoxml.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.noemus.neoxml.DocumentException;
import org.noemus.neoxml.io.DOMReader;
import org.noemus.neoxml.io.OutputFormat;
import org.noemus.neoxml.io.SAXReader;
import org.noemus.neoxml.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class XMLHelper
{
  private static final boolean mergeAdjacentText = true;
  private static final boolean validation = false;
  private static final boolean schemaValidation = false;
  private static final boolean stripWhitespaceText = true;
  private static final boolean externalDTDDeclarations = false;
  private static final boolean internalDTDDeclarations = false;
  
  public static final String DEFAULT_ENCODING = "UTF-8";
  
  /**
   * Creates xml Document from classpath resource path
   *
   * @param classpath
   * @return org.noemus.neoxml.Document instance
   * @throws DocumentException
   */
  public static org.noemus.neoxml.Document createDocumentFromResource(String classpath) throws DocumentException {
    return createDocumentFromResource(classpath, null);
  }
  
  /**
   * Creates xml Document from classpath resource path
   *
   * @param classpath
   * @param encoding
   * @return org.noemus.neoxml.Document instance
   * @throws DocumentException
   */
  public static org.noemus.neoxml.Document createDocumentFromResource(String classpath, String encoding) throws DocumentException {
    try (InputStream xmlInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath)) {
      return createDocumentFromStream(classpath, xmlInputStream, encoding);
    }
    catch (IOException e) {
      throw new DocumentException(e);
    }
  }
  
  /**
   * Creates xml Document from reader
   *
   * @param reader
   * @return org.noemus.neoxml.Document instance
   * @throws DocumentException
   */
  public static org.noemus.neoxml.Document createDocumentFromReader(Reader reader) throws DocumentException {
    return createDocumentFromSource(reader.toString(), new InputSource(reader), null);
  }
  
  /**
   * Creates xml Document from reader
   *
   * @param reader
   * @param encoding
   * @return org.noemus.neoxml.Document instance
   * @throws DocumentException
   */
  public static org.noemus.neoxml.Document createDocumentFromReader(Reader reader, String encoding) throws DocumentException {
    return createDocumentFromSource(reader.toString(), new InputSource(reader), encoding);
  }
  
  /**
   * Creates xml Document from input stream
   *
   * @param in
   * @return org.noemus.neoxml.Document instance
   * @throws DocumentException
   */
  public static org.noemus.neoxml.Document createDocumentFromStream(InputStream in) throws DocumentException {
    return createDocumentFromStream(in.toString(), in, null);
  }
  
  /**
   * Creates xml Document from input stream
   *
   * @param in
   * @param encoding
   * @return org.noemus.neoxml.Document instance
   * @throws DocumentException
   */
  public static org.noemus.neoxml.Document createDocumentFromStream(InputStream in, String encoding) throws DocumentException {
    return createDocumentFromStream(in.toString(), in, encoding);
  }
  
  /**
   * Writes document to file
   *
   * @param doc xml Document
   * @param file
   * @throws IOException
   */
  public static void writeDocument(org.noemus.neoxml.Document doc, Path file) throws IOException {
    writeDocument(doc, createWriter(file));
  }
  
  /**
   * Writes document to Path
   *
   * @param doc xml Document
   * @param file
   * @param encoding
   * @throws IOException
   */
  public static void writeDocument(org.noemus.neoxml.Document doc, Path file, String encoding) throws IOException {
    writeDocument(doc, createWriter(file, encoding));
  }
  
  /**
   * Writes document to file
   *
   * @param doc xml Document
   * @param file
   * @throws IOException
   */
  public static void writeDocument(org.noemus.neoxml.Document doc, File file) throws IOException {
    writeDocument(doc, createWriter(file));
  }
  
  /**
   * Writes document to file
   *
   * @param doc xml Document
   * @param file
   * @param encoding
   * @throws IOException
   */
  public static void writeDocument(org.noemus.neoxml.Document doc, File file, String encoding) throws IOException {
    writeDocument(doc, createWriter(file, encoding));
  }
  
  /**
   * Writes document to stream
   *
   * @param doc xml Document
   * @param out output stream
   * @throws IOException
   */
  public static void writeDocument(org.noemus.neoxml.Document doc, OutputStream out) throws IOException {
    writeDocument(doc, out, DEFAULT_ENCODING);
  }
  
  /**
   * Writes document to stream
   *
   * @param doc xml Document
   * @param out output stream
   * @param encoding
   * @throws IOException
   */
  public static void writeDocument(org.noemus.neoxml.Document doc, OutputStream out, String encoding) throws IOException {
    writeDocument(doc, createWriter(out, encoding));
  }
  
  private static void writeDocument(org.noemus.neoxml.Document doc, XMLWriter xml) throws IOException {
    xml.write(doc);
    xml.flush();
    xml.close();
  }
  
  /**
   * @param file
   * @return new XMLWriter instance
   * @throws FileNotFoundException
   */
  public static XMLWriter createWriter(Path file) throws FileNotFoundException {
    return createWriter(file, DEFAULT_ENCODING);
  }
  
  /**
   * @param file
   * @param encoding
   * @return new XMLWriter instance
   * @throws FileNotFoundException
   */
  @SuppressWarnings("resource")
  public static XMLWriter createWriter(Path file, String encoding) throws FileNotFoundException {
    return createWriter(new FileOutputStream(file.toFile()), encoding);
  }
  
  /**
   * @param file
   * @return new XMLWriter instance
   * @throws FileNotFoundException
   */
  public static XMLWriter createWriter(File file) throws FileNotFoundException {
    return createWriter(file, DEFAULT_ENCODING);
  }
  
  /**
   * @param file
   * @param encoding
   * @return new XMLWriter instance
   * @throws FileNotFoundException
   */
  @SuppressWarnings("resource")
  public static XMLWriter createWriter(File file, String encoding) throws FileNotFoundException {
    return createWriter(new FileOutputStream(file), encoding);
  }
  
  /**
   * @param stream
   * @return new XMLWriter instance
   * @throws FileNotFoundException
   */
  public static XMLWriter createWriter(OutputStream out) {
    return createWriter(out, DEFAULT_ENCODING);
  }
  
  /**
   * @param stream
   * @param encoding
   * @return new XMLWriter instance
   * @throws FileNotFoundException
   */
  public static XMLWriter createWriter(OutputStream out, String encoding) {
    final OutputFormat format = defaultOutputFormat();
    return createWriter(out, encoding, format);
  }
  
  /**
   * @param stream
   * @param encoding
   * @param format
   * @return new XMLWriter instance
   * @throws FileNotFoundException
   */
  public static XMLWriter createWriter(OutputStream out, String encoding, OutputFormat format) {
    return new XMLWriter(new OutputStreamWriter(out, Charset.forName(encoding)), format); //NOSONAR
  }
  
  /**
   * @return default output format scpecification for use with XMLWriter
   */
  public static OutputFormat defaultOutputFormat() {
    final OutputFormat format = new OutputFormat("  ", false);
    format.setExpandEmptyElements(false);
    return format;
  }
  
  private static org.noemus.neoxml.Document createDocumentFromStream(String desc, InputStream in, String encoding) throws DocumentException {
    return createDocumentFromSource(desc, new InputSource(in), encoding);
  }
  
  private static org.noemus.neoxml.Document createDocumentFromSource(String desc, InputSource input, String encoding) throws DocumentException {
    if (encoding != null) {
      input.setEncoding(encoding);
    }
    
    final List<SAXParseException> errors = new ArrayList<>();
    
    final org.noemus.neoxml.Document doc = createSAXReader(desc, errors).read(input);
    
    if (!errors.isEmpty()) {
      final SAXParseException saxError = errors.get(0);
      throw new DocumentException ("failed to parse xml file", saxError);
    }
    
    return doc;
  }
  
  
  
  /**
   * Create a neoxml SAXReader which will append all validation errors	to errorList
   */
  public static SAXReader createSAXReader(String file, List<SAXParseException> errorsList) {
    final SAXReader reader = new SAXReader(schemaValidation || validation);
    
    reader.setEntityResolver(DTD_RESOLVER);
    reader.setErrorHandler( new ErrorLogger(file, errorsList) );
    reader.setMergeAdjacentText(mergeAdjacentText);
    reader.setStripWhitespaceText(stripWhitespaceText);
    reader.setIncludeExternalDTDDeclarations(externalDTDDeclarations);
    reader.setIncludeInternalDTDDeclarations(internalDTDDeclarations);
    
    if (schemaValidation) {
      try {
        reader.setFeature("http://apache.org/xml/features/validation/schema", true);
      }
      catch (SAXException e) {
        // ignore
      }
    }
    
    return reader;
  }
  
  /**
   * Create a neoxml DOMReader
   */
  public static DOMReader createDOMReader() {
    return new DOMReader();
  }
  
  static final Log log = LogFactory.getLog(XMLHelper.class);
  static final EntityResolver DTD_RESOLVER = new DTDEntityResolver();
  
  static class ErrorLogger implements ErrorHandler
  {
    private final String file;
    private final List<SAXParseException> errors;
    
    ErrorLogger(String file, List<SAXParseException> errors) {
      this.file = file;
      this.errors = errors;
    }
    
    @Override
    public void error(SAXParseException error) {
      log.error( "[" + file + ",line " + error.getLineNumber() + "]: " + error.getMessage() );
      if (errors != null) {
        errors.add(error);
      }
    }
    
    @Override
    public void fatalError(SAXParseException error) {
      error(error);
    }
    
    @Override
    public void warning(SAXParseException warn) {
      log.warn( "[" + file + ",line " + warn.getLineNumber() + "]: " + warn.getMessage() );
    }
  }
  
  //cannot be instantiated
  private XMLHelper() {}
}






