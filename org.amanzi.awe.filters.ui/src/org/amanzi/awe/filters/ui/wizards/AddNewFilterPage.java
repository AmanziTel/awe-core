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

package org.amanzi.awe.filters.ui.wizards;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.ExpressionType;
import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.FilterType;
import org.amanzi.neo.services.filters.IFilter;
import org.amanzi.neo.services.filters.INamedFilter;
import org.amanzi.neo.services.filters.NamedFilter;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * page for adding new filters to dataset
 * 
 * @author Vladislav_Kondratenko
 */
public class AddNewFilterPage {
    /*
     * constants
     */
    private final static String FILTER_NAME = "filterName";
    private final static String LAYER_NAME = "layerName";
    private final static String EXPESSION_TYPE = "expressionType";
    private final static String FILTER_TYPE = "filterType";
    private final static String NODE_TYPES = "nodeTypes";
    private final static String VALUE = "value";
    private final static String PROPERTY_NAME = "properyName";
    private final static String SPACE_SEPARATOR = " ";
    private final static String SEPARATOR_WORD_FROM = " from ";
    /*
     * labels
     */
    private final String GIS_NAME_LABEL = "Enter gis name";
    private static final String ENTER_VALUE_LABEL = "Enter Value";
    private static final String SELECT_FILTER_TYPE_LABEL = "Select filter Type";
    private static final String ENTER_FILTER_NAME_LABEL = "Enter filter name";
    private static final String SELECT_PROPERTY_LABEL = "Select property";
    private static final String SELECT_NODE_TYPE_LABEL = "Select Node type";
    private static final String SELECT_EXPRESSION_TYPE_LABEL = "Select expression Type";
    private static final String FILTER_DESCRIPTION_LABEL = "Filter description";
    private static final String SAVE_LABEL = "Save";
    private static final String CANCEL_LABEL = "Cancel";
    private static final String ADD_UNDERLINE_LABEL = "ADD underline";

    // container for property
    private Map<String, String> propCollector;
    // parent shell
    private final Shell parentShell;
    // Usable model
    private IRenderableModel model;
    // main composite
    private Composite main;
    // filter description composite
    private Composite filterDescriptionComposite;
    private Composite controlComposite;
    // current filter;
    private INamedFilter namedFilter;
    // current underline filter;
    private IFilter underlineFilter;
    private String tDescriptionContent = StringUtils.EMPTY;
    // current shell
    private Shell shell;
    /*
     * shell elements
     */
    private Text tFilterName;
    private Text tLayerName;
    private Text tValue;
    private Text tDescription;
    private Button btnAddUnderline;
    private Button btnSave;
    private Button btnCancel;
    private Combo cbExpression;
    private Combo cbFilterType;
    private Combo cbNodeTypes;
    private Combo cbPropertiesNames;

    public AddNewFilterPage(Shell parentShell, IRenderableModel model) {
        shell = new Shell(parentShell, SWT.SHELL_TRIM);
        this.model = model;
        this.parentShell = parentShell;
        initPropCollector();
        createControl();
    }

    /**
     * initialize property collector with default values
     */
    private void initPropCollector() {
        propCollector = new HashMap<String, String>();
        propCollector.put(LAYER_NAME, StringUtils.EMPTY);
        propCollector.put(NODE_TYPES, StringUtils.EMPTY);
        propCollector.put(FILTER_NAME, StringUtils.EMPTY);
        propCollector.put(FILTER_TYPE, StringUtils.EMPTY);
        propCollector.put(EXPESSION_TYPE, StringUtils.EMPTY);
        propCollector.put(VALUE, StringUtils.EMPTY);
        propCollector.put(PROPERTY_NAME, StringUtils.EMPTY);
    }

    /**
     * create shells elements
     */
    public void createControl() {
        createComposites();
        GridLayout rootLayout = new GridLayout(1, false);
        GridData gridData = new GridData(SWT.FILL);
        shell.setLayout(rootLayout);
        shell.setLayoutData(gridData);

        createLayerNameTextField();
        createFilterNameTextField();
        createFilterDescriptionTextField();

        createFilterTypeCombobox();
        createExpressionCombobox();
        createNodeTypeCombobox();
        createPropertiesCombobox();
        createValueTextField();
        createBtnControl();

        tFilterName.setEnabled(false);
        btnAddUnderline.setEnabled(false);
        btnSave.setEnabled(false);
        tValue.setEnabled(false);
        tDescription.setEnabled(true);

        setConditionForFilterFields(false);

        addListeners();
        shell.pack();
        shell.open();
        handleReaction(FILTER_TYPE, cbFilterType);
        handleReaction(EXPESSION_TYPE, cbExpression);
    }

