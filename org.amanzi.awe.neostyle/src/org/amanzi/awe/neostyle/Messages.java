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
package org.amanzi.awe.neostyle;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();
    
    public static String Symbol_Size;
    public static String Symbol_Transparency;
    public static String Symbol_Scale_With_Zoom;
    public static String Symbol_Use_Fixed_Size;
    public static String Symbol_Sizes;
    public static String Labels;
    public static String Small_Symbols;
    public static String Smallest_Symbols;
    public static String Density_Thresholds;
    public static String Color_Label;
    public static String Color_Line;
    public static String Color_Fill_Sector;
    public static String Color_Fill_Site;
    public static String Color_Fill_Drive;
    public static String Symbol_Max_Size;
    public static String Symbol_Def_Beam;

    public static String Font_Size;
    public static String Font_Size_Site;
    public static String Font_Size_Sector;

    public static String Site_Property;
    public static String Point_Property;
    public static String Sector_Property;
    public static String Measurement_Property;

    public static String Icon_Offset;

    public static String Ignore_transsparency;
    public static String Draw_correlation;

    public static String getFormattedString(String key, String ... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
}
