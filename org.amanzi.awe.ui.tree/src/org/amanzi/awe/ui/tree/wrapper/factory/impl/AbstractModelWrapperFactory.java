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

package org.amanzi.awe.ui.tree.wrapper.factory.impl;

import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.internal.INamedModelProvider;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractModelWrapperFactory<M extends IModel, P extends INamedModelProvider<M, IProjectModel>>
        implements
            ITreeWrapperFactory {

    private static final Logger LOGGER = Logger.getLogger(AbstractModelWrapperFactory.class);

    private final P provider;

    private final IProjectModelProvider projectModelProvider;

    public AbstractModelWrapperFactory(final P provider, final IProjectModelProvider projectModelProvider) {
        this.provider = provider;
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    public Iterator<ITreeWrapper> getWrappers(final Object parent) {
        Iterator<ITreeWrapper> result = null;

        try {
            IProjectModel projectModel = null;

            if (parent != null) {
                if (parent.equals(ObjectUtils.NULL)) {
                    projectModel = projectModelProvider.getActiveProjectModel();
                } else if (parent instanceof IUIItem) {
                    projectModel = ((IUIItem)parent).castChild(IProjectModel.class);
                }
            }

            if (projectModel != null) {
                final List<M> models = provider.findAll(projectModel);

                if (models != null) {
                    result = new TreeWrapperIterator<M>(models.iterator()) {

                        @Override
                        protected ITreeWrapper createTreeWrapper(final M model) {
                            return AbstractModelWrapperFactory.this.createTreeWrapper(model);
                        }

                    };
                }

            }
        } catch (final ModelException e) {
            LOGGER.error("Error on collecting Tree Wrappers", e);
        }
        return result;
    }

    protected abstract ITreeWrapper createTreeWrapper(M model);
}
