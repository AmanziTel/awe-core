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

package org.amanzi.statistics.view.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.amanzi.awe.statistics.functions.AggregationFunctions;
import org.amanzi.awe.statistics.template.Condition;
import org.amanzi.awe.statistics.template.Units;

/**
 * An abstract parent class for all KPI viewers specific for different KPI types. Has controls for
 * KPI name, unit, threshold, condition and aggregation function.
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public abstract class KPIViewer extends Composite {
    private static final String NAME = "Name:";
    private static final String UNIT = "Unit:";
    private static final String THRESHOLD = "Threshold:";
    private static final String CONDITION = "Condition:";
    private static final String AGGREGATION = "Aggregation:";
    private Composite parent;
    private int style;
    private Text txtName;
    private Combo cmbUnit;
    private Text txtThreshold;
    private Combo cmbCondition;
    private Combo cmbAggregation;
    private Composite container;

    // private

    /**
     * Constructor
     * 
     * @param parent
     * @param style
     */
    public KPIViewer(Composite parent, int style) {
        super(parent, style);
        this.parent = parent;
        this.style = style;
        createComposite();
    }

    private void createComposite() {
        container = new Composite(parent, style | SWT.BORDER);
        container.setLayout(new FormLayout());

        // Name
        Label lblName = new Label(container, SWT.LEFT);
        lblName.setText(NAME);

        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(20, 0);
        lblName.setLayoutData(formData);

        txtName = new Text(container, SWT.SINGLE | SWT.LEFT | SWT.BORDER);

        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(lblName, 5);
        formData.right = new FormAttachment(100, -5);
        txtName.setLayoutData(formData);

        // Specific to KPI type controls
        Composite composite = addControls(container);

        formData = new FormData();
        formData.top = new FormAttachment(txtName, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        composite.setLayoutData(formData);

         // Unit
         Label lblUnit = new Label(container, SWT.LEFT);
         lblUnit.setText(UNIT);
        
         formData = new FormData();
         // formData.top = new FormAttachment(txtName, 5);
         formData.top = new FormAttachment(composite, 5);
         formData.left = new FormAttachment(0, 5);
         formData.right = new FormAttachment(20, 0);
         lblUnit.setLayoutData(formData);
        
         cmbUnit = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN);
        
         formData = new FormData();
         // formData.top = new FormAttachment(txtName, 5);
         formData.top = new FormAttachment(composite, 5);
         formData.left = new FormAttachment(lblUnit, 5);
         formData.right = new FormAttachment(100, -5);
         cmbUnit.setLayoutData(formData);
        
         // Threshold
         Label lblThreshold = new Label(container, SWT.LEFT);
         lblThreshold.setText(THRESHOLD);
        
         formData = new FormData();
         formData.top = new FormAttachment(cmbUnit, 5);
         formData.left = new FormAttachment(0, 5);
         formData.right = new FormAttachment(20, 0);
         lblThreshold.setLayoutData(formData);
        
         txtThreshold = new Text(container, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        
         formData = new FormData();
         formData.top = new FormAttachment(cmbUnit, 5);
         formData.left = new FormAttachment(lblThreshold, 5);
         formData.right = new FormAttachment(100, -5);
         txtThreshold.setLayoutData(formData);
        
         // Condition
         Label lblCondition = new Label(container, SWT.LEFT);
         lblCondition.setText(CONDITION);
        
         formData = new FormData();
         formData.top = new FormAttachment(txtThreshold, 5);
         formData.left = new FormAttachment(0, 5);
         formData.right = new FormAttachment(20, 0);
         lblCondition.setLayoutData(formData);
        
         cmbCondition = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN);
        
         formData = new FormData();
         formData.top = new FormAttachment(txtThreshold, 5);
         formData.left = new FormAttachment(lblCondition, 5);
         formData.right = new FormAttachment(100, -5);
         cmbCondition.setLayoutData(formData);
        
         // Aggregation
         Label lblAggregation = new Label(container, SWT.LEFT);
         lblAggregation.setText(AGGREGATION);
        
         formData = new FormData();
         formData.top = new FormAttachment(cmbCondition, 5);
         formData.left = new FormAttachment(0, 5);
         formData.right = new FormAttachment(20, 0);
         lblAggregation.setLayoutData(formData);
        
         cmbAggregation = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN);
        
         formData = new FormData();
         formData.top = new FormAttachment(cmbCondition, 5);
         formData.left = new FormAttachment(lblAggregation, 5);
         formData.right = new FormAttachment(100, -5);
         cmbAggregation.setLayoutData(formData);
        
         init();

    }

    private void init() {
        // Units
        for (Units unit : Units.values()) {
            cmbUnit.add(unit.getText());
        }
        cmbUnit.setText(cmbUnit.getItem(0));
        // Conditions
        for (Condition condition : Condition.values()) {
            cmbCondition.add(condition.getText());
        }
        cmbCondition.setText(cmbCondition.getItem(0));
        // Aggregation functions
        for (AggregationFunctions func : AggregationFunctions.values()) {
            cmbAggregation.add(func.getFunctionName());
        }
        cmbAggregation.setText(cmbAggregation.getItem(0));
        // Initialize KPI type specific controls
//         initControls();
    }

    public abstract Composite addControls(Composite parent);

    public abstract void initControls();

    public abstract void addListeners();

    /**
     * @return Returns the txtName.
     */
    public Text getNameControl() {
        return txtName;
    }

    /**
     * @return Returns the cmbUnit.
     */
    public Combo getUnitControl() {
        return cmbUnit;
    }

    /**
     * @return Returns the txtThreshold.
     */
    public Text getThresholdControl() {
        return txtThreshold;
    }

    /**
     * @return Returns the cmbCondition.
     */
    public Combo getConditionControl() {
        return cmbCondition;
    }

    /**
     * @return Returns the cmbAggregation.
     */
    public Combo getAggregationControl() {
        return cmbAggregation;
    }

    /**
     * @return Returns the container.
     */
    public Composite getContainer() {
        return container;
    }

}
