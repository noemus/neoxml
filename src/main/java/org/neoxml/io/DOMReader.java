/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neoxml.Branch;
import org.neoxml.DefaultDocumentFactory;
import org.neoxml.Document;
import org.neoxml.DocumentFactory;
import org.neoxml.Element;
import org.neoxml.Namespace;
import org.neoxml.QName;
import org.neoxml.tree.NamespaceStack;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <code>DOMReader</code> navigates a W3C DOM tree and creates a neoxml tree from it.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.17 $
 */
public class DOMReader {
    /**
     * <code>DefaultDocumentFactory</code> used to create new document objects
     */
    private DocumentFactory factory;

    /**
     * stack of <code>Namespace</code> and <code>QName</code> objects
     */
    private final NamespaceStack namespaceStack;

    public DOMReader() {
        this.factory = DefaultDocumentFactory.getInstance();
        this.namespaceStack = new NamespaceStack(factory);
    }

    public DOMReader(DocumentFactory factory) {
        this.factory = factory;
        this.namespaceStack = new NamespaceStack(factory);
    }

    /**
     * @return the <code>DefaultDocumentFactory</code> used to create document
     * objects
     */
    public DocumentFactory getDocumentFactory() {
        return factory;
    }

    /**
     * <p>
     * This sets the <code>DocumentFactory</code> used to create new documents. This method allows the building of
     * custom neoxml tree objects to be implemented easily using a custom derivation of
     * {@link org.neoxml.DefaultDocumentFactory}
     * </p>
     *
     * @param docFactory <code>DocumentFactory</code> used to create neoxml objects
     */
    public void setDocumentFactory(DocumentFactory docFactory) {
        this.factory = docFactory;
        this.namespaceStack.setDocumentFactory(factory);
    }

    public Document read(org.w3c.dom.Document domDocument) {
        if (domDocument instanceof Document) {
            return (Document) domDocument;
        }

        Document document = createDocument();
        clearNamespaceStack();
        readTree(domDocument, document);

        return document;
    }

    private void readTree(org.w3c.dom.Document domDocument, Document document) {
        org.w3c.dom.NodeList nodeList = domDocument.getChildNodes();

        for (int i = 0, size = nodeList.getLength(); i < size; i++) {
            readTree(nodeList.item(i), document);
        }
    }

    protected void readTree(org.w3c.dom.Node node, Branch current) {
        switch (node.getNodeType()) {
            case org.w3c.dom.Node.ELEMENT_NODE:
                readElement(node, current);
                break;

            case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                current.addProcessingInstruction(node.getNodeName(), node.getNodeValue());
                break;

            case org.w3c.dom.Node.COMMENT_NODE:
                current.addComment(node.getNodeValue());
                break;

            case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
                org.w3c.dom.DocumentType domDocType = (org.w3c.dom.DocumentType) node;
                Document.require(current)
                        .addDocType(domDocType.getName(), domDocType.getPublicId(), domDocType.getSystemId());
                break;

            case org.w3c.dom.Node.TEXT_NODE:
                Element.require(current).addText(node.getNodeValue());
                break;

            case org.w3c.dom.Node.CDATA_SECTION_NODE:
                Element.require(current).addCDATA(node.getNodeValue());
                break;

            case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:
                // is there a better way to get the value of an entity?
                org.w3c.dom.Node firstChild = node.getFirstChild();

                if (firstChild != null) {
                    Element.require(current).addEntity(node.getNodeName(), firstChild.getNodeValue());
                } else {
                    Element.require(current).addEntity(node.getNodeName(), "");
                }
                break;

            case org.w3c.dom.Node.ENTITY_NODE:
                Element.require(current).addEntity(node.getNodeName(), node.getNodeValue());
                break;

            default:
                if (log.isWarnEnabled()) {
                    log.warn("WARNING: Unknown DOM node type: " + node.getNodeType());
                }
        }
    }

    protected void readElement(org.w3c.dom.Node node, Branch current) {
        int previouslyDeclaredNamespaces = namespaceStack.size();

        String namespaceUri = node.getNamespaceURI();

        org.w3c.dom.NamedNodeMap attributeList = node.getAttributes();

        if ((attributeList != null) && (namespaceUri == null)) {
            // test if we have a "xmlns" attribute
            org.w3c.dom.Node attribute = attributeList.getNamedItem("xmlns");

            if (attribute != null) {
                namespaceUri = attribute.getNodeValue();
            }
        }

        QName qName = namespaceStack.getQName(namespaceUri, node.getLocalName(), node.getNodeName());
        Element element = current.addElement(qName);

        if (attributeList != null) {
            int size = attributeList.getLength();
            List<org.w3c.dom.Node> attributes = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                org.w3c.dom.Node attribute = attributeList.item(i);

                // Define all namespaces first then process attributes later
                String name = attribute.getNodeName();

                if (name.startsWith("xmlns")) {
                    String prefix = getPrefix(name);
                    String uri = attribute.getNodeValue();

                    Namespace namespace = namespaceStack.addNamespace(prefix, uri);
                    element.add(namespace);
                } else {
                    attributes.add(attribute);
                }
            }

            addAttributes(element, attributes);
        }

        recurseOnChildNodes(node, element);
        popNamespaceFromStack(previouslyDeclaredNamespaces);
    }

    private void addAttributes(Element element, List<org.w3c.dom.Node> attributes) {
        // the namespaces should be available by now
        for (org.w3c.dom.Node attribute : attributes) {
            QName attributeQName = namespaceStack.getQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getNodeName());
            element.addAttribute(attributeQName, attribute.getNodeValue());
        }
    }

    private void recurseOnChildNodes(org.w3c.dom.Node node, Element element) {
        org.w3c.dom.NodeList children = node.getChildNodes();

        for (int i = 0, size = children.getLength(); i < size; i++) {
            org.w3c.dom.Node child = children.item(i);
            readTree(child, element);
        }
    }

    private void popNamespaceFromStack(int previouslyDeclaredNamespaces) {
        while (namespaceStack.size() > previouslyDeclaredNamespaces) {
            namespaceStack.pop();
        }
    }

    protected Namespace getNamespace(String prefix, String uri) {
        return getDocumentFactory().createNamespace(prefix, uri);
    }

    protected Document createDocument() {
        return getDocumentFactory().createDocument();
    }

    protected void clearNamespaceStack() {
        namespaceStack.clear();

        if (!namespaceStack.contains(Namespace.XML_NAMESPACE)) {
            namespaceStack.push(Namespace.XML_NAMESPACE);
        }
    }

    private String getPrefix(String xmlnsDecl) {
        int index = xmlnsDecl.indexOf(':', 5);

        if (index != -1) {
            return xmlnsDecl.substring(index + 1);
        } else {
            return "";
        }
    }

    private static final Log log = LogFactory.getLog(DOMReader.class);
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
