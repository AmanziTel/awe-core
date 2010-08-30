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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.nodes.DeletableRelationshipType;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.index.PropertyIndex.NeoIndexRelationshipTypes;
import org.eclipse.swt.graphics.Image;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Contains all existed node types 
 * </p>
 * @author Saelenchits_N
 * @since 1.0.0
 */
public enum NodeTypes {
    // TODO: Enum has a copy: org.neo4j.neoclipse.property.NodeTypes
    // TODO: Copy needed for fully implementation of Feature #962
    // TODO: All changes must be reflected in copy
    GIS_PROPERTY("gis_property_type"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            boolean isLinkOut = isLinkOut(aNode, cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.PROPERTIES)){                
                if (isLinkOut) {
                    return NodeDeletableTypes.UNLINK;
                }else{
                    return NodeDeletableTypes.DELETE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if (isLinkOut) {
                    return NodeDeletableTypes.UNLINK;
                }else{
                    return NodeDeletableTypes.DELETE;
                }
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    SITE("site"),
    CITY("city"),
    TRANSMISSION("transmission"),
    MP("mp"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            for(Relationship link : aNode.getRelationships(Direction.INCOMING)){
                if(!link.equals(cameFrom)){
                    RelationshipType type = link.getType();
                    if(type.equals(GeoNeoRelationshipTypes.LOCATION)){
                        return NodeDeletableTypes.UNLINK;
                    }
                }
            }
            return NodeDeletableTypes.DELETE;
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    SECTOR("sector"),
    RNC("rnc"),
    NEIGHBOUR("neighbours"),
    MM("mm"),
    CALL("call"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            boolean isLinkOut = isLinkOut(aNode, cameFrom);
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if(isLinkOut){
                    return NodeDeletableTypes.UNLINK;
                }
                else {
                    return NodeDeletableTypes.DELETE_LINE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            if(linkType.equals(ProbeCallRelationshipType.CALLER)
               ||linkType.equals(ProbeCallRelationshipType.CALLEE)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.SOURCE)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            ArrayList<NodeTypes> res = new ArrayList<NodeTypes>();
            res.add(DATASET);
            return res;
        }
    },
    GIS_PROPERTIES("properties"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.PROPERTIES)){
                boolean isLinkOut = isLinkOut(aNode, cameFrom);
                if (isLinkOut) {
                    return NodeDeletableTypes.UNLINK;
                }else{
                    return NodeDeletableTypes.DELETE;
                }
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    NETWORK("network"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                boolean isLinkOut = isLinkOut(aNode, cameFrom);
                if (isLinkOut) {
                    return NodeDeletableTypes.RELINK;
                }else{
                    return NodeDeletableTypes.UNLINK;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.DELETE;
            }else if(linkType.equals(GeoNeoRelationshipTypes.USE_FILTER)){
                return NodeDeletableTypes.DELETE;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    HILBERT_INDEX("hilbert_index",false),
    CALLS("calls"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(ProbeCallRelationshipType.PROBE_DATASET)){
                return NodeDeletableTypes.DELETE;
            }
            if(linkType.equals(ProbeCallRelationshipType.CALLEE)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(ProbeCallRelationshipType.CALLER)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(ProbeCallRelationshipType.CALLS)){
                return NodeDeletableTypes.DELETE;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    NTPQS("ntpqs"),
    ROOT_SECTOR_DRIVE("root_sector_site"),
    PROBE("probe"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                return NodeDeletableTypes.DELETE;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.LOCATION)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.SOURCE)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(ProbeCallRelationshipType.CALLS)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(ProbeCallRelationshipType.NTPQS)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
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
    GPEH_CELL_ROOT("cell_root"),
    GPEH_CELL("gpeh_cell"),
    AWE_PROJECT("awe_project"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(SplashRelationshipTypes.AWE_PROJECT)){
                return NodeDeletableTypes.DELETE;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    FILE("file"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            boolean isLinkOut = cameFrom.getStartNode().equals(aNode);
            DeletableRelationshipType linkType = (DeletableRelationshipType)cameFrom.getType();
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if(isLinkOut){
                    return NodeDeletableTypes.RELINK;
                }
                else {
                    return NodeDeletableTypes.DELETE_LINE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            ArrayList<NodeTypes> res = new ArrayList<NodeTypes>();
            res.add(DATASET);
            res.add(DIRECTORY);
            return res;
        }
    },
    DIRECTORY("directory"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            boolean isLinkOut = cameFrom.getStartNode().equals(aNode);
            DeletableRelationshipType linkType = (DeletableRelationshipType)cameFrom.getType();
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if(isLinkOut){
                    return NodeDeletableTypes.RELINK;
                }
                else {
                    return NodeDeletableTypes.DELETE_LINE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            ArrayList<NodeTypes> res = new ArrayList<NodeTypes>();
            res.add(DATASET);
            return res;
        }
    },
    DATASET("dataset"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                Node other = cameFrom.getOtherNode(aNode);
                if(getNodeType(other, null).equals(GIS)){
                    return NodeDeletableTypes.DELETE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.VIRTUAL_DATASET)){
                if(isLinkOut(aNode, cameFrom)){
                    return NodeDeletableTypes.UNLINK;
                }
                else{
                    return NodeDeletableTypes.DELETE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                return NodeDeletableTypes.UNLINK;
            }
            if(linkType.equals(ProbeCallRelationshipType.CALL_ANALYSIS)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    M_AGGR("m_aggr"),
    M("m"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = (DeletableRelationshipType)cameFrom.getType();
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                Node other = cameFrom.getOtherNode(aNode);
                if(getNodeType(other, null).equals(FILE)){
                    return NodeDeletableTypes.DELETE_LINE; 
                }
                else {
                    return NodeDeletableTypes.UNLINK; 
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.LOCATION)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        
        @Override
        protected List<NodeTypes> getParentTypes() {
            ArrayList<NodeTypes> res = new ArrayList<NodeTypes>();
            res.add(FILE);
            return res;
        }
    },
    HEADER_MS("ms"),
    AGGREGATION("aggregation"),
    COUNT("count"),
    CALL_ANALYSIS("call analysis"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if(isLinkOut(aNode, cameFrom)){
                    return NodeDeletableTypes.UNLINK;
                }
                else{
                    return NodeDeletableTypes.DELETE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.SOURCE)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            ArrayList<NodeTypes> res = new ArrayList<NodeTypes>();
            res.add(CALL_ANALYSIS_ROOT);
            return res;
        }
    },
    S_ROW("s_row"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if(isLinkOut(aNode, cameFrom)){
                    return NodeDeletableTypes.RELINK;
                }
                else {
                    return NodeDeletableTypes.DELETE_LINE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.SOURCE)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            ArrayList<NodeTypes> res = new ArrayList<NodeTypes>();
            res.add(CALL_ANALYSIS);
            res.add(S_GROUP);
            return res;
        }
    },
    GIS("gis",INeoConstants.PROPERTY_NAME_NAME){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                return NodeDeletableTypes.DELETE;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.DELETE;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    CALL_ANALYSIS_ROOT("call analysis root"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(ProbeCallRelationshipType.CALL_ANALYSIS)){
                return NodeDeletableTypes.DELETE;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            return false;
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    S_CELL("s_cell"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if(isLinkOut(aNode, cameFrom)){
                    return NodeDeletableTypes.RELINK;
                }
                else {
                    return NodeDeletableTypes.DELETE_LINE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.SOURCE)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            ArrayList<NodeTypes> res = new ArrayList<NodeTypes>();
            res.add(S_ROW);
            return res;
        }
    },
    S_GROUP("s_group"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                if(isLinkOut(aNode, cameFrom)){
                    return NodeDeletableTypes.RELINK;
                }
                else {
                    return NodeDeletableTypes.DELETE_LINE;
                }
            }
            if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            if(linkType.equals(GeoNeoRelationshipTypes.SOURCE)){
                return NodeDeletableTypes.UNLINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    BSC("bsc"),
    DELTA_NETWORK("delta_network"),
    DELTA_SITE("delta_site"), 
    DELTA_SECTOR("delta_sector"), 
    MISSING_SITES("missing_sites"),
    MISSING_SECTORS("missing_sectors"), 
    MISSING_SITE("missing_site"),
    MISSING_SECTOR("missing_sector"),
    MULTI_INDEX("multi_index"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            DeletableRelationshipType linkType = getLinkType(cameFrom);
            if(linkType.equals(NeoIndexRelationshipTypes.IND_CHILD)){
                for(Relationship link : aNode.getRelationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)){
                    if(!link.equals(cameFrom)){
                        return NodeDeletableTypes.UNLINK;
                    }
                }
                return NodeDeletableTypes.DELETE; 
            }
            if(linkType.equals(NeoIndexRelationshipTypes.INDEX)){
                return NodeDeletableTypes.DELETE;
            }
            if(linkType.equals(NeoIndexRelationshipTypes.IND_NEXT)){
                return NodeDeletableTypes.RELINK;
            }
            throw new IllegalArgumentException("Unknown link type <"+linkType.name()+">.");
        }
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
            RelationshipType linkType = link.getType();
            return linkType.equals(NeoIndexRelationshipTypes.IND_NEXT)
                    || linkType.equals(NeoIndexRelationshipTypes.INDEX);
        }
        @Override
        protected List<NodeTypes> getParentTypes() {
            return null;
        }
    },
    AFP("afp"),
    AFP_CELL("afp_cell"),
    SECTOR_SECTOR_RELATIONS("sector_sector_relations"),
    OSS("oss"), 
    GPEH_EVENT("gpeh_event"), 
    OSS_MAIN("oss_main"),
//    URBAN_CONFIG("urban_config"),
    FILTER("filter"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            return checkDeletableByTypeForCorrectStructure(aNode, cameFrom); 
        }
        
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link) {
            return isGoodLinkForCorrectStructure(aNode, cameFrom, link);
        }
    },
    FILTER_GROUP("filter_group"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            return checkDeletableByTypeForCorrectStructure(aNode, cameFrom); 
        }
        
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link) {
            return isGoodLinkForCorrectStructure(aNode, cameFrom, link);
        }
    },
    FILTER_CHAIN("filter_chain"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            return checkDeletableByTypeForCorrectStructure(aNode, cameFrom); 
        }
        
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link) {
            return isGoodLinkForCorrectStructure(aNode, cameFrom, link);
        }
    },
    FILTER_ROOT("filter_root"){
        @Override
        protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
            return NodeDeletableTypes.DELETE_LINE; 
        }
        
        @Override
        protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link) {
            return isGoodLinkForCorrectStructure(aNode, cameFrom, link);
        }
    }, UTRAN_DATA("utran_data");
    
    private final String id;
    private boolean nodeReadOnly;
    private Set<String> notEditableProperties;
    
    /**
     * Constructor with list of properties that can't be modified for current node type 
     * @param id node type ID
     * @param notEditableProperties list of not editable properties
     */
    private NodeTypes(String id, String... notEditableProperties) {
        this.id = id;
        Set<String> pr = new HashSet<String>();
        applyGeneralNotEditablePropertis();
        if(notEditableProperties != null){
            pr.addAll(Arrays.asList(notEditableProperties));
        }
        updatePropertySet(pr);
    }
    
    /**
     * 
     * @param id node type ID
     * @param isNodeReadOnly true if all properties of current node can not be edited.
     */
    private NodeTypes(String id, boolean isNodeReadOnly) {
        this.id = id;

        if(isNodeReadOnly){
            this.nodeReadOnly = isNodeReadOnly;
        }else {
            applyGeneralNotEditablePropertis();
        }
    }
    
    /**
     * Apply list of not editable properties that actual for any nodeTypes
     */
    private void applyGeneralNotEditablePropertis() {
        Set<String> properties = new HashSet<String>();
        
        //General not editable properties for all node types
        properties.add(INeoConstants.PROPERTY_TYPE_NAME);
        //end
        
        updatePropertySet(properties);
    }
