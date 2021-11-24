/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neoxml.Branch;
import org.neoxml.CharacterData;
import org.neoxml.Document;
import org.neoxml.DocumentType;
import org.neoxml.Element;
import org.neoxml.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * <code>DOMNodeHelper</code> contains a collection of utility methods for use across Node implementations.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.20 $
 */
public class DOMNodeHelper {
    private static final Log log = LogFactory.getLog(DOMNodeHelper.class);

    public static final NodeList EMPTY_NODE_LIST = new EmptyNodeList();

    protected DOMNodeHelper() {}

    public static boolean supports(Node node, String feature, String version) {
        return false;
    }

    public static String getNamespaceURI(Node node) {
        return null;
    }

    public static String getPrefix(Node node) {
        return null;
    }

    public static String getLocalName(Node node) {
        return null;
    }

    public static void setPrefix(Node node, String prefix) {
        notSupported();
    }

    public static String getNodeValue(Node node) {
        return node.getText();
    }

    public static void setNodeValue(Node node, String nodeValue) {
        node.setText(nodeValue);
    }

    public static org.w3c.dom.Node getParentNode(Node node) {
        return asDOMNode(node.getParent());
    }

    public static NodeList getChildNodes(Node node) {
        return EMPTY_NODE_LIST;
    }

    public static org.w3c.dom.Node getFirstChild(Node node) {
        return null;
    }

    public static org.w3c.dom.Node getLastChild(Node node) {
        return null;
    }

    public static org.w3c.dom.Node getPreviousSibling(Node node) {
        Element parent = node.getParent();

        if (parent != null) {
            int index = parent.indexOf(node);

            if (index > 0) {
                Node previous = parent.node(index - 1);

                return asDOMNode(previous);
            }
        }

        return null;
    }

    public static org.w3c.dom.Node getNextSibling(Node node) {
        Element parent = node.getParent();

        if (parent != null) {
            int index = parent.indexOf(node);

            if (index >= 0) {
                if (++index < parent.nodeCount()) {
                    Node next = parent.node(index);

                    return asDOMNode(next);
                }
            }
        }

        return null;
    }

    public static NamedNodeMap getAttributes(Node node) {
        return null;
    }

    public static org.w3c.dom.Document getOwnerDocument(Node node) {
        return asDOMDocument(node.getDocument());
    }

    public static org.w3c.dom.Node insertBefore(Node node, org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) {
        if (node instanceof Branch) {
            Branch branch = (Branch) node;
            List<Node> list = branch.content();
            int index = refChild instanceof Node ? list.indexOf((Node)refChild) : -1;

            if (index < 0) {
                branch.add(asNode(newChild));
            } else {
                list.add(index, asNode(newChild));
            }

            return newChild;
        }

        throw newHierarchyRequestError(node);
    }

    public static org.w3c.dom.Node replaceChild(Node node, org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) {
        if (node instanceof Branch) {
            Branch branch = (Branch) node;
            List<Node> list = branch.content();
            int index = oldChild instanceof Node ? list.indexOf((Node)oldChild) : -1;

            if (index < 0) {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "Tried to replace a non existing child for node: " + node);
            }

            list.set(index, asNode(newChild));

            return oldChild;
        }

