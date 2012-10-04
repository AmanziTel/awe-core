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

package org.amanzi.awe.ui.view.widgets;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.awe.ui.view.widgets.PropertyComboWidget.IPropertySelectionListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyComboWidget extends AbstractComboWidget<String, IPropertySelectionListener> {

    public interface IPropertySelectionListener extends AbstractComboWidget.IComboSelectionListener {

        void onPropertySelected(String property);

    }

    private static final String SEPARATOR = "------";

    private final Set<String> defaultProperties = new LinkedHashSet<String>();

    private IPropertyStatisticsModel propertyModel;

    private INodeType nodeType;

    /**
     * @param parent
     * @param label
     */
    protected PropertyComboWidget(final Composite parent, final IPropertySelectionListener listener, final String label,
            final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
    }

    @Override
    protected Collection<String> getItems() {
        if (propertyModel != null) {
            Set<String> properties = new LinkedHashSet<String>();
            properties.addAll(defaultProperties);
            if (!defaultProperties.isEmpty()) {
                properties.add(SEPARATOR);
            }
            properties.addAll(getPropertyNames(nodeType));
            return properties;
        }
        return null;
    }

    protected Set<String> getPropertyNames(final INodeType nodeType) {
        if (nodeType == null) {
            return propertyModel.getPropertyNames();
        } else {
            return propertyModel.getPropertyNames(nodeType);
        }
    }

    protected IPropertyStatisticsModel getModel() {
        return propertyModel;
    }

    public void setDefaultProperties(final Iterable<String> properties) {
        for (String prop : properties) {
            defaultProperties.add(prop);
        }
    }

    @Override
    protected String getItemName(final String item) {
        return item;
    }

    public void setModel(final IMeasurementModel model) {
        if (model != null) {
            this.propertyModel = model.getPropertyStatistics();
            this.nodeType = model.getMainMeasurementNodeType();

            fillCombo();
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    public void setModel(final IPropertyStatisticalModel model, final INodeType nodeType) {
        this.propertyModel = model.getPropertyStatistics();
        this.nodeType = nodeType;

        fillCombo();
        setEnabled(true);
    }

    public void setModel(final IPropertyStatisticalModel model) {
        this.propertyModel = model.getPropertyStatistics();
    }

    @Override
    protected void fireListener(final IPropertySelectionListener listener, final String selectedItem) {
        listener.onPropertySelected(selectedItem);
    }

}
