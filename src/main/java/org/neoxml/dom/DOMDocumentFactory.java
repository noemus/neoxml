/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.dom;

import org.neoxml.*;
import org.neoxml.util.SingletonHelper;
import org.neoxml.util.SingletonStrategy;

import java.util.Map;

/**
 * <p>
 * <code>DOMDocumentFactory</code> is a factory of neoxml objects which implement the W3C DOM API.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.21 $
 */
public class DOMDocumentFactory extends DefaultDocumentFactory implements org.w3c.dom.DOMImplementation {

    /**
     * The Singleton instance
     */
    private static final SingletonStrategy<DOMDocumentFactory> singleton = SingletonHelper.getSingletonStrategy("org.neoxml.dom.DOMDocumentFactory.singleton.strategy", DOMDocumentFactory.class);

    /**
     * <p>
     * Access to the singleton instance of this factory.
     * </p>
     *
     * @return the default singleon instance
     */
    @SuppressWarnings("sync-override")
    public static DOMDocumentFactory getInstance() {
        return singleton.instance();
    }

    @Override
    public Document createDocument() {
        DOMDocument answer = new DOMDocument();
        answer.setDocumentFactory(this);

        return answer;
    }

    @Override
    public DocumentType createDocType(String name, String publicId, String systemId) {
        return new DOMDocumentType(name, publicId, systemId);
    }

    @Override
    public Element createElement(QName qname) {
        return new DOMElement(qname);
    }

    public Element createElement(QName qname, int attributeCount) {
        return new DOMElement(qname, attributeCount);
    }

    @Override
    public Attribute createAttribute(Element owner, QName qname, String value) {
        return new DOMAttribute(qname, value);
    }

    @Override
    public CDATA createCDATA(String text) {
        return new DOMCDATA(text);
    }

    @Override
    public Comment createComment(String text) {
        return new DOMComment(text);
    }

    @Override
    public Text createText(String text) {
        return new DOMText(text);
    }

    public Entity createEntity(String name) {
        return new DOMEntityReference(name);
    }

    @Override
    public Entity createEntity(String name, String text) {
        return new DOMEntityReference(name, text);
    }

    @Override
    public Namespace createNamespace(String prefix, String uri) {
        return new DOMNamespace(prefix, uri);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return new DOMProcessingInstruction(target, data);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, Map<String, String> data) {
        return new DOMProcessingInstruction(target, data);
    }

    // org.w3c.dom.DOMImplementation interface

    @Override
    public boolean hasFeature(String feature, String version) {
        if ("XML".equalsIgnoreCase(feature) || "Core".equalsIgnoreCase(feature)) {
            return ((version == null) || (version.length() == 0) || "1.0".equals(version) || "2.0".equals(version));
        }

        return false;
    }

    @Override
    public org.w3c.dom.DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) {
        return new DOMDocumentType(qualifiedName, publicId, systemId);
    }

    @Override
    public org.w3c.dom.Document createDocument(String namespaceURI, String qualifiedName, org.w3c.dom.DocumentType docType) {
        DOMDocument document;

        if (docType != null) {
            DOMDocumentType documentType = asDocumentType(docType);
            document = new DOMDocument(documentType);
        } else {
            document = new DOMDocument();
        }

        document.addElement(createQName(qualifiedName, namespaceURI));

        return document;
    }

    // Implementation methods

    protected DOMDocumentType asDocumentType(org.w3c.dom.DocumentType docType) {
        if (docType instanceof DOMDocumentType) {
            return (DOMDocumentType) docType;
        } else {
            return new DOMDocumentType(docType.getName(), docType.getPublicId(), docType.getSystemId());
        }
    }

    @Override
    public Object getFeature(String feature, String version) {
        throw new UnsupportedFeatureException();
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
