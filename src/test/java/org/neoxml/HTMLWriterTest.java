/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Test;
import org.neoxml.io.HTMLWriter;
import org.neoxml.io.OutputFormat;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test harness for the HTMLWriter
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.4 $
 */
public class HTMLWriterTest extends AbstractTestCase {
    @Test
    public void testWriter() throws Exception {
        String xml = "<html> <body><![CDATA[First&nbsp;test]]></body> </html>";
        Document document = DocumentHelper.parseText(xml);
        StringWriter buffer = new StringWriter();
        HTMLWriter writer = new HTMLWriter(buffer);
        writer.write(document);

        String output = buffer.toString();

        String expects = "\n<html>\n  <body>First&nbsp;test</body>\n</html>\n";

        log.info("expects: {}", expects);
        log.info("output: {}", output);

        assertEquals("Output is correct", expects, output);
    }

    @Test
    public void testBug923882() throws Exception {
        Document doc = DefaultDocumentFactory.getInstance().createDocument();
        Element root = doc.addElement("root");
        root.addText("this is ");
        root.addText(" sim");
        root.addText("ple text ");
        root.addElement("child");
        root.addText(" contai");
        root.addText("ning spaces and");
        root.addText(" multiple textnodes");

        OutputFormat format = new OutputFormat();
        format.setEncoding("UTF-8");
        format.setIndentSize(4);
        format.setNewlines(true);
        format.setTrimText(true);
        format.setExpandEmptyElements(true);

        StringWriter buffer = new StringWriter();
        try (HTMLWriter writer = new HTMLWriter(buffer, format)) {
            writer.write(doc);
        }

        String xml = buffer.toString();
        log.debug(xml);

        int start = xml.indexOf("<root");
        int end = xml.indexOf("/root>") + 6;
        String expected = "<root>this is simple text<child></child>containing spaces and multiple textnodes</root>";
        log.info("Expected:");
        log.info(expected);
        log.info("Obtained:");
        log.info(xml.substring(start, end));
        assertEquals(expected, xml.substring(start, end));
    }

    @Test
    public void testBug923882asWriter() throws Exception {
        // use an the HTMLWriter sax-methods.
        //
        StringWriter buffer = new StringWriter();
        try (HTMLWriter writer = new HTMLWriter(buffer, OutputFormat.createPrettyPrint())) {
            writer.characters("wor".toCharArray(), 0, 3);
            writer.characters("d-being-cut".toCharArray(), 0, 11);
        }

        String expected = "word-being-cut";
        assertEquals(expected, buffer.toString());

        buffer = new StringWriter();
        try (HTMLWriter writer = new HTMLWriter(buffer, OutputFormat.createPrettyPrint())) {
            writer.characters("    wor".toCharArray(), 0, 7);
            writer.characters("d being    ".toCharArray(), 0, 11);
            writer.characters("  cut".toCharArray(), 0, 5);
        }

        expected = "word being cut";
        assertEquals(expected, buffer.toString());
    }

    @Test
    public void testBug923882asWriterWithEmptyCharArray() throws Exception {
        // use an the HTMLWriter sax-methods.
        StringWriter buffer = new StringWriter();
        try (HTMLWriter writer = new HTMLWriter(buffer, OutputFormat.createPrettyPrint())) {
            writer.characters("wor".toCharArray(), 0, 3);
            writer.characters(new char[0], 0, 0);
            writer.characters("d-being-cut".toCharArray(), 0, 11);
        }

        String expected = "word-being-cut";
        assertEquals(expected, buffer.toString());
    }

    @Test
    public void testBug619415() throws Exception {
        Document doc = getDocument("/src/test/xml/test/dosLineFeeds.xml");

        StringWriter wr = new StringWriter();
        HTMLWriter writer = new HTMLWriter(wr, new OutputFormat("", false));
        writer.write(doc);

        String result = wr.toString();
        log.info(result);

        assertTrue(result.contains("Mary had a little lamb."));
        assertTrue(result.contains("Hello, this is a test."));
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
