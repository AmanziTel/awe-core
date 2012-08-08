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

package org.amanzi.neo.loader.ui.page.preference.dateformat;

import org.amanzi.neo.loader.ui.page.preference.dateformat.enumeration.DateFormatPreferencePageTableColumns;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
public class DateFormatPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final int EXAMPLE_COLUMN_WIDTH = 300;
    private static final int FORMAT_COLUMN_WIDTH = 200;
    private static final Layout LAYOUT_FOR_ONE_COMPONENTS = new GridLayout(1, false);;
    private static final IContentProvider CONTENT_PROVIDER = new DateFormatTableContentProvider();
    private static final DateFormatTableColumnProvider EXAMPLE_COLUMN_LABEL_PROVIDER = new DateFormatTableColumnProvider(
            DateFormatPreferencePageTableColumns.EXAMPLE_COLUMN);
    private static final DateFormatTableColumnProvider FORMAT_COLUMN_LABEL_PROVIDER = new DateFormatTableColumnProvider(
            DateFormatPreferencePageTableColumns.FORMAT_COLUMN);
    private TableViewer tableViewer;

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected Control createContents(Composite parent) {
        createTable(parent);
        return parent;
    }

    /**
     * @param parent
     */
    private void createTable(Composite parent) {
        Composite tableViewerComposite = new Composite(parent, SWT.NONE);
        tableViewerComposite.setLayout(LAYOUT_FOR_ONE_COMPONENTS);
        tableViewerComposite.setLayoutData(createGridData());

        tableViewer = new TableViewer(tableViewerComposite, SWT.FULL_SELECTION | SWT.BORDER);
        Table table = tableViewer.getTable();
        table.setLayoutData(createGridData());
        createTableColumn(EXAMPLE_COLUMN_WIDTH, tableViewer, EXAMPLE_COLUMN_LABEL_PROVIDER);
        createTableColumn(FORMAT_COLUMN_WIDTH, tableViewer, FORMAT_COLUMN_LABEL_PROVIDER);
        table.setHeaderVisible(true);
        tableViewer.setContentProvider(CONTENT_PROVIDER);
        tableViewer.add(new String[] {"f", "b", "c"});
    }

    /**
     * @param columnName
     * @param columnWidth
     * @param tableViewer2
     * @return
     */
    private TableViewerColumn createTableColumn(int columnWidth, TableViewer viewer, DateFormatTableColumnProvider provider) {
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

}
