package org.neoxml.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.neoxml.Document;

public class XMLHelperTest
{

  @Test
  public void testCreateDocumentFromResourceString() throws Exception {
    final Document doc = XMLHelper.createDocumentFromResource("test.xml");
    assertEquals("root", doc.getRootElement().getName());
  }

  /*
  @Test
  public void testCreateDocumentFromResourceStringString() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateDocumentFromReaderReader() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateDocumentFromReaderReaderString() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateDocumentFromStreamInputStream() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateDocumentFromStreamInputStreamString() {
    fail("Not yet implemented");
  }

  @Test
  public void testWriteDocumentDocumentPath() {
    fail("Not yet implemented");
  }

  @Test
  public void testWriteDocumentDocumentPathString() {
    fail("Not yet implemented");
  }

  @Test
  public void testWriteDocumentDocumentFile() {
    fail("Not yet implemented");
  }

  @Test
  public void testWriteDocumentDocumentFileString() {
    fail("Not yet implemented");
  }

  @Test
  public void testWriteDocumentDocumentOutputStream() {
    fail("Not yet implemented");
  }

  @Test
  public void testWriteDocumentDocumentOutputStreamString() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateWriterPath() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateWriterPathString() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateWriterFile() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateWriterFileString() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateWriterOutputStream() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateWriterOutputStreamString() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateWriterOutputStreamStringOutputFormat() {
    fail("Not yet implemented");
  }

  @Test
  public void testDefaultOutputFormat() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateSAXReader() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateDOMReader() {
    fail("Not yet implemented");
  }
  */
}
