/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.tree;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.function.Predicate;

import org.neoxml.CDATA;
import org.neoxml.Comment;
import org.neoxml.DefaultDocumentFactory;
import org.neoxml.Document;
import org.neoxml.DocumentFactory;
import org.neoxml.Element;
import org.neoxml.Namespace;
import org.neoxml.Node;
import org.neoxml.NodeFilter;
import org.neoxml.NodeType;
import org.neoxml.ProcessingInstruction;
import org.neoxml.QName;
import org.neoxml.Text;
import org.neoxml.XPath;
import org.neoxml.rule.Pattern;

/**
 * <p>
 * <code>AbstractNode</code> is an abstract base class for tree implementors to use for implementation inheritence.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.31 $
 */
public abstract class AbstractNode implements Node, Serializable
{
  /**
   * The <code>DefaultDocumentFactory</code> instance used by default
   */
  private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.getInstance();
  
  public AbstractNode() {}

  @Override
  public NodeType getNodeTypeEnum() {
    return NodeType.UNKNOWN_NODE;
  }

  @Override
  public short getNodeType() {
    return this.getNodeTypeEnum().getCode();
  }

  @Override
  public String getNodeTypeName() {
    return this.getNodeTypeEnum().getName();
  }

  @Override
  public Document getDocument() {
    Element element = getParent();

    return (element != null) ? element.getDocument() : null;
  }

  @Override
  public void setDocument(Document document) {
    // XXX maybe throw UnsupportedOperationException ?
  }

  @Override
  public Element getParent() {
    return null;
  }

  @Override
  public void setParent(Element parent) {
    // XXX maybe throw UnsupportedOperationException ?
  }

