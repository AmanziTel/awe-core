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

import org.eclipse.swt.graphics.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * contain image by defined size
 * 
 * @author Vladislav_Kondratenko
 */
public class IconContainer {
    /*
     * appropriation with name and image
     */
    private Map<String, Image> container = new HashMap<String, Image>();
    /*
     * image size to store
     */
    private IconSize size;

    /**
     * @return Returns the container.
     */
    public Map<String, Image> getContainer() {
        return container;
    }

    /**
     * create a container
     * 
     * @param size of contained icons
     */
    public IconContainer(IconSize size) {
        this.size = size;
    }

    /**
     * @return Returns the size.
     */
    public IconSize getSize() {
        return size;
    }

    /**
     * add item to container if item is already exist then do nothing
     * 
     * @param imageName key in container
     * @param image value in container
     * @return handled image
     */
    public Image addItemtoContainer(String imageName, Image image) {
        if (container.containsKey(imageName)) {
            return image;
        } else {
            container.put(imageName, image);
        }
        return image;
    }

}
