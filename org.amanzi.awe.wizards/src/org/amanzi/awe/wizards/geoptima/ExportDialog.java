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

package org.amanzi.awe.wizards.geoptima;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoTreeContentProvider;
import org.amanzi.neo.core.utils.NeoTreeElement;
import org.amanzi.neo.core.utils.NeoTreeLabelProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ExportDialog extends Dialog implements IPropertyChangeListener {

    private int status;
    private Shell shell;
    private CheckboxTreeViewer viewer;
    private String property;
    private GraphDatabaseService service;

    /**
     * @param parent
     */
    public ExportDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (property != getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA)) {
            formInput();
        }
    }


    private void formInput() {
        service = NeoServiceProvider.getProvider().getService();
        property = getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA);
        viewer.setInput(property);
        viewer.setAllChecked(false);
        validateExportButton();
    }

    /**
     *
     */
    private void validateExportButton() {
        // TODo implement
    }

    /**
     * Returs preference store
     * 
     * @return IPreferenceStore
     */
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
    public int open() {
        Shell parentShell = getParent();
        Shell shell = new Shell(parentShell, getStyle());
        shell.setText("Export");

        createContents(shell);
        shell.pack();

        // calculate location
        Point size = parentShell.getSize();
        int dlgWidth = shell.getSize().x;
        int dlgHeight = shell.getSize().y;
        shell.setLocation((size.x - dlgWidth) / 2, (size.y - dlgHeight) / 2);
        NeoLoaderPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(this);
        beforeOpen();
        shell.open();
        // wait
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        NeoLoaderPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(this);
        return status;
    }

    /**
     *
     */
    private void beforeOpen() {
        formInput();
    }

    /**
     * @param shell
     */
    private void createContents(final Shell shell) {
        this.shell = shell;
        shell.setLayout(new GridLayout(2, true));
        Label label = new Label(shell, SWT.NONE);
        label.setText("Select necessary data and properties for export:");
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        label.setLayoutData(layoutData);
        viewer = new CheckboxTreeViewer(shell);
        viewer.setLabelProvider(new NeoTreeLabelProvider());
        viewer.setContentProvider(new TreeContentProvider());
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
        layoutData.widthHint = 300;
        layoutData.heightHint = 400;
        viewer.getControl().setLayoutData(layoutData);
        Button bCorrelate = new Button(shell, SWT.PUSH);
        bCorrelate.setText("Export");
        bCorrelate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                export();
            }

        });

        Button btnOk = new Button(shell, SWT.PUSH);
        btnOk.setText("OK");
        GridData gdBtnOk = new GridData();
        gdBtnOk.horizontalAlignment = GridData.END;
        gdBtnOk.widthHint = 70;
        btnOk.setLayoutData(gdBtnOk);
        btnOk.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                shell.close();
            }

        });
        viewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                // TODO implement
                validateExportButton();
            }
        });

    }

    /**
     *
     */
    protected void export() {
    }

    private class TreeContentProvider extends NeoTreeContentProvider {
        LinkedHashSet<TreeElem> elements = new LinkedHashSet<TreeElem>();
        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new TreeElem[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                elements.clear();
            } else {
                Transaction tx = service.beginTx();
                try {
                    String nodeSet = (String)newInput;
                    StringTokenizer st = new StringTokenizer(nodeSet, DataLoadPreferences.CRS_DELIMETERS);
                    while (st.hasMoreTokens()) {
                        String nodeId = st.nextToken();
                        Node node = service.getNodeById(Long.parseLong(nodeId));
                        elements.add(new TreeElem(ElemType.ROOT, null, node, null, service));
                    }
                } finally {
                    tx.finish();
                }
            }
        }

    }
    private static class TreeElem extends NeoTreeElement{

        private final TreeElem parent;
        private final ElemType elemType;


        public TreeElem(ElemType elemType,String name,Node node,TreeElem parent, GraphDatabaseService service) {
            super(node, service);
            this.elemType = elemType;
            this.parent = parent;
            switch (elemType) {
            case PROPERTY:
                setText(name);
                break;
            case CORRELATE:
                setText("Correlated sets");
                break;
            case SITE:
                setText("Sites properties");
                break;
            case SECTOR:
                setText("Sector properties");
                break;
            default:
                break;
            }
        }

        @Override
        public NeoTreeElement[] getChildren() {
            //TODO handle correlate
            Transaction tx;
            switch (elemType) {
            case ROOT:
                tx=service.beginTx();
                try{
                    ArrayList<TreeElem>networkElements=new ArrayList<TreeElem>();
                    if (NodeTypes.NETWORK.checkNode(node)){
                        Relationship sitePropertyRel = node.getSingleRelationship(GeoNeoRelationshipTypes.VIRTUAL_DATASET,Direction.OUTGOING);
                        TreeElem siteNode=sitePropertyRel==null?null:new TreeElem(ElemType.SITE,"site",sitePropertyRel.getOtherNode(node) , this, service) ;
                        if (siteNode!=null){
                            networkElements.add(siteNode);
                        }
                        networkElements.add(new TreeElem(ElemType.SECTOR, null, node, this, service));
                        networkElements.add(new TreeElem(ElemType.CORRELATE, null, node, this, service));
                        return networkElements.toArray(new TreeElem[0]);
                    }
                }finally{
                    tx.finish();
                }
                //handle in next case - not use break!
            case SITE:
            case SECTOR:
            case SET:
                tx=service.beginTx();
                try{
                    ArrayList<TreeElem>networkElements=new ArrayList<TreeElem>();
                    String[] allProperties = new PropertyHeader(node).getAllFields();
                    if (allProperties!=null){
                        for (String string : allProperties) {
                            networkElements.add(new TreeElem(ElemType.PROPERTY, string, node, this, service));
                        }
                    }
                    return networkElements.toArray(new TreeElem[0]);  
                }finally{
                    tx.finish();
                }
            case CORRELATE:
                ArrayList<TreeElem>networkElements=new ArrayList<TreeElem>();
                tx=service.beginTx();
                try{
                    for (Node data:NeoUtils.getAllCorrelatedDatasets(node, service)){
                        networkElements.add(new TreeElem(ElemType.SET, null, data, this, service));  
                    }
                    return networkElements.toArray(new TreeElem[0]);
                }finally{
                    tx.finish();
                }              
            default:
                return new TreeElem[0];
            }
            
        }

        @Override
        public NeoTreeElement getParent() {
            return parent;
        }

        @Override
        public boolean hasChildren() {
            Transaction tx;
            switch (elemType) {
            case PROPERTY:
                return false;
            case ROOT:
                tx=service.beginTx();
                try{
                   return NodeTypes.NETWORK.checkNode(node)||new PropertyHeader(node).isHavePropertyNode(); 
                }finally{
                    tx.finish();
                }
            case SITE:
            case SECTOR:
            case SET:
                tx=service.beginTx();
                try{
                   return new PropertyHeader(node).isHavePropertyNode();
                }finally{
                    tx.finish();
                }
            case CORRELATE:
                tx=service.beginTx();
                try{
                   return NeoUtils.getAllCorrelatedDatasets(node, service).iterator().hasNext();
                }finally{
                    tx.finish();
                }
            default:
                return false;
            }
            
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((elemType == null) ? 0 : elemType.hashCode());
            result = prime * result + ((parent == null) ? 0 : parent.hashCode());
            result = prime * result + ((node == null) ? 0 : node.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            TreeElem other = (TreeElem)obj;
            if (elemType == null) {
                if (other.elemType != null)
                    return false;
            } else if (!elemType.equals(other.elemType))
                return false;
            if (parent == null) {
                if (other.parent != null)
                    return false;
            } else if (!parent.equals(other.parent))
                return false;
            if (node == null) {
                if (other.node != null)
                    return false;
            } else if (!node.equals(other.node))
                return false;
            return true;
        }
        
    }
    private static enum ElemType{
        SITE,SECTOR,CORRELATE,PROPERTY,ROOT,SET;
    }
}
