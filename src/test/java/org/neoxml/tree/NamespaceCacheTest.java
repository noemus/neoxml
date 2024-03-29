/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.tree;

import org.junit.Test;
import org.neoxml.AbstractTestCase;
import org.neoxml.Namespace;

import java.util.stream.Stream;

import static org.junit.Assert.fail;

/**
 * A test harness to test the performance of the NamespaceCache
 *
 * @author <a href="mailto:bfinnell@users.sourceforge.net">Brett Finnell </a>
 */
public class NamespaceCacheTest extends AbstractTestCase {
    private static final int THREADCOUNT = 50;

    private static final int ITERATIONCOUNT = 10000;

    // Test case(s)
    // -------------------------------------------------------------------------

    @Test
    public void testGetSameNamespaceSingleThread() {
        long start = System.currentTimeMillis();
        SameNSTest test = new SameNSTest();
        assertNotThrows(test);

        long end = System.currentTimeMillis();
        log.info("Same NS Single took " + (end - start) + " ms");
    }

    @Test
    public void testGetSameNamespaceMultiThread() throws Exception {
        long start = System.currentTimeMillis();
        runMultiThreadedTest(new SameNSTest());

        long end = System.currentTimeMillis();
        log.info("Different NS Single took " + (end - start) + " ms");
    }

    @Test
    public void testGetNewNamespaceSingleThread() {
        long start = System.currentTimeMillis();
        DifferentNSTest test = new DifferentNSTest();
        assertNotThrows(test);

        long end = System.currentTimeMillis();
        log.info("Same NS Multi took " + (end - start) + " ms");
    }

    @Test
    public void testGetNewNamespaceMultiThread() throws Exception {
        long start = System.currentTimeMillis();
        runMultiThreadedTest(new DifferentNSTest());

        long end = System.currentTimeMillis();
        log.info("Different NS Multi took " + (end - start) + " ms");
    }

    private void runMultiThreadedTest(Runnable test) throws Exception {
        Stream.generate(() -> new Thread(test))
              .limit(THREADCOUNT)
              .peek(Thread::start)
              .forEach(t -> {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
              });
    }

    private static void assertNotThrows(Runnable test) {
        try {
            test.run();
        } catch (Exception e) {
            fail("Test should not throw any exception");
        }
    }

    private static class SameNSTest implements Runnable {
        @Override
        public void run() {
            NamespaceCache cache = new NamespaceCache();

            for (int i = 0; i < ITERATIONCOUNT; i++) {
                Namespace ns = cache.get("prefix", "uri");
            }
        }
    }

    private static class DifferentNSTest implements Runnable {
        @Override
        public void run() {
            NamespaceCache cache = new NamespaceCache();

            for (int i = 0; i < ITERATIONCOUNT; i++) {
                Namespace ns = cache.get("prefix", Integer.toString(i));
            }
        }
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
