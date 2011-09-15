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

package org.amanzi.neo.services.model;

/**
 * <p>
 * The interface describes model that can be correlated with other models (basically network
 * models).
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface ICorrelatableModel {

    /**
     * Returns a list of models, describing relations that involve the current model.
     * 
     * @return
     */
    public Iterable<ICorrelationModel> getCorrelatedModels();

    /**
     * @param correlationModelName the name of correlation model to find
     * @return a correlation model describing relations of current model with some other, or
     *         <code>null</code>.
     */
    public ICorrelationModel getCorrelatedModel(String correlationModelName);
}
