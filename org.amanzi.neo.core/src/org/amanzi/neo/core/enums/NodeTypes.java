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

package org.amanzi.neo.core.enums;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Contains all existed node types 
 * </p>
 * @author Saelenchits_N
 * @since 1.0.0
 */
public enum NodeTypes {
    // TODO: Enum has a cop: org.neo4j.neoclipse.property.NodeTypes
    // TODO: Copy needed for fully implementation of Feature #962
    // TODO: All changes must be reflected in copy
    GIS_PROPERTY("gis_property_type"),
    SITE("site"),
 CITY("city"),
    TRANSMISSION("transmission"),
    MP("mp"),
    SECTOR("sector"),
    NEIGHBOUR("neighbours"),
    MM("mm"),
    CALL("call"),
    GIS_PROPERTIES("gis_properties"),
    NETWORK("network"),
    HILBERT_INDEX("hilbert_index"),
    CALLS("calls"),
    ROOT_SECTOR_DRIVE("root_sector_site"),
    PROBE("probe"),
    SPREADSHEET("spreadsheet"),
    SPLASH_FORMAT("splash_format"),
    SCRIPT("ruby_script"),
    RUBY_PROJECT("ruby_project"),
    REPORT("report"),
    PIE_CHART("spreadsheet_chart"),
    PIE_CHART_ITEM("spreadsheet_pie_chart_item"),
    CHART("spreadsheet_chart"),
    CHART_ITEM("spreadsheet_chart_item"),
    CELL("spreadsheet_cell"),
    AWE_PROJECT("awe_project"),
    FILE("file"),
    DIRECTORY("directory"),
    DATASET("dataset"),
    HEADER_M("m"),
    HEADER_MS("ms"),
    AGGREGATION("aggregation"),
    COUNT("count"),
    CALL_ANALYZIS("call analyzis"),
    S_ROW("s_row"),
    GIS("gis",INeoConstants.PROPERTY_NAME_NAME),
    CALL_ANALYZIS_ROOT("call analyzis root"),
    S_CELL("s_cell"),
    BSC("bsc"),
    DELTA_NETWORK("delta_network"),
    DELTA_SITE("delta_site"), 
    DELTA_SECTOR("delta_sector"), 
    MISSING_SITES("missing_sites"),
    MISSING_SECTORS("missing_sectors"), 
    MISSING_SITE("missing_site"),
    MISSING_SECTOR("missing_sector");
    
    private final String id;
    private Set<String> result;
    
    /**
     * 
     * @param id node type ID
     * @param nonEditableProperties list of not editable properties
     */
    private NodeTypes(String id,String... nonEditableProperties) {
        this.id = id;
        HashSet<String> pr = new HashSet<String>();

        //Geeral not editable properties for all node types
        pr.add(INeoConstants.PROPERTY_TYPE_NAME);
        //end

        if(nonEditableProperties != null){
            for(String property : nonEditableProperties){
                pr.add(property);
            }
        }
        result=Collections.unmodifiableSet(pr);
    }
    
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
/**
 * Returns NodeTypes by its ID
 *
 * @param enumId id of Node Type
 * @return NodeTypes or null
 */
    public static NodeTypes getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (NodeTypes call : NodeTypes.values()) {
            if (call.getId().equals(enumId)) {
                return call;
            }
        }
        return null;
    }
/**
 * returns type of node
 *
 * @param container PropertyContainer
 * @param service NeoService
 * @return type of node 
 */
    public static NodeTypes getNodeType(PropertyContainer container,NeoService service) {
        Transaction tx = service==null?null:service.beginTx();
        try{
            return getEnumById((String)container.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null));
        }finally{
            if (service!=null){
                tx.finish();
            }
        }
    }
/**
 * Returns list of not editable properties
 *
 * @return Collection<String> of not editable properties
 */
    public Collection<String> getNonEditableProperties() {
        return result;
    }
    

}
