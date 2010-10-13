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
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public class PropertyBasedHeader implements IStatisticsHeader {
    private String property;
    

    /**
     * @param property
     */
    public PropertyBasedHeader(String property) {
        this.property = property;
    }

    @Override
    public Number calculate(IDatasetService service, Node node) {
        Object value = node.getProperty(property);
        if (value instanceof Number) {
            return (Number) value;
        }else{
            return Double.valueOf(value.toString());
        }
    }

    @Override
    public String getName() {
        return property;
    }

}
