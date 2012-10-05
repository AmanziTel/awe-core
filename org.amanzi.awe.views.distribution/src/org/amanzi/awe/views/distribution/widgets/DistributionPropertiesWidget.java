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
import org.amanzi.awe.ui.view.widgets.ColorWidget;
import org.amanzi.awe.ui.view.widgets.ColorWidget.IColorChangedListener;
import org.amanzi.awe.ui.view.widgets.PaletteComboWidget;
import org.amanzi.awe.ui.view.widgets.PaletteComboWidget.IPaletteChanged;
import org.amanzi.awe.ui.view.widgets.SpinnerWidget;
import org.amanzi.awe.ui.view.widgets.SpinnerWidget.ISpinnerListener;
import org.amanzi.awe.ui.view.widgets.TextWidget;
import org.amanzi.awe.ui.view.widgets.TextWidget.ITextChandedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.views.distribution.widgets.ChartTypeWidget.IChartTypeListener;
import org.amanzi.awe.views.distribution.widgets.DistributionPropertiesWidget.IDistributionPropertiesListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionPropertiesWidget extends AbstractAWEWidget<Composite, IDistributionPropertiesListener>
        implements
            IChartTypeListener,
            ICheckBoxSelected,
            ITextChandedListener,
            ISpinnerListener,
            IColorChangedListener,
            IPaletteChanged {

    public interface IDistributionPropertiesListener extends AbstractAWEWidget.IAWEWidgetListener {

        public void onChartTypeChanged(ChartType chartType);

    }

    private ChartTypeWidget chartTypeCombo;

    private CheckBoxWidget colorProperties;

    private TextWidget selectedValues;

    private SpinnerWidget spinner;

    private CheckBoxWidget blendProperties;

    private ColorWidget leftColor;

    private ColorWidget rightColor;

    private CheckBoxWidget thirdColorProperties;

    private ColorWidget thirdColor;

    private PaletteComboWidget paletteCombo;

    private boolean isBlendPanelHidden = true;

    private boolean isThirdColorPropertiesHidden = true;

    private Composite mainComposite;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public DistributionPropertiesWidget(final Composite parent, final IDistributionPropertiesListener listener) {
        super(parent, SWT.NONE, listener);
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        mainComposite = new Composite(parent, style);
        mainComposite.setLayout(new GridLayout(7, false));

        colorProperties = AWEWidgetFactory.getFactory().addCheckBoxWidget(this, "Color properties", mainComposite);
        chartTypeCombo = addChartTypeWidget(mainComposite, "Chart Type:");

        selectedValues = AWEWidgetFactory.getFactory().addTextWidget(this, "Selected values:", mainComposite);
        spinner = AWEWidgetFactory.getFactory().addSpinnerWidget(this, "Selection adjency:", mainComposite);

        blendProperties = AWEWidgetFactory.getFactory().addCheckBoxWidget(this, "Blend", mainComposite);

        rightColor = AWEWidgetFactory.getFactory().addColorWidget(this, mainComposite);
        leftColor = AWEWidgetFactory.getFactory().addColorWidget(this, mainComposite);
        thirdColorProperties = AWEWidgetFactory.getFactory().addCheckBoxWidget(this, "Third color:", mainComposite);
        thirdColor = AWEWidgetFactory.getFactory().addColorWidget(this, mainComposite);

        paletteCombo = AWEWidgetFactory.getFactory().addPaletteComboWidget(this, "Palette:", mainComposite);

        setBlendPanelHidden(true);
        setStandardStatusPanelHidden(false);

        return mainComposite;
    }

    public void setDistributionManager(final DistributionManager distributionManager) {
        chartTypeCombo.setDistributionManager(distributionManager);
    }

    @Override
    public void onChartTypeSelected(final ChartType chartType) {
        for (IDistributionPropertiesListener listener : getListeners()) {
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
        boolean isHidden = !source.isChecked();

        if (source.equals(colorProperties)) {
            setStandardStatusPanelHidden(!isHidden);
            setBlendPanelHidden(isHidden);
        } else if (source.equals(blendProperties)) {
            isBlendPanelHidden = isHidden;
            setBlendPanelHidden(false);

        } else if (source.equals(thirdColorProperties)) {
            isThirdColorPropertiesHidden = isHidden;
            setColorsPanelHidden(false);
        }

        mainComposite.layout(true, true);
    }

    private void setColorsPanelHidden(final boolean isHidden) {
        rightColor.setHidden(isHidden);
        leftColor.setHidden(isHidden);

        thirdColorProperties.setHidden(isHidden);
        thirdColor.setHidden(isHidden || isThirdColorPropertiesHidden);
    }

    private void setPalettePanelHidden(final boolean isHidden) {
        paletteCombo.setHidden(isHidden);
    }

    private void setStandardStatusPanelHidden(final boolean isHidden) {
        selectedValues.setHidden(isHidden);
        spinner.setHidden(isHidden);
    }

    private void setBlendPanelHidden(final boolean isHidden) {
        blendProperties.setHidden(isHidden);

        setColorsPanelHidden(isHidden || isBlendPanelHidden);
        setPalettePanelHidden(isHidden || !isBlendPanelHidden);
    }

    @Override
    public void setVisible(boolean isVisible) {
        chartTypeCombo.setVisible(isVisible);

        super.setVisible(isVisible);
    }

}
