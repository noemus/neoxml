/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.xtree;

/**
 * <p>
 * <code>Visitor</code> is used to implement the <code>Visitor</code> pattern in DOM4J. An object of this interface can
 * be passed to a <code>Node</code> which will then call its typesafe methods. Please refer to the <i>Gang of Four </i>
 * book of Design Patterns for more details on the <code>Visitor</code> pattern.
 * </p>
 * <p/>
 * <p>
 * This <a href="http://www.patterndepot.com/put/8/JavaPatterns.htm">site </a> has further discussion on design patterns
 * and links to the GOF book. This <a href="http://www.patterndepot.com/put/8/visitor.pdf">link </a> describes the
 * Visitor pattern in detail.
 * </p>
 * TODO: update links add description of hierarchical visitor pattern and its motivation
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.8 $
 */
public interface Visitor
{
  /**
   * <p>
   * Visits the given <code>Document</code>
   * </p>
   *
   * @param document is the <code>Document</code> node to visit.
   * @return false to indicate that processing stopped at some point
   */
  boolean visit(Document document);

  /**
   * <p>
   * Called before visiting <code>Document</code>. It is possibble to suppress processing of subnodes.
   * </p>
   *
   * @param document is the <code>Document</code> node to visit.
   * @return true if subnodes should be processed
   */
  boolean visitEnter(Document document);

  /**
   * <p>
   * Called after processing all nodes of <code>Document</code>.
   * </p>
   *
   * @param document is the <code>Document</code> node to visit.
   * @return false to indicate that processing stopped at some point
   */
  boolean visitLeave(Document document);

  /**
   * <p>
   * Visits the given <code>DocumentType</code>
   * </p>
   *
   * @param documentType is the <code>DocumentType</code> node to visit.
   */
  boolean visit(DocumentType documentType);

  /**
   * <p>
   * Visits the given <code>Element</code>
   * </p>
   *
   * @param node is the <code>Element</code> node to visit.
   */
  boolean visit(Element node);

  boolean visitEnter(Element node);

  boolean visitLeave(Element node);

  /**
   * <p>
   * Visits the given <code>Attribute</code>
   * </p>
   *
   * @param node is the <code>Attribute</code> node to visit.
   */
  boolean visit(Attribute node);

  /**
   * <p>
   * Visits the given <code>CDATA</code>
   * </p>
   *
   * @param node is the <code>CDATA</code> node to visit.
   */
  boolean visit(CDATA node);

  /**
   * <p>
   * Visits the given <code>Comment</code>
   * </p>
   *
   * @param node is the <code>Comment</code> node to visit.
   */
  boolean visit(Comment node);

  /**
   * <p>
   * Visits the given <code>Entity</code>
   * </p>
   *
   * @param node is the <code>Entity</code> node to visit.
   */
  boolean visit(Entity node);

  /**
   * <p>
   * Visits the given <code>Namespace</code>
   * </p>
   *
   * @param namespace is the <code>Namespace</code> node to visit.
   */
  boolean visit(Namespace namespace);

  /**
   * <p>
   * Visits the given <code>ProcessingInstruction</code>
   * </p>
   *
   * @param node is the <code>ProcessingInstruction</code> node to visit.
   */
  boolean visit(ProcessingInstruction node);

  /**
   * <p>
   * Visits the given <code>Text</code>
   * </p>
   *
   * @param node is the <code>Text</code> node to visit.
   */
  boolean visit(Text node);
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
