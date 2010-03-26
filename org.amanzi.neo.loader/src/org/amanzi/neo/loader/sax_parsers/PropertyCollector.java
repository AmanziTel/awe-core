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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

/**
 * <p>
 * Property collector Create map <tag name,value>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class PropertyCollector implements IXmlTag {

    private Boolean hasOpenTag;
    private final IXmlTag parent;
    private final Map<String, String> propertyMap;
    private final List<String> skippedTag;

    /**
     * 
     */
    public PropertyCollector(IXmlTag parent) {
        this.parent = parent;
        hasOpenTag = false;
        propertyMap = new LinkedHashMap<String, String>();
        skippedTag = new LinkedList<String>();
    }

    @Override
    public IXmlTag endElement(String localName, StringBuilder chars) {
        if (!hasOpenTag) {
            return parent;
        }
        propertyMap.put(localName, chars.toString());
        hasOpenTag = false;
        return this;
    }

    @Override
    public String getName() {
        return "property collector";
    }

    @Override
    public IXmlTag startElement(String localName, Attributes attributes) {
        if (hasOpenTag) {
            skippedTag.add(localName);
            return new SkipTag(this);
        } else {
            hasOpenTag = true;
            return this;
        }
    }

    /**
     * @return Returns the propertyMap.
     */
    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    /**
     * @return Returns the skippedTag.
     */
    public List<String> getSkippedTag() {
        return skippedTag;
    }

}
