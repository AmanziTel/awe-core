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
 *Skip tag handler
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class SkipTag implements IXmlTag {

    private final IXmlTag parent;
    private Integer stack;

/**
 * Constructor
 * @param parent - parent tag
 */
    public SkipTag(IXmlTag parent) {
        this.parent = parent;
        stack=0;
    }
    @Override
    public IXmlTag endElement(String localName, StringBuilder chars) {
        return --stack<0?parent:this;
        
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public IXmlTag startElement(String localName, Attributes attributes) {
        stack++;
        return this;
    }

}
