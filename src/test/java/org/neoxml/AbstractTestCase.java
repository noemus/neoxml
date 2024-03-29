/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Before;
import org.junit.Ignore;
import org.neoxml.io.SAXReader;
import org.neoxml.util.NodeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * An abstract base class for some neoxml test cases
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.24 $
 */
@Ignore
public class AbstractTestCase {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected Document document;

    protected Document getDocument() {
        return document;
    }

    protected Document getDocument(String path) throws Exception {
        return getDocument(path, new SAXReader());
    }

    protected Document getDocument(String path, SAXReader reader) throws Exception {
        return reader.read(getFile(path));
    }

    protected File getFile(String path) {
        final File workDir = new File(System.getProperty("user.dir"));
        final File result = firstExistingFile(new File(workDir, path), new File(workDir, "src/test/" + path));
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Can't find test file " + path);
    }

    private static File firstExistingFile(final File... files) {
        for (final File f : files) {
          if (f.exists()) { return f; }
        }
        return null;
    }

    public void assertDocumentsEqual(Document doc1, Document doc2) {
        assertNotNull("Doc1 not null", doc1);
        assertNotNull("Doc2 not null", doc2);

        doc1.normalize();
        doc2.normalize();

        assertNodesEqual(doc1, doc2);

        assertEquals("Documents are equal", 0, NodeComparator.compare(doc1, doc2));
    }

    public void assertNodesEqual(Document n1, Document n2) {
        assertNodesEqual(n1.getDocType(), n2.getDocType());
        assertNodesEqualContent(n1, n2);
    }

    public void assertNodesEqual(Element n1, Element n2) {
        assertNodesEqual(n1.getQName(), n2.getQName());

        int c1 = n1.attributeCount();
        int c2 = n2.attributeCount();

        assertEquals("Elements have same number of attributes (" + c1 + ", " + c2 + " for: " + n1 + " and " + n2, c1, c2);

        for (int i = 0; i < c1; i++) {
            Attribute a1 = n1.attribute(i);
            Attribute a2 = n2.attribute(a1.getQName());
            assertNodesEqual(a1, a2);
        }

        assertNodesEqualContent(n1, n2);
    }

    public void assertNodesEqual(Attribute n1, Attribute n2) {
        assertNodesEqual(n1.getQName(), n2.getQName());

        assertEquals("Attribute values for: " + n1 + " and " + n2, n1.getValue(), n2.getValue());
    }

    public void assertNodesEqual(QName n1, QName n2) {
        assertEquals("URIs equal for: " + n1.getQualifiedName() + " and " + n2.getQualifiedName(), n1.getNamespaceURI(), n2.getNamespaceURI());
        assertEquals("qualified names equal", n1.getQualifiedName(), n2.getQualifiedName());
    }

    public void assertNodesEqual(CharacterData t1, CharacterData t2) {
        assertEquals("Text equal for: " + t1 + " and " + t2, t1.getText(), t2.getText());
    }

    public void assertNodesEqual(DocumentType o1, DocumentType o2) {
        if (o1 != o2) {
            if (o1 == null) {
                fail("Missing DocType: " + o2);
            } else if (o2 == null) {
                fail("Missing DocType: " + o1);
            } else {
                assertEquals("DocType name equal", o1.getName(), o2.getName());
                assertEquals("DocType publicID equal", o1.getPublicID(), o2.getPublicID());
                assertEquals("DocType systemID equal", o1.getSystemID(), o2.getSystemID());
            }
        }
    }

    public void assertNodesEqual(Entity o1, Entity o2) {
        assertEquals("Entity names equal", o1.getName(), o2.getName());
        assertEquals("Entity values equal", o1.getText(), o2.getText());
    }

    public void assertNodesEqual(ProcessingInstruction n1, ProcessingInstruction n2) {
        assertEquals("PI targets equal", n1.getTarget(), n2.getTarget());
        assertEquals("PI text equal", n1.getText(), n2.getText());
    }

    public void assertNodesEqual(Namespace n1, Namespace n2) {
        assertEquals("Namespace prefixes not equal", n1.getPrefix(), n2.getPrefix());
        assertEquals("Namespace URIs not equal", n1.getURI(), n2.getURI());
    }

    public void assertNodesEqualContent(Branch b1, Branch b2) {
        int c1 = b1.nodeCount();
        int c2 = b2.nodeCount();

        if (c1 != c2) {
            log.debug("Content of: " + b1);
            log.debug("is: " + b1.content());
            log.debug("Content of: " + b2);
            log.debug("is: " + b2.content());
        }

        assertEquals("Branches have same number of children (" + c1 + ", " + c2 + " for: " + b1 + " and " + b2, c1, c2);

        for (int i = 0; i < c1; i++) {
            Node n1 = b1.node(i);
            Node n2 = b2.node(i);
            assertNodesEqual(n1, n2);
        }
    }

    public void assertNodesEqual(Node n1, Node n2) {
        NodeType nodeType1 = n1.getNodeTypeEnum();
        NodeType nodeType2 = n2.getNodeTypeEnum();
        assertSame("Nodes are of same type: ", nodeType1, nodeType2);

        switch (nodeType1) {
            case ELEMENT_NODE:
                assertNodesEqual((Element) n1, (Element) n2);
                break;

            case DOCUMENT_NODE:
                assertNodesEqual((Document) n1, (Document) n2);
                break;

            case ATTRIBUTE_NODE:
                assertNodesEqual((Attribute) n1, (Attribute) n2);
                break;

            case TEXT_NODE:
                assertNodesEqual((Text) n1, (Text) n2);
                break;

            case CDATA_SECTION_NODE:
                assertNodesEqual((CDATA) n1, (CDATA) n2);
                break;

            case ENTITY_REFERENCE_NODE:
                assertNodesEqual((Entity) n1, (Entity) n2);
                break;

            case PROCESSING_INSTRUCTION_NODE:
                assertNodesEqual((ProcessingInstruction) n1, (ProcessingInstruction) n2);
                break;

            case COMMENT_NODE:
                assertNodesEqual((Comment) n1, (Comment) n2);
                break;

            case DOCUMENT_TYPE_NODE:
                assertNodesEqual((DocumentType) n1, (DocumentType) n2);
                break;

            case NAMESPACE_NODE:
                assertNodesEqual((Namespace) n1, (Namespace) n2);
                break;

            default:
                fail("Invalid node types. node1: " + n1 + " and node2: " + n2);
        }
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        document = DocumentHelper.createDocument();

        Element root = document.addElement("root");

        Element author1 = root.addElement("author")
                              .addAttribute("name", "James")
                              .addAttribute("location", "UK")
                              .addText("James Strachan");

        Element url1 = author1.addElement("url");
        url1.addText("http://sourceforge.net/users/jstrachan/");

        Element author2 = root.addElement("author")
                              .addAttribute("name", "Bob")
                              .addAttribute("location", "Canada")
                              .addText("Bob McWhirter");

        Element url2 = author2.addElement("url");
        url2.addText("http://sourceforge.net/users/werken/");
    }

    /**
     * DOCUMENT ME!
     *
     * @return the root element of the document
     */
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
