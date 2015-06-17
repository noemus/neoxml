/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package cz.tdp.xtree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.tdp.xtree.rule.Pattern;
import cz.tdp.xtree.tree.AbstractDocument;
import cz.tdp.xtree.tree.DefaultAttribute;
import cz.tdp.xtree.tree.DefaultCDATA;
import cz.tdp.xtree.tree.DefaultComment;
import cz.tdp.xtree.tree.DefaultDocument;
import cz.tdp.xtree.tree.DefaultDocumentType;
import cz.tdp.xtree.tree.DefaultElement;
import cz.tdp.xtree.tree.DefaultEntity;
import cz.tdp.xtree.tree.DefaultProcessingInstruction;
import cz.tdp.xtree.tree.DefaultText;
import cz.tdp.xtree.tree.QNameCache;
import cz.tdp.xtree.util.SingletonHelper;
import cz.tdp.xtree.util.SingletonStrategy;
import cz.tdp.xtree.xpath.XPathPattern;

/**
 * <p>
 * <code>DefaultDocumentFactory</code> is a collection of factory methods to allow easy custom building of DOM4J trees.
 * The default tree that is built uses a doubly linked tree.
 * </p>
 * <p/>
 * <p>
 * The tree built allows full XPath expressions from anywhere on the tree.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 */
@SuppressWarnings("serial")
public class DefaultDocumentFactory implements DocumentFactory, Serializable
{
  private static SingletonStrategy<DocumentFactory> singleton = null;

  protected transient QNameCache cache;

  /**
   * Default namespace prefix -> URI mappings for XPath expressions to use
   */
  private Map<String,String> xpathNamespaceURIs;

  @SuppressWarnings("unchecked")
  private static SingletonStrategy<DocumentFactory> createSingleton() {
    return SingletonHelper.getSingletonStrategy("org.dom4j.DefaultDocumentFactory.singleton.strategy", "org.dom4j.factory", DefaultDocumentFactory.class);
  }

  public DefaultDocumentFactory() {
    init();
  }

  /**
   * <p>
   * Access to singleton implementation of DefaultDocumentFactory which is used if no DefaultDocumentFactory is
   * specified when building using the standard builders.
   * </p>
   *
   * @return the default singleon instance
   */
  public static synchronized DocumentFactory getInstance() {
    if (singleton == null) {
      singleton = createSingleton();
    }
    return singleton.instance();
  }

  // Factory methods

  @Override
  public Document createDocument() {
    DefaultDocument answer = new DefaultDocument();
    answer.setDocumentFactory(this);

    return answer;
  }

  /**
   * DOCUMENT ME!
   *
   * @param encoding DOCUMENT ME!
   * @return DOCUMENT ME!
   * @since 1.5
   */
  @Override
  public Document createDocument(String encoding) {
    // to keep the DefaultDocumentFactory backwards compatible, we have to do this
    // in this not so nice way, since subclasses only need to extend the
    // createDocument() method.
    Document answer = createDocument();

    if (answer instanceof AbstractDocument) {
      ((AbstractDocument)answer).setXMLEncoding(encoding);
    }

    return answer;
  }

  @Override
  public Document createDocument(Element rootElement) {
    Document answer = createDocument();
    answer.setRootElement(rootElement);

    return answer;
  }

  @Override
  public DocumentType createDocType(String name, String publicId, String systemId) {
    return new DefaultDocumentType(name, publicId, systemId);
  }

  @Override
  public Element createElement(QName qname) {
    return new DefaultElement(qname);
  }

  @Override
  public Element createElement(String name) {
    return createElement(createQName(name));
  }

  @Override
  public Element createElement(String qualifiedName, String namespaceURI) {
    return createElement(createQName(qualifiedName, namespaceURI));
  }

  @Override
  public Attribute createAttribute(Element owner, QName qname, String value) {
    return new DefaultAttribute(qname, value);
  }

  @Override
  public Attribute createAttribute(Element owner, String name, String value) {
    return createAttribute(owner, createQName(name), value);
  }

  @Override
  public CDATA createCDATA(String text) {
    return new DefaultCDATA(text);
  }

  @Override
  public Comment createComment(String text) {
    return new DefaultComment(text);
  }

  @Override
  public Text createText(String text) {
    if (text == null) {
      throw new IllegalArgumentException("Adding text to an XML document must not be null");
    }

    return new DefaultText(text);
  }

  @Override
  public Entity createEntity(String name, String text) {
    return new DefaultEntity(name, text);
  }

  @Override
  public Namespace createNamespace(String prefix, String uri) {
    return Namespace.get(prefix, uri);
  }

  @Override
  public ProcessingInstruction createProcessingInstruction(String target, String data) {
    return new DefaultProcessingInstruction(target, data);
  }

  @Override
  public ProcessingInstruction createProcessingInstruction(String target, Map<String,String> data) {
    return new DefaultProcessingInstruction(target, data);
  }

