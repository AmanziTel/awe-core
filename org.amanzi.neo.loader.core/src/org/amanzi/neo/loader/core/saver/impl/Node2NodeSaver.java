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

package org.amanzi.neo.loader.core.saver.impl;

import java.util.Arrays;
import java.util.Map;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Abstract implementation of Node2Node saver
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public abstract class Node2NodeSaver extends AbstractHeaderSaver<BaseTransferData> implements IStructuredSaver<BaseTransferData>{

    /** The header not handled. */
    private boolean headerNotHandled;
    
    /** The neighbour root. */
    private Node neighbourRoot;
    
    /** The neighbour name. */
    private String neighbourName;
    
    /** The model. */
    protected NodeToNodeRelationModel model;

    /**
     * Inits the.
     *
     * @param element the element
     */
    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
        startMainTx(1000);
    }

    /**
     * Save.
     *
     * @param element the element
     */
    @Override
    public void save(BaseTransferData element) {
        if (headerNotHandled){
            definePropertyMap(element);
            headerNotHandled=false;
        }
        saveRow(element);
    }

    /**
     * Save row.
     * 
     * @param element the element
     */
    protected void saveRow(BaseTransferData element) {
        Node serSector = defineServ(element);
        if (serSector == null) {
            error(String.format("Line %s not saved. Not found serve sector.", element.getLine()));
            return;
        }
        Node neighSector = defineNeigh(element);
        if (neighSector == null) {
            error(String.format("Line %s not saved. Not found neighbour sector.", element.getLine()));
            return;
        }
        createNeighbour(serSector, neighSector, element);

    }

    /**
     * Define neigh.
     *
     * @param element the element
     * @return the node
     */
    protected abstract Node defineNeigh(BaseTransferData element);

    /**
     * Define serv.
     *
     * @param element the element
     * @return the node
     */
    protected abstract  Node defineServ(BaseTransferData element);

    /**
     * Creates the neighbour.
     * 
     * @param serSector the ser sector
     * @param neighSector the neigh sector
     * @param element the element
     */
    private void createNeighbour(Node serSector, Node neighSector, BaseTransferData element) {
        Relationship rel=model.getRelation(serSector, neighSector);
        updateTx(3, 3);
        storeHandledData(rel,element);
        storeNonHandledData(rel,element);
    }
    
    /**
     * Store non handled data.
     *
     * @param rel the rel
     * @param element the element
     */
    protected  void storeNonHandledData(Relationship rel, BaseTransferData element) {
        Map<String, Object> sectorData = getNotHandledData(element, neighbourName, NodeTypes.NODE_NODE_RELATIONS.getId());

        for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
            String key = entry.getKey();
            updateProperty(neighbourName, NodeTypes.NODE_NODE_RELATIONS.getId(), rel, key, entry.getValue());
        }
    }


    /**
     * Store handled data.
     *
     * @param rel the rel
     * @param element the element
     */
    protected abstract void storeHandledData(Relationship rel, BaseTransferData element) ;

    /**
     * Define property map.
     * 
     * @param element the element
     */
    protected abstract void definePropertyMap(BaseTransferData element);

    /**
     * Fill root node.
     *
     * @param rootNode the root node
     * @param element the element
     */
    @Override
    protected void fillRootNode(Node rootNode, BaseTransferData element) {
    }

    /**
     * Gets the root node type.
     *
     * @return the root node type
     */
    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    /**
     * Gets the type id for gis count.
     *
     * @param gis the gis
     * @return the type id for gis count
     */
    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SECTOR.getId();
    }

    /**
     * Gets the meta data.
     *
     * @return the meta data
     */
    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[0]);
    }
    
    /**
     * Gets the model.
     *
     * @param neighbourName the neighbour name
     * @return the model
     */
    public abstract NodeToNodeRelationModel getModel(String neighbourName);

    /**
     * Before save new element.
     *
     * @param element the element
     * @return true, if successful
     */
    @Override
    public boolean beforeSaveNewElement(BaseTransferData element) {
        neighbourName = element.getFileName();
        model=getModel(neighbourName);
        headerNotHandled = true;

        return false;
    }

    /**
     * Finish save new element.
     *
     * @param element the element
     */
    @Override
    public void finishSaveNewElement(BaseTransferData element) {
        statistic.setTypeCount(neighbourName, NodeTypes.PROXY.getId(), model.getProxyCount());
        statistic.setTypeCount(neighbourName, NodeTypes.NODE_NODE_RELATIONS.getId(), model.getRelationCount());
    }
    
}
