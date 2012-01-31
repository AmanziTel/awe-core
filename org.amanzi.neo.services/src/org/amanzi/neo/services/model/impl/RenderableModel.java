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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.filters.INamedFilter;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * This class holds basic implementation of methods, used by Classes, that implement
 * <code>IRenderableModel</code> interface.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class RenderableModel extends AbstractIndexedModel {

    private static Logger LOGGER = Logger.getLogger(RenderableModel.class);
    protected List<IDataElement> renderableModelElements = new ArrayList<IDataElement>();

    static final String CRS_NAME = "crs";
    static final String DESCRIPTION = "description";
    protected GisModel currentGisModel;
    protected final static String DEFAULT_EPSG = "EPSG:31467";

    protected RenderableModel(Node rootNode, INodeType nodeType) throws AWEException {
        super(rootNode, nodeType);
        if (rootNode != null) {
            currentGisModel = new GisModel((String)rootNode.getProperty(DatasetService.NAME));
        }
    }

    /**
     * @return A <code>String</code> description for use of geo tools;
     */
    public String getDescription() {
        return this.rootNode.getProperty(DESCRIPTION, StringUtils.EMPTY).toString();
    }

    public ReferencedEnvelope getBounds() {
        return currentGisModel.getBounds();
    }

    public void setSelectedDataElementToList(IDataElement dataElement) {
        renderableModelElements.add(dataElement);
    }

    public List<IDataElement> getSelectedElements() {
        return renderableModelElements;
    }

    public void clearSelectedElements() {
        renderableModelElements.clear();
    }

    public void setSelectedDataElements(List<IDataElement> dataElements) {
        renderableModelElements.addAll(dataElements);
    }

    @Override
    public void finishUp() throws AWEException {
        Iterable<Node> allGis = datasetService.getAllGisByDataset(rootNode);
        for (Node gis : allGis) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(CRS_NAME, currentGisModel.getCRSCode());
            datasetService.setProperties(gis, params);
        }
        super.finishUp();
    }

    public CoordinateReferenceSystem getCrs() {
        return currentGisModel.getCrs();
    }

    public CoordinateReferenceSystem updateCRS(String crsCode) {
        return currentGisModel.updateCRS(crsCode);
    }

    public void setCRS(CoordinateReferenceSystem crs) {
        currentGisModel.setCRS(crs);
    }

    public void addLayer(String name, INamedFilter filter) {
        try {
            GisModel gis = new GisModel(name);
            gis.addFilter(filter);
        } catch (DatabaseException e) {
            LOGGER.error("addLayer(...) throw database exception", e);
        }
    }

    public Iterable<GisModel> getAllGisModels() throws DatabaseException {
        Iterable<Node> gisNodes = datasetService.getAllGisByDataset(rootNode);
        List<GisModel> gisElements = new LinkedList<GisModel>();
        for (Node gis : gisNodes) {
            gisElements.add(new GisModel(gis));
        }
        return gisElements;
    }

    public Iterable<INamedFilter> findAllFitlers(IDataElement element) {
        Iterable<INamedFilter> filters = datasetService.loadFilters(((DataElement)element).getNode());
        return filters;
    }

    public GisModel findGisByName(String gisName) throws DatabaseException {
        if (gisName == null || gisName.isEmpty()) {
            throw new IllegalArgumentException("findGisByName(... ) gisName cann't be null");
        }
        Iterable<GisModel> allGis = getAllGisModels();
        for (GisModel gis : allGis) {
            if (gis.getName().equals(gisName)) {
                currentGisModel = gis;
                return gis;
            }
        }
        return null;
    }

    /**
     * <p>
     * Class which hold information about dataset gis's nodes. <b>Gis node basically response for
     * store information about Coordinate Reference System(crs), max and min coordinate values, and
     * filters we should use when current gis selected.</b>
     * <p>
     * Basic gis( <code>currentGisModel</code>) is a gis which created with dataset creation(doesn't
     * store any filters).For creation a new gis you should invoke addLayer(String name,INamedFIlter
     * filter) method.To initialize <code>currentGisModel</code> with other gis you should to invoke
     * findGisByName(String name) method at first
     * </p>
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    public class GisModel implements IModel {
        private Node gisRoot;
        private String name;
        /** The field used in geo tools. Assignment not yet implemented.//TODO */
        protected CoordinateReferenceSystem crs;

        private String crsCode;
        private INamedFilter filter;

        public GisModel(Node node) throws DatabaseException {
            gisRoot = node;
            initGisProperties();

        }

        public GisModel(String name) throws DatabaseException {
            gisRoot = datasetService.getGisNodeByDataset(rootNode, name);
            initGisProperties();
        }

        /**
         * initialize default preferences for current gis
         */
        private void initGisProperties() {
            this.name = (String)gisRoot.getProperty(DatasetService.NAME);
            crsCode = StringUtils.EMPTY;
            if (gisRoot != null) {
                crsCode = gisRoot.getProperty(CRS_NAME, StringUtils.EMPTY).toString();
            }
            Iterator<INamedFilter> filters = datasetService.loadFilters(gisRoot).iterator();
            if (filters.hasNext()) {
                this.filter = filters.next();
            }
            try {
                if (crsCode.equals(StringUtils.EMPTY)) {
                    crsCode = DEFAULT_EPSG;
                }
                crs = CRS.decode(crsCode);
            } catch (FactoryException e) {
                LOGGER.error("Could not parse epsg.", e);

            }
        }

        /**
         * add new filter to current gis
         * 
         * @param filter
         * @throws DatabaseException
         */
        private void addFilter(INamedFilter filter) throws DatabaseException {
            datasetService.saveFilter(gisRoot, filter);
            this.filter = filter;
        }

        /**
         * return current crs code
         * 
         * @return
         */
        public String getCRSCode() {
            return this.crsCode;
        }

        /**
         * return current crs
         * 
         * @return
         */
        public CoordinateReferenceSystem getCrs() {
            return this.crs;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Node getRootNode() {
            return gisRoot;
        }

        @Override
        public INodeType getType() {
            return DatasetTypes.GIS;
        }

        /**
         * Updates the model CRS and returns a <code>CoordinateReferenceSystem</code> object,
         * representing the new value.
         * 
         * @param crsCode a string representing the new CRS - something like "EPSG:31247"
         * @return the updated CRS as <code>CoordinateReferenceSystem</code> object
         */
        public CoordinateReferenceSystem updateCRS(String crsCode) {
            try {
                crs = CRS.decode(crsCode);
                this.crsCode = crsCode;
                return crs;
            } catch (FactoryException e) {
                LOGGER.error("Could not parse epsg.", e);
            }
            return null;
        }

        /**
         * @return An envelope, representing the coordinate bounds for the data in current model.
         */
        public ReferencedEnvelope getBounds() {
            return new ReferencedEnvelope(min_longitude, max_longitude, min_latitude, max_latitude, crs);
        }

        /**
         * @param crs The crs to set.
         */
        public void setCRS(CoordinateReferenceSystem crs) {
            this.crs = crs;
            crsCode = "";// ToDO:
        }
        
        public void setCRS(double lat, double lon, String fileName) {
            if (this.crs == null) {
                try {
                    this.crs = CRS.decode(org.amanzi.neo.services.CRS.fromLocation(lat, lon, fileName).getEpsg());
                } catch (FactoryException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * get filter for this gis
         * 
         * @param filterName
         * @return
         */
        public INamedFilter getFilter() {
            return filter;
        }

        @Override
        public void finishUp() throws AWEException {
        }

    }
}
