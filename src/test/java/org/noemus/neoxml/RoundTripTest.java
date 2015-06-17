/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.noemus.neoxml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;
import org.noemus.neoxml.Document;
import org.noemus.neoxml.io.DOMReader;
import org.noemus.neoxml.io.DOMWriter;
import org.noemus.neoxml.io.DocumentResult;
import org.noemus.neoxml.io.DocumentSource;
import org.noemus.neoxml.io.SAXContentHandler;
import org.noemus.neoxml.io.SAXReader;
import org.noemus.neoxml.io.SAXWriter;
import org.noemus.neoxml.io.XMLWriter;

/**
 * A test harness to test the the round trips of Documents.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.4 $
 */
public class RoundTripTest extends AbstractTestCase
{
  protected String[] testDocuments = {
    "/src/test/xml/test/encode.xml",
    "/src/test/xml/fibo.xml", "/src/test/xml/test/schema/personal-prefix.xsd",
    "/src/test/xml/test/soap2.xml", "/src/test/xml/test/test_schema.xml"
  };

  public static void main(String[] args) throws InitializationError {
    new JUnit4(RoundTripTest.class).run(null);
  }

  // Test case(s)
  // -------------------------------------------------------------------------

  @Test
  public void testTextRoundTrip() throws Exception {
    for (int i = 0, size = testDocuments.length; i < size; i++) {
      Document doc = getDocument(testDocuments[i]);
      roundTripText(doc);
    }
  }

  @Test
  public void testSAXRoundTrip() throws Exception {
    for (int i = 0, size = testDocuments.length; i < size; i++) {
      Document doc = getDocument(testDocuments[i]);
      roundTripSAX(doc);
    }
  }

  @Test
  public void testDOMRoundTrip() throws Exception {
    for (int i = 0, size = testDocuments.length; i < size; i++) {
      Document doc = getDocument(testDocuments[i]);
      roundTripDOM(doc);
    }
  }

  @Test
  public void testJAXPRoundTrip() throws Exception {
    for (int i = 0, size = testDocuments.length; i < size; i++) {
      Document doc = getDocument(testDocuments[i]);
      roundTripJAXP(doc);
    }
  }

  @Test
  public void testFullRoundTrip() throws Exception {
    for (int i = 0, size = testDocuments.length; i < size; i++) {
      Document doc = getDocument(testDocuments[i]);
      roundTripFull(doc);
    }
  }

  @Test
  public void testRoundTrip() throws Exception {
    Document document = getDocument("/src/test/xml/xmlspec.xml");

    // Document doc1 = roundTripText( document );
    Document doc1 = roundTripSAX(document);
    Document doc2 = roundTripDOM(doc1);
    Document doc3 = roundTripSAX(doc2);
    Document doc4 = roundTripText(doc3);
    Document doc5 = roundTripDOM(doc4);

    // Document doc5 = roundTripDOM( doc3 );
    assertDocumentsEqual(document, doc5);
  }

  // Implementation methods
  // -------------------------------------------------------------------------

  protected Document roundTripDOM(Document document) throws Exception {
    // now lets make a DOM object
    DOMWriter domWriter = new DOMWriter();
    org.w3c.dom.Document domDocument = domWriter.write(document);

    // now lets read it back as a DOM4J object
    DOMReader domReader = new DOMReader();
    Document newDocument = domReader.read(domDocument);

    // lets ensure names are same
    newDocument.setName(document.getName());

    assertDocumentsEqual(document, newDocument);

    return newDocument;
  }

  protected Document roundTripJAXP(Document document) throws Exception {
    // output the document to a text buffer via JAXP
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();

    StringWriter buffer = new StringWriter();
    StreamResult streamResult = new StreamResult(buffer);
    DocumentSource documentSource = new DocumentSource(document);

    transformer.transform(documentSource, streamResult);

    // now lets parse it back again via JAXP
    DocumentResult documentResult = new DocumentResult();
    StreamSource streamSource = new StreamSource(new StringReader(buffer
      .toString()));

    transformer.transform(streamSource, documentResult);

    Document newDocument = documentResult.getDocument();

    // lets ensure names are same
    newDocument.setName(document.getName());

    assertDocumentsEqual(document, newDocument);

    return newDocument;
  }

  protected Document roundTripSAX(Document document) throws Exception {
    // now lets write it back as SAX events to
    // a SAX ContentHandler which should build up a new document
    SAXContentHandler contentHandler = new SAXContentHandler();
    SAXWriter saxWriter = new SAXWriter(contentHandler, contentHandler,
      contentHandler);

    saxWriter.write(document);

    Document newDocument = contentHandler.getDocument();

    // lets ensure names are same
    newDocument.setName(document.getName());

    assertDocumentsEqual(document, newDocument);

    return newDocument;
  }

  protected Document roundTripText(Document document) throws Exception {
    StringWriter out = new StringWriter();
    XMLWriter xmlWriter = new XMLWriter(out);

    xmlWriter.write(document);

    // now lets read it back
    String xml = out.toString();

    StringReader in = new StringReader(xml);
    SAXReader reader = new SAXReader();
    Document newDocument = reader.read(in);

    // lets ensure names are same
    newDocument.setName(document.getName());

    assertDocumentsEqual(document, newDocument);

    return newDocument;
  }

  protected Document roundTripFull(Document document) throws Exception {
    Document doc2 = roundTripDOM(document);
    Document doc3 = roundTripSAX(doc2);
    Document doc4 = roundTripText(doc3);

    assertDocumentsEqual(document, doc4);

    return doc4;
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
