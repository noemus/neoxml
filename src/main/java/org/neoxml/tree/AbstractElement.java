/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.neoxml.tree;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neoxml.Attribute;
import org.neoxml.CDATA;
import org.neoxml.CharacterData;
import org.neoxml.Comment;
import org.neoxml.DefaultDocumentFactory;
import org.neoxml.Document;
import org.neoxml.DocumentFactory;
import org.neoxml.Element;
import org.neoxml.Entity;
import org.neoxml.IllegalAddException;
import org.neoxml.Namespace;
import org.neoxml.Node;
import org.neoxml.NodeList;
import org.neoxml.NodeType;
import org.neoxml.ProcessingInstruction;
import org.neoxml.QName;
import org.neoxml.Text;
import org.neoxml.Visitor;
import org.neoxml.io.XMLWriter;
import org.neoxml.util.AttributeHelper;
import org.xml.sax.Attributes;

/**
 * <p>
 * <code>AbstractElement</code> is an abstract base class for tree implementors to use for implementation inheritence.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.80 $
 */
public abstract class AbstractElement extends AbstractBranch implements Element
{
  /**
   * The <code>DefaultDocumentFactory</code> instance used by default
   */
  private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.getInstance();

  protected static final boolean VERBOSE_TOSTRING = false;
  protected static final boolean USE_STRINGVALUE_SEPARATOR = false;

  public AbstractElement() {}

  @Override
  public NodeType getNodeTypeEnum() {
    return NodeType.ELEMENT_NODE;
  }

