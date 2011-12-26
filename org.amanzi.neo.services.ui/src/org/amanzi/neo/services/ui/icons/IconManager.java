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

package org.amanzi.neo.services.ui.icons;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * this class is response for work with icons
 * 
 * @author Vladislav_Kondratenko
 */
public class IconManager {
    private static final Logger LOGGER = Logger.getLogger(IconManager.class);
    private static IconManager iconManager;
    /**
     * Separator to use in file paths.
     */
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /**
     * Image file EXTENSIONS to look for.
     */
    public static final String[] EXTENSIONS = new String[] {"png", "PNG", "gif", "GIF", "ico", "ICO", "bmp", "BMP", "jpg", "JPG",
            "jpeg", "JPEG", "tif", "TIF", "tiff", "TIFF"};

    private final String ICONS_DIRECTORY = "images/icons";
    private final String EVENTS_DIRECTORY = "images/events";
    private Map<IconSize, IconContainer> existedContainers = new HashMap<IconSize, IconContainer>();
    private List<String> missesIcons = new LinkedList<String>();
    private final static String DOT_SEPARATOR = ".";

    /**
     * get instance of icon manager
     * 
     * @return
     */
    public static IconManager getInstance() {
        if (iconManager == null) {
            iconManager = new IconManager();
        }
        return iconManager;
    }

    /**
     * search default size image only by typeE
     * <p>
     * default size is 16
     * </p>
     * 
     * @param nodeType
     * @return image if found and null if not found
     */
    public Image getImage(INodeType nodeType) {
        return getImage(nodeType, IconSize.SIZE_16);
    }

    /**
     * search image by size and node type
     * 
     * @param nodeType
     * @param size
     * @return image or null if not found
     */
    public Image getImage(INodeType nodeType, IconSize size) {
        String iconName = nodeType.getId();
        String directoryPath = getDirectoryPath(ICONS_DIRECTORY, size.getImageSize());
        return getImage(iconName, directoryPath, size);
    }

    /**
     * search default size image only by event type
     * <p>
     * default size is 16
     * </p>
     * 
     * @param nodeType
     * @return image if found and null if not found
     */
    public Image getImage(EventIcons icon) {
        return getImage(icon, IconSize.SIZE_16);
    }

    /**
     * search image by size and event icon type
     * 
     * @param nodeType
     * @param size
     * @return image or null if not found
     */
    public Image getImage(EventIcons nodeType, IconSize size) {
        String iconName = nodeType.getIconName();
        String directoryPath = getDirectoryPath(EVENTS_DIRECTORY, size.getImageSize());
        return getImage(iconName, directoryPath, size);
    }

    /**
     * search image by name size and directory path
     * 
     * @param iconName the name of image
     * @param directoryPath path to image
     * @param size image size
     * @return found image or null if not found
     */
    private Image getImage(String iconName, String directoryPath, IconSize size) {
        if (iconName == null || iconName.isEmpty()) {
            LOGGER.error("icon name cann't be null");
        }

        // check if current icon is already handled and not be found
        if (missesIcons.contains(iconName)) {
            return null;
        }
        // if container for images defined size is not exist then create new one
        IconContainer container;
        if (!existedContainers.containsKey(size)) {
            container = new IconContainer(size);
            existedContainers.put(size, container);
        } else {
            container = existedContainers.get(size);
        }
        // if current container contain required image then return it
        Map<String, Image> imageContainer = container.getContainer();
        if (imageContainer.containsKey(iconName)) {
            return imageContainer.get(iconName);
        }

        // if current container doesn't contain required image then try to find it in a required
        // directory and add it in container for next fast search
        File directory = new File(directoryPath);
        String[] directoryFile = directory.list();
        for (String file : directoryFile) {
            for (String imgExt : EXTENSIONS) {
                if (file.equals(iconName + DOT_SEPARATOR + imgExt)) {
                    String imgFileName = directoryPath + FILE_SEPARATOR + file;
                    Image image = new Image(Display.getDefault(), imgFileName);
                    imageContainer.put(iconName, image);
                    return image;
                }
            }
        }
        missesIcons.add(iconName);
        return null;
    }

    /**
     * get necessary directory path
     * 
     * @param mainPath
     * @param innerPath
     * @return
     */
    private String getDirectoryPath(String mainPath, String innerPath) {
        URL fileDir;
        try {
            fileDir = FileLocator.toFileURL(Platform.getBundle(NeoServicesUiPlugin.PLUGIN_ID)
                    .findEntries(mainPath, innerPath, false).nextElement());
        } catch (IOException e) {
            return null;
        }
        return fileDir.getPath();
    }
}
