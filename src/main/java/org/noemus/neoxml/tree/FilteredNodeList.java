/*
 * Copyright (c) 2013 TDP Ltd. All Rights Reserved.
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED,
 * EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF TDP LTD. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 */
package org.noemus.neoxml.tree;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.noemus.neoxml.Node;
import org.noemus.neoxml.NodeList;
import org.noemus.neoxml.util.ProxyListIterator;

import java8.util.function.Predicate;

/**
 * This implementation is based on backing list that implements RandomAccess
 */
class FilteredNodeList<T extends Node> extends AbstractNodeListFacade<T>
{
  final Predicate<? super T> condition;

  @SuppressWarnings("unchecked")
  public FilteredNodeList(DefaultNodeList<T> list, Predicate<? super T> condition) {
    super(list);

    Objects.requireNonNull(condition);

    this.condition = condition;
  }

  @Override
  public T get(int index) {
    return nodeList.get(find(index));
  }

  @Override
  public boolean add(T element) {
    nodeList.addNode(element);
    return condition.test(element);
  }

  @Override
  public T set(int index, T element) {
    return nodeList.setNode(find(index), element);
  }

  @Override
  public void add(int index, T element) {
    nodeList.addNode(findForAdd(index), element);
  }

  @Override
  public T remove(int index) {
    return nodeList.removeNode(find(index));
  }
  
  @Override
  public int indexOf(Object element) {
    int index = -1;
    
    for (T node : this) {
      ++index;
      
      if (node == element) {
        break;
      }
    }
    
    return index;
  }

  @Override
  public int lastIndexOf(Object element) {
    int lastIndex = -1;
    int index = -1;
    
    for (T node : this) {
      ++index;
      if (node == element) {
        lastIndex = index;
      }
    }
    
    return lastIndex;
  }

  @Override
  public void clear() {
    if (!nodeList.isEmpty()) {
      for (int i=nodeList.size()-1; i>=0; --i) {
        if (condition.test(nodeList.get(i))) {
          nodeList.remove(i);
        }
      }
    }
  }

