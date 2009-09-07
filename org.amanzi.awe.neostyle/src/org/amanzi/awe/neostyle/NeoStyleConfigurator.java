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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * Style editor
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NeoStyleConfigurator extends IStyleConfigurator {
    /** String GROUP_SITE field */
    private static final String GROUP_SITE = "Site density thresholds";
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
    private Label lSmallSymb;
    private Text tSmallSymb;
    private Label lSmallestSymb;
    private Text tSmallestSymb;
    private Label lLabeling;
    private Text tLabeling;
    private Button rButton1;
    private Button rButton2;
    private Label lSymbolSize;
    private Text tSymbolSize;
    private Label lSectorTr;
    private Text tSectorTr;

    public void createControl(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);

        labelFill = new Label(parent, SWT.NONE);
        labelFill.setText(COLOR_FILL);
        cEdFill = new ColorEditor(parent);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0);
        labelFill.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 50);
        cEdFill.getButton().setLayoutData(formData);

        labelLine = new Label(parent, SWT.NONE);
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
        labelLabel = new Label(parent, SWT.NONE);
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
        Group cGroup = new Group(parent, SWT.NONE);
        cGroup.setText(GROUP_SITE);
        formData = new FormData();
        formData.top = new FormAttachment(labelLabel, 25);
        formData.left = new FormAttachment(0);
        cGroup.setLayout(new FormLayout());
        cGroup.setLayoutData(formData);

        lSmallestSymb = new Label(cGroup, SWT.NONE);
        tSmallestSymb = new Text(cGroup, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(tSmallestSymb, 5, SWT.CENTER);
        formData.left = new FormAttachment(0);
        lSmallestSymb.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(lSmallestSymb, 10);
        formData.top = new FormAttachment(0, 5);
        tSmallestSymb.setLayoutData(formData);

        lSmallSymb = new Label(cGroup, SWT.NONE);
        tSmallSymb = new Text(cGroup, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(tSmallSymb, 5, SWT.CENTER);
        formData.left = new FormAttachment(0);
        lSmallSymb.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(lSmallSymb, 10);
        formData.top = new FormAttachment(tSmallestSymb, 5);
        tSmallSymb.setLayoutData(formData);

        lLabeling = new Label(cGroup, SWT.NONE);
        tLabeling = new Text(cGroup, SWT.NONE);

        formData = new FormData();
        formData.top = new FormAttachment(tLabeling, 5, SWT.CENTER);
        formData.left = new FormAttachment(0);
        lLabeling.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(lLabeling, 10);
        formData.top = new FormAttachment(tSmallSymb, 5);
        tLabeling.setLayoutData(formData);

        lSmallestSymb.setText("for smallest symbols");
        lSmallSymb.setText("for small symbols");
        lLabeling.setText("for labeling");

        Group rGroup = new Group(parent, SWT.NONE);
        rGroup.setText("Scale symbols with zoom");
        formData = new FormData();
        formData.top = new FormAttachment(cGroup, 25);
        formData.left = new FormAttachment(0);
        rGroup.setLayout(new FormLayout());
        rGroup.setLayoutData(formData);
        rButton1 = new Button(rGroup, SWT.RADIO);
        formData = new FormData();
        formData.left = new FormAttachment(0);
        formData.top = new FormAttachment(0, 5);
        rButton1.setLayoutData(formData);
        rButton2 = new Button(rGroup, SWT.RADIO);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(rButton1, 10);
        rButton2.setLayoutData(formData);

        rButton1.setText("Scale symbols with zoom");
        rButton2.setText("Use fixed symbol size");

        lSymbolSize = new Label(parent, SWT.NONE);
        lSymbolSize.setText("Symbol size");
        tSymbolSize = new Text(parent, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(tSymbolSize, 5, SWT.CENTER);
        formData.left = new FormAttachment(0);
        lSymbolSize.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(lSymbolSize, 10);
        formData.top = new FormAttachment(rGroup, 5);
        tSymbolSize.setLayoutData(formData);

        lSectorTr = new Label(parent, SWT.NONE);
        lSectorTr.setText("Sector transparency (%)");
        tSectorTr = new Text(parent, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(tSectorTr, 5, SWT.CENTER);
        formData.left = new FormAttachment(0);
        lSectorTr.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(lSectorTr, 10);
        formData.top = new FormAttachment(tSymbolSize, 5);
        tSectorTr.setLayoutData(formData);

    }

    @Override
    protected void refresh() {
        getApplyAction().setEnabled(false);
        try {
            curStyle = (NeoStyle)getStyleBlackboard().get(ID);
            cEdFill.setColorValue(rgbFromColor(curStyle.getFill()));
            cEdLabel.setColorValue(rgbFromColor(curStyle.getLabel()));
            cEdLine.setColorValue(rgbFromColor(curStyle.getLine()));
            String value = String.valueOf(curStyle.getSmallestSymb());
            tSmallestSymb.setText("null".equals(value) ? "" : value);
            value = String.valueOf(curStyle.getSmallSymb());
            tSmallSymb.setText("null".equals(value) ? "" : value);
            value = String.valueOf(curStyle.getLabeling());
            tLabeling.setText("null".equals(value) ? "" : value);
            if (curStyle.isFixSymbolSize()) {
                rButton2.setSelection(true);
            } else {
                rButton1.setSelection(true);
            }
            value = String.valueOf(curStyle.getSymbolSize());
            tSymbolSize.setText("null".equals(value) ? "" : value);
            value = String.valueOf(curStyle.getSectorTransparency());
            tSectorTr.setText("null".equals(value) ? "" : value);
        } finally {
        }
    }

    @Override
    public void preApply() {
        super.preApply();
        curStyle.setFill(colorFromRGB(cEdFill.getColorValue()));
        curStyle.setLabel(colorFromRGB(cEdLabel.getColorValue()));
        curStyle.setLine(colorFromRGB(cEdLine.getColorValue()));
        curStyle.setSmallestSymb(getSmallestSymb());
        curStyle.setSmallSymb(getSmallSymb());
        curStyle.setLabeling(getLabeling());
        curStyle.setFixSymbolSize(rButton2.getSelection());
        curStyle.setSymbolSize(getSymbolSize());
        curStyle.setSectorTransparency(getSectorTransparency());
        try {
            getStyleBlackboard().put(ID, curStyle);
        } catch (Exception e) {
            // TODO - remove after debug
            e.printStackTrace();
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    /**
     * @return
     */
    private Integer getSectorTransparency() {
        try {
            return Integer.parseInt(tSectorTr.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * gets integer value of textfield tSymbolSize
     * 
     * @return
     */
    private Integer getSymbolSize() {
        try {
            return Integer.parseInt(tSymbolSize.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * gets integer value of textfield tLabeling
     * 
     * @return
     */
    private Integer getLabeling() {
        try {
            return Integer.parseInt(tLabeling.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * gets integer value of textfield tSmallSymb
     * 
     * @return
     */
    private Integer getSmallSymb() {
        try {
            return Integer.parseInt(tSmallSymb.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * gets integer value of textfield tSmallestSymb
     * 
     * @return
     */
    private Integer getSmallestSymb() {
        try {
            return Integer.parseInt(tSmallestSymb.getText());
        } catch (NumberFormatException e) {
            return null;
        }
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
