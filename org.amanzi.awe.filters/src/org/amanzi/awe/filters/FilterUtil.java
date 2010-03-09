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

package org.amanzi.awe.filters;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * </p>
 * 
 * @author Дом
 * @since 1.0.0
 */
public class FilterUtil {
    private FilterUtil() {
    }

    public static final String PROPERTY_FILTERED_NAME = "filter_property";
    public static final String PROPERTY_FILTERED_VALID = "valid";
    public static final String PROPERTY_FIRST = "first";
    public static final String PROPERTY_SECOND = "second";
    public static final String PROPERTY_FIRST_TXT = "firstTXT";
    public static final String PROPERTY_SECOND_REL = "second_rel";
    public static final String PROPERTY_SECOND_TXT = "secondTXT";

    public static String getGroupProperty(Node node, String defValue, NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            return (String)node.getProperty(PROPERTY_FILTERED_NAME, defValue);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * @return
     */
    public static String[] getFilterDes() {
        return new String[] {"<", "<=", "==", ">", ">=", "!="};
    }

    /**
     * @return
     */
    public static String[] getFilterRel() {
        return new String[] {"||", "&&"};
    }

    /**
     * @param node
     * @param property
     * @param service
     */
    public static void setGroupProperty(Node node, String property, NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            node.setProperty(PROPERTY_FILTERED_NAME, property);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * @param node
     * @param isValid
     * @param service
     */
    public static void setFilterValid(Node node, Boolean isValid, NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            node.setProperty(PROPERTY_FILTERED_VALID, isValid);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * @param dataNode
     * @param service
     * @return
     */
    public static AbstractFilter getFilterOfData(Node dataNode,  NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Relationship filterRelation = dataNode.getSingleRelationship(GeoNeoRelationshipTypes.USE_FILTER, Direction.OUTGOING);
            if (filterRelation==null){
                return null;
            }
            return AbstractFilter.getInstance(filterRelation.getOtherNode(dataNode), service);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * @param node
     * @param isValid
     * @param service
     */
    public static Boolean isFilterValid(Node node, NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            return (Boolean)node.getProperty(PROPERTY_FILTERED_VALID, false);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }
}
