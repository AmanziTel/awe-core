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

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.render.IRenderableModel;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
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

                if (selectedElement instanceof ITreeItem) {
                    ITreeItem<?, ?> treeItem = (ITreeItem<?, ?>)selectedElement;

                    if (treeItem.getChild() instanceof IDataElement) {
                        IRenderableModel elementParent = (IRenderableModel)treeItem.getParent();
                        if (parent == null) {
                            parent = elementParent;
                        } else {
                            if (!parent.equals(elementParent)) {
                                return false;
                            }
                        }

                        elements.add((IDataElement)treeItem.getChild());
                        testElements = true;
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



}
