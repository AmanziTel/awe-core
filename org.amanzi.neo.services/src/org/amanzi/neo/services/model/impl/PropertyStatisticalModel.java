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

package org.amanzi.neo.services.model.impl;

import org.amanzi.neo.services.NewStatisticsService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateStatisticsException;
import org.amanzi.neo.services.exceptions.FailedParseValueException;
import org.amanzi.neo.services.exceptions.IndexPropertyException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.exceptions.LoadVaultException;
import org.amanzi.neo.services.exceptions.UnsupportedClassException;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;
import org.amanzi.neo.services.statistic.IVault;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class PropertyStatisticalModel extends DataModel implements IPropertyStatisticalModel {

    private IVault statisticsVault = null;
    private NewStatisticsService statisticsService = new NewStatisticsService();
    private static Logger LOGGER = Logger.getLogger(NewStatisticsService.class);
    
    protected void indexProperty(INodeType nodeType, String propertyName, Object propertyValue) 
            throws InvalidStatisticsParameterException, LoadVaultException, IndexPropertyException {
        
        if (statisticsVault == null) {
            statisticsVault = statisticsService.loadVault(getRootNode());
        }
        statisticsVault.indexProperty(nodeType.getId(), propertyName, propertyValue);
    }

    protected Object parse(INodeType nodeType, String propertyName, String propertyValue) 
            throws FailedParseValueException, UnsupportedClassException, AWEException {
        
        if (statisticsVault == null) {
            statisticsVault = statisticsService.loadVault(getRootNode());
        }
        return statisticsVault.parse(nodeType.getId(), propertyName, propertyValue);
    }

    @Override
    public INodeToNodeRelationsType getNodeToNodeRelationsType() {
        return null;
    }

    @Override
    public int getNodeCount(INodeType nodeType) {
        if (statisticsVault == null) {
            try {
                statisticsVault = statisticsService.loadVault(getRootNode());
            } catch (InvalidStatisticsParameterException e) {
                LOGGER.debug("root node should not be null");
            } catch (LoadVaultException e) {
                // TODO Handle LoadVaultException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        return statisticsVault.getNodeCount(nodeType.getId());
    }

    @Override
    public int getPropertyCount(INodeType nodeType, String propertyName) {
        if (statisticsVault == null) {
            try {
                statisticsVault = statisticsService.loadVault(getRootNode());
            } catch (InvalidStatisticsParameterException e) {
                LOGGER.debug("root node should not be null");
            } catch (LoadVaultException e) {
                // TODO Handle LoadVaultException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        return statisticsVault.getPropertyCount(nodeType.getId(), propertyName);
    }

    @Override
    public String[] getAllProperties() {
        if (statisticsVault == null) {
            try {
                statisticsVault = statisticsService.loadVault(getRootNode());
            } catch (InvalidStatisticsParameterException e) {
                LOGGER.debug("root node should not be null");
            } catch (LoadVaultException e) {
                // TODO Handle LoadVaultException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
//        return statisticsVault.getAllProperties();
        return null;
    }

    @Override
    public String[] getAllProperties(INodeType nodeType) {
        return null;
    }
    
    @Override
    public void finishUp() {
        try {
            statisticsService.saveVault(getRootNode(), statisticsVault);
        } catch (DatabaseException e) {
            // TODO Handle DatabaseException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } catch (InvalidStatisticsParameterException e) {
            // TODO Handle InvalidStatisticsParameterException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } catch (DuplicateStatisticsException e) {
            // TODO Handle DuplicateStatisticsException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

}
