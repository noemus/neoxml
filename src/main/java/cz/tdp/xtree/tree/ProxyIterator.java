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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class ProxyIterator<T> implements Iterator<T>
{
  private final Iterator<? extends T> it;

  protected final Predicate<? super T> cond;
  protected T nextElt;

  public ProxyIterator(Iterator<? extends T> it, Predicate<? super T> cond) {
    this.it = it;
    this.cond = cond;
  }

  protected boolean findNext() {
    while (it.hasNext()) {
      final T entry = it.next();
      if (cond.test(entry)) {
        nextElt = entry;
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean hasNext() {
    return (nextElt != null) || findNext();
  }

  @Override
  public T next() {
    if (hasNext()) {
      final T elt = nextElt;
      nextElt = null;
      return elt;
    }

    throw new NoSuchElementException();
  }

  @Override
  public void remove() {
    it.remove();
  }
}
