package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.amanzi.neo.services.ui.utils.AbstractDialog;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class CreateNewElementDialog extends AbstractDialog<Integer> {

	// sector
	private final static String[] SECTOR_PROPERTIES = { "name", "ci", "lac" };

	// site
	private final static String[] SITE_PROPERTIES = { "name", "lat", "lon" };

	/*
	 * table
	 */
	private TableViewer tableViewer;

	/*
	 * table providers
	 */
	private TableContentProvider contentProvider;
	private TableLabelProvider labelProvider;

	/*
	 * list content properties of selected element
	 */
	private static List<String> properties;

	// selected element for copy
	private IDataElement element;

	private INetworkModel networkModel;

	/*
	 * element type
	 */
	private String type;

	/** The b ok. */
	private Button bOk;

	/** The b cancel. */
	private Button bCancel;

	/** Shell */
	private Shell shell;

	private Shell parentShell;

	// table row elements
	private List<RowValues> elements = new ArrayList<RowValues>();

	public CreateNewElementDialog(Shell parent, IDataElement element,
			String type, INetworkModel networkModel, String title, int style) {
		super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL
				| SWT.CENTER);
		parentShell = parent;
		shell = new Shell(parent, SWT.SHELL_TRIM);
		status = SWT.CANCEL;
		this.element = element;
		this.type = type;
		this.networkModel = networkModel;
	}

	/**
	 * Update properties from selected element
	 */
	private void updateProperies() {

		properties = new ArrayList<String>();

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
	}

	@Override
	protected void createContents(Shell composite) {

		parentShell = composite;

		// composite
		composite.setLayout(new GridLayout(1, true));

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 150;
		data.heightHint = 300;
		composite.setLayoutData(data);

		// label
		Label label = new Label(composite, SWT.NONE);
		label.setText(NetworkMessages.LABEL_TITLE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		// table
		tableViewer = new TableViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION);
		tableViewer.setUseHashlookup(true);

		GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataTable.widthHint = 280;

		tableViewer.getControl().setLayoutData(dataTable);

		labelProvider = new TableLabelProvider();
		labelProvider.createTableColumn();
		tableViewer.setLabelProvider(labelProvider);

		contentProvider = new TableContentProvider();
		tableViewer.setContentProvider(contentProvider);

		tableViewer.setInput(StringUtils.EMPTY);

		// child composite
		Composite child = new Composite(composite, SWT.FILL);
		final GridLayout layout = new GridLayout(2, true);
		child.setLayout(layout);

		// buttons
		bOk = new Button(child, SWT.PUSH);
		GridData dataButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		dataButton.widthHint = 140;
		bOk.setLayoutData(dataButton);
		bOk.setText(NetworkMessages.BUTTON_OK_TITLE);
		bCancel = new Button(child, SWT.PUSH);
		bCancel.setLayoutData(dataButton);
		bCancel.setText(NetworkMessages.BUTTON_CANCEL_TITLE);
		addListeners();

	}

	/**
	 * Adds the button listeners.
	 */
	private void addListeners() {

		// OK
		bOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				status = SWT.OK;
				try {
					// copy element
					boolean save = saveElement();
					if (save) {
						// update views
						EventManager.getInstance().fireEvent(
								new UpdateDataEvent());
						parentShell.close();
					}
				} catch (AWEException e1) {
					// TODO Handle AWEException
					throw (RuntimeException) new RuntimeException()
							.initCause(e1);
				}
			}
		});

		// Cancel
		bCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				status = SWT.CANCEL;
				parentShell.close();
			}
		});
	}

	/**
	 * Create new element
	 * 
	 * @throws AWEException
	 */
	private boolean saveElement() throws AWEException {

		// parameters
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AbstractService.TYPE, type);
		for (RowValues r : elements) {
			if ((r.getProperty().equals(INeoConstants.PROPERTY_LAT_NAME) || r
					.getProperty().equals(INeoConstants.PROPERTY_LON_NAME))
					&& isDouble(r.getValue().toString())) {
				params.put(r.getProperty(),
						Double.parseDouble(r.getValue().toString()));
			} else {
				params.put(r.getProperty(), r.getValue());
			}
		}

		// validation
		if (params.get(AbstractService.NAME).toString().isEmpty()) {
			MessageDialog.openError(shell, "Enter name!",
					"Name is empty. Enter name.");
			return false;
		} else if (networkModel.findElementByPropertyValue(
				NodeTypeManager.getType(type), AbstractService.NAME,
				params.get(AbstractService.NAME)).size() != 0) {
			MessageDialog.openError(shell,
					NetworkMessages.ELEMENT_EXIST_ERROR_TITLE,
					NetworkMessages.ELEMENT_EXIST_ERROR);
			return false;
		} else if (type.equals(NetworkElementNodeType.SITE.getId())) {
			if (!isDouble(params.get(INeoConstants.PROPERTY_LAT_NAME)
					.toString())
					|| !isDouble(params.get(INeoConstants.PROPERTY_LON_NAME)
							.toString())) {
				MessageDialog.openError(shell,
						NetworkMessages.LAT_LON_ERROR_TITLE,
						NetworkMessages.LAT_LON_ERROR);
				return false;
			}

		} else if (type.equals(NetworkElementNodeType.SECTOR.getId())) {
			if (!isDouble(params.get(INeoConstants.PROPERTY_SECTOR_CI)
					.toString())
					|| !isDouble(params.get(INeoConstants.PROPERTY_SECTOR_LAC)
							.toString())) {
				MessageDialog.openError(shell,
						NetworkMessages.CI_LAC_ERROR_TITLE,
						NetworkMessages.CI_LAC_ERROR);
				return false;
			}
		}

		// create element
		networkModel.createElement(element, params);
		return true;
	}

	/**
	 * Checking value - double or no
	 * 
	 * @param value
	 *            string value
	 * @return true if double
	 */
	private boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException exc) {
			return false;
		}
		return true;
	}

	/**
	 * TODO Purpose of CreateNewElementDialog
	 * <p>
	 * Content provider for property table of selected element
	 * </p>
	 * 
	 * @author ladornaya_a
	 * @since 1.0.0
	 */
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			elements.clear();
			updateProperies();
			((TableLabelProvider) tableViewer.getLabelProvider())
					.createTableColumn();

			for (String property : properties) {

				RowValues r = new RowValues(property, StringUtils.EMPTY);
				elements.add(r);

			}

		}

		@Override
		public Object[] getElements(Object inputElement) {
			return elements.toArray(new RowValues[0]);
		}

	}

	/**
	 * TODO Purpose of CreateNewElementDialog
	 * <p>
	 * Label provider for property table
	 * </p>
	 * 
	 * @author ladornaya_a
	 * @since 1.0.0
	 */
	private class TableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		// column size
		private final static int DEF_SIZE = 147;

		// column name
		private final String PROPERTY_NAME = "Property";
		private final String PROPERTY_VALUE = "Value";

		// column list
		private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();

		// create one column
		private void createColumn(String label, int size, final int idx,
				boolean edit) {
			TableViewerColumn column = new TableViewerColumn(tableViewer,
					SWT.LEFT);
			TableColumn col = column.getColumn();
			col.setText(label);
			columns.add(col);
			col.setWidth(DEF_SIZE);
			col.setResizable(true);
			if (edit) {
				column.setEditingSupport(new ValueEditingSupport(tableViewer));
			}
		}

		/**
		 * create column table
		 */
		public void createTableColumn() {
			Table tabl = tableViewer.getTable();
			for (TableColumn column : columns) {
				column.dispose();
			}
			int idx = 0;
			createColumn(PROPERTY_NAME, DEF_SIZE, idx, false);
			idx++;
			createColumn(PROPERTY_VALUE, DEF_SIZE, idx, true);
			idx++;

			tabl.setHeaderVisible(true);
			tabl.setLinesVisible(true);
			tableViewer.setLabelProvider(this);
			tableViewer.refresh();
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			RowValues r = (RowValues) element;
			if (columnIndex == 0) {
				return r.getProperty();
			} else {
				return r.getValue().toString();
			}
		}
	}

	/**
	 * TODO Purpose of CreateNewElementDialog
	 * <p>
	 * Save row values
	 * </p>
	 * 
	 * @author ladornaya_a
	 * @since 1.0.0
	 */
	private class RowValues {

		// row values
		private String property;
		private Object value;

		public RowValues(String property, Object value) {
			this.property = property;
			this.value = value;
		}

		public String getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}

	/**
	 * TODO Purpose of CreateNewElementDialog
	 * <p>
	 * Class for editing value column
	 * </p>
	 * 
	 * @author ladornaya_a
	 * @since 1.0.0
	 */
	public class ValueEditingSupport extends EditingSupport {

		// table
		private final TableViewer viewer;

		public ValueEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((RowValues) element).getValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			((RowValues) element).setValue(value);
			viewer.refresh();
		}
	}

}
