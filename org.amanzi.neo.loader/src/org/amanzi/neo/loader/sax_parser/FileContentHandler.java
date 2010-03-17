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

package org.amanzi.neo.loader.sax_parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
/**
 * <p>
 * SAX parsing handler
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class FileContentHandler extends DefaultHandler {
    public String tag = null;
    public StringBuilder chars;
    private final Tags[] rootTags;
    private Tags rootTag;

    /**
     * constructor
     * @param rootTag- root tags for start handling
     */
    public FileContentHandler(Tags[] rootTag) {
        this.rootTags = rootTag;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (tag == null || rootTag == null) {
            return;
        }
        for (int i = 0; i < length; i++) {
            chars.append(ch[start + i]);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        tag = localName;
        if (rootTag == null) {
            rootTag = findRootTag(tag);
            if (rootTag != null) {
                chars = new StringBuilder();
                rootTag.startElement(tag, attributes);
            }
            tag = null;
            return;
        } else {
            chars = new StringBuilder();
            rootTag.startElement(tag, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        tag = null;
        if (rootTag != null) {
            if (rootTag.getTagName().equals(localName)) {
                rootTag = null;
            } else {
                rootTag.endElement(localName, chars);
            }
        }
    }

    /**
     * find root tag by id
     * 
     * @param localName - tag name;
     * @return tag or null
     */
    public Tags findRootTag(String localName) {
        for (Tags tag : rootTags) {
            if (tag.getTagName().equals(localName)) {
                tag.initialize();
                return tag;
            }
        }
        return null;
    }
}
