/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.noemus.neoxml.xpath;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.VariableContext;
import org.jaxen.XPath;
import org.noemus.neoxml.InvalidXPathException;
import org.noemus.neoxml.Node;
import org.noemus.neoxml.XPathException;

/**
 * <p>
 * Default implementation of {@link org.noemus.neoxml.XPath}which uses the <a href="http://jaxen.org">Jaxen </a> project.
 * </p>
 *
 * @author bob mcwhirter
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 */
public class DefaultXPath implements org.noemus.neoxml.XPath, Serializable
{
  private String text;

  private XPath xpath;

  private NamespaceContext namespaceContext;

  /**
   * Construct an XPath
   *
   * @param text DOCUMENT ME!
   * @throws InvalidXPathException DOCUMENT ME!
   */
  public DefaultXPath(String text) throws InvalidXPathException {
    this.text = text;
    this.xpath = parse(text);
  }

  @Override
  public String toString() {
    return "[XPath: " + xpath + "]";
  }

  // XPath interface

  /**
   * Retrieve the textual XPath string used to initialize this Object
   *
   * @return The XPath string
   */
  @Override
  public String getText() {
    return text;
  }

  public FunctionContext getFunctionContext() {
    return xpath.getFunctionContext();
  }

  public void setFunctionContext(FunctionContext functionContext) {
    xpath.setFunctionContext(functionContext);
  }

  public NamespaceContext getNamespaceContext() {
    return namespaceContext;
  }

  @Override
  public void setNamespaceURIs(Map<String,String> map) {
    setNamespaceContext(new SimpleNamespaceContext(map));
  }

  public void setNamespaceContext(NamespaceContext namespaceContext) {
    this.namespaceContext = namespaceContext;
    xpath.setNamespaceContext(namespaceContext);
  }

  public VariableContext getVariableContext() {
    return xpath.getVariableContext();
  }

  public void setVariableContext(VariableContext variableContext) {
    xpath.setVariableContext(variableContext);
  }

  @Override
  public Object evaluate(Object context) {
    try {
      setNSContext(context);

      List<? extends Node> answer = xpath.selectNodes(context);

      if ((answer != null) && (answer.size() == 1)) {
        return answer.get(0);
      }

      return answer;
    }
    catch (JaxenException e) {
      handleJaxenException(e);

      return null;
    }
  }

  @Override
  public List<Node> selectNodes(Object context) {
    try {
      setNSContext(context);

      return xpath.selectNodes(context);
    }
    catch (JaxenException e) {
      handleJaxenException(e);

      return Collections.EMPTY_LIST;
    }
  }

  @Override
  public List<Node> selectNodes(Object context, org.noemus.neoxml.XPath sortXPath) {
    List<Node> answer = selectNodes(context);
    sortXPath.sort(answer);

    return answer;
  }

  @Override
  public List<Node> selectNodes(Object context, org.noemus.neoxml.XPath sortXPath, boolean distinct) {
    List<Node> answer = selectNodes(context);
    sortXPath.sort(answer, distinct);

    return answer;
  }

  @Override
  public Node selectSingleNode(Object context) {
    try {
      setNSContext(context);

      Object answer = xpath.selectSingleNode(context);

      if (answer instanceof Node) {
        return (Node)answer;
      }

      if (answer == null) {
        return null;
      }

      throw new XPathException("The result of the XPath expression is not a Node. It was: " + answer + " of type: " + answer.getClass().getName());
    }
    catch (JaxenException e) {
      handleJaxenException(e);

      return null;
    }
  }

  @Override
  public String valueOf(Object context) {
    try {
      setNSContext(context);

      return xpath.stringValueOf(context);
    }
    catch (JaxenException e) {
      handleJaxenException(e);

      return "";
    }
  }

  @Override
  public Number numberValueOf(Object context) {
    try {
      setNSContext(context);

      return xpath.numberValueOf(context);
    }
    catch (JaxenException e) {
      handleJaxenException(e);

      return null;
    }
  }

