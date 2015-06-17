/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package cz.tdp.xtree.bean;

import cz.tdp.xtree.Attribute;
import cz.tdp.xtree.QName;
import cz.tdp.xtree.tree.DefaultNodeList;

/**
 * <p>
 * <code>BeanAttributeList</code> implements a list of Attributes which are the properties of a JavaBean.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.9 $
 */
public class BeanAttributeList extends DefaultNodeList<Attribute>
{
  /**
   * The BeanElement that this
   */
  private BeanElement parent;

  /**
   * The BeanElement that this
   */
  private BeanMetaData beanMetaData;

  /**
   * The attributes
   */
  private BeanAttribute[] attributes;

  public BeanAttributeList(BeanElement parent, BeanMetaData beanMetaData) {
    super(parent);
    this.parent = parent;
    
    this.beanMetaData = beanMetaData;
    this.attributes = new BeanAttribute[beanMetaData.attributeCount()];
  }

  public BeanAttributeList(BeanElement parent) {
    super(parent);
    this.parent = parent;

    Object data = parent.getData();
    Class<?> beanClass = (data != null) ? data.getClass() : null;
    this.beanMetaData = BeanMetaData.get(beanClass);
    this.attributes = new BeanAttribute[beanMetaData.attributeCount()];
  }

  public Attribute attribute(String name) {
    int index = beanMetaData.getIndex(name);

    return attribute(index);
  }

  public Attribute attribute(QName qname) {
    int index = beanMetaData.getIndex(qname);

    return attribute(index);
  }

  public BeanAttribute attribute(int index) {
    if ((index >= 0) && (index <= attributes.length)) {
      BeanAttribute attribute = attributes[index];

      if (attribute == null) {
        attribute = createAttribute(parent, index);
        attributes[index] = attribute;
      }

      return attribute;
    }

    return null;
  }

  @Override
  public BeanElement getParent() {
    return parent;
  }

  public QName getQName(int index) {
    return beanMetaData.getQName(index);
  }

  public Object getData(int index) {
    return beanMetaData.getData(index, parent.getData());
  }

  public void setData(int index, Object data) {
    beanMetaData.setData(index, parent.getData(), data);
  }

  // List interface
  // -------------------------------------------------------------------------

  @Override
  public int size() {
    return attributes.length;
  }

  @Override
  public BeanAttribute get(int index) {
    BeanAttribute attribute = attributes[index];

    if (attribute == null) {
      attribute = createAttribute(parent, index);
      attributes[index] = attribute;
    }

    return attribute;
  }

  @Override
  public boolean add(Attribute object) {
    throw new UnsupportedOperationException("add(BeanAttribute) unsupported");
  }

  @Override
  public void add(int index, Attribute object) {
    throw new UnsupportedOperationException("add(int,BeanAttribute) unsupported");
  }

  @Override
  public Attribute set(int index, Attribute object) {
    throw new UnsupportedOperationException("set(int,BeanAttribute) unsupported");
  }

  @Override
  public boolean remove(Object object) {
    return false;
  }

  @Override
  public Attribute remove(int index) {
    BeanAttribute attribute = get(index);
    attribute.setValue(null);

    return attribute;
  }

  @Override
  public void clear() {
    for (int i = 0, size = attributes.length; i < size; i++) {
      BeanAttribute attribute = attributes[i];

      if (attribute != null) {
        attribute.setValue(null);
      }
    }
  }

  // Implementation methods
  // -------------------------------------------------------------------------

  protected BeanAttribute createAttribute(BeanElement element, int index) {
    return new BeanAttribute(this, index);
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
