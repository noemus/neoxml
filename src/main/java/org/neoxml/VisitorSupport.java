/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

/**
 * <p>
 * <code>VisitorSupport</code> is an abstract base class which is useful for implementation inheritence or when using
 * anonymous inner classes to create simple <code>Visitor</code> implementations.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.6 $
 */
public abstract class VisitorSupport implements Visitor
{
  public VisitorSupport() {}

  /**
   * Default visit method for all node types
   *
   * @param node
   */
  public boolean visit(Node node) {
    return true;
  }

  /**
   * Default visitEnter method for all node types
   *
   * @param node
   */
  public boolean visitEnter(Node node) {
    return true;
  }

  /**
   * Default visitLeave method for all node types
   *
   * @param node
   */
  public boolean visitLeave(Node node) {
    return true;
  }

  /**
   * Default visit method for all Branch nodes
   *
   * @param node
   */
  public boolean visit(Branch node) {
    return visit((Node)node);
  }

  /**
   * Default visitEnter method for all Branch nodes
   *
   * @param node
   */
  public boolean visitEnter(Branch node) {
    return visitEnter((Node)node);
  }

  /**
   * Default visitLeave method for all Branch nodes
   *
   * @param node
   */
  public boolean visitLeave(Branch node) {
    return visitLeave((Node)node);
  }

  /**
   * Default visit method for all CharacterData nodes
   *
   * @param node
   */
  public boolean visit(CharacterData node) {
    return visit((Node)node);
  }

  @Override
  public boolean visit(Document document) {
    return visit((Branch)document);
  }

  @Override
  public boolean visit(DocumentType documentType) {
    return visit((Node)documentType);
  }

  @Override
  public boolean visit(Element node) {
    return visit((Branch)node);
  }

  @Override
  public boolean visit(Attribute node) {
    return visit((Node)node);
  }

  @Override
  public boolean visit(CDATA node) {
    return visit((CharacterData)node);
  }

  @Override
  public boolean visit(Comment node) {
    return visit((CharacterData)node);
  }

  @Override
  public boolean visit(Entity node) {
    return visit((Node)node);
  }

  @Override
  public boolean visit(Namespace namespace) {
    return visit((Node)namespace);
  }

  @Override
  public boolean visit(ProcessingInstruction node) {
    return visit((Node)node);
  }

  @Override
  public boolean visit(Text node) {
    return visit((CharacterData)node);
  }

  @Override
  public boolean visitEnter(Document document) {
    return visitEnter((Branch)document);
  }

  @Override
  public boolean visitLeave(Document document) {
    return visitLeave((Branch)document);
  }

  @Override
  public boolean visitEnter(Element node) {
    return visitEnter((Branch)node);
  }

  @Override
  public boolean visitLeave(Element node) {
    return visitLeave((Branch)node);
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
