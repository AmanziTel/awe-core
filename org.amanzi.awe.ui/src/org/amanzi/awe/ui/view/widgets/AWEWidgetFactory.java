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

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.ui.view.widgets.CRSSelector.ICRSSelectorListener;
import org.amanzi.awe.ui.view.widgets.CharsetWidget.ICharsetChangedListener;
import org.amanzi.awe.ui.view.widgets.CheckBoxWidget.ICheckBoxSelected;
import org.amanzi.awe.ui.view.widgets.ColorWidget.IColorChangedListener;
import org.amanzi.awe.ui.view.widgets.DateTimeWidget.ITimeChangedListener;
import org.amanzi.awe.ui.view.widgets.DriveComboWidget.IDriveSelectionListener;
import org.amanzi.awe.ui.view.widgets.NetworkComboWidget.INetworkSelectionListener;
import org.amanzi.awe.ui.view.widgets.PaletteComboWidget.IPaletteChanged;
import org.amanzi.awe.ui.view.widgets.PropertyComboWidget.IPropertySelectionListener;
import org.amanzi.awe.ui.view.widgets.ResourceSelectorWidget.IResourceSelectorListener;
import org.amanzi.awe.ui.view.widgets.ResourceSelectorWidget.ResourceType;
import org.amanzi.awe.ui.view.widgets.SpinnerWidget.ISpinnerListener;
import org.amanzi.awe.ui.view.widgets.TextWidget.ITextChandedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class AWEWidgetFactory {

    private static class WizardFactoryHolder {
        private static volatile AWEWidgetFactory instance = new AWEWidgetFactory();
    }

    public static AWEWidgetFactory getFactory() {
        return WizardFactoryHolder.instance;
    }

    private final IProjectModelProvider projectModelProvider;

    private final IDriveModelProvider driveModelProvider;

    private final INetworkModelProvider networkModelProvider;

    private AWEWidgetFactory() {
        this.projectModelProvider = AWEUIPlugin.getDefault().getProjectModelProvider();
        this.driveModelProvider = AWEUIPlugin.getDefault().getDriveModelProvider();
        this.networkModelProvider = AWEUIPlugin.getDefault().getNetworkModelProvider();
    }

    public CharsetWidget addCharsetWidget(final ICharsetChangedListener listener, final Composite parent, final int minLabelWidth) {
        return initializeWidget(new CharsetWidget(parent, listener, minLabelWidth));
    }

    public CheckBoxWidget addCheckBoxWidget(final ICheckBoxSelected listener, final String label, final Composite parent) {
        return initializeWidget(new CheckBoxWidget(parent, listener, label));
    }

    public ColorWidget addColorWidget(final IColorChangedListener listener, final Composite parent, final String tooltip) {
        return initializeWidget(new ColorWidget(parent, listener, tooltip));
    }

    public CRSSelector addCRSSelectorWidget(final ICRSSelectorListener listener, final Composite parent) {
        return initializeWidget(new CRSSelector(parent, listener));
    }

    public ResourceSelectorWidget addDirectorySelector(final Composite parent, final IResourceSelectorListener listener) {
        return initializeWidget(new ResourceSelectorWidget(ResourceType.DIRECTORY, parent, listener, projectModelProvider));
    }

    public DriveComboWidget addDriveComboWidget(final IDriveSelectionListener listener, final String labelText,
            final Composite parent, final int minimalLabelWidth) {
        return initializeWidget(new DriveComboWidget(parent, listener, labelText, projectModelProvider, driveModelProvider,
                minimalLabelWidth));
    }

    public ResourceSelectorWidget addFileSelector(final Composite parent, final IResourceSelectorListener listener,
            final String... fileExtensions) {
        return initializeWidget(new ResourceSelectorWidget(ResourceType.FILE, parent, listener, projectModelProvider,
                fileExtensions));
    }

    public NetworkComboWidget addNetworkComboWidget(final INetworkSelectionListener listener, final String labelText,
            final Composite parent, final int minimalLabelWidth) {
        return initializeWidget(new NetworkComboWidget(parent, listener, labelText, projectModelProvider, networkModelProvider,
                minimalLabelWidth));
    }

    public PaletteComboWidget addPaletteComboWidget(final IPaletteChanged listener, final String label, final Composite parent) {
        return initializeWidget(new PaletteComboWidget(parent, listener, label));
    }

    public DateTimeWidget addPeriodWidget(final ITimeChangedListener listener, final String label, final Composite parent,
            final int minimalLabelWidth) {
        return initializeWidget(new DateTimeWidget(parent, listener, label, minimalLabelWidth));
    }

    public PropertyComboWidget addPropertyComboWidget(final IPropertySelectionListener listener, final String labelText,
            final Composite parent, final int minimalLabelWidth, final boolean shouldSort) {
        return initializeWidget(new PropertyComboWidget(parent, listener, labelText, minimalLabelWidth, shouldSort));
    }

    public SpinnerWidget addSpinnerWidget(final ISpinnerListener listener, final String label, final Composite parent) {
        return initializeWidget(new SpinnerWidget(parent, listener, label));
    }

    public TextWidget addStyledTextWidget(final ITextChandedListener listener, final int style, final String label,
            final Composite parent) {
        return initializeWidget(new TextWidget(parent, style, listener, label));
    }

    public TextWidget addTextWidget(final ITextChandedListener listener, final String label, final Composite parent) {
        return initializeWidget(new TextWidget(parent, SWT.BORDER | SWT.READ_ONLY, listener, label));
    }

    public <T extends AbstractAWEWidget< ? , ? >> T initializeWidget(final T widget) {
        widget.initializeWidget();
        return widget;
    }

}
