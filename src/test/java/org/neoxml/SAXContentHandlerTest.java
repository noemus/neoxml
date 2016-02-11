/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;
import org.neoxml.Document;
import org.neoxml.Element;
import org.neoxml.Node;
import org.neoxml.NodeType;
import org.neoxml.io.SAXContentHandler;
import org.xml.sax.XMLReader;

public class SAXContentHandlerTest extends AbstractTestCase
{
  private XMLReader xmlReader;

  protected String[] testDocuments = {
    "/src/test/xml/test/test_schema.xml",
    "/src/test/xml/test/encode.xml", "/src/test/xml/fibo.xml",
    "/src/test/xml/test/schema/personal-prefix.xsd", "/src/test/xml/test/soap2.xml"
  };

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);

    SAXParser parser = spf.newSAXParser();
    xmlReader = parser.getXMLReader();
  }

  @Test
  public void testSAXContentHandler() throws Exception {
    SAXContentHandler contentHandler = new SAXContentHandler();
    xmlReader.setContentHandler(contentHandler);
    xmlReader.setDTDHandler(contentHandler);
    xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",
      contentHandler);

    for (int i = 0, size = testDocuments.length; i < size; i++) {
      Document docFromSAXReader = getDocument(testDocuments[i]);

      xmlReader.parse(getFile(testDocuments[i]).toURI().toURL().toExternalForm());

      Document docFromSAXContentHandler = contentHandler.getDocument();

      docFromSAXContentHandler.setName(docFromSAXReader.getName());

      assertDocumentsEqual(docFromSAXReader, docFromSAXContentHandler);
      assertEquals(docFromSAXReader.asXML(), docFromSAXContentHandler
        .asXML());
    }
  }

  @Test
  public void testBug926713() throws Exception {
    Document doc = getDocument("/src/test/xml/test/cdata.xml");
    Element foo = doc.getRootElement();
    Element bar = foo.element("bar");
    List content = bar.content();
    assertEquals(3, content.size());
    assertEquals(NodeType.TEXT_NODE, ((Node)content.get(0)).getNodeTypeEnum());
    assertEquals(NodeType.CDATA_SECTION_NODE, ((Node)content.get(1))
      .getNodeTypeEnum());
    assertEquals(NodeType.TEXT_NODE, ((Node)content.get(2)).getNodeTypeEnum());
  }
}

/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. The name "DOM4J" must not be used to endorse or promote products derived
 * from this Software without prior written permission of MetaStuff, Ltd. For
 * written permission, please contact dom4j-info@metastuff.com.
 * 4. Products derived from this Software may not be called "DOM4J" nor may
 * "DOM4J" appear in their names without prior written permission of MetaStuff,
 * Ltd. DOM4J is a registered trademark of MetaStuff, Ltd.
 * 5. Due credit should be given to the DOM4J Project - http://dom4j.sourceforge.net
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 */
