/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.neoxml.io;

import org.neoxml.Attribute;
import org.neoxml.Branch;
import org.neoxml.CDATA;
import org.neoxml.Comment;
import org.neoxml.Document;
import org.neoxml.DocumentType;
import org.neoxml.Element;
import org.neoxml.Entity;
import org.neoxml.Namespace;
import org.neoxml.Node;
import org.neoxml.Text;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.util.XMLEventConsumer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * Writes neoxml {@link Node}s to a StAX event stream. In addition the <code>createXXX</code> methods are provided to
 * directly create STAX events
 * from neoxml nodes.
 *
 * @author Christian Niles
 */
public class STAXEventWriter {
    /**
     * The event stream to which events are written.
     */
    private XMLEventConsumer consumer;

    /**
     * The event factory used to construct events.
     */
    protected XMLEventFactory factory = XMLEventFactory.newInstance();

    private final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    public STAXEventWriter() {}

    /**
     * Constructs a <code>STAXEventWriter</code> that writes events to the
     * provided file.
     *
     * @param file The file to which events will be written.
     * @throws XMLStreamException If an error occurs creating an event writer from the file.
     * @throws IOException        If an error occurs openin the file for writing.
     */
    @SuppressWarnings("resource")
    public STAXEventWriter(File file) throws XMLStreamException, IOException {
        consumer = outputFactory.createXMLEventWriter(new FileWriter(file));
    }

    /**
     * Constructs a <code>STAXEventWriter</code> that writes events to the
     * provided character stream.
     *
     * @param writer The character stream to which events will be written.
     * @throws XMLStreamException If an error occurs constructing an event writer from the
     *                            character stream.
     */
    public STAXEventWriter(Writer writer) throws XMLStreamException {
        consumer = outputFactory.createXMLEventWriter(writer);
    }

    /**
     * Constructs a <code>STAXEventWriter</code> that writes events to the
     * provided stream.
     *
     * @param stream The output stream to which events will be written.
     * @throws XMLStreamException If an error occurs constructing an event writer from the
     *                            stream.
     */
    public STAXEventWriter(OutputStream stream) throws XMLStreamException {
        consumer = outputFactory.createXMLEventWriter(stream);
    }

