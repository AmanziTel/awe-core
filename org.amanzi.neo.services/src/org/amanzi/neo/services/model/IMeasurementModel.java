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

import java.io.File;
import java.util.Map;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public interface IMeasurementModel extends IRenderableModel {

    /**
     * Adds a FILE node to the drive model. FILE nodes are added to root node via
     * CHILD-NEXT-...-NEXT chain. FILE nodes are indexed by NAME.
     * 
     * @param file a File object containing file name and path
     * @return the newly created node
     * @throws DatabaseException if errors occur in database
     * @throws DuplicateNodeNameException when trying to add a file that already exists
     */
    public IDataElement addFile(File file) throws AWEException;

    /**
     * Adds a measurement node to a file node in <code>file</code> parameter. If params map contains
     * lat and lon properties, also creates a location node. Use this method if you want to create a
     * measurement with default type.
     * 
     * @param file a <code>IDataElement</code>, containing he file node
     * @param params a map containing parameters of the new measurement
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params) throws AWEException;

    /**
     * Adds a measurement node to a file node in the <code>file</code> parameter. If params map
     * contains lat and lon properties, also creates a location node. Use this method if you want to
     * create a measurement with type, that is different from drive model primary type.
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @param nodeType the type of node to create
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params, INodeType nodeType) throws AWEException;

    /**
     * Adds a measurement node to a file node in the <code>file</code> parameter. If params map
     * contains lat and lon properties, also creates a location node if
     * <code>isNeedToCreateLocation</code> set to true, else creating of location element will be
     * ignored. Use this method if you want to create a measurement with type, that is different
     * from drive model primary type.
     * 
     * @param file
     * @param params
     * @param nodeType
     * @param isNeedToCreateLocation
     * @return
     * @throws AWEException
     */
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params, INodeType nodeType,
            boolean isNeedToCreateLocation) throws AWEException;

    /**
     * Adds a measurement node to a file node with defined filename. If params map contains lat and
     * lon properties, also creates a location node. Use this method if you want to create a
     * measurement with default type.
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(String filename, Map<String, Object> params) throws AWEException;

    /**
     * same with addMeasurement(String filename, Map<String, Object> params) but
     * isNeedToCreateLocation flag response for posibility to create location node
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(String filename, Map<String, Object> params, boolean isNeedToCreateLocation)
            throws AWEException;

    /**
     * Adds a measurement node to a file node with defined filename. If params map contains lat and
     * lon properties, also creates a location node. Use this method if you want to create a
     * measurement with type, that is different from drive model primary type.
     * 
     * @param filename the name of file
     * @param params a map containing parameters of the new measurement
     * @param nodeType the type of node to create
     * @return the newly created node
     * @throws AWEException
     */
    public IDataElement addMeasurement(String filename, Map<String, Object> params, INodeType nodeType,
            boolean isNeedToCreateLocation) throws AWEException;

    /**
     * Looks up for a file node through index
     * 
     * @param name
     * @return
     */
    public IDataElement findFile(String name) throws AWEException;

    /**
     * Finds or creates a file with the defined name.
     * 
     * @param name
     * @return FILE node
     * @throws DatabaseException if errors occur in database
     */
    public IDataElement getFile(String name) throws AWEException;

    /**
     * get list of file nodes
     * 
     * @return an iterator over FILE nodes
     */
    public Iterable<IDataElement> getFiles();

    /**
     * Gets all measurements under defined file.
     * 
     * @param filename the name of the file
     * @return and iterator over measurement nodes
     */
    public Iterable<IDataElement> getMeasurements(String filename);

    /**
     * Get primary type of drive model nodes
     * 
     * @return Primary type of drive model nodes
     */
    public INodeType getPrimaryType();
    


    /**
     * Creates a node, sets its LATITUDE and LONGITUDE properties, and created a LOCATION
     * relationship from parent node.
     * 
     * @param parent
     * @param lat
     * @param lon
     * @throws DatabaseException if errors occur in the database
     */
    public IDataElement createLocationNode(IDataElement parent, double lat, double lon) throws DatabaseException;

}
