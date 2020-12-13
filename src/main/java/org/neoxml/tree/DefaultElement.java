/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.neoxml.tree;

import org.neoxml.Attribute;
import org.neoxml.Branch;
import org.neoxml.DefaultDocumentFactory;
import org.neoxml.Document;
import org.neoxml.DocumentFactory;
import org.neoxml.Element;
import org.neoxml.Namespace;
import org.neoxml.Node;
import org.neoxml.NodeList;
import org.neoxml.QName;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * <code>DefaultElement</code> is the default neoxml default implementation of an XML element.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.59 $
 */
public class DefaultElement extends AbstractElement {
    /**
     * The <code>DefaultDocumentFactory</code> instance used by default
     */
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.getInstance();

    /**
     * The <code>QName</code> for this element
     */
    protected QName qname;

    /**
     * Stores the parent branch of this node which is either a Document if this
     * element is the root element in a document, or another Element if it is a
     * child of the root document, or null if it has not been added to a
     * document yet.
     */
    protected Branch parentBranch;

    /**
     * Stores null for no content or a List
     * for multiple content nodes. The List will be lazily constructed when required.
     */
    protected NodeList<Node> content;

    /**
     * Lazily constructed list of attributes
     */
    protected NodeList<Attribute> attributes;


    public DefaultElement(String name) {
        this(DOCUMENT_FACTORY.createQName(name));
    }

    public DefaultElement(QName qname) {
        this.qname = qname;
    }

    public DefaultElement(QName qname, int attributeCount) {
        this.qname = qname;
        this.attributes = createAttributeList(attributeCount);
    }

    public DefaultElement(String name, Namespace namespace) {
        this(DOCUMENT_FACTORY.createQName(name, namespace));
    }


    @Override
    public Element getParent() {
        Element result = null;

        if (parentBranch instanceof Element) {
            result = (Element) parentBranch;
        }

        return result;
    }

    @Override
    public void setParent(Element parent) {
        if (parentBranch instanceof Element || (parent != null)) {
            parentBranch = parent;
        }
    }

    @Override
    public Document getDocument() {
        if (parentBranch instanceof Document) {
            return (Document) parentBranch;
        } else if (parentBranch instanceof Element) {
            Element parent = (Element) parentBranch;

            return parent.getDocument();
        }

        return null;
    }

    @Override
    public void setDocument(Document document) {
        if (parentBranch instanceof Document || (document != null)) {
            parentBranch = document;
        }
    }

    @Override
    public boolean supportsParent() {
        return true;
    }

    @Override
    public QName getQName() {
        return qname;
    }

    @Override
    public void setQName(QName name) {
        this.qname = name;
    }

