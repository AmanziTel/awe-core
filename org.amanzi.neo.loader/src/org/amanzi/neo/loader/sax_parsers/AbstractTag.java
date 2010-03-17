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
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public abstract class AbstractTag implements IXmlTag {

    private final String tagName;
    private final IXmlTag parent;
    private IXmlTag child;

    protected AbstractTag(String tagName, IXmlTag parent) {
        this.tagName = tagName;
        this.parent = parent;
        child = null;
    }

    @Override
    public IXmlTag endElement(String localName, StringBuilder chars) {
            if (tagName.equals(localName)) {
                closeCurrentElement(chars);
                return parent;
            } else {
                closeNewTag(localName, chars);
                return this;
            }
    }

    /**
     * @param localName
     * @param chars
     */
    protected void closeNewTag(String localName, StringBuilder chars) {
        throw new IllegalArgumentException(String.format("Wrong handle closing of tag: %s", localName));
    }

    /**
     * @param chars
     */
    protected void closeCurrentElement(StringBuilder chars) {
        // do nothing
    }

    @Override
    public String getName() {
        return tagName;
    }

    @Override
    public abstract IXmlTag startElement(String localName, Attributes attributes);

}
