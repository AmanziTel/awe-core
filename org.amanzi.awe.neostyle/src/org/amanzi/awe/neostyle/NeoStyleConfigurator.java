package org.amanzi.awe.neostyle;

import java.awt.Color;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * Style editor
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NeoStyleConfigurator extends IStyleConfigurator {
    private static final String COLOR_TITLE = "Label";
    private static final String COLOR_LINE = "Line";
    private static final String COLOR_FILL = "Fill";
    public static final String ID = "org.amanzi.awe.neostyle.style";

    public NeoStyleConfigurator() {
        super();
    }

    @Override
    public boolean canStyle(Layer aLayer) {
        return aLayer.getStyleBlackboard().get(ID) != null;
    }

    private Label labelFill;
    private Label labelLabel;
    private Button buttonLabel;
    private Label labelLine;
    private Button buttonLine;
    private NeoStyle curStyle;
    private ColorEditor cEdFill;
    private ColorEditor cEdLine;
    private ColorEditor cEdLabel;

    public void createControl(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);

        labelFill = new Label(parent, SWT.PUSH);
        labelFill.setText(COLOR_FILL);
        cEdFill = new ColorEditor(parent);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0);
        labelFill.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 50);
        cEdFill.getButton().setLayoutData(formData);

        labelLine = new Label(parent, SWT.PUSH);
        labelLine.setText(COLOR_LINE);
        cEdLine = new ColorEditor(parent);
        formData = new FormData();
        formData.top = new FormAttachment(labelFill, 25);
        formData.left = new FormAttachment(0);
        labelLine.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 50);
        formData.top = new FormAttachment(labelFill, 20);
        cEdLine.getButton().setLayoutData(formData);

        labelLabel = new Label(parent, SWT.PUSH);
        labelLabel.setText(COLOR_TITLE);
        cEdLabel = new ColorEditor(parent);
        formData = new FormData();
        formData.top = new FormAttachment(labelLine, 25);
        formData.left = new FormAttachment(0);
        labelLabel.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 50);
        formData.top = new FormAttachment(labelLine, 20);
        cEdLabel.getButton().setLayoutData(formData);
    }

    @Override
    protected void refresh() {
        getApplyAction().setEnabled(false);
        try {
            curStyle = (NeoStyle)getStyleBlackboard().get(ID);
            cEdFill.setColorValue(rgbFromColor(curStyle.getFill()));
            cEdLabel.setColorValue(rgbFromColor(curStyle.getLabel()));
            cEdLine.setColorValue(rgbFromColor(curStyle.getLine()));
        } finally {
        }
    }

    @Override
    public void preApply() {
        super.preApply();
        curStyle.setFill(colorFromRGB(cEdFill.getColorValue()));
        curStyle.setLabel(colorFromRGB(cEdLabel.getColorValue()));
        curStyle.setLine(colorFromRGB(cEdLine.getColorValue()));
        getStyleBlackboard().put(ID, curStyle);
    }

    /**
     * gets color from RGB
     * 
     * @param colorValue - RGB value
     * @return
     */
    private Color colorFromRGB(RGB colorValue) {
        return new Color(colorValue.red, colorValue.green, colorValue.blue);
    }

    /**
     * gets RGB from color
     * 
     * @param color color value
     * @return
     */
    private RGB rgbFromColor(Color color) {
        return new RGB(color.getRed(), color.getGreen(), color.getBlue());
    }
}
