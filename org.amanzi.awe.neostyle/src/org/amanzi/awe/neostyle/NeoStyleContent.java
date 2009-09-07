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

    public static final String ID = "org.amanzi.awe.neostyle.style";
    private static final String LINE_PRFX = "LINE_";
    private static final String FILL_PRFX = "FILL_";
    private static final String LABEL_PRFX = "LABEL_";
    private static final String COLOR_RGB = "RGB";
    private static final String SMALLEST_SYMB = "SECTOR_SMALLEST_SYMB";
    private static final String SMALL_SYMB = "SECTOR_SMALL_SYMB";
    private static final String LABELING = "SECTOR_LABELING";
    private static final String FIX_SYMBOL = "FIX_SYMBOL";
    private static final String SYMBOL_SIZE = "SYMBOL_SIZE";
    private static final String SECTOR_TRANSPARENCY = "SECTOR_TRANSPARENCY";

    public NeoStyleContent() {
        super(ID);
    }

    @Override
    public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor) throws IOException {
        if (resource.canResolve(NeoGeoResource.class)) {
            NeoGeoResource res = resource.resolve(NeoGeoResource.class, monitor);
            int transparency = (int)(0.6 * 255.0);
            if (res.getGeoNeo(monitor).getGisType() == GisTypes.Network) {
                NeoStyle result = new NeoStyle(Color.DARK_GRAY, new Color(255, 255, 128, transparency), Color.DARK_GRAY);
                result.setSmallestSymb(1000);
                result.setSmallSymb(100);
                result.setLabeling(50);
                result.setFixSymbolSize(false);
                result.setSymbolSize(15);
                result.setSectorTransparency(60);
                return result;
            } else {
                return new NeoStyle(Color.BLACK, new Color(200, 128, 255, transparency), Color.BLACK);
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
        NeoStyle result = new NeoStyle(line, fill, label);
        result.setSmallestSymb(memento.getInteger(SMALLEST_SYMB));
        result.setSmallSymb(memento.getInteger(SMALL_SYMB));
        result.setLabeling(memento.getInteger(LABELING));
        result.setFixSymbolSize(Boolean.parseBoolean(memento.getString(FIX_SYMBOL)));
        result.setSymbolSize(memento.getInteger(SYMBOL_SIZE));
        result.setSectorTransparency(memento.getInteger(SECTOR_TRANSPARENCY));
        return result;
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
        memento.putInteger(SMALLEST_SYMB, style.getSmallestSymb());
        memento.putInteger(SMALL_SYMB, style.getSmallSymb());
        memento.putInteger(LABELING, style.getLabeling());
        memento.putString(FIX_SYMBOL, String.valueOf(style.isFixSymbolSize()));
        memento.putInteger(SYMBOL_SIZE, style.getSymbolSize());
        memento.putInteger(SECTOR_TRANSPARENCY, style.getSectorTransparency());

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
