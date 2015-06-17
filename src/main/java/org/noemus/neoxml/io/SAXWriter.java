/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.noemus.neoxml.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.noemus.neoxml.Attribute;
import org.noemus.neoxml.Branch;
import org.noemus.neoxml.CDATA;
import org.noemus.neoxml.CharacterData;
import org.noemus.neoxml.Comment;
import org.noemus.neoxml.Document;
import org.noemus.neoxml.DocumentType;
import org.noemus.neoxml.Element;
import org.noemus.neoxml.Entity;
import org.noemus.neoxml.Namespace;
import org.noemus.neoxml.Node;
import org.noemus.neoxml.NodeType;
import org.noemus.neoxml.ProcessingInstruction;
import org.noemus.neoxml.Text;
import org.noemus.neoxml.tree.NamespaceStack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

/**
 * <p>
 * <code>SAXWriter</code> writes a DOM4J tree to a SAX ContentHandler.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.24 $
 */
public class SAXWriter implements XMLReader
{
  protected static final String[] LEXICAL_HANDLER_NAMES = {
    "http://xml.org/sax/properties/lexical-handler",
    "http://xml.org/sax/handlers/LexicalHandler"
  };

  protected static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

  protected static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";

  /**
   * <code>ContentHandler</code> to which SAX events are raised
   */
  private ContentHandler contentHandler;

  /**
   * <code>DTDHandler</code> fired when a document has a DTD
   */
  private DTDHandler dtdHandler;

  /**
   * <code>EntityResolver</code> fired when a document has a DTD
   */
  private EntityResolver entityResolver;

  private ErrorHandler errorHandler;

  /**
   * <code>LexicalHandler</code> fired on Entity and CDATA sections
   */
  private LexicalHandler lexicalHandler;

  /**
   * <code>AttributesImpl</code> used when generating the Attributes
   */
  private AttributesImpl attributes = new AttributesImpl();

  /**
   * Stores the features
   */
  private Map<String,Boolean> features = new HashMap<>();

  /**
   * Stores the properties
   */
  private Map<String,Object> properties = new HashMap<>();

  /**
   * Whether namespace declarations are exported as attributes or not
   */
  private boolean declareNamespaceAttributes;

  public SAXWriter() {
    properties.put(FEATURE_NAMESPACE_PREFIXES, false);
    properties.put(FEATURE_NAMESPACE_PREFIXES, true);
  }

  public SAXWriter(ContentHandler contentHandler) {
    this();
    this.contentHandler = contentHandler;
  }

  public SAXWriter(ContentHandler contentHandler,
      LexicalHandler lexicalHandler) {
    this();
    this.contentHandler = contentHandler;
    this.lexicalHandler = lexicalHandler;
  }

  public SAXWriter(ContentHandler contentHandler,
      LexicalHandler lexicalHandler, EntityResolver entityResolver) {
    this();
    this.contentHandler = contentHandler;
    this.lexicalHandler = lexicalHandler;
    this.entityResolver = entityResolver;
  }

  /**
   * A polymorphic method to write any Node to this SAX stream
   *
   * @param node DOCUMENT ME!
   * @throws SAXException DOCUMENT ME!
   */
  public void write(Node node) throws SAXException {
    NodeType nodeType = node.getNodeTypeEnum();

    switch (nodeType) {
      case ELEMENT_NODE:
        write((Element)node);

        break;

      case ATTRIBUTE_NODE:
        write(node);

        break;

      case TEXT_NODE:
        write(node.getText());

        break;

      case CDATA_SECTION_NODE:
        write((CDATA)node);

        break;

      case ENTITY_REFERENCE_NODE:
        write((Entity)node);

        break;

      case PROCESSING_INSTRUCTION_NODE:
        write((ProcessingInstruction)node);

        break;

      case COMMENT_NODE:
        write((Comment)node);

        break;

      case DOCUMENT_NODE:
        write((Document)node);

        break;

      case DOCUMENT_TYPE_NODE:
        write(node);

        break;

      case NAMESPACE_NODE:

        // Will be output with attributes
        // write((Namespace) node);
        break;

      default:
        throw new SAXException("Invalid node type: " + node);
    }
  }

  /**
   * Generates SAX events for the given Document and all its content
   *
   * @param document is the Document to parse
   * @throws SAXException if there is a SAX error processing the events
   */
  public void write(Document document) throws SAXException {
    if (document != null) {
      checkForNullHandlers();

      documentLocator(document);
      startDocument();
      entityResolver(document);
      dtdHandler(document);

      writeContent(document, new NamespaceStack());
      endDocument();
    }
  }

