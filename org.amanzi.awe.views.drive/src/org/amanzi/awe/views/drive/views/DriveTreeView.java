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
import org.amanzi.awe.views.network.view.NetworkMessages;
import org.amanzi.awe.views.network.view.PropertiesView;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.ui.providers.CommonViewLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This View contains a tree of measurements found in the database.
 * 
 * @author Bondoronok_P
 */
public class DriveTreeView extends ViewPart {

	public static final String DRIVE_TREE_VIEW_ID = "org.amanzi.awe.views.drive.views.DriveTreeView";
	public static final String PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.PropertiesView";
	public static final String DRIVE_MODEL = "drive_model";

	private TreeViewer tree;
	private Text tSearch;
	private EventManager eventManager;
	private DriveTreeContentProvider contentProvider;

	/**
	 * The Constructor
	 */
	public DriveTreeView() {
		eventManager = EventManager.getInstance();
		contentProvider = new DriveTreeContentProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		tSearch = new Text(parent, SWT.BORDER);
		tree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.VIRTUAL);
		tree.setContentProvider(contentProvider);
		tree.setLabelProvider(new CommonViewLabelProvider(tree));
		tree.setInput(getSite());
		initializeListeners();
		setLayout(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * @param parent
	 */
	private void setLayout(Composite parent) {
		FormLayout layout = new FormLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginWidth = 0;
		layout.spacing = 0;
		parent.setLayout(layout);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		tSearch.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(tSearch, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		tree.getTree().setLayoutData(formData);
	}

	/**
	 * Initialize tree view listeners
	 */
	@SuppressWarnings("unchecked")
	private void initializeListeners() {
		eventManager
				.addListener(EventsType.ANALYSE, new AnalyseEventListener());

		tree.addTreeListener(new ITreeViewerListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				String expandedElement = event.getElement().toString();
				boolean isPrevious = Messages.PreviousElementsTitle
						.equals(expandedElement);
				if (Messages.NextElementsTitle.equals(expandedElement)
						|| isPrevious) {
					contentProvider.setPreviousPressed(isPrevious);
					tree.setInput(getSite());
				}
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
			}
		});

		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				Iterator<?> iterator = selection.iterator();
				List<IDataElement> renderableElements = new ArrayList<IDataElement>();
				Set<IDataElement> selectedMeasurements = new HashSet<IDataElement>();
				IDriveModel model = null;
				while (iterator.hasNext()) {
					Object element = iterator.next();
					if (element instanceof IDriveModel) {
						model = (IDriveModel) element;
					} else if (element instanceof IDataElement) {
						IDataElement measurement = (IDataElement) element;
						selectedMeasurements.add(measurement);
						model = (IDriveModel) (measurement).get(DRIVE_MODEL);
						if (model != null) {
							IDataElement renderableLocation = model
									.getLocation(measurement);
							renderableElements.add(renderableLocation);
						}
					}
				}

				if (model != null) {
					// TODO BP : clear previously selected elements in another
					// place (abstract model)
					model.clearSelectedElements();
					model.setSelectedDataElements(renderableElements);
					eventManager.fireEvent(new ShowOnMapEvent(model, 170d,
							false));
					updatePropertiesView(selectedMeasurements, model);
				}
			}
		});
	}

	/**
	 * Load selected data elements to network properties view
	 * 
	 * @param isEditable
	 */
	private void updatePropertiesView(Set<IDataElement> elements,
			IDriveModel model) {
		try {
			PropertiesView propertiesView = (PropertiesView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(PROPERTIES_VIEW_ID);
			propertiesView.updateTableView(elements, model, true);
		} catch (PartInitException e) {
			MessageDialog.openError(null, NetworkMessages.ERROR_TITLE,
					NetworkMessages.NETWORK_PROPERTIES_OPEN_ERROR + e);
		}
	}

	/**
	 * Analyse event listener implementation
	 * 
	 * @author Bondoronok_P
	 */
	private class AnalyseEventListener implements IEventsListener<AnalyseEvent> {

		@Override
		public void handleEvent(AnalyseEvent data) {
			tree.expandToLevel(data.getSelectedModel(), 1);
			tree.setSelection(new StructuredSelection(data
					.getSelectedElements()));			
		}

		@Override
		public Object getSource() {
			return DRIVE_TREE_VIEW_ID;
		}
	}
}
