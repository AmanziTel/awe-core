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

package org.amanzi.awe.render.core.testers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.amanzi.awe.views.treeview.provider.IPeriodTreeItem;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.dto.ISourcedElement;
import org.amanzi.neo.models.IAnalyzisModel;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.eclipse.core.expressions.PropertyTester;
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
public class RenderingTester extends PropertyTester {

    @SuppressWarnings("unchecked")
    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        if (receiver instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection)receiver;

            Iterator<Object> selectedElements = selection.iterator();

            IRenderableModel parent = null;

            boolean testElements = false;
            Set<IDataElement> elements = new HashSet<IDataElement>();

            while (selectedElements.hasNext()) {
                Object selectedElement = selectedElements.next();

                if (selectedElement instanceof IPeriodTreeItem) {
                    IPeriodTreeItem< ? , ? > periodItem = (IPeriodTreeItem< ? , ? >)selectedElement;

                    if (periodItem.getPeriod() != null) {

                        if (periodItem.getParent() instanceof IMeasurementModel) {
                            IMeasurementModel parentModel = getParentModel(periodItem, IMeasurementModel.class);

                            if (parentModel != null) {
                                try {
                                    Iterables.addAll(elements,
                                            parentModel.getElements(periodItem.getStartDate(), periodItem.getEndDate()));
                                } catch (ModelException e) {
                                    // TODO: handle
                                }
                                parent = parentModel;
                                testElements = true;

                                continue;
                            }
                        }
                    }
                }
                if (selectedElement instanceof ITreeItem) {
                    ITreeItem< ? , ? > treeItem = (ITreeItem< ? , ? >)selectedElement;

                    if (treeItem.getChild() instanceof ISourcedElement) {
                        IRenderableModel elementParent = getParentModel(treeItem, IRenderableModel.class);

                        if (elementParent != null) {
                            Iterables.addAll(elements, ((ISourcedElement)treeItem.getChild()).getSources());


                            parent = elementParent;
                            testElements = true;

                            continue;
                        }

                    } else if (treeItem.getChild() instanceof IDataElement) {
                        IRenderableModel elementParent = getParentModel(treeItem, IRenderableModel.class);
                        if (elementParent != null) {
                            if (parent == null) {
                                parent = elementParent;
                            } else {
                                if (!parent.equals(elementParent)) {
                                    return false;
                                }
                            }

                            elements.add((IDataElement)treeItem.getChild());
                            testElements = true;
                        }
                    } else if (treeItem.getChild() instanceof IRenderableModel) {
                        IRenderableModel renderableModel = (IRenderableModel)treeItem.getChild();

                        if (!renderableModel.isRenderable()) {
                            return false;
                        }
                        if (selectedElements.hasNext()) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            if (testElements) {
                if (!parent.getElementsLocations(elements).iterator().hasNext()) {
                    return false;
                }
            }
        }

        return true;
    }

    private <T extends IModel> T getParentModel(final ITreeItem<?, ?> treeItem, final Class<T> clazz) {
        IModel model = treeItem.getParent();

        if (model != null) {
            if (model instanceof IAnalyzisModel) {
                model = ((IAnalyzisModel<?>)model).getSourceModel();
            }

            if (model != null) {
                if (clazz.isAssignableFrom(model.getClass())) {
                    return clazz.cast(model);
                }
            }
        }

        return null;
    }

}
