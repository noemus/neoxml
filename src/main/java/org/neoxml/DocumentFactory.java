package org.neoxml;

import org.neoxml.rule.Pattern;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: filip
 * Date: 5.4.2010
 * Time: 17:05:44
 * To change this template use File | Settings | File Templates.
 */
public interface DocumentFactory
{
  Document createDocument();

  Document createDocument(String encoding);

  Document createDocument(Element rootElement);

  DocumentType createDocType(String name, String publicId, String systemId);

  Element createElement(QName qname);

  Element createElement(String name);

  Element createElement(String qualifiedName, String namespaceURI);

  Attribute createAttribute(Element owner, QName qname, String value);

  Attribute createAttribute(Element owner, String name, String value);

  CDATA createCDATA(String text);

  Comment createComment(String text);

  Text createText(String text);

  Entity createEntity(String name, String text);

  Namespace createNamespace(String prefix, String uri);

  ProcessingInstruction createProcessingInstruction(String target, String data);

  ProcessingInstruction createProcessingInstruction(String target, Map<String,String> data);

  QName createQName(String localName, Namespace namespace);

  QName createQName(String localName);

  QName createQName(String name, String prefix, String uri);

  QName createQName(String qualifiedName, String uri);

  XPath createXPath(String xpathExpression) throws InvalidXPathException;

  NodeFilter createXPathFilter(String xpathFilterExpression);

  Pattern createPattern(String xpathPattern);

  List<QName> getQNames();

  Map<String,String> getXPathNamespaceURIs();

  void setXPathNamespaceURIs(Map<String,String> namespaceURIs);
}
