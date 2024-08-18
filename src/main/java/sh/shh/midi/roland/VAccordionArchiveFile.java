package sh.shh.midi.roland;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class VAccordionArchiveFile {
    private final DocumentBuilder xmlBuilder;
    private Document xml;
    private InputStream stio;
    private File file;

    private VAccordionArchiveFile() {
        try {
            xmlBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public VAccordionArchiveFile(String filename) throws IOException, SAXException {
        this(new File(filename));
    }

    public VAccordionArchiveFile(File file) throws IOException, SAXException {
        this(new FileInputStream(file));
        this.file = file;
    }

    public VAccordionArchiveFile(InputStream stio) throws IOException, SAXException {
        this();
        this.stio = stio;
        String xmlData = extractXmlString();
        xml = xmlBuilder.parse(new InputSource(new StringReader(xmlData)));
    }

    public String getFilename() {
        return file.getName();
    }

    public Path getPath() {
        return file.toPath();
    }

    public Document getXml() {
        return xml;
    }

    public void writeXml(String outputFilename) throws IOException, TransformerException {
        writeXml(new File(outputFilename));
    }

    public void writeXml(File outputFile) throws TransformerException, IOException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        FileWriter writer = new FileWriter(outputFile, StandardCharsets.UTF_8);
        transformer.transform(new DOMSource(xml), new StreamResult(writer));
        writer.close();
    }

    String extractXmlString() throws IOException {
        byte[] utf8bom = stio.readNBytes(3); // first 3 bytes are UTF-8 BOM
        if (!Arrays.equals(utf8bom, new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF})) {
            throw new UTFDataFormatException(String.format("Invalid VAccordion archive file; starting bytes should" +
                    " be 'efbbbf', found '%02x%02x%02x'", utf8bom[0], utf8bom[1], utf8bom[2]));
        }
        int xmlDocLength = ByteBuffer.wrap(stio.readNBytes(4)).getInt(); // next 4 bytes are int length of XML file
        return new String(stio.readNBytes(xmlDocLength), StandardCharsets.UTF_8);
    }
}
