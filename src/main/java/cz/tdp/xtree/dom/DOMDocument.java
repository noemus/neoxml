/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package cz.tdp.xtree.dom;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.UserDataHandler;

import cz.tdp.xtree.DocumentFactory;
import cz.tdp.xtree.Element;
import cz.tdp.xtree.QName;
import cz.tdp.xtree.tree.DefaultDocument;

/**
 * <p>
 * <code>DOMDocument</code> implements an XML document which supports the W3C DOM API.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.17 $
 */
public class DOMDocument extends DefaultDocument implements Document
{
  /**
   * The <code>DefaultDocumentFactory</code> instance used by default
   */
  private static final DOMDocumentFactory DOCUMENT_FACTORY = (DOMDocumentFactory)DOMDocumentFactory.getInstance();

  public DOMDocument() {
    init();
  }

  public DOMDocument(String name) {
    super(name);
    init();
  }

  public DOMDocument(DOMElement rootElement) {
    super(rootElement);
    init();
  }

  public DOMDocument(DOMDocumentType docType) {
    super(docType);
    init();
  }

  public DOMDocument(DOMElement rootElement, DOMDocumentType docType) {
    super(rootElement, docType);
    init();
  }

  public DOMDocument(String name, DOMElement rootElement, DOMDocumentType docType) {
    super(name, rootElement, docType);
    init();
  }

  private void init() {
    setDocumentFactory(DOCUMENT_FACTORY);
  }

  // org.w3c.dom.Node interface
  // -------------------------------------------------------------------------

  public boolean supports(String feature, String version) {
    return DOMNodeHelper.supports(this, feature, version);
  }

  @Override
  public String getNamespaceURI() {
    return DOMNodeHelper.getNamespaceURI(this);
  }

  @Override
  public String getPrefix() {
    return DOMNodeHelper.getPrefix(this);
  }

  @Override
  public void setPrefix(String prefix) throws DOMException {
    DOMNodeHelper.setPrefix(this, prefix);
  }

  @Override
  public String getLocalName() {
    return DOMNodeHelper.getLocalName(this);
  }

  @Override
  public String getNodeName() {
    return "#document";
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
    return null;
  }

  @Override
  public org.w3c.dom.Document getOwnerDocument() {
    return null;
  }

  @Override
  public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
    checkNewChildNode(newChild);

    return DOMNodeHelper.insertBefore(this, newChild, refChild);
  }

  @Override
  public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
    checkNewChildNode(newChild);

    return DOMNodeHelper.replaceChild(this, newChild, oldChild);
  }

  @Override
  public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
    return DOMNodeHelper.removeChild(this, oldChild);
  }

  @Override
  public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
    checkNewChildNode(newChild);

    return DOMNodeHelper.appendChild(this, newChild);
  }

  private void checkNewChildNode(org.w3c.dom.Node newChild) throws DOMException {
    final int nodeType = newChild.getNodeType();

    if (!((nodeType == org.w3c.dom.Node.ELEMENT_NODE)
        || (nodeType == org.w3c.dom.Node.COMMENT_NODE)
        || (nodeType == org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE)
        || (nodeType == org.w3c.dom.Node.DOCUMENT_TYPE_NODE))) {
      throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
          "Given node cannot be a child of document");
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

  // org.w3c.dom.Document interface
  // -------------------------------------------------------------------------

  @Override
  public NodeList getElementsByTagName(String name) {
    List<Element> list = new ArrayList<>();
    DOMNodeHelper.appendElementsByTagName(list, this, name);

    return DOMNodeHelper.createNodeList(list);
  }

  @Override
  public NodeList getElementsByTagNameNS(String namespace, String name) {
    List<Element> list = new ArrayList<>();
    DOMNodeHelper.appendElementsByTagNameNS(list, this, namespace, name);

    return DOMNodeHelper.createNodeList(list);
  }

  @Override
  public org.w3c.dom.DocumentType getDoctype() {
    return DOMNodeHelper.asDOMDocumentType(getDocType());
  }

  @Override
  public org.w3c.dom.DOMImplementation getImplementation() {
    if (getDocumentFactory() instanceof org.w3c.dom.DOMImplementation) {
      return (org.w3c.dom.DOMImplementation)getDocumentFactory();
    }
    else {
      return DOCUMENT_FACTORY;
    }
  }

  @Override
  public org.w3c.dom.Element getDocumentElement() {
    return DOMNodeHelper.asDOMElement(getRootElement());
  }

  @Override
  public org.w3c.dom.Element createElement(String name) throws DOMException {
    return (org.w3c.dom.Element)getDocumentFactory().createElement(name);
  }

  @Override
  public org.w3c.dom.DocumentFragment createDocumentFragment() {
    DOMNodeHelper.notSupported();

    return null;
  }

  @Override
  public org.w3c.dom.Text createTextNode(String data) {
    return (org.w3c.dom.Text)getDocumentFactory().createText(data);
  }

  @Override
  public org.w3c.dom.Comment createComment(String data) {
    return (org.w3c.dom.Comment)getDocumentFactory().createComment(data);
  }

  @Override
  public CDATASection createCDATASection(String data) throws DOMException {
    return (CDATASection)getDocumentFactory().createCDATA(data);
  }

  @Override
  public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
    return (ProcessingInstruction)getDocumentFactory().createProcessingInstruction(target, data);
  }

  @Override
  public Attr createAttribute(String name) throws DOMException {
    QName qname = getDocumentFactory().createQName(name);

    return (Attr)getDocumentFactory().createAttribute(null, qname, "");
  }

  @Override
  public EntityReference createEntityReference(String name) throws DOMException {
    return (EntityReference)getDocumentFactory().createEntity(name, null);
  }

  @Override
  public org.w3c.dom.Node importNode(org.w3c.dom.Node importedNode, boolean deep) throws DOMException {
    DOMNodeHelper.notSupported();

    return null;
  }

  @Override
  public org.w3c.dom.Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
    QName qname = getDocumentFactory().createQName(qualifiedName, namespaceURI);

    return (org.w3c.dom.Element)getDocumentFactory().createElement(qname);
  }

  @Override
  public org.w3c.dom.Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
    QName qname = getDocumentFactory().createQName(qualifiedName, namespaceURI);

    return (org.w3c.dom.Attr)getDocumentFactory().createAttribute(null, qname, null);
  }

  @Override
  public org.w3c.dom.Element getElementById(String elementId) {
    return DOMNodeHelper.asDOMElement(elementByID(elementId));
  }

  // Implementation methods
  // -------------------------------------------------------------------------

  @Override
  protected DocumentFactory getDocumentFactory() {
    if (super.getDocumentFactory() == null) {
      return DOCUMENT_FACTORY;
    }
    else {
      return super.getDocumentFactory();
    }
  }

  @Override
  public String getInputEncoding() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getXmlEncoding() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean getXmlStandalone() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getXmlVersion() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setXmlVersion(String xmlVersion) throws DOMException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean getStrictErrorChecking() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setStrictErrorChecking(boolean strictErrorChecking) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getDocumentURI() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDocumentURI(String documentURI) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Node adoptNode(Node source) throws DOMException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public DOMConfiguration getDomConfig() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void normalizeDocument() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
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
