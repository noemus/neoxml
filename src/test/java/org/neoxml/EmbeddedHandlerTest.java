/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml;

import org.junit.Test;
import org.neoxml.io.SAXReader;

import java.io.File;

import static org.junit.Assert.fail;

/**
 * TestEmbeddedHandler
 *
 * @author <a href="mailto:franz.beil@temis-group.com">FB </a>
 */
public class EmbeddedHandlerTest extends AbstractTestCase {
    private static final int MAIN_READER = 0;

    private static final int ON_END_READER = 1;

    protected String[] testDocuments = {
            "xml/test/FranzBeilMain.xml"
    };

    private final StringBuilder[] results = {
            new StringBuilder(), new StringBuilder()
    };

    protected int test;

    @Test
    public void testMainReader() throws Exception {
        test = MAIN_READER;
        try {
            readDocuments();
        } catch (Exception e) {
            fail("No exception should be thrown, but we caught: " + e.getMessage());
        }
    }

    @Test
    public void testOnEndReader() {
        test = ON_END_READER;
        try {
            readDocuments();
        } catch (Exception e) {
            fail("No exception should be thrown, but we caught: " + e.getMessage());
        }
    }

    @Test
    public void testBothReaders() throws Exception {
        testMainReader();
        testOnEndReader();

        if (!results[MAIN_READER].toString().equals(
                results[ON_END_READER].toString())) {
            final String msg = "Results of tests should be equal!\n" +
                    "Results testMainReader():\n" + results[MAIN_READER].toString() +
                    "Results testOnEndReader():\n" + results[ON_END_READER].toString();
            fail(msg);
        }
    }

    private void readDocuments() throws Exception {
        for (String testDocument : testDocuments) {
            File testDoc = getFile(testDocument);
            String mainDir = testDoc.getParent();
            SAXReader reader = new SAXReader();
            ElementHandler mainHandler = new MainHandler(mainDir);
            reader.addHandler("/main/import", mainHandler);
            getDocument(testDocument, reader);
        }
    }

    class MainHandler implements ElementHandler {
        private final SAXReader mainReader;

        private final String mainDir;

        public MainHandler(String dir) {
            mainReader = new SAXReader();
            mainDir = dir;
            mainReader.addHandler("/import/stuff", new EmbeddedHandler());
        }

        @Override
        public void onStart(ElementPath path) {}

        @Override
        public void onEnd(ElementPath path) {
            String href = path.getCurrent().attribute("href").getValue();
            Element importRef = path.getCurrent();
            Element parentElement = importRef.getParent();
            SAXReader onEndReader = new SAXReader();
            onEndReader.addHandler("/import/stuff", new EmbeddedHandler());

            File file = new File(mainDir + File.separator + href);
            Element importElement = null;

            try {
                if (test == MAIN_READER) {
                    importElement = mainReader.read(file).getRootElement();
                } else if (test == ON_END_READER) {
                    importElement = onEndReader.read(file).getRootElement();
                }
            } catch (Exception e) {
                // too bad that it's not possible to throw the exception at the
                // caller
                e.printStackTrace();
            }

            // prune and replace
            importRef.detach();
            parentElement.add(importElement);
        }
    }

    public class EmbeddedHandler implements ElementHandler {
        @Override
        public void onStart(ElementPath path) {
            results[test].append(path.getCurrent().attribute("name").getValue()).append("\n");
        }

        @Override
        public void onEnd(ElementPath path) {}
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
