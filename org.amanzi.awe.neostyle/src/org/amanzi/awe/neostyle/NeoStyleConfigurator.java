package org.amanzi.awe.neostyle;

import java.awt.Color;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * Style editor for org.amanzi.awe.render.network
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeoStyleConfigurator extends IStyleConfigurator {

    public static final String ID = "org.amanzi.awe.neostyle.style";

    private static final String SYMBOL_SIZE = "Symbol base size";
    private static final String RADIO_SCALE_WITH_ZOOM = "Scale with zoom";
    private static final String RADIO_FIXED_SYMB = "Use fixed size";
    private static final String GROUP_SCALE_SYMB = "Symbol sizes";
    private static final String LABELING = "Labels";
    private static final String SMALL_SYMBOLS = "Small symbols";
    private static final String SMALLEST_SYMBOLS = "Smallest symbols";
    private static final String SECTOR_TRANSPARENCY = "Sector transparency (%)";
    private static final String GROUP_SITE = "Site density thresholds";
    private static final String COLOR_TITLE = "Label";
    private static final String COLOR_LINE = "Line";
    private static final String COLOR_FILL = "Sector fill";
    private static final String COLOR_FILL_SITE = "Site fill";
    private static final String MAX_SYMB_SIZE = "Maximum size";

    public NeoStyleConfigurator() {
        super();
    }

    @Override
    public boolean canStyle(Layer aLayer) {
        return aLayer.getStyleBlackboard().get(ID) != null;
    }

    private Label labelFill;
    private Label labelLabel;
    private Label labelLine;
    private NeoStyle curStyle;
    private ColorEditor cEdFill;
    private ColorEditor cEdLine;
    private ColorEditor cEdLabel;
    private Label lSmallSymb;
    private Spinner tSmallSymb;
    private Label lSmallestSymb;
    private Spinner tSmallestSymb;
    private Label lLabeling;
    private Spinner tLabeling;
    private Button rButton1;
    private Button rButton2;
    private Label lSymbolSize;
    private Spinner tSymbolSize;
    private Label lSectorTr;
    private Spinner tSectorTr;
    private Label lSite;
    private ColorEditor cEdFillSite;
    private Label lMaxSymSize;
    private Spinner sMaxSymSize;

    public void createControl(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);

        Group xGroup = new Group(parent, SWT.NONE);
        xGroup.setText("Colors");
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(70, -10);
        xGroup.setLayout(new FormLayout());
        xGroup.setLayoutData(formData);

        labelFill = new Label(xGroup, SWT.NONE);
        labelFill.setText(COLOR_FILL);
        cEdFill = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(2);
        labelFill.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        cEdFill.getButton().setLayoutData(formData);

        lSite = new Label(xGroup, SWT.NONE);
        lSite.setText(COLOR_FILL_SITE);
        cEdFillSite = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(labelFill, 15);
        formData.left = new FormAttachment(2);
        lSite.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(labelFill, 10);
        cEdFillSite.getButton().setLayoutData(formData);

        labelLine = new Label(xGroup, SWT.NONE);
        labelLine.setText(COLOR_LINE);
        cEdLine = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(lSite, 15);
        formData.left = new FormAttachment(2);
        labelLine.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(lSite, 10);
        cEdLine.getButton().setLayoutData(formData);

        labelLabel = new Label(xGroup, SWT.NONE);
        labelLabel.setText(COLOR_TITLE);
        cEdLabel = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(labelLine, 15);
        formData.left = new FormAttachment(2);
        labelLabel.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(labelLine, 10);
        cEdLabel.getButton().setLayoutData(formData);

        Group cGroup = new Group(parent, SWT.NONE);
        cGroup.setText(GROUP_SITE);
        formData = new FormData();
        formData.top = new FormAttachment(xGroup, 15);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(70, -10);
        cGroup.setLayout(new FormLayout());
        cGroup.setLayoutData(formData);

        lSmallestSymb = new Label(cGroup, SWT.NONE);
        tSmallestSymb = new Spinner(cGroup, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSmallestSymb, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSmallestSymb.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.right = new FormAttachment(100, -5);
        formData.top = new FormAttachment(0, 5);
        tSmallestSymb.setLayoutData(formData);

        lSmallSymb = new Label(cGroup, SWT.NONE);
        tSmallSymb = new Spinner(cGroup, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSmallSymb, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSmallSymb.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.right = new FormAttachment(100, -5);
        formData.top = new FormAttachment(tSmallestSymb, 5);
        tSmallSymb.setLayoutData(formData);

        lLabeling = new Label(cGroup, SWT.NONE);
        tLabeling = new Spinner(cGroup, SWT.BORDER);

        formData = new FormData();
        formData.top = new FormAttachment(tLabeling, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lLabeling.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.right = new FormAttachment(100, -5);
        formData.top = new FormAttachment(tSmallSymb, 5);
        tLabeling.setLayoutData(formData);

        lSmallestSymb.setText(SMALLEST_SYMBOLS);
        lSmallSymb.setText(SMALL_SYMBOLS);
        lLabeling.setText(LABELING);

        Group rGroup = new Group(parent, SWT.NONE);
        rGroup.setText(GROUP_SCALE_SYMB);
        formData = new FormData();
        formData.top = new FormAttachment(cGroup, 15);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(70, -10);
        rGroup.setLayout(new FormLayout());
        rGroup.setLayoutData(formData);
        rButton1 = new Button(rGroup, SWT.RADIO);
        formData = new FormData();
        formData.left = new FormAttachment(2);
        formData.top = new FormAttachment(0, 5);
        rButton1.setLayoutData(formData);
        rButton2 = new Button(rGroup, SWT.RADIO);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(rButton1, 10);
        rButton2.setLayoutData(formData);

        rButton1.setText(RADIO_SCALE_WITH_ZOOM);
        rButton2.setText(RADIO_FIXED_SYMB);

        lSymbolSize = new Label(rGroup, SWT.NONE);
        lSymbolSize.setText(SYMBOL_SIZE);
        tSymbolSize = new Spinner(rGroup, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSymbolSize, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSymbolSize.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(rButton1, 5);
        formData.right = new FormAttachment(100, -5);
        tSymbolSize.setLayoutData(formData);

        lSectorTr = new Label(rGroup, SWT.NONE);
        lSectorTr.setText(SECTOR_TRANSPARENCY);
        tSectorTr = new Spinner(rGroup, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSectorTr, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSectorTr.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(tSymbolSize, 5);
        formData.right = new FormAttachment(100, -5);
        tSectorTr.setLayoutData(formData);

        lMaxSymSize = new Label(rGroup, SWT.NONE);
        sMaxSymSize = new Spinner(rGroup, SWT.BORDER);
        lMaxSymSize.setText(MAX_SYMB_SIZE);

        formData = new FormData();
        formData.top = new FormAttachment(sMaxSymSize, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lMaxSymSize.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(tSectorTr, 5);
        formData.right = new FormAttachment(100, -5);
        sMaxSymSize.setLayoutData(formData);
        // sets spinners range
        tLabeling.setMinimum(1);
        tLabeling.setMaximum(10000);
        tSectorTr.setMinimum(1);
        tSectorTr.setMaximum(100);
        tSmallestSymb.setMinimum(1);
        tSmallestSymb.setMaximum(10000);
        tSmallSymb.setMinimum(1);
        tSmallSymb.setMaximum(10000);
        tSymbolSize.setMinimum(1);
        tSymbolSize.setMaximum(10000);
        rButton1.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                lMaxSymSize.setVisible(rButton1.getSelection());
                sMaxSymSize.setVisible(rButton1.getSelection());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    @Override
    protected void refresh() {
        // getApplyAction().setEnabled(false);
        try {
            curStyle = (NeoStyle)getStyleBlackboard().get(ID);
            cEdFill.setColorValue(rgbFromColor(curStyle.getFill()));
            cEdLabel.setColorValue(rgbFromColor(curStyle.getLabel()));
            cEdLine.setColorValue(rgbFromColor(curStyle.getLine()));
            cEdFillSite.setColorValue(rgbFromColor(curStyle.getSiteFill()));
            // String value = String.valueOf(curStyle.getSmallestSymb());
            tSmallestSymb.setSelection(curStyle.getSmallestSymb());// ("null".equals(value) ? "" :
                                                                   // value);
            // value = String.valueOf(curStyle.getSmallSymb());
            tSmallSymb.setSelection(curStyle.getSmallSymb());// setText("null".equals(value) ? "" :
                                                             // value);
            // value = String.valueOf(curStyle.getLabeling());
            tLabeling.setSelection(curStyle.getLabeling());// setText("null".equals(value) ? "" :
                                                           // value);
            if (curStyle.isFixSymbolSize()) {
                rButton2.setSelection(true);
            } else {
                rButton1.setSelection(true);
            }
            // value = String.valueOf(curStyle.getSymbolSize());
            tSymbolSize.setSelection(curStyle.getSymbolSize());// setText("null".equals(value) ? ""
                                                               // : value);
            // value = String.valueOf(curStyle.getSectorTransparency());
            tSectorTr.setSelection(curStyle.getSectorTransparency());// setText("null".equals(value)
            sMaxSymSize.setSelection(curStyle.getMaximumSymbolSize()); // ? "" : value);
        } finally {
        }
    }

    @Override
    public void preApply() {
        super.preApply();
        curStyle.setFill(colorFromRGB(cEdFill.getColorValue()));
        curStyle.setSiteFill(colorFromRGB(cEdFillSite.getColorValue()));
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
        curStyle.setMaximumSymbolSize(sMaxSymSize.getSelection());
    }

    /**
     * @return
     */
    private Integer getSectorTransparency() {
        return tSectorTr.getSelection();
        // try {
        // return Integer.parseInt(tSectorTr.getText());
        // } catch (NumberFormatException e) {
        // return null;
        // }
    }

    /**
     * gets integer value of textfield tSymbolSize
     * 
     * @return
     */
    private Integer getSymbolSize() {
        return tSymbolSize.getSelection();
        // try {
        // return Integer.parseInt(tSymbolSize.getText());
        // } catch (NumberFormatException e) {
        // return null;
        // }
    }

    /**
     * gets integer value of textfield tLabeling
     * 
     * @return
     */
    private Integer getLabeling() {
        return tLabeling.getSelection();
        // try {
        // return Integer.parseInt(tLabeling.getText());
        // } catch (NumberFormatException e) {
        // return null;
        // }
    }

    /**
     * gets integer value of textfield tSmallSymb
     * 
     * @return
     */
    private Integer getSmallSymb() {
        return tSmallSymb.getSelection();
        // try {
        // return Integer.parseInt(tSmallSymb.getText());
        // } catch (NumberFormatException e) {
        // return null;
        // }
    }

    /**
     * gets integer value of textfield tSmallestSymb
     * 
     * @return
     */
    private Integer getSmallestSymb() {
        return tSmallestSymb.getSelection();
        // try {
        // return Integer.parseInt(tSmallestSymb.getText());
        // } catch (NumberFormatException e) {
        // return null;
        // }
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
