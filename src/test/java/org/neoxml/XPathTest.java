/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Test;
import org.neoxml.tree.DefaultElement;
import org.neoxml.xpath.DefaultXPath;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A test harness to test XPath expression evaluation in DOM4J
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.4 $
 */
public class XPathTest extends AbstractTestCase {
    protected static String[] paths = {
            ".",
            "*",
            "/",
            "/.",
            "/*",
            "/node()",
            "/child::node()",
            "/self::node()",
            "root",
            "/root",
            "/root/author",
            "text()",
            "//author",
            "//author/text()",
            "//@location",
            "//attribute::*",
            "//namespace::*",
            "normalize-space(/root)",
            "//author[@location]",
            "//author[@location='UK']",
            "root|author",
            "//*[.='James Strachan']",
            "//root/author[1]",
            "normalize-space(/root/author)",
            "normalize-space(' a  b  c  d ')",
            "//root|//author[1]|//author[2]",
            "//root/author[2]",
            "//root/author[3]"
    };

    @Test
    public void testBug1116471() throws Exception {
        String xml = "<a><b>Water T &amp; D-46816</b></a>";
        String expected = "Water T & D-46816";

        Document doc = DocumentHelper.parseText(xml);
        String result = (String) doc.selectObject("string(a/b[1])");

        assertEquals("xpath result not correct", expected, result);

        Node node = doc.selectSingleNode("a/b");
        String result2 = node.getStringValue();

        assertEquals("xpath result not correct", expected, result2);
    }

    @Test
    public void testXPaths() {
        for (String path : paths) {
            testXPath(path);
        }
    }

    @Test
    public void testCreateXPathBug() {
        Element element = new DefaultElement("foo");
        XPath xpath = element.createXPath("//bar");

        assertNotNull("created a valid XPath", xpath);
    }

    @Test
    public void testBug857704() throws Exception {
        Document doc = DocumentHelper.parseText("<foo xmlns:bar='http://blort'/>");
        // shouldn't throw NPE
        assertNotNull(doc.selectNodes("//*[preceding-sibling::*]"));
    }

    @Test
    public void testBooleanValueOf() throws Exception {
        Document doc = DocumentHelper.parseText("<root><foo>blah</foo></root>");

        XPath path = new DefaultXPath("//root");
        assertTrue(path.booleanValueOf(doc));

        path = new DefaultXPath("//root2");
        assertFalse(path.booleanValueOf(doc));
    }

    protected void testXPath(String xpathExpression) {
        log.debug("Searched path: " + xpathExpression);

        XPath xpath = DocumentHelper.createXPath(xpathExpression);

        List<Node> list = xpath.selectNodes(document);

        if (list == null) {
            log.debug("null");
        } else {
            log.debug("[");

            for (Node node : list) {
                String text = node != null ? node.asXML() : "null";
                log.debug("    " + text);
            }

            log.debug("]");
        }

        log.debug("...........................................");
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
