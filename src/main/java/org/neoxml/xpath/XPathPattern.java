/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.xpath;

import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jaxen.XPathFunctionContext;
import org.jaxen.pattern.Pattern;
import org.jaxen.pattern.PatternParser;
import org.jaxen.saxpath.SAXPathException;
import org.neoxml.InvalidXPathException;
import org.neoxml.Node;
import org.neoxml.NodeType;
import org.neoxml.XPathException;

import java.util.ArrayList;

/**
 * <p>
 * <code>XPathPattern</code> is an implementation of Pattern which uses an XPath xpath.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.18 $
 */
public class XPathPattern implements org.neoxml.rule.Pattern {
    private final String text;

    private final Pattern pattern;

    private final Context context;

    public XPathPattern(Pattern pattern) {
        this.pattern = pattern;
        this.text = pattern.getText();
        this.context = new Context(getContextSupport());
    }

    public XPathPattern(String text) {
        this.text = text;
        this.context = new Context(getContextSupport());

        try {
            this.pattern = PatternParser.parse(text);
        } catch (SAXPathException e) {
            throw new InvalidXPathException(text, e.getMessage());
        } catch (RuntimeException e) {
            throw new InvalidXPathException(text);
        }
    }

    @Override
    public boolean matches(Node node) {
        try {
            ArrayList<Node> list = new ArrayList<>(1);
            list.add(node);
            context.setNodeSet(list);

            return pattern.matches(node, context);
        } catch (JaxenException e) {
            handleJaxenException(e);

            return false;
        }
    }

    public String getText() {
        return text;
    }

    @Override
    public double getPriority() {
        return pattern.getPriority();
    }

    @Override
    public org.neoxml.rule.Pattern[] getUnionPatterns() {
        Pattern[] patterns = pattern.getUnionPatterns();

        if (patterns != null) {
            int size = patterns.length;
            XPathPattern[] answer = new XPathPattern[size];

            for (int i = 0; i < size; i++) {
                answer[i] = new XPathPattern(patterns[i]);
            }

            return answer;
        }

        //@nosonar it's not union pattern
        return null;
    }

    @Override
    public NodeType getMatchType() {
        return NodeType.byCode(pattern.getMatchType());
    }

    @Override
    public String getMatchesNodeName() {
        return pattern.getMatchesNodeName();
    }

    public void setVariableContext(VariableContext variableContext) {
        context.getContextSupport().setVariableContext(variableContext);
    }

    @Override
    public String toString() {
        return "[XPathPattern: text: " + text + " Pattern: " + pattern + "]";
    }

    protected ContextSupport getContextSupport() {
        return new ContextSupport(new SimpleNamespaceContext(), XPathFunctionContext.getInstance(),
                                  new SimpleVariableContext(), DocumentNavigator.getInstance());
    }

    protected void handleJaxenException(JaxenException exception) {
        throw new XPathException(text, exception);
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
