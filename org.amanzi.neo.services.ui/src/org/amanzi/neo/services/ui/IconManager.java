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
package org.amanzi.neo.services.ui;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.Icons;
import org.neo4j.neoclipse.decorate.UserIcons;
import org.neo4j.neoclipse.preference.DecoratorPreferences;

/**
 * Class that provides access to Neo icons
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class IconManager implements IPropertyChangeListener {
    public static enum EventIcons {
        CONNECT("event_normal"),
        CONNECT_GOOD("event_good"),
        CONNECT_BAD("event_bad"),
        CALL_BLOCKED("event_call_blocked"),
        CALL_DROPPED("event_call_dropped"),
        CALL_FAILURE("event_call_failure"),
        CALL_SUCCESS("event_call_success"),
        HANDOVER_FAILURE("event_handover_failure"),
        HANDOVER_SUCCESS("event_handover_success");
        private String fileName;
        private HashMap<Integer,BufferedImage> images = new HashMap<Integer,BufferedImage>();

        EventIcons(String fileName) {
            this.fileName = fileName;
            for(int size: new Integer[]{6,8,12,16,32,48}){
                loadImage(size);
            }
        }
        private void loadImage(int size) {
            InputStream stream = NeoServicesUiPlugin.getDefault().getClass().getClassLoader().getResourceAsStream("images/events/" + fileName + "_" + size + ".png");
            try {
                images.put(size,ImageIO.read(stream));
            } catch (Exception e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }

        /**
         * @return Returns the image.
         */
        public java.awt.Image getImage(int size) {
            if (!images.containsKey(size))
                loadImage(size);
            return images.get(size);
        }

    }
	/**
	 * file name of network icons
	 */
	public static final String NETWORK_ICON = "network";
	/*
	 * Name of Integer type supported by NeoIcons
	 */
	private static final String INT_TYPE_NAME = "INT";

	/*
	 * Name of Root element for Neo
	 */
	public static final String NEO_ROOT = "neo_root";

	/*
	 * Instance of IconManager
	 */
	private static IconManager iconManager = null;

	/*
	 * User specific Neo Icons
	 */
	private UserIcons icons = null;

	private ArrayList<Viewer> viewers = new ArrayList<Viewer>(0);

	/**
	 * Creates IconManager instance
	 * 
	 * @return IconManager
	 */

	public static IconManager getIconManager() {
		if (iconManager == null) {
			iconManager = new IconManager();

			Activator.getDefault().getPreferenceStore()
					.addPropertyChangeListener(iconManager);
		}

		return iconManager;
	}

	/**
	 * Constructor for IconManager
	 */

	protected IconManager() {
		updateUserIcons(null);
	}

	/**
     * Updates UserIcons from given location
     * 
     * @param newLocation location of UserIcons (if null than location computes from NeoPreferences)
     */

	private void updateUserIcons(String newLocation) {
		if (newLocation == null) {
            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            newLocation = store.getString(DecoratorPreferences.NODE_ICON_LOCATION);
        }

		icons = new UserIcons(newLocation);
	}

	/**
	 * Listens for changing properties
	 */

	public void propertyChange(PropertyChangeEvent event) {
		// if location of icons was changes than we must re-initialize UserIcons
		if (event.getProperty().equals(DecoratorPreferences.NODE_ICON_LOCATION)) {
            updateUserIcons((String)event.getNewValue());

            for (Viewer singleViewer : viewers) {
                singleViewer.refresh();
            }
        }
	}

	/**
	 * Add Viewer for IconManager
	 * 
	 * All Viewer will be refreshed if icon location was changed
	 * 
	 * @param viewer
	 */

	public void addViewer(Viewer viewer) {
		viewers.add(viewer);
	}

	/**
	 * Returns image for given name
	 * 
	 * @param name
	 *            name
	 * @return image
	 */

	public Image getImage(String name) {
		if (!name.equals(NEO_ROOT)) {
            // first search for UserIcons
            Image result = icons.getImage(name);
            if (result == null) {
                // if no such Image in UserIcons than search in NeoIcons
                name = "TYPE_" + convertName(name);
                try {
                    result = Icons.valueOf(name).image();
                } catch (IllegalArgumentException e) {
                    // TODO check returned img for user defined types
                    return Icons.NEW_TYPE_ENABLED.image();
                }
            }

            return result;
        } else {
            // if it's name of Root element than compute it
            return Icons.NEO_ROOT.image();
        }
	}
	
	/**
	 * Returns Image for Rollback Button
	 *
	 * @return rollback image
	 */
	public Image getRollbackImage() {
	    return Icons.ROLLBACK_ENABLED.image();
	}
	
	/**
	 * Returns Image for Commit Button
	 *
	 * @return commit image
	 */
	public Image getCommitImage() {
	    return Icons.COMMIT_ENABLED.image();
	}

    /**
     * Returns Image for Commit Button
     * 
     * @return commit image
     */
    public Image getNeoImage(String neoName) {
        return Icons.valueOf(neoName).image();
    }
	/**
	 * Converts name of Type to name that provides by NeoIcons
	 * 
	 * @param name
	 *            name
	 * @return converted name
	 */

	private String convertName(String name) {
		if (name.equalsIgnoreCase(Integer.class.getSimpleName())) {
			return INT_TYPE_NAME;
		} else {
			return name.toUpperCase();
		}
	}

}
