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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.CorrelationService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.ICorrelationModel;
import org.amanzi.neo.services.model.ICountersModel;
import org.amanzi.neo.services.model.ICountersType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class CountersModel extends MeasurementModel implements ICountersModel {
    // private members
    private ICountersType countersType;    
    private CorrelationService crServ = NeoServiceFactory.getInstance().getCorrelationService();
    private DatasetService dsServ = NeoServiceFactory.getInstance().getDatasetService();

    protected CountersModel(Node rootNode) throws AWEException {
        super(rootNode, DatasetTypes.COUNTERS);
        // validate
        if (rootNode == null) {
            throw new IllegalArgumentException("Network root is null.");
        }
        if (!DatasetTypes.COUNTERS.getId().equals(rootNode.getProperty(AbstractService.TYPE, null))) {
            throw new IllegalArgumentException("Root node must be of type NETWORK.");
        }

        this.rootNode = rootNode;
        this.name = rootNode.getProperty(AbstractService.NAME, StringUtils.EMPTY).toString();
        setPrimaryType(DriveNodeTypes.findById(rootNode.getProperty(DatasetService.PRIMARY_TYPE).toString()));
        initializeStatistics();
    }

    /**
     * This constructor additionally sets the primary type of current CountersModel measurements. By
     * default it is <code>{@link DriveNodeTypes#M}</code>. See also
     * {@link CountersModel#CountersModel(Node, Node, String, IDriveType)}.
     * 
     * @param parent
     * @param rootNode
     * @param name
     * @param type
     * @param primaryType
     * @throws AWEException
     */
    public CountersModel(Node parent, Node rootNode, String name, ICountersType type) throws AWEException {
        this(parent, rootNode, name, type, DriveNodeTypes.M);
    }

    /**
     * Constructor. Pass only rootNode, if you have one, <i>OR</i> all the other parameters.
     * 
     * @param parent a project node
     * @param rootNode a drive node
     * @param name the name of root node of the new drive model
     * @param type the type of root node of the new drive model
     * @param primaryType the primary type of root node of the new drive model
     * @throws AWEException if parameters are null or empty or some errors occur in database during
     *         creation of nodes
     */
    public CountersModel(Node parent, Node rootNode, String name, ICountersType type, INodeType primaryType) throws AWEException {
        super(rootNode, DatasetTypes.COUNTERS);
        // if root node is null, get one by name
        if (rootNode != null) {
            datasetService = NeoServiceFactory.getInstance().getDatasetService();

            this.rootNode = rootNode;
            this.name = (String)rootNode.getProperty(AbstractService.NAME, null);
            this.countersType = type;
        } else {
            // validate params
            if (parent == null) {
                throw new IllegalArgumentException("Parent is null.");
            }

            this.rootNode = datasetService.getDataset(parent, name, DatasetTypes.COUNTERS, type, primaryType);
            this.name = name;
            this.countersType = type;
        }
        setPrimaryType(primaryType);
        initializeStatistics();
    }

    @Override
    public Iterable<ICorrelationModel> getCorrelatedModels() throws AWEException {
        List<ICorrelationModel> result = new ArrayList<ICorrelationModel>();
        for (Node network : crServ.getCorrelatedNetworks(getRootNode())) {
            result.add(new CorrelationModel(network, getRootNode()));
        }
        return result;
    }

    @Override
    public ICorrelationModel getCorrelatedModel(String correlationModelName) throws AWEException {
        ICorrelationModel result = null;
        for (Node network : crServ.getCorrelatedNetworks(getRootNode())) {
            if (network.getProperty(AbstractService.NAME, StringUtils.EMPTY).equals(correlationModelName)) {
                result = new CorrelationModel(network, getRootNode());
                break;
            }
        }
        return result;
    }

    @Override
    public ICountersType getCountersType() {
        return countersType;
    }

    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        return null;
    }

    @Override
    public boolean isUniqueProperties(String property) {
        return false;
    }

    @Override
    public IModel getParentModel() throws AWEException {
        if (rootNode == null) {
            throw new IllegalArgumentException("currentModel type is null.");
        }
        Iterator<Node> isVirtual = dsServ.getFirstRelationTraverser(rootNode, DatasetRelationTypes.DATASET, Direction.INCOMING)
                .iterator();
        if (isVirtual.hasNext()) {
            return new ProjectModel(isVirtual.next());
        }
        return null;
    }

    @Override
    public Iterable<IDataElement> findAllElementsByTimestampPeriod(long min_timestamp, long max_timestamp) {
        return null;
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return this.crs;
    }
    
}
