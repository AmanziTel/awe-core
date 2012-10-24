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

package org.amanzi.awe.render.core.utils;

import java.util.Iterator;

import org.amanzi.awe.catalog.neo.selection.Selection;
import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.dto.ISourcedElement;
import org.amanzi.neo.models.IAnalyzisModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.models.render.IRenderableModel;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class RenderMenuUtils {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class SingletonIterable<E> implements Iterable<E> {

        private final Iterator iterator;

        public SingletonIterable(final E object) {
            iterator = new SingletonIterator(object, false);
        }

        @Override
        public Iterator<E> iterator() {
            return iterator;
        }

    }

    private RenderMenuUtils() {

    }

    public static Selection getLocationElements(final IStructuredSelection selection) {
        IRenderableModel renderableModel = null;
        Iterable<ILocationElement> elementLocations = null;
        boolean incorrect = false;

        Iterable<IDataElement> elementsIterable = Iterables.emptyIterable();

        for (final Object element : selection.toArray()) {
            if (element instanceof IUIItem) {
                final IUIItem item = (IUIItem)element;

                final IRenderableModel itemModel = getRenderableModel(item);

                // check UI Item is correct
                if (itemModel == null) {
                    incorrect = true;
                    break;
                }

                // check model is same for all elements
                if (renderableModel == null) {
                    renderableModel = itemModel;
                } else if (itemModel.equals(renderableModel)) {
                    incorrect = true;
                    break;
                }

                // check internal elements
                final Iterable<IDataElement> subIterable = collectDataElements(item, renderableModel);

                if (subIterable == null) {
                    incorrect = true;
                    break;
                }

                elementsIterable = Iterables.concat(elementsIterable, subIterable);
            }
        }

        if (!incorrect) {
            elementLocations = renderableModel.getElementsLocations(elementsIterable);

            if (!Iterables.isEmpty(elementLocations)) {
                return new Selection(renderableModel, elementsIterable, elementLocations);
            }
        }

        return null;
    }

    private static IRenderableModel getRenderableModel(final IUIItem item) {
        IRenderableModel model = item.castChild(IRenderableModel.class);

        if (model == null) {
            IAnalyzisModel< ? > analyzisModel = item.castChild(IAnalyzisModel.class);

            if (analyzisModel != null) {
                model = getRenderableModel(analyzisModel);
            }

            if (model == null) {
                model = item.castParent(IRenderableModel.class);

                if (model == null) {
                    analyzisModel = item.castParent(IAnalyzisModel.class);

                    if (analyzisModel != null) {
                        model = getRenderableModel(analyzisModel);
                    }
                }
            }
        }

        return model;
    }

    private static Iterable<IDataElement> collectDataElements(final IUIItem item, final IRenderableModel model) {
        final ISourcedElement sourcedElement = item.castChild(ISourcedElement.class);

        if (sourcedElement != null) {
            return collectDataElements(sourcedElement);
        }

        final IDataElement dataElement = item.castChild(IDataElement.class);

        if (dataElement != null) {
            return new SingletonIterable<IDataElement>(dataElement);
        }

        return null;
    }

    private static Iterable<IDataElement> collectDataElements(final ISourcedElement element) {
        return element.getSources();
    }

    private static IRenderableModel getRenderableModel(final IAnalyzisModel< ? > analyzedModel) {
        if (analyzedModel.getSourceModel() instanceof IRenderableModel) {
            return (IRenderableModel)analyzedModel.getSourceModel();
        }

        return null;
    }

}
