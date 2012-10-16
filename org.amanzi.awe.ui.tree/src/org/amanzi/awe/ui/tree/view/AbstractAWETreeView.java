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

package org.amanzi.awe.ui.tree.view;

import java.util.Set;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.tree.provider.AWETreeContentProvider;
import org.amanzi.awe.ui.tree.provider.AWETreeLabelProvider;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.amanzi.awe.ui.views.IAWEView;
import org.amanzi.awe.views.properties.AWEPropertiesPlugin;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractAWETreeView extends ViewPart implements IAWEEventListenter, IAWEView {

    private static final EventStatus[] DEFAULT_SUPPORTED_EVENTS = {EventStatus.DATA_UPDATED, EventStatus.PROJECT_CHANGED};

    private final AWEEventManager eventManager;

    private TreeViewer treeViewer;

    private final FactoryResolver factoryResolver;

    protected AbstractAWETreeView() {
        eventManager = AWEEventManager.getManager();

        factoryResolver = FactoryResolver.getResolver();
    }

    @Override
    public void createPartControl(final Composite parent) {
        treeViewer = createTreeViewer(parent);
        final MenuManager menu = createMenu(treeViewer);

        getSite().setSelectionProvider(treeViewer);
        getSite().registerContextMenu(menu, treeViewer);

        eventManager.addListener(this, getSupportedEvents());
    }

    protected TreeViewer createTreeViewer(final Composite parent) {
        final TreeViewer treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);

        treeViewer.setContentProvider(createContentProvider());
        treeViewer.setLabelProvider(createLabelProvider());
        treeViewer.setInput(ObjectUtils.NULL);

        parent.setLayout(new GridLayout(1, false));
        treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return treeViewer;
    }

    private MenuManager createMenu(final TreeViewer treeViewer) {
        final MenuManager menuManager = new MenuManager();
        final Menu menu = menuManager.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);

        return menuManager;
    }

    protected ITreeContentProvider createContentProvider() {
        return new AWETreeContentProvider(getFactories());
    }

    protected Set<ITreeWrapperFactory> getFactories() {
        return factoryResolver.getWrapperFactories(getViewId());
    }

    protected IBaseLabelProvider createLabelProvider() {
        return new AWETreeLabelProvider(getSupporedLabelTemplates());
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

    @Override
    public void dispose() {
        AWEPropertiesPlugin.getDefault().unregisterView(this);

        eventManager.removeListener(this);

        super.dispose();
    }

    protected EventStatus[] getSupportedEvents() {
        return DEFAULT_SUPPORTED_EVENTS;
    }

    protected abstract String[] getSupporedLabelTemplates();

    protected void update() {
        treeViewer.refresh();
    }

    @Override
    public void onEvent(final IEvent event) {
        switch (event.getStatus()) {
        case PROJECT_CHANGED:
        case DATA_UPDATED:
            update();
            break;
        default:
            break;
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter) {
        if (adapter.equals(IPropertySheetPage.class)) {
            return AWEPropertiesPlugin.getDefault().registerView(this);
        }

        return super.getAdapter(adapter);
    }
}