    @Override
    public Namespace getNamespaceForURI(String uri) {
        Namespace namespace = super.getNamespaceForURI(uri);

        if (namespace == null) {
            Element parent = getParent();

            if (parent != null) {
                namespace = parent.getNamespaceForURI(uri);
            }
        }

        return namespace;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setContent(List<? extends Node> nodes) {
        if (nodes instanceof NodeList) {
            final NodeList<Node> nodeList = (NodeList<Node>) nodes;

            if (nodeList.getParent() != this) {
                clearContentList();

                if (nodeList.getParent() != null) {
                    this.content = nodeList.copy();
                } else {
                    this.content = nodeList.content();
                }

                safeContentList().attach(this);
            }
        } else {
            clearContentList();
            setContentList(nodes);
        }
    }

    @Override
    public Node node(int index) {
        final NodeList<Node> list = safeContentList();

        if (index < 0 || index >= list.size()) {
            return null;
        }

        return list.get(index);
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

    @Override
    public NodeList<Attribute> attributes() {
        return safeAttributeList().facade();
    }

    @Override
    public void setAttributes(List<Attribute> attributes) {
        if (attributes instanceof NodeList) {
            final NodeList<Attribute> attrList = (NodeList<Attribute>) attributes;

            if (attrList.getParent() != this) {
                clearAttributeList();

                if (attrList.getParent() != null) {
                    this.attributes = attrList.copy();
                } else {
                    this.attributes = attrList.content();
                }

                safeAttributeList().attach(this);
            }
        } else {
            clearAttributeList();
            setAttributeList(attributes);
        }
    }

    @Override
    public Iterator<Attribute> attributeIterator() {
        return safeAttributeList().iterator();
    }

    @Override
    public Attribute attribute(int index) {
        final NodeList<Attribute> attrList = safeAttributeList();

        if (index < 0 || index >= attrList.size()) {
            return null;
        }

        return attrList.get(index);
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

    @Override
    public Attribute attribute(String name, Namespace namespace) {
        return attribute(getDocumentFactory().createQName(name, namespace));
    }

    @Override
    public void add(Attribute attribute) {
        if (attribute.getValue() == null) {
            // try remove a previous attribute with the same
            // name since adding an attribute with a null value
            // is equivalent to removing it.
            Attribute oldAttribute = attribute(attribute.getQName());

            if (oldAttribute != null) {
                remove(oldAttribute);
            }
        } else {
            attributeList().add(attribute);
        }
    }

    @Override
    public boolean remove(Attribute attribute) {
        boolean answer = attributeList().remove(attribute);

        if (!answer) {
            // we may have a copy of the attribute
            Attribute copy = attribute(attribute.getQName());

            if (copy != null) {
                answer = attributeList().remove(copy);
            }
        }

        return answer;
    }

    /**
     * <p>
     * This returns a deep clone of this element. The new element is detached from its parent, and getParent() on the
     * clone will return null.
     * </p>
     *
     * @return the clone of this element
     */
    @Override
    public DefaultElement clone() {
        DefaultElement answer = (DefaultElement) super.clone();

        if (answer != this) {
            answer.attributes = attributes != null ? attributes.copy().attach(answer) : null;
            answer.content = content != null ? content.copy().attach(answer) : null;
        }

        return answer;
    }

    @Override
    protected void addNewNode(Node node) {
        contentList().add(node);
    }

    @Override
    protected boolean removeNode(Node node) {
        return safeContentList().remove(node);
    }

    @Override
    protected DocumentFactory getDocumentFactory() {
        DocumentFactory factory = qname.getDocumentFactory();
        return (factory != null) ? factory : DOCUMENT_FACTORY;
    }

    @Override
    protected NodeList<Node> contentList() {
        if (this.content == null) {
            this.content = createContentList();
        }
        return this.content;
    }

    @Override
    protected NodeList<Node> contentList(int size) {
        if (this.content == null) {
            this.content = createContentList(size);
        } else if (this.content instanceof DefaultNodeList<?>) {
            ((DefaultNodeList<Node>) this.content).ensureCapacity(size);
        }
        return this.content;
    }

    @Override
    protected NodeList<Node> safeContentList() {
        if (this.content == null) {
            return emptyNodeList();
        }
        return this.content;
    }

    @Override
    protected void clearContentList() {
        safeContentList().clear();
    }

    @Override
    protected void setContentList(List<? extends Node> nodes) {
        if (nodes != null && !nodes.isEmpty()) {
            final NodeList<Node> newContent = contentList(nodes.size());

            for (Node node : nodes) {
                appendNewNode(newContent, node);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends Node> void appendNewNode(NodeList<T> newContent, T node) {
        Element parent = node.getParent();

        if (parent != null) {
            node = (T) node.clone();
        }

        newContent.add(node);
    }

    @Override
    protected NodeList<Attribute> attributeList() {
        if (this.attributes == null) {
            this.attributes = createAttributeList();
        }
        return attributes;
    }

    @Override
    protected NodeList<Attribute> attributeList(int attributeCount) {
        if (this.attributes == null) {
            this.attributes = createAttributeList(attributeCount);
        } else if (this.attributes instanceof DefaultNodeList<?>) {
            ((DefaultNodeList<Attribute>) this.attributes).ensureCapacity(attributeCount);
        }
        return attributes;
    }

    @Override
    protected NodeList<Attribute> safeAttributeList() {
        if (this.attributes == null) {
            return emptyAttributeList();
        }

        return this.attributes;
    }

    @Override
    protected void clearAttributeList() {
        if (this.attributes != null) {
            this.attributes.clear();
        }
    }

    @Override
    protected void setAttributeList(List<Attribute> attrs) {
        if (attrs != null && !attrs.isEmpty()) {
            final NodeList<Attribute> newContent = attributeList(attrs.size());

            for (Attribute node : attrs) {
                appendNewNode(newContent, node);
            }
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
