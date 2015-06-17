package org.noemus.neoxml.xpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;
import org.noemus.neoxml.Attribute;
import org.noemus.neoxml.Branch;
import org.noemus.neoxml.CDATA;
import org.noemus.neoxml.Comment;
import org.noemus.neoxml.Document;
import org.noemus.neoxml.DocumentException;
import org.noemus.neoxml.Element;
import org.noemus.neoxml.Namespace;
import org.noemus.neoxml.Node;
import org.noemus.neoxml.ProcessingInstruction;
import org.noemus.neoxml.QName;
import org.noemus.neoxml.Text;
import org.noemus.neoxml.io.SAXReader;

/**
 * Interface for navigating around the DOM4J object model.
 * <p>
 * This class is not intended for direct usage, but is used by the Jaxen engine during evaluation.
 * </p>
 *
 * @see XPath
 * @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 * @author Stephen Colebourne
 */
class DocumentNavigator extends DefaultNavigator implements NamedAccessNavigator
{
  private static final long serialVersionUID = 1;
  
  private transient SAXReader reader;
  
  /**
   * Retrieve the singleton instance of this <code>DocumentNavigator</code>.
   */
  public static Navigator getInstance() {
    return Singleton.instance;
  }
  
  @Override
  public boolean isElement(Object obj) {
    return obj instanceof Element;
  }
  
  @Override
  public boolean isComment(Object obj) {
    return obj instanceof Comment;
  }
  
  @Override
  public boolean isText(Object obj) {
    return (obj instanceof Text || obj instanceof CDATA);
  }
  
  @Override
  public boolean isAttribute(Object obj) {
    return obj instanceof Attribute;
  }
  
  @Override
  public boolean isProcessingInstruction(Object obj) {
    return obj instanceof ProcessingInstruction;
  }
  
  @Override
  public boolean isDocument(Object obj) {
    return obj instanceof Document;
  }
  
  @Override
  public boolean isNamespace(Object obj) {
    return obj instanceof Namespace;
  }
  
  @Override
  public String getElementName(Object obj) {
    Element elem = (Element)obj;
    
    return elem.getName();
  }
  
  @Override
  public String getElementNamespaceUri(Object obj) {
    Element elem = (Element)obj;
    
    String uri = elem.getNamespaceURI();
    if (uri == null) return "";
    
    else return uri;
  }
  
  @Override
  public String getElementQName(Object obj) {
    Element elem = (Element)obj;
    
    return elem.getQualifiedName();
  }
  
  @Override
  public String getAttributeName(Object obj) {
    Attribute attr = (Attribute)obj;
    
    return attr.getName();
  }
  
  @Override
  public String getAttributeNamespaceUri(Object obj) {
    Attribute attr = (Attribute)obj;
    
    String uri = attr.getNamespaceURI();
    if (uri == null) return "";
    else return uri;
  }
  
  @Override
  public String getAttributeQName(Object obj) {
    Attribute attr = (Attribute)obj;
    
    return attr.getQualifiedName();
  }
  
  @Override
  public Iterator getChildAxisIterator(Object contextNode) {
    final Iterator<Node> result;
    
    if (contextNode instanceof Branch) {
      Branch node = (Branch)contextNode;
      result = node.nodeIterator();
    }
    else {
      result = JaxenConstants.EMPTY_ITERATOR;
    }
    
    return result;
  }
  
  /**
   * Retrieves an <code>Iterator</code> over the child elements that
   * match the supplied name.
   *
   * @param contextNode the origin context node
   * @param localName the local name of the children to return, always present
   * @param namespacePrefix the prefix of the namespace of the children to return
   * @param namespaceURI the uri of the namespace of the children to return
   * @return an Iterator that traverses the named children, or null if none
   */
  @Override
  public Iterator getChildAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
    if (contextNode instanceof Element) {
      Element node = (Element)contextNode;
      return node.elementIterator(QName.get(localName, namespacePrefix, namespaceURI));
    }
    
    if (contextNode instanceof Document) {
      Document node = (Document)contextNode;
      Element el = node.getRootElement();
      
      if (el == null || el.getName().equals(localName) == false) {
        return JaxenConstants.EMPTY_ITERATOR;
      }
      
      if (namespaceURI != null) {
        if (namespaceURI.equals(el.getNamespaceURI()) == false) {
          return JaxenConstants.EMPTY_ITERATOR;
        }
      }
      
      return new SingleObjectIterator(el);
    }
    
