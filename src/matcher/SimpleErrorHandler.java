package matcher;

import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;

/**
 * Created by lpoon2 on 3/1/2017.
 */
public class SimpleErrorHandler implements ErrorHandler {
    public void warning(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }
}
