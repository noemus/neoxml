/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.io;

import org.neoxml.Element;
import org.neoxml.ElementHandler;
import org.neoxml.ElementPath;

import java.util.Arrays;

/**
 * <p>
 * <code>ElementStack</code> is used internally inside the {@link SAXContentHandler} to maintain a stack of
 * {@link Element}instances. It opens an integration possibility allowing derivations to prune the tree when a node is
 * complete.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.14 $
 */
class ElementStack implements ElementPath {
    private static final String SLASH = "/";
    /**
     * stack of <code>Element</code> objects
     */
    protected Element[] stack;

    /**
     * index of the item at the top of the stack or -1 if the stack is empty
     */
    protected int lastElementIndex = -1;

    private DispatchHandler handler = null;

    public ElementStack() {
        this(64);
    }

    public ElementStack(int defaultCapacity) {
        stack = new Element[defaultCapacity];
    }

    public void setDispatchHandler(DispatchHandler dispatchHandler) {
        this.handler = dispatchHandler;
    }

    public DispatchHandler getDispatchHandler() {
        return this.handler;
    }

    /**
     * Peeks at the top element on the stack without changing the contents of
     * the stack.
     */
    public void clear() {
        lastElementIndex = -1;
    }

    /**
     * Peeks at the top element on the stack without changing the contents of
     * the stack.
     *
     * @return the current element on the stack
     */
    public Element peekElement() {
        if (lastElementIndex < 0) {
            return null;
        }

        return stack[lastElementIndex];
    }

    /**
     * Pops the element off the stack
     *
     * @return the element that has just been popped off the stack
     */
    public Element popElement() {
        if (lastElementIndex < 0) {
            return null;
        }

        return stack[lastElementIndex--];
    }

    /**
     * Pushes a new element onto the stack
     *
     * @param element DOCUMENT ME!
     */
    public void pushElement(Element element) {
        int length = stack.length;

        if (++lastElementIndex >= length) {
            reallocate(length * 2);
        }

        stack[lastElementIndex] = element;
    }

    /**
     * Reallocates the stack to the given size
     *
     * @param size DOCUMENT ME!
     */
    protected void reallocate(int size) {
        stack = Arrays.copyOf(stack, size);
    }

    // The ElementPath Interface
    //

    @Override
    public int size() {
        return lastElementIndex + 1;
    }

    @Override
    public Element getElement(int depth) {
        Element element;

        try {
            element = stack[depth];
        } catch (ArrayIndexOutOfBoundsException e) {
            element = null;
        }

        return element;
    }

    @Override
    public String getPath() {
        if (handler == null) {
            setDispatchHandler(new DispatchHandler());
        }

        return handler.getPath();
    }

    @Override
    public Element getCurrent() {
        return peekElement();
    }

    @Override
    public void addHandler(String path, ElementHandler elementHandler) {
        this.handler.addHandler(getHandlerPath(path), elementHandler);
    }

    @Override
    public void removeHandler(String path) {
        this.handler.removeHandler(getHandlerPath(path));
    }

    private String getHandlerPath(String path) {
        String handlerPath;

        if (this.handler == null) {
            setDispatchHandler(new DispatchHandler());
        }

        if (path.startsWith(SLASH)) {
            handlerPath = path;
        } else if (getPath().equals(SLASH)) {
            handlerPath = SLASH + path;
        } else {
            handlerPath = getPath() + SLASH + path;
        }

        return handlerPath;
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
