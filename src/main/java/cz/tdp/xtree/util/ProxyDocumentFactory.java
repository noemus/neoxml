/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package cz.tdp.xtree.util;

import java.util.Map;

import cz.tdp.xtree.Attribute;
import cz.tdp.xtree.CDATA;
import cz.tdp.xtree.Comment;
import cz.tdp.xtree.DefaultDocumentFactory;
import cz.tdp.xtree.Document;
import cz.tdp.xtree.DocumentFactory;
import cz.tdp.xtree.DocumentType;
import cz.tdp.xtree.Element;
import cz.tdp.xtree.Entity;
import cz.tdp.xtree.Namespace;
import cz.tdp.xtree.ProcessingInstruction;
import cz.tdp.xtree.QName;
import cz.tdp.xtree.Text;

/**
 * <p>
 * <code>ProxyDocumentFactory</code> implements a proxy to a DefaultDocumentFactory which is useful for implementation
 * inheritence, allowing the pipelining of various factory implementations. For example an EncodingDocumentFactory which
 * takes care of encoding strings outside of allowable XML ranges could be used with a DatatypeDocumentFactory which is
 * XML Schema Data Type aware.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.13 $
 */
public abstract class ProxyDocumentFactory
{
  private DocumentFactory proxy;

  public ProxyDocumentFactory() {
    // use default factory
    this.proxy = DefaultDocumentFactory.getInstance();
  }

  public ProxyDocumentFactory(DocumentFactory proxy) {
    this.proxy = proxy;
  }

  // Factory methods
  // -------------------------------------------------------------------------

  public Document createDocument() {
    return proxy.createDocument();
  }

  public Document createDocument(Element rootElement) {
    return proxy.createDocument(rootElement);
  }

  public DocumentType createDocType(String name, String publicId,
      String systemId) {
    return proxy.createDocType(name, publicId, systemId);
  }

  public Element createElement(QName qname) {
    return proxy.createElement(qname);
  }

  public Element createElement(String name) {
    return proxy.createElement(name);
  }

  public Attribute createAttribute(Element owner, QName qname, String value) {
    return proxy.createAttribute(owner, qname, value);
  }

  public Attribute createAttribute(Element owner, String name, String value) {
    return proxy.createAttribute(owner, name, value);
  }

  public CDATA createCDATA(String text) {
    return proxy.createCDATA(text);
  }

  public Comment createComment(String text) {
    return proxy.createComment(text);
  }

  public Text createText(String text) {
    return proxy.createText(text);
  }

  public Entity createEntity(String name, String text) {
    return proxy.createEntity(name, text);
  }

  public Namespace createNamespace(String prefix, String uri) {
    return proxy.createNamespace(prefix, uri);
  }

  public ProcessingInstruction createProcessingInstruction(String target,
      String data) {
    return proxy.createProcessingInstruction(target, data);
  }

  public ProcessingInstruction createProcessingInstruction(String target, Map<String,String> data) {
    return proxy.createProcessingInstruction(target, data);
  }

  public QName createQName(String localName, Namespace namespace) {
    return proxy.createQName(localName, namespace);
  }

  public QName createQName(String localName) {
    return proxy.createQName(localName);
  }

  public QName createQName(String name, String prefix, String uri) {
    return proxy.createQName(name, prefix, uri);
  }

  public QName createQName(String qualifiedName, String uri) {
    return proxy.createQName(qualifiedName, uri);
  }

  // Implementation methods
  // -------------------------------------------------------------------------

  protected DocumentFactory getProxy() {
    return proxy;
  }

  protected void setProxy(DocumentFactory proxy) {
    if (proxy == null) {
      // use default factory
      proxy = DefaultDocumentFactory.getInstance();
    }

    this.proxy = proxy;
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
