/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.io;

import org.neoxml.DocumentFactory;
import org.neoxml.Element;
import org.neoxml.ElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * This extension of the SAXContentHandler writes SAX events immediately to the
 * provided XMLWriter, unless some {@link org.neoxml.ElementHandler} is still
 * handling the current Element.
 *
 * @author Wonne Keysers (Realsoftware.be)
 * @see org.neoxml.io.SAXContentHandler
 */
class SAXModifyContentHandler extends SAXContentHandler {
    private XMLWriter xmlWriter;

    public SAXModifyContentHandler() {}

    public SAXModifyContentHandler(DocumentFactory documentFactory) {
        super(documentFactory);
    }

    public SAXModifyContentHandler(DocumentFactory documentFactory, ElementHandler elementHandler) {
        super(documentFactory, elementHandler);
    }

    public SAXModifyContentHandler(DocumentFactory documentFactory, ElementHandler elementHandler, ElementStack elementStack) {
        super(documentFactory, elementHandler, elementStack);
    }

    public void setXMLWriter(XMLWriter writer) {
        this.xmlWriter = writer;
    }

    @Override
    public void startCDATA() throws SAXException {
        super.startCDATA();

        if (noActiveHandlers() && (xmlWriter != null)) {
            xmlWriter.startCDATA();
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        super.startDTD(name, publicId, systemId);

        if (xmlWriter != null) {
            xmlWriter.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        super.endDTD();

        if (xmlWriter != null) {
            xmlWriter.endDTD();
        }
    }

    @Override
    public void comment(char[] characters, int parm2, int parm3)
            throws SAXException {
        super.comment(characters, parm2, parm3);

        if (noActiveHandlers() && (xmlWriter != null)) {
            xmlWriter.comment(characters, parm2, parm3);
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        super.startEntity(name);

        if (xmlWriter != null) {
            xmlWriter.startEntity(name);
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        super.endCDATA();

        if (noActiveHandlers() && (xmlWriter != null)) {
            xmlWriter.endCDATA();
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        super.endEntity(name);

        if (xmlWriter != null) {
            xmlWriter.endEntity(name);
        }
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notation) throws SAXException {
        super.unparsedEntityDecl(name, publicId, systemId, notation);

        if (noActiveHandlers() && (xmlWriter != null)) {
            xmlWriter.unparsedEntityDecl(name, publicId, systemId, notation);
        }
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        super.notationDecl(name, publicId, systemId);

        if (xmlWriter != null) {
            xmlWriter.notationDecl(name, publicId, systemId);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        if (noActiveHandlers() && (xmlWriter != null)) {
            xmlWriter.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();

        if (xmlWriter != null) {
            xmlWriter.startDocument();
        }
    }

    @Override
    public void ignorableWhitespace(char[] parm1, int parm2, int parm3) throws SAXException {
        super.ignorableWhitespace(parm1, parm2, parm3);

        if (noActiveHandlers() && (xmlWriter != null)) {
            xmlWriter.ignorableWhitespace(parm1, parm2, parm3);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);

        if (noActiveHandlers() && (xmlWriter != null)) {
            xmlWriter.processingInstruction(target, data);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);

        if (xmlWriter != null) {
            xmlWriter.setDocumentLocator(locator);
        }
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        super.skippedEntity(name);

        if (noActiveHandlers() && xmlWriter != null) {
            xmlWriter.skippedEntity(name);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();

        if (xmlWriter != null) {
            xmlWriter.endDocument();
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);

        if (xmlWriter != null) {
            xmlWriter.startPrefixMapping(prefix, uri);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        ElementHandler currentHandler = getElementStack().getDispatchHandler().getHandler(getElementStack().getPath());

        super.endElement(uri, localName, qName);

        if (noActiveHandlers() && xmlWriter != null) {
            if (currentHandler == null) {
                xmlWriter.endElement(uri, localName, qName);
            } else if (currentHandler instanceof SAXModifyElementHandler) {
                SAXModifyElementHandler modifyHandler = (SAXModifyElementHandler) currentHandler;
                Element modifiedElement = modifyHandler.getModifiedElement();

                try {
                    xmlWriter.write(modifiedElement);
                } catch (IOException ex) {
                    throw new SAXModifyException(ex);
                }
            }
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);

        if (xmlWriter != null) {
            xmlWriter.endPrefixMapping(prefix);
        }
    }

    @Override
    public void characters(char[] parm1, int parm2, int parm3) throws SAXException {
        super.characters(parm1, parm2, parm3);

        if (noActiveHandlers() && xmlWriter != null) {
            xmlWriter.characters(parm1, parm2, parm3);
        }
    }

    protected XMLWriter getXMLWriter() {
        return this.xmlWriter;
    }

    private boolean noActiveHandlers() {
        DispatchHandler handler = getElementStack().getDispatchHandler();
        return handler.getActiveHandlerCount() <= 0;
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
