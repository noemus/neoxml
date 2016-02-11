package org.neoxml.tree;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

import org.neoxml.Node;
import org.neoxml.NodeList;

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
    
    int size = 0;
    
    for (T node : nodeList) {
      if (condition.test(node)) {
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
    return listIterator(0);
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    checkLowerBound(index);
    
    if (nodeList.isEmpty()) {
      return Collections.emptyListIterator();
    }
    
    return new ListItr(index);
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
  
  protected int find(int index) {
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
  
  int findForAdd(int index) {
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
    int nextIndex = -1;
    int currIndex = -1;
    T nextElt;
    int lastModCount = nodeList.modCount();
    int size = nodeList.size();
    
    @Override
    public boolean hasNext() {
      return nextElt != null || findNext();
    }

    protected boolean findNext() {
      while (++nextIndex < size) {
        checkForComodification();
        nextElt = nodeList.get(nextIndex);
        
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
        T currElt = nextElt;
        currIndex = nextIndex;
        nextElt = null;
        return currElt;
      }
      
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      if (currIndex < 0) {
        throw new IllegalStateException("Cannot call remove() without previous call to next() or previous()");
      }
      
      checkForComodification();
      nodeList.remove(nextIndex);
      
      currIndex = -1;
      --nextIndex;
      
      lastModCount = nodeList.modCount();
      size = nodeList.size();
    }
    
    protected void checkForComodification() {
      if (nodeList.modCount() != lastModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }

  public final class ListItr extends Itr implements ListIterator<T>
  {
    int prevIndex = -1;
    T prevElt;
    
    int idx;

    public ListItr(int index) {
      super();
      
      // set last returned index in underlying list
      if (index > 0) {
        prevIndex = findForAdd(index);
        nextIndex = prevIndex + 1;
      }
      else {
        prevIndex = -1;
        nextIndex = -1;
      }
      
      // next index in current list
      idx = index;
    }
    
    @Override
    public T next() {
      prevElt = super.next();
      prevIndex = currIndex;
      ++idx;
      return prevElt;
    }
    
    @Override
    public boolean hasPrevious() {
      return prevElt != null || findPrev();
    }
    
    protected boolean findPrev() {
      while (prevIndex > 0) {
        checkForComodification();
        
        prevElt = nodeList.get(--prevIndex);
        
        if (condition.test(prevElt)) {
          return true;
        }
      }
      
      prevElt = null;
      
      return false;
    }

    @Override
    public T previous() {
      if (hasPrevious()) {
        T currElt = prevElt;
        currIndex = prevIndex;
        nextIndex = currIndex;
        nextElt = currElt;
        prevElt = null;
        --idx;
        return currElt;
      }
      
      throw new NoSuchElementException();
    }

    @Override
    public int nextIndex() {
      return idx;
    }

    @Override
    public int previousIndex() {
      return idx - 1;
    }
    
    @Override
    public void remove() {
      super.remove();
      
      if (prevElt != null) {
        // after next();
        prevElt = null;
        --idx;
      }
      
      if (nextElt != null) {
        // after previous()
        nextElt = null;
      }
    }

    @Override
    public void set(T e) {
      if (currIndex < 0) {
        throw new IllegalStateException("Cannot call set() without previous call to next() or previous()");
      }
      
      checkForComodification();
      
      nodeList.set(currIndex, e);
      
      currIndex = -1;
      
      if (condition.test(e)) {
        prevElt = e;
      }
      else {
        prevElt = null;
      }
      
      lastModCount = nodeList.modCount();
      size = nodeList.size();
    }

    @Override
    public void add(T e) {
      if (currIndex < 0) {
        throw new IllegalStateException("Cannot call add() without previous call to next() or previous()");
      }
      
      checkForComodification();
      
      nodeList.add(currIndex, e);
      
      ++currIndex;
      ++nextIndex;
      
      if (condition.test(e)) {
        ++idx;
      }
      
      lastModCount = nodeList.modCount();
      size = nodeList.size();
    }
  }
}
