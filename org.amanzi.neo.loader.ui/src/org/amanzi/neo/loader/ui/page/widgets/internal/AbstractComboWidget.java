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

package org.amanzi.neo.loader.ui.page.widgets.internal;

import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractComboWidget extends AbstractPageWidget<Combo> implements ModifyListener {

    private static final int EDITABLE_COMBO_STYLE = SWT.DROP_DOWN;

    private static final int NON_EDITABLE_COMBO_STYLE = SWT.DROP_DOWN | SWT.READ_ONLY;

    private final boolean isEditable;

    private final String labelText;

    /**
     * @param loaderPage
     * @param projectModelProvider
     */
    protected AbstractComboWidget(String labelText, final boolean isEditable, final boolean isEnabled,
            final ILoaderPage< ? > loaderPage, final IProjectModelProvider projectModelProvider) {
        super(isEnabled, loaderPage, projectModelProvider);
        this.isEditable = isEditable;
        this.labelText = labelText;
    }

    @Override
    protected Combo createWidget(final Composite parent, final int style) {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(getLabelLayout());
        label.setText(labelText);

        Combo combo = new Combo(parent, style);

        combo.addModifyListener(this);
        combo.setLayoutData(getComboLayoutData());

        return combo;
    }

    protected static GridData getComboLayoutData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
    }

    @Override
    protected int getStyle() {
        return isEditable ? EDITABLE_COMBO_STYLE : NON_EDITABLE_COMBO_STYLE;
    }

    public String getText() {
        return getWidget().getText();
    }

    public abstract void fillData();

    @Override
    public void initializeWidget() {
        super.initializeWidget();

        fillData();
    }

    public void updateData() {

    }

}
