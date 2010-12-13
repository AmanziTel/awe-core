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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class ExportNetworkWizardColumnsConfigPage extends WizardPage {

	private Group main;
	private TableViewer viewer;
	private final List<RowWr> propertyList = new ArrayList<RowWr>();

	private final int colIndexType = 0;
	private final int colIndexProperty = 1;
	private final int colIndexColumn = 2;
	private static final Color BAD_COLOR = new Color(null, 255, 0, 0);
	private Node rootNode;

	protected ExportNetworkWizardColumnsConfigPage(String pageName) {
		super(pageName, "Export network", null);
		setDescription("Choose network that should be exported");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		main = new Group(parent, SWT.NULL);
		main.setLayout(new GridLayout(3, false));
		main.setText("Properties");
		// Label label = new Label(main, SWT.LEFT);
		// label.setText("Network");
		// label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
		// 3, 1));

		viewer = new TableViewer(main, SWT.BORDER | SWT.FULL_SELECTION);

		TableContentProvider provider = new TableContentProvider();
		createTableColumn();
		viewer.setContentProvider(provider);

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		viewer.getControl().setLayoutData(layoutData);

		layoutData.grabExcessVerticalSpace = true;
		viewer.setInput(new Object[0]);

		setControl(main);
	}

	private void createTableColumn() {
		Table table = viewer.getTable();
		TableViewerColumn column;
		TableColumn col;

		column = new TableViewerColumn(viewer, SWT.RIGHT);
		col = column.getColumn();
		col.setText("Node type");
		col.setWidth(50);
		col.setResizable(true);
		column.setLabelProvider(new ColLabelProvider(colIndexType));
		column.setEditingSupport(new TableEditableSupport(viewer, colIndexType));

		column = new TableViewerColumn(viewer, SWT.RIGHT);
		col = column.getColumn();
		col.setText("Property name");
		col.setWidth(200);
		col.setResizable(true);
		column.setLabelProvider(new ColLabelProvider(colIndexProperty));
		column.setEditingSupport(new TableEditableSupport(viewer, colIndexProperty));

		column = new TableViewerColumn(viewer, SWT.RIGHT);
		col = column.getColumn();
		col.setText("File column name");
		col.setWidth(200);
		col.setResizable(true);
		column.setLabelProvider(new ColLabelProvider(colIndexColumn));
		column.setEditingSupport(new TableEditableSupport(viewer, colIndexColumn));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.refresh();

	}

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Taskc List, for
	 * example).
	 */

	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return propertyList.toArray(new RowWr[0]);
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	public class RowWr {
		private final String nodeType;
		private final String propertyName;
		private String columnName;

		public RowWr(String nodeType, String propertyName, String columnName) {
			this.nodeType = nodeType;
			this.propertyName = propertyName;
			this.columnName = columnName;
		}

		public String getValue(int colIndex) {
			switch (colIndex) {
			case colIndexColumn:
				return columnName;
			case colIndexProperty:
				return propertyName;
			case colIndexType:
				return nodeType;
			default:
				return null;
			}
		}

		public void setValue(int colIndex, String sValue) {
			if (colIndexColumn == colIndex) {
				columnName = sValue;
			}
		}

		public boolean isValid() {
			return false;
		}
	}

	private class ColLabelProvider extends ColumnLabelProvider {

		private final int colInd;

		public ColLabelProvider(int colInd) {
			super();
			this.colInd = colInd;
		}

		@Override
		public Color getForeground(Object element) {
			RowWr wrapper = (RowWr) element;
			if (colInd == colIndexColumn) {
				return wrapper.isValid() ? null : BAD_COLOR;
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			RowWr wrapper = (RowWr) element;
			return wrapper.getValue(colInd);
		}
	}

	public class TableEditableSupport extends EditingSupport {

		private final TextCellEditor editor;
		private final int colIndex;

		public TableEditableSupport(TableViewer viewer, int colIndex) {
			super(viewer);
			this.colIndex = colIndex;
			editor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return colIndex == colIndexColumn;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			return ((RowWr) element).getValue(colIndex);
		}

		@Override
		protected void setValue(Object element, Object value) {
			String sValue = ((String) value).trim();
			((RowWr) element).setValue(colIndex, sValue);
			// checkEmpty();
			// validate();
			viewer.refresh();
		}

	}

	/**
	 * 
	 * @param selectedNode
	 */
	public void changeNodeSelection(Node selectedNode) {

		this.rootNode = selectedNode;
		DatasetService datasetService = NeoServiceFactory.getInstance()
				.getDatasetService();
		String[] strtypes = datasetService.getSructureTypesId(rootNode);
		List<String> headers = new ArrayList<String>();

		for (int i = 1; i < strtypes.length; i++) {
			headers.add(strtypes[i]);
		}
		// Collect all existed properties
		HashMap<String, Collection<String>> propertyMap = new HashMap<String, Collection<String>>();
		TraversalDescription descr = Traversal
				.description()
				.depthFirst()
				.uniqueness(Uniqueness.NONE)
				.relationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING)
				.filter(Traversal.returnAllButStartNode());
		for (Path path : descr.traverse(rootNode)) {
			Node node = path.endNode();
			INodeType type = datasetService.getNodeType(node);
			if (type != null && headers.contains(type.getId())) {
				Collection<String> coll = propertyMap.get(type.getId());
				if (coll == null) {
					coll = new TreeSet<String>();
					propertyMap.put(type.getId(), coll);
				}
				for (String propertyName : node.getPropertyKeys()) {
					if (INeoConstants.PROPERTY_TYPE_NAME.equals(propertyName)) {
						continue;
					}
					coll.add(propertyName);
				}
			}
		}

//		Create table content
		Map<String, String> originalHeaders = datasetService.getOriginalFileHeaders(rootNode);
		List<RowWr> rows = new ArrayList<RowWr>();
		for (Map.Entry<String, Collection<String>> entry : propertyMap.entrySet()) {
			String type = entry.getKey();
			for (String propertyName : entry.getValue()) {
				String columnName = "";
				String origHeader = originalHeaders.get(type + INeoConstants.PROPERTY_NAME_PREFIX + propertyName);
				if(origHeader != null){
					columnName = origHeader;
				}
				rows.add(new RowWr(type, propertyName, columnName));
			}
		}
		propertyList.clear();
		propertyList.addAll(rows);
		if(viewer != null)
			viewer.setInput("");

	}
}