  @Override
  public boolean isRootElement() {
    Document document = getDocument();

    if (document != null) {
      Element root = document.getRootElement();

      if (root == this) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void setName(String name) {
    setQName(getDocumentFactory().createQName(name));
  }

  public void setNamespace(Namespace namespace) {
    setQName(getDocumentFactory().createQName(getName(), namespace));
  }

  /**
   * Returns the XPath expression to match this Elements name which is
   * getQualifiedName() if there is a namespace prefix defined or if no
   * namespace is present then it is getName() or if a namespace is defined
   * with no prefix then the expression is [name()='X'] where X = getName().
   *
   * @return DOCUMENT ME!
   */
  public String getXPathNameStep() {
    String uri = getNamespaceURI();

    if ((uri == null) || (uri.length() == 0)) {
      return getName();
    }

    String prefix = getNamespacePrefix();

    if ((prefix == null) || (prefix.length() == 0)) {
      return "*[name()='" + getName() + "']";
    }

    return getQualifiedName();
  }

  @Override
  public String getPath(Element context) {
    if (this == context) {
      return ".";
    }

    Element parent = getParent();

    if (parent == null) {
      return "/" + getXPathNameStep();
    }
    else if (parent == context) {
      return getXPathNameStep();
    }

    return parent.getPath(context) + "/" + getXPathNameStep();
  }

  @Override
  public String getUniquePath(Element context) {
    Element parent = getParent();

    if (parent == null) {
      return "/" + getXPathNameStep();
    }

    StringBuilder buffer = new StringBuilder();

    if (parent != context) {
      buffer.append(parent.getUniquePath(context));

      buffer.append("/");
    }

    buffer.append(getXPathNameStep());

    List<Element> mySiblings = parent.elements(getQName());

    if (mySiblings.size() > 1) {
      int idx = mySiblings.indexOf(this);

      if (idx >= 0) {
        buffer.append("[");

        buffer.append(Integer.toString(++idx));

        buffer.append("]");
      }
    }

    return buffer.toString();
  }

  @Override
  public String asXML() {
    try (StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out)) {
      
      writer.write(this);
      writer.flush();

      return out.toString();
    }
    catch (IOException e) {
      throw new RuntimeException("IOException while generating " + "textual representation: " + e.getMessage());
    }
  }

  @Override
  public void write(Writer out) throws IOException {
    try (XMLWriter writer = new XMLWriter(out)) {
      writer.write(this);
    }
  }

  /**
   * <p>
   * <code>accept</code> method is the <code>Visitor Pattern</code> method.
   * </p>
   *
   * @param visitor <code>Visitor</code> is the visitor.
   */
  @Override
  public boolean accept(Visitor visitor) {
    if (visitor.visitEnter(this)) {// enter this node?
      if (visitor.visit(this) && visitAttributes(visitor)) {
        visitNodes(visitor);
      }
    }

    // leave this node and indicate whether to stop processing after this element
    return visitor.visitLeave(this);
  }

  private boolean visitAttributes(Visitor visitor) {
    for (int i = 0, size = attributeCount(); i < size; i++) {
      Attribute attribute = attribute(i);

      if (!visitor.visit(attribute)) {
        return false;
      }
    }

    return true;
  }

  private void visitNodes(Visitor visitor) {
    for (int i = 0, size = nodeCount(); i < size; i++) {
      final Node node = node(i);

      if (node != null) {
        if (!node.accept(visitor)) {
          break;
        }
      }
    }
  }

  @Override
  protected void toString(StringBuilder builder) {
    String uri = getNamespaceURI();

    super.toString(builder);
    builder.append(" [Element: <").append(getQualifiedName());
    if ((uri != null) && (uri.length() > 0)) {
      builder.append(" uri: ").append(uri);
    }
    builder.append(" attributes: ").append(attributeList());
    if (VERBOSE_TOSTRING) {
      builder.append(" content: ").append(contentList());
      builder.append(' ');
    }
    builder.append("/>]");
  }

  // QName methods
  // -------------------------------------------------------------------------

  @Override
  public Namespace getNamespace() {
    return getQName().getNamespace();
  }

  @Override
  public String getName() {
    return getQName().getName();
  }

  @Override
  public String getNamespacePrefix() {
    return getQName().getNamespacePrefix();
  }

  @Override
  public String getNamespaceURI() {
    return getQName().getNamespaceURI();
  }

  @Override
  public String getQualifiedName() {
    return getQName().getQualifiedName();
  }

  @Override
  public Object getData() {
    return getText();
  }

  @Override
  public void setData(Object data) {
    // ignore this method
  }

  // Node methods
  // -------------------------------------------------------------------------

  @Override
  public Node node(int index) {
    if (index >= 0) {
      List<Node> list = safeContentList();

      if (index >= list.size()) {
        return null;
      }
      return list.get(index);
    }

    return null;
  }

  @Override
  public int indexOf(Node node) {
    return safeContentList().indexOf(node);
  }

  @Override
  public int nodeCount() {
    return safeContentList().size();
  }

  @Override
  public Iterator<Node> nodeIterator() {
    return safeContentList().iterator();
  }

  // Element methods
  // -------------------------------------------------------------------------

  @Override
  public Element element(String name) {
    return safeContentList().find(new ElementCondition(name), Element.class);
  }

  @Override
  public Element element(QName qName) {
    return safeContentList().find(new ElementQNameCondition(qName), Element.class);
  }

  public Element element(String name, Namespace namespace) {
    return element(getDocumentFactory().createQName(name, namespace));
  }

  @Override
  public NodeList<Element> elements() {
    return contentList().filter(ELEMENT_CONDITION, Element.class);
  }

  @Override
  public NodeList<Element> elements(String name) {
    return contentList().filter(new ElementCondition(name), Element.class);
  }

  @Override
  public NodeList<Element> elements(QName qName) {
    return contentList().filter(new ElementQNameCondition(qName), Element.class);
  }

  public NodeList<Element> elements(String name, Namespace namespace) {
    return elements(getDocumentFactory().createQName(name, namespace));
  }

  @Override
  public Iterator<Element> elementIterator() {
    return elements().iterator();
  }

  @Override
  public Iterator<Element> elementIterator(String name) {
    return elements(name).iterator();
  }

  @Override
  public Iterator<Element> elementIterator(QName qName) {
    return elements(qName).iterator();
  }

  public Iterator<Element> elementIterator(String name, Namespace ns) {
    return elementIterator(getDocumentFactory().createQName(name, ns));
  }

  // Attribute methods
  // -------------------------------------------------------------------------

  @Override
  public NodeList<Attribute> attributes() {
    return attributeList();
  }

  @Override
  public Iterator<Attribute> attributeIterator() {
    return safeAttributeList().iterator();
  }

  @Override
  public Attribute attribute(int index) {
    return safeAttributeList().get(index);
  }

  @Override
  public int attributeCount() {
    return safeAttributeList().size();
  }

  @Override
  public Attribute attribute(String name) {
    for (Attribute attribute : safeAttributeList()) {
      if (name.equals(attribute.getName())) {
        return attribute;
      }
    }

    return null;
  }

  @Override
  public Attribute attribute(QName qName) {
    for (Attribute attribute : safeAttributeList()) {
      if (qName.equals(attribute.getQName())) {
        return attribute;
      }
    }

    return null;
  }

  public Attribute attribute(String name, Namespace namespace) {
    return attribute(getDocumentFactory().createQName(name, namespace));
  }

  /**
   * This method provides a more optimal way of setting all the attributes on
   * an Element particularly for use in {@link org.neoxml.io.SAXReader}.
   *
   * @param attributes DOCUMENT ME!
   * @param namespaceStack DOCUMENT ME!
   * @param noNamespaceAttributes DOCUMENT ME!
   */
  public void setAttributes(Attributes attributes, NamespaceStack namespaceStack, boolean noNamespaceAttributes) {
    // clear attributes even if provided attributes are empty
    clearAttributeList();

    // now lets add all attribute values
    final int size = attributes.getLength();

    if (size > 0) {
      final DocumentFactory factory = getDocumentFactory();

      final NodeList<Attribute> list;

      if (size == 1) {
        list = attributeList();
      }
      else {
        list = attributeList(size);
      }

      for (int i = 0; i < size; i++) {
        // optimised to avoid the call to attribute(QName) to
        // lookup an attribute for a given QName
        String name = attributes.getQName(i);

        if (noNamespaceAttributes || !name.startsWith("xmlns")) {
          String attributeURI = attributes.getURI(i);

          String attributeLocalName = attributes.getLocalName(i);

          String attributeValue = attributes.getValue(i);

          QName attributeQName = namespaceStack.getAttributeQName(attributeURI, attributeLocalName, name);

          Attribute attribute = factory.createAttribute(this, attributeQName, attributeValue);

          list.add(attribute);
        }
      }
    }
  }

  @Override
  public String attributeValue(String name) {
    return AttributeHelper.getAttributeValue(attribute(name), null);
  }

  @Override
  public String attributeValue(QName qName) {
    return AttributeHelper.getAttributeValue(attribute(qName), null);
  }

  @Override
  public String attributeValue(String name, String defaultValue) {
    return AttributeHelper.getAttributeValue(attribute(name), defaultValue);
  }

  @Override
  public String attributeValue(QName qName, String defaultValue) {
    return AttributeHelper.getAttributeValue(attribute(qName), defaultValue);
  }

  @Override
  public void add(Attribute attribute) {
    if (attribute.getParent() != null) {
      String message = "The Attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"";

      throw new IllegalAddException(this, attribute, message);
    }

    if (attribute.getValue() == null) {
      // try remove a previous attribute with the same
      // name since adding an attribute with a null value
      // is equivalent to removing it.
      Attribute oldAttribute = attribute(attribute.getQName());

      if (oldAttribute != null) {
        remove(oldAttribute);
      }
    }
    else {
      attributeList().add(attribute);
    }
  }

  @Override
  public boolean remove(Attribute attribute) {
    List<Attribute> list = safeAttributeList();

    if (!list.remove(attribute)) {
      // we may have a copy of the attribute
      Attribute copy = attribute(attribute.getQName());

      if (copy != null) {
        return list.remove(copy);
      }

      return false;
    }

    return true;
  }

  // Content Model methods
  // -------------------------------------------------------------------------

  @Override
  public Node getXPathResult(int index) {
    Node answer = node(index);

    if ((answer != null) && !answer.supportsParent()) {
      return answer.asXPathResult(this);
    }

    return answer;
  }

  @Override
  public Element addAttribute(String name, String value) {
    // adding a null value is equivalent to removing the attribute
    Attribute attribute = attribute(name);

    if (value != null) {
      if (attribute == null) {
        add(getDocumentFactory().createAttribute(this, name, value));
      }
      else if (attribute.isReadOnly()) {
        remove(attribute);

        add(getDocumentFactory().createAttribute(this, name, value));
      }
      else {
        attribute.setValue(value);
      }
    }
    else if (attribute != null) {
      remove(attribute);
    }

    return this;
  }

  @Override
  public Element addAttribute(QName qName, String value) {
    // adding a null value is equivalent to removing the attribute
    Attribute attribute = attribute(qName);

    if (value != null) {
      if (attribute == null) {
        add(getDocumentFactory().createAttribute(this, qName, value));
      }
      else if (attribute.isReadOnly()) {
        remove(attribute);

        add(getDocumentFactory().createAttribute(this, qName, value));
      }
      else {
        attribute.setValue(value);
      }
    }
    else if (attribute != null) {
      remove(attribute);
    }

    return this;
  }

  @Override
  public Element addCDATA(String cdata) {
    CDATA node = getDocumentFactory().createCDATA(cdata);

    addNewNode(node);

    return this;
  }

  @Override
  public Element addComment(String comment) {
    Comment node = getDocumentFactory().createComment(comment);

    addNewNode(node);

    return this;
  }

  @Override
  public Element addElement(String name) {
    DocumentFactory factory = getDocumentFactory();

    int index = name.indexOf(":");

    String prefix = "";

    String localName = name;

    Namespace namespace = null;

    if (index > 0) {
      prefix = name.substring(0, index);

      localName = name.substring(index + 1);

      namespace = getNamespaceForPrefix(prefix);

      if (namespace == null) {
        throw new IllegalAddException("No such namespace prefix: " + prefix + " is in scope on: " + this + " so cannot add element: " + name);
      }
    }
    else {
      namespace = getNamespaceForPrefix("");
    }

    Element node;

    if (namespace != null) {
      QName qname = factory.createQName(localName, namespace);

      node = factory.createElement(qname);
    }
    else {
      node = factory.createElement(name);
    }

    addNewNode(node);

    return node;
  }

  @Override
  public Element addEntity(String name, String text) {
    Entity node = getDocumentFactory().createEntity(name, text);

    addNewNode(node);

    return this;
  }

  @Override
  public Element addNamespace(String prefix, String uri) {
    Namespace node = getDocumentFactory().createNamespace(prefix, uri);

    addNewNode(node);

    return this;
  }

  @Override
  public Element addProcessingInstruction(String target, String data) {
    ProcessingInstruction node = getDocumentFactory().createProcessingInstruction(target, data);

    addNewNode(node);

    return this;
  }

  @Override
  public Element addProcessingInstruction(String target, Map<String,String> data) {
    ProcessingInstruction node = getDocumentFactory().createProcessingInstruction(target, data);

    addNewNode(node);

    return this;
  }

  @Override
  public Element addText(String text) {
    // it doesn't make sense to add empty text nodes => ignore them
    if (text != null && !text.isEmpty()) {
      Text node = getDocumentFactory().createText(text);

      addNewNode(node);
    }

    return this;
  }

  // polymorphic node methods

  @Override
  public void add(Node node) {
    switch (node.getNodeTypeEnum()) {
      case ELEMENT_NODE:
        add((Element)node);

        break;

      case ATTRIBUTE_NODE:
        add((Attribute)node);

        break;

      case TEXT_NODE:
        add((Text)node);

        break;

      case CDATA_SECTION_NODE:
        add((CDATA)node);

        break;

      case ENTITY_REFERENCE_NODE:
        add((Entity)node);

        break;

      case PROCESSING_INSTRUCTION_NODE:
        add((ProcessingInstruction)node);

        break;

      case COMMENT_NODE:
        add((Comment)node);

        break;

        /*
         * XXXX: to do! case DOCUMENT_TYPE_NODE: add((DocumentType) node);
         * break;
         */
      case NAMESPACE_NODE:
        add((Namespace)node);

        break;

      default:
        invalidNodeTypeAddException(node);
    }
  }

  @Override
  public boolean remove(Node node) {
    switch (node.getNodeTypeEnum()) {
      case ELEMENT_NODE:
        return remove((Element)node);

      case ATTRIBUTE_NODE:
        return remove((Attribute)node);

      case TEXT_NODE:
        return remove((Text)node);

      case CDATA_SECTION_NODE:
        return remove((CDATA)node);

      case ENTITY_REFERENCE_NODE:
        return remove((Entity)node);

      case PROCESSING_INSTRUCTION_NODE:
        return remove((ProcessingInstruction)node);

      case COMMENT_NODE:
        return remove((Comment)node);

        /*
         * case DOCUMENT_TYPE_NODE: return remove((DocumentType) node);
         */
      case NAMESPACE_NODE:
        return remove((Namespace)node);

      default:
        return false;
    }
  }

  // typesafe versions using node classes

  @Override
  public void add(CDATA cdata) {
    addNode(cdata);
  }

  @Override
  public void add(Comment comment) {
    addNode(comment);
  }

  @Override
  public void add(Element element) {
    addNode(element);
  }

  @Override
  public void add(Entity entity) {
    addNode(entity);
  }

  @Override
  public void add(Namespace namespace) {
    addNode(namespace);
  }

  @Override
  public void add(ProcessingInstruction pi) {
    addNode(pi);
  }

  @Override
  public void add(Text text) {
    addNode(text);
  }

  @Override
  public boolean remove(CDATA cdata) {
    return removeNode(cdata);
  }

  @Override
  public boolean remove(Comment comment) {
    return removeNode(comment);
  }

  @Override
  public boolean remove(Element element) {
    return removeNode(element);
  }

  @Override
  public boolean remove(Entity entity) {
    return removeNode(entity);
  }

  @Override
  public boolean remove(Namespace namespace) {
    return removeNode(namespace);
  }

  @Override
  public boolean remove(ProcessingInstruction pi) {
    return removeNode(pi);
  }

  @Override
  public boolean remove(Text text) {
    return removeNode(text);
  }

  // Helper methods
  // -------------------------------------------------------------------------

  @Override
  public boolean hasMixedContent() {
    List<Node> contentList = safeContentList();

    if (contentList.isEmpty() || contentList.size() < 2) {
      return false;
    }

    Class<?> prevClass = null;

    for (Node node : contentList) {
      Class<?> newClass = node.getClass();

      if (newClass != prevClass) {
        if (prevClass != null) {
          return true;
        }

        prevClass = newClass;
      }
    }

    return false;
  }

  @Override
  public boolean isTextOnly() {
    List<Node> contentList = safeContentList();

    if (contentList.isEmpty()) {
      return true;
    }

    for (Node node : contentList) {
      if (!(node instanceof CharacterData)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void setText(String text) {
    /* remove all text nodes */

    for (Iterator<Node> it = safeContentList().iterator(); it.hasNext();) {
      Node node = it.next();

      switch (node.getNodeTypeEnum()) {
        case CDATA_SECTION_NODE:

          // case ENTITY_NODE:
        case ENTITY_REFERENCE_NODE:
        case TEXT_NODE:
          it.remove();

        default:
          break;
      }
    }

    addText(text);
  }

  @Override
  public String getStringValue() {
    List<Node> list = safeContentList();

    int size = list.size();

    if (size > 0) {
      if (size == 1) {
        // optimised to avoid StringBuilder creation
        return getContentAsStringValue(list.get(0));
      }
      else {
        StringBuilder buffer = new StringBuilder();

        for (Node node : list) {
          String string = getContentAsStringValue(node);

          if (string.length() > 0) {
            if (USE_STRINGVALUE_SEPARATOR) {
              if (buffer.length() > 0) {
                buffer.append(' ');
              }
            }

            buffer.append(string);
          }
        }

        return buffer.toString();
      }
    }

    return "";
  }

  /**
   * Puts all <code>Text</code> nodes in the full depth of the sub-tree
   * underneath this <code>Node</code>, including attribute nodes, into a
   * "normal" form where only structure (e.g., elements, comments, processing
   * instructions, CDATA sections, and entity references) separates <code>Text</code> nodes, i.e., there are neither
   * adjacent <code>Text</code> nodes nor empty <code>Text</code> nodes. This can
   * be used to ensure that the DOM view of a document is the same as if it
   * were saved and re-loaded, and is useful when operations (such as XPointer
   * lookups) that depend on a particular document tree structure are to be
   * used.In cases where the document contains <code>CDATASections</code>,
   * the normalize operation alone may not be sufficient, since XPointers do
   * not differentiate between <code>Text</code> nodes and <code>CDATASection</code> nodes.
   *
   * @since DOM Level 2
   */
  @Override
  public void normalize() {
    List<Node> list = safeContentList();

    Text previousText = null;

    int i = 0;

    while (i < list.size()) {
      Node node = list.get(i);

      if (node instanceof Text) {
        Text text = (Text)node;

        if (previousText != null) {
          previousText.appendText(text.getText());

          remove(text);
        }
        else {
          String value = text.getText();

          // only remove empty Text nodes, not whitespace nodes
          // if ( value == null || value.trim().length() <= 0 ) {
          if ((value == null) || (value.length() <= 0)) {
            remove(text);
          }
          else {
            previousText = text;

            i++;
          }
        }
      }
      else {
        if (node instanceof Element) {
          Element element = (Element)node;

          element.normalize();
        }

        previousText = null;

        i++;
      }
    }
  }

  @Override
  public String elementText(String name) {
    Element element = element(name);

    return (element != null) ? element.getText() : null;
  }

  @Override
  public String elementText(QName qName) {
    Element element = element(qName);

    return (element != null) ? element.getText() : null;
  }

  @Override
  public String elementTextTrim(String name) {
    Element element = element(name);

    return (element != null) ? element.getTextTrim() : null;
  }

  @Override
  public String elementTextTrim(QName qName) {
    Element element = element(qName);

    return (element != null) ? element.getTextTrim() : null;
  }

  // add to me content from another element
  // analagous to the addAll(collection) methods in Java 2 collections

  @Override
  public void appendAttributes(Element element) {
    final List<Attribute> elementAttrs = element.attributes();

    // ensure attribute list size
    attributeList(attributeCount() + elementAttrs.size());

    for (Attribute attribute : elementAttrs) {
      if (attribute.supportsParent()) {
        addAttribute(attribute.getQName(), attribute.getValue());
      }
      else {
        add(attribute);
      }
    }
  }

  @Override
  public Element createCopy() {
    Element clone = createElement(getQName());

    clone.setAttributes(safeAttributeList());
    clone.setContent(safeContentList());

    return clone;
  }

  @Override
  public Element createCopy(String name) {
    Element clone = createElement(name);

    clone.setAttributes(safeAttributeList());
    clone.setContent(safeContentList());

    return clone;
  }

  @Override
  public Element createCopy(QName qName) {
    Element clone = createElement(qName);

    clone.setAttributes(safeAttributeList());
    clone.setContent(safeContentList());

    return clone;
  }

  @Override
  public QName getQName(String qualifiedName) {
    String prefix = "";

    String localName = qualifiedName;

    int index = qualifiedName.indexOf(":");

    if (index > 0) {
      prefix = qualifiedName.substring(0, index);

      localName = qualifiedName.substring(index + 1);
    }

    Namespace namespace = getNamespaceForPrefix(prefix);

    if (namespace != null) {
      return getDocumentFactory().createQName(localName, namespace);
    }
    else {
      return getDocumentFactory().createQName(localName);
    }
  }

  @Override
  public Namespace getNamespaceForPrefix(String prefix) {
    if (prefix == null) {
      prefix = "";
    }

    if (prefix.equals(getNamespacePrefix())) {
      return getNamespace();
    }
    else if (prefix.equals("xml")) {
      return Namespace.XML_NAMESPACE;
    }
    else {
      Namespace answer = safeContentList().find(new NamespacePrefixCondition(prefix), Namespace.class);

      if (answer != null) {
        return answer;
      }
    }

    Element parent = getParent();

    if (parent != null) {
      Namespace answer = parent.getNamespaceForPrefix(prefix);

      if (answer != null) {
        return answer;
      }
    }

    if (prefix.length() <= 0) {
      return Namespace.NO_NAMESPACE;
    }

    return null;
  }

  @Override
  public Namespace getNamespaceForURI(String uri) {
    if ((uri == null) || (uri.length() <= 0)) {
      return Namespace.NO_NAMESPACE;
    }
    else if (uri.equals(getNamespaceURI())) {
      return getNamespace();
    }

    return safeContentList().find(new NamespaceCondition(uri), Namespace.class);
  }

  @Override
  public NodeList<Namespace> getNamespacesForURI(String uri) {
    return contentList().filter(new NamespaceCondition(uri), Namespace.class);
  }

  @Override
  public NodeList<Namespace> declaredNamespaces() {
    return contentList().filter(NAMESPACE_CONDITION, Namespace.class);
  }

  @Override
  public NodeList<Namespace> additionalNamespaces() {
    return contentList().filter(new AdditionalNamespaceCondition(getNamespace()), Namespace.class);
  }

  public NodeList<Namespace> additionalNamespaces(String defaultNamespaceURI) {
    return contentList().filter(new AdditionalNamespaceCondition(defaultNamespaceURI), Namespace.class);
  }

  // Implementation helper methods
  // -------------------------------------------------------------------------

  /**
   * Ensures that the list of attributes has the given size
   *
   * @param minCapacity DOCUMENT ME!
   */
  public void ensureAttributesCapacity(int minCapacity) {
    if (minCapacity > 1) {
      List<Attribute> list = attributeList();

      if (list instanceof ArrayList) {
        ((ArrayList<Attribute>)list).ensureCapacity(minCapacity);
      }
    }
  }

  // Implementation methods
  // -------------------------------------------------------------------------

  protected Element createElement(String name) {
    return getDocumentFactory().createElement(name);
  }

  protected Element createElement(QName qName) {
    return getDocumentFactory().createElement(qName);
  }

  @Override
  protected void beforeChildAdd(Node node) {
    if (node.getParent() != null) {
      // XXX: could clone here
      String message = "The Node already has an existing parent of \"" + node.getParent().getQualifiedName() + "\"";

      throw new IllegalAddException(this, node, message);
    }
  }

  /**
   * Called when a new child node is added to create any parent relationships
   *
   * @param node DOCUMENT ME!
   */
  @Override
  protected void childAdded(Node node) {
    if (node != null) {
      node.setParent(this);
    }
  }

  @Override
  protected void childRemoved(Node node) {
    if (node != null) {
      node.setParent(null);
      node.setDocument(null);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @return the internal List used to store attributes or creates one if one is not available
   */
  protected abstract NodeList<Attribute> attributeList();

  /**
   * DOCUMENT ME!
   *
   * @param attributeCount DOCUMENT ME!
   * @return the internal List used to store attributes or creates one with
   *         the specified size if one is not available
   */
  protected abstract NodeList<Attribute> attributeList(int attributeCount);

  /**
   * DOCUMENT ME!
   *
   * @return the internal List used to store attributes or emptyList if not available
   */
  protected abstract NodeList<Attribute> safeAttributeList();

  /**
   * DOCUMENT ME!
   */
  protected abstract void clearAttributeList();
  
  /**
   * This method is for internal use only
   * Replaces nodes in node list with suplied nodes (nodes are cloned if necessary). Used in setAttributes
   * 
   * @param newContent DOCUMENT ME!
   * @param node DOCUMENT ME!
   */
  protected abstract void setAttributeList(List<Attribute> nodes);

  @SuppressWarnings("unchecked")
  protected NodeList<Attribute> emptyAttributeList() {
    return (NodeList<Attribute>)EMPTY_NODE_LIST;
  }

  @Override
  protected DocumentFactory getDocumentFactory() {
    QName qName = getQName();

    // QName might be null as we might not have been constructed yet
    if (qName != null) {
      DocumentFactory factory = qName.getDocumentFactory();

      if (factory != null) {
        return factory;
      }
    }

    return DOCUMENT_FACTORY;
  }

  /**
   * A Factory Method pattern which creates a List implementation used to store attributes
   *
   * @return DOCUMENT ME!
   */
  protected final NodeList<Attribute> createAttributeList() {
    return createAttributeList(DEFAULT_CONTENT_LIST_SIZE);
  }

  /**
   * A Factory Method pattern which creates a List implementation used to store attributes
   *
   * @param size DOCUMENT ME!
   * @return DOCUMENT ME!
   */
  protected final NodeList<Attribute> createAttributeList(int size) {
    return new DefaultNodeList<>(this, size);
  }
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
