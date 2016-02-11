/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.neoxml.Branch;
import org.neoxml.Node;
import org.neoxml.NodeList;
import org.neoxml.util.HeadList;

/**
 * <code>BackedList</code> represents a list of content of a {@link org.neoxml.Branch}. Changes to the list will be
 * reflected in the branch,
 * though changes to the branch will not be reflected in this list.
 * This implementation doesn't allow null entries!
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.14 $
 */
@SuppressWarnings("serial")
public class DefaultNodeList<T extends Node> extends HeadList<T> implements NodeList<T>
{
  /**
   * The <code>AbstractBranch</code> instance which owns the content
   */
  private AbstractBranch branch;

  public DefaultNodeList(AbstractBranch branch) {
    super();
    this.branch = branch;
  }

  public DefaultNodeList(AbstractBranch branch, int initialSize) {
    super(initialSize);
    this.branch = branch;
  }

  protected DefaultNodeList(AbstractBranch branch, HeadList<? extends T> nodes) {
    super(nodes);
    this.branch = branch;
  }
  
  protected DefaultNodeList(AbstractBranch branch, List<? extends T> nodes) {
    super(nodes);
    this.branch = branch;
  }

  @Override
  public boolean add(T node) {
    addNode(node);
    return true;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public final void add(T first, T... nodes) {
    add(first);
    
    if (nodes != null) {
      for (T n : nodes) {
        add(n);
      }
    }
  }
  
  @Override
  public void add(int index, T node) {
    addNode(index, node);
  }

  @Override
  public T set(int index, T node) {
    return setNode(index, node);
  }

  @Override
  public boolean remove(Object node) {
    if (node instanceof Node) {
      return removeNode((Node)node);
    }
    return false;
  }

  
  @Override
  public T remove(int index) {
    return removeNode(index);
  }

  @Override
  public void clear() {
    clearNodes();
  }

  @Override
  public NodeList<T> detach() {
    if (branch != null) {
      branch.contentRemoved();
      branch = null;
    }
    return this;
  }

  @Override
  public NodeList<T> attach(Branch parent) {
    if (parent instanceof AbstractBranch) {
      attachBranch((AbstractBranch)parent);
      return this;
    }
    else {
      //XXX find a better way, for only throw exception if branch does extend AbstractBranch class
      throw new UnsupportedOperationException("Cannot attach to branch that doesn't extend AbstractBranch class");
    }
  }

  @Override
  public AbstractBranch getParent() {
    return branch;
  }

  @Override
  public T find(Predicate<? super T> cond) {
    for (T node : this) {
      if (cond.test(node)) {
        return node;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends T> N find(Predicate<? super T> cond, Class<N> nodeType) {
    return (N)find(cond);
  }

  @Override
  public NodeList<T> filter(Predicate<? super T> cond) {
    return new FilteredNodeList<>(this, cond);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends T> NodeList<N> filter(Predicate<? super T> cond, Class<N> clazz) {
    return (NodeList<N>)filter(cond);
  }

  @Override
  public NodeList<T> remove(Predicate<? super T> cond) {
    for (Iterator<T> it = iterator(); it.hasNext();) {
      if (cond.test(it.next())) {
        it.remove();
      }
    }

    return this;
  }

  @Override
  public NodeList<T> facade() {
    return new NodeListFacade<>(this);
  }

  @Override
  public DefaultNodeList<T> duplicate() {
    return new DefaultNodeList<>(branch, this);
  }

  @Override
  public NodeList<T> copy() {
    final DefaultNodeList<T> newNodes = new DefaultNodeList<>(null, size());
    
    for (T node : this) {
      @SuppressWarnings("unchecked")
      T newNode = (T)node.clone();
      
      newNodes.addNode(newNode);
    }
    
    return newNodes;
  }
  
  @Override
  public NodeList<T> content() {
    if (branch != null) {
      return copy();
    }
    
    return this;
  }
  
  @Override
  public DefaultNodeList<T> clone() {
    @SuppressWarnings("unchecked")
    DefaultNodeList<T> answer = (DefaultNodeList<T>)super.clone();
    return answer;
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }
  
  void addNode(T node) {
    Objects.requireNonNull(node);

    if (branch != null) {
      branch.beforeChildAdd(node);
    }
    
    super.add(node);
    
    if (branch != null) {
      branch.childAdded(node);
    }
  }

  void addNode(int index, T node) {
    Objects.requireNonNull(node);

    if (branch != null) {
      branch.beforeChildAdd(node);
    }
    
    super.add(index, node);
    
    if (branch != null) {
      branch.childAdded(node);
    }
  }

  T setNode(int index, T node) {
    Objects.requireNonNull(node);

    if (branch != null) {
      branch.beforeChildAdd(node);
    }

    final T removed = super.set(index, node);
    
    if (node != removed) {
      if (branch != null) {
        if (removed != null) {
          branch.childRemoved(removed);
        }
        branch.childAdded(node);
      }
    }

    return removed;
  }

  boolean removeNode(Node node) {
    if (super.remove(node)) {
      if (branch != null) {
        branch.childRemoved(node);
      }
      return true;
    }

    return false;
  }

  T removeNode(int index) {
    final T removed = super.remove(index);

    if (removed != null && branch != null) {
      branch.childRemoved(removed);
    }

    return removed;
  }

  void clearNodes() {
    // must be before super.clear()
    if (branch != null) {
      branch.contentRemoved();
    }
    
    super.clear();
  }
  
  int modCount() {
    return modCount;
  }

  private void attachBranch(AbstractBranch parent) {
    this.branch = parent;
    
    if (branch != null) {
      for (T node : this) {
        branch.childAdded(node);
      }
    }
  }
}


