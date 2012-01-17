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

/**
 * contains images size
 * 
 * @author Vladislav_Kondratenko
 */
public enum IconSize {

    SIZE_16("16"), SIZE_32("32"), SIZE_48("48"), SIZE_64("64"), SIZE_128("128"), SIZE_256("256");

    private String size;

    private IconSize(String imageSize) {
        this.size = imageSize;
    }

    /**
     * return image size appropriate to directory name or
     * <p>
     * to name ending( for events icons)
     * </p>
     * 
     * @return
     */
    public String getImageSize() {
        return size;
    }

}
