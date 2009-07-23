package org.amanzi.neo.core.icons;

import java.util.ArrayList;

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
 * @since 1.1.0
 */

public class IconManager implements IPropertyChangeListener {
    
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
    
    private ArrayList<Viewer> viewers = new ArrayList<Viewer>();
    
    /**
     * Creates IconManager instance
     *
     * @return IconManager
     */
    
    public static IconManager getIconManager() {
        if (iconManager == null) {
            iconManager = new IconManager();
            
            Activator.getDefault().getPreferenceStore().addPropertyChangeListener(iconManager);
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
        //if location of icons was changes than we must re-initialize UserIcons
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
     * @param name name
     * @return image
     */
    
    public Image getImage(String name) {
        if (!name.equals(NEO_ROOT)) {
            //first search for UserIcons
            Image result = icons.getImage(name);
            if (result == null) {
                //if no such Image in UserIcons than search in NeoIcons
                name = "TYPE_" + convertName(name);
                result = NeoIcons.valueOf(name).image();
            }
            
            return result;
        }
        else {
            //if it's name of Root element than compute it
            return NeoIcons.NEO_ROOT.image();            
        }
    }
    
    /**
     * Converts name of Type to name that provides by NeoIcons
     *
     * @param name name
     * @return converted name
     */
    
    private String convertName(String name) {
        if (name.equalsIgnoreCase(Integer.class.getSimpleName())) {
            return INT_TYPE_NAME;
        }
        else {
            return name.toUpperCase();
        }
    }
    
    
}
