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

import java.util.Set;

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
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
public interface IDriveModel
        extends
            ICorrelatableModel,
            IRenderableModel,
            ITimelineModel,
            IDistributionalModel,
            IMeasurementModel{
    
    public final static String SELECTED_PROPERTIES = "selected_properties";
    
    /**
     * @return a List<Node> containing DriveModels created on base of virtual dataset nodes in
     *         current DriveModel
     */
    public Iterable<IDriveModel> getVirtualDatasets();

    /**
     * get drive type
     * 
     * @return drive type
     */
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
     * The method creates CALL_M relationships between <code>parent</code> node and
     * <code>source</code> nodes.
     * 
     * @param parent a <code>DataElement</code>, that contains parent node.
     * @param source list of <code>DataElement</code>s, containing <code>Node</code> objects.
     * @throws DatabaseException if problems occur in database
     */
    @Deprecated
    public void linkNode(IDataElement parent, Iterable<IDataElement> source, RelationshipType rel) throws DatabaseException;

    /**
     * Finds a locations node.
     * 
     * @param parent
     * @return the found location node or null.
     */
    public Iterable<IDataElement> getLocations(IDataElement parentElement);

    /**
     * Add a node with selected properties in DriveInquirerView
     *
     * @param selectedProperties Set of selected properties
     * @return Created IDataElement with node
     */
    public IDataElement addSelectedProperties(Set<String> selectedProperties);
    
    /**
     * Method to get saved selected properties from DriveInquirerView
     *
     * @return Set of selected properties from DriveInquirerView
     */
    public Set<String> getSelectedProperties();
}
