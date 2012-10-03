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

package org.amanzi.awe.views.distribution.widgets;

import org.amanzi.awe.distribution.engine.manager.DistributionManager;
import org.amanzi.awe.distribution.model.type.IDistributionType.ChartType;
import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.CheckBoxWidget;
import org.amanzi.awe.ui.view.widgets.CheckBoxWidget.ICheckBoxSelected;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.views.distribution.widgets.ChartTypeWidget.IChartTypeListener;
import org.amanzi.awe.views.distribution.widgets.ColoringPropertiesWidget.IColoringPropertiesListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ColoringPropertiesWidget extends AbstractAWEWidget<Composite, IColoringPropertiesListener> implements IChartTypeListener, ICheckBoxSelected {

    public interface IColoringPropertiesListener extends AbstractAWEWidget.IAWEWidgetListener {

        public void onChartTypeChanged(ChartType chartType);

        public void update();

    }

    private ChartTypeWidget chartTypeCombo;

    private CheckBoxWidget colorProperties;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public ColoringPropertiesWidget(final Composite parent, final IColoringPropertiesListener listener) {
        super(parent, SWT.NONE, listener);
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        Composite mainComposite = new Composite(parent, style);
        mainComposite.setLayout(new GridLayout(2, false));

        colorProperties = AWEWidgetFactory.getFactory().addCheckBoxWidget(this, "Color properties", mainComposite);
        chartTypeCombo = addChartTypeWidget(mainComposite, "Chart Type:");

        return mainComposite;
    }

    public void setDistributionManager(final DistributionManager distributionManager) {
        chartTypeCombo.setDistributionManager(distributionManager);
    }

    @Override
    public void onChartTypeSelected(final ChartType chartType) {
        for (IColoringPropertiesListener listener : getListeners()) {
            listener.onChartTypeChanged(chartType);
        }
    }

    private ChartTypeWidget addChartTypeWidget(final Composite parent, final String label) {
        return initializeWidget(new ChartTypeWidget(parent, this, label));
    }

    private <T extends AbstractAWEWidget< ? , ? >> T initializeWidget(final T widget) {
        widget.initializeWidget();
        return widget;
    }

    @Override
    public void onCheckBoxSelected(final CheckBoxWidget source) {
        boolean state = source.isChecked();

        if (source.equals(colorProperties)) {
            setStandardStatusPanelVisible(!state);
            setBlendPanelVisible(state);
        }
    }

    private void setStandardStatusPanelVisible(final boolean isVisible) {

    }

    private void setBlendPanelVisible(final boolean isVisible) {

    }

}
