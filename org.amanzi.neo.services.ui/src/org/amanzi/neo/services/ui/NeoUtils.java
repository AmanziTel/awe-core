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

import org.amanzi.neo.services.Utils;
import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.neoclipse.preference.DecoratorPreferences;



/**
 * <p>
 * Utility class that provides common methods for work with neo nodes
 * </p>
 * .
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeoUtils extends Utils{
    /**
     * gets formated node name.
     * 
     * @param node node
     * @param defName the def name
     * @return node name or defValue
     */
    public static String getFormatedNodeName(Node node, String defName) {
        Transaction tx = beginTransaction();
        try {
            String prefStore = org.neo4j.neoclipse.Activator.getDefault().getPreferenceStore().getString(DecoratorPreferences.NODE_PROPERTY_NAMES);
            StringBuilder values = new StringBuilder();
            for (String name : prefStore.split(",")) {
                name = name.trim();
                if ("".equals(name)) {
                    continue;
                }
                Object propertyValue = node.getProperty(name, null);
                if (propertyValue == null) {
                    continue;
                }
                values.append(", ").append(propertyValue.toString());
            }
            return values.length() == 0 ? defName : values.substring(2);
        } finally {
            tx.finish();
        }
    }
    /**
     * Save color in database.
     * 
     * @param node node
     * @param property property name
     * @param rgb color
     * @param service - NeoService - if null, then transaction do not created
     */
    public static void saveColor(Node node, String property, RGB rgb, GraphDatabaseService service) {
        if (node == null || property == null) {
            return;
        }
        Transaction tx = beginTx(service);
        try {
            if (rgb == null) {
                node.removeProperty(property);
            } else {
                int[] array = new int[3];
                array[0] = rgb.red;
                array[1] = rgb.green;
                array[2] = rgb.blue;
                node.setProperty(property, array);
            }
            successTx(tx);
        } finally {
            finishTx(tx);
        }
    }
    /**
     * Gets color.
     * 
     * @param node - node
     * @param property - property name
     * @param defaultColor - default color (return if no color stored in property)
     * @param service - NeoService - if null, then transaction do not created
     * @return RGB
     */
    public static RGB getColor(Node node, String property, RGB defaultColor, GraphDatabaseService service) {
        if (node != null) {
            Transaction tx = beginTx(service);
            try {
                int[] colors = (int[])node.getProperty(property, null);
                if (colors != null) {
                    return new RGB(colors[0], colors[1], colors[2]);
                }
            } finally {
                finishTx(tx);
            }
        }
        return defaultColor;
    }

}
