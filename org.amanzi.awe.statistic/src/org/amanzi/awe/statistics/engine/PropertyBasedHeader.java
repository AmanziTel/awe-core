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

package org.amanzi.awe.statistics.engine;

import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class PropertyBasedHeader implements IStatisticsHeader {
    private String property;
    private String name;

    public PropertyBasedHeader() {

    }

    /**
     * @param property
     */
    public PropertyBasedHeader(String property) {
        this.property = property;
        this.name = property;
    }

    /**
     * @param property
     */
    public PropertyBasedHeader(String property, String name) {
        this.property = property;
        this.name = name;
    }

    @Override
    public Number calculate(IDatasetService service, Node node) {
        Object value = node.getProperty(property);
        if (value instanceof Number) {
            return (Number)value;
        } else {
            return Double.valueOf(value.toString());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the property.
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property The property to set.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Property: %s", name, property);
    }

}
