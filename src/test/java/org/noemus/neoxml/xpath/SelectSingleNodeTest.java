/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.noemus.neoxml.xpath;

import org.junit.Test;
import org.noemus.neoxml.AbstractTestCase;
import org.noemus.neoxml.Document;
import org.noemus.neoxml.Element;
import org.noemus.neoxml.Node;

/**
 * Tests the selectSingleNode method
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.4 $
 */
public class SelectSingleNodeTest extends AbstractTestCase
{
  // Test case(s)
  // -------------------------------------------------------------------------

  @Test
  public void testSelectSingleNode() throws Exception {
    Document document = getDocument("/src/test/xml/test/jimBrain.xml");
    Node node = document.selectSingleNode("/properties/client/threadsafe");
    assertTrue("Found a valid node", node != null);

    Element server = (Element)document
        .selectSingleNode("/properties/server");
    assertTrue("Found a valid server", server != null);

    Element root = document.getRootElement();
    server = (Element)root.selectSingleNode("/properties/server");
    assertTrue("Found a valid server", server != null);

    // try finding it via a relative path
    server = (Element)document.selectSingleNode("properties/server");
    assertTrue("Found a valid server", server != null);

    // now lets use a relative path
    Element connection = (Element)server.selectSingleNode("db/connection");
    assertTrue("Found a valid connection", connection != null);
  }

  /**
   * Test out Steen's bug
   *
   * @throws Exception DOCUMENT ME!
   */
  @Test
  public void testSteensBug() throws Exception {
    Document document = getDocument("/src/test/xml/schema/personal.xsd");

    String xpath = "/xs:schema/xs:element[@name='person']";
    assertNotNull("element is null", document.selectSingleNode(xpath));

    Element root = document.getRootElement();

    assertNotNull("element is null", root.selectSingleNode(xpath));
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