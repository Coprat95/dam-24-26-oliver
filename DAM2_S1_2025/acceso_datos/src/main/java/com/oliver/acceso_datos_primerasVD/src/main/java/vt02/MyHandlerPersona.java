package vt02;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandlerPersona extends DefaultHandler {
    private String currentElement = "";
    private String name = "";
    private String age = "";

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
    }


}
