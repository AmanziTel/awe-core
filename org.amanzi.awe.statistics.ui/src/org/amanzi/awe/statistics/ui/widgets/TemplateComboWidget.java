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

package org.amanzi.awe.statistics.ui.widgets;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.amanzi.awe.statistics.manager.StatisticsManager;
import org.amanzi.awe.statistics.template.ITemplate;
import org.amanzi.awe.statistics.ui.widgets.TemplateComboWidget.ITemplateSelectionListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TemplateComboWidget extends AbstractComboWidget<ITemplate, ITemplateSelectionListener> {

    public interface ITemplateSelectionListener extends AbstractComboWidget.IComboSelectionListener {

        void onTemplateSelected(ITemplate template);

    }

    private StatisticsManager statisticsManager;

    /**
     * @param parent
     * @param label
     */
    public TemplateComboWidget(final Composite parent, final ITemplateSelectionListener listener, final String label,
            final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
    }

    @Override
    protected Collection<ITemplate> getItems() {
        if (statisticsManager != null) {
            final ProgressMonitorDialog monitor = new ProgressMonitorDialog(getControl().getShell());

            try {
                monitor.run(false, false, new IRunnableWithProgress() {

                    @Override
                    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        statisticsManager.getAvailableTemplates(monitor);
                    }
                });
            } catch (final InterruptedException e) {

            } catch (final InvocationTargetException e) {

            }
            return statisticsManager.getAvailableTemplates(null);
        }

        return null;
    }

    public void setStatisticsManager(final StatisticsManager manager) {
        this.statisticsManager = manager;
        fillCombo();
    }

    @Override
    protected String getItemName(final ITemplate item) {
        return item.getName();
    }

    @Override
    protected void fireListener(final ITemplateSelectionListener listener, final ITemplate selectedItem) {
        listener.onTemplateSelected(selectedItem);
    }

}
