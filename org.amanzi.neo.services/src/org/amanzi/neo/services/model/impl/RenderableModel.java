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
import org.amanzi.neo.services.model.IDataElement;
import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
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
public class RenderableModel extends AbstractIndexedModel {
    static final String DESCRIPTION = "description";

    /** The field used in geo tools. Assignment not yet implemented.//TODO */
    protected CoordinateReferenceSystem crs;

    // TODO: make it abstract?
    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        return null;
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        return null;
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
        return new ReferencedEnvelope(min_latitude, max_latitude, min_longitude, max_longitude, crs);
    }

}
