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

package org.amanzi.awe.ui.view.widget.internal;

import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.ui.view.widget.internal.AbstractAWEWidget.IAWEWidgetListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractAWEWidget<C extends Control, L extends IAWEWidgetListener> {

    protected interface IAWEWidgetListener {

    }

    private C widget;

    private final Composite parent;

    private final int style;

    private final Set<L> listeners = new HashSet<L>();

    protected AbstractAWEWidget(final Composite parent, final int style) {
        this.parent = parent;
        this.style = style;
    }

    public void initializeWidget() {
        widget = createWidget(parent, style);
    }

    protected abstract C createWidget(Composite parent, int style);

    public void setLayout(final Layout layout) {
        widget.setLayoutData(layout);
    }

    public void addListener(final L listener) {
        listeners.add(listener);
    }

    protected Set<L> getListeners() {
        return listeners;
    }

    protected void dispose() {
        listeners.clear();
    }

}
