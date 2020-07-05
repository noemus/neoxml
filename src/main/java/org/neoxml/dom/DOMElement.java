/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.dom;

import org.neoxml.Attribute;
import org.neoxml.DocumentFactory;
import org.neoxml.Element;
import org.neoxml.Namespace;
import org.neoxml.QName;
import org.neoxml.tree.DefaultElement;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <code>DOMElement</code> implements an XML element which supports the W3C DOM API.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.23 $
 */
public class DOMElement extends DefaultElement implements org.w3c.dom.Element {
    /**
     * The <code>DefaultDocumentFactory</code> instance used by default
     */
    private static final DocumentFactory DOCUMENT_FACTORY = DOMDocumentFactory.getInstance();

    public DOMElement(String name) {
        super(name);
    }

    public DOMElement(QName qname) {
        super(qname);
    }

    public DOMElement(QName qname, int attributeCount) {
        super(qname, attributeCount);
    }

    public DOMElement(String name, Namespace namespace) {
        super(name, namespace);
    }

    // org.w3c.dom.Node interface
    // -------------------------------------------------------------------------

    public boolean supports(String feature, String version) {
        return DOMNodeHelper.supports(this, feature, version);
    }

    @Override
    public String getNamespaceURI() {
        return getQName().getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return getQName().getNamespacePrefix();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        DOMNodeHelper.setPrefix(this, prefix);
    }

    @Override
    public String getLocalName() {
        return getQName().getName();
    }

    @Override
    public String getNodeName() {
        return getName();
    }

    // already part of API
    //
    // public short getNodeType();

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {}

    @Override
    public org.w3c.dom.Node getParentNode() {
        return DOMNodeHelper.getParentNode(this);
    }

    @Override
    public NodeList getChildNodes() {
        return DOMNodeHelper.createNodeList(content());
    }

    @Override
    public org.w3c.dom.Node getFirstChild() {
        return DOMNodeHelper.asDOMNode(node(0));
    }

    @Override
    public org.w3c.dom.Node getLastChild() {
        return DOMNodeHelper.asDOMNode(node(nodeCount() - 1));
    }

    @Override
    public org.w3c.dom.Node getPreviousSibling() {
        return DOMNodeHelper.getPreviousSibling(this);
    }

    @Override
    public org.w3c.dom.Node getNextSibling() {
        return DOMNodeHelper.getNextSibling(this);
    }

    @Override
    public NamedNodeMap getAttributes() {
        return new DOMAttributeNodeMap(this);
    }

    @Override
    public Document getOwnerDocument() {
        return DOMNodeHelper.getOwnerDocument(this);
    }

