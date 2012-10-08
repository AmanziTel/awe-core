/**
 * 
 */
package org.amanzi.awe.neostyle.drive;

import java.util.Set;

import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;

import org.amanzi.awe.neostyle.Messages;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Bondoronok_p
 */
public class DriveStyleDefiner extends ViewPart {

    // TODO write save for label property in memento
    private static final String[] FONT_SIZE_ARRAY = new String[] {"8", "9", "10", "11", "12", "14", "16", "18", "20", "24"}; //$NON-NLS-1$

    private Label labelFill;
    private Label labelLabel;
    private Label labelLine;
    private ColorEditor colorEdFill;
    private ColorEditor colorEdLine;
    private ColorEditor colorEdLabel;
    private Label labelFillLocation;
    private Combo locationLabelFontSize;
    private Combo locationLabelTypeCombo;
    private Label labelFontSize;
    private Label locationLabelTypeLabel;
    private Label measurementLabelPropertyLabel;
    private Combo measurementLabelPropertyCombo;
    private Group labelsGroup;

    private DriveStyle currentStyle;
    private IDriveModel model;

    /**
     * The Default Constructor
     */
    public DriveStyleDefiner() {
    }

    /**
     * The constructor
     * 
     * @param driveStyle Drive Style
     */
    public DriveStyleDefiner(final DriveStyle driveStyle) {
        this.currentStyle = driveStyle;
    }

    public DriveStyle getCurrentStyle() {
        return currentStyle;
    }

    public void setCurrentStyle(final DriveStyle driveStyle) {
        this.currentStyle = driveStyle;
    }

    public void refresh() {
        colorEdFill.setColorValue(rgbFromColor(currentStyle.getLocationColor()));
        colorEdLabel.setColorValue(rgbFromColor(currentStyle.getLabelColor()));
        colorEdLine.setColorValue(rgbFromColor(currentStyle.getLineColor()));
        locationLabelTypeCombo.setText(currentStyle.getLocationLabelType());

        if (DriveStyleContent.MEASUREMENT.equals(currentStyle.getLocationLabelType())) {
            fillMeasurementsPropertiesCombo();
            measurementLabelPropertyCombo.setText(currentStyle.getMeasurementNameProperty());
        }
        locationLabelFontSize.setText(String.valueOf(currentStyle.getFontSize()));
    }

    public void preApply() {
        currentStyle.setFontSize(getLocationLabelFontSize());
        currentStyle.setLabelColor(colorFromRGB(colorEdLabel.getColorValue()));
        currentStyle.setLineColor(colorFromRGB(colorEdLine.getColorValue()));
        currentStyle.setLocationColor(colorFromRGB(colorEdFill.getColorValue()));
        currentStyle.setMeasurementNameProperty(measurementLabelPropertyCombo.getText());
        currentStyle.setLocationLabelType(locationLabelTypeCombo.getText());
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets .Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {
        Composite pMain = parent;
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        pMain.setLayout(layout);
        Group xGroup = new Group(pMain, SWT.NONE);
        xGroup.setText("Colors"); //$NON-NLS-1$
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(90, -10);
        xGroup.setLayout(new FormLayout());
        xGroup.setLayoutData(formData);

        labelFill = new Label(xGroup, SWT.NONE);
        labelFill.setText(Messages.Location_color);
        colorEdFill = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(2);
        labelFill.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        colorEdFill.getButton().setLayoutData(formData);

        labelLine = new Label(xGroup, SWT.NONE);
        labelLine.setText(Messages.Color_Line);
        colorEdLine = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(labelFillLocation, 40);
        formData.left = new FormAttachment(2);
        labelLine.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(labelFillLocation, 35);
        colorEdLine.getButton().setLayoutData(formData);

        labelLabel = new Label(xGroup, SWT.NONE);
        labelLabel.setText(Messages.Color_Label);
        colorEdLabel = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(labelLine, 17);
        formData.left = new FormAttachment(2);
        labelLabel.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(labelLine, 13);
        colorEdLabel.getButton().setLayoutData(formData);

        labelsGroup = new Group(pMain, SWT.NONE);
        labelsGroup.setText("Labels");
        formData = new FormData();
        formData.top = new FormAttachment(xGroup, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(90, -10);
        labelsGroup.setLayoutData(formData);
        labelsGroup.setLayout(new GridLayout(2, false));

        /*
         * Location label type
         */
        locationLabelTypeLabel = new Label(labelsGroup, SWT.NONE);
        locationLabelTypeLabel.setText(Messages.Location_Label);
        locationLabelTypeLabel.setLayoutData(new GridData(SWT.LEFT));
        locationLabelTypeCombo = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        locationLabelTypeCombo.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        locationLabelTypeCombo.setItems(getLocationLabelType());

        /*
         * measurement properties
         */
        measurementLabelPropertyLabel = new Label(labelsGroup, SWT.NONE);
        measurementLabelPropertyLabel.setText(Messages.Location_Label_Property);
        measurementLabelPropertyLabel.setLayoutData(new GridData(SWT.LEFT));
        measurementLabelPropertyCombo = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        measurementLabelPropertyCombo.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));

        /*
         * font size
         */
        labelFontSize = new Label(labelsGroup, SWT.NONE);
        labelFontSize.setText(Messages.Location_Label_Font_Size);
        labelFontSize.setLayoutData(new GridData(SWT.LEFT));
        locationLabelFontSize = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.RIGHT);
        locationLabelFontSize.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        locationLabelFontSize.setItems(getDefaultFontItem());

        locationLabelTypeCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                fillMeasurementsPropertiesCombo();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * Fill measurement properties combo
     */
    private void fillMeasurementsPropertiesCombo() {
        measurementLabelPropertyCombo.setItems(DriveStyleContent.MEASUREMENT.equals(locationLabelTypeCombo.getText())
                ? getMeasurementProperties() : new String[0]);
    }

    /**
     * Get all measurements properties array
     * 
     * @return measurements properties array or empty array
     */
    private String[] getMeasurementProperties() {
        if (model != null) {
            Set<String> propertyNames = model.getPropertyStatistics().getPropertyNames(model.getMainMeasurementNodeType());
            return propertyNames.toArray(new String[propertyNames.size()]);
        }
        return new String[0];
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

    /**
     * Get location label possible types
     * 
     * @return possible types
     */
    private String[] getLocationLabelType() {
        return new String[] {DriveStyleContent.WHITHOUT_NAME, DriveStyleContent.TIMESTAMP, DriveStyleContent.MEASUREMENT};
    }

    /**
     * gets RGB from color
     * 
     * @param color color value
     * @return
     */
    private RGB rgbFromColor(final java.awt.Color color) {
        return new RGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * gets color from RGB
     * 
     * @param colorValue - RGB value
     * @return
     */
    private java.awt.Color colorFromRGB(final RGB colorValue) {
        return new java.awt.Color(colorValue.red, colorValue.green, colorValue.blue);
    }

    /**
     * gets measurement font size
     * 
     * @return font size
     */
    private Integer getLocationLabelFontSize() {
        try {
            return Integer.parseInt(locationLabelFontSize.getText());
        } catch (NumberFormatException e) {
            return DriveStyleContent.DEFAULT_LOCATION_LABEL_FONT_SIZE;
        }
    }

    private String[] getDefaultFontItem() {
        return FONT_SIZE_ARRAY;
    }

    public IRenderableModel getRenderableModel() {
        return model;
    }

    public void setRenderableModel(final IDriveModel model) {
        this.model = model;
    }
}
