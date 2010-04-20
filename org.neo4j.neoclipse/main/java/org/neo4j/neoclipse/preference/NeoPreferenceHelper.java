package org.neo4j.neoclipse.preference;

import java.net.URL;
import java.util.List;

/**
 * Provides additional operations for neoclipse plug-in
 * 
 * @author Pechko_E
 * 
 */
public class NeoPreferenceHelper {
	/**
	 * Icon directories list
	 */
	private List<URL> iconLocations;
    private String iconLocationLabel = DecoratorPreferencePage.NODE_ICONS_LOCATION_LABEL;
    private String iconLocationNode = DecoratorPreferencePage.ICON_LOCATION_NOTE;

	/**
	 * The constructor
	 */
	public NeoPreferenceHelper() {
	}

	/**
	 * Getter for list of available icon directories
	 * 
	 * @return list of icon directories
	 */
	public List<URL> getIconLocations() {
		return iconLocations;
	}

	/**
	 * Setter for list of available icon directories
	 * 
	 * @param iconDirectories
	 *            available icon directories
	 */
	public void setIconLocations(List<URL> iconLocations) {
		this.iconLocations = iconLocations;
	}

    public String getIconLocationLabel() {
        return iconLocationLabel;
    }

    public void setIconLocationLabel(String iconLocationLabel) {
        this.iconLocationLabel = iconLocationLabel;
    }

    public String getIconLocationNote() {
        return iconLocationNode;
    }

    public void setIconLocationNote(String iconLocationNote) {
        this.iconLocationNode = iconLocationNote;
    }

}
