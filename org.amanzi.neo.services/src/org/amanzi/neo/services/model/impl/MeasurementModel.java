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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.impl.DistributionModel;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IMeasurementModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public abstract class MeasurementModel extends RenderableModel implements IMeasurementModel {

    private int count = 0;

    private Index<Node> files;

    private INodeType primaryType = DriveNodeTypes.M;

    /**
     * @param rootNode
     * @param nodeType
     * @throws AWEException
     */
    protected MeasurementModel(Node rootNode, INodeType nodeType) throws AWEException {
        super(rootNode, nodeType);
    }

    private static Logger LOGGER = Logger.getLogger(MeasurementModel.class);

    @Override
    public IDataElement addFile(File file) throws AWEException {
        LOGGER.debug("start addFile(File file)");

        // file nodes are added as c-n-n
        // validate params
        if (file == null) {
            throw new IllegalArgumentException("File is null.");
        }
        if (findFile(file.getName()) != null) {
            throw new DuplicateNodeNameException(file.getName(), DriveNodeTypes.FILE);
        }

        Node fileNode = datasetService.addChild(rootNode, datasetService.createNode(DriveNodeTypes.FILE), null);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AbstractService.NAME, file.getName());
        params.put(PATH, file.getPath());
        datasetService.setProperties(fileNode, params);
        if (files == null) {
            files = datasetService.addNodeToIndex(fileNode, AbstractService.getIndexKey(rootNode, DriveNodeTypes.FILE),
                    AbstractService.NAME, file.getName());
        } else {
            datasetService.addNodeToIndex(fileNode, files, AbstractService.NAME, file.getName());
        }
        return new DataElement(fileNode);
    }

    @Override
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params) throws AWEException {
        return addMeasurement(file, params, primaryType);
    }

    @Override
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params, INodeType nodeType) throws AWEException {
        return addMeasurement(file, params, nodeType, true);
    }

    @Override
    public IDistributionModel getDistributionModel(IDistribution< ? > distributionType) throws AWEException {
        return new DistributionModel(this, distributionType);
    }

    @Override
    public IDataElement addMeasurement(IDataElement file, Map<String, Object> params, INodeType nodeType,
            boolean isNeedToCreateLocation) throws AWEException {

        // validate parameters
        if (file == null) {
            throw new IllegalArgumentException("File element is null.");
        }
        Node fileNode = ((DataElement)file).getNode();
        if (fileNode == null) {
            throw new IllegalArgumentException("File node is null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameter map is null.");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        Node m = datasetService.createNode(nodeType);
        datasetService.addChild(fileNode, m, null);
        Double lat = (Double)params.get(LATITUDE);
        Double lon = (Double)params.get(LONGITUDE);
        Long tst = (Long)params.get(TIMESTAMP);
        indexNode(m);

        if ((lat != null) && (lat != 0) && (lon != null) && (lon != 0) && isNeedToCreateLocation) {
            createLocationNode(new DataElement(m), lat, lon);
            params.remove(LATITUDE);
            params.remove(LONGITUDE);
        }
        if ((tst != null) && (tst != 0)) {
            updateTimestamp(tst);
        }
        datasetService.setProperties(m, params);
        indexProperty(primaryType, params); // TODO: ??????????

        count++;
        Map<String, Object> prop = new HashMap<String, Object>();
        prop.put(PRIMARY_TYPE, primaryType.getId());// TODO: ?????????????
        prop.put(COUNT, count);
        datasetService.setProperties(rootNode, prop);
        indexProperty(primaryType, prop); // TODO: ???????????

        return new DataElement(m);
    }

    @Override
    public IDataElement addMeasurement(String filename, Map<String, Object> params) throws AWEException {
        return addMeasurement(filename, params, primaryType, true);
    }

    @Override
    public IDataElement addMeasurement(String filename, Map<String, Object> params, boolean isNeedToCreateLocation)
            throws AWEException {
        return addMeasurement(filename, params, primaryType, isNeedToCreateLocation);
    }

    @Override
    public IDataElement addMeasurement(String filename, Map<String, Object> params, INodeType nodeType,
            boolean isNeedToCreateLocation) throws AWEException {
        LOGGER.debug("start addMeasurement(String filename, Map<String, Object> params)");

        // measurements are added as c-n-n o file nodes
        // lat, lon properties are stored in a location node

        // validate params
        if ((filename == null) || (filename.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Filename is null or empty.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters map is null.");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        Node fileNode = ((DataElement)findFile(new File(filename).getName())).getNode();
        if (fileNode == null) {
            throw new IllegalArgumentException("File node " + filename + " not found.");
        }

        return addMeasurement(new DataElement(fileNode), params, nodeType, isNeedToCreateLocation);
    }

    @Override
    public IDataElement findFile(String name) throws AWEException {
        // validate parameters
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name is null or empty");
        }
        if (files == null) {
            files = datasetService.getIndex(rootNode, DriveNodeTypes.FILE);
        }

        Node fileNode = files.get(AbstractService.NAME, name).getSingle();
        return fileNode == null ? null : new DataElement(fileNode);
    }

    @Override
    public IDataElement getFile(String name) throws AWEException {
        IDataElement result = ((DataElement)findFile(name));
        if (result == null) {
            try {
                result = ((DataElement)addFile(new File(name)));
            } catch (DuplicateNodeNameException e) {
                // impossible
            }
        }
        return result;
    }

    @Override
    public Iterable<IDataElement> getMeasurements(String filename) {
        // validate
        if ((filename == null) || (filename.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Filename is null or empty.");
        }

        return new DataElementIterable(datasetService.getChildrenChainTraverser(files.get(AbstractService.NAME,
                new File(filename).getName()).getSingle()));
    }

    @Override
    public Iterable<IDataElement> getFiles() {
        return new DataElementIterable(datasetService.getChildrenChainTraverser(rootNode));
    }

    /**
     * returns current primary node type
     */
    @Override
    public INodeType getPrimaryType() {
        return primaryType;
    }

    /**
     * @param primaryType The primaryType to set.
     */
    protected void setPrimaryType(INodeType primaryType) {
        this.primaryType = primaryType;
    }

    @Override
    public IDataElement createLocationNode(IDataElement parent, double lat, double lon) throws DatabaseException {
        LOGGER.debug("start createLocationNode(Node measurement, long lat, long lon)");
        // validate params
        if (parent == null) {
            throw new IllegalArgumentException("Parent nde is null.");
        }

        Node parentElement = ((DataElement)parent).getNode();
        if (parentElement == null) {
            throw new IllegalArgumentException("Parent nde is null.");
        }
        Node location = datasetService.createNode(parentElement, DriveRelationshipTypes.LOCATION, DriveNodeTypes.MP);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LATITUDE, lat);
        params.put(LONGITUDE, lon);
        datasetService.setProperties(location, params);
        updateLocationBounds(lat, lon);
        indexNode(location);
        return new DataElement(location);
    }

    @Override
    public Iterable<IDataElement> getElements(Envelope bounds_transformed) throws AWEException {
        return new DataElementIterable(getNodesInBounds(DriveNodeTypes.MP, bounds_transformed.getMinY(),
                bounds_transformed.getMinX(), bounds_transformed.getMaxY(), bounds_transformed.getMaxX()));
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        // validate
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }
        LOGGER.info("getAllElementsByType(" + elementType.getId() + ")");

        return new DataElementIterable(datasetService.findAllDatasetElements(getRootNode(), elementType));
    }

    @Override
    public Coordinate getCoordinate(IDataElement element) {
        return new Coordinate((Double)element.get(LONGITUDE), (Double)element.get(LATITUDE));
    }
}
