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

package org.amanzi.neo.services.enums;

/**
 * <p>
 * contains enumeration of possible band values;
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum Band {

    BAND_850("GSM800"), BAND_900("GSM900"), BAND_1800("GSM1800"), BAND_1900("GSM1900");

    String id = "";

    Band(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