  @Override
  public boolean supportsParent() {
    return false;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

  @Override
  public boolean hasContent() {
    return false;
  }

  @Override
  public String getPath() {
    return getPath(null);
  }

  @Override
  public String getUniquePath() {
    return getUniquePath(null);
  }

  @Override
  public AbstractNode clone() {
    if (isReadOnly()) {
      return this;
    }
    else {
      try {
        AbstractNode answer = (AbstractNode)super.clone();
        if (answer.supportsParent()) {
          answer.setParent(null);
          answer.setDocument(null);
        }

        return answer;
      }
      catch (CloneNotSupportedException e) {
        // should never happen
        throw new AssertionError("This should never happen. Caught: ", e);
      }
    }
  }

  @Override
  public Node detach() {
    Element parent = getParent();

    if (parent != null) {
      parent.remove(this);
    }
    else {
      Document document = getDocument();

      if (document != null) {
        document.remove(this);
      }
    }

    setParent(null);
    setDocument(null);

    return this;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public void setName(String name) {
    throw new UnsupportedOperationException("This node cannot be modified");
  }

  @Override
  public String getText() {
    return null;
  }

  @Override
  public String getStringValue() {
    return getText();
  }

  @Override
  public void setText(String text) {
    throw new UnsupportedOperationException("This node cannot be modified");
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(asXML());
  }

  // XPath methods

  @Override
  public Object selectObject(String xpathExpression) {
    XPath xpath = createXPath(xpathExpression);

    return xpath.evaluate(this);
  }

  @Override
  public List<Node> selectNodes(String xpathExpression) {
    XPath xpath = createXPath(xpathExpression);

    return xpath.selectNodes(this);
  }

  @Override
  public List<Node> selectNodes(String xpathExpression, String comparisonXPathExpression) {
    return selectNodes(xpathExpression, comparisonXPathExpression, false);
  }

  @Override
  public List<Node> selectNodes(String xpathExpression, String comparisonXPathExpression, boolean removeDuplicates) {
    XPath xpath = createXPath(xpathExpression);
    XPath sortBy = createXPath(comparisonXPathExpression);

    return xpath.selectNodes(this, sortBy, removeDuplicates);
  }

  @Override
  public Node selectSingleNode(String xpathExpression) {
    XPath xpath = createXPath(xpathExpression);

    return xpath.selectSingleNode(this);
  }

  @Override
  public String valueOf(String xpathExpression) {
    XPath xpath = createXPath(xpathExpression);

    return xpath.valueOf(this);
  }

  @Override
  public Number numberValueOf(String xpathExpression) {
    XPath xpath = createXPath(xpathExpression);

    return xpath.numberValueOf(this);
  }

  @Override
  public boolean matches(String patternText) {
    NodeFilter filter = createXPathFilter(patternText);

    return filter.matches(this);
  }

  @Override
  public XPath createXPath(String xpathExpression) {
    return getDocumentFactory().createXPath(xpathExpression);
  }

  public NodeFilter createXPathFilter(String patternText) {
    return getDocumentFactory().createXPathFilter(patternText);
  }

  public Pattern createPattern(String patternText) {
    return getDocumentFactory().createPattern(patternText);
  }

  @Override
  public Node asXPathResult(Element parent) {
    if (supportsParent()) {
      return this;
    }

    return createXPathResult(parent);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    this.toString(builder);
    return builder.toString();
  }

  protected void toString(StringBuilder builder) {
    builder.append(super.toString());
  }

  protected DocumentFactory getDocumentFactory() {
    return DOCUMENT_FACTORY;
  }
  
  protected Node createXPathResult(Element parent) {
    throw new RuntimeException("asXPathResult() not yet implemented fully for: " + this);
  }

  protected static final Predicate<Node> ELEMENT_CONDITION = new ElementCondition();
  protected static final Predicate<Node> NAMESPACE_CONDITION = new NamespaceCondition();
  protected static final Predicate<Node> TEXT_CONDITION = new TextCondition();
  protected static final Predicate<Node> CDATA_CONDITION = new CDATACondition();
  protected static final Predicate<Node> COMMENT_CONDITION = new CommentCondition();
  protected static final Predicate<Node> PI_CONDITION = new ProcessingInstructionCondition();

  protected static class NodeNameCondition implements Predicate<Node>
  {
    protected final String name;

    protected NodeNameCondition(String name) {
      this.name = name;
    }

    @Override
    public boolean test(Node node) {
      return name == null || name.equals(node.getName());
    }
  }

  protected static class ElementQNameCondition implements Predicate<Node>
  {
    protected final QName qName;

    protected ElementQNameCondition(QName qName) {
      this.qName = qName;
    }

    @Override
    public boolean test(Node node) {
      if (node instanceof Element) {
        return qName == null || qName.equals(((Element)node).getQName());
      }
      return false;
    }
  }

  protected static class ElementCondition extends NodeNameCondition
  {
    protected ElementCondition() {
      super(null);
    }

    protected ElementCondition(String name) {
      super(name);
    }

    @Override
    public boolean test(Node node) {
      return node instanceof Element && super.test(node);
    }
  }

  protected static class NamespacePrefixCondition implements Predicate<Node>
  {
    protected final String prefix;

    protected NamespacePrefixCondition(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public boolean test(Node node) {
      return node instanceof Namespace && (prefix == null || prefix.equals(((Namespace)node).getPrefix()));
    }
  }

  protected static class NamespaceCondition implements Predicate<Node>
  {
    protected final String uri;

    protected NamespaceCondition() {
      this(null);
    }

    protected NamespaceCondition(String uri) {
      this.uri = uri;
    }

    @Override
    public boolean test(Node node) {
      return node instanceof Namespace && (uri == null || uri.equals(((Namespace)node).getURI()));
    }
  }

  protected static class AdditionalNamespaceCondition implements Predicate<Node>
  {
    protected final String defaultNamespaceUri;
    protected final Namespace namespace;

    protected AdditionalNamespaceCondition(Namespace namespace) {
      this.defaultNamespaceUri = null;
      this.namespace = namespace;
    }

    protected AdditionalNamespaceCondition(String defaultNamespaceUri) {
      this.defaultNamespaceUri = defaultNamespaceUri;
      this.namespace = null;
    }

    @Override
    public boolean test(Node node) {
      if (node instanceof Namespace) {
        if (namespace != null) {
          return !namespace.equals(node);
        }
        if (defaultNamespaceUri != null) {
          return !defaultNamespaceUri.equals(((Namespace)node).getURI());
        }
      }

      return false;
    }
  }

  protected static class CommentCondition implements Predicate<Node>
  {
    protected CommentCondition() {}

    @Override
    public boolean test(Node node) {
      return node instanceof Comment;
    }
  }

  protected static class CDATACondition implements Predicate<Node>
  {
    protected CDATACondition() {}

    @Override
    public boolean test(Node node) {
      return node instanceof CDATA;
    }
  }

  protected static class TextCondition implements Predicate<Node>
  {
    protected TextCondition() {}

    @Override
    public boolean test(Node node) {
      return node instanceof Text;
    }
  }

  protected static class ProcessingInstructionCondition extends NodeNameCondition
  {
    protected ProcessingInstructionCondition() {
      super(null);
    }

    protected ProcessingInstructionCondition(String name) {
      super(name);
    }

    @Override
    public boolean test(Node node) {
      return node instanceof ProcessingInstruction && super.test(node);
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