  /**
   * Generates SAX events for the given Element and all its content
   *
   * @param element is the Element to parse
   * @throws SAXException if there is a SAX error processing the events
   */
  public void write(Element element) throws SAXException {
    write(element, new NamespaceStack());
  }

  /**
   * <p>
   * Writes the opening tag of an {@link Element}, including its {@link Attribute}s but without its content.
   * </p>
   *
   * @param element <code>Element</code> to output.
   * @throws SAXException DOCUMENT ME!
   */
  public void writeOpen(Element element) throws SAXException {
    startElement(element, null);
  }

  /**
   * <p>
   * Writes the closing tag of an {@link Element}
   * </p>
   *
   * @param element <code>Element</code> to output.
   * @throws SAXException DOCUMENT ME!
   */
  public void writeClose(Element element) throws SAXException {
    endElement(element);
  }

  /**
   * Generates SAX events for the given text
   *
   * @param text is the text to send to the SAX ContentHandler
   * @throws SAXException if there is a SAX error processing the events
   */
  public void write(String text) throws SAXException {
    if (text != null) {
      char[] chars = text.toCharArray();
      contentHandler.characters(chars, 0, chars.length);
    }
  }

  /**
   * Generates SAX events for the given CDATA
   *
   * @param cdata is the CDATA to parse
   * @throws SAXException if there is a SAX error processing the events
   */
  public void write(CDATA cdata) throws SAXException {
    String text = cdata.getText();

    if (lexicalHandler != null) {
      lexicalHandler.startCDATA();
      write(text);
      lexicalHandler.endCDATA();
    }
    else {
      write(text);
    }
  }

  /**
   * Generates SAX events for the given Comment
   *
   * @param comment is the Comment to parse
   * @throws SAXException if there is a SAX error processing the events
   */
  public void write(Comment comment) throws SAXException {
    if (lexicalHandler != null) {
      String text = comment.getText();
      char[] chars = text.toCharArray();
      lexicalHandler.comment(chars, 0, chars.length);
    }
  }

  /**
   * Generates SAX events for the given Entity
   *
   * @param entity is the Entity to parse
   * @throws SAXException if there is a SAX error processing the events
   */
  public void write(Entity entity) throws SAXException {
    String text = entity.getText();

    if (lexicalHandler != null) {
      String name = entity.getName();
      lexicalHandler.startEntity(name);
      write(text);
      lexicalHandler.endEntity(name);
    }
    else {
      write(text);
    }
  }

  /**
   * Generates SAX events for the given ProcessingInstruction
   *
   * @param pi is the ProcessingInstruction to parse
   * @throws SAXException if there is a SAX error processing the events
   */
  public void write(ProcessingInstruction pi) throws SAXException {
    String target = pi.getTarget();
    String text = pi.getText();
    contentHandler.processingInstruction(target, text);
  }

  /**
   * Should namespace declarations be converted to "xmlns" attributes. This
   * property defaults to <code>false</code> as per the SAX specification.
   * This property is set via the SAX feature
   * "http://xml.org/sax/features/namespace-prefixes"
   *
   * @return DOCUMENT ME!
   */
  public boolean isDeclareNamespaceAttributes() {
    return declareNamespaceAttributes;
  }

  /**
   * Sets whether namespace declarations should be exported as "xmlns"
   * attributes or not. This property is set from the SAX feature
   * "http://xml.org/sax/features/namespace-prefixes"
   *
   * @param declareNamespaceAttrs DOCUMENT ME!
   */
  public void setDeclareNamespaceAttributes(boolean declareNamespaceAttrs) {
    this.declareNamespaceAttributes = declareNamespaceAttrs;
  }

  // XMLReader methods
  // -------------------------------------------------------------------------

  /**
   * DOCUMENT ME!
   *
   * @return the <code>ContentHandler</code> called when SAX events are
   *         raised
   */
  @Override
  public ContentHandler getContentHandler() {
    return contentHandler;
  }

