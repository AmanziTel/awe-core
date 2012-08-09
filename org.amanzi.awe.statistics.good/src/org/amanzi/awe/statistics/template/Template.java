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

package org.amanzi.awe.statistics.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.measurement.IMeasurementModel;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class Template {

    private final String templateName;

    private Map<Object, Object> metadata = new HashMap<Object, Object>();

    public Template(final String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setMetadata(final Map<Object, Object> metadata) {
        this.metadata = metadata;
    }

    public boolean canResolve(final IMeasurementModel model) {
        IDataElement dataElement = model.asDataElement();

        for (Entry<Object, Object> entry : metadata.entrySet()) {
            if (!dataElement.get(entry.getKey().toString()).equals(entry.getValue().toString())) {
                return false;
            }
        }

        return true;
    }

}
