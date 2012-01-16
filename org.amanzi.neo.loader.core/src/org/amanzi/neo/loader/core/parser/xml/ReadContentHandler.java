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

package org.amanzi.neo.loader.core.parser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class ReadContentHandler extends DefaultHandler {
    
    public StringBuilder chars;
    private IXmlTag currentTag;
    private final IXmlTagFactory factory;

    /**
     * Constructor
     * 
     * @param factory - tag factory for creating roots tag
     */
    public ReadContentHandler(IXmlTagFactory factory) {
        this.factory = factory;
        chars = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (chars != null) {
            for (int i = 0; i < length; i++) {
                chars.append(ch[start + i]);
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        if (currentTag == null) {
            currentTag = factory.createInstance(localName, attributes);
        } else {
            currentTag = currentTag.startElement(localName, attributes);
        }
        if (currentTag != null) {
            chars = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        
        if (currentTag != null) {
            currentTag = currentTag.endElement(localName, chars != null ? chars : new StringBuilder());
        }
        
        chars = null;
    }

}
