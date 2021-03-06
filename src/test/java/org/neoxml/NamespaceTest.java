/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Before;
import org.junit.Test;
import org.neoxml.io.SAXReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A test harness to test the use of Namespaces.
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.4 $
 */
public class NamespaceTest extends AbstractTestCase {
    /**
     * Input XML file to read
     */
    private static final String INPUT_XML_FILE = "/src/test/xml/namespaces.xml";

    /**
     * Namespace to use in tests
     */
    private static final Namespace XSL_NAMESPACE = Namespace.get("xsl", "http://www.w3.org/1999/XSL/Transform");

    private static final QName XSL_TEMPLATE = QName.get("template", XSL_NAMESPACE);

    public void debugShowNamespaces() {
        Element root = getRootElement();

        for (Iterator<Element> iter = root.elementIterator(); iter.hasNext(); ) {
            Element element = iter.next();

            log.debug("Found element:    {}", element);
            log.debug("Namespace:        {}", element.getNamespace());
            log.debug("Namespace prefix: {}", element.getNamespacePrefix());
            log.debug("Namespace URI:    {}", element.getNamespaceURI());
        }
    }

    @Test
    public void testGetElement() {
        Element root = getRootElement();

        Element firstTemplate = root.element(XSL_TEMPLATE);
        assertNotNull("Root element contains at least one <xsl:template/> element", firstTemplate);

        log.debug("Found element: {}", firstTemplate);
    }

    @Test
    public void testGetElements() {
        Element root = getRootElement();

        List<Element> list = root.elements(XSL_TEMPLATE);
        assertTrue("Root element contains at least one <xsl:template/> element", list.size() > 0);

        log.debug("Found elements: {}", list);
    }

    @Test
    public void testElementIterator() {
        Element root = getRootElement();
        Iterator<Element> iter = root.elementIterator(XSL_TEMPLATE);
        assertTrue("Root element contains at least one <xsl:template/> element", iter.hasNext());

        do {
            Element element = iter.next();
            log.debug("Found element: {}", element);
        }
        while (iter.hasNext());
    }

    /**
     * Tests the use of namespace URI Mapping associated with a DefaultDocumentFactory
     *
     * @throws Exception DOCUMENT ME!
     */
    @Test
    public void testNamespaceUriMap() throws Exception {
        // register namespace prefix->uri mappings with factory
        Map<String,String> uris = new HashMap<>();
        uris.put("x", "fooNamespace");
        uris.put("y", "barNamespace");

        DocumentFactory factory = new DefaultDocumentFactory();
        factory.setXPathNamespaceURIs(uris);

        // parse or create a document
        SAXReader reader = new SAXReader();
        reader.setDocumentFactory(factory);

        Document doc = getDocument("/src/test/xml/test/nestedNamespaces.xml", reader);

        // evaluate XPath using registered namespace prefixes
        // which do not appear in the document (though the URIs do!)
        String value = doc.valueOf("/x:pizza/y:cheese/x:pepper");

        log.debug("Found value: {}", value);

        assertEquals("XPath used default namesapce URIS", "works", value);
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        document = getDocument(INPUT_XML_FILE);
    }

    /**
     * @return the root element of the document
     */
    @Override
    protected Element getRootElement() {
        Element root = document.getRootElement();
        assertNotNull("Document has root element", root);
        return root;
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
