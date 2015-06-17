// ///////////////////////////////////////////////////////////////////////////
// $Id$
package org.noemus.neoxml.util;

import java.util.HashMap;
import java.util.Map;

import org.noemus.neoxml.QName;

/**
 * @author Jirsák Filip
 * @version $Revision$
 */
public class DoubleNameMap<T>
{
  private Map<String,T> namedMap = new HashMap<>();
  private Map<QName,T> qNamedMap = new HashMap<>();
  
  public DoubleNameMap() {
    
  }

  public void put(QName qName, T value) {
    qNamedMap.put(qName, value);
    namedMap.put(qName.getName(), value);
  }

  public T get(String name) {
    return namedMap.get(name);
  }

  public T get(QName qName) {
    return qNamedMap.get(qName);
  }

  public void remove(QName qName) {
    this.qNamedMap.remove(qName);
    this.namedMap.remove(qName.getName());
  }
}
