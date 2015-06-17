package org.noemus.neoxml.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java8.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

public class ProxyIteratorTest
{
  List<String> list;
  Iterator<String> iterator;
  
  @Before
  public void setup() {
    list = new ArrayList<>(Arrays.asList("A", "B", "C", "ABC"));
    iterator = new ProxyIterator<>(list.iterator(), new Predicate<String>() {
      @Override
      public boolean test(String t) {
        return t.startsWith("A");
      }
    });
  }
  
  @Test
  public void testIteration() {
    assertTrue(iterator.hasNext());
    assertEquals("A", iterator.next());
    
    assertTrue(iterator.hasNext());
    assertEquals("ABC", iterator.next());
    
    assertFalse(iterator.hasNext());
  }
  
  @Test(expected=NoSuchElementException.class)
  public void testBeyondEnd() {
    iterator.next();
    iterator.next();
    iterator.next();
  }
  
  @Test
  public void testRemove() {
    assertEquals("A", iterator.next());
    iterator.remove();
    assertEquals("ABC", iterator.next());
    iterator.remove();
    
    assertEquals(2, list.size());
  }
}
