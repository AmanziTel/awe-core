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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */

public interface INetworkModel extends IDataModel, IPropertyStatisticalModel, IRenderableModel, IDistributionalModel {

    /**
     * Traverse database to find all correlation root nodes and create models based on them.
     * 
     * @return an iterable over correlation models, referring to current network
     * @throws AWEException if errors occur in database
     */
    public Iterable<ICorrelationModel> getCorrelationModels() throws AWEException;

    /**
     * Find or create a correlation model between the current network and the defined dataset.
     * 
     * @param datasetElement
     * @return
     * @throws AWEException
     */
    public ICorrelationModel getCorrelationModel(IDataElement datasetElement) throws AWEException;

    /**
     * Traverse database to find all n2n root nodes and create models based on them.
     * 
     * @return an iterable over n2n models, referring to current network
     * @throws AWEException if errors occur in database
     */
    public Iterable<INodeToNodeRelationsModel> getNodeToNodeModels() throws AWEException;

    /**
     * Traverse database to find all n2n root nodes of one type and create models based on them.
     * 
     * @param type
     * @return an iterable over n2n models filtered by type, referring to current network
     * @throws AWEException if errors occur in database
     */
    public Iterable<INodeToNodeRelationsModel> getNodeToNodeModels(N2NRelTypes type) throws AWEException;

    public INodeToNodeRelationsModel createNodeToNodeMmodel(INodeToNodeRelationsType relType, String name, INodeType nodeType)
            throws AWEException;

    public INodeToNodeRelationsModel findNodeToNodeModel(INodeToNodeRelationsType relType, String name, INodeType nodeType)
            throws AWEException;

    public INodeToNodeRelationsModel getNodeToNodeModel(INodeToNodeRelationsType relType, String name, INodeType nodeType)
            throws AWEException;

    public INetworkType getNetworkType();

    /**
     * Remove current INCOMING CHILD relationship from current node and create new OUTGOING CHILD
     * relationship from newParentElement to currentNode
     * 
     * @param newParentElement
     * @param currentNode
     * @throws AWEException
     */
    public void replaceRelationship(IDataElement newParentElement, IDataElement currentNode) throws AWEException;

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

    /**
     * Find a network element, based on properties set in the <code>IDataElement</code> object.
     * Don't forget to set TYPE property.
     * 
     * @param params
     * @return <code>DataElement</code> object, created on base of the found network node, or
     *         <code>null</code>.
     * @throws AWEException
     */
    public IDataElement findElement(Map<String, Object> params) throws AWEException;

    /**
     * Find a sector in network by propertyName and propertyValue
     * 
     * @param propertyName Name of property in sector
     * @param propertyValue Value of property in sector
     * @return Found sector or null
     * @throws AWEException
     */
    public IDataElement findSector(String propertyName, String propertyValue) throws AWEException;

    /**
     * Create a new network element based on <code>IDataElement element</code> object. MUST set NAME
     * and TYPE.
     * 
     * @param parent
     * @param params
     * @return <code>DataElement</code> object, created on base of the new network node.
     * @throws AWEException
     */
    public IDataElement createElement(IDataElement parent, Map<String, Object> params) throws AWEException;

    /**
     * Delete a network element based on <code>IDataElement element</code> object.
     * 
     * @param elementToDelete Element to delete
     * @throws AWEException
     */
    public void deleteElement(IDataElement elementToDelete) throws AWEException;

    /**
     * Rename name of a network element based on <code>IDataElement element</code> object.
     * 
     * @param elementToRename Element to rename
     * @param newName New name of node and dataElement
     * @throws AWEException
     */
    public void renameElement(IDataElement elementToRename, String newName) throws AWEException;

    /**
     * Update any property of a network element based of <code>IDataElement elementToUpdate</code>
     * object.
     * 
     * @param elementToUpdate Element to update
     * @param propertyName Value of dataElement to update
     * @param newValue New value of updating value
     * @throws AWEException
     */
    public void updateElement(IDataElement elementToUpdate, String propertyName, Object newValue) throws AWEException;

