/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.neoxml.dom;

import org.neoxml.CDATA;
import org.neoxml.Element;
import org.neoxml.tree.DefaultCDATA;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

/**
 * <p>
 * <code>DOMCDATA</code> implements a CDATA Section which supports the W3C DOM API.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.12 $
 */
public class DOMCDATA extends DefaultCDATA implements org.w3c.dom.CDATASection {

    public DOMCDATA(String text) {
        super(text);
    }

    public DOMCDATA(Element parent, String text) {
        super(parent, text);
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
        return "#cdata-section";
    }

    @Override
    public String getNodeValue() throws DOMException {
        return DOMNodeHelper.getNodeValue(this);
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        DOMNodeHelper.setNodeValue(this, nodeValue);
    }

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
                               "CDATASection nodes cannot have children");
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

    // org.w3c.dom.CharacterData interface
    // -------------------------------------------------------------------------

    @Override
    public String getData() throws DOMException {
        return DOMNodeHelper.getData(this);
    }

    @Override
    public void setData(String data) throws DOMException {
        DOMNodeHelper.setData(this, data);
    }

    @Override
    public int getLength() {
        return DOMNodeHelper.getLength(this);
    }

    @Override
    public String substringData(int offset, int count) throws DOMException {
        return DOMNodeHelper.substringData(this, offset, count);
    }

    @Override
    public void appendData(String arg) throws DOMException {
        DOMNodeHelper.appendData(this, arg);
    }

    @Override
    public void insertData(int offset, String arg) throws DOMException {
        DOMNodeHelper.insertData(this, offset, arg);
    }

    @Override
    public void deleteData(int offset, int count) throws DOMException {
        DOMNodeHelper.deleteData(this, offset, count);
    }

    @Override
    public void replaceData(int offset, int count, String arg)
            throws DOMException {
        DOMNodeHelper.replaceData(this, offset, count, arg);
    }

    // org.w3c.dom.Text interface
    // -------------------------------------------------------------------------

    @SuppressWarnings("hiding")
    @Override
    public org.w3c.dom.Text splitText(int offset) throws DOMException {
        if (isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "CharacterData node is read only: " + this);
        } else {
            String text = getText();
            int length = (text != null) ? text.length() : 0;

            if ((offset < 0) || (offset >= length)) {
                throw new DOMException(DOMException.INDEX_SIZE_ERR, "No text at offset: " + offset);
            } else {
                @SuppressWarnings("null")
                // nemuze byt null
                String start = text.substring(0, offset);
                String rest = text.substring(offset);
                setText(start);

                Element parent = getParent();
                CDATA newText = createCDATA(rest);

                if (parent != null) {
                    parent.add(newText);
                }

                return DOMNodeHelper.asDOMText(newText);
            }
        }
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    @SuppressWarnings("hiding")
    protected CDATA createCDATA(String text) {
        return new DOMCDATA(text);
    }

    @Override
    public boolean isElementContentWhitespace() {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWholeText() {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Text replaceWholeText(String content) throws DOMException {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
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
