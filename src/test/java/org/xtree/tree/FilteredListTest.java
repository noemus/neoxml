package org.xtree.tree;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import java8.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.xtree.Node;
import org.xtree.tree.DefaultElement;
import org.xtree.tree.DefaultNodeList;
import org.xtree.tree.FilteredNodeList;

public class FilteredListTest
{
  private static final DefaultElement ELEM_A = elem("A");
  private static final DefaultElement ELEM_B = elem("B");
  private static final DefaultElement ELEM_C = elem("C");
  
  private static final DefaultElement ELEM_10 = elem("10");
  private static final DefaultElement ELEM_11 = elem("11");
  private static final DefaultElement ELEM_12 = elem("12");
  
  private DefaultNodeList<Node> list1;
  private DefaultNodeList<Node> list2;

  @Before
  public void setup() {
    list1 = new DefaultNodeList<>(null, 6);
    list1.add(ELEM_A, ELEM_B, ELEM_C, ELEM_10, ELEM_11, ELEM_12);
    
    list2 = new DefaultNodeList<>(null, 6);
    list2.add(ELEM_10, ELEM_A, ELEM_B, ELEM_11, ELEM_12, ELEM_C);
  }
  
  @Test
  public void createFilteredList_success() {
    new FilteredNodeList<>(list1, new TwoOrMore());
  }
  
  @Test(expected = NullPointerException.class)
  public void testFilteredList_invalidArg0() {
    new FilteredNodeList<>(null, new TwoOrMore());
  }
  
  @Test(expected = NullPointerException.class)
  public void testFilteredList_invalidArg1() {
    new FilteredNodeList<>(list1, null);
  }
  
