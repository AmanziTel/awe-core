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

/**
 * <p>
 * abstract TAG of OSS  XML data
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public abstract class Tags {
    protected final String tagName;

    /**
     * constructor
     * 
     * @param tagName tag name
     */
    public Tags(String tagName) {
        this.tagName = tagName;
    }

    /**
     * handle start element
     * 
     * @param tag - start tag
     * @param attributes TODO
     */
    public void startElement(String tag, Attributes attributes) {
    }
    /**
     * handle start element
     * 
     * @param tag - start tag
     * @param attributes TODO
     */
    public void closedSubElement(String tag) {
    }

    /**
     *initialize tags
     */
    public void initialize() {
    }

    /**
     * Handle end element
     * 
     * @param tag - tag
     * @param builder - string in current element
     */
    public abstract void endElement(String tag, StringBuilder builder);

    /**
     * @return Returns the tagName.
     */
    public String getTagName() {
        return tagName;
    }


}
