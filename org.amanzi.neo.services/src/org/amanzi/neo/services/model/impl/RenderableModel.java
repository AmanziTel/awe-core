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

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.NoSuchAuthorityCodeException;
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

    protected final static String DEFAULT_EPSG = "EPSG:31467";
    /** The field used in geo tools. Assignment not yet implemented.//TODO */
    protected CoordinateReferenceSystem crs;

    protected RenderableModel(Node rootNode) throws AWEException {
        super(rootNode);
    }

    // TODO: make it abstract?
    @Override
    public abstract Iterable<IDataElement> getChildren(IDataElement parent);

    @Override
    public abstract Iterable<IDataElement> getAllElementsByType(INodeType elementType);

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
        try {
            crs = CRS.decode(DEFAULT_EPSG);
        } catch (NoSuchAuthorityCodeException e) {
            LOGGER.error("Could not parse epsg.", e);
        }
        return new ReferencedEnvelope(min_latitude, max_latitude, min_longitude, max_longitude, crs);
    }

}
