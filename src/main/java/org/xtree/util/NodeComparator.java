/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.xtree.util;

import java.util.Comparator;

import org.xtree.Attribute;
import org.xtree.Branch;
import org.xtree.CDATA;
import org.xtree.CharacterData;
import org.xtree.Comment;
import org.xtree.Document;
import org.xtree.DocumentType;
import org.xtree.Element;
import org.xtree.Entity;
import org.xtree.Namespace;
import org.xtree.Node;
import org.xtree.NodeType;
import org.xtree.ProcessingInstruction;
import org.xtree.QName;
import org.xtree.Text;

/**
 * <p>
 * <code>NodeComparator</code> is a {@link Comparator}of Node instances which is capable of comparing Nodes for equality
 * based on their values.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.10 $
 */
public class NodeComparator implements Comparator<Node>
{
  public static final NodeComparator INSTANCE = new NodeComparator();

  @Override
  public int compare(Node n1, Node n2) {
    return compareNodes(n1, n2);
  }

  private static int compareNodes(Node n1, Node n2) {
    if (n1 == null && n2 == null) return 0;
    else if (n1 == null) return -1; // null is less
    else if (n2 == null) return 1;

    NodeType nodeType1 = n1.getNodeTypeEnum();
    NodeType nodeType2 = n2.getNodeTypeEnum();

    if (nodeType1 != nodeType2) {
      return nodeType1.getCode() - nodeType2.getCode();
    }

    switch (nodeType1) {
      case ELEMENT_NODE:
        return compare((Element)n1, (Element)n2);

      case DOCUMENT_NODE:
        return compare((Document)n1, (Document)n2);

      case ATTRIBUTE_NODE:
        return compare((Attribute)n1, (Attribute)n2);

      case TEXT_NODE:
        return compare((Text)n1, (Text)n2);

      case CDATA_SECTION_NODE:
        return compare((CDATA)n1, (CDATA)n2);

      case ENTITY_REFERENCE_NODE:
        return compare((Entity)n1, (Entity)n2);

      case PROCESSING_INSTRUCTION_NODE:
        return compare((ProcessingInstruction)n1,
          (ProcessingInstruction)n2);

      case COMMENT_NODE:
        return compare((Comment)n1, (Comment)n2);

      case DOCUMENT_TYPE_NODE:
        return compare((DocumentType)n1, (DocumentType)n2);

      case NAMESPACE_NODE:
        return compare((Namespace)n1, (Namespace)n2);

      default:
        throw new RuntimeException("Invalid node types. node1: " + n1 + " and node2: " + n2);
    }
  }

  public static int compare(Document n1, Document n2) {
    if (n1 == null && n2 == null) return 0;
    else if (n1 == null) return -1; // null is less
    else if (n2 == null) return 1;

    int answer = compare(n1.getDocType(), n2.getDocType());

    if (answer == 0) {
      answer = compareContent(n1, n2);
    }

    return answer;
  }

  public static int compare(Element n1, Element n2) {
    int answer = compare(n1.getQName(), n2.getQName());

    if (answer == 0) {
      // lets compare attributes
      int c1 = n1.attributeCount();
      int c2 = n2.attributeCount();
      answer = c1 - c2;

      if (answer == 0) {
        for (int i = 0; i < c1; i++) {
          Attribute a1 = n1.attribute(i);
          Attribute a2 = n2.attribute(a1.getQName());

          // we need to check whether the second attribute is null
          answer = a2 != null ? compare(a1, a2) : 1 /* null is less */;

          if (answer != 0) {
            return answer;
          }
        }

        answer = compareContent(n1, n2);
      }
    }

    return answer;
  }

  public static int compare(Attribute n1, Attribute n2) {
    int answer = compare(n1.getQName(), n2.getQName());

    if (answer == 0) {
      answer = compare(n1.getValue(), n2.getValue());
    }

    return answer;
  }

  public static int compare(QName n1, QName n2) {
    int answer = compare(n1.getNamespaceURI(), n2.getNamespaceURI());

    if (answer == 0) {
      answer = compare(n1.getQualifiedName(), n2.getQualifiedName());
    }

    return answer;
  }

  public static int compare(Namespace n1, Namespace n2) {
    int answer = compare(n1.getURI(), n2.getURI());

    if (answer == 0) {
      answer = compare(n1.getPrefix(), n2.getPrefix());
    }

    return answer;
  }

  public static int compare(CharacterData t1, CharacterData t2) {
    return compare(t1.getText(), t2.getText());
  }

  public static int compare(DocumentType o1, DocumentType o2) {
    if (o1 == o2) {
      return 0;
    }
    else if (o1 == null) {
      // null is less
      return -1;
    }
    else if (o2 == null) {
      return 1;
    }

    int answer = compare(o1.getPublicID(), o2.getPublicID());

    if (answer == 0) {
      answer = compare(o1.getSystemID(), o2.getSystemID());

      if (answer == 0) {
        answer = compare(o1.getName(), o2.getName());
      }
    }

    return answer;
  }

  public static int compare(Entity n1, Entity n2) {
    int answer = compare(n1.getName(), n2.getName());

    if (answer == 0) {
      answer = compare(n1.getText(), n2.getText());
    }

    return answer;
  }

  public static int compare(ProcessingInstruction n1, ProcessingInstruction n2) {
    int answer = compare(n1.getTarget(), n2.getTarget());

    if (answer == 0) {
      answer = compare(n1.getText(), n2.getText());
    }

    return answer;
  }

  public static int compareContent(Branch b1, Branch b2) {
    int c1 = b1.nodeCount();
    int c2 = b2.nodeCount();
    int answer = c1 - c2;

    if (answer == 0) {
      for (int i = 0; i < c1; i++) {
        Node n1 = b1.node(i);
        Node n2 = b2.node(i);
        answer = compareNodes(n1, n2);

        if (answer != 0) {
          break;
        }
      }
    }

    return answer;
  }

  public static int compare(String o1, String o2) {
    if (o1 == o2) {
      return 0;
    }
    else if (o1 == null) {
      // null is less
      return -1;
    }
    else if (o2 == null) {
      return 1;
    }

    return o1.compareTo(o2);
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