    return JaxenConstants.EMPTY_ITERATOR;
  }
  
  @Override
  public Iterator getParentAxisIterator(Object contextNode) {
    if (contextNode instanceof Document) {
      return JaxenConstants.EMPTY_ITERATOR;
    }
    
    Node node = (Node)contextNode;
    
    Object parent = node.getParent();
    
    if (parent == null) {
      parent = node.getDocument();
    }
    
    return new SingleObjectIterator(parent);
  }
  
  @Override
  public Iterator getAttributeAxisIterator(Object contextNode) {
    if (!(contextNode instanceof Element)) {
      return JaxenConstants.EMPTY_ITERATOR;
    }
    
    Element elem = (Element)contextNode;
    
    return elem.attributeIterator();
  }
  
  /**
   * Retrieves an <code>Iterator</code> over the attribute elements that
   * match the supplied name.
   *
   * @param contextNode the origin context node
   * @param localName the local name of the attributes to return, always present
   * @param namespacePrefix the prefix of the namespace of the attributes to return
   * @param namespaceURI the URI of the namespace of the attributes to return
   * @return an Iterator that traverses the named attributes, not null
   */
  @Override
  public Iterator getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
    if (contextNode instanceof Element) {
      final Element node = (Element)contextNode;
      final Attribute attr = node.attribute(QName.get(localName, namespacePrefix, namespaceURI));
      
      if (attr == null) {
        return JaxenConstants.EMPTY_ITERATOR;
      }
      
      return new SingleObjectIterator(attr);
    }
    
    return JaxenConstants.EMPTY_ITERATOR;
  }
  
  @Override
  public Iterator getNamespaceAxisIterator(Object contextNode) {
    if (!(contextNode instanceof Element)) {
      return JaxenConstants.EMPTY_ITERATOR;
    }
    
    final List<Node> nsList = new ArrayList<>();
    final Set<String> prefixes = new HashSet<>();
    
    Element element = (Element)contextNode;
    
    for (Element context = element; context != null; context = context.getParent()) {
      final List<Namespace> declaredNS = new ArrayList<>(context.declaredNamespaces());
      
      declaredNS.add(context.getNamespace());
      
      for (Attribute attr : context.attributes()) {
        declaredNS.add(attr.getNamespace());
      }
      
      for (Namespace namespace : declaredNS) {
        if (namespace != Namespace.NO_NAMESPACE) {
          String prefix = namespace.getPrefix();
          
          if (!prefixes.contains(prefix)) {
            prefixes.add(prefix);
            nsList.add(namespace.asXPathResult(element));
          }
        }
      }
    }
    
    nsList.add(Namespace.XML_NAMESPACE.asXPathResult(element));
    
    return nsList.iterator();
  }
  
  @Override
  public Object getDocumentNode(Object contextNode) {
    if (contextNode instanceof Document) {
      return contextNode;
    }
    else if (contextNode instanceof Node) {
      Node node = (Node)contextNode;
      return node.getDocument();
    }
    
    return null;
  }
  
  /**
   * Returns a parsed form of the given XPath string, which will be suitable
   * for queries on DOM4J documents.
   */
  @Override
  public XPath parseXPath(String xpath) throws SAXPathException {
    return new Dom4jXPath(xpath);
  }
  
  @Override
  public Object getParentNode(Object contextNode) {
    if (contextNode instanceof Node) {
      Node node = (Node)contextNode;
      Object answer = node.getParent();
      
      if (answer == null) {
        answer = node.getDocument();
        
        if (answer == contextNode) {
          return null;
        }
      }
      
      return answer;
    }
    
    return null;
  }
  
  @Override
  public String getTextStringValue(Object obj) {
    return getNodeStringValue((Node)obj);
  }
  
  @Override
  public String getElementStringValue(Object obj) {
    return getNodeStringValue((Node)obj);
  }
  
  @Override
  public String getAttributeStringValue(Object obj) {
    return getNodeStringValue((Node)obj);
  }
  
  private String getNodeStringValue(Node node) {
    return node.getStringValue();
  }
  
  @Override
  public String getNamespaceStringValue(Object obj) {
    Namespace ns = (Namespace)obj;
    return ns.getURI();
  }
  
  @Override
  public String getNamespacePrefix(Object obj) {
    Namespace ns = (Namespace)obj;
    return ns.getPrefix();
  }
  
  @Override
  public String getCommentStringValue(Object obj) {
    Comment cmt = (Comment)obj;
    return cmt.getText();
  }
  
  @Override
  public String translateNamespacePrefixToUri(String prefix, Object context) {
    Element element = null;
    if (context instanceof Element) {
      element = (Element)context;
    }
    else if (context instanceof Node) {
      Node node = (Node)context;
      element = node.getParent();
    }
    
    if (element != null) {
      final Namespace namespace = element.getNamespaceForPrefix(prefix);
      
      if (namespace != null) {
        return namespace.getURI();
      }
    }
    
    return null;
  }
  
  @Override
  public short getNodeType(Object node) {
    if (node instanceof Node) {
      return ((Node)node).getNodeType();
    }
    
    return 0;
  }
  
  @Override
  public Object getDocument(String uri) throws FunctionCallException {
    try {
      return getSAXReader().read(uri);
    }
    catch (DocumentException e) {
      throw new FunctionCallException("Failed to parse document for URI: " + uri, e);
    }
  }
  
  @Override
  public String getProcessingInstructionTarget(Object obj) {
    ProcessingInstruction pi = (ProcessingInstruction)obj;
    return pi.getTarget();
  }
  
  @Override
  public String getProcessingInstructionData(Object obj) {
    ProcessingInstruction pi = (ProcessingInstruction)obj;
    return pi.getText();
  }
  
  // Properties
  //-------------------------------------------------------------------------
  public SAXReader getSAXReader() {
    if (reader == null) {
      reader = new SAXReader();
      reader.setMergeAdjacentText(true);
    }
    
    return reader;
  }
  
  public void setSAXReader(SAXReader reader) {
    this.reader = reader;
  }

  
  /**
   * Singleton implementation.
   */
  private static class Singleton
  {
    /**
     * Singleton instance.
     */
    static DocumentNavigator instance = new DocumentNavigator();
  }
}
