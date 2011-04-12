package org.amanzi.awe.views.reuse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.awe.views.reuse.range.Bar;
import org.amanzi.awe.views.reuse.range.RangeModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.utils.Pair;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.geotools.util.NumberRange;

// TODO: Auto-generated Javadoc
/**
 * The Class DistributionPreferences.
 */
public class DistributionPreferences extends PreferencePage implements IWorkbenchPreferencePage {
    public static Logger LOG = Logger.getLogger(DistributionPreferences.class);

    /** The content. */
    private Composite content;

    /** The viewer. */
    private TableViewer viewer;

    /** The range name. */
    private Combo rangeName;

    /** The save. */
    private Button save;

    /** The cancel. */
    private Button cancel;

    /** The model. */
    private RangeModel model;

    /** The models. */
    private Map<String, RangeModel> models = new HashMap<String, RangeModel>();

    /**
     * Instantiates a new distribution preferences.
     */
    public DistributionPreferences() {
        super();
    }

    /**
     * Inits the.
     * 
     * @param workbench the workbench
     */
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(ReusePlugin.getDefault().getPreferenceStore());
        setTitle("Create custom range for distribution view");
    }

    /**
     * Creates the contents.
     * 
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        content = new Composite(parent, SWT.NULL);
        GridLayout mainLayout = new GridLayout(6, false);
        content.setLayout(mainLayout);
        Label label = new Label(content, SWT.NONE);
        label.setText("Custom range name: ");
        rangeName = new Combo(content, SWT.BORDER);
        rangeName.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeModel();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                changeModel();
            }
        });
        formRangeList();
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        rangeName.setLayoutData(layoutData);
        label = new Label(content, SWT.NONE);
        label.setText("Number of bar");
        Spinner spin = new Spinner(content, SWT.BORDER);
        spin.setMinimum(1);
        spin.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                changeModelLen(((Spinner)e.widget).getSelection());

            }
        });

        save = new Button(content, SWT.PUSH);
        save.setText("Save");
        save.setEnabled(false);
        save.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                saveModel();
                validate();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cancel = new Button(content, SWT.PUSH);
        cancel.setText("Cancel");
        cancel.setEnabled(false);
        viewer = new TableViewer(content, SWT.FULL_SELECTION);
        formColumns();
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 4);
        viewer.getControl().setLayoutData(layoutData);
        viewer.getTable().addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                Table table = (Table)e.widget;
                int width = table.getClientArea().width;
                width = (width - table.getColumn(2).getWidth() - 3) / 2;
                table.getColumn(0).setWidth(width);
                table.getColumn(1).setWidth(width);
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });
        viewer.setContentProvider(new ContentProvider());
        viewer.setInput(model);
        validate();
        return content;
    }

    @Override
    protected void performApply() {
        saveModel();
        super.performApply();
    }
    /**
     * Form range list.
     */
    private void formRangeList() {
        loadModelList();
        rangeName.setItems(models.keySet().toArray(new String[0]));
        createDefModel();
    }


    /**
     * Load model list.
     */
    @SuppressWarnings("unchecked")
    private void loadModelList() {
        models.clear();
        String modelStr = ReusePlugin.getDefault().getPreferenceStore().getString(PreferenceInitializer.RV_MODELS);
        if (!modelStr.isEmpty()) {
            ByteArrayInputStream bin = new ByteArrayInputStream(modelStr.getBytes());
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(new BufferedInputStream(bin));
                Object object = in.readObject();
                in.close();
                models.putAll((Map< ? extends String, ? extends RangeModel>)object);
            } catch (IOException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (ClassNotFoundException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }



    /**
     * Change model.
     */
    protected void changeModel() {
        String newName = rangeName.getText().trim();
        RangeModel md = models.get(newName);
        if (md == null || md == model) {
            model.setName(newName);
            if (md != null) {
                changeModelName(newName);
            }
            return;
        }
        if (model.isChanged()) {
                setMessage("Please save/cancel current changed model");
                rangeName.setText(model.getName());
                return;
        } else {
            model = md;
            if (model == null) {
                createDefModel();
            }
            viewer.setInput(model);
            validate();
        }
    }

    /**
     * @param newName
     */
    public void changeModelName(String newName) {
        models.values().remove(model);
        model.setName(newName);
        models.put(newName, model);
        rangeName.setItems(models.keySet().toArray(new String[0]));
        rangeName.setText(newName);
    }

    /**
     * Creates the default model.
     */
    private void createDefModel() {
        model = new RangeModel(rangeName.getText().trim());
        model.setSize(1);
    }


    /**
     * Save model.
     */
    protected void saveModel() {
        if (!model.isChanged()) {
            return;
        }
        clearChartsFromDB(model.getName());
        String newName = rangeName.getText().trim();
        RangeModel md = models.get(newName);
        if (md != null && md != model) {
            setMessage("This name already exist", DialogPage.ERROR);
            rangeName.setFocus();
            return;
        }
        changeModelName(newName);
        if (StringUtils.isEmpty(model.getName())) {
            setMessage("Please enter the model name", DialogPage.ERROR);
            rangeName.setFocus();
            return;
        }
        Pair<Boolean, String> res = model.validate();
        if (res.getLeft()) {
            models.put(newName, model);
            model.setChanged(false);
            storeAll();
            validate();
        } else {
            setMessage(res.getRight(), DialogPage.ERROR);
        }
    }

    /**
     * clear all created charts with this model if model was changed
     * 
     * @param modelName
     */
    private void clearChartsFromDB(final String modelName) {
        Job job=new Job("Delete charts"){

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                NeoServiceFactory.getInstance().getDatasetService().deleteAggregateCharts(INeoConstants.PROPERTY_DISTRIBUTE_NAME, modelName);
                return Status.OK_STATUS;
            }
            
        };
        job.schedule();
    }

    /**
     *
     */
    private void storeAll() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(bout));
            out.writeObject(models);
            out.close();
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        String value = new String(bout.toByteArray());
        LOG.debug("Store size " + value.length());
        getPreferenceStore().setValue(PreferenceInitializer.RV_MODELS, value);
    }

    /**
     * Change model len.
     * 
     * @param newSize the new size
     */
    protected void changeModelLen(int newSize) {
        if (model.getSize() == newSize) {
            return;
        }
        model.setSize(newSize);
        viewer.setInput(model);
        validate();
    }

    /**
     * Validate.
     */
    public void validate() {
        updateButtons();
        if (model.isChanged()) {
            setMessage("Please save/cancel current model");
            setValid(false);
        }
        setMessage("");
        setValid(true);
    }


    /**
     * Update buttons.
     */
    private void updateButtons() {
        boolean res = model.isChanged();
        save.setEnabled(res);
        cancel.setEnabled(res);
    }


    /**
     * Form columns.
     */
    private void formColumns() {
        Table table = viewer.getTable();
        TableViewerColumn column;
        TableColumn col;

        column = new TableViewerColumn(viewer, SWT.CENTER);
        col = column.getColumn();
        col.setText("Range");
        col.setWidth(100);
        col.setResizable(true);
        col.setToolTipText("Format [from, to], use - or + for infinity values: [-,10) or [10,+]");
        column.setLabelProvider(new LabelProvider(0));
        column.setEditingSupport(new Editing(viewer, 0));

        column = new TableViewerColumn(viewer, SWT.CENTER);
        col = column.getColumn();
        col.setText("Bar name");
        col.setWidth(100);
        col.setResizable(true);
        column.setLabelProvider(new LabelProvider(1));
        column.setEditingSupport(new Editing(viewer, 1));

        column = new TableViewerColumn(viewer, SWT.CENTER);
        col = column.getColumn();
        col.setText("Bar color");
        col.setWidth(100);
        col.setResizable(true);
        col.setToolTipText("Select Bar color. White color mean, that current bar has default color.");
        column.setLabelProvider(new LabelProvider(2));
        column.setEditingSupport(new Editing(viewer, 2));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.refresh();
    }

    /**
     * The Class LabelProvider.
     */
    public class LabelProvider extends ColumnLabelProvider {

        /** The column index. */
        private final int columnIndex;

        /** The not valid. */
        private Color notValid = new Color(null, 255, 215, 186);

        /**
         * Instantiates a new label provider.
         * 
         * @param columnIndex the column index
         */
        public LabelProvider(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        /**
         * Dispose.
         */
        @Override
        public void dispose() {
            notValid.dispose();
            super.dispose();
        }

        /**
         * Gets the text.
         * 
         * @param element the element
         * @return the text
         */
        @Override
        public String getText(Object element) {
            Bar bar = (Bar)element;
            if (columnIndex == 2) {
                return "";
            } else if (columnIndex == 0) {
                return bar.getRangeAsStr(null);
            } else {
                return bar.getDefaultName();
            }
        }

        /**
         * Gets the background.
         * 
         * @param element the element
         * @return the background
         */
        @Override
        public Color getBackground(Object element) {
            Bar bar = (Bar)element;
            if (columnIndex == 2) {
                return new Color(viewer.getControl().getShell().getDisplay(), bar.getDefaultRGB());
            } else if (columnIndex == 0 && !bar.isValid()) {
                return notValid;
            }
            return super.getBackground(element);
        }

    }

    /**
     * The Class ContentProvider.
     */
    public static class ContentProvider implements IStructuredContentProvider {

        /** The model. */
        RangeModel model;

        /**
         * Dispose.
         */
        @Override
        public void dispose() {
        }

        /**
         * Input changed.
         * 
         * @param viewer the viewer
         * @param oldInput the old input
         * @param newInput the new input
         */
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            model = (RangeModel)newInput;
        }

        /**
         * Gets the elements.
         * 
         * @param inputElement the input element
         * @return the elements
         */
        @Override
        public Object[] getElements(Object inputElement) {
            return model != null ? model.getBars() : null;
        }

    }

    /**
     * The Class Editing.
     */
    public class Editing extends EditingSupport {

        /** The pt. */
        Pattern pt = Pattern
.compile("(^.*)([\\[\\(]{1})([^0-9\\-]*)([\\-\\d\\.]+)([^0-9\\-\\+]+)([\\+\\-\\d\\.]+)([\\]\\)]{1})(.*$)");

        /** The column index. */
        private final int columnIndex;

        /** The editor. */
        private CellEditor editor;

        /**
         * Instantiates a new editing.
         * 
         * @param viewer the viewer
         * @param columnIndex the column index
         */
        public Editing(TableViewer viewer, int columnIndex) {
            super(viewer);
            this.columnIndex = columnIndex;
            if (columnIndex == 2) {
                editor = new ColorCellEditor(viewer.getTable());
            } else {
                editor = new TextCellEditor(viewer.getTable());
            }
        }

        /**
         * Gets the cell editor.
         * 
         * @param element the element
         * @return the cell editor
         */
        @Override
        protected CellEditor getCellEditor(Object element) {
            return editor;
        }

        /**
         * Can edit.
         * 
         * @param element the element
         * @return true, if successful
         */
        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        /**
         * Gets the value.
         * 
         * @param element the element
         * @return the value
         */
        @Override
        protected Object getValue(Object element) {
            Bar bar = (Bar)element;
            if (columnIndex == 2) {
                return bar.getDefaultRGB();
            } else if (columnIndex == 1) {
                return bar.getDefaultName();
            }
            return bar.getRangeAsStr("");
        }

        /**
         * Sets the value.
         * 
         * @param element the element
         * @param value the value
         */
        @Override
        protected void setValue(Object element, Object value) {
            Bar bar = (Bar)element;
            if (columnIndex == 2) {
                if (!ObjectUtils.equals(value, bar.getDefaultRGB())) {
                    bar.setColor((RGB)value);
                }
            } else {
                String vl = (String)value;
                if (vl.isEmpty()) {
                    vl = null;
                }
                if (columnIndex == 1) {
                    if (!ObjectUtils.equals(vl, bar.getDefaultName())) {
                        bar.setName(vl);
                    }
                } else {
                    if (!ObjectUtils.equals(vl, bar.getRangeAsStr(null))) {
                        if (vl != null) {
                            try {
                                Matcher matcher = pt.matcher(vl);
                                if (matcher.matches()) {
                                    String g1 = matcher.group(2);
                                    String num1 = matcher.group(4);
                                    String num2 = matcher.group(6);
                                    String g2 = matcher.group(7);
                                    Double v1 = "-".equals(num1.trim()) ? Double.NEGATIVE_INFINITY : Double.valueOf(num1);
                                    Double v2 = "+".equals(num2.trim()) ? Double.POSITIVE_INFINITY : Double.valueOf(num2);
                                    boolean v1Incl = "[".equals(g1);
                                    boolean v2Incl = "]".equals(g2);
                                    NumberRange range;
                                    if (v1 <= v2) {
                                        range = new NumberRange(v1, v1Incl, v2, v2Incl);
                                    } else {
                                        range = new NumberRange(v2, v2Incl, v1, v1Incl);
                                    }
                                    bar.setRange(range);
                                }
                            } catch (Exception e) {
                                // do nothing;
                            }
                        } else {
                            bar.setRange(null);
                        }
                    }
                }
                getViewer().update(element, null);
                validate();
            }
        }
    }

    /**
     * The main method.
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {
        Pattern pt = Pattern
.compile("(^.*)([\\[\\(]{1})([^0-9\\-]*)([\\-\\d\\.]+)([^0-9\\-\\+]+)([\\+\\-\\d\\.]+)([\\]\\)]{1})(.*$)");
        Matcher matcher = pt.matcher("(40.0, 50.0]");
        System.out.println(matcher.matches());
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(4));
        System.out.println(matcher.group(6));
        System.out.println(matcher.group(7));
        NumberRange r = new NumberRange(9, 10);
        System.out.println(r.getMinimum());
        System.out.println(r.contains((Number)new Double(9.5)));
    }
}
