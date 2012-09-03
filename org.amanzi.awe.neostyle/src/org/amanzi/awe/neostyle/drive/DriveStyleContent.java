/**
 * 
 */
package org.amanzi.awe.neostyle.drive;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * @author Bondoronok_P
 */
public class DriveStyleContent extends StyleContent {

    public static final String ID = "org.amanzi.awe.neostyle.style.drive";
    public static final Integer DEFAULT_LOCATION_LABEL_FONT_SIZE = 8;
    public static final String WHITHOUT_NAME = "";
    public static final String TIMESTAMP = "timestamp";
    public static final String MEASUREMENT = "measurement";

    private static final Color DEFAULT_LINE_COLOR = Color.DARK_GRAY;
    private static final Color DEFAULT_LABEL_COLOR = Color.DARK_GRAY;
    private static final Color DEFAULT_LOCATION_COLOR = Color.MAGENTA;

    private static final String LINE_PRFX = "NET_LINE";
    private static final String LABEL_PRFX = "NET_LABEL";
    private static final String LOCATION_PRFX = "NET_LOCATION";
    private static final String COLOR_RGB = "NET_RGB";
    private static final String FONT_SIZE = "NET_FONT_SIZE";
    private static final String LOCATION_NAME = "NET_LOCATION_NAME_PROPERTY";
    private static final String LOCATON_LABEL_TYPE = "NET_LOCATION_LABEL_TYPE";

    /**
     * The Constructor
     */
    public DriveStyleContent() {
        super(ID);
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.StyleContent#getStyleClass()
     */
    @Override
    public Class<DriveStyle> getStyleClass() {
        return DriveStyle.class;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.StyleContent#save(org.eclipse.ui.IMemento,
     * java.lang.Object)
     */
    @Override
    public void save(IMemento memento, Object value) {
        DriveStyle driveStyle = (DriveStyle)value;
        saveColor(memento, LINE_PRFX, driveStyle.getLineColor());
        saveColor(memento, LOCATION_PRFX, driveStyle.getLocationColor());
        saveColor(memento, LABEL_PRFX, driveStyle.getLabelColor());
        memento.putInteger(FONT_SIZE, driveStyle.getFontSize());
        memento.putString(LOCATON_LABEL_TYPE, driveStyle.getLocationLabelType());
        memento.putString(LOCATION_NAME, driveStyle.getMeasurementNameProperty());
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.StyleContent#load(org.eclipse.ui.IMemento)
     */
    @Override
    public Object load(IMemento memento) {
        Color line = loadColor(memento, LINE_PRFX, Color.BLACK);
        Color location = loadColor(memento, LOCATION_PRFX, Color.BLACK);
        Color label = loadColor(memento, LABEL_PRFX, Color.BLACK);
        DriveStyle driveStyle = new DriveStyle();
        driveStyle.setLabelColor(label);
        driveStyle.setLineColor(line);
        driveStyle.setLocationColor(location);
        driveStyle.setFontSize(memento.getInteger(FONT_SIZE));
        driveStyle.setMeasurementNameProperty(memento.getString(LOCATION_NAME));
        driveStyle.setLocationLabelType(memento.getString(LOCATON_LABEL_TYPE));
        return driveStyle;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.StyleContent#load(java.net.URL,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public Object load(URL url, IProgressMonitor monitor) throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.StyleContent#createDefaultStyle(net.refractions
     * .udig.catalog.IGeoResource, java.awt.Color, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public DriveStyle createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor) throws IOException {
        DriveStyle driveStyle = new DriveStyle();
        driveStyle.setFontSize(DEFAULT_LOCATION_LABEL_FONT_SIZE);
        driveStyle.setLabelColor(DEFAULT_LABEL_COLOR);
        driveStyle.setLineColor(DEFAULT_LINE_COLOR);
        driveStyle.setLocationColor(DEFAULT_LOCATION_COLOR);
        driveStyle.setMeasurementNameProperty(TIMESTAMP);
        driveStyle.setLocationLabelType(WHITHOUT_NAME);
        return driveStyle;
    }

    /**
     * Load color from memento
     * 
     * @param memento
     * @param prfx prefix
     * @param defColor default color
     * @return loaded color from memento or null
     */
    private Color loadColor(IMemento memento, String prfx, Color defColor) {
        Integer rgb = memento.getInteger(prfx + COLOR_RGB);
        return rgb == null ? defColor : new Color(rgb);
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
