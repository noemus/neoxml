/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.tree;

import org.neoxml.DefaultDocumentFactory;
import org.neoxml.DocumentFactory;
import org.neoxml.Namespace;
import org.neoxml.QName;

/**
 * <code>FlyweightAttribute</code> is a Flyweight pattern implementation of a singly linked, read-only XML Attribute.
 * <p>
 * This node could be shared across documents and elements though it does not support the parent relationship.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.7 $
 */
public class FlyweightAttribute extends AbstractAttribute {
    /**
     * The <code>DefaultDocumentFactory</code> instance used by default
     */
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.getInstance();

    /**
     * The <code>QName</code> for this element
     */
    private final QName qname;

    /**
     * The value of the <code>Attribute</code>
     */
    protected final String value;

    public FlyweightAttribute(QName qname) {
        this(qname, null);
    }

    public FlyweightAttribute(QName qname, String value) {
        this.qname = qname;
        this.value = value;
    }

    /**
     * Creates the <code>Attribute</code> with the specified local name and
     * value.
     *
     * @param name  is the name of the attribute
     * @param value is the value of the attribute
     */
    public FlyweightAttribute(String name, String value) {
        this(DOCUMENT_FACTORY.createQName(name), value);
    }

    /**
     * Creates the <code>Attribute</code> with the specified local name, value
     * and <code>Namespace</code>.
     *
     * @param name      is the name of the attribute
     * @param value     is the value of the attribute
     * @param namespace is the namespace of the attribute
     */
    public FlyweightAttribute(String name, String value, Namespace namespace) {
        this(DOCUMENT_FACTORY.createQName(name, namespace), value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public QName getQName() {
        return qname;
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
