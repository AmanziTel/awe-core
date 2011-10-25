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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.IRange;
import org.amanzi.neo.services.enums.INodeType;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * Abstract class for Distributions
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractDistribution<T extends IRange> implements IDistribution<T> {
    
    private static final Logger LOGGER = Logger.getLogger(AbstractDistribution.class);
    
    /*
     * Analyzed Model 
     */
    protected IDistributionalModel model;
    
    /*
     * Type of Node to Analyze
     */
    protected INodeType nodeType;
    
    /*
     * Name of Property to Analyze
     */
    protected String propertyName;
    
    /*
     * Were Ranges already created
     */
    private boolean isInitialized = false;
    
    /*
     * List of ranges
     */
    protected List<T> ranges = new ArrayList<T>();
    
    /*
     * Type of Select
     */
    protected Select select;
    
    /**
     * Default constructor
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     */
    protected AbstractDistribution(IDistributionalModel model, INodeType nodeType, String propertyName) {
        LOGGER.debug("start new AbstractDistribution(<" + model + ">, <" + nodeType + ">, <" + propertyName + ">)");
        
        //check input
        if (model == null) {
            LOGGER.error("Analyzed model cannot be null");
            throw new IllegalArgumentException("Analyzed model cannot be null");
        }
        if (nodeType == null) {
            LOGGER.error("NodeType cannot be null");
            throw new IllegalArgumentException("NodeType cannot be null");
        }
        if (propertyName == null || propertyName.isEmpty()) {
            LOGGER.error("PropertyName to Analyze cannot be null or empty");
            throw new IllegalArgumentException("PropertyName to Analyze cannot be null or empty");
        }
        
        this.model = model;
        this.nodeType = nodeType;
        this.propertyName = propertyName;
        
        LOGGER.debug("finish new AbstractDistribution()");
    }

    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public void init() {
        //distribution can came from Cache, so initialize it only once
        if (!isInitialized) {
            isInitialized = true;
            createRanges();
        }
    }
    
    @Override
    public List<T> getRanges() {
        return ranges;
    }
    
    /**
     * Method to Create Ranges of Distribution
     */
    protected abstract void createRanges();
    


    @Override
    public INodeType getNodeType() {
        return nodeType;
    }
    
    /**
     * Returns Default Select for this Distribution
     *
     * @return
     */
    protected abstract Select getDefaultSelect();
    
    @Override
    public void setSelect(Select select) {
        if (ArrayUtils.contains(getPossibleSelects(), select)) {
            if (this.select != select) {
                this.select = select;
                isInitialized = false;
            }
        } else {
            throw new IllegalArgumentException("Cannot set Select <" + select + "> for Distribution <" + this + ">.");
        }
    }
    
    @Override
    public String getPropertyName() {
        return propertyName;
    }

}
