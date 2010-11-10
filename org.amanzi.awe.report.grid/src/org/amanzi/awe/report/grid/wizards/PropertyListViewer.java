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

package org.amanzi.awe.report.grid.wizards;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Labeled combo viewer that supports filtering and based on list input.
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class PropertyListViewer {
    private Composite parent;
    private String label;
    private Text txtFilter;
    private ListViewer propertyViewer;
    private Composite container;
    private Button btnApplyFilter;
    private Button btnClearFilter;
    private org.eclipse.swt.widgets.List lstSelectedSites;

    public PropertyListViewer(Composite parent, String label) {
        this.parent = parent;
        this.label = label;
        createControls();
    }

    private void createControls() {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());

        Label lblProperty = new Label(container, SWT.LEFT);
        lblProperty.setText(label);

        FormData layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(0, 2);
        layoutData.right = new FormAttachment(20, 0);
        lblProperty.setLayoutData(layoutData);

        txtFilter = new Text(container, SWT.BORDER);
        layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(lblProperty, 2);
        layoutData.right = new FormAttachment(80, -2);
        txtFilter.setLayoutData(layoutData);

        final RegexViewerFilter regexViewerFilter = new RegexViewerFilter();

        btnApplyFilter = new Button(container, SWT.PUSH);
        btnApplyFilter.setText("Find");
        btnApplyFilter.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String text = txtFilter.getText();
                if (text != null && !text.isEmpty()) {
                    regexViewerFilter.setFilterText(text);
                    propertyViewer.setFilters(new ViewerFilter[] {regexViewerFilter});
                    org.eclipse.swt.widgets.List list = propertyViewer.getList();
                    btnClearFilter.setEnabled(true);
                }
            }

        });
        layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(80, 1);
        layoutData.right = new FormAttachment(90, -1);
        btnApplyFilter.setLayoutData(layoutData);

        btnClearFilter = new Button(container, SWT.PUSH);
        btnClearFilter.setText("Clear");
        btnClearFilter.setEnabled(false);
        btnClearFilter.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                propertyViewer.setFilters(new ViewerFilter[0]);
                btnClearFilter.setEnabled(false);
                txtFilter.setText("");
            }

        });
        layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(90, 1);
        layoutData.right = new FormAttachment(100, -1);
        btnClearFilter.setLayoutData(layoutData);

        Label lblAvailableSites = new Label(container, SWT.NONE);
        lblAvailableSites.setText("Available:");
        layoutData = new FormData();
        layoutData.top = new FormAttachment(txtFilter, 2);
        layoutData.left = new FormAttachment(lblProperty, 2);
        layoutData.right = new FormAttachment(50, -1);
        lblAvailableSites.setLayoutData(layoutData);

        Label lblSelectedSites = new Label(container, SWT.NONE);
        lblSelectedSites.setText("Selected:");
        layoutData = new FormData();
        layoutData.top = new FormAttachment(txtFilter, 2);
        layoutData.left = new FormAttachment(60, 2);
        lblSelectedSites.setLayoutData(layoutData);

        Composite viewerPanel = new Composite(container, SWT.NONE);
        viewerPanel.setLayout(new FillLayout());
        layoutData = new FormData();
        layoutData.top = new FormAttachment(lblAvailableSites, 2);
        layoutData.left = new FormAttachment(lblProperty, 2);
        layoutData.right = new FormAttachment(100, -2);
        viewerPanel.setLayoutData(layoutData);

        propertyViewer = new ListViewer(viewerPanel);
        propertyViewer.setLabelProvider(new ArrayLabelProvider());
        propertyViewer.setContentProvider(new ArrayContentProvider());
        propertyViewer.addFilter(regexViewerFilter);
        propertyViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getSelection();
                if (selection instanceof StructuredSelection) {
                    Iterator iterator = ((StructuredSelection)selection).iterator();
                    while (iterator.hasNext()) {
                        String itemToAdd = iterator.next().toString();
                        if (!Arrays.asList(lstSelectedSites.getItems()).contains(itemToAdd)) {
                            propertyViewer.remove(itemToAdd);
                            lstSelectedSites.add(itemToAdd);
                        }
                    }
                }
            }

        });

        lstSelectedSites = new org.eclipse.swt.widgets.List(viewerPanel, SWT.BORDER | SWT.V_SCROLL);
        lstSelectedSites.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                for (String item : lstSelectedSites.getSelection()) {
                    propertyViewer.add(item);
                    lstSelectedSites.remove(item);
                }
            }

        });
    }

    public Composite getContainer() {
        return container;
    }

    public void setInput(List<String> input) {
        propertyViewer.setInput(input);
    }
   
    public void setInput(String[] input) {
        propertyViewer.setInput(input);
    }

    private class ArrayLabelProvider implements ILabelProvider {

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public String getText(Object element) {
            return (String)element;
        }

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return true;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

    }

    private class RegexViewerFilter extends ViewerFilter {

        private String filter;

        /**
         * 
         */
        public RegexViewerFilter() {
            super();
            // this.text = combo;
        }

        public void setFilterText(String text) {
            this.filter = text;
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (filter == null || filter.isEmpty()) {
                return true;
            }
            String clearedText = filter.toLowerCase().replaceAll("\\.", "\\.");
            return ((String)element).toLowerCase().matches(".*" + clearedText + ".*");
        }

    }

    public List<String> getSelection() {
        return Arrays.asList(lstSelectedSites.getItems());
    }
}
