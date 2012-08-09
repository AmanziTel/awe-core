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

package org.amanzi.awe.views.statistics.widget;

import java.util.Collection;

import org.amanzi.awe.statistics.manager.StatisticsManager;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.ui.view.widget.internal.AbstractComboWidget;
import org.amanzi.awe.views.statistics.widget.TemplateComboWidget.ITemplateSelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TemplateComboWidget extends AbstractComboWidget<Template, ITemplateSelectionListener> {

    public interface ITemplateSelectionListener extends AbstractComboWidget.IComboSelectionListener {

        void onTemplateSelected(Template template);

    }

    private StatisticsManager statisticsManager;

    /**
     * @param parent
     * @param label
     */
    public TemplateComboWidget(final Composite parent, final ITemplateSelectionListener listener, final String label) {
        super(parent, listener, label);
    }

    @Override
    protected Collection<Template> getItems() {
        if (statisticsManager != null) {
            return statisticsManager.getAvailableTemplates();
        }

        return null;
    }

    public void setStatisticsManager(final StatisticsManager manager) {
        this.statisticsManager = manager;
        fillCombo();
    }

    @Override
    protected String getItemName(final Template item) {
        return item.getTemplateName();
    }

    @Override
    protected void fireListener(final ITemplateSelectionListener listener, final Template selectedItem) {
        listener.onTemplateSelected(selectedItem);
    }


}
