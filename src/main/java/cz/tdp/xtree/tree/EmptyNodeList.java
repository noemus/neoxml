/*
 * Copyright (c) 2014 TDP Ltd. All Rights Reserved.
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED,
 * EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF TDP LTD. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 */
package cz.tdp.xtree.tree;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import cz.tdp.xtree.Branch;
import cz.tdp.xtree.Node;
import cz.tdp.xtree.NodeList;

final class EmptyNodeList<T extends Node> extends AbstractList<T> implements NodeList<T>
{
  EmptyNodeList() {}
  
  @Override
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public final void add(T first, T... nodes) {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Iterator<T> iterator() {
    return (Iterator<T>)EmptyIterator.EMPTY_ITERATOR;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ListIterator<T> listIterator() {
    return (ListIterator<T>)EmptyListIterator.EMPTY_LIST_ITERATOR;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean contains(Object obj) {
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return c.isEmpty();
  }

  private static final Object[] EMPTY = new Object[0];

  @Override
  public Object[] toArray() {
    return EMPTY;
  }

  @Override
  public <E> E[] toArray(E[] a) {
    return a;
  }

  @Override
  public T get(int index) {
    throw new IndexOutOfBoundsException("Index: " + index);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof NodeList) && ((List<?>)o).isEmpty() && ((NodeList<?>)o).getParent() == null;
  }

  @Override
  public int hashCode() {
    return 1;
  }

  // Preserves singleton property
  private Object readResolve() {
    return AbstractBranch.EMPTY_NODE_LIST;
  }

  @Override
  public NodeList<T> detach() {
    return this;
  }

  @Override
  public NodeList<T> attach(Branch branch) {
    return this;
  }

  @Override
  public Branch getParent() {
    return null;
  }

  @Override
  public T find(Predicate<? super T> cond) {
    return null;
  }

  @Override
  public <N extends T> N find(Predicate<? super T> cond, Class<N> nodeType) {
    return null;
  }

  @Override
  public NodeList<T> filter(Predicate<? super T> cond) {
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.dom4j.NodeList#filter(org.dom4j.java.util.functions.Predicate, java.lang.Class)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <N extends T> NodeList<N> filter(Predicate<? super T> cond, Class<N> clazz) {
    return (NodeList<N>)this;
  }

  @Override
  public NodeList<T> remove(Predicate<? super T> cond) {
    return this;
  }

  @Override
  public NodeList<T> facade() {
    return this;
  }

  @Override
  public NodeList<T> duplicate() {
    return this;
  }

  @Override
  public NodeList<T> copy() {
    return this;
  }
  
  @Override
  public NodeList<T> content() {
    return null;
  }
  
  @Override
  public boolean isReadOnly() {
    return true;
  }

  static class EmptyListIterator<T> extends EmptyIterator<T> implements ListIterator<T>
  {
    static final EmptyListIterator<Object> EMPTY_LIST_ITERATOR = new EmptyListIterator<>();

    @Override
    public boolean hasPrevious() {
      return false;
    }

    @Override
    public T previous() {
      throw new NoSuchElementException();
    }

    @Override
    public int nextIndex() {
      return 0;
    }

    @Override
    public int previousIndex() {
      return -1;
    }

    @Override
    public void set(T e) {
      throw new IllegalStateException();
    }

    @Override
    public void add(T e) {
      throw new UnsupportedOperationException();
    }
  }

  static class EmptyIterator<E> implements Iterator<E>
  {
    static final EmptyIterator<Object> EMPTY_ITERATOR = new EmptyIterator<>();

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public E next() {
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new IllegalStateException();
    }
  }
}
