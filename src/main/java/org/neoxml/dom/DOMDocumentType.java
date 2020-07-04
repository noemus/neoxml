/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.neoxml.dom;

import org.neoxml.tree.DefaultDocumentType;
import org.w3c.dom.*;

/**
 * <p>
 * <code>DOMDocumentType</code> implements a DocumentType node which supports the W3C DOM API.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.11 $
 */
public class DOMDocumentType extends DefaultDocumentType implements
org.w3c.dom.DocumentType
{

  public DOMDocumentType() {}

  public DOMDocumentType(String elementName, String systemID) {
    super(elementName, systemID);
  }

  public DOMDocumentType(String name, String publicID, String systemID) {
    super(name, publicID, systemID);
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
    return DOMNodeHelper.getChildNodes(this);
  }

  @Override
  public org.w3c.dom.Node getFirstChild() {
    return DOMNodeHelper.getFirstChild(this);
  }

  @Override
  public org.w3c.dom.Node getLastChild() {
    return DOMNodeHelper.getLastChild(this);
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
    throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
        "DocumentType nodes cannot have children");
  }

  @Override
  public boolean hasChildNodes() {
    return DOMNodeHelper.hasChildNodes(this);
  }

  @Override
  public org.w3c.dom.Node cloneNode(boolean deep) {
    return DOMNodeHelper.cloneNode(this, deep);
  }

  @Override
  public void normalize() {
    DOMNodeHelper.normalize(this);
  }

  @Override
  public boolean isSupported(String feature, String version) {
    return DOMNodeHelper.isSupported(this, feature, version);
  }

  @Override
  public boolean hasAttributes() {
    return DOMNodeHelper.hasAttributes(this);
  }

  // org.w3c.dom.DocumentType interface
  // -------------------------------------------------------------------------

  @Override
  public NamedNodeMap getEntities() {
    return null;
  }

  @Override
  public NamedNodeMap getNotations() {
    return null;
  }

  @Override
  public String getPublicId() {
    return getPublicID();
  }

  @Override
  public String getSystemId() {
    return getSystemID();
  }

  @Override
  public String getInternalSubset() {
    return getElementName();
  }

  @Override
  public String getBaseURI() {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public short compareDocumentPosition(Node other) throws DOMException {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getTextContent() throws DOMException {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTextContent(String textContent) throws DOMException {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isSameNode(Node other) {
    return DOMNodeHelper.isNodeSame(this, other);
  }

  @Override
  public String lookupPrefix(String namespaceURI) {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isDefaultNamespace(String namespaceURI) {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String lookupNamespaceURI(String prefix) {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isEqualNode(Node other) {
    return DOMNodeHelper.isNodeEquals(this, other);
  }

  @Override
  public Object getFeature(String feature, String version) {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object setUserData(String key, Object data, UserDataHandler handler) {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getUserData(String key) {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public short getNodeType() {
    //TODO
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