  /**
   * Sets the <code>ContentHandler</code> called when SAX events are raised
   *
   * @param contentHandler is the <code>ContentHandler</code> called when SAX events
   *          are raised
   */
  @Override
  public void setContentHandler(ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  /**
   * DOCUMENT ME!
   *
   * @return the <code>DTDHandler</code>
   */
  @Override
  public DTDHandler getDTDHandler() {
    return dtdHandler;
  }

  /**
   * Sets the <code>DTDHandler</code>.
   *
   * @param handler DOCUMENT ME!
   */
  @Override
  public void setDTDHandler(DTDHandler handler) {
    this.dtdHandler = handler;
  }

  /**
   * DOCUMENT ME!
   *
   * @return the <code>ErrorHandler</code>
   */
  @Override
  public ErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * Sets the <code>ErrorHandler</code>.
   *
   * @param errorHandler DOCUMENT ME!
   */
  @Override
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  /**
   * DOCUMENT ME!
   *
   * @return the <code>EntityResolver</code> used when a Document contains a
   *         DTD
   */
  @Override
  public EntityResolver getEntityResolver() {
    return entityResolver;
  }

  /**
   * Sets the <code>EntityResolver</code>.
   *
   * @param entityResolver is the <code>EntityResolver</code>
   */
  @Override
  public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  /**
   * DOCUMENT ME!
   *
   * @return the <code>LexicalHandler</code> used when a Document contains a
   *         DTD
   */
  public LexicalHandler getLexicalHandler() {
    return lexicalHandler;
  }

  /**
   * Sets the <code>LexicalHandler</code>.
   *
   * @param lexicalHandler is the <code>LexicalHandler</code>
   */
  public void setLexicalHandler(LexicalHandler lexicalHandler) {
    this.lexicalHandler = lexicalHandler;
  }

  /**
   * Sets the <code>XMLReader</code> used to write SAX events to
   *
   * @param xmlReader is the <code>XMLReader</code>
   */
  public void setXMLReader(XMLReader xmlReader) {
    setContentHandler(xmlReader.getContentHandler());
    setDTDHandler(xmlReader.getDTDHandler());
    setEntityResolver(xmlReader.getEntityResolver());
    setErrorHandler(xmlReader.getErrorHandler());
  }

  /**
   * Looks up the value of a feature.
   *
   * @param name DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws SAXNotRecognizedException DOCUMENT ME!
   * @throws SAXNotSupportedException DOCUMENT ME!
   */
  @Override
  public boolean getFeature(String name) throws SAXNotRecognizedException,
  SAXNotSupportedException {
    Boolean answer = features.get(name);

    return (answer != null) && answer.booleanValue();
  }

  /**
   * This implementation does actually use any features but just stores them
   * for later retrieval
   *
   * @param name DOCUMENT ME!
   * @param value DOCUMENT ME!
   * @throws SAXNotRecognizedException DOCUMENT ME!
   * @throws SAXNotSupportedException DOCUMENT ME!
   */
  @Override
  public void setFeature(String name, boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    if (FEATURE_NAMESPACE_PREFIXES.equals(name)) {
      setDeclareNamespaceAttributes(value);
    }
    else if (FEATURE_NAMESPACE_PREFIXES.equals(name)) {
      if (!value) {
        String msg = "Namespace feature is always supported in dom4j";
        throw new SAXNotSupportedException(msg);
      }
    }

    features.put(name, value);
  }

  /**
   * Sets the given SAX property
   *
   * @param name DOCUMENT ME!
   * @param value DOCUMENT ME!
   */
  @Override
  public void setProperty(String name, Object value) {
    for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
      if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
        setLexicalHandler((LexicalHandler)value);

        return;
      }
    }

