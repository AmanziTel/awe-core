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

import java.util.Map;

import org.amanzi.neo.services.exceptions.AWEException;
import org.neo4j.graphdb.RelationshipType;

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
     * Remove current INCOMING CHILD relationship from current node and create new OUTGOING CHILD
     * relationship from newParentElement to currentNode
     * 
     * @param newParentElement
     * @param currentNode
     */
    public void replaceRelationship(IDataElement newParentElement, IDataElement currentNode);

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
     */
    public IDataElement findElement(Map<String, Object> params);

    /**
     * Create a new network element based on <code>IDataElement element</code> object. MUST set NAME
     * and TYPE.
     * 
     * @param parent
     * @param params
     * @return <code>DataElement</code> object, created on base of the new network node.
     */
    public IDataElement createElement(IDataElement parent, Map<String, Object> params);

    /**
     * Delete a network element based on <code>IDataElement element</code> object.
     *
     * @param elementToDelete Element to delete
     */
    public void deleteElement(IDataElement elementToDelete);
    
    /**
     * Rename name of a network element based on <code>IDataElement element</code> object.
     *
     * @param elementToRename Element to rename
     * @param newName New name of node and dataElement
     */
    public void renameElement(IDataElement elementToRename, String newName);
    
    /**
     * complete existedElement with new property. if
     * 
     * @param existedElement
     * @param newPropertySet
     * @param isReplaceExistedis <code>replaceExisted</code> set <b>true</b> than existed property
     *        will replaced with new
     * @return
     */
    public IDataElement completeProperties(IDataElement existedElement, Map<String, Object> newPropertySet, boolean isReplaceExisted);

    /**
     * Create a new network element based on <code>IDataElement element</code> object. MUST set NAME
     * and TYPE.
     * 
     * @param parent
     * @param element
     * @return <code>DataElement</code> object, created on base of the new network node.
     */
    public IDataElement createElement(IDataElement parent, Map<String, Object> element, RelationshipType type);

    /**
     * create required relationship between 2 nodes
     * 
     * @param parent
     * @param child
     * @param rel
     */
    public void createRelationship(IDataElement parent, IDataElement child, RelationshipType rel);
}
