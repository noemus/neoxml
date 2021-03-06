/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Test;
import org.neoxml.io.OutputFormat;
import org.neoxml.io.XMLWriter;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class LineFeedTest extends AbstractTestCase {
    private static final String ATT_TEXT = "Hello&#xa;There&#xa;&lt;&gt;&amp;";

    private static final String TEXT = "Hello\nThere\n&lt;&gt;&amp;";

    private static final String EXPECTED_TEXT = "Hello\nThere\n<>&";

    private static final String EXPECTED_ATT_TEXT = "Hello There <>&";

    @Test
    public void testElement() throws Exception {
        Document doc = DocumentHelper.parseText("<elem>" + TEXT + "</elem>");
        Element elem = doc.getRootElement();
        assertEquals(EXPECTED_TEXT, elem.getText());
    }

    @Test
    public void testAttribute() throws Exception {
        Document doc = DocumentHelper.parseText("<elem attr=\"" + TEXT + "\"/>");
        Element elem = doc.getRootElement();

        assertEquals(EXPECTED_ATT_TEXT, elem.attributeValue("attr"));

        doc = DocumentHelper.parseText("<elem attr=\"" + ATT_TEXT + "\"/>");
        elem = doc.getRootElement();

        assertEquals(EXPECTED_TEXT, elem.attributeValue("attr"));
    }

    @Test
    public void testCDATA() throws Exception {
        Document doc = DocumentHelper.parseText("<elem><![CDATA[" + EXPECTED_TEXT + "]]></elem>");
        Element elem = doc.getRootElement();

        assertEquals(EXPECTED_TEXT, elem.getText());
    }

    @Test
    public void testXmlWriter() throws Exception {
        Element elem = DocumentHelper.createElement("elem");
        Document doc = DocumentHelper.createDocument(elem);
        elem.addCDATA(EXPECTED_TEXT);

        StringWriter sw = new StringWriter();
        XMLWriter xWriter = new XMLWriter(sw, OutputFormat.createPrettyPrint());
        xWriter.write(doc);
        xWriter.close();

        String xmlString = sw.toString();
        doc = DocumentHelper.parseText(xmlString);
        elem = doc.getRootElement();

        assertEquals(EXPECTED_TEXT, elem.getText());
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
