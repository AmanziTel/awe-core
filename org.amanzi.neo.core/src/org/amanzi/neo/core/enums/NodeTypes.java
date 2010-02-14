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
    GIS_PROPERTY("gis_property_type"),
    SITE("site"),
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
    AGGREGATION("aggregation"),
    COUNT("count"),
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
