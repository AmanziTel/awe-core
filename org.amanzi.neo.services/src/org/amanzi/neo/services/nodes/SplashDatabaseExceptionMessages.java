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
package org.amanzi.neo.services.nodes;

import java.text.MessageFormat;
import java.util.ResourceBundle;



public class SplashDatabaseExceptionMessages {
    
    private static final String BUNDLE_NAME = SplashDatabaseExceptionMessages.class.getName();
	public static final String Not_Single_Chart_by_ID = null;
	public static final String Not_Single_Chart_Item_by_ID = null;
	public static final String Not_Single_Pie_Chart_Item_by_ID = null;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String Not_Single_Row_by_ID;
    public static String Not_Single_Column_by_ID;
    public static String Not_Single_Cell_by_ID;
    public static String Service_Method_Exception;
    public static String Duplicate_Spreadsheet;
    public static String Loop_In_References;

    
    private SplashDatabaseExceptionMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public static String getFormattedString(String key, String ... args) {
        return MessageFormat.format(key, (Object[])args);
    }
 

}