  @Override
  public QName createQName(String localName, Namespace namespace) {
    return cache.get(localName, namespace);
  }

  @Override
  public QName createQName(String localName) {
    return cache.get(localName);
  }

  @Override
  public QName createQName(String name, String prefix, String uri) {
    return cache.get(name, Namespace.get(prefix, uri));
  }

  @Override
  public QName createQName(String qualifiedName, String uri) {
    return cache.get(qualifiedName, uri);
  }

  /**
   * <p>
   * <code>createXPath</code> parses an XPath expression and creates a new XPath <code>XPath</code> instance.
   * </p>
   *
   * @param xpathExpression is the XPath expression to create
   * @return a new <code>XPath</code> instance
   * @throws InvalidXPathException if the XPath expression is invalid
   */
  @Override
  public XPath createXPath(String xpathExpression) throws InvalidXPathException {
    XPath xpath = createXPathInstance(xpathExpression);
    
    if (xpathNamespaceURIs != null) {
      xpath.setNamespaceURIs(xpathNamespaceURIs);
    }

    return xpath;
  }

  private XPath createXPathInstance(String xpathExpression) throws XPathNotSupportedException {
    try {
      final Class<?> clazz = Class.forName("org.dom4j.xpath.DefaultXPath");
      final Constructor<?> ctr = clazz.getConstructor(String.class);
      return (XPath)ctr.newInstance(xpathExpression);
    }
    catch (Exception e) {
      throw new XPathNotSupportedException(e);
    }
  }

  /**
   * <p>
   * <code>createXPathFilter</code> parses a NodeFilter from the given XPath filter expression. XPath filter expressions
   * occur within XPath expressions such as <code>self::node()[ filterExpression ]</code>
   * </p>
   *
   * @param xpathFilterExpression is the XPath filter expression to create
   * @return a new <code>NodeFilter</code> instance
   */
  @Override
  public NodeFilter createXPathFilter(String xpathFilterExpression) {
    return createXPath(xpathFilterExpression);

    // return new DefaultXPath( xpathFilterExpression );
  }

  /**
   * <p>
   * <code>createPattern</code> parses the given XPath expression to create an XSLT style {@link Pattern}instance which
   * can then be used in an XSLT processing model.
   * </p>
   *
   * @param xpathPattern is the XPath pattern expression to create
   * @return a new <code>Pattern</code> instance
   */
  @Override
  public Pattern createPattern(String xpathPattern) {
    return new XPathPattern(xpathPattern);
  }

  // Properties
  // -------------------------------------------------------------------------

  /**
   * Returns a list of all the QName instances currently used by this document
   * factory
   *
   * @return DOCUMENT ME!
   */
  @Override
  public List<QName> getQNames() {
    return cache.getQNames();
  }

  /**
   * DOCUMENT ME!
   *
   * @return the Map of namespace URIs that will be used by by XPath
   *         expressions to resolve namespace prefixes into namespace URIs.
   *         The map is keyed by namespace prefix and the value is the
   *         namespace URI. This value could well be null to indicate no
   *         namespace URIs are being mapped.
   */
  @Override
  public Map<String,String> getXPathNamespaceURIs() {
    return xpathNamespaceURIs;
  }

  /**
   * Sets the namespace URIs to be used by XPath expressions created by this
   * factory or by nodes associated with this factory. The keys are namespace
   * prefixes and the values are namespace URIs.
   *
   * @param namespaceURIs DOCUMENT ME!
   */
  @Override
  public void setXPathNamespaceURIs(Map<String,String> namespaceURIs) {
    this.xpathNamespaceURIs = namespaceURIs;
  }

  // Implementation methods
  // -------------------------------------------------------------------------

  /**
   * <p>
   * <code>createSingleton</code> creates the singleton instance from the given class name.
   * </p>
   *
   * @param className is the name of the DefaultDocumentFactory class to use
   * @return a new singleton instance.
   */
  protected static DocumentFactory createSingleton(String className) {
    // let's try and class load an implementation?
    try {
      // I'll use the current class loader
      // that loaded me to avoid problems in J2EE and web apps
      Class<?> theClass = Class.forName(className, true, DefaultDocumentFactory.class.getClassLoader());

      return (DocumentFactory)theClass.newInstance();
    }
    catch (Throwable e) {
      log.warn("Cannot load DefaultDocumentFactory: " + className);

      return new DefaultDocumentFactory();
    }
  }

  /**
   * Factory method to create the QNameCache. This method should be overloaded
   * if you wish to use your own derivation of QName.
   *
   * @return DOCUMENT ME!
   */
  protected QNameCache createQNameCache() {
    return new QNameCache(this);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    init();
  }

  protected void init() {
    cache = createQNameCache();
  }
  
  private static final Log log = LogFactory.getLog(DefaultDocumentFactory.class);
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
