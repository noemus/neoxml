/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Test;
import org.neoxml.dom.DOMDocument;
import org.neoxml.dom.DOMDocumentFactory;
import org.neoxml.util.NodeComparator;

import static org.junit.Assert.*;

/**
 * A test harness to test the clone() methods on Nodes
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.6 $
 */
public class CloneTest extends AbstractTestCase {
    @Test
    public void testBug1148333() {
        DOMDocumentFactory factory = DOMDocumentFactory.getInstance();
        DOMDocument doc = (DOMDocument) factory.createDocument();
        Element el = doc.addElement("root");
        el.addNamespace("pref2", "uri2");

        DOMDocument clone = (DOMDocument) doc.cloneNode(true);

        assertNotSame(doc, clone);
        assertNodesEqual(doc, clone);
    }

    @Test
    public void testElementWithNamespaceClone() {
        Element element = DefaultDocumentFactory.getInstance().createElement("element");
        element.addNamespace("prefix", "uri");
        Element clone = (Element) element.clone();

        assertNotSame(element, clone);
        assertNodesEqual(element, clone);
    }

    @Test
    public void testDocumentClone() throws Exception {
        document.setName("doc1");

        Document doc2 = (Document) document.clone();

        assertNotSame(document, doc2);
        assertNodesEqual(document, doc2);
    }

    @Test
    public void testAddCloneToOtherElement() {
        DocumentFactory factory = DefaultDocumentFactory.getInstance();
        Document doc = factory.createDocument();
        Element root = doc.addElement("root");
        Element parent1 = root.addElement("parent");
        Element child1 = parent1.addElement("child");

        Element parent2 = (Element) parent1.clone();
        root.add(parent2);

        assertSame("parent not correct", root, parent2.getParent());
        assertSame("document not correct", doc, parent2.getDocument());

        Element child2 = parent2.element("child");

        assertNotSame("child not cloned", child1, child2);
        assertNotNull("null child", child2);
        assertSame("parent not correct", parent2, child2.getParent());
        assertSame("document not correct", doc, child2.getDocument());
    }

    @Test
    public void testRootElementClone() throws Exception {
        testElementClone(document.getRootElement());
    }

    @Test
    public void testAuthorElementClone() throws Exception {
        testElementClone((Element) document.selectSingleNode("//author"));
    }

    @Test
    public void testRootCompare1() throws Exception {
        Document doc2 = (Document) document.clone();
        Element author = doc2.getRootElement();
        author.addAttribute("foo", "bar");

        assertNotEquals("Documents are not equal", 0, NodeComparator.compare(document, doc2));
    }

    @Test
    public void testRootCompare2() throws Exception {
        Document doc2 = (Document) document.clone();
        Element author = doc2.getRootElement();

        author.addText("foo");

        assertNotEquals("Documents are not equal", 0, NodeComparator.compare(document, doc2));
    }

    @Test
    public void testAuthorCompare1() throws Exception {
        Document doc2 = (Document) document.clone();
        Element author = (Element) doc2.selectSingleNode("//author");
        author.addAttribute("name", "James Strachan");

        assertNotEquals("Documents are not equal", 0, NodeComparator.compare(document, doc2));
    }

    @Test
    public void testAuthorCompare2() throws Exception {
        Document doc2 = (Document) document.clone();
        Element author = (Element) doc2.selectSingleNode("//author");

        author.addText("foo");

        assertNotEquals("Documents are not equal", 0, NodeComparator.compare(document, doc2));
    }

    protected void testElementClone(Element element) throws Exception {
        Element element2 = (Element) element.clone();

        assertNotSame("Returned a new Element", element2, element);
        assertNull("New element has no parent", element2.getParent());
        assertNull("New element has no Document", element2.getDocument());

        assertEquals("Element fragments are equal", 0, NodeComparator.compare(element, element2));
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
