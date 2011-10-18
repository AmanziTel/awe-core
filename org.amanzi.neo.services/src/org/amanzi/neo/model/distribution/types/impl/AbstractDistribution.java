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

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.enums.INodeType;

/**
 * Abstract class for Distributions
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractDistribution implements IDistribution {
    
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
    
    /**
     * Default constructor
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     */
    protected AbstractDistribution(IDistributionalModel model, INodeType nodeType, String propertyName) {
        this.model = model;
        this.nodeType = nodeType;
        this.propertyName = propertyName;
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
    
    /**
     * Method to Create Ranges of Distribution
     */
    protected abstract void createRanges();

}
