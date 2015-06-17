package org.noemus.neoxml.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java8.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

public class ProxyListIteratorTest
{
  List<String> list;
  ListIterator<String> iterator;
  
  @Before
  public void setup() {
    list = new ArrayList<>(Arrays.asList("A", "B", "C", "ABC"));
    iterator = new ProxyListIterator<>(list.listIterator(), new Predicate<String>() {
      @Override
      public boolean test(String t) {
        return t.startsWith("A");
      }
    });
  }
  
  @Test
  public void testForwardIteration() {
    assertTrue(iterator.hasNext());
    assertEquals("A", iterator.next());
    
    assertTrue(iterator.hasNext());
    assertEquals("ABC", iterator.next());
    
    assertFalse(iterator.hasNext());
  }
  
  @Test
  public void testBackwardIteration() {
    iterator = new ProxyListIterator<>(list.listIterator(4), new Predicate<String>() {
      @Override
      public boolean test(String t) {
        return t.startsWith("A");
      }
    });
    
    assertTrue(iterator.hasPrevious());
    assertEquals("ABC", iterator.previous());
    
    assertTrue(iterator.hasPrevious());
    assertEquals("A", iterator.previous());
    
    assertFalse(iterator.hasPrevious());
  }
  
  @Test(expected=NoSuchElementException.class)
  public void testAfterEnd() {
    iterator.next();
    iterator.next();
    iterator.next();
  }
  
  @Test(expected=NoSuchElementException.class)
  public void testBeforeStart() {
    assertEquals("A", iterator.next());
    assertEquals("ABC", iterator.next());
    assertEquals("ABC", iterator.previous());
    assertEquals("A", iterator.previous());
    iterator.previous();
  }
  
  @Test
  public void testRemove() {
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator.remove();
    
    assertEquals(2, list.size());
  }
  
  @Test
  public void testAdd() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testSet() {
    fail("Not yet implemented");
  }
}
