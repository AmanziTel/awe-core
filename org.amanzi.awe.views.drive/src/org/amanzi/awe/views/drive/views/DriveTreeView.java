/**
 * 
 */
package org.amanzi.awe.views.drive.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.views.drive.views.view.provider.DriveTreeContentProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This View contains a tree of measurements found in the database.
 * 
 * @author Bondoronok_P
 */
public class DriveTreeView extends AbstractTreeView {

    public static final String DRIVE_TREE_VIEW_ID = "org.amanzi.awe.views.drive.views.DriveTreeView";
    public static final String PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.PropertiesView";
    public static final String DRIVE_MODEL = "drive_model";

    /**
     * The Constructor
     */
    public DriveTreeView() {
        this(new DriveTreeContentProvider());
    }

    protected DriveTreeView(DriveTreeContentProvider driveTreeContentProvider) {
        super(driveTreeContentProvider);
    }

    @Override
    public void createPartControl(Composite parent) {
        setSearchField(new Text(parent, SWT.BORDER));
        super.createPartControl(parent);
        initializeListeners();
    }

    /**
     * Initialize tree view listeners
     */
    // TODO KV: need refactoring
    private void initializeListeners() {
        getTreeViewer().addTreeListener(new ITreeViewerListener() {

            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                String expandedElement = event.getElement().toString();
                boolean isPrevious = Messages.PreviousElementsTitle.equals(expandedElement);
                if (Messages.NextElementsTitle.equals(expandedElement) || isPrevious) {
                    ((DriveTreeContentProvider)getContentProvider()).setPreviousPressed(isPrevious);
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

}
