package org.xtree.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xtree.Element;
import org.xtree.QName;
import org.xtree.bean.BeanElement;

public class BeanElementTest
{
  @Test
  public void testAddAttribute() {
    final QName bean = new QName("foo");
    final QName attr = new QName("attr");

    final BeanElement elem = new BeanElement(bean, new Foo());

    m(elem, attr);

    assertEquals("value", elem.attributeValue(attr));
  }

  void m(Element elem, QName name) {
    elem.addAttribute(name, "value");
  }

  public static class Foo
  {
    private String attr;

    public Foo() {}

    public String getAttr() {
      return this.attr;
    }

    public void setAttr(String attr) {
      this.attr = attr;
    }
  }
}