    /**
     * create group's layout
     */
    private void createComposites() {
        main = new Group(shell, SWT.FILL);
        main.setLayout(new GridLayout(2, false));
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        filterDescriptionComposite = new Group(shell, SWT.FILL);
        filterDescriptionComposite.setLayout(new GridLayout(2, false));
        filterDescriptionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        controlComposite = new Composite(shell, SWT.FILL);
        controlComposite.setLayout(new GridLayout(3, true));
        controlComposite.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, true, true));
    }

    /**
     * create btns ok cancel addUnderline
     */
    private void createBtnControl() {
        GridData rootLayoutData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
        btnCancel = new Button(controlComposite, SWT.LEFT);
        btnCancel.setText(CANCEL_LABEL);
        btnAddUnderline = new Button(controlComposite, SWT.LEFT);
        btnAddUnderline.setText(ADD_UNDERLINE_LABEL);
        btnSave = new Button(controlComposite, SWT.LEFT);
        btnSave.setText(SAVE_LABEL);
        btnCancel.setLayoutData(rootLayoutData);
        rootLayoutData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
        btnSave.setLayoutData(rootLayoutData);
        rootLayoutData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
        btnAddUnderline.setLayoutData(rootLayoutData);
    }

    /**
     * create text field for enter filter name
     */
    private void createFilterNameTextField() {
        Label label = new Label(main, SWT.LEFT);
        label.setText(ENTER_FILTER_NAME_LABEL);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
        tFilterName = new Text(main, SWT.BORDER);
        tFilterName.setLayoutData(rootLayoutData);
    }

    /**
     * create text field for enter layer name filter belong
     */
    private void createLayerNameTextField() {
        Label label = new Label(main, SWT.LEFT);
        label.setText(GIS_NAME_LABEL);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
        tLayerName = new Text(main, SWT.BORDER);
        tLayerName.setLayoutData(rootLayoutData);
    }

    /**
     * create combobox which allow to select any possible filter types
     */
    private void createFilterTypeCombobox() {
        Label label = new Label(filterDescriptionComposite, SWT.LEFT);
        label.setText(SELECT_FILTER_TYPE_LABEL);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        FilterType[] filterArray = FilterType.values();
        cbFilterType = new Combo(filterDescriptionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbFilterType.setLayoutData(rootLayoutData);
        for (FilterType type : filterArray) {
            cbFilterType.add(type.name());
        }
        cbFilterType.select(0);
    }

    /**
     * create text field for Filter Value set
     */
    private void createValueTextField() {
        Label label = new Label(filterDescriptionComposite, SWT.LEFT);
        label.setText(ENTER_VALUE_LABEL);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        tValue = new Text(filterDescriptionComposite, SWT.BORDER);
        tValue.setLayoutData(rootLayoutData);
    }

    /**
     * create combobox which allow to select necessary node type for filter; this method also
     * consist handling of combobox element selection
     */
    private void createNodeTypeCombobox() {
        List<INodeType> nodeTypeList = new LinkedList<INodeType>();
        Label label = new Label(filterDescriptionComposite, SWT.LEFT);
        label.setText(SELECT_NODE_TYPE_LABEL);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        cbNodeTypes = new Combo(filterDescriptionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbNodeTypes.setLayoutData(rootLayoutData);
        if (model instanceof INetworkModel) {
            nodeTypeList = ((INetworkModel)model).getNetworkStructure();
        } else if (model instanceof IDriveModel) {
            nodeTypeList.add(((IDriveModel)model).getPrimaryType());
        }
        for (INodeType typeItem : nodeTypeList) {
            cbNodeTypes.add(typeItem.getId());
        }
    }

    /**
     * create combobox which store info about possible expression types
     */
    private void createExpressionCombobox() {
        Label label = new Label(filterDescriptionComposite, SWT.LEFT);
        label.setText(SELECT_EXPRESSION_TYPE_LABEL);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        ExpressionType[] expressionArray = ExpressionType.values();
        cbExpression = new Combo(filterDescriptionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbExpression.setLayoutData(rootLayoutData);
        for (ExpressionType type : expressionArray) {
            cbExpression.add(type.name());
        }
        cbExpression.select(0);
    }

    /**
     * create field which show info about created filters
     */
    private void createFilterDescriptionTextField() {
        Label label = new Label(main, SWT.LEFT);
        label.setText(FILTER_DESCRIPTION_LABEL);
        GridData gridData = new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 2);
        label.setLayoutData(gridData);
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
        rootLayoutData.minimumHeight = 100;
        tDescription = new Text(main, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        tDescription.setLayoutData(rootLayoutData);
        tDescription.setEditable(true);
    }

    /**
     * create combobox to select properties
     */
    private void createPropertiesCombobox() {
        Label label = new Label(filterDescriptionComposite, SWT.LEFT);
        label.setText(SELECT_PROPERTY_LABEL);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        cbPropertiesNames = new Combo(filterDescriptionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbPropertiesNames.setLayoutData(rootLayoutData);
        cbPropertiesNames.setEnabled(false);

    }

    /**
     * add listeners to created shell elements
     */
    private void addListeners() {

        /*
         * listeners for buttons
         */
        btnAddUnderline.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                addFilter();
            }
        });
        btnSave.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                saveFilter();
            }
        });
        btnCancel.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                close();
            }
        });
        /*
         * listeners to text fields
         */
        tLayerName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateLayerName();
            }
        });
        tFilterName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateFilterName();
            }
        });
        tValue.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                handleReaction(VALUE, tValue);
            }
        });
        /*
         * listeners for comboboxes
         */
        cbFilterType.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleReaction(FILTER_TYPE, cbFilterType);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cbPropertiesNames.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleReaction(PROPERTY_NAME, cbPropertiesNames);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        cbNodeTypes.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleReaction(NODE_TYPES, cbNodeTypes);
                updatecbProperties();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        cbExpression.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                handleReaction(EXPESSION_TYPE, cbExpression);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

    }

    /**
     * save filter chain to database
     */
    protected void saveFilter() {
        addFilter();
        namedFilter.addFilter(underlineFilter);
        model.addLayer(propCollector.get(LAYER_NAME), namedFilter);
        close();
    }

    /**
     * create new INamedFilter filter if its first filter. else add underline IFilter to already
     * exists INamedFilter filter
     */
    protected void addFilter() {
        IFilter curFilter;
        if (namedFilter == null) {
            namedFilter = new NamedFilter(propCollector.get(FILTER_NAME));
            // disable layer and filter name for the following filters
            tLayerName.setEnabled(false);
            tFilterName.setEnabled(false);
            curFilter = namedFilter;
        } else if (underlineFilter == null) {
            underlineFilter = new Filter(ExpressionType.getByName(propCollector.get(EXPESSION_TYPE)));
            namedFilter.addFilter(underlineFilter);
            curFilter = underlineFilter;
        } else {
            curFilter = new Filter(ExpressionType.getByName(propCollector.get(EXPESSION_TYPE)));
            IFilter lasUnderline = getLastUnderlineFilter(underlineFilter);
            lasUnderline.addFilter(curFilter);
        }
        setFilterValues(curFilter);
        // also collect description
        tDescriptionContent = tDescription.getText();
        tDescriptionContent += SPACE_SEPARATOR + propCollector.get(EXPESSION_TYPE) + SPACE_SEPARATOR + "\n";
        updateDescriptionField();
        tValue.setText(StringUtils.EMPTY);
    }

    /**
     * return last underline filter
     * 
     * @param underlineFilter2
     * @return
     */
    private IFilter getLastUnderlineFilter(IFilter underlineFilter) {
        if (underlineFilter.getUnderlyingFilter() == null) {
            return underlineFilter;
        }
        return getLastUnderlineFilter(underlineFilter.getUnderlyingFilter());
    }

    /**
     * set common values for both type of filters;
     * 
     * @param filter
     */
    private void setFilterValues(IFilter filter) {
        filter.setExpression(NodeTypeManager.getType(propCollector.get(NODE_TYPES)), propCollector.get(PROPERTY_NAME),
                (Serializable)propCollector.get(VALUE));
    }

    /**
     * describe reaction for filter
     */
    protected void updateLayerName() {
        String layerName = tLayerName.getText();
        handleReaction(LAYER_NAME, tLayerName);
        if (!layerName.isEmpty() && !tFilterName.isEnabled()) {
            tFilterName.setEnabled(true);
        }
    }

    /**
     * update filter name
     */
    protected void updateFilterName() {
        handleReaction(FILTER_NAME, tFilterName);
        if (!((String)propCollector.get(FILTER_NAME)).isEmpty()) {
            setConditionForFilterFields(true);
        } else {
            setConditionForFilterFields(false);
        }
    }

    /**
     * update properties combobox. properties combobox should be available only when node type
     * selected
     */
    private void updatecbProperties() {
        if (cbNodeTypes.getSelectionIndex() < 0) {
            cbPropertiesNames.setEnabled(false);
            propCollector.put(VALUE, StringUtils.EMPTY);
            tValue.setText(StringUtils.EMPTY);
            tValue.setEnabled(false);
            return;
        }
        if (model instanceof IPropertyStatisticalModel) {
            IPropertyStatisticalModel castedModel = (IPropertyStatisticalModel)model;
            String selectedNodeType = cbNodeTypes.getItem(cbNodeTypes.getSelectionIndex()).toLowerCase();
            String[] propertyNames = castedModel.getAllPropertyNames(NodeTypeManager.getType(selectedNodeType));
            cbPropertiesNames.setItems(propertyNames);
            cbPropertiesNames.setEnabled(true);
        }
        if (cbPropertiesNames.getItemCount() <= 0) {
            tValue.setEnabled(false);
            tValue.setText(StringUtils.EMPTY);
        } else {
            cbPropertiesNames.select(0);
            tValue.setEnabled(true);
        }
        handleReaction(PROPERTY_NAME, cbPropertiesNames);
    }

    /**
     * update button condition
     */
    private void updateBtnControll() {
        boolean isCorrect = true;
        for (String key : propCollector.keySet()) {
            if (propCollector.get(key).isEmpty()) {
                isCorrect = false;
                break;
            }
        }

        btnSave.setEnabled(isCorrect);
        btnAddUnderline.setEnabled(isCorrect);
    }

    /**
     * handle reaction when new item selected in combobox
     * 
     * @param name name of property need to store in property collector
     * @param combobox handled combobox
     */
    private void handleReaction(String name, Combo combobox) {
        int selectionIndex = combobox.getSelectionIndex();
        if (selectionIndex < 0) {
            propCollector.put(name, StringUtils.EMPTY);
        } else {
            propCollector.put(name, combobox.getItem(combobox.getSelectionIndex()));
        }

        updateDescriptionField();
        updateBtnControll();
    }

    private void handleReaction(String name, Text textField) {
        propCollector.put(name, textField.getText());
        updateDescriptionField();
        updateBtnControll();
    }

    /**
     * response for content of description field
     */
    private void updateDescriptionField() {
        String newFilterDescription = collectDescriptionFromMap();
        tDescription.setText(tDescriptionContent + newFilterDescription);
    }

    /**
     * set enabled conditions for next elements : cbFilterType,cbNodeTypes,tValue,cbExpression
     * 
     * @param isEnabled
     */
    private void setConditionForFilterFields(boolean isEnabled) {
        cbFilterType.setEnabled(isEnabled);
        cbExpression.setEnabled(isEnabled);
        cbNodeTypes.setEnabled(isEnabled);
    }

    /**
     * collect description of current filter from property collector map
     * 
     * @param tDescriptionContent
     */
    private String collectDescriptionFromMap() {
        String description = StringUtils.EMPTY;
        String propName = propCollector.get(PROPERTY_NAME);
        String nodeType = propCollector.get(NODE_TYPES);
        String value = propCollector.get(VALUE);
        description = propName + SEPARATOR_WORD_FROM + nodeType + SPACE_SEPARATOR + propCollector.get(FILTER_TYPE)
                + SPACE_SEPARATOR + value;
        return description;
    }

    /**
     * close current shell and update parent
     */
    private void close() {
        parentShell.notifyListeners(SWT.CHANGED, null);
        shell.close();
        shell.dispose();
    }
}
