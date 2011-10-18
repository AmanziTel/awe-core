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

import java.util.List;

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.IRange;
import org.amanzi.neo.services.enums.INodeType;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class StringDistribution extends AbstractDistribution {
    
    static final String STRING_DISTRIBUTION_NAME = "auto";

    /**
     * @param model
     * @param nodeType
     * @param propertyName
     */
    public StringDistribution(IDistributionalModel model, INodeType nodeType, String propertyName) {
        super(model, nodeType, propertyName);
    }

    @Override
    public String getName() {
        return STRING_DISTRIBUTION_NAME;
    }

    @Override
    public List<IRange> getRanges() {
        return null;
    }

    @Override
    public INodeType getNodeType() {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    protected void createRanges() {
    }

}
