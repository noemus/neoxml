package org.neoxml.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class HeadList<T> extends AbstractList<T> implements RandomAccess, Cloneable, Serializable
{
  private static final long serialVersionUID = 1L;
  
  private static final int DEFAULT_ARRAY_SIZE = 4;

  protected T head;
  protected ArrayList<T> list;

  public HeadList() {
    super();
  }

  public HeadList(int minSize) {
    super();

    if (minSize > 1) {
      list = new ArrayList<>(minSize);
    }
  }

  protected HeadList(List<? extends T> nodes) {
    super();

    final int size = nodes.size();
    if (size == 1) {
      head = nodes.get(0);
    }
    else if (size > 1) {
      list = new ArrayList<>(nodes);
    }
  }
  
  @SuppressWarnings("unchecked")
  protected HeadList(HeadList<? extends T> nodes) {
    super();

    head = nodes.head;
    list = nodes.list != null ? (ArrayList<T>)nodes.list.clone() : null;
  }

  @Override
  public T get(int index) {
    if (list != null) {
      return list.get(index);
    }
    else if (head != null && index == 0) {
      return head;
    }

    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
  }

  @Override
  public boolean add(T element) {
    if (list != null) {
      list.add(element);
    }
    else {
      if (head == null) {
        head = element;
      }
      else {
        createList(head, element);
      }
    }
    modCount++;
    return true;
  }

  private void createList(T h, T e) {
    list = new ArrayList<>(DEFAULT_ARRAY_SIZE);
    list.add(h);
    
    if (e != null) {
      list.add(e);
    }
    
    // clear reference
    head = null;
  }

  @Override
  public T set(int index, T element) {
    if (list != null) {
      return list.set(index, element);
    }
    else if (head != null && index == 0) {
      head = element;
      return head;
    }
    
    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
  }

  @Override
  public void add(int index, T element) {
    if (list != null) {
      list.add(index, element);
      modCount++;
    }
    else if (index == 0) {
      if (head != null) {
        createList(element, head);
      }
      else {
        head = element;
      }
      modCount++;
    }
    else if (index == 1 && head != null) {
      createList(head, element);
      modCount++;
    }
    else {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
    }
  }

  @Override
  public T remove(int index) {
    if (list != null) {
      T _elt = list.remove(index);
      if (_elt != null) {
        modCount++;
      }
      return _elt;
    }
    else if (head != null && index == 0) {
      T _head = head;
      head = null;
      modCount++;
      return _head;
    }

    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
  }
  
  @Override
  public boolean remove(Object element) {
    if (list != null) {
      if (list.remove(element)) {
        modCount++;
        return true;
      }
    }
    else if (head != null) {
      if (head == element) {
        head = null;
        modCount++;
        return true;
      }
    }
    return false;
  }
  
  @Override
  public int indexOf(Object element) {
    if (list != null) {
      return list.indexOf(element);
    }
    else if (head != null) {
      if (head == element) {
        return 0;
      }
    }
    
    return -1;
  }

  @Override
  public int lastIndexOf(Object element) {
    if (list != null) {
      return list.lastIndexOf(element);
    }
    else if (head != null) {
      if (head == element) {
        return 0;
      }
    }
    
    return -1;
  }

  @Override
  public int size() {
    if (list != null) {
      return list.size();
    }
    
    return head != null ? 1 : 0;
  }
  
  @Override
  public boolean isEmpty() {
    if (list != null) {
      return list.isEmpty();
    }
    
    return head == null;
  }

  @Override
  public ListIterator<T> listIterator() {
    return listIterator(0);
  }
  
  @Override
  public ListIterator<T> listIterator(final int index) {
    if (list != null) {
      return list.listIterator(index);
    }
    else if (head != null) {
      createList(head, null);
      return list.listIterator(index);
    }

    return Collections.emptyListIterator();
  }

  @Override
  public Iterator<T> iterator() {
    if (list != null) {
      return list.iterator();
    }
    else if (head != null) {
      return new SingleItr();
    }

    return Collections.emptyIterator();
  }
  
  public void ensureCapacity(int minCapacity) {
    if (list != null) {
      list.ensureCapacity(minCapacity);
    }
    else {
      list = new ArrayList<>(minCapacity);
      
      if (head != null) {
        list.add(head);
        head = null;
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public HeadList<T> clone() {
    try {
      HeadList<T> v = (HeadList<T>) super.clone();
      if (list != null) {
        v.list = (ArrayList<T>)list.clone();
      }
      return v;
    }
    catch (CloneNotSupportedException e) {
        // this shouldn't happen, since we are Cloneable
        throw new InternalError();
    }
  }
  
  @SuppressWarnings("synthetic-access")
  private class SingleItr implements Iterator<T>
  {
    protected boolean atStart = true;
    int expectedModCount = modCount;

    SingleItr() {}

    @Override
    public boolean hasNext() {
      return atStart && head != null;
    }

    @Override
    public T next() {
      if (atStart) {
        checkForComodification();
        if (head != null) {
          atStart = false;
          return head;
        }
      }

      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      if (head != null && !atStart) {
        checkForComodification();
        HeadList.this.remove(0);
        return;
      }

      throw new NoSuchElementException();
    }

    final void checkForComodification() {
      if (modCount != expectedModCount) throw new ConcurrentModificationException();
    }
  }
}
