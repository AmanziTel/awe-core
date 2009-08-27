package org.amanzi.splash.ui;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.nodes.ChartItemNode;
import org.amanzi.neo.core.database.nodes.ChartNode;
import org.amanzi.neo.core.database.nodes.PieChartItemNode;
import org.amanzi.neo.core.database.nodes.PieChartNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.service.listener.INeoServiceProviderListener;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.splash.database.services.SpreadsheetService;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.ColumnHeaderRenderer;
import org.amanzi.splash.swing.SplashTable;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.amanzi.splash.views.importbuilder.ImportBuilderTableViewer;
import org.amanzi.splash.views.importbuilder.ImportBuilderView;
import org.amanzi.splash.ui.wizards.ExportScriptWizard;
import org.eclipse.albireo.core.SwingControl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;

import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellFormatPanel;

/**
 * Defines a sample "mini-spreadsheet" editor that demonstrates how to create an
 * editor whose input is based on either resources from the workspace (IDE) or
 * directly from the file system (RCP).
 */

public abstract class AbstractSplashEditor extends EditorPart implements
		TableModelListener, INeoServiceProviderListener {
	static final String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int defaultAlignment;
	private boolean isDirty = false;
	private boolean enabled;
	private TableViewer tableViewer;
	CellFormatPanel cellFormatPanel = null;
	CellFormat cellFormat = null;

	private ResourceBundle resourceBundle = ResourceBundle.getBundle("com.eteks.openjeks.resources.openjeks");
	private String selectCellValue = "";
	SplashTable table;
	private SwingControl swingControl;
	private int ROWS_EDGE_MARGIN = 5;
	private int COLUMNS_EDGE_MARGIN = 5;
	private String splashID;

	/**
	 * Class constructor
	 */
	public AbstractSplashEditor() {

	}

	public SplashTable getTable() {
		return table;
	}

	/**
	 * Create a new valid <code>IEditorInput</code> for this concrete
	 * implementation (e.g., by opening a "new dialog" or launching a creation
	 * wizard).
	 */
	public abstract IEditorInput createNewInput(String message) throws CoreException;

	/**
	 * 
	 * @param table
	 */
	private void launchCellFormatPanel(SplashTable table) {
        final int firstRow, firstColumn, lastRow, lastColumn;
		firstRow = table.getSelectedRow();
		firstColumn = table.getSelectedColumn();
		lastRow = firstRow + table.getSelectedRowCount() - 1;
		lastColumn = firstColumn + table.getSelectedColumnCount() - 1;

		cellFormat = ((Cell) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn())).getCellFormat();
		cellFormatPanel = new CellFormatPanel(cellFormat);
		if (JOptionPane.showConfirmDialog(null, cellFormatPanel, resourceBundle.getString("FORMAT_PANEL_TITLE"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
			cellFormat = cellFormatPanel.getCellFormat();
            ActionUtil.getInstance().runTask(new Runnable() {

                @Override
                public void run() {
                    for (int i = firstRow; i <= lastRow; i++) {
                        for (int j = firstColumn; j <= lastColumn; j++) {
                            updateCellFormat(i, j, cellFormat);
                        }
                    }
                }
            }, false);
			setIsDirty(true);
		}
		cellFormatPanel = null;
		table.repaint();
	}

	/**
	 * Update cell format and save the new format in the Neo4j database
	 * 
	 * @param r
	 * @param c
	 * @param cf
	 */
	private void updateCellFormat(int r, int c, CellFormat cf) {
		Cell cell = (Cell) table.getValueAt(r, c);
		cell.setCellFormat(cf);
		((SplashTableModel) (table.getModel())).updateCellFormat(cell);
	}

	/**
	 * 
	 */
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		TableModel model = (TableModel) e.getSource();
		if (!selectCellValue.equals(model.getValueAt(row, column)))
			setIsDirty(true);
	}

	/**
	 * Updates value of Cell from referenced script
	 * 
	 * @param rowIndex
	 *            row index
	 * @param columnIndex
	 *            column index
	 * @author Lagutko_N
	 */

	private void updateCell(final Cell cell) {
		// get selected cell and update value of cell

		((SplashTableModel) table.getModel()).updateCellFromScript(cell);
	}

	/**
	 * Opens referenced script in editor
	 * 
	 * @param cell
	 *            cell to open
	 * @author Lagutko_N
	 */

	private void openCell(final Cell cell) {
		// run open action
		ActionUtil.getInstance().runTask(new Runnable() {
			public void run() {
				// find file by URI
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(cell.getScriptURI());
				if (files.length == 1) {
					try {
						// open file in editor
						EditorUtility.openInEditor(files[0]);
					} catch (RubyModelException e) {
						SplashPlugin.error(null, e);
					} catch (PartInitException e) {
						SplashPlugin.error(null, e);
					}
				} else {
					// TODO: handle this situation
				}
			}
		}, true);
	}

	/**
	 * Method that exports cell to script
	 * 
	 * @param cell
	 *            cell to export
	 * @author Lagutko_N
	 * @param spreadsheetNode
	 */

	private void exportCell(final SpreadsheetNode spreadsheetNode, final Cell cell) {
		// get Cell and Display
		final Display display = swingControl.getDisplay();

		// run ExportScriptWizard
		ActionUtil.getInstance().runTask(new Runnable() {
			public void run() {
				// TODO: put a project for script to wizard's selection
				// Lagutko, 30.07.2009, for now Spreadsheet node is a child of a
				// Reference Node, but not of a
				// Project Node and it means that it's impossible to find a
				// project in which Spreadsheet stored
				WizardDialog dialog = new WizardDialog(display.getActiveShell(), new ExportScriptWizard(spreadsheetNode, cell));
				dialog.open();
				SplashPlugin.getDefault().getSpreadsheetService().updateCell(spreadsheetNode, cell);
			}
		}, false);

		// SplashPlugin.getDefault().getSpreadsheetService().updateCell(
		// spreadsheetNode, cell);
		// ((SplashTableModel) table.getModel()).refreshCell(cell);
	}

	/**
	 * handle event for cancelling cell editing
	 */
	private void cancelCellEditing() {
		CellEditor ce = table.getCellEditor();
		if (ce != null) {
			ce.cancelCellEditing();
		}
	}

	/**
	 * show column menu pop up
	 * 
	 * @param e
	 */
	private void maybeShowColumnPopup(MouseEvent e) {
		/* if (e.isPopupTrigger() && table.isEnabled()) { */
		Point p = new Point(e.getX(), e.getY());
		int col = table.columnAtPoint(p);
		int row = table.rowAtPoint(p);

		// translate table index to model index
		int mcol = table.getColumn(table.getColumnName(col)).getModelIndex();

		if (row >= 0 && row < table.getRowCount()) {
			cancelCellEditing();

			// create popup menu...
			JPopupMenu contextMenu = createColumnContextMenu(row, mcol);

			// ... and show it
			if (contextMenu != null && contextMenu.getComponentCount() > 0) {
				contextMenu.show(table, p.x, p.y);
			}
		}
		// }
	}

	/**
	 * show row menu pop up
	 * 
	 * @param e
	 */
	private void maybeShowRowPopup(MouseEvent e) {
		// if (e.isPopupTrigger() && table.isEnabled()) {
		Point p = new Point(e.getX(), e.getY());
		int col = table.columnAtPoint(p);
		int row = table.rowAtPoint(p);

		// translate table index to model index
		int mcol = table.getColumn(table.getColumnName(col)).getModelIndex();

		if (row >= 0 && row < table.getRowCount()) {
			cancelCellEditing();

			// create popup menu...
			JPopupMenu contextMenu = createRowContextMenu(row, mcol);

			// ... and show it
			if (contextMenu != null && contextMenu.getComponentCount() > 0) {
				contextMenu.show(table, p.x, p.y);
			}
		}
		// }
	}

	/**
	 * show cell menu pop up
	 * 
	 * @param e
	 */
	private void maybeShowPopup(MouseEvent e) {
		// if (e.isPopupTrigger() && table.isEnabled()) {
		Point p = new Point(e.getX(), e.getY());
		int col = table.columnAtPoint(p);
		int row = table.rowAtPoint(p);

		// translate table index to model index
		int mcol = table.getColumn(table.getColumnName(col)).getModelIndex();

		if (row >= 0 && row < table.getRowCount()) {
			cancelCellEditing();

			// create popup menu...
			JPopupMenu contextMenu = createContextMenu(row, mcol);

			// ... and show it
			if (contextMenu != null && contextMenu.getComponentCount() > 0) {
				contextMenu.show(table, p.x, p.y);
			}
		}
		// }
	}

	// Returns the preferred height of a row.
	// The result is equal to the tallest cell in the row.
	public int getPreferredRowHeight(JTable table, int rowIndex, int margin) {
		// Get the current default height for all rows
		int height = table.getRowHeight();

		// Determine highest cell in the row
		for (int c = 0; c < table.getColumnCount(); c++) {
			TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
			Component comp = table.prepareRenderer(renderer, rowIndex, c);
			int h = comp.getPreferredSize().height + 2 * margin;
			height = Math.max(height, h);
		}
		return height;
	}

	/**
	 * create Swing table
	 * 
	 * @param parent
	 */
	private void createTable(final Composite parent) {
		swingControl = new SwingControl(parent, SWT.NONE) {
			{
				setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
				setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			}

			protected JComponent createSwingComponent() {

				JScrollPane scrollPane = new JScrollPane(table);

				table.setOpaque(true);
				table.setBackground(getAWTHierarchyRoot().getBackground());

				scrollPane.setBorder(new EmptyBorder(1, 1, 1, 1));

				scrollPane.setRowHeaderView(getTable().rowHeader);

				// table.setTableFormat(tableModel.tableFormat);
				table.setRowHeight(table.getDefaultRowHeight());

				// table.getColumnModel().getColumn(2).setCellRenderer(new
				// BackgroundColumnCellRenderer(new
				// java.awt.Color(255,255,204)));

				for (int i = 0; i < table.getColumnCount(); i++) {
					TableColumn col = table.getColumnModel().getColumn(i);
					col.setPreferredWidth(table.getDefaultColumnWidth());

					col.setHeaderRenderer(new ColumnHeaderRenderer(table.getDefaultColumnWidth(), 20));
				}

				// Handle the listener
				ListSelectionModel selectionModel = table.getSelectionModel();
				selectionModel.addListSelectionListener(new ListSelectionListener() {

					// @Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub

						// TODO:
						// updateTableHeaderHighlights(table.getSelectedRow(),
						// table.getSelectedColumn());

						// for (int
						// i=0;i<table.getSelectedRows().length;i++){
						// for (int
						// j=0;j<table.getSelectedColumns().length;j++){
						// updateTableHeaderHighlights(i,j);
						// }
						// }
					}

				});

				// set selection mode for contiguous intervals
				MouseListener ml = new HeaderMouseAdapter();

				// we don't allow reordering
				table.getTableHeader().setReorderingAllowed(false);
				table.getTableHeader().addMouseListener(ml);
                table.rowHeader.addMouseListener(ml);
                // add listener for resizing row
                new TableRowResizer(table.rowHeader, table);
				table.addKeyListener(new KeyListener() {

					public void keyPressed(KeyEvent e) {

					}

					@SuppressWarnings("static-access")
					public void keyReleased(KeyEvent e) {
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();

						// Util.log("e.VK_ENTER = " + e.VK_ENTER);
						if (e.getKeyCode() == e.VK_ENTER) /* ENTER */
						{

							int rdiff = table.getRowCount() - row;
							int cdiff = table.getColumnCount() - column;

							if (rdiff < COLUMNS_EDGE_MARGIN) {
								getTable().insertRows(table.getRowCount() - 1, COLUMNS_EDGE_MARGIN - rdiff);
								setIsDirty(true);
							}

							if (cdiff < ROWS_EDGE_MARGIN) {
								getTable().insertColumns(table.getColumnCount() - 1, ROWS_EDGE_MARGIN - cdiff);
								setIsDirty(true);
							}
						}

						if (e.getKeyCode() == 66 || e.getKeyCode() == 73 || e.getKeyCode() == 85) {
							if (table.isEditing() == false) {
								table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
								DefaultCellEditor editor = (DefaultCellEditor) table.getCellEditor();
								JTextField textfield = (JTextField) editor.getComponent();
								String s = textfield.getText();
								if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK) == true) {
									s += e.getKeyText(e.getKeyCode()).toUpperCase();
								} else {
									s += e.getKeyText(e.getKeyCode()).toLowerCase();
								}
								textfield.setText(s);
								textfield.setCaretPosition(textfield.getText().length());
								textfield.getCaret().setVisible(true);
							}
						}

						if (e.isControlDown() && e.getKeyCode() == 67) {
							table.copyCell(row, column);

						} else if (e.isControlDown() && e.getKeyCode() == 88) {
							table.cutCell(row, column);
						} else if (e.isControlDown() && e.getKeyCode() == 86) {
							table.pasteCell(row, column);
						} else if (e.isControlDown() && e.getKeyCode() == 66) {

							Cell cell = (Cell) table.getValueAt(row, column);
							CellFormat cf = cell.getCellFormat();

							Integer fs = cf.getFontStyle().intValue();

							switch (fs) {
							case Font.PLAIN:
								fs = Font.BOLD;
								break;
							case Font.BOLD:
								fs = Font.PLAIN;
								break;
							case Font.ITALIC:
								fs = Font.BOLD + Font.ITALIC;
								break;
							case Font.BOLD + Font.ITALIC:
								fs = Font.ITALIC;
								break;
							}

							cf.setFontStyle(fs);
							updateCellFormat(row, column, cf);

							table.repaint();
							// setIsDirty(true);
						} else if (e.isControlDown() && e.getKeyCode() == 73) {

							Cell cell = (Cell) table.getValueAt(row, column);
							CellFormat cf = cell.getCellFormat();

							Integer fs = cf.getFontStyle().intValue();

							switch (fs) {
							case Font.PLAIN:
								fs = Font.ITALIC;
								break;
							case Font.BOLD:
								fs = Font.BOLD + Font.ITALIC;
								break;
							case Font.ITALIC:
								fs = Font.PLAIN;
								break;
							case Font.BOLD + Font.ITALIC:
								fs = Font.BOLD;
								break;
							}

							cf.setFontStyle(fs);
							updateCellFormat(row, column, cf);
							table.repaint();
							// setIsDirty(true);
						} else if (e.isControlDown() && e.getKeyCode() == 85) {

							Cell cell = (Cell) table.getValueAt(row, column);

							String old_value = (String) cell.getValue();
							String new_value = "";
							if (old_value.contains("<HTML><U>") && old_value.contains("</U></HTML>")) {
								new_value = old_value.replace("<HTML><U>", "");
								new_value = new_value.replace("</U></HTML>", "");
							} else {
								new_value = "<HTML><U>" + old_value + "</U></HTML>";
							}

							cell.setValue(new_value);
							table.setValueAt(cell, row, column);
							setIsDirty(true);
						}

					}

					public void keyTyped(KeyEvent e) {
						// TODO Auto-generated method stub

					}

				});
				table.addMouseListener(new MouseListener() {

					public void mouseClicked(MouseEvent e) {

					}

					public void mouseEntered(MouseEvent e) {

					}

					public void mouseExited(MouseEvent e) {

					}

					public void mousePressed(MouseEvent e) {

						int column = table.columnAtPoint(e.getPoint());
						int row = table.rowAtPoint(e.getPoint());
						if (e.getButton() == 3) {
							table.setColumnSelectionInterval(column, column);
							table.setRowSelectionInterval(row, row);

							maybeShowPopup(e);
							// launchCellFormatPanel(table);
						} else {

							// table.repaint();
						}
					}

					public void mouseReleased(MouseEvent e) {

					}

				});

				return scrollPane;
			}

			public Composite getLayoutAncestor() {
				return parent;
			}

		};
	}

	int prev_selected_column = -1;
	int prev_selected_row = -1;

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(final Composite parent) {
		// Lagutko, 28.07.2009, we use SplashEditorInput
		SplashEditorInput sei = (SplashEditorInput) getEditorInput();

		splashID = sei.getName().replace(".splash", "");
		RubyProjectNode root = sei.getRoot();

		NeoSplashUtil.logn("splashID = " + splashID);

		table = new SplashTable(splashID, root);
		table.getModel().addTableModelListener(this);

		createTable(parent);

		IWorkspaceRoot root1 = ResourcesPlugin.getWorkspace().getRoot();
		final IProject resource = root1.getProject("project.AWEScript");
		SpreadsheetService service = SplashPlugin.getDefault().getSpreadsheetService();
		SplashTableModel model = (SplashTableModel) table.getModel();
		SpreadsheetNode spreadsheet = model.getSpreadsheet();
		int chartsCount = spreadsheet.getChartsCount();
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			if (validateEditorInput(getEditorInput()) != null) {
				if (getEditorInput().exists())
					saveContents();
				else
					doSaveAs(MessageFormat.format("The original input ''{0}'' has been deleted.", new Object[] { getEditorInput()
							.getName() }));
			} else {
				doSaveAs();
			}
		} catch (CoreException e) {
			monitor.setCanceled(true);
			MessageDialog.openError(null, "Unable to Save Changes", e.getLocalizedMessage());
			return;
		}
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Subclasses should persist the mini-spreadsheet in their
	 * implementation-specific manner (in our case, directly in file system or
	 * the workspace).
	 */
	public abstract boolean saveContents() throws CoreException;

	/**
	 * Respond to the Outline view's request to update the selection.
	 * 
	 * @see org.amanzi.splash.neo4j.ui.outline.JRSSContentOutlinePage
	 */
	public void selectionChanged(ISelection selection) {
		tableViewer.setSelection(selection);
	}

	/**
	 * Set the mini-spreadsheet's contents using the given
	 * <code>IEditorInput</code> (subclasses can assume it has previously been
	 * validated by <code>validateEditorInput</code>.
	 */
	public abstract void setContents(IEditorInput editorInput) throws CoreException;

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// table.setFocus();
	}

	/**
	 * Persist the mini-spreadsheet as a workspace resource, allowing for the
	 * <b>Restore from Local History</b> options.
	 */

	/**
	 * Flag the mini-spreadsheet as dirty, enable the <b>Save</b> options, an
	 * update the editor's modification indicator (*).
	 */
	protected void setIsDirty(boolean isDirty) {

		// this.isDirty = isDirty;

		// firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Verify the editor input is valid (subclasses may transform it to another
	 * implementator of <code>IEditorInput</code>), or null if the input is
	 * unacceptable.
	 */
	public abstract IEditorInput validateEditorInput(IEditorInput editorInput);

	/**
	 * @see org.eclipse.ui.IEditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		doSaveAs("Save As");
	}

	private void doSaveAs(String message) {
		try {
			IEditorInput input = createNewInput(message);
			if (input != null) {
				setInput(input);
				saveContents();
				setPartName(input.getName());

				firePropertyChange(PROP_INPUT);
			}
		} catch (CoreException e) {
			MessageDialog.openError(null, "Unable to Save Changes", e.getLocalizedMessage());
			return;
		}
	}

	/**
	 * Return the column alignment.
	 */
	public int getDefaultAlignment() {
		return defaultAlignment;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(IEditorSite, IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {

		if (!editorInput.exists())
			throw new PartInitException(editorInput.getName() + "does not exist.");

		IEditorInput ei = validateEditorInput(editorInput);

		// This message includes class names to help
		// the programmer / reader; production code would instead
		// log an error and provide a helpful, friendly message.
		if (ei == null)
			throw new PartInitException(MessageFormat.format("Invalid input.\n\n({0} is not a valid input for {1})", editorInput
					.getClass().getName(), this.getClass().getName()));

		try {

			setInput(ei);
			setContents(ei);
			setSite(site);
			setPartName(editorInput.getName());
		} catch (CoreException e) {
			throw new PartInitException(e.getMessage());
		}
	}

	/**
	 * create context menu for columns
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private JPopupMenu createColumnContextMenu(final int rowIndex, final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem columnInsertMenu = new JMenuItem();
		columnInsertMenu.setText("Insert Column");
		columnInsertMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTable().insertColumn(columnIndex);
				table.setColumnSelectionInterval(columnIndex, columnIndex);
				table.setRowSelectionInterval(0, table.getRowCount() - 1);
				setIsDirty(true);
			}
		});
		contextMenu.add(columnInsertMenu);

		JMenuItem deleteColumnMenu = new JMenuItem();
		deleteColumnMenu.setText("Delete Column");
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTable().deleteColumn(columnIndex);
				setIsDirty(true);
			}
		});
		contextMenu.add(deleteColumnMenu);

		JMenuItem moveColumnLeftMenu = new JMenuItem();
		moveColumnLeftMenu.setText("Move Column Left");
		moveColumnLeftMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTable().moveColumnLeft(columnIndex);
				if (columnIndex > 0) {
					table.setColumnSelectionInterval(columnIndex - 1, columnIndex - 1);
				}
				setIsDirty(true);
			}

		});
		contextMenu.add(moveColumnLeftMenu);

		JMenuItem moveColumnRightMenu = new JMenuItem();
		moveColumnRightMenu.setText("Move Column Right");
		moveColumnRightMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTable().moveColumnRight(columnIndex);
				table.setColumnSelectionInterval(columnIndex + 1, columnIndex + 1);
				setIsDirty(true);
			}
		});
		contextMenu.add(moveColumnRightMenu);

		JMenuItem columnFormattingMenu = new JMenuItem();
		columnFormattingMenu.setText("Column Formatting");
		columnFormattingMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCellFormatPanel(table);
			}
		});
		contextMenu.add(columnFormattingMenu);

		return contextMenu;
	}

	/**
	 * create context menu for rows
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private JPopupMenu createRowContextMenu(final int rowIndex, final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem rowInsertMenu = new JMenuItem();
		rowInsertMenu.setText("Insert Row");
		rowInsertMenu.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int col = table.getSelectedColumn();
				getTable().insertRow(rowIndex);

				table.setColumnSelectionInterval(col, col);
				table.setRowSelectionInterval(0, rowIndex);
				setIsDirty(true);
			}
		});
		contextMenu.add(rowInsertMenu);

		JMenuItem deleteRowMenu = new JMenuItem();
		deleteRowMenu.setText("Delete Row");
		deleteRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTable().deleteRow(rowIndex);
				setIsDirty(true);
			}
		});
		contextMenu.add(deleteRowMenu);

		JMenuItem moveRowUpMenu = new JMenuItem();
		moveRowUpMenu.setText("Move Row Up");
		moveRowUpMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTable().moveRowUp(rowIndex);
				table.setRowSelectionInterval(rowIndex - 1, rowIndex - 1);
				setIsDirty(true);
			}
		});
		contextMenu.add(moveRowUpMenu);

		JMenuItem moveRowDownMenu = new JMenuItem();
		moveRowDownMenu.setText("Move Row Down");
		moveRowDownMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTable().moveRowDown(rowIndex);
				table.setRowSelectionInterval(rowIndex + 1, rowIndex + 1);
				setIsDirty(true);
			}
		});
		contextMenu.add(moveRowDownMenu);

		JMenuItem rowFormattingMenu = new JMenuItem();
		rowFormattingMenu.setText("Row Formatting");
		rowFormattingMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCellFormatPanel(table);
			}
		});
		contextMenu.add(rowFormattingMenu);

		return contextMenu;
	}

	/**
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private JPopupMenu createContextMenu(final int rowIndex, final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem cellCopyMenu = new JMenuItem();
		cellCopyMenu.setText("Copy \t\t Ctrl+C");
		cellCopyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.copyCell(rowIndex, columnIndex);
			}
		});
		contextMenu.add(cellCopyMenu);

		JMenuItem cellCutMenu = new JMenuItem();
		cellCutMenu.setText("Cut \t\t Ctrl+X");
		cellCutMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.cutCell(rowIndex, columnIndex);
			}
		});
		contextMenu.add(cellCutMenu);

		JMenuItem cellPasteMenu = new JMenuItem();
		cellPasteMenu.setText("Paste \t\t Ctrl+V");
		cellPasteMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.pasteCell(rowIndex, columnIndex);
			}
		});
		contextMenu.add(cellPasteMenu);

		JMenuItem cellFormattingMenu = new JMenuItem();
		cellFormattingMenu.setText("Cell Formatting");
		cellFormattingMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCellFormatPanel(table);
			}
		});
		contextMenu.add(new JSeparator());
		contextMenu.add(cellFormattingMenu);
		// Lagutko, 30.07.2009, new menu items for integration with RDT

		final Cell cell = (Cell) table.getValueAt(rowIndex, columnIndex);
		JSeparator separator = new JSeparator();
		contextMenu.add(separator);

		if (!cell.hasReference()) {
			JMenuItem exportCellMenu = new JMenuItem();
			exportCellMenu.setText("Export Cell");
			exportCellMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exportCell(((SplashTableModel) table.getModel()).getSpreadsheet(), cell);
				}
			});
			contextMenu.add(exportCellMenu);
		} else {
			JMenuItem openCellMenu = new JMenuItem();
			openCellMenu.setText("Open Cell");
			openCellMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openCell(cell);
				}
			});
			contextMenu.add(openCellMenu);

			JMenuItem updateCellMenu = new JMenuItem();
			updateCellMenu.setText("Update Cell");
			updateCellMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateCell(cell);
				}
			});
			contextMenu.add(updateCellMenu);
			contextMenu.add(updateCellMenu);
		}
		
		JMenuItem addToImportFilterMenu = new JMenuItem();
		addToImportFilterMenu.setText("Add to Import Filter");
		addToImportFilterMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					
				addNewFilter();
			}
		});
		contextMenu.add(addToImportFilterMenu);
		
		
		return contextMenu;
	}
	
	public void addNewFilter(){
		ImportBuilderView view = 
			(ImportBuilderView) PlatformUI.getWorkbench().
		getActiveWorkbenchWindow().getActivePage().findView("org.amanzi.splash.neo4j.views.ImportBuilderView");
		
		ImportBuilderTableViewer iv = view.getImportBuilderTableViewer();
		
		SplashTableModel model = (SplashTableModel)table.getModel();
		
		Cell c = (Cell) model.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
		
		
		String text = (String) c.getValue();
		
		Cell hc = (Cell) model.getValueAt(0, table.getSelectedColumn());
		
		String heading = (String) hc.getValue();
		
		iv.getFiltersList().addHeadingToList(heading);
		
		ComboBoxCellEditor cb = (ComboBoxCellEditor)iv.getTableViewer().getCellEditors()[0];
		
		//cb.setItems(iv.getFiltersList().getHeadingsList());
		int index = 0;
		String[] filter_headings = iv.getFiltersList().getHeadingsList(); 
		for (int i=0;i<iv.getFiltersList().getHeadingsList().length;i++){
			
			if (heading.equals(filter_headings[i]) == true){
				index = i;
				break;
			}
		}
		((CCombo)cb.getControl()).select(index);
		
		iv.getFiltersList().addFilter(heading, text, "filter" + iv.getFiltersList().getFilters().size()+1 + ".rb");
	}
	
	
	public void LoadHeadings(){
		ImportBuilderView view = 
			(ImportBuilderView) PlatformUI.getWorkbench().
		getActiveWorkbenchWindow().getActivePage().findView("org.amanzi.splash.neo4j.views.ImportBuilderView");
		
		ImportBuilderTableViewer iv = view.getImportBuilderTableViewer();
		
		SplashTableModel model = (SplashTableModel)table.getModel();
		
		
		Cell hc = (Cell) model.getValueAt(0, 0);
		String heading = (String) hc.getValue();
		
		int j = 0;
		
		while (heading != ""){
		iv.getFiltersList().addHeadingToList(heading);
		
		ComboBoxCellEditor cb = (ComboBoxCellEditor)iv.getTableViewer().getCellEditors()[0];
		
		cb.setItems(iv.getFiltersList().getHeadingsList());
		j++;
		hc = (Cell) model.getValueAt(0, j);
		heading = (String) hc.getValue();
		}
		

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private class HeaderMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {

			if (e.getButton() == 3) {
				int col = -1;
				int row = -1;
				if (e.getSource() instanceof JTableHeader) {
					col = table.columnAtPoint(e.getPoint());
				}
				if (e.getSource() instanceof JTable) {
					row = table.rowAtPoint(e.getPoint());
				}
				if (col >= 0) {
					maybeShowColumnPopup(e);
				} else if (row >= 0) {
					maybeShowRowPopup(e);
				}

				int rowCount = table.getRowCount();
				int colCount = table.getColumnCount();

				table.setRowSelectionInterval(0, rowCount - 1);

				if (col >= 0) // select column
				{
					table.setColumnSelectionInterval(col, col);
					table.setRowSelectionInterval(0, rowCount - 1);
				} else if (row >= 0) // select row
				{
					table.setColumnSelectionInterval(0, colCount - 1);
					table.setRowSelectionInterval(row, row);
				} else // select all
				{
					table.setColumnSelectionInterval(0, colCount - 1);
					table.setRowSelectionInterval(0, rowCount - 1);
				}
			}
		}

		public void mouseClicked(MouseEvent e) {

			int col = -1;
			int row = -1;
			if (e.getSource() instanceof JTableHeader) {
				col = table.columnAtPoint(e.getPoint());
			}
			if (e.getSource() instanceof JTable) {
				row = table.rowAtPoint(e.getPoint());
			}

			int rowCount = table.getRowCount();
			int colCount = table.getColumnCount();

			table.setRowSelectionInterval(0, rowCount - 1);

			if (col >= 0) // select column
			{
				table.setColumnSelectionInterval(col, col);
				table.setRowSelectionInterval(0, rowCount - 1);
			} else if (row >= 0) // select row
			{
				table.setColumnSelectionInterval(0, colCount - 1);
				table.setRowSelectionInterval(row, row);
			} else // select all
			{
				table.setColumnSelectionInterval(0, colCount - 1);
				table.setRowSelectionInterval(0, rowCount - 1);
			}

		}
	}

	public CellFormat getCellFormat() {
		return cellFormat;
	}

	public void setCellFormat(CellFormat cellFormat) {
		this.cellFormat = cellFormat;
	}

	protected InputStream getJFreeBarChartInitialContents() {

		int firstRow, firstColumn, lastRow, lastColumn;
		firstRow = table.getSelectedRow();
		firstColumn = table.getSelectedColumn();
		lastRow = firstRow + table.getSelectedRowCount() - 1;
		lastColumn = firstColumn + table.getSelectedColumnCount() - 1;
		NeoSplashUtil.logn("firstRow: " + firstRow);
		NeoSplashUtil.logn("firstColumn: " + firstColumn);
		NeoSplashUtil.logn("lastRow: " + lastRow);
		NeoSplashUtil.logn("lastColumn: " + lastColumn);
		NeoSplashUtil.logn("lastColumn-firstColumn: " + (lastColumn - firstColumn));

		StringBuffer sb = new StringBuffer();
		for (int j = firstColumn; j <= lastColumn; j++) {
			Cell c = (Cell) ((SplashTableModel) table.getModel()).getValueAt(firstRow, j);
			sb.append((String) c.getValue() + ";" + (String) ((Cell) table.getValueAt(lastRow, j)).getValue() + ";");

		}
		return new ByteArrayInputStream(sb.toString().getBytes());
	}

	private static int chartCounter = 0;

	public void plotCellsBarChart() {
		String chartName = "";
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject resource = root.getProject("project.AWEScript");
		SpreadsheetService service = SplashPlugin.getDefault().getSpreadsheetService();
		SplashTableModel model = (SplashTableModel) table.getModel();
		SpreadsheetNode spreadsheet = model.getSpreadsheet();
		// int chartsCount = spreadsheet.getChartsCount();

		chartName = "Chart" + chartCounter;
		NeoSplashUtil.log("chartName = " + chartName);
		chartCounter++;
		ChartNode chartNode = service.createChart(spreadsheet, chartName);
		IFile file = resource.getFile(new Path(chartName));
		InputStream stream = getJFreeBarChartInitialContents();
		if (file.exists()) {
			try {
				file.setContents(stream, true, true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				file.create(stream, true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			stream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int firstRow, firstColumn, lastRow, lastColumn;
		firstRow = table.getSelectedRow();
		firstColumn = table.getSelectedColumn();
		lastRow = firstRow + table.getSelectedRowCount() - 1;
		lastColumn = firstColumn + table.getSelectedColumnCount() - 1;
		ChartItemNode[] items = new ChartItemNode[lastColumn - firstColumn + 1];
		for (int i = firstColumn; i <= lastColumn; i++) {
			Cell c = (Cell) ((SplashTableModel) table.getModel()).getValueAt(firstRow, i);

			try {
				items[i] = service.createChartItem(chartNode, "item" + i);
			} catch (SplashDatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			items[i].setChartItemCategory((String) c.getValue());
			items[i].setChartItemValue((String) ((Cell) table.getValueAt(lastRow, i)).getValue());

		}
		IEditorInput editorInput = new ChartEditorInput(file);
		((ChartEditorInput) editorInput).setChartName(chartName);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(editorInput, NeoSplashUtil.AMANZI_NEO4J_SPLASH_CHART_EDITOR);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void plotCellsPieChart() {
		String chartName = "";
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject resource = root.getProject("project.AWEScript");
		SpreadsheetService service = SplashPlugin.getDefault().getSpreadsheetService();
		SplashTableModel model = (SplashTableModel) table.getModel();
		SpreadsheetNode spreadsheet = model.getSpreadsheet();
		// int chartsCount = spreadsheet.getChartsCount();

		chartName = "PieChart" + chartCounter;
		NeoSplashUtil.log("chartName = " + chartName);
		chartCounter++;
		PieChartNode chartNode = service.createPieChart(spreadsheet, chartName);
		IFile file = resource.getFile(new Path(chartName));
		InputStream stream = getJFreeBarChartInitialContents();
		if (file.exists()) {
			try {
				file.setContents(stream, true, true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				file.create(stream, true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			stream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int firstRow, firstColumn, lastRow, lastColumn;
		firstRow = table.getSelectedRow();
		firstColumn = table.getSelectedColumn();
		lastRow = firstRow + table.getSelectedRowCount() - 1;
		lastColumn = firstColumn + table.getSelectedColumnCount() - 1;
		PieChartItemNode[] items = new PieChartItemNode[lastColumn - firstColumn + 1];
		for (int i = firstColumn; i <= lastColumn; i++) {
			Cell c = (Cell) ((SplashTableModel) table.getModel()).getValueAt(firstRow, i);

			try {
				items[i] = service.createPieChartItem(chartNode, "item" + i);
			} catch (SplashDatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			items[i].setPieChartItemCategory((String) c.getValue());
			items[i].setPieChartItemValue((String) ((Cell) table.getValueAt(lastRow, i)).getValue());
		}
		IEditorInput editorInput = new PieChartEditorInput(file);
		((PieChartEditorInput) editorInput).setPieChartName(chartName);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(editorInput, NeoSplashUtil.AMANZI_NEO4J_SPLASH_PIE_CHART_EDITOR);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
