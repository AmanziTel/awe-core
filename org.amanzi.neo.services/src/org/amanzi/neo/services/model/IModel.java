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

package org.amanzi.neo.services.model;

import org.amanzi.neo.services.enums.INodeType;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IModel {
 
    // constants
    public final static String DRIVE_TYPE = "drive_type";
    public final static String TIMESTAMP = "timestamp";
    public final static String LATITUDE = "lat";
    public final static String LONGITUDE = "lon";
    public final static String PATH = "path";
    public final static String COUNT = "count";
    public final static String PRIMARY_TYPE = "primary_type";
    public final static String MIN_TIMESTAMP = "min_timestamp";
    public final static String MAX_TIMESTAMP = "max_timestamp";
    public final static String MIN_LATITUDE = "min_latitude";
    public final static String MIN_LONGITUDE = "min_longitude";
    public final static String MAX_LATITUDE = "max_latitude";
    public final static String MAX_LONGITUDE = "max_longitude";

    /**
     * The name of a model is usually the value of NAME property of the model root node.
     * 
     * @return the name of the model
     */
    public String getName();

    /**
     * @return root node of a model
     */
    public Node getRootNode();

    /**
     * @return ???
     */
    public INodeType getType();
}
