package org.noemus.neoxml.io;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.noemus.neoxml.Document;
import org.noemus.neoxml.DocumentHelper;
import org.noemus.neoxml.Element;
import org.noemus.neoxml.io.OutputFormat;
import org.noemus.neoxml.io.XMLWriter;

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
