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

package org.amanzi.neo.models.network;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IPropertyStatisticalModel;
import org.amanzi.neo.models.distribution.IDistributionalModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.n2n.INodeToNodeRelationsModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */

public interface INetworkModel extends IPropertyStatisticalModel, IRenderableModel, IDistributionalModel {

    INetworkType getNetworkType();

    /**
     * Remove current INCOMING CHILD relationship from current node and create new OUTGOING CHILD
     * relationship from newParentElement to currentNode
     * 
     * @param newParentElement
     * @param currentNode
     * @throws ModelException
     */
    void replaceRelationship(IDataElement newParentElement, IDataElement currentNode) throws ModelException;

    /**
     * Searches for a Selection Model by it's name
     * 
     * @param name name of selection model
     * @return instance of SelectionModel, or null if it's not found
     */
    ISelectionModel findSelectionModel(String name) throws ModelException;

    /**
     * Creates new Selection Model
     * 
     * @param name name of new Selection Model
     * @return created Selection Model
     */
    ISelectionModel createSelectionModel(String name) throws ModelException;

    /**
     * Try to find a Selection Model by it's name, Creates new one if it's not found
     * 
     * @param name name of Selection Model
     * @return instance of Selection Model
     */
    ISelectionModel getSelectionModel(String name) throws ModelException;

    /**
     * Returns all selection models of this Network
     * 
     * @return list of Selection Models related to current network
     */
    Iterable<ISelectionModel> getAllSelectionModels() throws ModelException;

    /**
     * Find a network element, based on properties set in the <code>IDataElement</code> object.
     * Don't forget to set TYPE property.
     * 
     * @param params
     * @return <code>DataElement</code> object, created on base of the found network node, or
     *         <code>null</code>.
     * @throws ModelException
     */
    IDataElement findElement(Map<String, Object> params) throws ModelException;

    /**
     * Find a sector in network by propertyName and propertyValue
     * 
     * @param propertyName Name of property in sector
     * @param propertyValue Value of property in sector
     * @return Found sector or null
     * @throws ModelException
     */
    IDataElement findSector(String propertyName, String propertyValue) throws ModelException;

    /**
     * Create a new network element based on <code>IDataElement element</code> object. MUST set NAME
     * and TYPE.
     * 
     * @param parent
     * @param params
     * @return <code>DataElement</code> object, created on base of the new network node.
     * @throws ModelException
     */
    IDataElement createElement(IDataElement parent, Map<String, Object> params) throws ModelException;

    /**
     * Delete a network element based on <code>IDataElement element</code> object.
     * 
     * @param elementToDelete Element to delete
     * @throws ModelException
     */
    void deleteElement(IDataElement elementToDelete) throws ModelException;

    /**
     * Rename name of a network element based on <code>IDataElement element</code> object.
     * 
     * @param elementToRename Element to rename
     * @param newName New name of node and dataElement
     * @throws ModelException
     */
    void renameElement(IDataElement elementToRename, String newName) throws ModelException;

    /**
     * Update any property of a network element based of <code>IDataElement elementToUpdate</code>
     * object.
     * 
     * @param elementToUpdate Element to update
     * @param propertyName Value of dataElement to update
     * @param newValue New value of updating value
     * @throws ModelException
     */
    void updateElement(IDataElement elementToUpdate, String propertyName, Object newValue) throws ModelException;

    /**
     * complete existedElement with new property. if
     * 
     * @param existedElement
     * @param newPropertySet
     * @param isReplaceExistedis <code>replaceExisted</code> set <b>true</b> than existed property
     *        will replaced with new
     * @return
     * @throws DatabaseException
     * @throws ModelException
     */
    IDataElement completeProperties(IDataElement existedElement, Map<String, Object> newPropertySet, boolean isReplaceExisted) throws DatabaseException, ModelException;

    /**
     * Method return current structure of network
     * 
     * @return Current structure of network
     */
    List<INodeType> getNetworkStructure();

    /**
     * find closest to <code>servSector</code>sector by <code>bsic</code> and <code>bcch</code>
     * 
     * @param servSector
     * @param bsic
     * @param arfcn
     * @return
     * @throws DatabaseException
     */
    IDataElement getClosestSectorByBsicBcch(IDataElement servSector, Integer bsic, Integer arfcn) throws DatabaseException;

    /**
     * get sequence of nodes which link to <code>parent</code> by OUTGOING <code>relType</code>
     * relationShip
     * 
     * @param parent
     * @param reltype
     * @return
     */
    Iterable<IDataElement> getRelatedNodes(IDataElement parent, RelationshipType reltype);

    /**
     * find sectors by propertyName and propertyValue
     * 
     * @param bsic
     * @param bcch
     * @return
     * @throws DatabaseException
     */
    Set<IDataElement> findElementByPropertyValue(INodeType type, String propertyName, Object propertyValue) throws DatabaseException;

    /**
     * return closest to serviceSector nodes
     * 
     * @param servSector
     * @param nodes
     * @param i
     * @return
     */
    IDataElement getClosestElement(IDataElement servSector, Set<IDataElement> candidates, int maxDistance);

    /**
     * return selection models of sector
     * 
     * @param element
     * @return
     * @throws ModelException
     */
    Iterable<ISelectionModel> getAllSelectionModelsOfSector(IDataElement element) throws ModelException;

    IDataElement getNetworkElement(IDataElement parent, Map<String, Object> params) throws ModelException;

    /**
     * return current N2N relationship model
     * 
     * @return N2N relationship model
     */
    INodeToNodeRelationsModel getCurrentNodeToNodeRelationshipModel() throws ModelException;

    /**
     * Set current N2N relationship model
     * 
     * @param model
     * @throws ModelException
     */
    void setCurrentNodeToNodeRelationshipModel(INodeToNodeRelationsModel model) throws ModelException;

    /**
     * Get Star Tool selected model or null
     * 
     * @return <code>IRenderableModel</code> or null if model doesn't selected
     * @throws ModelException
     */
    INetworkModel getStarToolSelectedModel() throws ModelException;

    /**
     * Set Star Tool selected model
     * 
     * @param selectedModel selected model
     * @throws ModelException
     */
    void setStarToolSelectedModel() throws ModelException;

    /**
     * Removing star tool selected model;
     */
    void removeStarToolSelectedModel() throws ModelException;

}
