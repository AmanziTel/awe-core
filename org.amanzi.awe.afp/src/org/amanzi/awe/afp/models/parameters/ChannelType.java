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

package org.amanzi.awe.afp.models.parameters;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.filters.Filter;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public enum ChannelType implements IOptimizationParameterEnum {
    BCCH("BCCH") {
        
        private Filter filter;
        
        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new Filter();
            
                filter.setExpression(NodeTypes.TRX, INeoConstants.PROPERTY_BCCH_NAME, true);
            }
            
            return filter;
        }
    },
    TCH("TCH Non/BB Hopping") {
        
        private Filter filter;
        
        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new Filter();
                
                filter.setExpression(NodeTypes.TRX, INeoConstants.PROPERTY_BCCH_NAME, false);
                
                Filter hopTypeFilter = new Filter();
                hopTypeFilter.setExpression(NodeTypes.TRX, INeoConstants.PROPERTY_HOPPING_TYPE_NAME, 0);
                filter.addFilter(hopTypeFilter);
            }
            
            return filter;
        }
    },
    SY("TCH SY Hopping") {
        private Filter filter;
        
        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new Filter();
                
                filter.setExpression(NodeTypes.TRX, INeoConstants.PROPERTY_BCCH_NAME, false);
                
                Filter hopTypeFilter = new Filter();
                hopTypeFilter.setExpression(NodeTypes.TRX, INeoConstants.PROPERTY_HOPPING_TYPE_NAME, 1);
                filter.addFilter(hopTypeFilter);
            }
            
            return filter;
        }
    };

    private String text;
    
    private ChannelType(String text) {
        this.text = text;
    }
    
    @Override
    public String getText() {
        return text;
    }
    
    public abstract Filter getFilter();

}
