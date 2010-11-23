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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 *Saver for transmission data
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class TransmissionSaver extends AbstractHeaderSaver<BaseTransferData> {

    /** The header not handled. */
    private boolean headerNotHandled;
    private Node transmissionRoot;
    private String transmissionName;
    private MetaData metadata=new MetaData("dataset", MetaData.SUB_TYPE,"tems");

    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
    }

    @Override
    public void save(BaseTransferData element) {
        if (headerNotHandled) {
            transmissionName = element.getFileName();
            transmissionRoot = service.getTransmission(rootNode, transmissionName);
            definePropertyMap(element);
            startMainTx(1000);
            headerNotHandled = false;
        }
        saveRow(element);
    }

    /**
     * Save row.
     * 
     * @param element the element
     */
    protected void saveRow(BaseTransferData element) {
        String name = getStringValue("serv_name", element);
        String no = getStringValue("serv_no", element);
        if (StringUtils.isEmpty(name)&&StringUtils.isEmpty(no)) {
            error(String.format("Line %s not saved. Not found id property for serv site.", element.getLine()));
            return;
        }
        Node serSite = service.findSite(rootNode, name, no);
        if (serSite == null) {
            serSite=createTransmissionSite(name,no);
        }
        name = getStringValue("neigh_name", element);
        no = getStringValue("neigh_no", element);
        if (StringUtils.isEmpty(name)&&StringUtils.isEmpty(no)) {
            error(String.format("Line %s not saved. Not found id property for neighbour site.", element.getLine()));
            return;
        }
        Node neighSite = service.findSite(rootNode, name, no);
        if (neighSite == null) {
            serSite=createTransmissionSite(name,no);
        }
        createTransmission(serSite, neighSite, element);

    }


    /**
     * Creates the transmission site.
     *
     * @param name the name
     * @param no the no
     * @return the node
     */
    private Node createTransmissionSite(String name, String no) {
        if (StringUtils.isEmpty(name)){
            name=no;
        }
       Node result= addSimpleChild(rootNode, NodeTypes.SITE,name);
       if (StringUtils.isNotEmpty(no)){
           result.setProperty(INeoConstants.PROPERTY_SITE_NO, no);
           getIndexService().index(result, Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_SITE_NO, NodeTypes.SITE), no);
       }
        return result;
    }

    /**
     * Creates the neighbour.
     * 
     * @param serSite the ser sector
     * @param neighSite the neigh sector
     * @param element the element
     */
    private void createTransmission(Node serSite, Node neighSite, BaseTransferData element) {
        NodeResult proxyServ = service.getTransmissionProxy(transmissionRoot, serSite);
        if (proxyServ.isCreated()) {
            updateTx(1, 1);
            statistic.updateTypeCount(transmissionName, NodeTypes.SITE_SITE_RELATIONS.getId(), 1);
        }
        NodeResult proxyNeigh = service.getNeighbourProxy(transmissionRoot, neighSite);
        if (proxyNeigh.isCreated()) {
            updateTx(1, 1);
            statistic.updateTypeCount(transmissionName, NodeTypes.SITE_SITE_RELATIONS.getId(), 1);
        }
        Relationship rel;
        if (proxyServ.isCreated() || proxyNeigh.isCreated()) {
            rel = proxyServ.createRelationshipTo(proxyNeigh, NetworkRelationshipTypes.TRANSMISSION);
            updateTx(0, 1);
        } else {
            Iterator<Relationship> it = Utils.getRelations(proxyServ, proxyNeigh, NetworkRelationshipTypes.TRANSMISSION).iterator();
            rel = it.hasNext() ? it.next() : null;
            if (rel == null) {
                rel = proxyServ.createRelationshipTo(proxyNeigh, NetworkRelationshipTypes.TRANSMISSION);
                updateTx(0, 1);
            }
        }
        Map<String, Object> siteData = getNotHandledData(element, transmissionName, NodeTypes.SITE_SITE_RELATIONS.getId());

        for (Map.Entry<String, Object> entry : siteData.entrySet()) {
            String key = entry.getKey();
            updateProperty(transmissionName, NodeTypes.SITE_SITE_RELATIONS.getId(), rel, key, entry.getValue());
        }
    }

    /**
     * Define property map.
     * 
     * @param element the element
     */
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "serv_name", getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_SERV));
        defineHeader(headers, "serv_no", getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_SERV));
        defineHeader(headers, "neigh_name", getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_NEIB));
        defineHeader(headers, "neigh_no", getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_NEIB));
    }

    @Override
    protected void fillRootNode(Node rootNode, BaseTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SECTOR.getId();
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[]{metadata});
    }

}
