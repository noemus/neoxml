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
package cz.tdp.xtree.tree;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class ProxyListIterator<T> extends ProxyIterator<T> implements ListIterator<T>
{
  private final ListIterator<? extends T> it;
  private T prevElt;

  public ProxyListIterator(ListIterator<? extends T> it, Predicate<? super T> cond) {
    super(it, cond);
    this.it = it;
  }

  private boolean findPrev() {
    while (it.hasPrevious()) {
      final T entry = it.previous();
      if (cond.test(entry)) {
        prevElt = entry;
        //nextElt = null;
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean hasPrevious() {
    return (prevElt != null) || findPrev();
  }

  @Override
  public T next() {
    if (hasNext()) {
      final T elt = prevElt;
      prevElt = elt;
      nextElt = null;
      return elt;
    }

    throw new NoSuchElementException();
  }

  @Override
  public T previous() {
    if (hasPrevious()) {
      final T elt = prevElt;
      prevElt = null;
      nextElt = elt;
      return elt;
    }

    throw new NoSuchElementException();
  }

  @Override
  public int nextIndex() {
    return 0;
  }

  @Override
  public int previousIndex() {
    return 0;
  }

  @Override
  public void set(T e) {

  }

  @Override
  public void add(T e) {

  }
}
