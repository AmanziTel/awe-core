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

package org.amanzi.neo.model.distribution.types.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.types.ranges.impl.SimpleRange;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.Filter;
import org.apache.log4j.Logger;

/**
 * Distribution for Enumerated properties (such as String and Boolean for example)
 * 
 * Creates range for each String value of property
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class EnumeratedDistribution extends AbstractDistribution<SimpleRange> {
    
    private static final Logger LOGGER = Logger.getLogger(EnumeratedDistribution.class);
    
    static final String STRING_DISTRIBUTION_NAME = "auto";
    
    /*
     * Maximum count of property values, if count > max that exception will be thrown
     */
    static final int MAX_PROPERTY_VALUE_COUNT = 100;
    
    private int count = 0;
    
    private static final Select[] POSSIBLE_SELECTS = new Select[] {Select.EXISTS};
    
    /**
     * @param model
     * @param nodeType
     * @param propertyName
     */
    public EnumeratedDistribution(IDistributionalModel model, INodeType nodeType, String propertyName) {
        super(model, nodeType, propertyName);
        setCanChangeColors(true);
    }

    @Override
    public String getName() {
        return STRING_DISTRIBUTION_NAME;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    protected void createRanges() {
        LOGGER.debug("start createRange()");
        
        //initialize count of all properties
        count = model.getPropertyCount(nodeType, propertyName);
        
        Set<String> uniqueValues = new HashSet<String>();
        Object[] propertyValues = model.getPropertyValues(nodeType, propertyName);
        for (Object value : propertyValues) {
            uniqueValues.add(value.toString());
            if (uniqueValues.size() > MAX_PROPERTY_VALUE_COUNT) {
                throw new IllegalArgumentException("Property values count more than " + MAX_PROPERTY_VALUE_COUNT);
            }
        }
        //initialize ranges
        for (Object value : propertyValues) {
            //we are sure that it's a string
            String sValue = value.toString();
            Serializable serValue = (Serializable) value;
            
            Filter filter = new Filter();
            filter.setExpression(nodeType, propertyName, serValue);
            ranges.add(new SimpleRange(sValue, filter));
        }
        
        LOGGER.debug("finish createRange()");
    }

    @Override
    public Select[] getPossibleSelects() {
        return POSSIBLE_SELECTS;
    }

    @Override
    protected Select getDefaultSelect() {
        return Select.EXISTS;
    }    

}
