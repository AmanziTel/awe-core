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

package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 * Network properties view
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class PropertiesView extends ViewPart {

	public static final String PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.PropertiesView";
	public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NewNetworkTreeView";
	public static final String DRIVE_TREE_VIEW_ID = "org.amanzi.awe.views.drive.views.DriveTreeView";

	/*
	 * table
	 */
	private TableViewer tableViewer;

	private CheckboxTableViewer propertyListTable;

	/*
	 * table providers
	 */
	private MultiplyTableLabelProvider labelProvider;
	private MultiplyTableContentProvider provider;

	/*
	 * set for selection elements TODO BP: try change to Iterable<IDataElement>
	 */
	private Set<IDataElement> currentDataElements;

	// all properties
	private static List<String> allProperties;

	// selected properties
	private static List<String> headers;

	// structure
	private static Map<String, Map<String, String>> parents;

	private static boolean updateTable = false;

	private static List<RowWrapper> elements = new ArrayList<RowWrapper>();

	private static String CELL_MODIFIER_1 = "column1";
	private static String CELL_MODIFIER_2 = "column2";

	private boolean notInterruptEvent = Boolean.TRUE;
	private boolean updateMeasurementsProperties = false;
	private boolean dataFromTreeView = false;

	public static boolean showMessageBox = true;
	private IDataModel currentModel;
	private EventManager eventManager;

	/**
	 * The Constructor
	 */
	public PropertiesView() {
		super();
		eventManager = EventManager.getInstance();
	}

	public void updateTableView(Set<IDataElement> dataElements,
			boolean isEditable) {
		notInterruptEvent = Boolean.FALSE;
		this.currentDataElements = dataElements;
		this.updateMeasurementsProperties = false;
		this.dataFromTreeView = false;
		tableViewer.setInput(StringUtils.EMPTY);
		tableViewer.refresh();
	}

	public void updateTableView(Set<IDataElement> dataElements, IDataModel model) {
		notInterruptEvent = Boolean.FALSE;
		this.updateMeasurementsProperties = false;
		this.dataFromTreeView = false;
		this.currentDataElements = dataElements;
		this.currentModel = model;
		tableViewer.setInput(StringUtils.EMPTY);
		tableViewer.refresh();
	}

	public void updateTableView(Set<IDataElement> dataElements,
			IDataModel model, boolean dataFromTreeView) {
		notInterruptEvent = Boolean.FALSE;
		this.updateMeasurementsProperties = false;
		this.dataFromTreeView = dataFromTreeView;
		this.currentDataElements = dataElements;
		this.currentModel = model;
		tableViewer.setInput(StringUtils.EMPTY);
		tableViewer.refresh();
	}

	public void updateTableView() {
		notInterruptEvent = Boolean.FALSE;
		this.updateMeasurementsProperties = false;
		tableViewer.setInput(StringUtils.EMPTY);
		tableViewer.refresh();
	}

	private void updateTableViewForMeasurements(List<RowWrapper> elements,
			List<String> headers, IDataModel currentModel,
			boolean dataFromTreeView) {
		this.updateMeasurementsProperties = true;
		this.dataFromTreeView = dataFromTreeView;
		PropertiesView.elements = elements;
		PropertiesView.headers = headers;
		this.currentModel = currentModel;
		tableViewer.setInput(StringUtils.EMPTY);
		tableViewer.refresh();
	}

	private void createLabel(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.FLAT);
		label.setText(labelText + ":");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	}

	@Override
	public void createPartControl(Composite mainParent) {
		Composite parent = new Composite(mainParent, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 0;
		formLayout.marginWidth = 0;
		formLayout.spacing = 0;
		parent.setLayout(formLayout);

		Composite child = new Composite(parent, SWT.FILL);
		final GridLayout layout = new GridLayout(6, false);
		child.setLayout(layout);
		createLabel(child, NetworkMessages.DESTINATION_OF_THIS_VIEW);

		updateTable = false;

		createViewer(parent, child);
	}

	private void createViewer(Composite parent, Composite child) {
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);

		FormData fData = new FormData();
		fData.left = new FormAttachment(0, 0);
		fData.right = new FormAttachment(100, 0);
		fData.top = new FormAttachment(child, 2);
		fData.bottom = new FormAttachment(100, -2);
		tableViewer.getControl().setLayoutData(fData);

		labelProvider = new MultiplyTableLabelProvider();
		labelProvider.createTableColumn();
		tableViewer.setLabelProvider(labelProvider);

		provider = new MultiplyTableContentProvider();
		tableViewer.setContentProvider(provider);

		tableViewer.setColumnProperties(new String[] { CELL_MODIFIER_1,
				CELL_MODIFIER_2 });

		ColumnViewerEditorActivationStrategy activationStrategy = new ColumnViewerEditorActivationStrategy(
				tableViewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		tableViewer.setCellEditors(new CellEditor[] {
				new TextCellEditor(tableViewer.getTable()),
				new TextCellEditor(tableViewer.getTable()) });

		TableViewerEditor.create(tableViewer, activationStrategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tableViewer.setInput(StringUtils.EMPTY);

		tableViewer.getTable().addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent e) {
			}

			@Override
			public void mouseExit(MouseEvent e) {
			}

			@Override
			public void mouseEnter(MouseEvent e) {
				notInterruptEvent = Boolean.TRUE;
			}
		});

		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if (notInterruptEvent) {
							IStructuredSelection selection = ((IStructuredSelection) event
									.getSelection());
							RowWrapper wrappedElement = (RowWrapper) selection
									.getFirstElement();
							if (wrappedElement != null) {
								IDataElement element = wrappedElement
										.getElement();
								if (currentModel instanceof IRenderableModel) {
									IRenderableModel model = (IRenderableModel) currentModel;
									model.clearSelectedElements();
									if (currentModel instanceof INetworkModel) {
										model.setSelectedDataElementToList(element);
										eventManager
												.fireEvent(new ShowOnMapEvent(
														model, true));
										if (model instanceof INetworkModel) {
											eventManager
													.fireEvent(new AnalyseEvent(
															model,
															model.getSelectedElements(),
															NETWORK_TREE_VIEW_ID));
										}
									} else if (currentModel instanceof IDriveModel) {
										// TODO BP: Refactor
										IDriveModel driveModel = (IDriveModel) currentModel;
										IDataElement location = driveModel
												.getLocation(element);

										driveModel.clearSelectedElements();
										driveModel
												.setSelectedDataElementToList(location);
										eventManager
												.fireEvent(new ShowOnMapEvent(
														driveModel, 180d, true));

										driveModel.clearSelectedElements();
										driveModel
												.setSelectedDataElementToList(element);
										eventManager.fireEvent(new AnalyseEvent(
												driveModel, driveModel
														.getSelectedElements(),
												DRIVE_TREE_VIEW_ID));
									}
								}
							}
						}
					}
				});
	}

	/**
	 * TODO Purpose of NetworkPropertiesView
	 * <p>
	 * Label provider for multiply table
	 * </p>
	 * 
	 * @author Ladornaya_A
	 * @since 1.0.0
	 */
	private class MultiplyTableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
		private final static int DEF_SIZE = 110;
		private static final String MEASUREMENT_LOADING_JOB_HEADER = "Measurements loading";
		private static final String DRIVE_MODEL = "drive_model";
		private boolean level = true;

		private void createColumn(String label, int size, boolean sortable,
				final int idx) {
			TableViewerColumn column = new TableViewerColumn(tableViewer,
					SWT.LEFT);
			TableColumn col = column.getColumn();
			col.setText(label);
			columns.add(col);
			col.setWidth(DEF_SIZE);
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
		 * Load data elements and create table columns
		 */
		public void createTableColumn() {
			if (!updateMeasurementsProperties) {
				level = true;
				allProperties = new ArrayList<String>();
				parents = new HashMap<String, Map<String, String>>();
				for (TableColumn column : columns) {
					column.dispose();
				}

				if (currentModel instanceof INetworkModel) {
					loadNetworkElements();
				} else if (currentModel instanceof IDriveModel) {
					loadDriveElements();
				}
			}
			Table tabl = tableViewer.getTable();

			tabl.setHeaderVisible(true);
			tabl.setLinesVisible(true);
			tableViewer.setLabelProvider(this);
			tableViewer.refresh();

		}

		/**
		 * Load sites and sectors
		 */
		private void loadNetworkElements() {
			int idx = 0;

			if (currentDataElements != null) {
				allProperties.add(AbstractService.NAME);
				createColumn(AbstractService.NAME, DEF_SIZE, true, idx);
				idx++;
				String type = null;

				for (IDataElement element : currentDataElements) {

					if (type == null) {
						type = element.get(AbstractService.TYPE).toString();
					} else {
						if (!type.equals(element.get(AbstractService.TYPE)
								.toString())) {
							level = false;
						}
					}
					INetworkModel networkModel = (INetworkModel) currentModel;
					boolean exist = true;
					Map<String, String> structureMap = new HashMap<String, String>();
					IDataElement child = element;
					while (exist != false && child != null) {
						IDataElement parent = networkModel
								.getParentElement(child);
						if (parent != null) {
							String parentType = parent
									.get(AbstractService.TYPE).toString();
							if (parentType == null
									|| parentType
											.equals(AbstractService.NETWORK_ID)) {
								exist = false;
							} else {
								if (!allProperties.contains(parentType)) {
									allProperties.add(parentType);
								}
								structureMap.put(parentType,
										parent.get(AbstractService.NAME)
												.toString());
							}
						}
						child = parent;
					}

					parents.put(
							element.get(AbstractService.NAME) != null ? element
									.get(AbstractService.NAME).toString()
									: StringUtils.EMPTY, structureMap);

					for (String header : element.keySet()) {
						if (!allProperties.contains(header)) {
							allProperties.add(header);
						}

					}
				}

				if (!level) {
					updateTableView(null, true);
					updateTable = false;
				}

				if (!updateTable) {
					headers = new ArrayList<String>();
					headers = allProperties;
				}
				for (String header : headers) {
					if (!header.equals(AbstractService.NAME)) {
						createColumn(header, DEF_SIZE, false, idx);
						idx++;
					}
				}
			}
		}

		/**
		 * Load measurements
		 */
		private void loadDriveElements() {
			Job measurementsLoading = new Job(MEASUREMENT_LOADING_JOB_HEADER) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					// TODO implement virtual table functionality, cuz on big
					// dataset slow table scrolling

					Set<IDataElement> loadedMeasurements = fillingHeadersList(monitor);
					fillingElementsList(loadedMeasurements, monitor);

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							int columnIndex = 0;
							for (String header : headers) {
								createColumn(header, DEF_SIZE, false,
										columnIndex++);
							}
							updateTableViewForMeasurements(elements, headers,
									currentModel, dataFromTreeView);
						}
					});
					return monitor.isCanceled() ? Status.CANCEL_STATUS
							: Status.OK_STATUS;
				}
			};
			measurementsLoading.setPriority(Job.LONG);
			measurementsLoading.schedule();
		}

		/**
		 * Filling headers list loaded elements
		 * 
		 * @return Loaded Measurements
		 */
		private Set<IDataElement> fillingHeadersList(IProgressMonitor monitor) {
			DriveModel driveModel = (DriveModel) currentModel;

			Set<IDataElement> measurements = dataFromTreeView ? currentDataElements
					: (Set<IDataElement>) driveModel.getMeasurements(
							currentDataElements, monitor);
			monitor.beginTask(MEASUREMENT_LOADING_JOB_HEADER,
					measurements.size() * 2);
			Set<String> measurementHeaders = new HashSet<String>(0);
			for (IDataElement measurement : measurements) {
				measurementHeaders.addAll(measurement.keySet());
				monitor.worked(1);
			}
			// for showing name property in a first column TODO BP: try to
			// refactor !
			measurementHeaders.remove(AbstractService.NAME);
			measurementHeaders.remove(DRIVE_MODEL);
			allProperties.add(AbstractService.NAME);
			allProperties.addAll(measurementHeaders);

			if (!updateTable) {
				headers = new ArrayList<String>(0);
				headers.addAll(allProperties);
			}
			return measurements;
		}

		/**
		 * Filling list of elements measurements properties
		 */
		private void fillingElementsList(Set<IDataElement> measurements,
				IProgressMonitor monitor) {
			elements.clear();
			for (IDataElement measurement : measurements) {
				List<String> rowValues = new ArrayList<String>(0);
				Set<String> currentHeaders = measurement.keySet();
				for (String header : headers) {
					if (AbstractService.NAME.equals(header)) {
						rowValues.add(measurement.toString());
					} else {
						rowValues
								.add(currentHeaders.contains(header) ? measurement
										.get(header).toString()
										: StringUtils.EMPTY);
					}
				}
				RowWrapper rowWrapper = new RowWrapper(rowValues);
				rowWrapper.setElement(measurement);
				elements.add(rowWrapper);
				monitor.worked(1);
			}
			if (!elements.isEmpty()) {
				updateMeasurementsProperties = true;
			}
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			RowWrapper wrapper = (RowWrapper) element;
			return wrapper.getValues().get(columnIndex);
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
					if (TableColumnSorter.this.viewer.getComparator() != null
							|| TableColumnSorter.this.viewer.getComparator() == TableColumnSorter.this) {
						int tdirection = TableColumnSorter.this.direction;

						if (tdirection == ASC) {
							setSorter(TableColumnSorter.this, DESC);
						} else if (tdirection == DESC) {
							setSorter(TableColumnSorter.this, NONE);
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

	/**
	 * TODO Purpose of NetworkPropertiesView
	 * <p>
	 * Content provider for multiply table
	 * </p>
	 * 
	 * @author Ladornaya_A
	 * @since 1.0.0
	 */
	private class MultiplyTableContentProvider implements
			IStructuredContentProvider {

		/**
		 * The Constructor
		 */
		public MultiplyTableContentProvider() {
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
			if (!updateMeasurementsProperties) {
				elements.clear();
			}

			((MultiplyTableLabelProvider) tableViewer.getLabelProvider())
					.createTableColumn();

			if (currentModel instanceof INetworkModel) {
				createNetworkPropertiesRows();
			}

		}

		/**
		 * Fills in the table
		 */
		private void createNetworkPropertiesRows() {
			if (currentDataElements != null) {
				for (IDataElement element : currentDataElements) {
					Map<String, String> structureMap = parents.get(element
							.get(AbstractService.NAME) != null ? element.get(
							AbstractService.NAME).toString()
							: StringUtils.EMPTY);
					List<String> rowValues = new ArrayList<String>();
					for (String header : headers) {

						if (element.keySet().contains(header)) {
							rowValues.add(element.get(header).toString());
						} else if (structureMap.containsKey(header)) {
							rowValues.add(structureMap.get(header));
						} else {
							rowValues.add(StringUtils.EMPTY);
						}
					}
					RowWrapper row = new RowWrapper(rowValues);
					row.setElement(element);
					elements.add(row);
				}
			}
		}

	}

	/**
	 * <p>
	 * Wrapper of one row of table
	 * </p>
	 * 
	 * @author Kasnitskij_V
	 * @since 1.0.0
	 */
	private class RowWrapper {
		private List<String> values;
		private IDataElement element;

		private RowWrapper(List<String> values) {
			super();
			this.setValues(values);
		}

		public List<String> getValues() {
			return values;
		}

		public void setValues(List<String> values) {
			this.values = values;
		}

		public IDataElement getElement() {
			return element;
		}

		public void setElement(IDataElement element) {
			this.element = element;
		}
	}

	@Override
	public void setFocus() {
	}

	/**
	 * copy date to clipboard
	 */
	public void copyToClipboard() {

		Clipboard cb = new Clipboard(Display.getDefault());

		StringBuilder sb = new StringBuilder();
		// headers
		sb.append(parseToString(headers));

		// rows
		for (RowWrapper row : elements) {
			sb.append(parseToString(row.getValues()));
		}

		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[] { sb.toString() },
				new Transfer[] { textTransfer });
	}

	/**
	 * transform list at line
	 * 
	 * @param list
	 * @return line
	 */
	private String parseToString(List<String> list) {
		String line = StringUtils.EMPTY;
		for (String value : list) {
			line = line + value + "\t";
		}
		line = line + System.getProperty("line.separator");
		return line;
	}

	/*
	 * filter properties
	 */
	public void filter(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		Shell parent = workbenchWindow.getShell();
		Dialog dialog = new Dialog(parent) {
			@Override
			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite) super
						.createDialogArea(parent);
				composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						true));
				composite.setLayout(new GridLayout());
				final Button button = new Button(composite, SWT.NONE);
				button.setText(NetworkMessages.SELECT_ALL);
				button.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						propertyListTable.setAllChecked(true);
						propertyListTable.refresh();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						propertyListTable.refresh();
					}
				});
				propertyListTable = CheckboxTableViewer.newCheckList(composite,
						SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
								| SWT.CHECK);
				GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
				data.heightHint = 300;
				data.widthHint = 200;
				createTable(propertyListTable, NetworkMessages.PROPERTIES);
				propertyListTable.getControl().setLayoutData(data);
				propertyListTable
						.setContentProvider(new PropertyListContentProvider());
				propertyListTable
						.setLabelProvider(new PropertyListLabelProvider());
				propertyListTable.setInput(StringUtils.EMPTY);
				for (String header : headers) {
					propertyListTable.setChecked(header, true);
				}
				return dialogArea;
			}

			@Override
			protected Point getInitialSize() {
				return new Point(250, 400);
			}

			@Override
			protected void configureShell(Shell newShell) {
				super.configureShell(newShell);
				newShell.setText(NetworkMessages.PROPERTIES_FILTER);
			}

			@Override
			protected void okPressed() {
				Object[] selectedProperties = propertyListTable
						.getCheckedElements();
				headers = new ArrayList<String>();
				for (Object o : selectedProperties) {
					headers.add(o.toString());
				}
				updateTable = true;
				close();
			}

		};

		if (dialog.open() == Dialog.OK) {

		}

	}

	/**
	 * create filter properties table
	 */
	private void createTable(TableViewer tableView, String columnName) {
		Table table = tableView.getTable();
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setWidth(200);
		column.setText(columnName);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	/**
	 * TODO Purpose of NetworkPropertiesView
	 * <p>
	 * content provider for filter properties table
	 * </p>
	 * 
	 * @author Ladornaya_A
	 * @since 1.0.0
	 */
	private class PropertyListContentProvider implements
			IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return allProperties.toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	/**
	 * TODO Purpose of NetworkPropertiesView
	 * <p>
	 * label provider for filter properties table
	 * </p>
	 * 
	 * @author Ladornaya_A
	 * @since 1.0.0
	 */
	public class PropertyListLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			return element.toString();
		}
	}
}
