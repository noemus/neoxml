/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.tree;

import org.neoxml.Node;
import org.neoxml.NodeList;

import java.util.RandomAccess;

/**
 * <code>BackedList</code> represents a list of content of a {@link org.neoxml.Branch}. Changes to the list will be
 * reflected in the branch,
 * though changes to the branch will not be reflected in this list.
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.14 $
 */
class NodeListFacade<T extends Node> extends AbstractNodeListFacade<T> implements RandomAccess {
    NodeListFacade(DefaultNodeList<T> nodes) {
        super(nodes);
    }

    @Override
    public boolean add(T node) {
        nodeList.addNode(node);
        return true;
    }

    @Override
    public void add(int index, T node) {
        nodeList.addNode(index, node);
    }

    @Override
    public T set(int index, T node) {
        return nodeList.setNode(index, node);
    }

    @Override
    public boolean remove(Object node) {
        if (node instanceof Node) {
            return nodeList.removeNode((Node) node);
        }
        return false;
    }

    @Override
    public T remove(int index) {
        return nodeList.removeNode(index);
    }

    @Override
    public int indexOf(Object element) {
        return nodeList.indexOf(element);
    }

    @Override
    public int lastIndexOf(Object element) {
        return nodeList.lastIndexOf(element);
    }

    @Override
    public void clear() {
        nodeList.clearNodes();
    }

    @Override
    public AbstractBranch getParent() {
        return nodeList.getParent();
    }

    @Override
    public T get(int index) {
        return nodeList.get(index);
    }

    @Override
    public int size() {
        return nodeList.size();
    }

    @Override
    public boolean isEmpty() {
        return nodeList.isEmpty();
    }

    @Override
    public NodeList<T> facade() {
        return nodeList.facade();
    }

    @Override
    public NodeList<T> duplicate() {
        return nodeList.duplicate().facade();
    }

    @Override
    public NodeList<T> copy() {
        return nodeList.copy();
    }

    @Override
    public NodeList<T> content() {
        if (getParent() != null) {
            return copy();
        }
        return nodeList;
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
