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

package org.amanzi.neo.loader.sax_parsers;

import org.xml.sax.Attributes;

/**
 * <p>
 * Abstract Tag
 * </p>
 * 
 * @author Tsinkel_A
 * @since 1.0.0
 */
public abstract class AbstractTag implements IXmlTag {

    private final String tagName;
    protected final IXmlTag parent;

    /**
     * Constructor
     * 
     * @param tagName name of tag
     * @param parent - parent tag
     */
    protected AbstractTag(String tagName, IXmlTag parent) {
        this.tagName = tagName;
        this.parent = parent;
    }

    @Override
    public IXmlTag endElement(String localName, StringBuilder chars) {
        if (localName.equals(getName())) {
            return parent;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getName() {
        return tagName;
    }

    @Override
    public abstract IXmlTag startElement(String localName, Attributes attributes);

}