/**
 * Update set of not editable properties with new values.
 *
 * @param update set with properties must be added
 */
    private void updatePropertySet(Set<String> update) {
        if(notEditableProperties != null){
            update.addAll(notEditableProperties);
        }
        notEditableProperties=Collections.unmodifiableSet(update);
    }
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Returns the Image for current nodeType.
     */
    public Image getImage() {
        return IconManager.getIconManager().getImage(id);
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
    public static NodeTypes getNodeType(PropertyContainer container,GraphDatabaseService service) {
        Transaction tx = service==null?null:service.beginTx();
        try{
            if (container == null) {
                return null;
            }
            return getEnumById((String)container.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null));
        }finally{
            if (service!=null){
                tx.finish();
            }
        }
    }
    
    /**
     * Returns Node deleting type.
     *
     * @param aNode Node for delete
     * @param cameFrom Relationship by what the node need to be delete 
     * @param service NeoService
     * @return NodeDeletableTypes
     */
    public static NodeDeletableTypes getNodeDeletableType(Node aNode, Relationship cameFrom, GraphDatabaseService service){
        return getNodeType(aNode, service).getDeletableType(aNode, cameFrom, service);
    }
    
    /**
     * Is node can not be delete?
     * @param aNode Node for check
     * @param service NeoService
     * @return boolean
     */
    public static boolean isNodeFixed(Node aNode, GraphDatabaseService service){
        return getNodeType(aNode, service).isFixedType();
    }
    
    /**
     * Get pair for relationship for create new one. 
     *
     * @param aNode Node
     * @param cameFrom Relationship that need a Pair
     * @param service NeoService
     * @return Relationship
     */
    public static Relationship getSecondLinkForRelink(Node aNode, Relationship cameFrom, GraphDatabaseService service){
        return getNodeType(aNode, service).getSecondLinkForRelinkByType(aNode, cameFrom, service);
    }
    
    /**
     * Returns Node deleting type.
     *
     * @param aNode Node for delete
     * @param cameFrom Relationship by what the node need to be delete 
     * @param service NeoService
     * @return NodeDeletableTypes
     */
    protected NodeDeletableTypes getDeletableType(Node aNode, Relationship cameFrom, GraphDatabaseService service){
        Transaction tx = service.beginTx();
        try{
            if(isFixedType()||hasFixedLinks(aNode)){
                return NodeDeletableTypes.FIXED;
            }
            return checkDeletableByType(aNode, cameFrom);
        }
        finally{
            tx.finish();
        }
    }
    
    /**
     * Returns Node deleting type.
     *
     * @param aNode Node for delete
     * @param cameFrom Relationship by what the node need to be delete 
     * @return NodeDeletableTypes
     */
    protected NodeDeletableTypes checkDeletableByType(Node aNode, Relationship cameFrom){
        throw new UnsupportedOperationException("Method should be overrided for even enumeration element! Problem type is "+id+".");
    }
    
    /**
     * Is node of this type can not be delete?
     *
     * @return boolean
     */
    protected boolean isFixedType(){
        return false;
    }
    
    /**
     * Get pair for relationship for create new one. 
     *
     * @param aNode Node
     * @param cameFrom Relationship that need a Pair
     * @param service NeoService
     * @return Relationship
     */
    protected Relationship getSecondLinkForRelinkByType(Node aNode, Relationship cameFrom, GraphDatabaseService service){
        Transaction tx = service.beginTx();
        try{
            boolean isLinkOut = cameFrom.getStartNode().equals(aNode);
            Direction direction = isLinkOut?Direction.INCOMING:Direction.OUTGOING;
            for(Relationship link : aNode.getRelationships(direction)){
                if(isGoodLink(aNode, cameFrom, link)){
                    return link;
                }
            }
            return null;
        }
        finally{
            tx.finish();
        }
    }
    
    /**
     * Is relationship is a pair for 'cameFrom' relationship?
     *
     * @param aNode Node
     * @param cameFrom Relationship that need a Pair.
     * @param link Relationship for check
     * @return boolean
     */
    protected boolean isGoodLink(Node aNode, Relationship cameFrom, Relationship link){
        RelationshipType linkType = link.getType();
        if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
            return true;
        }
        if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
            Node other = link.getOtherNode(aNode);
            NodeTypes nodeType = getNodeType(other, null);
            if(getParentTypes().contains(nodeType)){
                return true; 
            }
        }
        return false;
    }
    
    /**
     * Returns list of NodeTypes, that can be parents for node.
     *
     * @return List<NodeTypes>
     */
    protected List<NodeTypes> getParentTypes() {
        throw new UnsupportedOperationException("Method should be overrided for even enumeration element! Problem type is "+id+".");
    }
    
    /**
     * Is node has relationships with deleting type FIXED. 
     *
     * @param aNode Node
     * @return boolean
     */
    protected boolean hasFixedLinks(Node aNode){
        Direction[] directions = new Direction[] {Direction.OUTGOING, Direction.INCOMING};
        //TODO: Lagutko: implement like this
        for (Direction singleDirection : directions) {
            for (Relationship link : aNode.getRelationships(singleDirection)) {
                DeletableRelationshipType linkType = NeoUtils.getRelationType(link);
                RelationDeletableTypes deletable = linkType.getDeletableType(singleDirection);
                if(deletable.equals(RelationDeletableTypes.FIXED)){
                    return true;
                }
            }            
        }
        return false;
    }
    
    /**
     * Check if property editable for current node
     *
     * @param property property to check
     * @return true if property editable
     */
    public boolean isPropertyEditable(String property) {
        if(nodeReadOnly)
            return false;
        return !notEditableProperties.contains(property);  
    }
    
    /**
     * Is relationship outgoing from node?
     *
     * @param aNode Node for check
     * @param cameFrom  Relationship for check
     * @return boolean
     */
    protected boolean isLinkOut(Node aNode, Relationship cameFrom) {
        return cameFrom.getStartNode().equals(aNode);
    }
    
    /**
     * Returns relationship type
     *
     * @param cameFrom Relationship
     * @return DeletableRelationshipType
     */
    protected DeletableRelationshipType getLinkType(Relationship cameFrom) {
        return NeoUtils.getRelationType(cameFrom);
    }

    /**
     * Check node by type
     * 
     * @param currentNode - node
     * @return true if node type
     */
    public boolean checkNode(Node currentNode) {
        return getId().equals(NeoUtils.getNodeType(currentNode, ""));
    }

    /**
     * save type in node
     * 
     * @param container PropertyContainer
     * @param service - neoservice. if null then new transaction not created
     */
    public void setNodeType(PropertyContainer container, GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            container.setProperty(INeoConstants.PROPERTY_TYPE_NAME, getId());
            NeoUtils.successTx(tx);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }
    private static NodeDeletableTypes checkDeletableByTypeForCorrectStructure(Node aNode, Relationship cameFrom){
        //TODO refactor
        DeletableRelationshipType linkType = NeoUtils.getRelationType(cameFrom);
        if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                return NodeDeletableTypes.DELETE_LINE; 
        }
        if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
            return NodeDeletableTypes.RELINK;
        }
        return NodeDeletableTypes.UNLINK;
    }
    /**
     * Is relationship is a pair for 'cameFrom' relationship?
     *
     * @param aNode Node
     * @param cameFrom Relationship that need a Pair.
     * @param link Relationship for check
     * @return boolean
     */
    private static boolean isGoodLinkForCorrectStructure(Node aNode, Relationship cameFrom, Relationship link){
       //TODO refactor
        RelationshipType linkType = link.getType();
        if(linkType.equals(GeoNeoRelationshipTypes.NEXT)){
            return true;
        }
        if(linkType.equals(GeoNeoRelationshipTypes.CHILD)){
                return true; 
        }
        return false;
    }

}