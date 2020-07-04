package org.neoxml.util;

import org.junit.Test;
import org.xml.sax.InputSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DTDEntityResolverTest
{
  @Test
  public void test_plainEntity() {
    final InputSource is = new DTDEntityResolver().resolveEntity("PUB", "sample.dtd");
    assertEquals("PUB", is.getPublicId());
    assertEquals("sample.dtd", is.getSystemId());
  }


  @Test
  public void test_httpEntity() {
    final InputSource is = new DTDEntityResolver().resolveEntity("PUB", "http://sample.dtd");
    assertEquals("PUB", is.getPublicId());
    assertEquals("sample.dtd", is.getSystemId());
  }

  @Test
  public void test_nullEntity() {
    assertNull(new DTDEntityResolver().resolveEntity("PUB", null));
  }

  @Test
  public void test_missingEntity() {
    assertNull(new DTDEntityResolver().resolveEntity("PUB", "missing.dtd"));
  }
}