        throw newHierarchyRequestError(node);
    }

    public static org.w3c.dom.Node removeChild(Node node, org.w3c.dom.Node oldChild) {
        if (node instanceof Branch) {
            Branch branch = (Branch) node;
            branch.remove((Node) oldChild);

            return oldChild;
        }

        throw newHierarchyRequestError(node);
    }

    public static org.w3c.dom.Node appendChild(Node node, org.w3c.dom.Node newChild) {
        if (node instanceof Branch) {
            Branch branch = (Branch) node;
            org.w3c.dom.Node previousParent = newChild.getParentNode();

            if (previousParent != null) {
                previousParent.removeChild(newChild);
            }

            branch.add((Node) newChild);

            return newChild;
        }

        throw newHierarchyRequestError(node);
    }

    public static boolean hasChildNodes(Node node) {
        return false;
    }

    public static org.w3c.dom.Node cloneNode(Node node, boolean deep) {
        return asDOMNode(node.clone());
    }

    public static void normalize(Node node) {
        notSupported();
    }

    public static boolean isSupported(Node n, String feature, String version) {
        return false;
    }

    public static boolean hasAttributes(Node node) {
        if (node instanceof Element) {
            return ((Element) node).attributeCount() > 0;
        }
        return false;
    }

    public static String getData(CharacterData charData) {
        return charData.getText();
    }

    public static void setData(CharacterData charData, String data) {
        charData.setText(data);
    }

    public static int getLength(CharacterData charData) {
        String text = charData.getText();

        return (text != null) ? text.length() : 0;
    }

    public static String substringData(CharacterData charData, int offset, int count) {
        checkCount(count);

        String text = charData.getText();
        int length = (text != null) ? text.length() : 0;

        checkOffset(offset, length);

        if (length == 0) {
            return "";
        }

        if ((offset + count) > length) {
            return text.substring(offset);
        }

        return text.substring(offset, offset + count);
    }

    public static void appendData(CharacterData charData, String arg) {
        checkIfReadOnly(charData);

        String text = charData.getText();
        if (text == null) {
            charData.setText(null);
        } else {
            charData.setText(text + arg);
        }
    }

    public static void insertData(CharacterData charData, int offset, String arg) {
        checkIfReadOnly(charData);

        String text = charData.getText();

        if (text == null) {
            charData.setText(arg);
        } else {
            int length = text.length();

            checkOffset(offset, length);

            StringBuilder buffer = new StringBuilder(text.length() + arg.length());
            buffer.append(text);
            buffer.insert(offset, arg);
            charData.setText(buffer.toString());
        }
    }

    public static void deleteData(CharacterData charData, int offset, int count) {
        checkIfReadOnly(charData);
        checkCount(count);

        String text = charData.getText();

        if (text != null) {
            int length = text.length();

            checkOffset(offset, length);

            StringBuilder buffer = new StringBuilder(text);
            buffer.delete(offset, offset + count);
            charData.setText(buffer.toString());
        }
    }

    public static void replaceData(CharacterData charData, int offset, int count, String arg) {
        checkIfReadOnly(charData);
        checkCount(count);

        String text = charData.getText();

        if (text != null) {
            int length = text.length();

            checkOffset(offset, length);

            StringBuilder buffer = new StringBuilder(text);
            buffer.replace(offset, offset + count, arg);
            charData.setText(buffer.toString());
        }
    }

    private static void checkIfReadOnly(CharacterData charData) {
        if (charData.isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "CharacterData node is read only: " + charData);
        }
    }

    private static void checkCount(int count) {
        if (count < 0) {
            throw new DOMException(DOMException.INDEX_SIZE_ERR, "Illegal value for count: " + count);
        }
    }

    private static void checkOffset(int offset, int length) {
        if ((offset < 0) || (offset >= length)) {
            throw new DOMException(DOMException.INDEX_SIZE_ERR, "No text at offset: " + offset);
        }
    }

    // Branch API
    // -------------------------------------------------------------------------

    public static void appendElementsByTagName(List<Element> list, Branch parent, String name) {
        final boolean isStar = "*".equals(name);

        for (Node node : parent) {
            if (node instanceof Element) {
                Element element = (Element) node;

                if (isStar || name.equals(element.getName())) {
                    list.add(element);
                }

                appendElementsByTagName(list, element, name);
            }
        }
    }

    public static void appendElementsByTagNameNS(List<Element> list, Branch parent, String namespace, String localName) {
        final boolean isStarNS = "*".equals(namespace);
        final boolean isStar = "*".equals(localName);

        for (Node node : parent) {
            if (node instanceof Element) {
                Element element = (Element) node;

                final boolean hasNotNamespace = ((namespace == null) || (namespace.length() == 0))
                        && ((element.getNamespaceURI() == null) || (element.getNamespaceURI().length() == 0));
                if ((isStarNS || hasNotNamespace || ((namespace != null) && namespace.equals(element.getNamespaceURI())))
                        && (isStar || localName.equals(element.getName()))) {
                    list.add(element);
                }

                appendElementsByTagNameNS(list, element, namespace, localName);
            }
        }
    }

    // Helper methods
    // -------------------------------------------------------------------------

    public static NodeList createNodeList(final List<? extends Node> list) {
        return new NodeList() {
            @Override
            public org.w3c.dom.Node item(int index) {
                if (index >= getLength()) {
                    /*
                     * From the NodeList specification: If index is greater than
                     * or equal to the number of nodes in the list, this returns
                     * null.
                     */
                    return null;
                }
                return DOMNodeHelper.asDOMNode(list.get(index));
            }

            @Override
            public int getLength() {
                return list.size();
            }
        };
    }

    public static org.w3c.dom.Node asDOMNode(Node node) {
        if (node == null) {
            return null;
        }

        if (node instanceof org.w3c.dom.Node) {
            return (org.w3c.dom.Node) node;
        }

        // Use DOMWriter?
        if (log.isWarnEnabled()) {
            log.warn("Cannot convert: " + node + " into a W3C DOM Node");
        }

        notSupported();
        return null;
    }

    public static org.w3c.dom.Document asDOMDocument(Document document) {
        if (document == null) {
            return null;
        }

        if (document instanceof org.w3c.dom.Document) {
            return (org.w3c.dom.Document) document;
        }

        // Use DOMWriter?
        notSupported();
        return null;
    }

    public static org.w3c.dom.DocumentType asDOMDocumentType(DocumentType dt) {
        if (dt == null) {
            return null;
        }

        if (dt instanceof org.w3c.dom.DocumentType) {
            return (org.w3c.dom.DocumentType) dt;
        }

        // Use DOMWriter?
        notSupported();
        return null;
    }

    public static org.w3c.dom.Text asDOMText(CharacterData text) {
        if (text == null) {
            return null;
        }

        if (text instanceof org.w3c.dom.Text) {
            return (org.w3c.dom.Text) text;
        }

        // Use DOMWriter?
        notSupported();
        return null;
    }

    public static org.w3c.dom.Element asDOMElement(Node element) {
        if (element == null) {
            return null;
        }

        if (element instanceof org.w3c.dom.Element) {
            return (org.w3c.dom.Element) element;
        }

        // Use DOMWriter?
        notSupported();
        return null;
    }

    public static org.w3c.dom.Attr asDOMAttr(Node attribute) {
        if (attribute == null) {
            return null;
        }

        if (attribute instanceof org.w3c.dom.Attr) {
            return (org.w3c.dom.Attr) attribute;
        }
        // Use DOMWriter?
        notSupported();

        return null;
    }

    public static Node asNode(org.w3c.dom.Node node) {
        if (node == null) {
            return null;
        }

        if (node instanceof Node) {
            return (Node) node;
        }
        if (log.isWarnEnabled()) {
            log.warn("Cannot convert: " + node + " into a W3C DOM Node");
        }
        notSupported();
        return null;
    }

    public static boolean isStringEquals(String string1, String string2) {
        return Objects.equals(string1, string2);
    }

    public static boolean isNodeEquals(org.w3c.dom.Node node1, org.w3c.dom.Node node2) {
        if (node1 == node2) {
            return true;
        }
        return node1 != null && node2 != null
                && node1.getNodeType() == node2.getNodeType()
                && isStringEquals(node1.getNodeName(), node2.getNodeName())
                && isStringEquals(node1.getLocalName(), node2.getLocalName())
                && isStringEquals(node1.getNamespaceURI(), node2.getNamespaceURI())
                && isStringEquals(node1.getPrefix(), node2.getPrefix())
                && isStringEquals(node1.getNodeValue(), node2.getNodeValue());
    }

    public static boolean isNodeSame(org.w3c.dom.Node node1, org.w3c.dom.Node node2) {
        return node1 == node2;
    }

    /**
     * Called when a method has not been implemented yet
     *
     * @throws DOMException DOCUMENT ME!
     */
    public static void notSupported() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported yet");
    }

    public static DOMException newHierarchyRequestError(org.w3c.dom.Node node) {
        return new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Children not allowed for this node: " + node);
    }

    private static DOMException newHierarchyRequestError(Node node) {
        return new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Children not allowed for this node: " + node);
    }

    public static class EmptyNodeList implements NodeList {
        @Override
        public org.w3c.dom.Node item(int index) {
            return null;
        }

        @Override
        public int getLength() {
            return 0;
        }
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
