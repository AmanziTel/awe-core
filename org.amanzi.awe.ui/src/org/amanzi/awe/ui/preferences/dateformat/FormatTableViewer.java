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

package org.amanzi.awe.ui.preferences.dateformat;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>
 * format table viewer
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FormatTableViewer extends TableViewer {

    private static final int EXAMPLE_COLUMN_WIDTH = 300;
    private static final int FORMAT_COLUMN_WIDTH = 200;
    private static final int IS_DEFAULT_TABLE_WIDTH = 60;

    private static final IContentProvider CONTENT_PROVIDER = new DateFormatTableContentProvider();

    private static final DateFormatTableLabelProvider EXAMPLE_COLUMN_LABEL_PROVIDER = new DateFormatTableLabelProvider(
            DateFormatPreferencePageTableColumns.EXAMPLE_COLUMN);
    private static final DateFormatTableLabelProvider IS_DEFAULT_COLUMN_LABEL_PROVIDER = new DateFormatTableLabelProvider(
            DateFormatPreferencePageTableColumns.IS_DEFAULT_COLUMN);
    private static final DateFormatTableLabelProvider FORMAT_COLUMN_LABEL_PROVIDER = new DateFormatTableLabelProvider(
            DateFormatPreferencePageTableColumns.FORMAT_COLUMN);

    private final Collection<String> addedFormats;
    private String defaultFormat;

    /**
     * @param parent
     * @param style
     */
    public FormatTableViewer(final Composite parent, final int style) {
        super(parent, style);
        addedFormats = new ArrayList<String>();
    }

    /**
     *
     */
    protected void setInput(final Collection<String> input) {
        addedFormats.addAll(input);
        super.setInput(addedFormats);

    }

    @Override
    public void add(final Object element) {
        assert !StringUtils.isEmpty((String)element);
        addedFormats.add((String)element);
        super.add(element);
    }

    /**
     * @return Returns the addedFormats.
     */
    protected Collection<String> getAddedFormats() {
        return addedFormats;
    }

    /**
     * @return Returns the defaultFormat.
     */
    protected String getDefaultFormat() {
        return defaultFormat;
    }

    public void create() {
        Table table = getTable();
        table.setLayoutData(createGridData());
        table.setLinesVisible(true);
        TableViewerColumn isDefaultColumn = createTableColumn(IS_DEFAULT_TABLE_WIDTH, IS_DEFAULT_COLUMN_LABEL_PROVIDER);
        isDefaultColumn.setEditingSupport(new IsDefaultEditor(this));
        createTableColumn(EXAMPLE_COLUMN_WIDTH, EXAMPLE_COLUMN_LABEL_PROVIDER);
        createTableColumn(FORMAT_COLUMN_WIDTH, FORMAT_COLUMN_LABEL_PROVIDER);
        table.setHeaderVisible(true);
        setContentProvider(CONTENT_PROVIDER);
    }

    /**
     * @param columnName
     * @param columnWidth
     * @param tableViewer2
     * @return
     */
    private TableViewerColumn createTableColumn(final int columnWidth, final DateFormatTableLabelProvider provider) {
        TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.LEFT);
        TableColumn column = viewerColumn.getColumn();
        column.setText(provider.getColumnName());
        column.setWidth(columnWidth);
        column.setResizable(true);
        provider.initViewer(this);
        viewerColumn.setLabelProvider(provider);
        return viewerColumn;
    }

    /**
     * set default format
     * 
     * @param value
     */
    protected void setDefaultFormat(final String value) {
        defaultFormat = value;
    }

    /**
     * @return
     */
    private GridData createGridData() {
        return new GridData(SWT.FILL, SWT.FILL, true, true);
    }

}
