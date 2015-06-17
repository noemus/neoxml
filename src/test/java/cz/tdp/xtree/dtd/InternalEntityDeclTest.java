/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package cz.tdp.xtree.dtd;

import org.junit.Test;

import cz.tdp.xtree.AbstractTestCase;
import cz.tdp.xtree.dtd.InternalEntityDecl;

/**
 * Tests the {@link InternalEntityDecl}functionality. Tests each of the
 * property access methods and the serialization mechanisms. Correct parsing is
 * tested by {@link DocTypeTest}.
 * <p/>
 * <p>
 * </p>
 *
 * @author Bryan Thompson
 * @author Maarten Coene
 * @version $Revision: 1.3 $
 * @todo The dom4j documentation needs to describe what representation SHOULD be
 *       generated by {@link InternalEntityDecl#toString()}.
 */
public class InternalEntityDeclTest extends AbstractTestCase
{
  // Test case(s)
  // -------------------------------------------------------------------------

  @Test
  public void testToString() {
    InternalEntityDecl decl1 = new InternalEntityDecl("name", "value");
    InternalEntityDecl decl2 = new InternalEntityDecl("%name", "value");

    assertEquals("<!ENTITY name \"value\">", decl1.toString());
    assertEquals("<!ENTITY % name \"value\">", decl2.toString());
  }

  /**
   * Tests parameter entity declaration, such as
   * <p/>
   *
   * <pre>
   * <p/>
   * <p/>
   *   &lt;!ENTITY % boolean &quot;( true | false )&quot;&gt;
   * <p/>
   * <p/>
   * </pre>
   * <p/>
   * Note: There is a required whitespace between the "%" and the name of the entity. This whitespace is required in the
   * declaration and is not allowed when writing a reference to the entity, e.g., "%boolean;" is a legal reference but
   * not "% boolean;".
   * <p/>
   * <p>
   * Note: The "%" is part of the parameter entity name as reported by the SAX API. See <a
   * href="http://tinyurl.com/6xe9y">LexicalHandler </a>, which states:
   * <p/>
   *
   * <pre>
   * <p/>
   * <p/>
   *   General entities are reported with their regular names,
   *   parameter entities have '%' prepended to their names, and the
   *   external DTD subset has the pseudo-entity name &quot;[dtd]&quot;.
   * <p/>
   * <p/>
   * </pre>
   * <p/>
   * </p>
   */
  @Test
  public void testParameterEntity() {
    String expectedName = "%boolean";

    String expectedValue = "( true | false )";

    String expectedText = "<!ENTITY % boolean \"( true | false )\">";

    InternalEntityDecl actual = new InternalEntityDecl(expectedName,
      expectedValue);

    assertEquals("name is correct", expectedName, actual.getName());

    assertEquals("value is correct", expectedValue, actual.getValue());

    assertEquals("toString() is correct", expectedText, actual.toString());
  }

  /**
   * Tests general entity declaration, such as:
   * <p/>
   *
   * <pre>
   * <p/>
   * <p/>
   *   &lt;!ENTITY foo &quot;bar&quot;&gt;
   * <p/>
   * <p/>
   * </pre>
   */
  @Test
  public void testGeneralEntity() {
    String expectedName = "foo";

    String expectedValue = "bar";

    String expectedText = "<!ENTITY foo \"bar\">";

    InternalEntityDecl actual = new InternalEntityDecl(expectedName,
      expectedValue);

    assertEquals("name is correct", expectedName, actual.getName());

    assertEquals("value is correct", expectedValue, actual.getValue());

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
