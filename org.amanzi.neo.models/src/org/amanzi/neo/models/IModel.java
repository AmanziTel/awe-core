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

package org.amanzi.neo.models;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IModel {

    /**
     * The name of a model is usually the value of NAME property of the model root node.
     * 
     * @return the name of the model
     */
    String getName();

    /**
     * @return ???
     */
    INodeType getType();

    /**
     * The method should perform final operations like storing indexes and statistics values.
     */
    void finishUp() throws ModelException;

    IDataElement asDataElement();

    boolean isRenderable();

    void flush();
}