  @Test
  public void testSize_normal() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list1, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered3 = new FilteredNodeList<>(list1, new AllwaysTrue());
    final FilteredNodeList<Node> filtered4 = new FilteredNodeList<>(list1, new AllwaysFalse());
    assertEquals(3, filtered1.size());
    assertEquals(3, filtered2.size());
    assertEquals(6, filtered3.size());
    assertEquals(0, filtered4.size());
    list1.add(elem("D"));
    assertEquals(3, filtered1.size());
    assertEquals(4, filtered2.size());
    assertEquals(7, filtered3.size());
    assertEquals(0, filtered4.size());
    list1.add(elem("AB"));
    assertEquals(4, filtered1.size());
    assertEquals(5, filtered2.size());
    assertEquals(8, filtered3.size());
    assertEquals(0, filtered4.size());
  }
  
  @Test
  public void testSize_mixed() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list2, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list2, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered3 = new FilteredNodeList<>(list2, new AllwaysTrue());
    final FilteredNodeList<Node> filtered4 = new FilteredNodeList<>(list2, new AllwaysFalse());
    assertEquals(3, filtered1.size());
    assertEquals(3, filtered2.size());
    assertEquals(6, filtered3.size());
    assertEquals(0, filtered4.size());
    list2.add(elem("D"));
    assertEquals(3, filtered1.size());
    assertEquals(4, filtered2.size());
    assertEquals(7, filtered3.size());
    assertEquals(0, filtered4.size());
    list2.add(elem("AB"));
    assertEquals(4, filtered1.size());
    assertEquals(5, filtered2.size());
    assertEquals(8, filtered3.size());
    assertEquals(0, filtered4.size());
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_fromEmptyList1() {
    list1.clear();
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    filtered.get(0);
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_fromEmptyList2() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new AllwaysFalse());
    filtered.get(0);
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_beforeStart() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    filtered.get(-1);
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_afterEnd() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    filtered.get(6);
  }
  
  @Test
  public void testGet_firstElt() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list1, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered3 = new FilteredNodeList<>(list1, new AllwaysTrue());
    final FilteredNodeList<Node> filtered4 = new FilteredNodeList<>(list2, new TwoOrMore());
    final FilteredNodeList<Node> filtered5 = new FilteredNodeList<>(list2, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered6 = new FilteredNodeList<>(list2, new AllwaysTrue());
    assertNodeName("10", filtered1.get(0));
    assertNodeName("A", filtered2.get(0));
    assertNodeName("A", filtered3.get(0));
    assertNodeName("10", filtered4.get(0));
    assertNodeName("A", filtered5.get(0));
    assertNodeName("10", filtered6.get(0));
  }
  
  @Test
  public void testGet_lastElt() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list1, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered3 = new FilteredNodeList<>(list1, new AllwaysTrue());
    final FilteredNodeList<Node> filtered4 = new FilteredNodeList<>(list2, new TwoOrMore());
    final FilteredNodeList<Node> filtered5 = new FilteredNodeList<>(list2, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered6 = new FilteredNodeList<>(list2, new AllwaysTrue());
    assertNodeName("12", filtered1.get(2));
    assertNodeName("C", filtered2.get(2));
    assertNodeName("12", filtered3.get(5));
    assertNodeName("12", filtered4.get(2));
    assertNodeName("C", filtered5.get(2));
    assertNodeName("C", filtered6.get(5));
  }
  
  @Test
  public void testGet_middleElt() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list1, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered3 = new FilteredNodeList<>(list1, new AllwaysTrue());
    final FilteredNodeList<Node> filtered4 = new FilteredNodeList<>(list2, new TwoOrMore());
    final FilteredNodeList<Node> filtered5 = new FilteredNodeList<>(list2, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered6 = new FilteredNodeList<>(list2, new AllwaysTrue());
    assertNodeName("11", filtered1.get(1));
    assertNodeName("B", filtered2.get(1));
    assertNodeName("10", filtered3.get(3));
    assertNodeName("11", filtered4.get(1));
    assertNodeName("B", filtered5.get(1));
    assertNodeName("11", filtered6.get(3));
  }
  
  @Test
  public void testAdd_filtered_added_1() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new TwoOrMore());
    filtered.add(elem("13"));
    assertEquals(4, filtered.size());
    assertEquals(7, list1.size());
    assertNodeName("13", filtered.get(3));
    assertNodeName("13", list1.get(6));
  }
  
  @Test
  public void testAdd_filtered_added_2() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list2, new TwoOrMore());
    filtered.add(elem("13"));
    assertEquals(4, filtered.size());
    assertEquals(7, list2.size());
    assertNodeName("13", filtered.get(3));
    assertNodeName("13", list2.get(6));
  }
  
  @Test
  public void testAdd_filtered_notAdded_1() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    filtered.add(elem("13"));
    assertEquals(3, filtered.size());
    assertEquals(7, list1.size());
    assertNodeName("13", list1.get(6));
  }
  
  @Test
  public void testAdd_filtered_notAdded_2() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list2, new NotStartsWithOne());
    filtered.add(elem("13"));
    assertEquals(3, filtered.size());
    assertEquals(7, list2.size());
    assertNodeName("13", list2.get(6));
  }
  
  @Test
  public void testAdd_original_added_1() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new TwoOrMore());
    list1.add(elem("13"));
    assertEquals(4, filtered.size());
    assertEquals(7, list1.size());
    assertNodeName("13", filtered.get(3));
    assertNodeName("13", list1.get(6));
  }
  
  @Test
  public void testAdd_original_added_2() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list2, new TwoOrMore());
    list2.add(elem("13"));
    assertEquals(4, filtered.size());
    assertEquals(7, list2.size());
    assertNodeName("13", filtered.get(3));
    assertNodeName("13", list2.get(6));
  }
  
  @Test
  public void testRemoveIndex_filtered_1() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new TwoOrMore());
    filtered.remove(2);
    assertEquals(2, filtered.size());
    assertEquals(5, list1.size());
    assertNodeName("10", filtered.get(0));
    assertNodeName("11", filtered.get(1));
    assertNodeName("A", list1.get(0));
    assertNodeName("B", list1.get(1));
    assertNodeName("C", list1.get(2));
    assertNodeName("10", list1.get(3));
    assertNodeName("11", list1.get(4));
    filtered.remove(0);
    assertEquals(1, filtered.size());
    assertEquals(4, list1.size());
    assertNodeName("11", filtered.get(0));
    assertNodeName("A", list1.get(0));
    assertNodeName("B", list1.get(1));
    assertNodeName("C", list1.get(2));
    assertNodeName("11", list1.get(3));
    filtered.remove(0);
    assertEquals(0, filtered.size());
    assertEquals(3, list1.size());
    assertNodeName("A", list1.get(0));
    assertNodeName("B", list1.get(1));
    assertNodeName("C", list1.get(2));
  }
  
  @Test
  public void testRemoveIndex_filtered_2() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list2, new TwoOrMore());
    filtered.remove(2);
    assertEquals(2, filtered.size());
    assertEquals(5, list2.size());
    assertNodeName("10", filtered.get(0));
    assertNodeName("11", filtered.get(1));
    assertNodeName("10", list2.get(0));
    assertNodeName("A", list2.get(1));
    assertNodeName("B", list2.get(2));
    assertNodeName("11", list2.get(3));
    assertNodeName("C", list2.get(4));
    filtered.remove(0);
    assertEquals(1, filtered.size());
    assertEquals(4, list2.size());
    assertNodeName("11", filtered.get(0));
    assertNodeName("A", list2.get(0));
    assertNodeName("B", list2.get(1));
    assertNodeName("11", list2.get(2));
    assertNodeName("C", list2.get(3));
    filtered.remove(0);
    assertEquals(0, filtered.size());
    assertEquals(3, list2.size());
    assertNodeName("A", list2.get(0));
    assertNodeName("B", list2.get(1));
    assertNodeName("C", list2.get(2));
  }
  
  @Test
  public void testRemoveIndex_original_1() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new TwoOrMore());
    list1.remove(0);
    assertEquals(3, filtered.size());
    assertEquals(5, list1.size());
    list1.remove(2);
    assertEquals(2, filtered.size());
    assertEquals(4, list1.size());
    assertNodeName("11", filtered.get(0));
    assertNodeName("12", filtered.get(1));
    assertNodeName("B", list1.get(0));
    assertNodeName("C", list1.get(1));
    assertNodeName("11", list1.get(2));
    assertNodeName("12", list1.get(3));
    list1.remove(3);
    assertEquals(1, filtered.size());
    assertEquals(3, list1.size());
    assertNodeName("11", filtered.get(0));
    assertNodeName("B", list1.get(0));
    assertNodeName("C", list1.get(1));
    assertNodeName("11", list1.get(2));
    list1.remove(2);
    assertEquals(0, filtered.size());
    assertEquals(2, list1.size());
    assertNodeName("B", list1.get(0));
    assertNodeName("C", list1.get(1));
  }
  
  @Test
  public void testRemoveIndex_original_2() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list2, new TwoOrMore());
    list2.remove(1);
    assertEquals(3, filtered.size());
    assertEquals(5, list2.size());
    list2.remove(0);
    assertEquals(2, filtered.size());
    assertEquals(4, list2.size());
    assertNodeName("11", filtered.get(0));
    assertNodeName("12", filtered.get(1));
    assertNodeName("B", list2.get(0));
    assertNodeName("11", list2.get(1));
    assertNodeName("12", list2.get(2));
    assertNodeName("C", list2.get(3));
    list2.remove(2);
    assertEquals(1, filtered.size());
    assertEquals(3, list2.size());
    assertNodeName("11", filtered.get(0));
    assertNodeName("B", list2.get(0));
    assertNodeName("11", list2.get(1));
    assertNodeName("C", list2.get(2));
    list2.remove(0);
    assertEquals(1, filtered.size());
    assertEquals(2, list2.size());
    assertNodeName("11", list2.get(0));
    assertNodeName("C", list2.get(1));
    list2.remove(1);
    assertEquals(1, filtered.size());
    assertEquals(1, list2.size());
    assertNodeName("11", list2.get(0));
    list2.remove(0);
    assertEquals(0, filtered.size());
    assertEquals(0, list2.size());
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void testRemoveIndex_fromEmpty() {
    list1.clear();
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    filtered.remove(0);
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void testRemoveIndex_beforeStart() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    filtered.remove(-1);
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void testRemoveIndex_afterEnd() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    filtered.remove(6);
  }
  
  @Test
  public void testIterator_1() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new TwoOrMore());
    final Iterator<Node> it = filtered.iterator();
    assertTrue(it.hasNext());
    assertNodeName("10", it.next());
    assertTrue(it.hasNext());
    assertNodeName("11", it.next());
    assertTrue(it.hasNext());
    assertNodeName("12", it.next());
    assertFalse(it.hasNext());
  }
  
  @Test
  public void testIterator_2() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new NotStartsWithOne());
    final Iterator<Node> it = filtered.iterator();
    assertTrue(it.hasNext());
    assertNodeName("A", it.next());
    assertTrue(it.hasNext());
    assertNodeName("B", it.next());
    assertTrue(it.hasNext());
    assertNodeName("C", it.next());
    assertFalse(it.hasNext());
  }
  
  @Test
  public void testIterator_3() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list2, new TwoOrMore());
    final Iterator<Node> it = filtered.iterator();
    assertTrue(it.hasNext());
    assertNodeName("10", it.next());
    assertTrue(it.hasNext());
    assertNodeName("11", it.next());
    assertTrue(it.hasNext());
    assertNodeName("12", it.next());
    assertFalse(it.hasNext());
  }
  
  @Test
  public void testIterator_4() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list2, new NotStartsWithOne());
    final Iterator<Node> it = filtered.iterator();
    assertTrue(it.hasNext());
    assertNodeName("A", it.next());
    assertTrue(it.hasNext());
    assertNodeName("B", it.next());
    assertTrue(it.hasNext());
    assertNodeName("C", it.next());
    assertFalse(it.hasNext());
  }
  
  @Test
  public void testIterator_empty_1() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new AllwaysFalse());
    final Iterator<Node> it = filtered.iterator();
    assertFalse(it.hasNext());
  }
  
  @Test
  public void testIterator_empty_2() {
    list1.clear();
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new AllwaysTrue());
    final Iterator<Node> it = filtered.iterator();
    assertFalse(it.hasNext());
  }
  
  @Test(expected = NoSuchElementException.class)
  public void testIterator_afterEnd() {
    final FilteredNodeList<Node> filtered = new FilteredNodeList<>(list1, new AllwaysFalse());
    final Iterator<Node> it = filtered.iterator();
    it.next();
  }
  
  @Test
  public void testIsEmpty() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list1, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered3 = new FilteredNodeList<>(list1, new AllwaysTrue());
    final FilteredNodeList<Node> filtered4 = new FilteredNodeList<>(list1, new AllwaysFalse());
    final FilteredNodeList<Node> filtered5 = new FilteredNodeList<>(list2, new TwoOrMore());
    final FilteredNodeList<Node> filtered6 = new FilteredNodeList<>(list2, new NotStartsWithOne());
    final FilteredNodeList<Node> filtered7 = new FilteredNodeList<>(list2, new AllwaysTrue());
    final FilteredNodeList<Node> filtered8 = new FilteredNodeList<>(list2, new AllwaysFalse());
    
    assertFalse(filtered1.isEmpty());
    assertFalse(filtered2.isEmpty());
    assertFalse(filtered3.isEmpty());
    assertTrue(filtered4.isEmpty());
    assertFalse(filtered5.isEmpty());
    assertFalse(filtered6.isEmpty());
    assertFalse(filtered7.isEmpty());
    assertTrue(filtered8.isEmpty());
  }
  
  @Test
  public void testClear_1() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list2, new TwoOrMore());
    
    filtered1.clear();
    assertTrue(filtered1.isEmpty());
    assertEquals(3, list1.size());
    assertNodeName("A", list1.get(0));
    assertNodeName("B", list1.get(1));
    assertNodeName("C", list1.get(2));
    
    filtered2.clear();
    assertTrue(filtered2.isEmpty());
    assertEquals(3, list2.size());
    assertNodeName("A", list2.get(0));
    assertNodeName("B", list2.get(1));
    assertNodeName("C", list2.get(2));
  }
  
  @Test
  public void testClear_2() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new AllwaysTrue());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list2, new AllwaysFalse());
    
    filtered1.clear();
    assertTrue(filtered1.isEmpty());
    assertEquals(0, list1.size());
    
    filtered2.clear();
    assertTrue(filtered2.isEmpty());
    assertEquals(6, list2.size());
  }
  
  @Test
  public void testContains_1() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new TwoOrMore());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list2, new TwoOrMore());
    assertTrue(filtered1.contains(ELEM_10));
    assertTrue(filtered1.contains(ELEM_11));
    assertTrue(filtered1.contains(ELEM_12));
    assertTrue(filtered2.contains(ELEM_10));
    assertTrue(filtered2.contains(ELEM_11));
    assertTrue(filtered2.contains(ELEM_12));
    assertFalse(filtered1.contains(ELEM_A));
    assertFalse(filtered1.contains(ELEM_B));
    assertFalse(filtered1.contains(ELEM_C));
    assertFalse(filtered2.contains(ELEM_A));
    assertFalse(filtered2.contains(ELEM_B));
    assertFalse(filtered2.contains(ELEM_C));
  }
  
  @Test
  public void testContains_2() {
    final FilteredNodeList<Node> filtered1 = new FilteredNodeList<>(list1, new AllwaysTrue());
    final FilteredNodeList<Node> filtered2 = new FilteredNodeList<>(list2, new AllwaysFalse());
    assertTrue(filtered1.contains(ELEM_10));
    assertTrue(filtered1.contains(ELEM_11));
    assertTrue(filtered1.contains(ELEM_12));
    assertFalse(filtered2.contains(ELEM_10));
    assertFalse(filtered2.contains(ELEM_11));
    assertFalse(filtered2.contains(ELEM_12));
    assertTrue(filtered1.contains(ELEM_A));
    assertTrue(filtered1.contains(ELEM_B));
    assertTrue(filtered1.contains(ELEM_C));
    assertFalse(filtered2.contains(ELEM_A));
    assertFalse(filtered2.contains(ELEM_B));
    assertFalse(filtered2.contains(ELEM_C));
  }
  
  static DefaultElement elem(String name) {
    return new DefaultElement(name);
  }
  
  static void assertNodeName(String name, Node node) {
    assertEquals(name, node.getName());
  }
  
  static final class TwoOrMore implements Predicate<Node>
  {
    @Override
    public boolean test(Node node) {
      final String entry = node != null ? node.getName() : null;
      return entry != null && entry.length() > 1;
    }
  }
  
  static final class NotStartsWithOne implements Predicate<Node>
  {
    @Override
    public boolean test(Node node) {
      final String entry = node != null ? node.getName() : null;
      return entry != null && !entry.isEmpty() && !entry.startsWith("1");
    }
  }
  
  static final class AllwaysFalse implements Predicate<Node>
  {
    @Override
    public boolean test(Node entry) {
      return false;
    }
  }
  
  static final class AllwaysTrue implements Predicate<Node>
  {
    @Override
    public boolean test(Node entry) {
      return true;
    }
  }
}