    @Override
    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild,
                                         org.w3c.dom.Node refChild) throws DOMException {
        checkNewChildNode(newChild);

        return DOMNodeHelper.insertBefore(this, newChild, refChild);
    }

    @Override
    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild,
                                         org.w3c.dom.Node oldChild) throws DOMException {
        checkNewChildNode(newChild);

        return DOMNodeHelper.replaceChild(this, newChild, oldChild);
    }

    @Override
    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild)
            throws DOMException {
        return DOMNodeHelper.removeChild(this, oldChild);
    }

    @Override
    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild)
            throws DOMException {
        checkNewChildNode(newChild);

        return DOMNodeHelper.appendChild(this, newChild);
    }

    private void checkNewChildNode(org.w3c.dom.Node newChild)
            throws DOMException {
        final int nodeType = newChild.getNodeType();

        if (!((nodeType == Node.ELEMENT_NODE) || (nodeType == Node.TEXT_NODE)
                || (nodeType == Node.COMMENT_NODE)
                || (nodeType == Node.PROCESSING_INSTRUCTION_NODE)
                || (nodeType == Node.CDATA_SECTION_NODE)
                || (nodeType == Node.ENTITY_REFERENCE_NODE))) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
                                   "Given node cannot be a child of element");
        }
    }

    @Override
    public boolean hasChildNodes() {
        return nodeCount() > 0;
    }

    @Override
    public org.w3c.dom.Node cloneNode(boolean deep) {
        return DOMNodeHelper.cloneNode(this, deep);
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return DOMNodeHelper.isSupported(this, feature, version);
    }

    @Override
    public boolean hasAttributes() {
        return DOMNodeHelper.hasAttributes(this);
    }

    // org.w3c.dom.Element interface
    // -------------------------------------------------------------------------

    @Override
    public String getTagName() {
        return getName();
    }

    @Override
    public String getAttribute(String name) {
        String answer = attributeValue(name);

        return (answer != null) ? answer : "";
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        addAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        Attribute attribute = attribute(name);

        if (attribute != null) {
            remove(attribute);
        }
    }

    @Override
    public org.w3c.dom.Attr getAttributeNode(String name) {
        return DOMNodeHelper.asDOMAttr(attribute(name));
    }

    @Override
    public org.w3c.dom.Attr setAttributeNode(org.w3c.dom.Attr newAttr)
            throws DOMException {
        if (this.isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                   "No modification allowed");
        }

        Attribute attribute = attribute(newAttr);

        if (attribute != newAttr) {
            if (newAttr.getOwnerElement() != null) {
                throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR,
                                       "Attribute is already in use");
            }

            Attribute newAttribute = createAttribute(newAttr);

            if (attribute != null) {
                attribute.detach();
            }

            add(newAttribute);
        }

        return DOMNodeHelper.asDOMAttr(attribute);
    }

    @Override
    public org.w3c.dom.Attr removeAttributeNode(org.w3c.dom.Attr oldAttr)
            throws DOMException {
        Attribute attribute = attribute(oldAttr);

        if (attribute != null) {
            attribute.detach();

            return DOMNodeHelper.asDOMAttr(attribute);
        } else {
            throw new DOMException(DOMException.NOT_FOUND_ERR,
                                   "No such attribute");
        }
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) {
        Attribute attribute = attribute(namespaceURI, localName);

        if (attribute != null) {
            String answer = attribute.getValue();

            if (answer != null) {
                return answer;
            }
        }

        return "";
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName,
                               String value) throws DOMException {
        Attribute attribute = attribute(namespaceURI, qualifiedName);

        if (attribute != null) {
            attribute.setValue(value);
        } else {
            QName qName = getQName(namespaceURI, qualifiedName);
            addAttribute(qName, value);
        }
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName)
            throws DOMException {
        Attribute attribute = attribute(namespaceURI, localName);

        if (attribute != null) {
            remove(attribute);
        }
    }

    @Override
    public org.w3c.dom.Attr getAttributeNodeNS(String namespaceURI,
                                               String localName) {
        Attribute attribute = attribute(namespaceURI, localName);

        if (attribute != null) {
            DOMNodeHelper.asDOMAttr(attribute);
        }

        return null;
    }

    @Override
    public org.w3c.dom.Attr setAttributeNodeNS(org.w3c.dom.Attr newAttr)
            throws DOMException {
        Attribute attribute = attribute(newAttr.getNamespaceURI(), newAttr
                .getLocalName());

        if (attribute != null) {
            attribute.setValue(newAttr.getValue());
        } else {
            attribute = createAttribute(newAttr);
            add(attribute);
        }

        return DOMNodeHelper.asDOMAttr(attribute);
    }

    @Override
    public NodeList getElementsByTagName(String name) {
        List<Element> list = new ArrayList<>();
        DOMNodeHelper.appendElementsByTagName(list, this, name);

        return DOMNodeHelper.createNodeList(list);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespace, String lName) {
        List<Element> list = new ArrayList<>();
        DOMNodeHelper.appendElementsByTagNameNS(list, this, namespace, lName);

        return DOMNodeHelper.createNodeList(list);
    }

    @Override
    public boolean hasAttribute(String name) {
        return attribute(name) != null;
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return attribute(namespaceURI, localName) != null;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    protected DocumentFactory getDocumentFactory() {
        DocumentFactory factory = getQName().getDocumentFactory();

        return (factory != null) ? factory : DOCUMENT_FACTORY;
    }

    protected Attribute attribute(org.w3c.dom.Attr attr) {
        return attribute(DOCUMENT_FACTORY.createQName(attr.getLocalName(), attr
                .getPrefix(), attr.getNamespaceURI()));
    }

    protected Attribute attribute(String namespaceURI, String localName) {
        for (Attribute attribute : safeAttributeList()) {
            if (localName.equals(attribute.getName()) && (emptyNamespace(namespaceURI, attribute) || sameNamespace(namespaceURI, attribute))) {
                return attribute;
            }
        }

        return null;
    }

    private boolean emptyNamespace(String namespaceURI, Attribute attribute) {
        final String uri = attribute.getNamespaceURI();
        return (namespaceURI == null || namespaceURI.length() == 0) && (uri == null || uri.length() == 0);
    }

    private boolean sameNamespace(String namespaceURI, Attribute attribute) {
        return namespaceURI != null && namespaceURI.equals(attribute.getNamespaceURI());
    }

    protected Attribute createAttribute(org.w3c.dom.Attr newAttr) {
        QName qName = null;
        String name = newAttr.getLocalName();

        if (name != null) {
            String prefix = newAttr.getPrefix();
            String uri = newAttr.getNamespaceURI();
            qName = getDocumentFactory().createQName(name, prefix, uri);
        } else {
            name = newAttr.getName();
            qName = getDocumentFactory().createQName(name);
        }

        return new DOMAttribute(qName, newAttr.getValue());
    }

    protected QName getQName(String namespace, String qualifiedName) {
        int index = qualifiedName.indexOf(':');
        String prefix = "";
        String localName = qualifiedName;

        if (index >= 0) {
            prefix = qualifiedName.substring(0, index);
            localName = qualifiedName.substring(index + 1);
        }

        return getDocumentFactory().createQName(localName, prefix, namespace);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getBaseURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTextContent() throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSameNode(Node other) {
        return DOMNodeHelper.isNodeSame(this, other);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEqualNode(Node other) {
        return DOMNodeHelper.isNodeEquals(this, other);
    }

    @Override
    public Object getFeature(String feature, String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getUserData(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
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
