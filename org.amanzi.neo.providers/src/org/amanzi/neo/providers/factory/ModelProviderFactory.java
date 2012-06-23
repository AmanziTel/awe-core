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

package org.amanzi.neo.providers.factory;

import org.amanzi.neo.providers.ISelectionModelProvider;
import org.amanzi.neo.providers.impl.SelectionModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ModelProviderFactory {

    private static ModelProviderFactory instance;

    private ISelectionModelProvider selectionModelProvider;

    private ModelProviderFactory() {
        // do nothing
    }

    public static ModelProviderFactory getInstance() {
        if (instance == null) {
            synchronized (ModelProviderFactory.class) {
                if (instance == null) {
                    instance = new ModelProviderFactory();
                }
            }
        }

        return instance;
    }

    public ISelectionModelProvider getSelectionModelProvider() {
        if (selectionModelProvider == null) {
            synchronized (ModelProviderFactory.class) {
                if (selectionModelProvider == null) {
                    selectionModelProvider = new SelectionModelProvider();
                }

            }
        }

        return selectionModelProvider;
    }

}