    properties.put(name, value);
  }

  /**
   * Gets the given SAX property
   *
   * @param name DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws SAXNotRecognizedException DOCUMENT ME!
   * @throws SAXNotSupportedException DOCUMENT ME!
   */
  @Override
  public Object getProperty(String name) throws SAXNotRecognizedException,
  SAXNotSupportedException {
    for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
      if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
        return getLexicalHandler();
      }
    }

    return properties.get(name);
  }

  /**
   * This method is not supported.
   *
   * @param systemId DOCUMENT ME!
   * @throws SAXNotSupportedException DOCUMENT ME!
   */
  @Override
  public void parse(String systemId) throws SAXNotSupportedException {
    throw new SAXNotSupportedException("This XMLReader can only accept"
        + " <dom4j> InputSource objects");
  }

  /**
   * Parses an XML document. This method can only accept DocumentInputSource
   * inputs otherwise a {@link SAXNotSupportedException}exception is thrown.
   *
   * @param input DOCUMENT ME!
   * @throws SAXException DOCUMENT ME!
   * @throws SAXNotSupportedException if the input source is not wrapping a dom4j document
   */
  @Override
  public void parse(InputSource input) throws SAXException {
    if (input instanceof DocumentInputSource) {
      DocumentInputSource documentInput = (DocumentInputSource)input;
      Document document = documentInput.getDocument();
      write(document);
    }
    else {
      throw new SAXNotSupportedException(
        "This XMLReader can only accept "
            + "<dom4j> InputSource objects");
    }
  }

  // Implementation methods
  // -------------------------------------------------------------------------

  protected void writeContent(Branch branch, NamespaceStack namespaceStack)
      throws SAXException {
    for (Node node : branch) {
      if (node instanceof Element) {
        write((Element)node, namespaceStack);
      }
      else if (node instanceof CharacterData) {
        if (node instanceof Text) {
          Text text = (Text)node;
          write(text.getText());
        }
        else if (node instanceof CDATA) {
          write((CDATA)node);
        }
        else if (node instanceof Comment) {
          write((Comment)node);
        }
        else {
          throw new SAXException("Invalid Node in DOM4J content: "
              + node + " of type: " + node.getClass());
        }
      }
      else if (node instanceof Entity) {
        write((Entity)node);
      }
      else if (node instanceof ProcessingInstruction) {
        write((ProcessingInstruction)node);
      }
      else if (node instanceof Namespace) {
        write(node);
      }
      else {
        throw new SAXException("Invalid Node in DOM4J content: "
            + node);
      }
    }
  }

  /**
   * The {@link org.xml.sax.Locator}is only really useful when parsing a
   * textual document as its main purpose is to identify the line and column
   * number. Since we are processing an in memory tree which will probably
   * have its line number information removed, we'll just use -1 for the line
   * and column numbers.
   *
   * @param document DOCUMENT ME!
   * @throws SAXException DOCUMENT ME!
   */
  protected void documentLocator(Document document) {
    LocatorImpl locator = new LocatorImpl();

    String publicID = null;
    String systemID = null;
    DocumentType docType = document.getDocType();

    if (docType != null) {
      publicID = docType.getPublicID();
      systemID = docType.getSystemID();
    }

    if (publicID != null) {
      locator.setPublicId(publicID);
    }

    if (systemID != null) {
      locator.setSystemId(systemID);
    }

    locator.setLineNumber(-1);
    locator.setColumnNumber(-1);

    contentHandler.setDocumentLocator(locator);
  }

  protected void entityResolver(Document document) throws SAXException {
    if (entityResolver != null) {
      DocumentType docType = document.getDocType();

      if (docType != null) {
        String publicID = docType.getPublicID();
        String systemID = docType.getSystemID();

        if ((publicID != null) || (systemID != null)) {
          try {
            entityResolver.resolveEntity(publicID, systemID);
          }
          catch (IOException e) {
            throw new SAXException("Could not resolve publicID: " + publicID + " systemID: " + systemID, e);
          }
        }
      }
    }
  }

  /**
   * We do not yet support DTD or XML Schemas so this method does nothing
   * right now.
   *
   * @param document DOCUMENT ME!
   * @throws SAXException DOCUMENT ME!
   */
  protected void dtdHandler(Document document) {}

  protected void startDocument() throws SAXException {
    contentHandler.startDocument();
  }

  protected void endDocument() throws SAXException {
    contentHandler.endDocument();
  }

  protected void write(Element element, NamespaceStack namespaceStack)
      throws SAXException {
    int stackSize = namespaceStack.size();
    AttributesImpl namespaceAttributes = startPrefixMapping(element,
      namespaceStack);
    startElement(element, namespaceAttributes);
    writeContent(element, namespaceStack);
    endElement(element);
    endPrefixMapping(namespaceStack, stackSize);
  }

  /**
   * Fires a SAX startPrefixMapping event for all the namespaceStack which
   * have just come into scope
   *
   * @param element DOCUMENT ME!
   * @param namespaceStack DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws SAXException DOCUMENT ME!
   */
  protected AttributesImpl startPrefixMapping(Element element,
      NamespaceStack namespaceStack) throws SAXException {
    AttributesImpl namespaceAttributes = null;

    // start with the namespace of the element
    Namespace elementNamespace = element.getNamespace();

    if ((elementNamespace != null)
        && !isIgnoreableNamespace(elementNamespace, namespaceStack)) {
      namespaceStack.push(elementNamespace);
      contentHandler.startPrefixMapping(elementNamespace.getPrefix(),
        elementNamespace.getURI());
      namespaceAttributes = addNamespaceAttribute(namespaceAttributes,
        elementNamespace);
    }

    for (Namespace namespace : element.declaredNamespaces()) {
      if (!isIgnoreableNamespace(namespace, namespaceStack)) {
        namespaceStack.push(namespace);
        contentHandler.startPrefixMapping(namespace.getPrefix(), namespace.getURI());
        namespaceAttributes = addNamespaceAttribute(namespaceAttributes, namespace);
      }
    }

    return namespaceAttributes;
  }

  /**
   * Fires a SAX endPrefixMapping event for all the namespaceStack which have
   * gone out of scope
   *
   * @param stack DOCUMENT ME!
   * @param stackSize DOCUMENT ME!
   * @throws SAXException DOCUMENT ME!
   */
  protected void endPrefixMapping(NamespaceStack stack, int stackSize)
      throws SAXException {
    while (stack.size() > stackSize) {
      Namespace namespace = stack.pop();

      if (namespace != null) {
        contentHandler.endPrefixMapping(namespace.getPrefix());
      }
    }
  }

  protected void startElement(Element element,
      AttributesImpl namespaceAttributes) throws SAXException {
    contentHandler.startElement(element.getNamespaceURI(), element
      .getName(), element.getQualifiedName(), createAttributes(
        element, namespaceAttributes));
  }

  protected void endElement(Element element) throws SAXException {
    contentHandler.endElement(element.getNamespaceURI(), element.getName(),
      element.getQualifiedName());
  }

  protected Attributes createAttributes(Element element, Attributes namespaceAttributes) {
    attributes.clear();

    if (namespaceAttributes != null) {
      attributes.setAttributes(namespaceAttributes);
    }

    for (Attribute attribute : element.attributes()) {
      attributes.addAttribute(
        attribute.getNamespaceURI(),
        attribute.getName(),
        attribute.getQualifiedName(),
        "CDATA",
        attribute.getValue());
    }

    return attributes;
  }

  /**
   * If isDelcareNamespaceAttributes() is enabled then this method will add
   * the given namespace declaration to the supplied attributes object,
   * creating one if it does not exist.
   *
   * @param attrs DOCUMENT ME!
   * @param namespace DOCUMENT ME!
   * @return DOCUMENT ME!
   */
  protected AttributesImpl addNamespaceAttribute(AttributesImpl attrs,
      Namespace namespace) {
    if (declareNamespaceAttributes) {
      if (attrs == null) {
        attrs = new AttributesImpl();
      }

      String prefix = namespace.getPrefix();
      String qualifiedName = "xmlns";

      if ((prefix != null) && (prefix.length() > 0)) {
        qualifiedName = "xmlns:" + prefix;
      }

      String uri = "";
      String localName = prefix;
      String type = "CDATA";
      String value = namespace.getURI();

      attrs.addAttribute(uri, localName, qualifiedName, type, value);
    }

    return attrs;
  }

  /**
   * DOCUMENT ME!
   *
   * @param namespace DOCUMENT ME!
   * @param namespaceStack DOCUMENT ME!
   * @return true if the given namespace is an ignorable namespace (such as
   *         Namespace.NO_NAMESPACE or Namespace.XML_NAMESPACE) or if the
   *         namespace has already been declared in the current scope
   */
  protected boolean isIgnoreableNamespace(Namespace namespace,
      NamespaceStack namespaceStack) {
    if (namespace.equals(Namespace.NO_NAMESPACE)
        || namespace.equals(Namespace.XML_NAMESPACE)) {
      return true;
    }

    String uri = namespace.getURI();

    if ((uri == null) || (uri.length() <= 0)) {
      return true;
    }

    return namespaceStack.contains(namespace);
  }

  /**
   * Ensures non-null content handlers?
   */
  protected void checkForNullHandlers() {}
}

/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. The name "DOM4J" must not be used to endorse or promote products derived
 * from this Software without prior written permission of MetaStuff, Ltd. For
 * written permission, please contact dom4j-info@metastuff.com.
 * 4. Products derived from this Software may not be called "DOM4J" nor may
 * "DOM4J" appear in their names without prior written permission of MetaStuff,
 * Ltd. DOM4J is a registered trademark of MetaStuff, Ltd.
 * 5. Due credit should be given to the DOM4J Project - http://dom4j.sourceforge.net
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 */