  @Override
  public int size() {
    if (nodeList.isEmpty()) {
      return 0;
    }
    
    final int origSize = nodeList.size();
    int size = 0;
    
    for (int i=0; i<origSize; ++i) {
      if (condition.test(nodeList.get(i))) {
        ++size;
      }
    }
    
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean contains(Object o) {
    if (nodeList.isEmpty()) {
      return false;
    }

    return super.contains(o);
  }

  @Override
  public ListIterator<T> listIterator() {
    if (nodeList.isEmpty()) {
      return Collections.emptyListIterator();
    }
    return new ProxyListIterator<>(nodeList.listIterator(), condition);
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    checkLowerBound(index);
    
    if (nodeList.isEmpty()) {
      return Collections.emptyListIterator();
    }
    return new ProxyListIterator<>(nodeList.listIterator(find(index)), condition);
  }

  @Override
  public Iterator<T> iterator() {
    if (nodeList.isEmpty()) {
      return Collections.emptyIterator();
    }
    return new Itr();
  }

  @Override
  public T find(Predicate<? super T> cond) {
    return super.find(and(cond));
  }

  @Override
  public <N extends T> N find(Predicate<? super T> cond, Class<N> nodeType) {
    return super.find(and(cond), nodeType);
  }

  @Override
  public NodeList<T> filter(Predicate<? super T> cond) {
    return super.filter(and(cond));
  }

  @Override
  public <N extends T> NodeList<N> filter(Predicate<? super T> cond, Class<N> nodeType) {
    return super.filter(and(cond), nodeType);
  }

  @Override
  public NodeList<T> remove(Predicate<? super T> cond) {
    super.remove(and(cond));
    return this;
  }

  @Override
  public NodeList<T> facade() {
    return nodeList.filter(condition);
  }

  @Override
  public NodeList<T> duplicate() {
    return new DefaultNodeList<>(nodeList.getParent(), this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public NodeList<T> copy() {
    // use parent nodelist size => no need to iterate twice
    final DefaultNodeList<T> clonedNodes = new DefaultNodeList<>(null, nodeList.size());

    for (T node : this) {
      node = (T)node.clone();
      clonedNodes.addNode(node);
    }
    return clonedNodes;
  }
  
  private int find(int index) {
    checkLowerBound(index);
    
    final int size = nodeList.size();
    
    int idx = -1;
    for (int orig=0; orig<size; ++orig) {
      if (condition.test(nodeList.get(orig))) {
        if (++idx == index) {
          return orig;
        }
      }
    }
    
    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (idx + 1));
  }
  
  private int findForAdd(int index) {
    checkLowerBound(index);
    
    final int size = nodeList.size();
    
    int idx = -1;
    for (int orig=0; orig<size; ++orig) {
      if (condition.test(nodeList.get(orig))) {
        if (++idx == index) {
          return orig;
        }
      }
    }
    
    if (index == idx + 1) {
      return size;
    }
    
    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (idx + 1));
  }

  /**
   * Called on nodeList modification
   */
  @Override
  void copyNodes() {
    // defensive copy of original list to suppress propagation of changes from branch to this node list
    //nodeList = nodeList.duplicate();
  }

  // internal helper methods and classes

  private Predicate<? super T> and(Predicate<? super T> cond) {
    return new AndCondition<>(condition, cond);
  }

  static void checkLowerBound(int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index);
    }
  }

  static final class AndCondition<T> implements Predicate<T>
  {
    final Predicate<? super T> cond1;
    final Predicate<? super T> cond2;

    AndCondition(Predicate<? super T> c1, Predicate<? super T> c2) {
      cond1 = c1;
      cond2 = c2;
    }

    @Override
    public boolean test(T t) {
      return cond1.test(t) && cond2.test(t);
    }
  }
  
  @SuppressWarnings("synthetic-access")
  class Itr implements Iterator<T>
  {
    int lastIndex = -1;
    T nextElt;
    int lastModCount = nodeList.modCount();
    int size = nodeList.size();
    
    @Override
    public boolean hasNext() {
      return nextElt != null || findNext();
    }

    private final boolean findNext() {
      while (++lastIndex < size) {
        checkForComodification();
        nextElt = nodeList.get(lastIndex);
        
        if (condition.test(nextElt)) {
          return true;
        }
      }
      
      nextElt = null;
      
      return false;
    }

    @Override
    public T next() {
      if (hasNext()) {
        T elt = nextElt;
        nextElt = null;
        return elt;
      }
      
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      if (lastIndex == -1 || lastIndex == size) {
        throw new NoSuchElementException();
      }
      
      checkForComodification();
      nodeList.remove(lastIndex);
      lastModCount = nodeList.modCount();
      size = nodeList.size();
    }
    
    private final void checkForComodification() {
      if (nodeList.modCount() != lastModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }

  /*
  abstract static class AbstractOp<T> implements Predicate<T>
  {
    protected int originalIdx = -1;
    protected int idx = -1;

    boolean iterate(List<? extends T> list, Predicate<? super T> cond) {
      final int size = list.size();

      for (originalIdx = 0; originalIdx < size; ++originalIdx) {
        final T v = list.get(originalIdx);

        if (cond.test(v)) {
          ++idx;

          if (test(v)) {
            return true;
          }
        }
      }

      return false;
    }

    int originalIndex() {
      return originalIdx;
    }

    int index() {
      return idx;
    }
  }

  static class CountOp<TT> extends AbstractOp<TT>
  {
    protected int count = 0;

    CountOp(List<? extends TT> list, Predicate<? super TT> cond) {
      iterate(list, cond);
    }

    @Override
    public boolean test(TT v) {
      ++count;
      return false;
    }

    int size() {
      return count;
    }
  }
  
  static class FindOp<TT> extends AbstractOp<TT>
  {
    final int index;

    FindOp(int i, List<? extends TT> list, Predicate<? super TT> cond) {
      this(i, list, cond, true);
    }
    
    FindOp(int i, List<? extends TT> list, Predicate<? super TT> cond, boolean failIfNotFound) {
      checkLowerBound(i);
      index = i;

      if (!iterate(list, cond) && failIfNotFound) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (idx + 1));
      }
    }

    @Override
    public boolean test(TT v) {
      return index == idx;
    }
  }
  */
}
