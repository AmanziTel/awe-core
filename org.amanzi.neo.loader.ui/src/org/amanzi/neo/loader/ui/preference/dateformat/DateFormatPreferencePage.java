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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.preference.dateformat.enumeration.DateFormatPreferencePageTableColumns;
import org.amanzi.neo.loader.ui.preference.dateformat.manager.DateFormatManager;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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

    private static final int EXAMPLE_COLUMN_WIDTH = 300;
    private static final int FORMAT_COLUMN_WIDTH = 200;
    private static final int TEXT_FIELD_WITDH = 300;
    private static final Layout LAYOUT_FOR_ONE_COMPONENTS = new GridLayout(1, false);
    private static final Layout LAYOUT_FOR_TWO_COMPONENTS = new GridLayout(2, false);
    private static final IContentProvider CONTENT_PROVIDER = new DateFormatTableContentProvider();
    private static final DateFormatTableLabelProvider EXAMPLE_COLUMN_LABEL_PROVIDER = new DateFormatTableLabelProvider(
            DateFormatPreferencePageTableColumns.EXAMPLE_COLUMN);
    private static final DateFormatTableLabelProvider FORMAT_COLUMN_LABEL_PROVIDER = new DateFormatTableLabelProvider(
            DateFormatPreferencePageTableColumns.FORMAT_COLUMN);

    private TableViewer tableViewer;
    private Composite tableViewerComposite;
    private Text inputField;
    private Button addButton;
    private DateFormatManager formatManager;
    private List<String> addedFormats;
    private String defaultFormat;

    @Override
    public void init(IWorkbench workbench) {
        formatManager = DateFormatManager.getInstance();
        addedFormats = new ArrayList<String>();
    }

    @Override
    protected Control createContents(Composite parent) {
        createTable(parent);
        createControls(parent);
        return parent;
    }

    /**
     * @param tableViewerComposite2
     */
    private void createControls(Composite tableComposite) {
        Composite controlsComposite = new Composite(tableComposite, SWT.NONE);
        controlsComposite.setLayout(LAYOUT_FOR_TWO_COMPONENTS);
        GridData data = createGridData();
        data.grabExcessVerticalSpace = false;
        controlsComposite.setLayoutData(data);
        inputField = new Text(controlsComposite, SWT.NONE);
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
    private void createTable(Composite parent) {
        tableViewerComposite = new Composite(parent, SWT.NONE);
        tableViewerComposite.setLayout(LAYOUT_FOR_ONE_COMPONENTS);
        tableViewerComposite.setLayoutData(createGridData());

        tableViewer = new TableViewer(tableViewerComposite, SWT.FULL_SELECTION | SWT.BORDER);
        Table table = tableViewer.getTable();
        table.setLayoutData(createGridData());
        createTableColumn(EXAMPLE_COLUMN_WIDTH, tableViewer, EXAMPLE_COLUMN_LABEL_PROVIDER);
        createTableColumn(FORMAT_COLUMN_WIDTH, tableViewer, FORMAT_COLUMN_LABEL_PROVIDER);
        table.setHeaderVisible(true);
        tableViewer.setContentProvider(CONTENT_PROVIDER);
        tableViewer.add(formatManager.getAllDateFormats().toArray());

    }

    /**
     * @param columnName
     * @param columnWidth
     * @param tableViewer2
     * @return
     */
    private TableViewerColumn createTableColumn(int columnWidth, TableViewer viewer, DateFormatTableLabelProvider provider) {
        TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.RIGHT);
        TableColumn column = viewerColumn.getColumn();
        column.setText(provider.getColumnName());
        column.setWidth(columnWidth);
        column.setResizable(true);
        viewerColumn.setLabelProvider(provider);
        return viewerColumn;
    }

    /**
     * @return
     */
    private GridData createGridData() {
        return new GridData(SWT.FILL, SWT.FILL, true, true);
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.type) {
        case SWT.MouseUp:
            String format = inputField.getText();
            if (!StringUtils.isEmpty(format)) {
                addedFormats.add(format);
                tableViewer.add(format);
            }
        }
    }

    @Override
    protected void performApply() {
        super.performApply();
        for (String newFormat : addedFormats) {
            formatManager.addNewFormat(newFormat);
        }
        if (!StringUtils.isEmpty(defaultFormat)) {
            formatManager.setDefaultFormat(defaultFormat);
        }
    }
}
