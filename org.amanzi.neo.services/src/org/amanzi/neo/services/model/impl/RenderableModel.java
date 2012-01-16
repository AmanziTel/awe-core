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
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
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

    static final String DESCRIPTION = "description";
    static final String CRS_NAME = "crs";

    protected List<IDataElement> renderableModelElements = new ArrayList<IDataElement>();
    
    protected final static String DEFAULT_EPSG = "EPSG:31467";
    /** The field used in geo tools. Assignment not yet implemented.//TODO */
    protected CoordinateReferenceSystem crs;

    protected String crsCode;

    protected RenderableModel(Node rootNode, INodeType nodeType) throws AWEException {
        super(rootNode, nodeType);
        Node gis = datasetService.getGisNodeByDataset(rootNode);
        crsCode = StringUtils.EMPTY;
        if (gis != null) {
            crsCode = gis.getProperty(CRS_NAME, StringUtils.EMPTY).toString();
        }
        try {
            if (!crsCode.equals(StringUtils.EMPTY)) {
                crsCode = DEFAULT_EPSG;
            }
            crs = CRS.decode(DEFAULT_EPSG);
        } catch (FactoryException e) {
            LOGGER.error("Could not parse epsg.", e);
        }
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
     * @param crs The crs to set.
     */
    public void setCRS(CoordinateReferenceSystem crs) {
        this.crs = crs;
        crsCode = "";// ToDO:
    }

    /**
     * @return A <code>String</code> description for use of geo tools;
     */
    public String getDescription() {
        return this.rootNode.getProperty(DESCRIPTION, StringUtils.EMPTY).toString();
    }

    /**
     * @return An envelope, representing the coordinate bounds for the data in current model.
     */
    public ReferencedEnvelope getBounds() {
        return new ReferencedEnvelope(min_longitude, max_longitude, min_latitude, max_latitude, crs);
    }

    @Override
    public void finishUp() throws AWEException {
        Node gis = datasetService.getGisNodeByDataset(rootNode);
        if (gis != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(CRS_NAME, crsCode);
            datasetService.setProperties(gis, params);
        }
        super.finishUp();
    }
    
    public void setSelectedDataElementToList(IDataElement dataElement) {        
        renderableModelElements.add(dataElement);
    }

    public void setSelectedDataElements(List<IDataElement> dataElements) {
        renderableModelElements.addAll(dataElements);
    }

    public List<IDataElement> getSelectedElements() {
        return renderableModelElements;
    }
    
    public void clearSelectedElements() {
        renderableModelElements.clear();
    }
              
}
