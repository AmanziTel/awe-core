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

import java.util.List;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IRenderableModel extends IModel {

    IGISModel getMainGIS();

    List<IGISModel> getAllGIS();

    void addGISModel(IGISModel model);

    Iterable<ILocationElement> getElements(Envelope bound) throws ModelException;

    Iterable<ILocationElement> getElements(Envelope bound, IFilter filter);

    int getRenderableElementCount();

    Iterable<ILocationElement> getElementsLocations(Iterable<IDataElement> dataElements) throws ModelException;

}
