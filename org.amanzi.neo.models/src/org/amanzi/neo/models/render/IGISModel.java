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

package org.amanzi.neo.models.render;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IGISModel extends IModel {

    public interface ILocationElement extends IDataElement {

        double getLatitude();

        double getLongitude();

    }

    void updateBounds(double latitude, double longitude);

    void setSourceModel(IRenderableModel sourceModel);

    CoordinateReferenceSystem getCRS();

    IRenderableModel getSourceModel();

    double getMinLatitude();

    double getMaxLatitude();

    double getMinLongitude();

    double getMaxLongitude();

    ReferencedEnvelope getBounds();

    Iterable<ILocationElement> getElements(Envelope bound);
}