    /**
     * Constructs a <code>STAXEventWriter</code> that writes events to the
     * provided event stream.
     *
     * @param consumer The event stream to which events will be written.
     */
    public STAXEventWriter(XMLEventConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Returns a reference to the underlying event consumer to which events are
     * written.
     *
     * @return The underlying event consumer to which events are written.
     */
    public XMLEventConsumer getConsumer() {
        return consumer;
    }

    /**
     * Sets the underlying event consumer to which events are written.
     *
     * @param consumer The event consumer to which events should be written.
     */
    public void setConsumer(XMLEventConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Returns a reference to the event factory used to construct STAX events.
     *
     * @return The event factory used to construct STAX events.
     */
    public XMLEventFactory getEventFactory() {
        return factory;
    }

    /**
     * Sets the event factory used to construct STAX events.
     *
     * @param eventFactory The new event factory.
     */
    public void setEventFactory(XMLEventFactory eventFactory) {
        this.factory = eventFactory;
    }

    /**
     * Writes a neoxml {@link Node}to the stream. This method is simply a
     * gateway to the overloaded methods such as {@link #writeElement(Element)}.
     *
     * @param n The neoxml {@link Node}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeNode(Node n) throws XMLStreamException {
        switch (n.getNodeTypeEnum()) {
            case ELEMENT_NODE:
                writeElement((Element) n);

                break;

            case TEXT_NODE:
                writeText((Text) n);

                break;

            case ATTRIBUTE_NODE:
                writeAttribute((Attribute) n);

                break;

            case NAMESPACE_NODE:
                writeNamespace((Namespace) n);

                break;

            case COMMENT_NODE:
                writeComment((Comment) n);

                break;

            case CDATA_SECTION_NODE:
                writeCDATA((CDATA) n);

                break;

            case PROCESSING_INSTRUCTION_NODE:
                writeProcessingInstruction((org.neoxml.ProcessingInstruction) n);

                break;

            case ENTITY_REFERENCE_NODE:
                writeEntity((Entity) n);

                break;

            case DOCUMENT_NODE:
                writeDocument((Document) n);

                break;

            case DOCUMENT_TYPE_NODE:
                writeDocumentType((DocumentType) n);

                break;

            default:
                throw new XMLStreamException("Unsupported neoxml node: " + n);
        }
    }

    /**
     * Writes each child node within the provided {@link Branch}instance. This
     * method simply iterates through the {@link Branch}'s nodes and calls {@link #writeNode(Node)}.
     *
     * @param branch The node whose children will be written to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeChildNodes(Branch branch) throws XMLStreamException {
        for (int i = 0, s = branch.nodeCount(); i < s; i++) {
            Node n = branch.node(i);
            writeNode(n);
        }
    }

    /**
     * Writes a neoxml {@link Element}node and its children to the stream.
     *
     * @param elem The {@link Element}node to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeElement(Element elem) throws XMLStreamException {
        consumer.add(createStartElement(elem));
        writeChildNodes(elem);
        consumer.add(createEndElement(elem));
    }

    /**
     * Constructs a STAX {@link StartElement}event from a neoxml {@link Element}.
     *
     * @param elem The {@link Element}from which to construct the event.
     * @return The newly constructed {@link StartElement}event.
     */
    public StartElement createStartElement(Element elem) {
        // create name
        QName tagName = createQName(elem.getQName());

        // create attribute & namespace iterators
        Iterator<javax.xml.stream.events.Attribute> attrIter = new AttributeIterator(elem.attributeIterator());
        Iterator<javax.xml.stream.events.Namespace> nsIter = new NamespaceIterator(elem.declaredNamespaces().iterator());

        // create start event
        return factory.createStartElement(tagName, attrIter, nsIter);
    }

    /**
     * Constructs a STAX {@link EndElement}event from a neoxml {@link Element}.
     *
     * @param elem The {@link Element}from which to construct the event.
     * @return The newly constructed {@link EndElement}event.
     */
    public EndElement createEndElement(Element elem) {
        QName tagName = createQName(elem.getQName());
        Iterator<javax.xml.stream.events.Namespace> nsIter = new NamespaceIterator(elem.declaredNamespaces().iterator());

        return factory.createEndElement(tagName, nsIter);
    }

    /**
     * Writes a neoxml {@link Attribute}to the stream.
     *
     * @param attr The {@link Attribute}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeAttribute(Attribute attr) throws XMLStreamException {
        consumer.add(createAttribute(attr));
    }

    /**
     * Constructs a STAX {@link javax.xml.stream.events.Attribute}event from a
     * neoxml {@link Attribute}.
     *
     * @param attr The {@link Attribute}from which to construct the event.
     * @return The newly constructed {@link javax.xml.stream.events.Attribute} event.
     */
    public javax.xml.stream.events.Attribute createAttribute(Attribute attr) {
        QName attrName = createQName(attr.getQName());
        String value = attr.getValue();

        return factory.createAttribute(attrName, value);
    }

    /**
     * Writes a neoxml {@link Namespace}to the stream.
     *
     * @param ns The {@link Namespace}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeNamespace(Namespace ns) throws XMLStreamException {
        consumer.add(createNamespace(ns));
    }

    /**
     * Constructs a STAX {@link javax.xml.stream.events.Namespace}event from a
     * neoxml {@link Namespace}.
     *
     * @param ns The {@link Namespace}from which to construct the event.
     * @return The constructed {@link javax.xml.stream.events.Namespace}event.
     */
    public javax.xml.stream.events.Namespace createNamespace(Namespace ns) {
        String prefix = ns.getPrefix();
        String uri = ns.getURI();

        return factory.createNamespace(prefix, uri);
    }

    /**
     * Writes a neoxml {@link Text}to the stream.
     *
     * @param text The {@link Text}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeText(Text text) throws XMLStreamException {
        consumer.add(createCharacters(text));
    }

    /**
     * Constructs a STAX {@link Characters}event from a neoxml {@link Text}.
     *
     * @param text The {@link Text}from which to construct the event.
     * @return The constructed {@link Characters}event.
     */
    public Characters createCharacters(Text text) {
        return factory.createCharacters(text.getText());
    }

    /**
     * Writes a neoxml {@link CDATA}to the event stream.
     *
     * @param cdata The {@link CDATA}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeCDATA(CDATA cdata) throws XMLStreamException {
        consumer.add(createCharacters(cdata));
    }

    /**
     * Constructs a STAX {@link Characters}event from a neoxml {@link CDATA}.
     *
     * @param cdata The {@link CDATA}from which to construct the event.
     * @return The newly constructed {@link Characters}event.
     */
    public Characters createCharacters(CDATA cdata) {
        return factory.createCData(cdata.getText());
    }

    /**
     * Writes a neoxml {@link Comment}to the stream.
     *
     * @param comment The {@link Comment}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeComment(Comment comment) throws XMLStreamException {
        consumer.add(createComment(comment));
    }

    /**
     * Constructs a STAX {@link javax.xml.stream.events.Comment}event from a
     * neoxml {@link Comment}.
     *
     * @param comment The {@link Comment}from which to construct the event.
     * @return The constructed {@link javax.xml.stream.events.Comment}event.
     */
    public javax.xml.stream.events.Comment createComment(Comment comment) {
        return factory.createComment(comment.getText());
    }

    /**
     * Writes a neoxml {@link ProcessingInstruction}to the stream.
     *
     * @param pi The {@link ProcessingInstruction}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeProcessingInstruction(org.neoxml.ProcessingInstruction pi)
            throws XMLStreamException {
        consumer.add(createProcessingInstruction(pi));
    }

    /**
     * Constructs a STAX {@link javax.xml.stream.events.ProcessingInstruction} event from a DOM4J
     * {@link ProcessingInstruction}.
     *
     * @param pi The {@link ProcessingInstruction}from which to construct the
     *           event.
     * @return The constructed {@link javax.xml.stream.events.ProcessingInstruction} event.
     */
    public ProcessingInstruction createProcessingInstruction(
            org.neoxml.ProcessingInstruction pi) {
        String target = pi.getTarget();
        String data = pi.getText();

        return factory.createProcessingInstruction(target, data);
    }

    /**
     * Writes a neoxml {@link Entity}to the stream.
     *
     * @param entity The {@link Entity}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeEntity(Entity entity) throws XMLStreamException {
        consumer.add(createEntityReference(entity));
    }

    /**
     * Constructs a STAX {@link EntityReference}event from a neoxml {@link Entity}.
     *
     * @param entity The {@link Entity}from which to construct the event.
     * @return The constructed {@link EntityReference}event.
     */
    private EntityReference createEntityReference(Entity entity) {
        return factory.createEntityReference(entity.getName(), null);
    }

    /**
     * Writes a neoxml {@link DocumentType}to the stream.
     *
     * @param docType The {@link DocumentType}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeDocumentType(DocumentType docType)
            throws XMLStreamException {
        consumer.add(createDTD(docType));
    }

    /**
     * Constructs a STAX {@link DTD}event from a neoxml {@link DocumentType}.
     *
     * @param docType The {@link DocumentType}from which to construct the event.
     * @return The constructed {@link DTD}event.
     * @throws RuntimeException DOCUMENT ME!
     */
    public DTD createDTD(DocumentType docType) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out)) {

            docType.write(writer);
            writer.flush();

            return factory.createDTD(out.toString());
        } catch (IOException e) {
            // this will not happen because we use ByteArrayOutputStream as an uderlying stream
            throw new IllegalStateException(e);
        }


    }

    /**
     * Writes a neoxml {@link Document}node, and all its contents, to the
     * stream.
     *
     * @param doc The {@link Document}to write to the stream.
     * @throws XMLStreamException If an error occurs writing to the stream.
     */
    public void writeDocument(Document doc) throws XMLStreamException {
        consumer.add(createStartDocument(doc));

        writeChildNodes(doc);

        consumer.add(createEndDocument(doc));
    }

    /**
     * Constructs a STAX {@link StartDocument}event from a neoxml {@link Document}.
     *
     * @param doc The {@link Document}from which to construct the event.
     * @return The constructed {@link StartDocument}event.
     */
    public StartDocument createStartDocument(Document doc) {
        String encoding = doc.getXMLEncoding();

        if (encoding != null) {
            return factory.createStartDocument(encoding);
        } else {
            return factory.createStartDocument();
        }
    }

    /**
     * Constructs a STAX {@link EndDocument}event from a neoxml {@link Document}.
     *
     * @param doc The {@link Document}from which to construct the event.
     * @return The constructed {@link EndDocument}event.
     */
    public EndDocument createEndDocument(Document doc) {
        return factory.createEndDocument();
    }

    /**
     * Constructs a STAX {@link QName}from a neoxml {@link org.neoxml.QName}.
     *
     * @param qname The {@link org.neoxml.QName}from which to construct the STAX {@link QName}.
     * @return The constructed {@link QName}.
     */
    public QName createQName(org.neoxml.QName qname) {
        return new QName(qname.getNamespaceURI(), qname.getName(), qname
                .getNamespacePrefix());
    }

    /**
     * Internal {@link Iterator}implementation used to pass neoxml {@link Attribute}s to the stream.
     */
    private class AttributeIterator implements Iterator<javax.xml.stream.events.Attribute> {
        /**
         * The underlying neoxml attribute iterator.
         */
        private final Iterator<Attribute> iter;

        public AttributeIterator(Iterator<Attribute> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public javax.xml.stream.events.Attribute next() {
            Attribute attr = iter.next();
            QName attrName = createQName(attr.getQName());
            String value = attr.getValue();

            return factory.createAttribute(attrName, value);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Internal {@link Iterator} implementation used to pass neoxml {@link Namespace}s to the stream.
     */
    private class NamespaceIterator implements Iterator<javax.xml.stream.events.Namespace> {
        private final Iterator<Namespace> iter;

        public NamespaceIterator(Iterator<Namespace> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public javax.xml.stream.events.Namespace next() {
            Namespace ns = iter.next();
            String prefix = ns.getPrefix();
            String nsURI = ns.getURI();

            return factory.createNamespace(prefix, nsURI);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
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
