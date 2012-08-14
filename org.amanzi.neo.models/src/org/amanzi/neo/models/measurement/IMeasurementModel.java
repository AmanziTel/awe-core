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

package org.amanzi.neo.models.measurement;

import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.internal.IDatasetModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IMeasurementModel extends IDatasetModel {

    public interface IFileElement extends IDataElement {

        @Override
        String getName();

        String getPath();

    }

    IFileElement getFile(IDataElement parent, String name, String path) throws ModelException;

    ILocationElement createLocation(IDataElement parent, double latitude, double longitude, long timestamp) throws ModelException;

    void addToLocation(IDataElement measurement, ILocationElement location) throws ModelException;

    IDataElement addMeasurement(IDataElement parent, Map<String, Object> properties) throws ModelException;

    INodeType getMainMeasurementNodeType();

    long getMinTimestamp();

    long getMaxTimestamp();

    Iterable<IDataElement> getElements(long minTimestamp, long maxTimestamp) throws ModelException;

}
