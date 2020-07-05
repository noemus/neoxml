/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.neoxml.tree;

import org.neoxml.Element;
import org.neoxml.NodeType;
import org.neoxml.ProcessingInstruction;
import org.neoxml.Visitor;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>
 * <code>AbstractProcessingInstruction</code> is an abstract base class for tree implementors to use for implementation
 * inheritence.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.17 $
 */
public abstract class AbstractProcessingInstruction extends AbstractNode implements ProcessingInstruction {
    public AbstractProcessingInstruction() {}

    @Override
    public NodeType getNodeTypeEnum() {
        return NodeType.PROCESSING_INSTRUCTION_NODE;
    }

    @Override
    public String getPath(Element context) {
        Element parent = getParent();

        return ((parent != null) && (parent != context)) ? (parent.getPath(context) + "/processing-instruction()")
                                                         : "processing-instruction()";
    }

    @Override
    public String getUniquePath(Element context) {
        Element parent = getParent();

        return ((parent != null) && (parent != context)) ? (parent.getUniquePath(context) + "/processing-instruction()")
                                                         : "processing-instruction()";
    }

    @Override
    public String toString() {
        return super.toString() + " [ProcessingInstruction: &" + getName() + ";]";
    }

    @Override
    public String asXML() {
        return "<?" + getName() + " " + getText() + "?>";
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write("<?");
        writer.write(getName());
        writer.write(" ");
        writer.write(getText());
        writer.write("?>");
    }

    @Override
    public boolean accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void setValue(String name, String value) {
        throw new UnsupportedOperationException("This PI is read-only and " + "cannot be modified");
    }

    @Override
    public void setValues(Map<String, String> data) {
        throw new UnsupportedOperationException("This PI is read-only and " + "cannot be modified");
    }

    @Override
    public String getName() {
        return getTarget();
    }

    @Override
    public void setName(String name) {
        setTarget(name);
    }

    @Override
    public boolean removeValue(String name) {
        return false;
    }

    // Helper methods

    /**
     * <p>
     * This will convert the Map to a string representation.
     * </p>
     *
     * @param values is a <code>Map</code> of PI data to convert
     * @return DOCUMENT ME!
     */
    protected String toString(Map<String, String> values) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            builder.append(name);
            builder.append("=\"");
            builder.append(value);
            builder.append("\" ");
        }

        // remove the last space
        builder.setLength(builder.length() - 1);

        return builder.toString();
    }

    /**
     * <p>
     * Parses the raw data of PI as a <code>Map</code>.
     * </p>
     *
     * @param text <code>String</code> PI data to parse
     * @return DOCUMENT ME!
     */
    protected Map<String, String> parseValues(String text) {
        Map<String, String> data = new HashMap<>();

        StringTokenizer s = new StringTokenizer(text, " =\'\"", true);

        while (s.hasMoreTokens()) {
            String name = getName(s);

            if (s.hasMoreTokens()) {
                String value = getValue(s);
                data.put(name, value);
            }
        }

        return data;
    }

    private String getName(StringTokenizer tokenizer) {
        String token = tokenizer.nextToken();
        StringBuilder name = new StringBuilder(token);

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            if (!token.equals("=")) {
                name.append(token);
            } else {
                break;
            }
        }

        return name.toString().trim();
    }

    private String getValue(StringTokenizer tokenizer) {
        String token = tokenizer.nextToken();
        StringBuilder value = new StringBuilder();

        /* get the quote */
        while (tokenizer.hasMoreTokens() && !token.equals("\'") && !token.equals("\"")) {
            token = tokenizer.nextToken();
        }

        String quote = token;

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            if (!quote.equals(token)) {
                value.append(token);
            } else {
                break;
            }
        }

        return value.toString();
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
