package org.neoxml.util;

import org.neoxml.QName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jirsák Filip
 * @version $Revision$
 */
public class DoubleNameMap<T> {
    private final Map<String, T> namedMap = new HashMap<>();
    private final Map<QName, T> qNamedMap = new HashMap<>();

    public DoubleNameMap() {
        super();
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
