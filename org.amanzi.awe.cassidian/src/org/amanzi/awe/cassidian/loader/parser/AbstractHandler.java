/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.cassidian.loader.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amanzi.awe.cassidian.structure.IXmlTag;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * common Parser
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public abstract class AbstractHandler extends DefaultHandler {
    private Logger LOGGER = Logger.getLogger(AbstractHandler.class);
    protected static String currentElement = "";
    protected boolean isOpenInnerTag = false;
    protected static Map<String, Class< ? extends IXmlTag>> innerElements;
    protected static Map<String, Class< ? extends IXmlTag>> mainElements;
    protected static SAXParserFactory factory;
    protected static SAXParser saxParser;

    protected static IXmlTag currentTag;
    protected IXmlTag parentTag = new TNSElement();
    protected static IXmlTag wrapperTag;
    protected static TNSElement tns;

    StringBuffer charBuilder;

    public abstract IXmlTag parseElement(File file);

    private void resetElementsMap() {
        if (innerElements != null) {
            innerElements.clear();

        } else {
            innerElements = new HashMap<String, Class< ? extends IXmlTag>>();
        }
        if (mainElements != null) {
            mainElements.clear();
        } else {
            mainElements = new HashMap<String, Class< ? extends IXmlTag>>();
        }
    }

    private void resetSax() {
        if (factory == null) {
            factory = SAXParserFactory.newInstance();
        }
        if (saxParser == null) {
            try {
                saxParser = factory.newSAXParser();
            } catch (ParserConfigurationException e) {
                LOGGER.info("Throw exception while trying to configure SAX parser ", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (SAXException e) {
                LOGGER.info("Throw exception while trying to get sax parser ", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }

    protected AbstractHandler() {
        resetElementsMap();
        resetSax();
    }

    protected void parse(File file) {
        try {
            saxParser.parse(file, this);
        } catch (SAXException e) {
            tns = null;
            return;

        } catch (IOException e) {
            tns = null;
            return;
        }
    }

    public void startElement(String uri, String localName, String tagName, Attributes attributes) throws SAXException {
        try {
            charBuilder = null;
            charBuilder = new StringBuffer();
            currentElement = tagName;

            if (mainElements.containsKey(tagName)) {
                wrapperTag = mainElements.get(tagName).newInstance();
            } else if (innerElements.containsKey(tagName)) {
                if (isOpenInnerTag) {
                    parentTag = currentTag;
                }
                currentTag = innerElements.get(tagName).newInstance();
                parentTag.setValueByTagType(tagName, currentTag);
                isOpenInnerTag = true;
            }

        } catch (InstantiationException e) {
            currentElement = "";
            LOGGER.info("Throw exception on element " + tagName, e);
        } catch (IllegalAccessException e) {
            LOGGER.info("Throw exception on element " + tagName, e);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (ch != null) {
            charBuilder.append(ch, start, length);
        }

    }

    public void endElement(String uri, String localName, String tagName) throws SAXException {
        if (!innerElements.containsKey(tagName) && !mainElements.containsKey(tagName) && !tagName.equals(tns.getType())) {
            if (charBuilder.length() == 0) {
                currentTag.setValueByTagType(currentElement, null);
            } else {
                currentTag.setValueByTagType(currentElement, charBuilder);
            }
        }

        if ((parentTag.getClass().isInstance(tns))) {
            parentTag = currentTag;
        }

        if (innerElements.containsKey(tagName)) {
            if (isOpenInnerTag) {
                wrapperTag.setValueByTagType(tagName, currentTag);
            } else {
                wrapperTag.setValueByTagType(tagName, parentTag);
            }
        } else if (mainElements.containsKey(tagName)) {
            tns.setValueByTagType(tagName, wrapperTag);
        }
        if (innerElements.containsKey(tagName)) {
            isOpenInnerTag = false;
        }

    }
}
