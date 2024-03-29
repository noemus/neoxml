/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.rule;

import org.junit.Test;
import org.neoxml.AbstractTestCase;
import org.neoxml.CDATA;
import org.neoxml.DefaultDocumentFactory;
import org.neoxml.DocumentFactory;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests the ordering of Rules
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.3 $
 */
public class RuleTest extends AbstractTestCase {
    protected DocumentFactory factory = new DefaultDocumentFactory();

    @Test
    public void testOrder() throws Exception {
        testGreater("foo", "*");
    }

    protected void testGreater(String expr1, String expr2) throws Exception {
        log.info("parsing: " + expr1 + " and " + expr2);

        Rule r1 = createRule(expr1);
        Rule r2 = createRule(expr2);

        log.info("rule1: " + r1 + " rule2: " + r2);

        assertTrue("r1 > r2", r1.compareTo(r2) > 0);
        assertTrue("r2 < r1", r2.compareTo(r1) < 0);
        //@nosonar testing reflexivity
        assertEquals("r1 == r1", 0, r1.compareTo(r1));
        //@nosonar testing reflexivity
        assertEquals("r2 == r2", 0, r2.compareTo(r2));

        ArrayList<Rule> list = new ArrayList<>();
        list.add(r1);
        list.add(r2);

        Collections.sort(list);

        assertSame("r2 should be first", r2, list.get(0));
        assertSame("r1 should be next", r1, list.get(1));

        list = new ArrayList<>();
        list.add(r2);
        list.add(r1);

        Collections.sort(list);

        assertSame("r2 should be first", r2, list.get(0));
        assertSame("r1 should be next", r1, list.get(1));

        /*
         * TreeSet set = new TreeSet(); set.add( r1 ); set.add( r2 );
         * assertTrue( "r2 should be first", set.first() == r2 ); assertTrue(
         * "r1 should be next", set.last() == r1 );
         * Object[] array = set.toArray();
         * assertTrue( "r2 should be first", array[0] == r2 ); assertTrue( "r1
         * should be next", array[1] == r1 );
         * set = new TreeSet(); set.add( r2 ); set.add( r1 );
         * assertTrue( "r2 should be first", set.first() == r2 ); assertTrue(
         * "r1 should be next", set.last() == r1 );
         * array = set.toArray();
         * assertTrue( "r2 should be first", array[0] == r2 ); assertTrue( "r1
         * should be next", array[1] == r1 );
         */
    }

    @Test
    public void testDocument() {
        Rule rule = createRule("/");
        document = factory.createDocument();
        document.addElement("foo");

        assertTrue("/ matches document", rule.matches(document));
        assertFalse("/ does not match root element", rule.matches(document.getRootElement()));
    }

    @Test
    public void testTextMatchesCDATA() {
        CDATA cdata = factory.createCDATA("<>&");
        Rule rule = createRule("text()");

        assertTrue("text() matches CDATA", rule.matches(cdata));
    }

    protected Rule createRule(String expr) {
        Pattern pattern = factory.createPattern(expr);

        return new Rule(pattern);
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
