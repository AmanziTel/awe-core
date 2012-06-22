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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.models.IModel;
import org.amanzi.neo.providers.IModelProvider;

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

    private Map<Class< ? >, IModelProvider<IModel, IModel>> modelProviderCache = new HashMap<Class< ? >, IModelProvider<IModel, IModel>>();

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

    @SuppressWarnings("unchecked")
    public <T1 extends IModel, T2 extends IModel> IModelProvider<T1, T2> getModelProvider(T1 modelClass) {
        return (IModelProvider<T1, T2>)modelProviderCache.get(modelClass);
    }

    @SuppressWarnings("unchecked")
    public synchronized void registerModelProvider(Class<IModelProvider< ? extends IModel, IModel>> modelClass) throws Exception {
        if (!modelProviderCache.containsKey(modelClass)) {
            IModelProvider<IModel, IModel> instance = (IModelProvider<IModel, IModel>)modelClass.newInstance();

            modelProviderCache.put(instance.getModel(), instance);
        }
    }
}
