/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.neoxml.tree;

import org.neoxml.Branch;
import org.neoxml.Comment;
import org.neoxml.Element;
import org.neoxml.IllegalAddException;
import org.neoxml.Namespace;
import org.neoxml.Node;
import org.neoxml.NodeList;
import org.neoxml.ProcessingInstruction;
import org.neoxml.QName;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>
 * <code>AbstractBranch</code> is an abstract base class for tree implementors to use for implementation inheritence.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.44 $
 */
public abstract class AbstractBranch extends AbstractNode implements Branch {

    protected static final int DEFAULT_CONTENT_LIST_SIZE = 5;

    public AbstractBranch() {}

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean hasContent() {
        return !safeContentList().isEmpty();
    }

    @Override
    public NodeList<Node> content() {
        return contentList();
    }

    @Override
    public void clearContent() {
        clearContentList();
    }

    @Override
    public String getText() {
        final List<Node> content = safeContentList();
        final int size = content.size();

        if (size >= 1) {
            Node first = content.get(0);
            String firstText = getContentAsText(first);

            if (size == 1) {
                // optimised to avoid StringBuilder creation
                return firstText;
            } else {
                StringBuilder buffer = new StringBuilder(firstText.length() * size).append(firstText);

                for (int i = 1; i < size; i++) {
                    Node node = content.get(i);
                    buffer.append(getContentAsText(node));
                }

                return buffer.toString();
            }
        }

        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param content DOCUMENT ME!
     * @return the text value of the given content object as text which returns
     * the text value of CDATA, Entity or Text nodes
     */
    protected String getContentAsText(Node node) {
        switch (node.getNodeTypeEnum()) {
            case CDATA_SECTION_NODE:
                // case ENTITY_NODE:
            case ENTITY_REFERENCE_NODE:
            case TEXT_NODE:
                return node.getText();

            default:
                return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param content DOCUMENT ME!
     * @return the XPath defined string-value of the given content object
     */
    protected String getContentAsStringValue(Node node) {
        switch (node.getNodeTypeEnum()) {
            case CDATA_SECTION_NODE:
                // case ENTITY_NODE:
            case ENTITY_REFERENCE_NODE:
            case TEXT_NODE:
            case ELEMENT_NODE:
                return node.getStringValue();
            default:
                return "";
        }
    }

    public String getTextTrim() {
        String text = getText();

        StringBuilder textContent = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(text);

        while (tokenizer.hasMoreTokens()) {
            String str = tokenizer.nextToken();
            textContent.append(str);

            if (tokenizer.hasMoreTokens()) {
                textContent.append(" "); // separator
            }
        }

        return textContent.toString();
    }

    @Override
    public void setProcessingInstructions(List<ProcessingInstruction> listOfPIs) {
        for (ProcessingInstruction pi : listOfPIs) {
            addNode(pi);
        }
    }

    @Override
    public Element addElement(String name) {
        Element node = getDocumentFactory().createElement(name);
        add(node);

        return node;
    }

    @Override
    public Element addElement(String qualifiedName, String namespaceURI) {
        Element node = getDocumentFactory().createElement(qualifiedName, namespaceURI);
        add(node);

        return node;
    }

    @Override
    public Element addElement(QName qname) {
        Element node = getDocumentFactory().createElement(qname);
        add(node);

        return node;
    }

    public Element addElement(String name, String prefix, String uri) {
        Namespace namespace = Namespace.get(prefix, uri);
        QName qName = getDocumentFactory().createQName(name, namespace);

        return addElement(qName);
    }

    // polymorphic node methods

    @Override
    public void add(Node node) {
        switch (node.getNodeTypeEnum()) {
            case ELEMENT_NODE:
                add((Element) node);
                break;
            case COMMENT_NODE:
                add((Comment) node);
                break;
            case PROCESSING_INSTRUCTION_NODE:
                add((ProcessingInstruction) node);
                break;
            default:
                invalidNodeTypeAddException(node);
        }
    }

    @Override
    public boolean remove(Node node) {
        switch (node.getNodeTypeEnum()) {
            case ELEMENT_NODE:
                return remove((Element) node);
            case COMMENT_NODE:
                return remove((Comment) node);
            case PROCESSING_INSTRUCTION_NODE:
                return remove((ProcessingInstruction) node);
            default:
                invalidNodeTypeAddException(node);
                return false;
        }
    }

    // typesafe versions using node classes

    @Override
    public void add(Comment comment) {
        addNode(comment);
    }

    @Override
    public void add(Element element) {
        addNode(element);
    }

    @Override
    public void add(ProcessingInstruction pi) {
        addNode(pi);
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
    public boolean remove(ProcessingInstruction pi) {
        return removeNode(pi);
    }

    @Override
    public Element elementByID(String elementID) {
        for (Element element : safeContentList().filter(ELEMENT_CONDITION, Element.class)) {
            final String id = element.attributeValue("ID");

            if ((id != null) && id.equals(elementID)) {
                return element;
            } else {
                element = element.elementByID(elementID);

                if (element != null) {
                    return element;
                }
            }
        }

        return null;
    }

    @Override
    public void appendContent(Branch branch) {
        contentList(branch.nodeCount());

        for (Node node : branch) {
            add(node.clone());
        }
    }

    @Override
    public void appendContent(NodeList<? extends Node> nodes) {
        if (nodes != null) {
            contentList(nodes.size());

            for (Node node : nodes) {
                add(node.clone());
            }
        }
    }

    @Override
    public Node node(int index) {
        return safeContentList().get(index);
    }

    @Override
    public int nodeCount() {
        return safeContentList().size();
    }

    @Override
    public int indexOf(Node node) {
        return safeContentList().indexOf(node);
    }

    @Override
    public Iterator<Node> nodeIterator() {
        return safeContentList().iterator();
    }

    @Override
    public Iterator<Node> iterator() {
        return nodeIterator();
    }

    //Processing instruction API
    // -------------------------------------------------------------------------

    @Override
    public NodeList<ProcessingInstruction> processingInstructions() {
        return contentList().filter(PI_CONDITION, ProcessingInstruction.class);
    }

    @Override
    public NodeList<ProcessingInstruction> processingInstructions(String target) {
        return contentList().filter(new ProcessingInstructionCondition(target), ProcessingInstruction.class);
    }

    @Override
    public ProcessingInstruction processingInstruction(String target) {
        return safeContentList().find(new ProcessingInstructionCondition(target), ProcessingInstruction.class);
    }

    @Override
    public boolean removeProcessingInstruction(String target) {
        return safeContentList().removeIf(new ProcessingInstructionCondition(target));
    }

    // Implementation methods

    /**
     * DOCUMENT ME!
     *
     * @return the internal List used to manage the content or creates new if not available
     */
    protected abstract NodeList<Node> contentList();

    /**
     * DOCUMENT ME!
     *
     * @param size DOCUMENT ME!
     * @return the internal List used to manage the content or creates new with given minimal size if not available
     */
    protected abstract NodeList<Node> contentList(int size);

    /**
     * DOCUMENT ME!
     *
     * @return the internal List used to manage the content or emptyList if not available
     */
    protected abstract NodeList<Node> safeContentList();

    /**
     * DOCUMENT ME!
     */
    protected abstract void clearContentList();

    /**
     * A Factory Method pattern which creates a List implementation used to store content
     *
     * @return DOCUMENT ME!
     */
    protected <T extends Node> NodeList<T> createContentList() {
        return new DefaultNodeList<>(this);
    }

    /**
     * A Factory Method pattern which creates a List implementation with given size used to store content
     *
     * @param size DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected <T extends Node> NodeList<T> createContentList(int size) {
        return new DefaultNodeList<>(this, size);
    }


    /**
     * This method is for internal use only
     * Replaces nodes in node list with suplied nodes (nodes are cloned if necessary). Used in setContent
     *
     * @param newContent DOCUMENT ME!
     * @param node       DOCUMENT ME!
     */
    protected abstract void setContentList(List<? extends Node> nodes);

    /**
     * This method is for internal use only
     * Appends node (and clone it if necessary) to newContent nodelist. used in setContent and setAttributes
     *
     * @param newContent DOCUMENT ME!
     * @param node       DOCUMENT ME!
     */
    protected abstract <T extends Node> void appendNewNode(NodeList<T> newContent, T node);

    @SuppressWarnings("unchecked")
    protected NodeList<Node> emptyNodeList() {
        return (NodeList<Node>) EMPTY_NODE_LIST;
    }

    protected void addNode(Node node) {
        beforeChildAdd(node);
        addNewNode(node);
    }

    protected void addNode(int index, Node node) {
        beforeChildAdd(node);
        addNewNode(index, node);
    }

    /**
     * Like addNode() but does not require a parent check
     *
     * @param node DOCUMENT ME!
     */
    protected void addNewNode(Node node) {
        contentList().add(node);
    }

    protected void addNewNode(int index, Node node) {
        contentList().add(index, node);
    }

    protected boolean removeNode(Node node) {
        return safeContentList().remove(node);
    }

    protected abstract void beforeChildAdd(Node node);

    /**
     * Called when a new child node has been added to me to allow any parent
     * relationships to be created or events to be fired.
     *
     * @param node DOCUMENT ME!
     */
    protected abstract void childAdded(Node node);

    /**
     * Called when a child node has been removed to allow any parent
     * relationships to be deleted or events to be fired.
     *
     * @param node DOCUMENT ME!
     */
    protected abstract void childRemoved(Node node);

    /**
     * Called when the given List content has been removed so each node should
     * have its parent and document relationships cleared
     */
    protected void contentRemoved() {
        for (Node node : safeContentList()) {
            childRemoved(node);
        }
    }

    /**
     * Called when an invalid node has been added. Throws an {@link IllegalAddException}.
     *
     * @param node DOCUMENT ME!
     * @throws IllegalAddException DOCUMENT ME!
     */
    protected void invalidNodeTypeAddException(Node node) {
        throw new IllegalAddException("Invalid node type. Cannot add node: " + node + " to this branch: " + this);
    }

    static final NodeList<? extends Node> EMPTY_NODE_LIST = new EmptyNodeList<>();
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
