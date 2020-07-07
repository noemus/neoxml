/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Test;
import org.neoxml.io.SAXReader;
import org.neoxml.rule.Pattern;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Performs a number of unit test cases on the XPath engine
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.5 $
 */
public class XPathExamplesTest extends AbstractTestCase {
    protected SAXReader xmlReader = new SAXReader();

    /**
     * The document on which the tests are being run
     */
    protected Document testDocument;

    /**
     * The context node on which the tests are being run
     */
    protected Node testContext;

    /**
     * factory for XPath, Patterns and nodes
     */
    protected DocumentFactory factory = DefaultDocumentFactory.getInstance();

    @Test
    public void testXPaths() throws Exception {
        Document document = getDocument("/src/test/xml/test/xpath/tests.xml");
        Element root = document.getRootElement();

        Iterator<Element> iter = root.elementIterator("document");

        while (iter.hasNext()) {
            Element documentTest = iter.next();
            testDocument(documentTest);
        }
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected void testDocument(Element documentTest) throws Exception {
        String url = documentTest.attributeValue("url");
        testDocument = xmlReader.read(getFile(url));
        assertNotNull("Loaded test document: " + url, testDocument);

        log.debug("Loaded document: " + url);

        for (Iterator<Element> iter = documentTest.elementIterator("context"); iter.hasNext(); ) {
            Element context = iter.next();
            testContext(documentTest, context);
        }
    }

    protected void testContext(Element documentTest, Element context) {
        String xpath = context.attributeValue("select");

        List<Node> list = testDocument.selectNodes(xpath);

        assertTrue("Found at least one context nodes to test for path: "
                           + xpath, (list != null) && (list.size() > 0));

        for (Node node : list) {
            assertNotNull("Context node is a Node: " + node, node);
            testContext = node;

            log.debug("Context is now: {}", testContext);
            runTests(documentTest, context);
            log.debug("");
        }
    }

    protected void runTests(Element documentTest, Element context) {
        for (Iterator<Element> iter = context.elementIterator("test"); iter.hasNext(); ) {
            Element test = iter.next();
            runTest(documentTest, context, test);
        }

        for (Iterator<Element> it = context.elementIterator("valueOf"); it.hasNext(); ) {
            Element valueOf = it.next();
            testValueOf(documentTest, context, valueOf);
        }

        for (Iterator<Element> it = context.elementIterator("pattern"); it.hasNext(); ) {
            Element pattern = it.next();
            testPattern(documentTest, context, pattern);
        }

        Iterator<Element> it = context.elementIterator("fileter");

        while (it.hasNext()) {
            Element filter = it.next();
            testFilter(documentTest, context, filter);
        }
    }

    protected void runTest(Element documentTest, Element context, Element test) {
        String xpath = test.attributeValue("select");

        String description = "Path: " + xpath;

        String exception = test.attributeValue("exception");
        if ((exception != null) && exception.equals("true")) {
            try {
                testContext.selectNodes(xpath);
                fail("Exception was not thrown");
            } catch (XPathException e) {
                return;
            }
        }

        String count = test.attributeValue("count");

        if (count != null) {
            int expectedSize = Integer.parseInt(count);
            List<Node> results = testContext.selectNodes(xpath);

            log.debug(description + " found result size: " + results.size());

            assertEquals(description + " wrong result size", expectedSize, results.size());
        }

        Element valueOf = test.element("valueOf");

        if (valueOf != null) {
            Node node = testContext.selectSingleNode(xpath);
            assertNotNull(description + " found node", node);

            String expected = valueOf.getText();
            String result = node.valueOf(valueOf.attributeValue("select"));

            log.debug(description);
            log.debug("\texpected: " + expected + " result: " + result);

            assertEquals(description, expected, result);
        }
    }

    protected void testValueOf(Element documentTest, Element context, Element valueOf) {
        String xpath = valueOf.attributeValue("select");
        String description = "valueOf: " + xpath;

        String expected = valueOf.getText();
        String result = testContext.valueOf(xpath);

        log.debug(description);
        log.debug("\texpected: " + expected + " result: " + result);

        assertEquals(description, expected, result);
    }

    protected void testPattern(Element documentTest, Element context, Element patternElement) {
        String match = patternElement.attributeValue("match");
        String description = "match: " + match;

        log.debug("");
        log.debug(description);

        Pattern pattern = factory.createPattern(match);

        assertTrue(description, pattern.matches(testContext));
    }

    protected void testFilter(Element documentTest, Element context, Element pattern) {
        String match = pattern.attributeValue("match");
        String description = "match: " + match;

        assertTrue(description, testContext.matches(match));
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
