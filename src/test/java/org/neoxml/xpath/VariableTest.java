/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.xpath;

import org.jaxen.SimpleVariableContext;
import org.junit.Before;
import org.junit.Test;
import org.neoxml.AbstractTestCase;
import org.neoxml.DocumentHelper;
import org.neoxml.Node;
import org.neoxml.XPath;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test harness for the valueOf() function
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.4 $
 */
public class VariableTest extends AbstractTestCase {
    protected static String[] paths = {
            "$author",
            "$author/@name",
            "$root/author",
            "$root/author[1]",
            "$root/author[1]/@name",
            "$author/@name"
    };

    private final SimpleVariableContext variableContext = new SimpleVariableContext();

    private Node rootNode;

    @Test
    public void testXPaths() {
        for (String path : paths) {
            testXPath(path);
        }
    }

    protected void testXPath(String xpathText) {
        XPath xpath = createXPath(xpathText);
        List<Node> list = xpath.selectNodes(document);

        log.debug("Searched path: " + xpathText + " found: " + list.size() + " result(s)");

        assertFalse("Results should not contain the root node", list.contains(rootNode));
    }

    protected XPath createXPath(String xpath) {
        final XPath result = DocumentHelper.createXPath(xpath);
        ((DefaultXPath) result).setVariableContext(variableContext);
        return result;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        rootNode = document.selectSingleNode("/root");
        Node authorNode = document.selectSingleNode("/root/author[1]");

        variableContext.setVariableValue("root", rootNode);
        variableContext.setVariableValue("author", authorNode);
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
