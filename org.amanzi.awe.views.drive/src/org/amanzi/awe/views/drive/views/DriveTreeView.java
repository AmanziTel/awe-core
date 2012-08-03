/**
 * 
 */
package org.amanzi.awe.views.drive.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.views.drive.views.view.provider.DriveTreeContentProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This View contains a tree of measurements found in the database.
 * 
 * @author Bondoronok_P
 */
public class DriveTreeView extends AbstractTreeView implements IAWEEventListenter {

    public static final String DRIVE_TREE_VIEW_ID = "org.amanzi.awe.views.drive.views.DriveTreeView";
    public static final String PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.PropertiesView";
    public static final String DRIVE_MODEL = "drive_model";

    private DriveTreeContentProvider contentProvider = new DriveTreeContentProvider();;

    /**
     * The Constructor
     */
    protected DriveTreeView() {
        this(AWEUIPlugin.getDefault().getGeneralNodeProperties());
    }

    protected DriveTreeView(IGeneralNodeProperties properties) {
        super(properties);
    }

    @Override
    protected void createControls(Composite parent) {
        setSearchField(new Text(parent, SWT.BORDER));
        setTreeViewer(new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL));
        setProviders();
        getTreeViewer().setInput(getSite());
        initializeListeners();
        setLayout(parent);

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

    /**
     * Initialize tree view listeners
     */
    private void initializeListeners() {
        getTreeViewer().addTreeListener(new ITreeViewerListener() {

            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                String expandedElement = event.getElement().toString();
                boolean isPrevious = Messages.PreviousElementsTitle.equals(expandedElement);
                if (Messages.NextElementsTitle.equals(expandedElement) || isPrevious) {
                    contentProvider.setPreviousPressed(isPrevious);
                    getTreeViewer().setInput(getSite());
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
            }
        });

        getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Iterator< ? > iterator = selection.iterator();
                List<IDataElement> renderableElements = new ArrayList<IDataElement>();
                Set<IDataElement> selectedMeasurements = new HashSet<IDataElement>();
                IDriveModel model = null;
                while (iterator.hasNext()) {
                    Object element = iterator.next();
                    if (element instanceof IDriveModel) {
                        model = (IDriveModel)element;
                    } else if (element instanceof IDataElement) {
                        IDataElement measurement = (IDataElement)element;
                        selectedMeasurements.add(measurement);
                        model = (IDriveModel)(measurement).get(DRIVE_MODEL);
                        if (model != null) {
                            IDataElement renderableLocation = model.getLocation(measurement);
                            renderableElements.add(renderableLocation);
                        }
                    }
                }

                if (model != null) {
                    // TODO BP : clear previously selected elements in another
                    // place (abstract model)
                    model.clearSelectedElements();
                    model.setSelectedDataElements(renderableElements);
                }
            }
        });
    }

    @Override
    protected IContentProvider getContentProvider() {
        return contentProvider;
    }

    @Override
    protected void addEventListeners() {
    }

    @Override
    public void onEvent(IEvent event) {
        // TODO Auto-generated method stub

    }

}
