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

package org.amanzi.neo.services.model;

import org.amanzi.neo.services.exceptions.AWEException;

//TODO: LN: comments
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface INetworkModel extends IDataModel, IPropertyStatisticalModel, IRenderableModel {

    public Iterable<ICorrelationModel> getCorrelationModels() throws AWEException;

    public INetworkType getNetworkType();
    
    /**
     * Searches for a Selection Model by it's name
     *
     * @param name name of selection model
     * @return instance of SelectionModel, or null if it's not found
     */
    public ISelectionModel findSelectionModel(String name) throws AWEException;
    
    /**
     * Creates new Selection Model 
     *
     * @param name name of new Selection Model
     * @return created Selection Model
     */
    public ISelectionModel createSelectionModel(String name) throws AWEException;
    
    /**
     * Try to find a Selection Model by it's name, Creates new one if it's not found
     *
     * @param name name of Selection Model
     * @return instance of Selection Model
     */
    public ISelectionModel getSelectionModel(String name) throws AWEException;
    
    /**
     * Returns all selection models of this Network
     *
     * @return list of Selection Models related to current network
     */
    public Iterable<ISelectionModel> getAllSelectionModels() throws AWEException;
}
