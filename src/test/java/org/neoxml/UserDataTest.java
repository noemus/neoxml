/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;
import org.neoxml.io.SAXReader;
import org.neoxml.util.UserDataAttribute;
import org.neoxml.util.UserDataDocumentFactory;
import org.neoxml.util.UserDataElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests the UserDataDocumentFactory
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.4 $
 */
public class UserDataTest extends AbstractTestCase {
    private static final String INPUT_XML_FILE = "/src/test/xml/web.xml";

    private final Object userData = 1.23456;

    public static void main(String[] args) throws InitializationError {
        new JUnit4(UserDataTest.class).run(null);
    }

    @Test
    public void testSetData() {
        Element root = getRootElement();

        assertTrue("Element instanceof UserDataElement", root instanceof UserDataElement);

        root.setData(userData);

        assertSame("Stored user data!", root.getData(), userData);

        log.debug("root: {}", root);

        assertUserData(root, userData);

        Element cloned = (Element) root.clone();
        assertNotSame("Cloned new instance", cloned, root);
        assertUserData(cloned, userData);

        cloned = root.createCopy();
        assertNotSame("Cloned new instance", cloned, root);
        assertUserData(cloned, userData);
    }

    @Test
    public void testCloneAttribute() {
        Element root = getRootElement();
        root.addAttribute("name", "value");

        Attribute attribute = root.attribute("name");
        assertTrue(attribute instanceof UserDataAttribute);

        Element cloned = (Element) root.clone();
        Attribute clonedAttribute = cloned.attribute("name");
        assertTrue(clonedAttribute instanceof UserDataAttribute);
    }

    @Test
    public void testNewAdditions() {
        Element root = getRootElement();

        Element newElement = root.addElement("foo1234");
        assertTrue("New Element is a UserDataElement", newElement instanceof UserDataElement);

        root.addAttribute("bar456", "123");

        Attribute newAttribute = root.attribute("bar456");

        assertTrue("New Attribute is a UserDataAttribute", newAttribute instanceof UserDataAttribute);
    }

    @Test
    public void testNewDocument() {
        DocumentFactory factory = UserDataDocumentFactory.getInstance();
        Document document = factory.createDocument();

        Element root = document.addElement("root");
        assertTrue("Root Element is a UserDataElement", root instanceof UserDataElement);

        Element newElement = root.addElement("foo1234");
        assertTrue("New Element is a UserDataElement", newElement instanceof UserDataElement);

        root.addAttribute("bar456", "123");

        Attribute newAttribute = root.attribute("bar456");

        assertTrue("New Attribute is a UserDataAttribute", newAttribute instanceof UserDataAttribute);
    }

    protected void assertUserData(Element root, Object data) {
        Object result = root.getData();

        assertNotNull("No user data!", result);
        assertEquals("Stored user data correctly", data, result);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        SAXReader reader = new SAXReader();
        reader.setDocumentFactory(UserDataDocumentFactory.getInstance());
        document = getDocument(INPUT_XML_FILE, reader);
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
