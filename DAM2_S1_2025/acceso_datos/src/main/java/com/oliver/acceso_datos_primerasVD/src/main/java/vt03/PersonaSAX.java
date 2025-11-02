package vt03;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

/**
 * Clase que implementa un parser SAX para procesar un archivo XML
 * con elementos &lt; person&gt; , &lt;name&gt; y &lt;age&gt;.
 * Muestra por consola el contenido de cada persona encontrada.
 *
 * Autor: Oliver
 */
public class PersonaSAX extends DefaultHandler {
    /** Bandera para indicar si se está procesando el elemento */
    boolean bName = false;
    /** Bandera para indicar si se está procesando el elemento */
    boolean bAge = false;

    /** Se ejecuta al encontrar una etiqueta de apertura.
     *
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @throws SAXException : si ocurre un error durante el análisis SAX
     *
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("person".equals(qName)) {
            System.out.println("Start Element: " + qName);
        } else if ("name".equals(qName)) {
            bName = true;
        } else if ("age".equals(qName)) {
            bAge = true;
        }
    }

    /** Se ejecuta al encontrar una etiqueta de cierre de elemento
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @throws SAXException si ocurre un error durante el análisis SAX
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("person".equals(qName)) {
            System.out.println("End Element: " + qName);
        }
    }

    /**
     * Se ejecuta cuando se encuentra texto dentro de un elemento
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @throws SAXException si ocurre un error durante el análisis SAX
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String content = new String(ch, start, length).trim();
        if (!content.isEmpty()) { // Si el contenido no está vacío haz esto
            if (bName) {
                System.out.println(content);
                bName = false;
            } else if (bAge) {
                System.out.println(content);
                bAge = false;
            }
        }
    }

    /**
     * Método principal que lanza el parser SAX sobre un archivo XML
     *
     * @param args (no se usan)
     */
    public static void main(String[] args) {
        try {
            File inputFile = new File("src/vt03/xmlFile.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            PersonaSAX userHandler = new PersonaSAX();
            saxParser.parse(inputFile, userHandler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error al procesar el XML :" + e.getMessage());
        }
    }
}
