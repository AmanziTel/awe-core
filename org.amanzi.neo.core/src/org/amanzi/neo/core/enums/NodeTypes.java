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

/**
 * <p>
 * Contains all existed node types 
 * </p>
 * @author Saelenchits_N
 * @since 1.0.0
 */
public enum NodeTypes {
    GIS_PROPERTY_TYPE("gis_property_type"),
    SITE("site"),
    TRANSMISSION_TYPE_NAME("transmission"),
    MP_TYPE_NAME("mp"),
    SECTOR("sector"),
    NEIGHBOUR_TYPE_NAME("neighbours"),
    MM("mm"),
    CALL_TYPE_NAME("call"),
    GIS_PROPERTIES("gis_properties"),
    NETWORK("network"),
    HILBERT_INDEX_TYPE("hilbert_index"),
    CALLS_TYPE_NAME("calls"),
    ROOT_SECTOR_DRIVE("root_sector_site"),
    PROBE_TYPE_NAME("probe"),
    SPREADSHEET_NODE_TYPE("spreadsheet"),
    SPLASH_FORMAT_NODE_TYPE("splash_format"),
    SCRIPT_TYPE("ruby_script"),
    RUBY_PROJECT_NODE_TYPE("ruby_project"),
    REPORT_TYPE("report"),
    PIE_CHART_NODE_TYPE("spreadsheet_chart"),
    PIE_CHART_ITEM_NODE_TYPE("spreadsheet_pie_chart_item"),
    CHART_NODE_TYPE("spreadsheet_chart"),
    CHART_ITEM_NODE_TYPE("spreadsheet_chart_item"),
    CELL_NODE_TYPE("spreadsheet_cell"),
    AWE_PROJECT_NODE_TYPE("awe_project"),
    FILE_TYPE_NAME("file"),
    DIRECTORY_TYPE_NAME("directory"),
    DATASET_TYPE_NAME("dataset"),
    HEADER_M("m"),
    AGGREGATION_TYPE_NAME("aggregation"),
    COUNT_TYPE_NAME("count"),
    CALL_ANALYZIS("call analyzis"),
    S_ROW("s_row"),
    GIS("gis"),
    CALL_ANALYZIS_ROOT("call analyzis root"),
    S_CELL("s_cell");
    
    private final String id;

    private NodeTypes(String id) {
        this.id = id;
    }
    
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
//    public boolean isEqualsRule(){
//        return true;
//    }
    public static NodeTypes getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (NodeTypes call : NodeTypes.values()) {
            if (/*call.isEqualsRule()?*/call.getId().equals(enumId)/*:enumId.startsWith(call.getId())*/) {
                return call;
            }
        }
        return null;
    }
}
