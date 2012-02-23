package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.ui.utils.AbstractDialog;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * <p>
 * Dialog for copy of element
 * </p>
 * 
 * @author ladornaya_a
 * @since 1.0.0
 */
public class CopyOfElementDialog extends AbstractDialog<Integer> {

    /*
     * table
     */
    private TableViewer tableViewer;

    /*
     * list content properties of selected element
     */
    private List<String> properties;

    // sector
    private final static String[] SECTOR_PROPERTIES = {"name", "ci", "lac"};

    // site
    private final static String[] SITE_PROPERTIES = {"name", "lat", "lon"};

    // selected element for copy
    private IDataElement element;

    public CopyOfElementDialog(Shell parent, IDataElement element, String title, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        status = SWT.CANCEL;
        this.element = element;
        updateProperies();
    }

    /**
     * Update properties from selected element
     */
    private void updateProperies() {

        properties = new ArrayList<String>();

        String type = NodeTypeManager.getType(element).getId();

        // if sector
        if (type.equals(NetworkElementNodeType.SECTOR.getId())) {

            // add to properties list unique properties for sector
            for (int i = 0; i < SECTOR_PROPERTIES.length; i++) {
                properties.add(SECTOR_PROPERTIES[i]);
            }
        }

        // if site
        else if (type.equals(NetworkElementNodeType.SITE.getId())) {

            // add to properties list unique properties for site
            for (int j = 0; j < SITE_PROPERTIES.length; j++) {
                properties.add(SITE_PROPERTIES[j]);
            }
        }

        // other elements
        else {
            properties.add(AbstractService.NAME);
        }

        // add other properties
        for (String property : element.keySet()) {
            if (!properties.contains(property)) {
                properties.add(property);
            }
        }
    }

    @Override
    protected void createContents(Shell shell) {
        tableViewer = new TableViewer(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        // createColumns(shell, tableViewer);
        final Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        tableViewer.setContentProvider(new TableContentProvider());
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setInput(StringUtils.EMPTY);

        // Layout the viewer
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        tableViewer.getControl().setLayoutData(gridData);
    }

    private class TableContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return null;
        }

    }

    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            return null;
        }

    }

}
