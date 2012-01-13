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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class PropertyCollector implements IXmlTag {

    protected String openTag = null;
    protected final IXmlTag parent;
    protected final Map<String, String> propertyMap;
    protected final List<String> skippedTag;
    protected final List<PropertyCollector> subCollectors;
    protected final Boolean computeSubChild;
    protected final String tagName;

    /**
     * 
     */
    public PropertyCollector(String tagName, IXmlTag parent, Boolean computeSubChild) {
        this.tagName = tagName;
        this.parent = parent;
        this.computeSubChild = computeSubChild;
        openTag = null;
        propertyMap = new LinkedHashMap<String, String>();
        skippedTag = new LinkedList<String>();
        subCollectors = new LinkedList<PropertyCollector>();
    }

    @Override
    public IXmlTag endElement(String localName, StringBuilder chars) {
        if (openTag == null) {
            return parent;
        }
        propertyMap.put(localName, chars.toString());
        openTag = null;
        return this;
    }

    @Override
    public String getName() {
        return tagName;
    }

    @Override
    public IXmlTag startElement(String localName, Attributes attributes) {
        if (openTag != null) {
            if (computeSubChild) {
                PropertyCollector col = new PropertyCollector(openTag, this, computeSubChild);
                subCollectors.add(col);
                openTag = null;
                return col.startElement(localName, attributes);
            } else {
                skippedTag.add(localName);
                return new SkipTag(this);
            }
        } else {
            openTag = localName;
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

    /**
     * @return Returns the subCollectors.
     */
    public List<PropertyCollector> getSubCollectors() {
        return subCollectors;
    }

    public PropertyCollector getSubCollectorByName(String name) {
        for (PropertyCollector col : subCollectors) {
            if (col.getName().equals(name)) {
                return col;
            }
        }
        return null;
    }

    /**
     * Gets the recurcive sub collector by name.
     * 
     * @param name the subtag name
     * @return the recurcive sub collector by name
     */
    public PropertyCollector getRecurciveSubCollectorByName(String name) {
        PropertyCollector result = getSubCollectorByName(name);
        if (result == null) {
            for (PropertyCollector col : subCollectors) {
                result = col.getRecurciveSubCollectorByName(name);
                if (result != null) {
                    return result;
                }
            }
        }
        return result;
    }

}
