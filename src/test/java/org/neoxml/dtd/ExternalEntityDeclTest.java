/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.dtd;

import org.junit.Test;
import org.neoxml.AbstractTestCase;

/**
 * Tests the {@link ExternalEntityDecl}functionality. Tests each of the
 * property access methods and the serialization mechanisms. Correct parsing is
 * tested by {@link DocTypeTest}.
 * <p/>
 * <P>
 * </p>
 *
 * @author Bryan Thompson
 * @author Maarten Coene
 * @version $Revision: 1.3 $
 * @todo The neoxml documentation needs to describe what representation SHOULD be
 *       generated by {@link ExternalEntityDecl#toString()}.
 * @todo Test support for NOTATION and NDATA when used as part of an external
 *       entity declaration. neoxml does not appear to support NOTATION and NDATA
 *       at this time.
 */
public class ExternalEntityDeclTest extends AbstractTestCase
{

  // Test case(s)
  // -------------------------------------------------------------------------

  @Test
  public void testToString() {
    ExternalEntityDecl decl1 = new ExternalEntityDecl("name", null,
        "systemID");
    ExternalEntityDecl decl2 = new ExternalEntityDecl("%name", null,
        "systemID");

    assertEquals("<!ENTITY name SYSTEM \"systemID\" >", decl1.toString());
    assertEquals("<!ENTITY % name SYSTEM \"systemID\" >", decl2.toString());
  }

  /**
   * Tests external entity declaration using only the SYSTEM identifier.
   */
  @Test
  public void testSystemId() {
    String expectedName = "anEntity";

    String expectedPublicID = null;

    String expectedSystemID = "http://www.myorg.org/foo";

    String expectedText = "<!ENTITY anEntity "
        + "SYSTEM \"http://www.myorg.org/foo\" >";

    ExternalEntityDecl actual = new ExternalEntityDecl(expectedName,
      expectedPublicID, expectedSystemID);

    assertEquals("name is correct", expectedName, actual.getName());

    assertEquals("publicID is correct", expectedPublicID, actual
      .getPublicID());

    assertEquals("systemID is correct", expectedSystemID, actual
      .getSystemID());

    assertEquals("toString() is correct", expectedText, actual.toString());
  }

  /**
   * Tests external entity declaration using both SYSTEM and PUBLIC
   * identifiers.
   */
  @Test
  public void testPublicIdSystemId() {
    String expectedName = "anEntity";

    String expectedPublicID = "-//dom4j//DTD sample";

    String expectedSystemID = "http://www.myorg.org/foo";

    String expectedText = "<!ENTITY anEntity "
        + "PUBLIC \"-//dom4j//DTD sample\" "
        + "\"http://www.myorg.org/foo\" >";

    ExternalEntityDecl actual = new ExternalEntityDecl(expectedName,
      expectedPublicID, expectedSystemID);

    assertEquals("name is correct", expectedName, actual.getName());

    assertEquals("publicID is correct", expectedPublicID, actual
      .getPublicID());

    assertEquals("systemID is correct", expectedSystemID, actual
      .getSystemID());

    assertEquals("toString() is correct", expectedText, actual.toString());
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
