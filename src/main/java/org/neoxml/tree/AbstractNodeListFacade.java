/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.tree;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

import org.neoxml.Branch;
import org.neoxml.Node;
import org.neoxml.NodeList;

/**
 * <code>BackedList</code> represents a list of content of a {@link org.neoxml.Branch}. Changes to the list will be
 * reflected in the branch,
 * though changes to the branch will not be reflected in this list.
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.14 $
 */
abstract class AbstractNodeListFacade<T extends Node> extends AbstractList<T> implements NodeList<T>
{
  /**
   * The content of the Branch which is modified if I am modified
   */
  protected DefaultNodeList<T> nodeList;

  AbstractNodeListFacade(DefaultNodeList<T> nodes) {
    super();
    Objects.requireNonNull(nodes);
    this.nodeList = nodes;
  }

  @Override
  @SafeVarargs
  public final void add(T first, T... nodes) {
    add(first);
    
    if (nodes != null) {
      Collections.addAll(this, nodes);
    }
  }

  @Override
  public NodeList<T> detach() {
    nodeList.detach();
    return this;
  }

  @Override
  public NodeList<T> attach(Branch branch) {
    nodeList.attach(branch);
    return this;
  }

  @Override
  public AbstractBranch getParent() {
    return nodeList.getParent();
  }

  @Override
  public T find(Predicate<? super T> cond) {
    return nodeList.find(cond);
  }

  @Override
  public <N extends T> N find(Predicate<? super T> cond, Class<N> nodeType) {
    return nodeList.find(cond, nodeType);
  }

  @Override
  public NodeList<T> filter(Predicate<? super T> cond) {
    return nodeList.filter(cond);
  }

  @Override
  public <N extends T> NodeList<N> filter(Predicate<? super T> cond, Class<N> nodeType) {
    return nodeList.filter(cond, nodeType);
  }
  
  @Override
  public NodeList<T> remove(Predicate<? super T> cond) {
    if (!nodeList.isEmpty()) {
      removeIf(cond);
    }
    return this;
  }

  @Override
  public NodeList<T> content() {
    if (getParent() != null) {
      return copy();
    }
    return nodeList;
  }
  
  @Override
  public boolean isReadOnly() {
    return nodeList.isReadOnly();
  }
  
  @Override
  public AbstractNodeListFacade<T> clone() {
    if (isReadOnly()) {
      return this;
    }
    else {
      try {
        @SuppressWarnings("unchecked")
        AbstractNodeListFacade<T> answer = (AbstractNodeListFacade<T>)super.clone();
        return answer;
      }
      catch (CloneNotSupportedException e) {
        // should never happen
        throw new AssertionError("This should never happen. Caught: ", e);
      }
    }
  }
  
  void copyNodes() {}
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
