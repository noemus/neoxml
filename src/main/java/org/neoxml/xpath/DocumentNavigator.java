package org.neoxml.xpath;

import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;
import org.neoxml.Attribute;
import org.neoxml.Branch;
import org.neoxml.CDATA;
import org.neoxml.Comment;
import org.neoxml.Document;
import org.neoxml.DocumentException;
import org.neoxml.Element;
import org.neoxml.Namespace;
import org.neoxml.Node;
import org.neoxml.ProcessingInstruction;
import org.neoxml.QName;
import org.neoxml.Text;
import org.neoxml.io.SAXReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyIterator;

/**
 * Interface for navigating around the neoxml object model.
 * <p>
 * This class is not intended for direct usage, but is used by the Jaxen engine during evaluation.
 * </p>
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 * @author Stephen Colebourne
 * @see XPath
 */
class DocumentNavigator extends DefaultNavigator implements NamedAccessNavigator {
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
        Element elem = (Element) obj;

        return elem.getName();
    }

    @Override
    public String getElementNamespaceUri(Object obj) {
        Element elem = (Element) obj;

        String uri = elem.getNamespaceURI();
        if (uri == null) { return ""; } else return uri;
    }

    @Override
    public String getElementQName(Object obj) {
        Element elem = (Element) obj;

        return elem.getQualifiedName();
    }

    @Override
    public String getAttributeName(Object obj) {
        Attribute attr = (Attribute) obj;

        return attr.getName();
    }

    @Override
    public String getAttributeNamespaceUri(Object obj) {
        Attribute attr = (Attribute) obj;

        String uri = attr.getNamespaceURI();
        if (uri == null) { return ""; } else return uri;
    }

    @Override
    public String getAttributeQName(Object obj) {
        Attribute attr = (Attribute) obj;

        return attr.getQualifiedName();
    }

    @Override
    public Iterator<Node> getChildAxisIterator(Object contextNode) {
        if (contextNode instanceof Branch) {
            Branch node = (Branch) contextNode;
            return node.nodeIterator();
        }

        return emptyIterator();
    }

    /**
     * Retrieves an <code>Iterator</code> over the child elements that
     * match the supplied name.
     *
     * @param contextNode     the origin context node
     * @param localName       the local name of the children to return, always present
     * @param namespacePrefix the prefix of the namespace of the children to return
     * @param namespaceURI    the uri of the namespace of the children to return
     * @return an Iterator that traverses the named children, or null if none
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Element> getChildAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        if (contextNode instanceof Element) {
            Element node = (Element) contextNode;
            return node.elementIterator(QName.get(localName, namespacePrefix, namespaceURI));
        }

        if (contextNode instanceof Document) {
            Document node = (Document) contextNode;
            Element el = node.getRootElement();

            if (el == null || !el.getName().equals(localName)) {
                return emptyIterator();
            }

            if (namespaceURI != null && !namespaceURI.equals(el.getNamespaceURI())) {
                return emptyIterator();
            }

            return new SingleObjectIterator(el);
        }

        return emptyIterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Node> getParentAxisIterator(Object contextNode) {
        if (contextNode instanceof Document) {
            return emptyIterator();
        }

        Node node = (Node) contextNode;

        Node parent = node.getParent();

        if (parent == null) {
            parent = node.getDocument();
        }

        return new SingleObjectIterator(parent);
    }

    @Override
    public Iterator<Attribute> getAttributeAxisIterator(Object contextNode) {
        if (!(contextNode instanceof Element)) {
            return emptyIterator();
        }

        Element elem = (Element) contextNode;

        return elem.attributeIterator();
    }

    /**
     * Retrieves an <code>Iterator</code> over the attribute elements that
     * match the supplied name.
     *
     * @param contextNode     the origin context node
     * @param localName       the local name of the attributes to return, always present
     * @param namespacePrefix the prefix of the namespace of the attributes to return
     * @param namespaceURI    the URI of the namespace of the attributes to return
     * @return an Iterator that traverses the named attributes, not null
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Attribute> getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        if (contextNode instanceof Element) {
            final Element node = (Element) contextNode;
            final Attribute attr = node.attribute(QName.get(localName, namespacePrefix, namespaceURI));

            if (attr == null) {
                return emptyIterator();
            }

            return new SingleObjectIterator(attr);
        }

        return emptyIterator();
    }

    @Override
    public Iterator<Node> getNamespaceAxisIterator(Object contextNode) {
        if (!(contextNode instanceof Element)) {
            return emptyIterator();
        }

        final List<Node> nsList = new ArrayList<>();
        final Set<String> prefixes = new HashSet<>();

        Element element = (Element) contextNode;

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
    public Document getDocumentNode(Object contextNode) {
        if (contextNode instanceof Document) {
            return (Document) contextNode;
        } else if (contextNode instanceof Node) {
            Node node = (Node) contextNode;
            return node.getDocument();
        }

        return null;
    }

    /**
     * Returns a parsed form of the given XPath string, which will be suitable
     * for queries on neoxml documents.
     */
    @Override
    public XPath parseXPath(String xpath) throws SAXPathException {
        return new Dom4jXPath(xpath);
    }

    @Override
    public Node getParentNode(Object contextNode) {
        if (contextNode instanceof Node) {
            Node node = (Node) contextNode;
            Node answer = node.getParent();

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
        return getNodeStringValue((Node) obj);
    }

    @Override
    public String getElementStringValue(Object obj) {
        return getNodeStringValue((Node) obj);
    }

    @Override
    public String getAttributeStringValue(Object obj) {
        return getNodeStringValue((Node) obj);
    }

    private String getNodeStringValue(Node node) {
        return node.getStringValue();
    }

    @Override
    public String getNamespaceStringValue(Object obj) {
        Namespace ns = (Namespace) obj;
        return ns.getURI();
    }

    @Override
    public String getNamespacePrefix(Object obj) {
        Namespace ns = (Namespace) obj;
        return ns.getPrefix();
    }

    @Override
    public String getCommentStringValue(Object obj) {
        Comment cmt = (Comment) obj;
        return cmt.getText();
    }

    @Override
    public String translateNamespacePrefixToUri(String prefix, Object context) {
        Element element = null;
        if (context instanceof Element) {
            element = (Element) context;
        } else if (context instanceof Node) {
            Node node = (Node) context;
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
            return ((Node) node).getNodeType();
        }

        return 0;
    }

    @Override
    public Object getDocument(String uri) throws FunctionCallException {
        try {
            return getSAXReader().read(uri);
        } catch (DocumentException e) {
            throw new FunctionCallException("Failed to parse document for URI: " + uri, e);
        }
    }

    @Override
    public String getProcessingInstructionTarget(Object obj) {
        ProcessingInstruction pi = (ProcessingInstruction) obj;
        return pi.getTarget();
    }

    @Override
    public String getProcessingInstructionData(Object obj) {
        ProcessingInstruction pi = (ProcessingInstruction) obj;
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
    private static class Singleton {
        /**
         * Singleton instance.
         */
        static DocumentNavigator instance = new DocumentNavigator();
    }
}
