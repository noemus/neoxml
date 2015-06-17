package cz.tdp.xtree.io;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

import cz.tdp.xtree.Document;
import cz.tdp.xtree.DocumentHelper;
import cz.tdp.xtree.Element;
import cz.tdp.xtree.io.OutputFormat;
import cz.tdp.xtree.io.XMLWriter;

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
