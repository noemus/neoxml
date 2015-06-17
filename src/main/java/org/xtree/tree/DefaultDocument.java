/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.xtree.tree;

import java.util.List;

import org.xml.sax.EntityResolver;
import org.xtree.DefaultDocumentFactory;
import org.xtree.Document;
import org.xtree.DocumentFactory;
import org.xtree.DocumentType;
import org.xtree.Element;
import org.xtree.IllegalAddException;
import org.xtree.Node;
import org.xtree.NodeList;

/**
 * <p>
 * <code>DefaultDocument</code> is the default DOM4J default implementation of an XML document.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.34 $
 */
public class DefaultDocument extends AbstractDocument
{
  /**
   * The name of the document
   */
  private String name;
  /**
   * The root element of this document
   */
  private Element rootElement;
  /**
   * Store the contents of the document as a lazily created <code>List</code>
   */
  private NodeList<Node> content = new DefaultNodeList<>(this, 1);
  /**
   * The document type for this document
   */
  private DocumentType docType;
  /**
   * The document factory used by default
   */
  private DocumentFactory documentFactory = DefaultDocumentFactory.getInstance();
  /**
   * The resolver of URIs
   */
  private transient EntityResolver entityResolver;

  public DefaultDocument() {
    this(null, null, null);
  }

  public DefaultDocument(String name) {
    this(name, null, null);
  }

  public DefaultDocument(Element rootElement) {
    this(null, rootElement, null);
  }

  public DefaultDocument(DocumentType docType) {
    this(null, null, docType);
  }

  public DefaultDocument(Element rootElement, DocumentType docType) {
    this(null, rootElement, docType);
  }

  public DefaultDocument(String name, Element rootElement, DocumentType docType) {
    this.name = name;
    this.setRootElement(rootElement);
    this.docType = docType;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Element getRootElement() {
    return rootElement;
  }

  @Override
  public DocumentType getDocType() {
    return docType;
  }

  @Override
  public void setDocType(DocumentType docType) {
    this.docType = docType;
  }

  @Override
  public Document addDocType(String docTypeName, String publicId, String systemId) {
    setDocType(getDocumentFactory().createDocType(docTypeName, publicId, systemId));

    return this;
  }

  @Override
  public String getXMLEncoding() {
    return encoding;
  }

  @Override
  public EntityResolver getEntityResolver() {
    return entityResolver;
  }

  @Override
  public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  @Override
  public DefaultDocument clone() {
    DefaultDocument document = (DefaultDocument)super.clone();
    
    if (document != null) {
      document.rootElement = null;
      document.content = content != null ? content.copy().attach(document) : null;
    }

    return document;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <T extends Node> void appendNewNode(NodeList<T> newContent, T node) {
    Document doc = node.getDocument();

    if (doc != null) {
      node = (T)node.clone();
    }

    newContent.add(node);
  }
  
  @Override
  protected void childAdded(Node node) {
    super.childAdded(node);
    
    if (node instanceof Element) {
      if (rootElement == null) {
        rootElement = (Element)node;
      }
      else {
        throw new IllegalAddException("A document may only contain one root element: " + content);
      }
    }
  }
  
  @Override
  protected void childRemoved(Node node) {
    super.childRemoved(node);
    
    if (rootElement == node) {
      rootElement = null;
    }
  }
  
  @Override
  protected void contentRemoved() {
    super.contentRemoved();
    rootElement = null;
  }
  

  public void setDocumentFactory(DocumentFactory documentFactory) {
    this.documentFactory = documentFactory;
  }

  @Override
  protected NodeList<Node> contentList() {
    return content;
  }

  @Override
  protected NodeList<Node> contentList(int size) {
    return content;
  }

  @Override
  protected NodeList<Node> safeContentList() {
    return content;
  }

  @Override
  protected void clearContentList() {
    safeContentList().clear();
  }

  @Override
  protected void beforeChildAdd(Node node) {
    Document document = node.getDocument();

    if ((document != null) && (document != this)) {
      // XXX: could clone here
      String message = "The Node already has an existing document: " + document;
      throw new IllegalAddException(this, node, message);
    }
  }

  @Override
  protected boolean removeNode(Node node) {
    if (node == rootElement) {
      rootElement = null;
    }

    return super.removeNode(node);
  }

  @Override
  protected void rootElementAdded(Element element) {
    this.rootElement = element;
    element.setDocument(this);
  }

  @Override
  protected DocumentFactory getDocumentFactory() {
    return documentFactory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setContent(List<? extends Node> nodes) {
    if (nodes instanceof NodeList) {
      final NodeList<Node> nodeList = (NodeList<Node>)nodes;

      if (nodeList.getParent() != this) {
        clearContentList();
        
        if (nodeList.getParent() != null) {
          this.content = nodeList.copy();
        }
        else {
          this.content = nodeList.content();
          
          if (this.content == null) {
            this.content = new DefaultNodeList<>(this, 1);
          }
        }
  
        this.content.attach(this);
      }
    }
    else {
      clearContentList();
      setContentList(nodes);
    }
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
