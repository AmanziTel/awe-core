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

package org.amanzi.awe.afp.loaders;

import org.amanzi.awe.afp.AfpNeighbourSubType;
import org.amanzi.neo.loader.core.parser.LineTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Afp saver
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AfpSaver extends AbstractHeaderSaver<LineTransferData> implements IStructuredSaver<LineTransferData> {
    private int fileNum;
    private AfpFileTypes type;
    private boolean skipLoadFile;
    private Node afpNeigh;
    private String neighName;
    private Node lastSector;
    private Node serve;

    @Override
    public void init(LineTransferData element) {
        super.init(element);
        fileNum = 0;
        skipLoadFile = false;
        startMainTx(1000);
    }

    @Override
    public void save(LineTransferData element) {
        switch (type) {
        case CELL:
            saveCellLine(element);
            break;
        case FORBIDDEN:
            saveForbiddenLine(element);
            break;
        case NEIGHBOUR:
            saveNeighbourLine(element);
            break;
        case EXCEPTION:
            saveExceptionLine(element);
            break;
        case INTERFERENCE:
            saveInterferenceLine(element);
            break;
        default:
            break;
        }
    }

    /**
     * @param element
     */
    private void saveInterferenceLine(LineTransferData element) {
        String line = element.getStringLine();
        try {
            String[] field = line.split("\\s");
            int i = 0;
            String name = field[i++].trim();
            if (name.equals("SUBCELL")) {
                String sectorName = field[6];
                String sectorNo = sectorName.substring(sectorName.length() - 1); 
                if (!sectorNo.matches("\\d")){
                    int diff = Character.getNumericValue(sectorName.charAt(sectorName.length() - 1)) - Character.getNumericValue('A') + 1;
                    sectorName = sectorName.substring(0, sectorName.length() - 1) + diff;
                }                   
                serve = defineServe(sectorName);
                //TODO add statistics?
                serve.setProperty("nonrelevant1", Integer.valueOf(field[i++]));
                serve.setProperty("nonrelevant2", Integer.valueOf(field[i++]));
                serve.setProperty("total-cell-area", Double.valueOf(field[i++]));
                serve.setProperty("total-cell-traffic", Double.valueOf(field[i++]));
                serve.setProperty("numberofinterferers", Integer.valueOf(field[i++]));
            } 
            else if (name.equals("INT")) {      
                if (serve == null) {
                    error("Not found serve cell for neighbours: " + line);
                    return;
                } 
                else {
                    String sectorName = field[7];
                    if (!sectorName.substring(sectorName.length() - 1).matches("\\d")){
                        int sectorNo = Character.getNumericValue(sectorName.charAt(sectorName.length() - 1)) - Character.getNumericValue('A') + 1;
                        sectorName = sectorName.substring(0, sectorName.length() - 1) + sectorNo;
                    }
                    Relationship relation = defineInterferer(sectorName);
                    //TODO add statistics?
                    relation.setProperty("nonrelevant1", Integer.valueOf(field[i++]));
                    relation.setProperty("nonrelevant2", Integer.valueOf(field[i++]));
                    relation.setProperty("co-channel-interf-area", Double.valueOf(field[i++]));
                    relation.setProperty("co-channel-interf-traffic", Double.valueOf(field[i++]));
                    relation.setProperty("adj-channel-interf-area", Double.valueOf(field[i++]));
                    relation.setProperty("adj-channel-interf-traffic", Double.valueOf(field[i++]));
                }
            }
        } catch (Exception e) {
            String errStr = String.format("Can't parse line: %s", line);
            error(errStr);
            Logger.getLogger(this.getClass()).error(errStr, e);
        }
    }
    /**
     * Define neigh.
     * 
     * @param siteName the site name
     * @param field the field
     */
    private Relationship defineInterferer(String sectorName) {
       String proxySectorName = neighName + "/" + sectorName;
        
       Node proxySector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
       if (proxySector == null) {
           Node sector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                error(". Interference File. Not found sector " + sectorName);
                return null;
            }
            proxySector = createProxySector(sector, lastSector, rootNode, NetworkRelationshipTypes.INTERFERENCE);
            lastSector = proxySector;
       }
        
        Relationship relation = serve.createRelationshipTo(proxySector, NetworkRelationshipTypes.INTERFERS);
        return relation;
    }

    /**
     * @param element
     */
    private void saveExceptionLine(LineTransferData element) {
        String line = element.getStringLine();
        try {
            String[] field = line.split("\\s");
            int i = 0;
            String siteName = field[i++];
            Integer sectorNo = Integer.valueOf(field[i++]);
            String sectorName = siteName +field[1];
            serve = defineServe(sectorName);
            siteName = field[i++];
            sectorNo = Integer.valueOf(field[i++]);
            sectorName = siteName +field[3];
            Relationship relation = defineException(sectorName);
            relation.setProperty("new_spacing", field[i++]);
        } catch (Exception e) {
            String errStr = String.format("Can't parse line: %s", line);
            error(errStr);
            Logger.getLogger(this.getClass()).error(errStr, e);
        }
    }
    /**
     * Define Exception.
     * 
     * @param siteName the site name
     * @param field the field
     */
    private Relationship defineException(String sectorName) {
        String proxySectorName = neighName + "/" + sectorName;

        Node proxySector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS),
                proxySectorName);
        if (proxySector == null) {
            Node sector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                error(". Exception File. Not found sector " + sectorName);
                return null;
            }
            proxySector = createProxySector(sector, lastSector, rootNode, NetworkRelationshipTypes.EXCEPTIONS);
            lastSector = proxySector;
        }

        Relationship relation = serve.createRelationshipTo(proxySector, NetworkRelationshipTypes.EXCEPTION);
        // if (numericProp.isEmpty()) {
        // numericProp.add("new_spacing");
        // allProp.add("new_spacing");
        // }
        return relation;
    }
    /**
     * Define serve.
     * 
     * @param siteName the site name
     * @param field the field
     * @return the node
     */
    private Node defineServe(String sectorName) {
        String proxySectorName = neighName + "/" + sectorName;
        
        Node proxySector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
            if (proxySector == null) {
                Node sector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
                if (sector == null) {
                    error(". Exceptions File. Not found sector " + sectorName);
                    return null;
                }
                proxySector = createProxySector(sector, lastSector, rootNode, NetworkRelationshipTypes.EXCEPTIONS);
                lastSector = proxySector;
            }           
      
        return proxySector;
    }
    /**
     * @param element
     */
    private void saveNeighbourLine(LineTransferData element) {
        String line = element.getStringLine();
        try {
            String[] field = line.split("\\s");
            int i = 0;
            String name = field[i++].trim();
            String siteName = field[i++];
            Integer sectorNo = Integer.valueOf(field[i++]);
            if (name.equals("CELL")) {
                serve = defineServe(siteName, field[2]);
            } else {
                if (serve == null) {
                    error("Not found serve cell for neighbours: " + line);
                    return;
                } else {
                    defineNeigh(siteName, field[2]);
                }
            }
        } catch (Exception e) {
            String errStr = String.format("Can't parse line: %s", line);
            error(errStr);
            Logger.getLogger(this.getClass()).error(errStr, e);
        }
    }

    /**
     * Define neigh.
     * 
     * @param siteName the site name
     * @param field the field
     */
    private void defineNeigh(String siteName, String field) {
        String sectorName = siteName.trim() + field.trim();
        String proxySectorName = neighName + "/" + sectorName;

        Node proxySector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS),
                proxySectorName);
        if (proxySector == null) {
            Node sector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                error(". Neighbours File. Not found sector " + sectorName);
                return;
            }
            proxySector = createProxySector(sector, lastSector, rootNode, NetworkRelationshipTypes.NEIGHBOURS);
            lastSector = proxySector;
        }

        serve.createRelationshipTo(proxySector, NetworkRelationshipTypes.NEIGHBOUR);
    }

    /**
     * Define serve.
     * 
     * @param siteName the site name
     * @param field the field
     * @return the node
     */
    private Node defineServe(String siteName, String field) {
        String sectorName = siteName.trim() + field.trim();
        String proxySectorName = neighName + "/" + sectorName;

        Node proxySector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS),
                proxySectorName);
        if (proxySector == null) {
            Node sector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                error(". Neighbours File. Not found sector " + sectorName);
                return null;
            }
            proxySector = createProxySector(sector, lastSector, rootNode, NetworkRelationshipTypes.NEIGHBOURS);
            lastSector = proxySector;
        }

        return proxySector;
    }

    /**
     * @param sector the sector whose proxy is to be created
     * @param lastSector sector whose proxy was created last
     * @param rootNode the list(neighbours/interference/exception) node corresponding to this proxy
     * @param type the relationship type for proxySector
     * @return
     */
    private Node createProxySector(Node sector, Node lastSector, Node rootNode, NetworkRelationshipTypes type) {

        Node proxySector;

        proxySector = getService().createNode();
        String sectorName = sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        String proxySectorName = NeoUtils.getNodeName(rootNode) + "/" + sectorName;
        proxySector.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS.getId());
        proxySector.setProperty(INeoConstants.PROPERTY_NAME_NAME, proxySectorName);

        // TODO: bad way. fix it to check lastSector.equals(rootNode)
        if (lastSector == null || lastSector.equals(rootNode))
            rootNode.createRelationshipTo(proxySector, NetworkRelationshipTypes.CHILD);
        else
            lastSector.createRelationshipTo(proxySector, NetworkRelationshipTypes.NEXT);

        sector.createRelationshipTo(proxySector, type);

        getIndexService().index(proxySector, NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);

        return proxySector;
    }

    /**
     * @param element
     */
    private void saveForbiddenLine(LineTransferData element) {
        String line = element.getStringLine();
        try {
            // TODO debug - in example do not have necessary file
            String[] field = line.split("\\s");
            int i = 0;
            String siteName = field[i++];
            // Integer sectorNo = Integer.valueOf(field[i++]);
            Integer numberofforbidden = Integer.valueOf(field[i++]);
            Integer[] forbList = new Integer[numberofforbidden];
            for (int j = 0; j < forbList.length; j++) {
                forbList[j] = Integer.valueOf(field[i++]);
            }
            String sectorName = siteName + field[1];
            Node sector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                error("Forbidden Frquencies File. Not found in network sector " + sectorName);
                return;
            }
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "numberofforbidden", numberofforbidden);
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "forb_fr_list", forbList);
        } catch (Exception e) {
            String errStr = String.format("Can't parse line: %s", line);
            error(errStr);
            Logger.getLogger(this.getClass()).error(errStr, e);
        }
    }

    /**
     * @param element
     */
    private void saveCellLine(LineTransferData element) {
        String line;
        try {
            line = element.getStringLine();
            String[] field = line.split("\\s");
            int i = 0;
            String siteName = field[i++];
            // Integer sectorNo = Integer.valueOf(field[i++]);
            Integer nonrelevant = Integer.valueOf(field[i++]);
            Integer numberoffreqenciesrequired = Integer.valueOf(field[i++]);
            Integer numberoffrequenciesgiven = Integer.valueOf(field[i++]);
            Integer[] frq = new Integer[numberoffrequenciesgiven];
            for (int j = 0; j < frq.length; j++) {
                frq[j] = Integer.valueOf(field[i++]);
            }

            Node site = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
            if (site == null) {
                site = addSimpleChild(rootNode, NodeTypes.SITE, siteName);
            }
            String sectorName = siteName + field[1];
            Node sector = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                sector = addSimpleChild(site, NodeTypes.SECTOR, sectorName);
            }
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "nonrelevant", nonrelevant);
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "numberoffreqenciesrequired", numberoffreqenciesrequired);
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "numberoffrequenciesgiven", numberoffrequenciesgiven);
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "frq", frq);

        } catch (Exception e) {
            String errStr = String.format("Can't parse line: %s \t %s", element.getLine(), element.getStringLine());
            error(errStr);
            Logger.getLogger(this.getClass()).error(errStr, e);
        }
    }

    @Override
    public void finishUp(LineTransferData element) {
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return null;
    }

    @Override
    public boolean beforeSaveNewElement(LineTransferData element) {
        if (skipLoadFile) {
            return true;
        }
        fileNum++;
        type = AfpFileTypes.valueOf(element.get("afpType"));
        if (fileNum == 1 && type != AfpFileTypes.CELL) {
            error("Not found Cite file");
            skipLoadFile = true;
            return true;
        }
        //TODO simplify code after testing or creating necessary services
        if (type == AfpFileTypes.NEIGHBOUR) {
            lastSector = null;
            neighName = element.getFileName();
            afpNeigh = NeoUtils.findNeighbour(rootNode, element.getFileName(), getService());
            if (afpNeigh == null) {
                try {
                    afpNeigh = getService().createNode();
                    afpNeigh.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NEIGHBOUR.getId());
                    afpNeigh.setProperty(INeoConstants.PROPERTY_NAME_NAME, element.getFileName());
                    rootNode.createRelationshipTo(afpNeigh, NetworkRelationshipTypes.NEIGHBOUR_DATA);
                   updateTx(1, 1);
                } catch (Exception e) {
                    mainTx.failure();
                }
            }
        } else if (type == AfpFileTypes.EXCEPTION) {
            neighName = element.getFileName();
            afpNeigh = NeoUtils.findNeighbour(rootNode, neighName, getService());
            if (afpNeigh == null) {

                afpNeigh = getService().createNode();
                afpNeigh.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NEIGHBOUR.getId());
                    AfpNeighbourSubType.EXCEPTION.setTypeToNode(afpNeigh, getService());
                    afpNeigh.setProperty(INeoConstants.PROPERTY_NAME_NAME, neighName);
                    rootNode.createRelationshipTo(afpNeigh, NetworkRelationshipTypes.EXCEPTION_DATA);

            }
        }else if (type == AfpFileTypes.INTERFERENCE) {
            neighName = element.getFileName();
            afpNeigh = NeoUtils.findNeighbour(rootNode, neighName , getService());
            if (afpNeigh == null) {

                    afpNeigh = getService().createNode();
                    afpNeigh.setProperty(INeoConstants.PROPERTY_TYPE_NAME,
                    NodeTypes.NEIGHBOUR.getId());
                    AfpNeighbourSubType.INTERFERENCE.setTypeToNode(afpNeigh, getService());
                    afpNeigh.setProperty(INeoConstants.PROPERTY_NAME_NAME, neighName);
                    rootNode.createRelationshipTo(afpNeigh, NetworkRelationshipTypes.INTERFERENCE_DATA);

            }
            
        }
        return false;
    }

    @Override
    public void finishSaveNewElement(LineTransferData element) {
    }

    @Override
    protected void fillRootNode(Node rootNode, LineTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return null;
    }
}
