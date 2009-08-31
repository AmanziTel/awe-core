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

    public NeoStyleContent() {
        super(ID);
    }

    @Override
    public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor) throws IOException {
        if (resource.canResolve(NeoGeoResource.class)) {
            NeoGeoResource res = resource.resolve(NeoGeoResource.class, monitor);
            int transparency = (int)(0.6 * 255.0);
            if (res.getGeoNeo(monitor).getGisType() == GisTypes.Network) {
                return new NeoStyle(Color.DARK_GRAY, new Color(255, 255, 128, transparency), Color.DARK_GRAY);
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
        return new NeoStyle(line, fill, label);
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
