package org.neoxml.io;

import org.junit.Test;
import org.neoxml.Document;
import org.neoxml.DocumentHelper;
import org.neoxml.Element;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class XMLWriterTest
{

  /**
   * Test that it doesn throw exeption
   * TODO: add test cases for full coverage
   */
  @Test
  public void testWriteDocument() {
    final Writer out = new StringWriter();
    try (XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint())) {
      Document doc = DocumentHelper.createDocument();
      Element rootElem = doc.addElement("root");
      rootElem.add(DocumentHelper.createText(""));

      writer.write(doc);  // <== throws StringIndexOutOfBoundsException !!!
      writer.close();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
