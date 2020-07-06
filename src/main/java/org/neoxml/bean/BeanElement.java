/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.bean;

import org.neoxml.Attribute;
import org.neoxml.DefaultDocumentFactory;
import org.neoxml.DocumentFactory;
import org.neoxml.Element;
import org.neoxml.Namespace;
import org.neoxml.QName;
import org.neoxml.tree.DefaultElement;
import org.neoxml.tree.NamespaceStack;
import org.xml.sax.Attributes;

import java.util.List;

/**
 * <p>
 * <code>BeanElement</code> uses a Java Bean to store its attributes.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.15 $
 */
public class BeanElement extends DefaultElement {
    /**
     * The <code>DefaultDocumentFactory</code> instance used by default
     */
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.getInstance();

    private static final String CLASS_ATTR = "class";

    /**
     * The JavaBean which defines my attributes
     */
    private Object bean;

    public BeanElement(String name, Object bean) {
        this(DOCUMENT_FACTORY.createQName(name), bean);
    }

    public BeanElement(String name, Namespace namespace, Object bean) {
        this(DOCUMENT_FACTORY.createQName(name, namespace), bean);
    }

    public BeanElement(QName qname, Object bean) {
        super(qname);
        this.bean = bean;
    }

    public BeanElement(QName qname) {
        super(qname);
    }

    /**
     * DOCUMENT ME!
     *
     * @return the JavaBean associated with this element
     */
    @Override
    public Object getData() {
        return bean;
    }

    @Override
    public void setData(Object data) {
        this.bean = data;

        // force the attributeList to be lazily
        // created next time an attribute related
        // method is called again.
        this.attributes = null;
    }

    @Override
    public Attribute attribute(String name) {
        return getBeanAttributeList().attribute(name);
    }

    @Override
    public Attribute attribute(QName qName) {
        return getBeanAttributeList().attribute(qName);
    }

    @Override
    public Element addAttribute(String name, String value) {
        Attribute attribute = attribute(name);

        if (attribute != null) {
            attribute.setValue(value);
        }

        return this;
    }

    @Override
    public Element addAttribute(QName qName, String value) {
        Attribute attribute = attribute(qName);

        if (attribute != null) {
            attribute.setValue(value);
        }

        return this;
    }

    @Override
    public void setAttributes(List<Attribute> attributes) {
        if (attributes != null) {
            for (Attribute attr : attributes) {
                final String attrName = attr.getName();

                if (!CLASS_ATTR.equalsIgnoreCase(attrName)) {
                    addAttribute(attrName, attr.getValue());
                }
            }
        }
    }

    // Method overridden from AbstractElement

    @Override
    public void setAttributes(Attributes attributes, NamespaceStack namespaceStack, boolean noNamespaceAttributes) {
        String className = attributes.getValue(CLASS_ATTR);

        if (className != null) {
            try {
                Class<?> beanClass = Class.forName(className, true, BeanElement.class.getClassLoader());

                // clears attributes
                this.setData(beanClass.newInstance());

                for (int i = 0; i < attributes.getLength(); i++) {
                    String attributeName = attributes.getLocalName(i);

                    if (!CLASS_ATTR.equalsIgnoreCase(attributeName)) {
                        addAttribute(attributeName, attributes.getValue(i));
                    }
                }
            } catch (Exception ex) {
                // What to do here?
                ((BeanDocumentFactory) this.getDocumentFactory()).handleException(ex);
            }
        } else {
            super.setAttributes(attributes, namespaceStack, noNamespaceAttributes);
        }
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    protected DocumentFactory getDocumentFactory() {
        return DOCUMENT_FACTORY;
    }

    protected BeanAttributeList getBeanAttributeList() {
        return attributeList();
    }

    protected BeanAttributeList beanAttributes;

    @Override
    protected BeanAttributeList attributeList() {
        if (beanAttributes == null) {
            beanAttributes = new BeanAttributeList(this);
        }
        return beanAttributes;
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
