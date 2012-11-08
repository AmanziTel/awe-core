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

package org.amanzi.awe.nem.ui.wizard.pages.export;

import java.nio.charset.Charset;

import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.widgets.ExportSeparatorWidget;
import org.amanzi.awe.nem.ui.widgets.ExportSeparatorWidget.ISeparatorChangedListener;
import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.CharsetWidget;
import org.amanzi.awe.ui.view.widgets.CharsetWidget.ICharsetChangedListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class EditExportSettingsPage extends WizardPage implements ICharsetChangedListener, ISeparatorChangedListener {

    private static final GridLayout ONE_COLUMN_LAYOUT = new GridLayout(1, false);

    private Composite main;

    private Charset charset;

    private String separator;

    private ExportSeparatorWidget separatorWidget;

    private CharsetWidget charsetWidget;

    /**
     * @param pageName
     */
    public EditExportSettingsPage() {
        super(NEMMessages.EXPORT_GENERAL_SETTINGS_PAGE);
        setTitle(NEMMessages.EXPORT_GENERAL_SETTINGS_PAGE);
    }

    @Override
    public void createControl(final Composite parent) {
        main = new Composite(parent, SWT.NONE);
        main.setLayout(ONE_COLUMN_LAYOUT);
        main.setLayoutData(new GridData(GridData.FILL_BOTH));

        charsetWidget = AWEWidgetFactory.getFactory().addCharsetWidget(this, main);
        separatorWidget = new ExportSeparatorWidget(main, this);
        separatorWidget.initializeWidget();

        setControl(main);
    }

    @Override
    public void dispose() {
        charsetWidget.dispose();
        separatorWidget.dispose();
        super.dispose();
    }

    /**
     * @return Returns the charset.
     */
    public Charset getCharset() {
        return charset;
    }

    public String getSeparator() {
        return separator;
    }

    @Override
    public void onCharsetChanged(final Charset charset) {
        this.charset = charset;

    }

    @Override
    public void onSeparatorChanged(final String separator) {
        this.separator = separator;

    }

}
