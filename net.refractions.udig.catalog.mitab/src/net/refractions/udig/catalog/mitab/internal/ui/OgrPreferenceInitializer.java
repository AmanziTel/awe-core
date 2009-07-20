package net.refractions.udig.catalog.mitab.internal.ui;

import java.io.File;
import java.io.FileFilter;

import net.refractions.udig.catalog.mitab.internal.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class OgrPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore preferenceStore = Activator.getInstance().getPreferenceStore();
        String path=findOgr2ogr(preferenceStore.getString(OgrPreferencePage.executablePathKey));
        preferenceStore.setDefault(OgrPreferencePage.executablePathKey, path==null?"":path);
    }

    /**
     * Searches for ogr2ogr location, starting with suggested location or null.
     * This could be a previous user choice of previously found location.
     * 
     * @param suggested ogr2ogr location
     * @return location found
     */
    private String findOgr2ogr(String suggested) {
        String ogr2ogr = null;
        if (suggested != null && suggested.matches(".*FWTools.*ogr2ogr.*") && (new File(suggested).exists())) {
            return suggested;
        }
        String userDir = System.getProperty("user.home");
        for (String path : new String[] {".", "C:/Program Files", "/usr/lib", "/usr/local/lib", userDir, userDir + "/dev"}) {
            String dir = findFWToolsDirectory(path);
            try {
                if (dir != null) {
                    path = path + "/" + dir;
                    // For linux
                    if ((new java.io.File(path + "/bin_safe")).isDirectory()
                            && (new java.io.File(path + "/bin_safe/ogr2ogr")).exists()) {
                        ogr2ogr = path + "/bin_safe/ogr2ogr";
                        break;
                    }
                    // For windows
                    if ((new java.io.File(path + "/bin")).isDirectory() && (new java.io.File(path + "/bin/ogr2ogr.exe")).exists()) {
                        ogr2ogr = path + "/bin/ogr2ogr.exe";
                        break;
                    }
                }

            } catch (Exception e) {
                System.err.println("Failed to process possible ogr2ogr path '" + path + "': " + e.getMessage());
            }
        }
        return ogr2ogr;
    }

    /**
     * Searches for FWTools directory location without regard for version
     * 
     * @param path path to find FWTools directory location
     * @return directory name if it was found, otherwise null
     */
    private String findFWToolsDirectory(String path) {
        FileFilter filter = new FileFilter(){

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().startsWith("FWTools");
            }

        };

        File[] dirs = (new File(path)).listFiles(filter);
        return dirs == null || dirs.length == 0 ? null : dirs[0].getName();
    }
}
