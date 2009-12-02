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
package org.amanzi.awe.neostyle;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;
import net.refractions.udig.style.IStyleConfigurator;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.NeoGeoResource;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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

    /** NeoStyleConfigurator ID field */
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

    private static final String DRIVE_FILL = "Fill";

    private static final String FONT_SIZE = "Site font size";
    private static final String FONT_SIZE_POINT = "Point font size";
    private static final String FONT_SIZE_SECTOR = "Sector font size";
    private static final String FONT_SIZE_MEAS = "Measurement  font size";

    private static final String[] FONT_SIZE_ARRAY = new String[] {"8", "9", "10", "11", "12", "14", "16", "18", "20", "24"};

    private static final String SITE_NAME = "Site name";
    private static final String POINT_NAME = "Point name";
    private static final String SECTOR_NAME = "Sector name";
    private static final String MEAS_NAME = "Measurment name";

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

    private Group grSiteSymb;

    private Group grScale;

    private boolean isNetwork;

    private Combo sFontSize;

    private Combo cSiteName;

    private Combo cSectorName;

    private Combo sSectorFontSize;

    private Label lPointFontSize;

    private Label lPointSiteName;

    private Group labelsGroup;

    private Label lFontSize;

    private Label lSectorName;

    public void createControl(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        // color block
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

        labelsGroup = new Group(parent, SWT.NONE);
        labelsGroup.setText("Labels");
        formData = new FormData();
        formData.top = new FormAttachment(xGroup, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(70, -10);
        labelsGroup.setLayoutData(formData);
        labelsGroup.setLayout(new GridLayout(2, false));

        lPointFontSize = new Label(labelsGroup, SWT.NONE);
        lPointFontSize.setText(FONT_SIZE);
        lPointFontSize.setLayoutData(new GridData(SWT.LEFT));
        sFontSize = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.RIGHT);
        sFontSize.setItems(getDefaultFontItem());
        sFontSize.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        lPointSiteName = new Label(labelsGroup, SWT.NONE);
        lPointSiteName.setText(SITE_NAME);
        lPointSiteName.setLayoutData(new GridData(SWT.LEFT));
        cSiteName = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        cSiteName.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));

        lFontSize = new Label(labelsGroup, SWT.NONE);
        lFontSize.setText(FONT_SIZE_SECTOR);
        lFontSize.setLayoutData(new GridData(SWT.LEFT));
        sSectorFontSize = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.RIGHT);
        sSectorFontSize.setItems(getDefaultFontItem());
        sSectorFontSize.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        lSectorName = new Label(labelsGroup, SWT.NONE);
        lSectorName.setText(SECTOR_NAME);
        lSectorName.setLayoutData(new GridData(SWT.LEFT));
        cSectorName = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        cSectorName.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));

        // formData = new FormData();
        // formData.top = new FormAttachment(sFontSize, 5, SWT.CENTER);
        // formData.left = new FormAttachment(2);
        // lFontSize.setLayoutData(formData);
        // formData = new FormData();
        // formData.left = new FormAttachment(labelFill, 130);
        // formData.top = new FormAttachment(labelLabel, 10);
        // sFontSize.setLayoutData(formData);

        grSiteSymb = new Group(parent, SWT.NONE);
        grSiteSymb.setText(GROUP_SITE);
        formData = new FormData();
        formData.top = new FormAttachment(labelsGroup, 15);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(70, -10);
        grSiteSymb.setLayout(new FormLayout());
        grSiteSymb.setLayoutData(formData);

        lSmallestSymb = new Label(grSiteSymb, SWT.NONE);
        tSmallestSymb = new Spinner(grSiteSymb, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSmallestSymb, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSmallestSymb.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.right = new FormAttachment(100, -5);
        formData.top = new FormAttachment(0, 5);
        tSmallestSymb.setLayoutData(formData);

        lSmallSymb = new Label(grSiteSymb, SWT.NONE);
        tSmallSymb = new Spinner(grSiteSymb, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSmallSymb, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSmallSymb.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.right = new FormAttachment(100, -5);
        formData.top = new FormAttachment(tSmallestSymb, 5);
        tSmallSymb.setLayoutData(formData);

        lLabeling = new Label(grSiteSymb, SWT.NONE);
        tLabeling = new Spinner(grSiteSymb, SWT.BORDER);

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

        grScale = new Group(parent, SWT.NONE);
        grScale.setText(GROUP_SCALE_SYMB);
        formData = new FormData();
        formData.top = new FormAttachment(grSiteSymb, 15);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(70, -10);
        grScale.setLayout(new FormLayout());
        grScale.setLayoutData(formData);
        rButton1 = new Button(grScale, SWT.RADIO);
        formData = new FormData();
        formData.left = new FormAttachment(2);
        formData.top = new FormAttachment(0, 5);
        rButton1.setLayoutData(formData);
        rButton2 = new Button(grScale, SWT.RADIO);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(rButton1, 10);
        rButton2.setLayoutData(formData);

        rButton1.setText(RADIO_SCALE_WITH_ZOOM);
        rButton2.setText(RADIO_FIXED_SYMB);

        lSymbolSize = new Label(grScale, SWT.NONE);
        lSymbolSize.setText(SYMBOL_SIZE);
        tSymbolSize = new Spinner(grScale, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSymbolSize, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSymbolSize.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(rButton1, 5);
        formData.right = new FormAttachment(100, -5);
        tSymbolSize.setLayoutData(formData);

        lSectorTr = new Label(grScale, SWT.NONE);
        lSectorTr.setText(SECTOR_TRANSPARENCY);
        tSectorTr = new Spinner(grScale, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tSectorTr, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lSectorTr.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(tSymbolSize, 5);
        formData.right = new FormAttachment(100, -5);
        tSectorTr.setLayoutData(formData);

        lMaxSymSize = new Label(grScale, SWT.NONE);
        sMaxSymSize = new Spinner(grScale, SWT.BORDER);
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
        tSectorTr.setMaximum(100);
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

    /**
     * @return
     */
    private String[] getDefaultFontItem() {

        return FONT_SIZE_ARRAY;
    }

    @Override
    protected void refresh() {
        try {
            curStyle = (NeoStyle)getStyleBlackboard().get(ID);
            cEdFill.setColorValue(rgbFromColor(curStyle.getFill()));
            cEdLabel.setColorValue(rgbFromColor(curStyle.getLabel()));
            cEdLine.setColorValue(rgbFromColor(curStyle.getLine()));
            cEdFillSite.setColorValue(rgbFromColor(curStyle.getSiteFill()));
            tSmallestSymb.setSelection(curStyle.getSmallestSymb());
            tSmallSymb.setSelection(curStyle.getSmallSymb());
            tLabeling.setSelection(curStyle.getLabeling());
            if (curStyle.isFixSymbolSize()) {
                rButton2.setSelection(true);
            } else {
                rButton1.setSelection(true);
            }

            tSymbolSize.setSelection(curStyle.getSymbolSize());
            tSectorTr.setSelection(curStyle.getSectorTransparency());
            sMaxSymSize.setSelection(curStyle.getMaximumSymbolSize());
            sFontSize.setText(String.valueOf(curStyle.getFontSize()));
            sSectorFontSize.setText(String.valueOf(curStyle.getSectorFontSize()));
            cSectorName.setItems(getSectorOrMeasurmentNames());
            cSiteName.setItems(getSiteName());
            cSectorName.setText(curStyle.getSectorName());
            cSiteName.setText(curStyle.getSiteName());

            if (!isNetwork) {
                changeToDriveStyle();
            }
        } finally {
        }
    }

    /**
     * get array of possible site names
     * 
     * @return String[]
     */
    private String[] getSiteName() {
        return new String[] {NeoStyleContent.DEF_SITE_NAME, "lat", "lon"};
    }

    /**
     *get array of sector names
     * 
     * @return array
     */
    private String[] getSectorOrMeasurmentNames() {
        List<String> result = new ArrayList<String>();
        result.add(NeoStyleContent.DEF_SECTOR_NAME);
        try {
            GeoNeo resource = getLayer().findGeoResource(GeoNeo.class).resolve(GeoNeo.class, null);
            // if (resource.getGisType() == GisTypes.NETWORK) {
                String[] allFields = new PropertyHeader(resource.getMainGisNode()).getSectorOrMeasurmentNames();
                if (allFields != null) {
                    result.addAll(Arrays.asList(allFields));
                }
            // }
            return result.toArray(new String[0]);
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
            return result.toArray(new String[0]);
        }
    }

    @Override
    public void focus(Layer layer1) {
        try {
            isNetwork = layer1.findGeoResource(NeoGeoResource.class).resolve(GeoNeo.class, null).getGisType() == GisTypes.NETWORK;
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
            isNetwork = true;
        }
        super.focus(layer1);
    }
    /**
     *
     */
    private void changeToDriveStyle() {
        labelFill.setText(DRIVE_FILL);
        cEdFillSite.getButton().setVisible(false);
        FormData formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(labelFill, 10);
        cEdLine.getButton().setLayoutData(formData);
        formData = new FormData();
        formData.top = new FormAttachment(labelFill, 15);
        formData.left = new FormAttachment(2);
        labelLine.setLayoutData(formData);
        lSite.setVisible(false);
        grSiteSymb.setVisible(false);
        grScale.setVisible(false);

        lPointFontSize.setText(FONT_SIZE_POINT);
        lPointSiteName.setText(POINT_NAME);
        lFontSize.setText(FONT_SIZE_MEAS);
        lSectorName.setText(MEAS_NAME);
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
        // font
        curStyle.setFontSize(getLabelFontSize());
        curStyle.setSectorFontSize(getSectorFontSize());

        curStyle.setMaximumSymbolSize(sMaxSymSize.getSelection());
        // property names
        curStyle.setSiteName(cSiteName.getText());
        curStyle.setSectorName(cSectorName.getText());
        getStyleBlackboard().put(ID, curStyle);
    }

    /**
     * gets sector font size
     * 
     * @return font size
     */
    private Integer getSectorFontSize() {
        try {
            return Integer.parseInt(sSectorFontSize.getText());
        } catch (NumberFormatException e) {
            return NeoStyleContent.DEF_FONT_SIZE_SECTOR;
        }
    }

    /**
     * gets label (site for network) font size
     * 
     * @return font size
     */
    private Integer getLabelFontSize() {
        try {
            return Integer.parseInt(sFontSize.getText());
        } catch (NumberFormatException e) {
            return NeoStyleContent.DEF_FONT_SIZE;
        }
    }

    /**
     * @return
     */
    private Integer getSectorTransparency() {
        return tSectorTr.getSelection();
    }

    /**
     * gets integer value of textfield tSymbolSize
     * 
     * @return
     */
    private Integer getSymbolSize() {
        return tSymbolSize.getSelection();

    }

    /**
     * gets integer value of textfield tLabeling
     * 
     * @return
     */
    private Integer getLabeling() {
        return tLabeling.getSelection();

    }

    /**
     * gets integer value of textfield tSmallSymb
     * 
     * @return
     */
    private Integer getSmallSymb() {
        return tSmallSymb.getSelection();

    }

    /**
     * gets integer value of textfield tSmallestSymb
     * 
     * @return
     */
    private Integer getSmallestSymb() {
        return tSmallestSymb.getSelection();

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
