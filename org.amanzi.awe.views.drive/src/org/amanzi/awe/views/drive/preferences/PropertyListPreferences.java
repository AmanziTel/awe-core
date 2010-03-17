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

package org.amanzi.awe.views.drive.preferences;


import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class PropertyListPreferences extends PreferencePage implements IWorkbenchPreferencePage  {

    private static final int MIN_FIELD_WIDTH = 50;
    private static final int MAX_PROP_COUNT = 6;
    private final List<RowWr> propertyLists=new ArrayList<RowWr>();
    private Composite mainFrame;
    MultiPropertyIndex<Long> timestampIndex = null;
    private TableViewer viewer;
    
    
    @Override
    protected Control createContents(Composite parent) {
        mainFrame=new Group(parent, SWT.NULL);
        
        GridLayout mainLayout = new GridLayout(3,false);
        
        mainFrame.setLayout(mainLayout);
        
        Label label = new Label(mainFrame, SWT.LEFT);
//        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        label.setText("Label");
        
       
        viewer = new TableViewer(mainFrame, SWT.BORDER | SWT.FULL_SELECTION);
        TableContentProvider provider = new TableContentProvider();
        createTableColumn();
        
        viewer.setContentProvider(provider);
        viewer.setLabelProvider(new TableLabelProvider());
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
        viewer.getControl().setLayoutData(layoutData);
        layoutData.grabExcessVerticalSpace=true;
        layoutData.grabExcessHorizontalSpace=true;
        viewer.setInput("");
        return mainFrame;
    }

    @Override
    public void init(IWorkbench workbench) {
      formPropertyList();  

    }
    /**
     *
     */
    private void formPropertyList() {
        propertyLists.clear();
        String val = getPreferenceStore().getString(DataLoadPreferences.PROPERY_LISTS);
        boolean isName=true;
        RowWr wr=null;
        for (String str:val.split(DataLoadPreferences.CRS_DELIMETERS)){
            if (isName){
                wr=new RowWr(str,"");
                propertyLists.add(wr);
            }else{
                wr.setProperties(str);
            }
            isName=!isName;
        }
    }
    @Override
    protected void performDefaults() {
        super.performDefaults();
        getPreferenceStore().setToDefault(DataLoadPreferences.PROPERY_LISTS);
        formPropertyList();
        viewer.setInput("");        
    }
    @Override
    public boolean performOk() {
        StringBuilder result=new StringBuilder();
       for (RowWr wr:propertyLists){
           result.append(DataLoadPreferences.CRS_DELIMETERS);
               result.append(wr.getListName());
               result.append(DataLoadPreferences.CRS_DELIMETERS);
               result.append(wr.getProperties());
       }
       String string = result.length()>0?result.substring(DataLoadPreferences.CRS_DELIMETERS.length()):result.toString();
    getPreferenceStore().setValue(DataLoadPreferences.PROPERY_LISTS,string);
        return true;
        
    }

    /**
     *Create the table columns of the Neighbour types view.
     */
    private void createTableColumn() {
        Table table = viewer.getTable();
        TableViewerColumn column;
        TableColumn col;
        
        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("List name");
        col.setWidth(100);
        col.setResizable(true);

        column.setEditingSupport(new TableEditableSupport(viewer, true));
        
        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("Properties");
        col.setWidth(200);
        col.setResizable(true);

        column.setEditingSupport(new TableEditableSupport(viewer, false));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        viewer.refresh();
        
    }
    

    
    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Task
     * List, for example).
     */

    private class TableContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return propertyLists.toArray(new RowWr[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        
    }
    
    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            RowWr wrapper=(RowWr)element;
            return columnIndex==0?wrapper.getListName():wrapper.getProperties();
        }
        
    }
    
    public class TableEditableSupport extends EditingSupport {

        private final TextCellEditor editor;
        private final boolean isName;

        /**
         * @param viewer
         */
        public TableEditableSupport(TableViewer viewer, boolean isName) {
            super(viewer);
            this.isName = isName;
            editor = new TextCellEditor(viewer.getTable());
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return editor;
        }

        @Override
        protected Object getValue(Object element) {
            return isName?((RowWr)element).getListName():((RowWr)element).getProperties();
        }

        @Override
        protected void setValue(Object element, Object value) {
            if(isName){
                ((RowWr)element).setListName((String)value);                
            }else{
                ((RowWr)element).setProperties((String)value);
            }
            validate();
            getViewer().update(element, null);
        }        
        
    }
    
    public class RowWr{
        private String listName;
        private String properties;
        
        /**
         * @param listName
         * @param properties
         */
        public RowWr(String listName, String properties) {
            super();
            this.listName = listName;
            this.properties = properties;
        }
        /**
         * @return Returns the listName.
         */
        public String getListName() {
            return listName;
        }
        /**
         * @param listName The listName to set.
         */
        public void setListName(String listName) {
            this.listName = listName;
        }
        /**
         * @return Returns the properties.
         */
        public String getProperties() {
            return properties;
        }
        /**
         * @param properties The properties to set.
         */
        public void setProperties(String properties) {
            this.properties = properties;
        }
        
    }

    /**
     *
     */
    public void validate() {
        setValid(isPageValid());
    }

    /**
     *
     * @return
     */
    private boolean isPageValid() {
        return true;
    }
@Override
public IPreferenceStore getPreferenceStore() {
    return NeoLoaderPlugin.getDefault().getPreferenceStore();
}
}
