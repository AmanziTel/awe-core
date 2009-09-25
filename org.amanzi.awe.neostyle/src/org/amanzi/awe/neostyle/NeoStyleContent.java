package org.amanzi.awe.neostyle;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.amanzi.awe.catalog.neo.NeoGeoResource;
import org.amanzi.neo.core.enums.GisTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * <p>
 * Style content
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NeoStyleContent extends StyleContent {

    /** int DEF_SECTOR_TR field */
    public static final int DEF_SECTOR_TR = 40;
    /** int DEF_SYMB_SIZE field */
    public static final int DEF_SYMB_SIZE = 15;
    /** boolean DEF_FIX_SYMB_SIZE field */
    public static final boolean DEF_FIX_SYMB_SIZE = false;
    /** int DEF_LABELING field */
    public static final int DEF_LABELING = 50;
    /** int DEF_SMALL_SYMB field */
    public static final int DEF_SMALL_SYMB = 100;
    /** int DEF_SMALLEST_SYMB field */
    public static final int DEF_SMALLEST_SYMB = 1000;
    /** Color DEF_COLOR_LINE field */
    public static final Color DEF_COLOR_LINE = Color.DARK_GRAY;
    /** NeoStyleContent DEF_COLOR_LABEL field */
    public static final Color DEF_COLOR_LABEL = Color.DARK_GRAY;
    /** Color DEF_COLOR_FILL field */
    public static final Color DEF_COLOR_FILL = new Color(255, 255, 128);
    /** NeoStyleContent DEF_FONT_SIZE field */
    public static final Integer DEF_FONT_SIZE = 12;
    /** NeoStyleContent DEF_COLOR_SITE field */
    public static final Color DEF_COLOR_SITE = Color.DARK_GRAY;
    public static final String ID = "org.amanzi.awe.neostyle.style";
    private static final String LINE_PRFX = "LINE_";
    private static final String FILL_PRFX = "FILL_";
    private static final String LABEL_PRFX = "LABEL_";
    private static final String SITE_PRFX = "SITE_";
    private static final String COLOR_RGB = "RGB";
    private static final String SMALLEST_SYMB = "SECTOR_SMALLEST_SYMB";
    private static final String SMALL_SYMB = "SECTOR_SMALL_SYMB";
    private static final String LABELING = "SECTOR_LABELING";
    private static final String FIX_SYMBOL = "FIX_SYMBOL";
    private static final String SYMBOL_SIZE = "SYMBOL_SIZE";
    private static final String SECTOR_TRANSPARENCY = "SECTOR_TRANSPARENCY";
    public static final int DEF_MAXIMUM_SYMBOL_SIZE = 40;
    private static final String MAX_SYMB_SIZE = "MAXIMUM_SYMBOL_SIZE";
    private static final String FONT_SIZE = "FONT_SIZE";

    // private static final String IS_NETWORK_STYLE = "IS_NETWORK";


    public NeoStyleContent() {
        super(ID);
    }

    @Override
    public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor) throws IOException {
        if (resource.canResolve(NeoGeoResource.class)) {
            NeoGeoResource res = resource.resolve(NeoGeoResource.class, monitor);
            if (res.getGeoNeo(monitor).getGisType() == GisTypes.Network) {
                NeoStyle result = new NeoStyle(DEF_COLOR_LINE, DEF_COLOR_FILL, DEF_COLOR_LABEL);
                result.setSmallestSymb(DEF_SMALLEST_SYMB);
                result.setSmallSymb(DEF_SMALL_SYMB);
                result.setLabeling(DEF_LABELING);
                result.setFixSymbolSize(DEF_FIX_SYMB_SIZE);
                result.setSymbolSize(DEF_SYMB_SIZE);
                result.setSectorTransparency(DEF_SECTOR_TR);
                result.setSiteFill(DEF_COLOR_SITE);
                result.setMaximumSymbolSize(DEF_MAXIMUM_SYMBOL_SIZE);
                result.setFontSize(DEF_FONT_SIZE);
                // result.setNetwork(true);
                return result;
            } else {
                NeoStyle result = new NeoStyle(Color.BLACK, new Color(200, 128, 255, (int)(0.6 * 255.0)), Color.BLACK);
                result.setFontSize(DEF_FONT_SIZE);
                // result.setNetwork(false);
                return result;
            }
        }
        return null;
    }

    @Override
    public Class<NeoStyle> getStyleClass() {
        return NeoStyle.class;
    }

    @Override
    public Object load(IMemento memento) {
        Color line = loadColor(memento, LINE_PRFX, Color.BLACK);
        Color fill = loadColor(memento, FILL_PRFX, Color.BLACK);
        Color label = loadColor(memento, LABEL_PRFX, Color.BLACK);
        Color site = loadColor(memento, SITE_PRFX, Color.BLACK);
        NeoStyle result = new NeoStyle(line, fill, label);
        result.setSiteFill(site);
        result.setSmallestSymb(memento.getInteger(SMALLEST_SYMB));
        result.setSmallSymb(memento.getInteger(SMALL_SYMB));
        result.setLabeling(memento.getInteger(LABELING));
        result.setFixSymbolSize(Boolean.parseBoolean(memento.getString(FIX_SYMBOL)));
        result.setSymbolSize(memento.getInteger(SYMBOL_SIZE));
        result.setSectorTransparency(memento.getInteger(SECTOR_TRANSPARENCY));
        result.setMaximumSymbolSize(memento.getInteger(MAX_SYMB_SIZE));
        result.setFontSize(memento.getInteger(FONT_SIZE));
        // result.setNetwork(getBoolean(memento, IS_NETWORK_STYLE, true));
        return result;
    }

    /**
     * @param memento
     * @param isNetworkStyle
     * @param b
     * @return
     */
    private boolean getBoolean(IMemento memento, String prefix, boolean defvalue) {
        try {
            return Boolean.parseBoolean(memento.getString(prefix));
        } catch (Exception e) {
            return defvalue;
        }
    }

    private Color loadColor(IMemento memento, String prfx, Color defColor) {
        Integer rgb = memento.getInteger(prfx + COLOR_RGB);
        return rgb == null ? defColor : new Color(rgb);
    }

    @Override
    public Object load(URL url, IProgressMonitor monitor) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save(IMemento memento, Object value) {
        NeoStyle style = (NeoStyle)value;
        saveColor(memento, LINE_PRFX, style.getLine());
        saveColor(memento, FILL_PRFX, style.getFill());
        saveColor(memento, LABEL_PRFX, style.getLabel());
        saveColor(memento, SITE_PRFX, style.getSiteFill());
        memento.putInteger(SMALLEST_SYMB, style.getSmallestSymb());
        memento.putInteger(SMALL_SYMB, style.getSmallSymb());
        memento.putInteger(LABELING, style.getLabeling());
        memento.putString(FIX_SYMBOL, String.valueOf(style.isFixSymbolSize()));
        memento.putInteger(SYMBOL_SIZE, style.getSymbolSize());
        memento.putInteger(SECTOR_TRANSPARENCY, style.getSectorTransparency());
        memento.putInteger(MAX_SYMB_SIZE, style.getMaximumSymbolSize());
        memento.putInteger(FONT_SIZE, style.getFontSize());
        // memento.putString(IS_NETWORK_STYLE, String.valueOf(style.isNetwork()));

    }

    /**
     * save color in IMemento
     * 
     * @param memento IMemento
     * @param prfx value prefix
     * @param colorTosave color to save
     */
    private void saveColor(IMemento memento, String prfx, Color colorTosave) {
        memento.putInteger(prfx + COLOR_RGB, colorTosave.getRGB());
    }

}
