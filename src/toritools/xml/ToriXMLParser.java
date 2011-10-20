package toritools.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Simple DOM XML Parser wrapper.
 *
 */
public class ToriXMLParser {

    /**
     * Parse an xml file into a DOM Document.
     *
     * @param file
     *            the file
     * @return null if error, otherwise a Document.
     */
    public static Document parse(final File file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document dom = null;
        try {
            db = dbf.newDocumentBuilder();
            dom = db.parse(file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dom;
    }
}
