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

package org.amanzi.neo.loader.ui.preference.dateformat;

import org.amanzi.neo.dateformat.DateFormatManager;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * DateFormatPreferencePage preference page
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DateFormatPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, Listener {

    private static final int TEXT_FIELD_WITDH = 300;
    // TODO: maybe it make sense to create some LayoutManager and move all constants in this class?
    private static final Layout LAYOUT_FOR_TWO_COMPONENTS = new GridLayout(2, false);
    private static final Layout LAYOUT_FOR_ONE_COMPONENTS = new GridLayout(1, false);

    private FormatTableViewer tableViewer;
    private Composite tableViewerComposite;
    private Text inputField;
    private Button addButton;
    private DateFormatManager formatManager;

    @Override
    public void init(final IWorkbench workbench) {
        formatManager = DateFormatManager.getInstance();
    }

    @Override
    protected Control createContents(final Composite parent) {
        createTable(parent);
        createControls(parent);
        return parent;
    }

    /**
     * @param tableViewerComposite2
     */
    private void createControls(final Composite tableComposite) {
        Composite controlsComposite = new Composite(tableComposite, SWT.NONE);
        controlsComposite.setLayout(LAYOUT_FOR_TWO_COMPONENTS);
        GridData data = createGridData();
        data.grabExcessVerticalSpace = false;
        controlsComposite.setLayoutData(data);
        inputField = new Text(controlsComposite, SWT.BORDER);
        data = createGridData();
        data.widthHint = TEXT_FIELD_WITDH;
        inputField.setLayoutData(data);
        addButton = new Button(controlsComposite, SWT.NONE);
        addButton.setText(Messages.dateTypesPreferencePageAddButton);
        addButton.addListener(SWT.MouseUp, this);
    }

    /**
     * @param parent
     */
    private void createTable(final Composite parent) {
        tableViewerComposite = new Composite(parent, SWT.NONE);
        tableViewerComposite.setLayout(LAYOUT_FOR_ONE_COMPONENTS);
        tableViewerComposite.setLayoutData(createGridData());
        tableViewer = new FormatTableViewer(tableViewerComposite, SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.create();
        tableViewer.setDefaultFormat(formatManager.getDefaultFormat());
        tableViewer.setInput(formatManager.getAllDateFormats());

    }

    /**
     * @return
     */
    private GridData createGridData() {
        return new GridData(SWT.FILL, SWT.FILL, true, true);
    }

    @Override
    public void handleEvent(final Event event) {
        switch (event.type) {
        case SWT.MouseUp:
            String format = inputField.getText();
            if (!StringUtils.isEmpty(format)) {
                tableViewer.add(format);
                tableViewer.refresh();
            }
        }
    }

    @Override
    protected void performApply() {
        super.performApply();
        formatManager.addNewFormats(tableViewer.getAddedFormats(), tableViewer.getDefaultFormat());
    }
}
