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

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
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
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkStyleDefiner extends ViewPart {
    private static final String[] FONT_SIZE_ARRAY = new String[] {"8", "9", "10", "11", "12", "14", "16", "18", "20", "24"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$

    private Label labelFill;
    private Label labelLabel;
    private Label labelLine;
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
    private Label lTransparency;
    private Spinner tTransparency;
    private Label lFillSite;
    private ColorEditor cEdFillSite;
    private Label lMaxSymSize;
    private Spinner sMaxSymSize;
    private Label lDefBeamwidth;
    private Spinner sDefBeamwidth;
    // private Label lIconOffset;
    // private Spinner sIconOffset;

    private Group grSiteSymb;

    private Group grScale;

    private Combo cFontSize;

    private Combo cMainProperty;

    private Combo cSecondaryProperty;

    private Combo cSecondaryFontSize;

    private Label lFontSize;

    private Label lMainProperty;

    private Group labelsGroup;

    private Label lSecondaryFontSize;

    private Label lSecondaryProperty;
    
    private Label lSectorLabelTypeId;
    private Combo cSectorLabelTypeId;

    private Button bTransp;

    private Button bCorrelation;

    private  NetworkNeoStyle curStyle;
    private GeoNeo resource;

    public NetworkStyleDefiner(NetworkNeoStyle curStyle){
        this.curStyle = curStyle;
        
    }
    public NetworkStyleDefiner(){
    }   
    public NetworkNeoStyle getCurStyle() {
        return curStyle;
    }

    public void setCurStyle(NetworkNeoStyle curStyle) {
        this.curStyle = curStyle;
    }

    public void refresh(){
        cSecondaryFontSize.setItems(getDefaultFontItem());
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

        cSectorLabelTypeId.setItems(getDefaultSectorType());
        tSymbolSize.setSelection(curStyle.getSymbolSize());
        tTransparency.setSelection(curStyle.getSymbolTransparency());
        bTransp.setSelection(curStyle.isIgnoreTransparency());
        bCorrelation.setSelection(curStyle.isDrawCorrelations());
        sMaxSymSize.setSelection(curStyle.getMaximumSymbolSize());
        sDefBeamwidth.setSelection(curStyle.getDefaultBeamwidth());
        cFontSize.setText(String.valueOf(curStyle.getFontSize()));
        cSectorLabelTypeId.setText(curStyle.getSectorLabelTypeId());
        setSectorLabelProperty(curStyle.getSectorLabelTypeId());
        cSecondaryFontSize.setText(String.valueOf(curStyle.getSecondaryFontSize()));
        cMainProperty.setItems(getMainPropertyChoices());
        cSecondaryProperty.setText(curStyle.getSectorLabelProperty());
        cMainProperty.setText(curStyle.getMainProperty());
              
    }
   
    public void preApply(){
        curStyle.setFill(colorFromRGB(cEdFill.getColorValue()));
        curStyle.setSiteFill(colorFromRGB(cEdFillSite.getColorValue()));
        curStyle.setLabel(colorFromRGB(cEdLabel.getColorValue()));
        curStyle.setLine(colorFromRGB(cEdLine.getColorValue()));
        curStyle.setSmallestSymb(getSmallestSymb());
        curStyle.setSmallSymb(getSmallSymb());
        curStyle.setLabeling(getLabeling());
        curStyle.setFixSymbolSize(rButton2.getSelection());
        curStyle.setSymbolSize(getSymbolSize());
        curStyle.setSymbolTransparency(getSectorTransparency());
        curStyle.setFontSize(getLabelFontSize());
        curStyle.setSectorFontSize(getSectorFontSize());
        curStyle.setMaximumSymbolSize(sMaxSymSize.getSelection());
        curStyle.setDefaultBeamwidth(sDefBeamwidth.getSelection());
        curStyle.setMainProperty(cMainProperty.getText());
        curStyle.setSectorLabelTypeId(cSectorLabelTypeId.getText());
        curStyle.setSectorLabelProperty(cSecondaryProperty.getText());
        curStyle.setIgnoreTransparency(bTransp.getSelection());
        curStyle.setDrawCorrelations(bCorrelation.getSelection());
    }
    @Override
    public void createPartControl(Composite parent) {

        Composite pMain = parent;//new Composite(parent, SWT.FILL);
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        pMain.setLayout(layout);
        // color block
        Group xGroup = new Group(pMain, SWT.NONE);
        xGroup.setText("Colors"); //$NON-NLS-1$
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(90,-10);
        xGroup.setLayout(new FormLayout());
        xGroup.setLayoutData(formData);

        labelFill = new Label(xGroup, SWT.NONE);
        labelFill.setText(Messages.Color_Fill_Sector);
        cEdFill = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(2);
        labelFill.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        cEdFill.getButton().setLayoutData(formData);

        lFillSite = new Label(xGroup, SWT.NONE);
        lFillSite.setText(Messages.Color_Fill_Site);
        cEdFillSite = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(labelFill, 15);
        formData.left = new FormAttachment(2);
        lFillSite.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(labelFill, 10);
        cEdFillSite.getButton().setLayoutData(formData);

        labelLine = new Label(xGroup, SWT.NONE);
        labelLine.setText(Messages.Color_Line);
        cEdLine = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(lFillSite, 15);
        formData.left = new FormAttachment(2);
        labelLine.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(lFillSite, 10);
        cEdLine.getButton().setLayoutData(formData);

        labelLabel = new Label(xGroup, SWT.NONE);
        labelLabel.setText(Messages.Color_Label);
        cEdLabel = new ColorEditor(xGroup);
        formData = new FormData();
        formData.top = new FormAttachment(labelLine, 15);
        formData.left = new FormAttachment(2);
        labelLabel.setLayoutData(formData);
        formData = new FormData();
        formData.left = new FormAttachment(labelFill, 130);
        formData.top = new FormAttachment(labelLine, 10);
        cEdLabel.getButton().setLayoutData(formData);

        labelsGroup = new Group(pMain, SWT.NONE);
        labelsGroup.setText("Labels"); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(xGroup, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(90, -10);
        labelsGroup.setLayoutData(formData);
        labelsGroup.setLayout(new GridLayout(2, false));

        lMainProperty = new Label(labelsGroup, SWT.NONE);
        lMainProperty.setText(Messages.Site_Property);
        lMainProperty.setLayoutData(new GridData(SWT.LEFT));
        cMainProperty = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        cMainProperty.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        lFontSize = new Label(labelsGroup, SWT.NONE);
        lFontSize.setText(Messages.Font_Size_Site);
        lFontSize.setLayoutData(new GridData(SWT.LEFT));
        cFontSize = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.RIGHT);
        cFontSize.setItems(getDefaultFontItem());
        cFontSize.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        lSectorLabelTypeId= new Label(labelsGroup, SWT.NONE);
        lSectorLabelTypeId.setText(Messages.NetworkStyleDefiner_SectorLabelType);
        cSectorLabelTypeId = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        cSectorLabelTypeId.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        lSecondaryProperty = new Label(labelsGroup, SWT.NONE);
        lSecondaryProperty.setText(Messages.Sector_Property);
        lSecondaryProperty.setLayoutData(new GridData(SWT.LEFT));
        cSecondaryProperty = new Combo(labelsGroup, SWT.DROP_DOWN /*| SWT.READ_ONLY*/);
        cSecondaryProperty.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));
        lSecondaryFontSize = new Label(labelsGroup, SWT.NONE);
        lSecondaryFontSize.setText(Messages.Font_Size_Sector);
        lSecondaryFontSize.setLayoutData(new GridData(SWT.LEFT));
        cSecondaryFontSize = new Combo(labelsGroup, SWT.DROP_DOWN | SWT.RIGHT);
        cSecondaryFontSize.setLayoutData(new GridData(SWT.FILL | GridData.FILL_HORIZONTAL));

        // formData = new FormData();
        // formData.top = new FormAttachment(sFontSize, 5, SWT.CENTER);
        // formData.left = new FormAttachment(2);
        // lFontSize.setLayoutData(formData);
        // formData = new FormData();
        // formData.left = new FormAttachment(labelFill, 130);
        // formData.top = new FormAttachment(labelLabel, 10);
        // sFontSize.setLayoutData(formData);

        grSiteSymb = new Group(pMain, SWT.NONE);
        grSiteSymb.setText(Messages.Density_Thresholds);
        formData = new FormData();
        formData.top = new FormAttachment(labelsGroup, 15);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(90, -10);
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

        lSmallestSymb.setText(Messages.Smallest_Symbols);
        lSmallSymb.setText(Messages.Small_Symbols);
        lLabeling.setText(Messages.Labels);

        grScale = new Group(pMain, SWT.NONE);
        grScale.setText(Messages.Symbol_Sizes);
        formData = new FormData();
        formData.top = new FormAttachment(grSiteSymb, 15);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(90, -10);
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

        rButton1.setText(Messages.Symbol_Scale_With_Zoom);
        rButton2.setText(Messages.Symbol_Use_Fixed_Size);

        lSymbolSize = new Label(grScale, SWT.NONE);
        lSymbolSize.setText(Messages.Symbol_Size);
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

        lTransparency = new Label(grScale, SWT.NONE);
        lTransparency.setText(Messages.Symbol_Transparency);
        tTransparency = new Spinner(grScale, SWT.BORDER);
        formData = new FormData();
        formData.top = new FormAttachment(tTransparency, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lTransparency.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(tSymbolSize, 5);
        formData.right = new FormAttachment(100, -5);
        tTransparency.setLayoutData(formData);

        bTransp = new Button(grScale, SWT.CHECK);
        bTransp.setText(Messages.Ignore_transsparency);
        formData = new FormData();
        formData.left = new FormAttachment(2);
        formData.top = new FormAttachment(tTransparency, 5);
        bTransp.setLayoutData(formData);


        bCorrelation = new Button(grScale, SWT.CHECK);
        bCorrelation.setText(Messages.Draw_correlation);
        formData = new FormData();
        formData.left = new FormAttachment(2);
        formData.top = new FormAttachment(bTransp, 5);
        bCorrelation.setLayoutData(formData);

        lMaxSymSize = new Label(grScale, SWT.NONE);
        sMaxSymSize = new Spinner(grScale, SWT.BORDER);
        lMaxSymSize.setText(Messages.Symbol_Max_Size);

        formData = new FormData();
        formData.top = new FormAttachment(sMaxSymSize, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lMaxSymSize.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(bCorrelation, 5);
        formData.right = new FormAttachment(100, -5);
        sMaxSymSize.setLayoutData(formData);

        lDefBeamwidth = new Label(grScale, SWT.NONE);
        sDefBeamwidth = new Spinner(grScale, SWT.BORDER);
        lDefBeamwidth.setText(Messages.Symbol_Def_Beam);

        formData = new FormData();
        formData.top = new FormAttachment(sDefBeamwidth, 5, SWT.CENTER);
        formData.left = new FormAttachment(2);
        lDefBeamwidth.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(70, 10);
        formData.top = new FormAttachment(sMaxSymSize, 5);
        formData.right = new FormAttachment(100, -5);
        sDefBeamwidth.setLayoutData(formData);

        // sets spinners range
        tLabeling.setMinimum(1);
        tLabeling.setMaximum(10000);
        tTransparency.setMinimum(1);
        tTransparency.setMaximum(100);
        tSmallestSymb.setMinimum(1);
        tSmallestSymb.setMaximum(10000);
        tSmallSymb.setMinimum(1);
        tSmallSymb.setMaximum(10000);
        tSymbolSize.setMinimum(1);
        tSymbolSize.setMaximum(10000);
        tTransparency.setMaximum(100);
        sDefBeamwidth.setMinimum(10);
        sDefBeamwidth.setMaximum(360);
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
        cSectorLabelTypeId.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                setSectorLabelProperty(cSectorLabelTypeId.getText());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

    }
    /**
     *
     */
    public void setSectorLabelProperty(String typeId) {
        cSecondaryProperty.setItems(getSecondaryPropertyChoices(typeId));
    }
    /**
     *
     * @return
     */
    private String[] getDefaultSectorType() {
        String[] result=new String[]{NodeTypes.SECTOR.getId(),NodeTypes.TRX.getId(),NodeTypes.FREQUENCY_PLAN.getId()};
        return result;
    }
    /**
     * get array of sector names
     * @param typeId 
     * 
     * @return array
     */
    private String[] getSecondaryPropertyChoices(String typeId) {
        List<String> result = new ArrayList<String>();
        result.add(NetworkNeoStyleContent.DEF_NONE);
        if (NodeTypes.SECTOR.getId().equals(typeId)){
            result.add(INeoConstants.PROPERTY_NAME_NAME);
            result.add(INeoConstants.PROPERTY_SECTOR_CI);
            result.add(INeoConstants.PROPERTY_SECTOR_LAC);
            result.add("vendor");
        }else if (NodeTypes.TRX.getId().equals(typeId)){
            result.add("band");
            result.add(INeoConstants.PROPERTY_BCCH_NAME);
            result.add("hopping_type");
        }else{
            result.add(INeoConstants.PROPERTY_SECTOR_ARFCN);
            result.add("hsn");
            result.add(INeoConstants.PROPERTY_MAIO);       
        }
            return result.toArray(new String[0]);
    }
    /**
     * get array of possible site names
     * 
     * @return String[]
     */
    private String[] getMainPropertyChoices() {
        return new String[] {NetworkNeoStyleContent.DEF_NONE, NetworkNeoStyleContent.DEF_MAIN_PROPERTY, "lat", "lon"}; //$NON-NLS-1$ //$NON-NLS-2$
    }
    @Override
    public void setFocus() {
    }
    /**
     * gets sector font size
     * 
     * @return font size
     */
    private Integer getSectorFontSize() {
        try {
            return Integer.parseInt(cSecondaryFontSize.getText());
        } catch (NumberFormatException e) {
            return NetworkNeoStyleContent.DEF_FONT_SIZE_SECTOR;
        }
    }

    /**
     * gets label (site for network) font size
     * 
     * @return font size
     */
    private Integer getLabelFontSize() {
        try {
            return Integer.parseInt(cFontSize.getText());
        } catch (NumberFormatException e) {
            return NetworkNeoStyleContent.DEF_FONT_SIZE;
        }
    }

    /**
     * @return
     */
    private Integer getSectorTransparency() {
        return tTransparency.getSelection();
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
    private java.awt.Color colorFromRGB(RGB colorValue) {
        return new java.awt.Color(colorValue.red, colorValue.green, colorValue.blue);
    }

    /**
     * gets RGB from color
     * 
     * @param color color value
     * @return
     */
    private RGB rgbFromColor(java.awt.Color color) {
        return new RGB(color.getRed(), color.getGreen(), color.getBlue());
    }
    /**
     *
     * @param resource
     */
    public void setGeoNeo(GeoNeo resource) {
        this.resource = resource;
        
    }
    public GeoNeo getGeoNeo(){
        return resource;
    }
    private String[] getDefaultFontItem() {

        return FONT_SIZE_ARRAY;
    }
}
