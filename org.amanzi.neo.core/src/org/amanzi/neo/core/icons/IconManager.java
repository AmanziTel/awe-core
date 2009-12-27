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
package org.amanzi.neo.core.icons;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.amanzi.neo.core.NeoCorePlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.NeoIcons;
import org.neo4j.neoclipse.decorate.UserIcons;
import org.neo4j.neoclipse.preference.NeoDecoratorPreferences;

/**
 * Class that provides access to Neo icons
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class IconManager implements IPropertyChangeListener {
    public static enum EventIcons {
        CONNECT(new String[] {"sector_6.png", "sector_8.png", "sector_12.png", "sector_16.png", "sector_48.png"}),
        CONNECT_GOOD(new String[] {"sector_good_16.png", "sector_good_16.png", "sector_good_16.png", "sector_good_16.png", "sector_good_16.png"}),
        CONNECT_BAD(new String[] {"sector_bad_16.png", "sector_bad_16.png", "sector_bad_16.png", "sector_bad_16.png", "sector_bad_16.png"}),
        CALL_BLOCKED(new String[] {"event_call_blocked_6.png", "event_call_blocked_8.png", "event_call_blocked_12.png", "event_call_blocked_16.png", "event_call_blocked_48.png"}),
        CALL_DROPPED(new String[] {"event_call_dropped_6.png", "event_call_dropped_8.png", "event_call_dropped_12.png", "event_call_dropped_16.png", "event_call_dropped_48.png"}),
        CALL_FAILURE(new String[] {"event_call_failure_6.png", "event_call_failure_8.png", "event_call_failure_12.png", "event_call_failure_16.png", "event_call_failure_48.png"}),
        CALL_SUCCESS(new String[] {"event_call_success_6.png", "event_call_success_8.png", "event_call_success_12.png", "event_call_success_16.png", "event_call_success_48.png"}),
        HANDOVER_FAILURE(new String[] {"event_handover_failure_6.png", "event_handover_failure_8.png", "event_handover_failure_12.png", "event_handover_failure_16.png", "event_handover_failure_48.png"}),
        HANDOVER_SUCCESS(new String[] {"event_handover_success_6.png", "event_handover_success_8.png", "event_handover_success_12.png", "event_handover_success_16.png", "event_handover_success_48.png"});
         private BufferedImage[] image = new BufferedImage[5];

        EventIcons(String[] fileNames) {
            for (int i = 0; i < fileNames.length; i++) {
                InputStream stream = NeoCorePlugin.getDefault().getClass().getClassLoader().getResourceAsStream(
                        "images/events/" + fileNames[i]);
                try {
                    image[i] = ImageIO.read(stream);
                } catch (Exception e) {
                    // TODO Handle IOException
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
            }

        }

        /**
         * @return Returns the image.
         */
        public java.awt.Image getImage(int size) {
            int index = size == 6 ? 0 : size == 8 ? 1 : size == 12 ? 2 : size == 16 ? 3 : size == 48 ? 4 : 0;
            return image[index];
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
            newLocation = store.getString(NeoDecoratorPreferences.NODE_ICON_LOCATION);
        }

		icons = new UserIcons(newLocation);
	}

	/**
	 * Listens for changing properties
	 */

	public void propertyChange(PropertyChangeEvent event) {
		// if location of icons was changes than we must re-initialize UserIcons
		if (event.getProperty().equals(NeoDecoratorPreferences.NODE_ICON_LOCATION)) {
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
                result = NeoIcons.valueOf(name).image();
            }

            return result;
        } else {
            // if it's name of Root element than compute it
            return NeoIcons.NEO_ROOT.image();
        }
	}
	
	/**
	 * Returns Image for Rollback Button
	 *
	 * @return rollback image
	 */
	public Image getRollbackImage() {
	    return NeoIcons.ROLLBACK_ENABLED.image();
	}
	
	/**
	 * Returns Image for Commit Button
	 *
	 * @return commit image
	 */
	public Image getCommitImage() {
	    return NeoIcons.COMMIT_ENABLED.image();
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
