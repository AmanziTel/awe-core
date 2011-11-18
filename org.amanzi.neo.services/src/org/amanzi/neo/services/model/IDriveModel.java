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

import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.neo4j.graphdb.RelationshipType;

/**
 * <p>
 * This interface contains declarations of methods, that are common for drive models.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IDriveModel extends ICorrelatableModel, IRenderableModel, IPropertyStatisticalModel, ITimelineModel {
    /**
     * @return a List<Node> containing DriveModels created on base of virtual dataset nodes in
     *         current DriveModel
     */
    public Iterable<IDriveModel> getVirtualDatasets();

    public IDriveType getDriveType();

    /**
     * Adds a new node of type DRIVE, creates VIRTUAL_DATASET relationship from root node of current
     * DM, and creates and returns a new DM on base of newly created node.
     * 
     * @param name the name of new virtual dataset
     * @param driveType the drive type of new virtual dataset (NB! not TYPE, TYPE is set to DRIVE)
     * @return DriveModel based on new virtual dataset node
     * @throws AWEException if parameters are null or empty or some errors occur in database during
     *         creation of nodes
     */
    public DriveModel addVirtualDataset(String name, IDriveType driveType) throws AWEException;

    /**
     * Looks for a virtual dataset node with the defined name, creates a DriveModel based on it, if
     * found
     * 
     * @param name the name of virtual dataset node
     * @return DriveModel based on the found node or null if search failed
     */
    public IDriveModel findVirtualDataset(String name);

    /**
     * Looks for a virtual dataset node or creates a new one if nothing found. returns a new
     * DriveModel based on resulting node.
     * 
     * @param name
     * @param driveType used to create a new virtual dataset
     * @return a DriveModel based on found or created virtual dataset node
     * @throws AWEException if errors occurred during creation of new node
     */
    public IDriveModel getVirtualDataset(String name, IDriveType driveType) throws AWEException;

    /**
     * Adds a FILE node to the drive model. FILE nodes are added to root node via
     * CHILD-NEXT-...-NEXT chain. FILE nodes are indexed by NAME.
     * 
     * @param file a File object containing file name and path
     * @return the newly created node
     * @throws DatabaseException if errors occur in database
     * @throws DuplicateNodeNameException when trying to add a file that already exists
     */
    public IDataElement addFile(File file) throws DatabaseException, DuplicateNodeNameException;

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
    public IDataElement addMeasurement(String filename, Map<String, Object> params, INodeType nodeType) throws AWEException;

    /**
     * The method creates CALL_M relationships between <code>parent</code> node and
     * <code>source</code> nodes.
     * 
     * @param parent a <code>DataElement</code>, that contains parent node.
     * @param source list of <code>DataElement</code>s, containing <code>Node</code> objects.
     * @throws DatabaseException if problems occur in database
     */
    public void linkNode(IDataElement parent, Iterable<IDataElement> source, RelationshipType rel) throws DatabaseException;

    /**
     * Finds a location node.
     * 
     * @param parent
     * @return the found location node or null.
     */
    public IDataElement getLocation(IDataElement parentElement);

    /**
     * Looks up for a file node through index
     * 
     * @param name
     * @return
     */
    public IDataElement findFile(String name);

    /**
     * Finds or creates a file with the defined name.
     * 
     * @param name
     * @return FILE node
     * @throws DatabaseException if errors occur in database
     */
    public IDataElement getFile(String name) throws DatabaseException;

    /**
     * Gets all measurements under defined file.
     * 
     * @param filename the name of the file
     * @return and iterator over measurement nodes
     */
    public Iterable<IDataElement> getMeasurements(String filename);

    /**
     * @return an iterator over FILE nodes
     */
    public Iterable<IDataElement> getFiles();
}
