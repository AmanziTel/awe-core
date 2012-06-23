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

package org.amanzi.awe.views.neighbours.views;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class NodeToNodeRelationsView extends ViewPart {
	public static final String ID = "org.amanzi.awe.views.neighbours.views.NodeToNodeRelationsView";
	private Combo cbNetwork, cbN2NType, cbN2NName;
	private Button btnFilter;
	private TableViewer tableViewer;
	private Iterable<INetworkModel> networkList;
	private Iterable<INodeToNodeRelationsModel> nodeToNodeModels;
	private TableLabelProvider labelProvider;
	private TableContentProvider provider;
	private TableFilter servingFilter, neighbourFilter;
	private final EventManager eventManager;
	private static final double ZOOM_8X = 80d;
	private static final double ZOOM_4X = 40d;
	private static final double ZOOM_2X = 20d;

	@SuppressWarnings("unchecked")
	public NodeToNodeRelationsView() {
		super();
		eventManager = EventManager.getInstance();
		eventManager.addListener(EventsType.UPDATE_DATA,
				new UpdateDataHandling());
		eventManager.addListener(EventsType.ANALYSE, new AnalyseHandling());
	}

	public abstract class TableFilter extends ViewerFilter {

		private String searchString;

		private TableFilter() {
			super();
		}

		public String getSearchString() {
			return searchString;
		}

		public void setSearchText(String s) {
			this.searchString = ".*" + s + ".*";
		}
	}

	private void createLabel(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.FLAT);
		label.setText(labelText + ":");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	}

	private GridData createLayoutData(int minimumWidth) {
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.minimumWidth = minimumWidth;
		return layoutData;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite frame = new Composite(parent, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 0;
		formLayout.marginWidth = 0;
		formLayout.spacing = 0;
		frame.setLayout(formLayout);

		Composite child = new Composite(frame, SWT.FILL);
		final GridLayout layout = new GridLayout(6, false);
		child.setLayout(layout);
		createLabel(child, NodeToNodeRelationsViewMessages.DATA_TITLE);
		cbNetwork = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

		cbNetwork.setLayoutData(createLayoutData(100));

		createLabel(child, NodeToNodeRelationsViewMessages.N2N_TYPE_TITLE);
		cbN2NType = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbN2NType.setLayoutData(createLayoutData(100));

		// layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// layoutData.minimumWidth = 150;
		// cDrive.setLayoutData(layoutData);

		createLabel(child, NodeToNodeRelationsViewMessages.N2N_NAME_TITLE);
		cbN2NName = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbN2NName.setLayoutData(createLayoutData(100));

		addListeners();
		setNetworkItems();
		setN2NTypeItems();
		enableElements();

		tableViewer = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
		createFilters(child);
		FormData fData = new FormData();
		fData.left = new FormAttachment(0, 0);
		fData.right = new FormAttachment(100, 0);
		fData.top = new FormAttachment(child, 2);
		fData.bottom = new FormAttachment(100, -2);
		tableViewer.getControl().setLayoutData(fData);
		labelProvider = new TableLabelProvider();
		labelProvider.createTableColumn(new String[] {});
		provider = new TableContentProvider();
		tableViewer.setContentProvider(provider);
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				NodeToNodeRelationsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);

	}

	protected void fillContextMenu(IMenuManager manager) {
		ShowOnMapAction showOnMap8X = new ShowOnMapAction(
				(IStructuredSelection) tableViewer.getSelection(),
				NodeToNodeRelationsViewMessages.SHOW_ON_MAP_8_X, ZOOM_8X);
		ShowOnMapAction showOnMap4X = new ShowOnMapAction(
				(IStructuredSelection) tableViewer.getSelection(),
				NodeToNodeRelationsViewMessages.SHOW_ON_MAP_4_X, ZOOM_4X);
		ShowOnMapAction showOnMap2X = new ShowOnMapAction(
				(IStructuredSelection) tableViewer.getSelection(),
				NodeToNodeRelationsViewMessages.SHOW_ON_MAP_2_X, ZOOM_2X);
		if (showOnMap8X.isEnabled()) {
			manager.add(showOnMap8X);
		}
		if (showOnMap4X.isEnabled()) {
			manager.add(showOnMap4X);
		}
		if (showOnMap2X.isEnabled()) {
			manager.add(showOnMap2X);
		}
	}

	private class ShowOnMapAction extends Action {
		private RowWrapper row = null;
		private boolean enabled;
		private String menuElementTitle;
		private double zoom;

		@Override
		public String getText() {
			return menuElementTitle;
		}

		public ShowOnMapAction(IStructuredSelection selection,
				String tableItem, double zoom) {
			this.menuElementTitle = tableItem;
			this.zoom = zoom;
			if (selection.getFirstElement() instanceof RowWrapper) {
				enabled = true;
			}
			if (enabled) {
				row = (RowWrapper) selection.getFirstElement();
			}
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public void run() {
			eventManager.fireEvent(new ShowOnMapEvent(getNetworkModel(), zoom));
		}

		private NetworkModel getNetworkModel() {
			NetworkModel resultModel = (NetworkModel) getSelectedNetwork();
			resultModel.clearSelectedElements();
			resultModel.setSelectedDataElementToList(row.getServingElement());
			return resultModel;
		}
	}

	private class TableLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
		private static final int DEF_SIZE = 120;
		private static final int PROP_DEF_SIZE = 80;
		private final String[] colNames = new String[] {
				NodeToNodeRelationsViewMessages.SERVING_COLUMN,
				NodeToNodeRelationsViewMessages.NEIGHBOR_COLUMN };

		private void createColumn(String label, int size, boolean sortable,
				final int idx) {
			TableViewerColumn column = new TableViewerColumn(tableViewer,
					SWT.LEFT);
			TableColumn col = column.getColumn();
			col.setText(label);
			columns.add(col);
			col.setWidth(size);
			col.setResizable(true);
			if (sortable) {
				TableColumnSorter cSorter = new TableColumnSorter(tableViewer,
						col) {
					protected int doCompare(Viewer v, Object e1, Object e2) {
						ITableLabelProvider lp = ((ITableLabelProvider) tableViewer
								.getLabelProvider());
						String t1 = lp.getColumnText(e1, idx);
						String t2 = lp.getColumnText(e2, idx);
						return t1.compareTo(t2);
					}
				};
				cSorter.setSorter(cSorter, TableColumnSorter.ASC);
			}
		}

		/**
		 * create column table
		 */
		public void createTableColumn(String[] properties) {
			Table tabl = tableViewer.getTable();
			for (TableColumn column : columns) {
				column.dispose();
			}
			int idx = 0;
			for (String colName : colNames) {
				createColumn(colName, DEF_SIZE, true, idx);
				idx++;
			}
			for (String prop : properties) {
				createColumn(prop, PROP_DEF_SIZE, false, idx);
				idx++;
			}
			tabl.setHeaderVisible(true);
			tabl.setLinesVisible(true);
			tableViewer.setLabelProvider(this);
			tableViewer.refresh();
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			RowWrapper wrapper = (RowWrapper) element;
			if (columnIndex == 0) {
				return wrapper.getServingName();
			} else if (columnIndex == 1) {
				return wrapper.getNeighbourName();
			} else {
				return wrapper.getPropValue(columnIndex - 2);
			}
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	private static abstract class TableColumnSorter extends ViewerComparator {
		public static final int ASC = 1;

		public static final int NONE = 0;

		public static final int DESC = -1;

		private int direction = 0;

		private TableColumn column;

		private TableViewer viewer;

		public TableColumnSorter(TableViewer viewer, TableColumn column) {
			this.column = column;
			this.viewer = viewer;
			this.column.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					if (TableColumnSorter.this.viewer.getComparator() != null) {
						if (TableColumnSorter.this.viewer.getComparator() == TableColumnSorter.this) {
							int tdirection = TableColumnSorter.this.direction;

							if (tdirection == ASC) {
								setSorter(TableColumnSorter.this, DESC);
							} else if (tdirection == DESC) {
								setSorter(TableColumnSorter.this, NONE);
							}
						} else {
							setSorter(TableColumnSorter.this, ASC);
						}
					} else {
						setSorter(TableColumnSorter.this, ASC);
					}
				}
			});
		}

		public void setSorter(TableColumnSorter sorter, int direction) {
			if (direction == NONE) {
				column.getParent().setSortColumn(null);
				column.getParent().setSortDirection(SWT.NONE);
				viewer.setComparator(null);
			} else {
				column.getParent().setSortColumn(column);
				sorter.direction = direction;

				if (direction == ASC) {
					column.getParent().setSortDirection(SWT.DOWN);
				} else {
					column.getParent().setSortDirection(SWT.UP);
				}

				if (viewer.getComparator() == sorter) {
					viewer.refresh();
				} else {
					viewer.setComparator(sorter);
				}

			}
		}

		public int compare(Viewer viewer, Object e1, Object e2) {
			return direction * doCompare(viewer, e1, e2);
		}

		protected abstract int doCompare(Viewer TableViewer, Object e1,
				Object e2);
	}

	private void enableElements() {
		cbN2NType.setEnabled(cbNetwork.getSelectionIndex() >= 0);
		cbN2NName.setEnabled(cbNetwork.getSelectionIndex() >= 0
				&& cbN2NType.getSelectionIndex() >= 0);
	}

	private class TableContentProvider implements IStructuredContentProvider {
		List<RowWrapper> elements = new ArrayList<RowWrapper>();

		public TableContentProvider() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return elements.toArray(new RowWrapper[0]);
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			elements.clear();
			if (newInput == null) {
				return;
			}
			final VirtualTable virtualTable = new VirtualTable();
			elements = virtualTable.getElements();
		}
	}

	/**
	 * Virtual Table implementation
	 * 
	 * @author Bondoronok_p
	 */
	private class VirtualTable {

		private static final int NUMBER_OF_LOADABLE_ITEMS = 8;
		private static final int FIRST_TABLE_ITEM = 0;

		private ListIterator<IDataElement> sectorsIterator;
		private List<RowWrapper> elements;
		private INodeToNodeRelationsModel n2nModel;
		private String[] properties;

		/**
		 * Initialize elements list
		 */
		@SuppressWarnings("unchecked")
		public VirtualTable() {
			elements = new ArrayList<RowWrapper>();
			n2nModel = getSelectedN2NModel();
			properties = n2nModel.getAllPropertyNames(n2nModel.getType());
			((TableLabelProvider) tableViewer.getLabelProvider())
					.createTableColumn(properties);
			sectorsIterator = new ListIteratorWrapper(n2nModel
					.getAllElementsByType(NetworkElementNodeType.SECTOR)
					.iterator());
			loadElements(Boolean.TRUE);
			scrollingListeners();
		}

		/**
		 * Get the full list of elements
		 * 
		 * @return ArrayList
		 */
		public List<RowWrapper> getElements() {
			return elements;
		}

		/**
		 * Listeners
		 */
		private void scrollingListeners() {
			Control tableViewerControl = tableViewer.getControl();

			ScrollBar scrollBar = tableViewer.getTable().getVerticalBar();
			scrollBar.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (e.detail == SWT.DRAG) {
						scrollBarSelection();
					}
				}
			});

			tableViewerControl.addMouseWheelListener(new MouseWheelListener() {
				@Override
				public void mouseScrolled(MouseEvent e) {
					scrollBarSelection();
				}
			});

			tableViewerControl.addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					Table table = tableViewer.getTable();
					if (e.keyCode == (SWT.ARROW_DOWN)
							|| e.keyCode == (SWT.PAGE_DOWN)) {
						if (table.getSelectionIndex() + 1 == table
								.getItemCount()) {
							loadElements(Boolean.TRUE);
						}
					} else if (e.keyCode == (SWT.ARROW_UP)
							|| e.keyCode == (SWT.PAGE_UP)) {
						if (table.getSelectionIndex() == FIRST_TABLE_ITEM) {
							loadElements(Boolean.FALSE);
						}
					} else if (e.keyCode == (SWT.HOME)) {
						while (sectorsIterator.hasPrevious()) {
							loadElements(Boolean.FALSE);
						}
					} else if (e.keyCode == SWT.END) {
						while (sectorsIterator.hasNext()) {
							loadElements(Boolean.TRUE);
						}
					}
					tableViewer.refresh();
				}
			});
		}

		/**
		 * Loading items by the selection of scroll bar
		 */
		private void scrollBarSelection() {
			ScrollBar scrollBar = tableViewer.getTable().getVerticalBar();
			if ((scrollBar.getSelection() + 5) >= tableViewer.getTable()
					.getItemCount()) {
				loadElements(Boolean.TRUE);
			} else if (scrollBar.getSelection() == FIRST_TABLE_ITEM) {
				loadElements(Boolean.FALSE);
			}
			tableViewer.refresh();
			// if (elements.size() != 0) {
			// tableViewer.getTable().getVerticalBar().setMinimum(20);
			// }
		}

		/**
		 * Loading node to node related elements to elements list
		 */
		private void loadElements(boolean scrollDown) {
			List<RowWrapper> loadedElements = new ArrayList<RowWrapper>(0);
			for (int i = 0; i < NUMBER_OF_LOADABLE_ITEMS; i++) {
				IDataElement source = getSourceElement(scrollDown);
				if (source != null) {
					Iterable<IDataElement> relations = n2nModel
							.getN2NRelatedElements(source);
					for (IDataElement element : relations) {
						// TODO: LN: do not use Relations!!!
						Relationship relation = ((DataElement) element)
								.getRelationship();
						String startElementPropetyName = (String) relation
								.getStartNode().getProperty(
										NetworkService.SOURCE_NAME, null);
						String endElementPropertyName = (String) relation
								.getEndNode().getProperty(
										NetworkService.SOURCE_NAME, null);
						RowWrapper row = new RowWrapper(
								startElementPropetyName, endElementPropertyName);
						for (int q = 0; q < properties.length; q++) {
							row.addPropValue(relation.getProperty(
									properties[q], null));
						}
						row.setServingElement(source);
						loadedElements.add(row);
					}
				} else {
					break;
				}
			}

			if (!loadedElements.isEmpty()) {
				elements.clear();
				elements.addAll(loadedElements);
			}
		}

		/**
		 * Get next or previous element from sectorsIterator
		 * 
		 * @param scrollDown
		 *            true or false
		 * @return IDataElement or null
		 */
		private IDataElement getSourceElement(boolean scrollDown) {
			IDataElement source = null;
			if (scrollDown) {
				source = sectorsIterator.hasNext() ? sectorsIterator.next()
						: source;
			} else {
				source = sectorsIterator.hasPrevious() ? sectorsIterator
						.previous() : source;
			}
			return source;
		}
	}

	/**
	 * <p>
	 * Wrapper of one row of table
	 * </p>
	 * 
	 * @author kostyukovich_n
	 * @since 1.0.0
	 */
	private class RowWrapper {
		private String servingName;
		private String neighbourName;
		private List<String> propValues;
		private IDataElement servingElement;

		private RowWrapper(String servingName, String neighbourName) {
			super();
			this.servingName = servingName;
			this.neighbourName = neighbourName;
			propValues = new ArrayList<String>();
		}

		public String getServingName() {
			return servingName;
		}

		public String getNeighbourName() {
			return neighbourName;
		}

		public void addPropValue(Object value) {
			propValues.add(value.toString());
		}

		public String getPropValue(int index) {
			if (propValues.size() <= index)
				return null;
			return propValues.get(index);
		}

		public void setServingElement(IDataElement source) {
			this.servingElement = source;
		}

		public IDataElement getServingElement() {
			return servingElement;
		}

	}

	private void createFilters(Composite composite) {
		createLabel(composite, NodeToNodeRelationsViewMessages.SERVING_FILTER);
		final Text servingText = new Text(composite, SWT.BORDER | SWT.SEARCH);
		servingText.setLayoutData(createLayoutData(70));
		servingFilter = new TableFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (getSearchString() == null
						|| getSearchString().length() == 0) {
					return true;
				}
				RowWrapper p = (RowWrapper) element;
				return p.getServingName().matches(getSearchString());
			}
		};

		createLabel(composite, NodeToNodeRelationsViewMessages.NEIGHBOR_FILTER);
		final Text neighbourText = new Text(composite, SWT.BORDER | SWT.SEARCH);
		neighbourText.setLayoutData(createLayoutData(70));
		neighbourFilter = new TableFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (getSearchString() == null
						|| getSearchString().length() == 0) {
					return true;
				}
				RowWrapper p = (RowWrapper) element;
				return p.getNeighbourName().matches(getSearchString());
			}
		};
		btnFilter = new Button(composite, SWT.PUSH);
		btnFilter.setText(NodeToNodeRelationsViewMessages.FILTER_TITLE);
		btnFilter.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				servingFilter.setSearchText(servingText.getText());
				neighbourFilter.setSearchText(neighbourText.getText());
				tableViewer.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		tableViewer.addFilter(servingFilter);
		tableViewer.addFilter(neighbourFilter);
	}

	private void addListeners() {
		cbNetwork.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setN2NModelsItems(getSelectedNetwork());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		SelectionListener selListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				enableElements();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		cbN2NName.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setInput("");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		cbNetwork.addSelectionListener(selListener);
		cbN2NType.addSelectionListener(selListener);
		cbN2NName.addSelectionListener(selListener);
	}

	/**
	 * handle analyse event to show n2n of recieved model
	 * 
	 * @return
	 */
	private class AnalyseHandling implements IEventsListener<AnalyseEvent> {
		@Override
		public void handleEvent(AnalyseEvent data) {
			setNetworkItems();
			try {
				INodeToNodeRelationsModel n2nModel = (INodeToNodeRelationsModel) data
						.getSelectedModel();
				selecModel(n2nModel.getParentModel(), cbNetwork);
				cbNetwork.setEnabled(true);
				setN2NTypeItems();
				int i = 0;
				for (String type : cbN2NType.getItems()) {
					if (n2nModel.getNodeToNodeRelationsType().name()
							.equalsIgnoreCase(type)) {
						cbN2NType.select(i);
						break;
					}
					i++;
				}
				cbN2NType.setEnabled(true);
				setN2NModelsItems(getSelectedNetwork());
				selecModel(n2nModel, cbN2NName);
				cbN2NName.setEnabled(true);
				tableViewer.setInput("");
			} catch (AWEException e) {
				return;
			}
		}

		@Override
		public Object getSource() {
			return ID;
		}
	}

	/**
	 * select model in combobox
	 */
	private void selecModel(IModel model, Combo combo) {
		String modelName = model.getName();
		int i = 0;
		for (String name : combo.getItems()) {
			if (name.equals(modelName)) {
				combo.select(i);
			}
			i++;
		}
	}

	/**
	 * <p>
	 * describe listener to refresh comboboxes
	 * </p>
	 * 
	 * @author Kondratenko_Vladislav
	 * @since 1.0.0
	 */
	private class UpdateDataHandling implements
			IEventsListener<UpdateDataEvent> {
		@Override
		public void handleEvent(UpdateDataEvent data) {
			int selectedNetworkIteam = cbNetwork.getSelectionIndex();
			int selectedN2NType = cbN2NType.getSelectionIndex();
			int selectedN2NName = cbN2NName.getSelectionIndex();
			setNetworkItems();
			setN2NTypeItems();
			cbNetwork.select(selectedNetworkIteam);
			cbN2NType.select(selectedN2NType);
			setN2NModelsItems(getSelectedNetwork());
			cbN2NName.select(selectedN2NName);
			tableViewer.refresh();

		}

		@Override
		public Object getSource() {
			return null;
		}
	}

	private INetworkModel getSelectedNetwork() {
		int networkIndex = cbNetwork.getSelectionIndex();
		if (networkIndex >= 0) {
			String network = cbNetwork.getItem(networkIndex);
			for (INetworkModel model : networkList) {
				if (model.getName().equals(network)) {
					return model;
				}
			}
		}
		return null;
	}

	private INodeToNodeRelationsModel getSelectedN2NModel() {
		int modelIndex = cbN2NName.getSelectionIndex();
		if (modelIndex >= 0) {
			String n2nModel = cbN2NName.getItem(modelIndex);
			for (INodeToNodeRelationsModel model : nodeToNodeModels) {
				if (model.getName().equals(n2nModel)) {
					try {
						getSelectedNetwork()
								.setCurrentNodeToNodeRelationshipModel(model);
						return model;
					} catch (AWEException e) {
						MessageDialog
								.openError(
										null,
										NodeToNodeRelationsViewMessages.ERROR_TITLE,
										NodeToNodeRelationsViewMessages.CANNOT_SET_N2N_MODEL
												+ "\n" + e);
					}
				}
			}
		}
		return null;
	}

	private N2NRelTypes getSelectedN2NType() {
		int typeIndex = cbN2NType.getSelectionIndex();
		if (typeIndex >= 0) {
			String n2nType = cbN2NType.getItem(typeIndex);
			for (N2NRelTypes type : N2NRelTypes.values()) {
				if (type.getId().equals(n2nType)) {
					return type;
				}
			}
		}
		return null;
	}

	private void setN2NModelsItems(INetworkModel network) {
		if (network == null) {
			cbN2NName.setItems(new String[] {});
			nodeToNodeModels = null;
		} else {
			try {
				nodeToNodeModels = network
						.getNodeToNodeModels(getSelectedN2NType());
				List<String> models = new ArrayList<String>();
				for (INodeToNodeRelationsModel model : nodeToNodeModels) {
					models.add(model.getName());
				}
				cbN2NName.setItems(models.toArray(new String[] {}));
			} catch (AWEException e) {
				throw (RuntimeException) new RuntimeException().initCause(e);
			}
		}
	}

	private void setN2NTypeItems() {
		String[] types = new String[N2NRelTypes.values().length];
		for (int i = 0; i < N2NRelTypes.values().length; i++) {
			types[i] = N2NRelTypes.values()[i].getId();
		}
		cbN2NType.setItems(types);
	}

	private void setNetworkItems() {
		try {
			networkList = ProjectModel.getCurrentProjectModel()
					.findAllNetworkModels();
			List<String> networks = new ArrayList<String>();
			for (INetworkModel network : networkList) {
				networks.add(network.getName());
			}
			cbNetwork.setItems(networks.toArray(new String[] {}));
		} catch (AWEException e) {
			throw (RuntimeException) new RuntimeException().initCause(e);
		}
	}

	@Override
	public void setFocus() {
	}
}
