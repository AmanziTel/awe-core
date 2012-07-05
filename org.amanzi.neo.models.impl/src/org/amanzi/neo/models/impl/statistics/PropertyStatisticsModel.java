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

package org.amanzi.neo.models.impl.statistics;

import java.util.Set;

import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.IStatisticsService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyStatisticsModel extends AbstractModel implements IPropertyStatisticsModel {

    private final IStatisticsService statisticsService;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public PropertyStatisticsModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IStatisticsService statisticsService) {
        super(nodeService, generalNodeProperties);

        this.statisticsService = statisticsService;
    }

    @Override
    public void finishUp() throws ModelException {
        // TODO Auto-generated method stub

    }

    @Override
    public void indexProperty(final INodeType nodeType, final String property, final Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<String> getPropertyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getPropertyNames(final INodeType nodeType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCount(final INodeType nodeType) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Set<Object> getValues(final INodeType nodeType, final String property) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPropertyCount(final INodeType nodeType, final String property, final Object value) {
        // TODO Auto-generated method stub
        return 0;
    }

}