  @Override
  public boolean booleanValueOf(Object context) {
    try {
      setNSContext(context);

      return xpath.booleanValueOf(context);
    }
    catch (JaxenException e) {
      handleJaxenException(e);

      return false;
    }
  }

  /**
   * <p>
   * <code>sort</code> sorts the given List of Nodes using this XPath expression as a {@link Comparator}.
   * </p>
   *
   * @param list is the list of Nodes to sort
   */
  @Override
  public void sort(List<? extends Node> list) {
    sort(list, false);
  }

  /**
   * <p>
   * <code>sort</code> sorts the given List of Nodes using this XPath expression as a {@link Comparator}and optionally
   * removing duplicates.
   * </p>
   *
   * @param list is the list of Nodes to sort
   * @param distinct if true then duplicate values (using the sortXPath for
   *          comparisions) will be removed from the List
   */
  @Override
  public void sort(List<? extends Node> list, boolean distinct) {
    if ((list != null) && !list.isEmpty()) {
      int size = list.size();
      HashMap<Node,String> sortValues = new HashMap<>(size);

      for (int i = 0; i < size; i++) {
        Object object = list.get(i);

        if (object instanceof Node) {
          Node node = (Node)object;
          String expression = getCompareValue(node);
          sortValues.put(node, expression);
        }
      }

      sort(list, sortValues);

      if (distinct) {
        removeDuplicates(list, sortValues);
      }
    }
  }

  @Override
  public boolean matches(Node node) {
    try {
      setNSContext(node);

      List<? extends Node> answer = xpath.selectNodes(node);

      if ((answer != null) && !answer.isEmpty()) {
        Object item = answer.get(0);

        if (item instanceof Boolean) {
          return ((Boolean)item).booleanValue();
        }

        return answer.contains(node);
      }

      return false;
    }
    catch (JaxenException e) {
      handleJaxenException(e);

      return false;
    }
  }

  /**
   * Sorts the list based on the sortValues for each node
   *
   * @param list DOCUMENT ME!
   * @param sortValues DOCUMENT ME!
   */
  protected void sort(List<? extends Node> list, final Map<Node,String> sortValues) {
    Collections.sort(list, new Comparator<Node>() {
      @Override
      public int compare(Node o1, Node o2) {
        String k1 = sortValues.get(o1);
        String k2 = sortValues.get(o2);

        if (k1 == k2) {
          // also handles k1 == null && k2 == null
          return 0;
        }
        else if (k1 != null && k2 != null) {
          return k1.compareTo(k2);
        }
        else if (k1 == null) {
          // k1 == null && k2 != null
          return 1;
        }
        else {
          // k2 == null && k1 != null
          return -1;
        }
      }
    });
  }

  // Implementation methods

  /**
   * Removes items from the list which have duplicate values
   *
   * @param list DOCUMENT ME!
   * @param sortValues DOCUMENT ME!
   */
  protected void removeDuplicates(List<? extends Node> list, Map<Node,String> sortValues) {
    // remove distinct
    Set<String> distinctValues = new HashSet<>();

    for (Iterator<? extends Node> iter = list.iterator(); iter.hasNext();) {
      Node node = iter.next();
      String value = sortValues.get(node);

      if (distinctValues.contains(value)) {
        iter.remove();
      }
      else {
        distinctValues.add(value);
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param node DOCUMENT ME!
   * @return the node expression used for sorting comparisons
   */
  protected String getCompareValue(Node node) {
    return valueOf(node);
  }

  protected static XPath parse(String text) {
    try {
      return new XtreeXPath(text);
    }
    catch (JaxenException e) {
      throw new InvalidXPathException(text, e.getMessage());
    }
    catch (RuntimeException e) {}

    throw new InvalidXPathException(text);
  }

  protected void setNSContext(Object context) {
    if (namespaceContext == null) {
      xpath.setNamespaceContext(DefaultNamespaceContext.create(context));
    }
  }

  protected void handleJaxenException(JaxenException exception) throws XPathException {
    throw new XPathException(text, exception);
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