    /**
     * complete existedElement with new property. if
     * 
     * @param existedElement
     * @param newPropertySet
     * @param isReplaceExistedis <code>replaceExisted</code> set <b>true</b> than existed property
     *        will replaced with new
     * @return
     * @throws DatabaseException
     * @throws AWEException
     */
    public IDataElement completeProperties(IDataElement existedElement, Map<String, Object> newPropertySet, boolean isReplaceExisted)
            throws DatabaseException, AWEException;

    /**
     * Create a new network element based on <code>IDataElement element</code> object. MUST set NAME
     * and TYPE.
     * 
     * @param parent
     * @param element
     * @return <code>DataElement</code> object, created on base of the new network node.
     * @throws AWEException
     */
    @Deprecated
    public IDataElement createElement(IDataElement parent, Map<String, Object> element, RelationshipType type) throws AWEException;

    /**
     * create required relationship between 2 nodes
     * 
     * @param parent
     * @param child
     * @param rel
     * @throws AWEException
     */
    @Deprecated
    public void createRelationship(IDataElement parent, IDataElement child, RelationshipType rel) throws AWEException;

    /**
     * Method return current structure of network
     * 
     * @return Current structure of network
     */
    public List<INodeType> getNetworkStructure();

    /**
     * find closest to <code>servSector</code>sector by <code>bsic</code> and <code>bcch</code>
     * 
     * @param servSector
     * @param bsic
     * @param arfcn
     * @return
     * @throws DatabaseException
     */
    public IDataElement getClosestSectorByBsicBcch(IDataElement servSector, Integer bsic, Integer arfcn) throws DatabaseException;

    /**
     * get sequence of nodes which link to <code>parent</code> by OUTGOING <code>relType</code>
     * relationShip
     * 
     * @param parent
     * @param reltype
     * @return
     */
    public Iterable<IDataElement> getRelatedNodes(IDataElement parent, RelationshipType reltype);

    /**
     * find sectors by propertyName and propertyValue
     * 
     * @param bsic
     * @param bcch
     * @return
     * @throws DatabaseException
     */
    public Set<IDataElement> findElementByPropertyValue(INodeType type, String propertyName, Object propertyValue)
            throws DatabaseException;

    /**
     * return closest to serviceSector nodes
     * 
     * @param servSector
     * @param nodes
     * @param i
     * @return
     */
    public IDataElement getClosestElement(IDataElement servSector, Set<IDataElement> candidates, int maxDistance);

    /**
     * return selection models of sector
     * 
     * @param element
     * @return
     * @throws AWEException
     */
    public Iterable<ISelectionModel> getAllSelectionModelsOfSector(IDataElement element) throws AWEException;

    public IDataElement getNetworkElement(IDataElement parent, Map<String, Object> params) throws AWEException;

    /**
     * return current N2N relationship model
     * 
     * @return N2N relationship model
     */
    public INodeToNodeRelationsModel getCurrentNodeToNodeRelationshipModel() throws AWEException;

    /**
     * Set current N2N relationship model
     * 
     * @param model
     * @throws AWEException
     */
    public void setCurrentNodeToNodeRelationshipModel(INodeToNodeRelationsModel model) throws AWEException;

    /**
     * Get Star Tool selected model or null
     * 
     * @return <code>IRenderableModel</code> or null if model doesn't selected
     * @throws AWEException
     */
    public INetworkModel getStarToolSelectedModel() throws AWEException;

    /**
     * Set Star Tool selected model
     * 
     * @param selectedModel selected model
     * @throws AWEException
     */
    public void setStarToolSelectedModel() throws AWEException;

    /**
     * Removing star tool selected model; 
     */
    public void removeStarToolSelectedModel() throws AWEException;

}
