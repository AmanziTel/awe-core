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

import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.saver.Node2NodeSaver;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 *Transmission Saver
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class TransmissionSaver extends Node2NodeSaver<BaseTransferData> {

    @Override
    protected Node defineNeigh(BaseTransferData element) {
        String name = getStringValue("neigh_name", element);
        String no = getStringValue("neigh_no", element);
        if (StringUtils.isEmpty(name)&&StringUtils.isEmpty(no)) {
            return null;
        }
        return  service.findSite(rootNode, name, no);
    }

    @Override
    protected Node defineServ(BaseTransferData element) {
        String name = getStringValue("serv_name", element);
        String no = getStringValue("serv_no", element);
        if (StringUtils.isEmpty(name)&&StringUtils.isEmpty(no)) {
            return null;
        }
        return service.findSite(rootNode, name, no);
    }

    @Override
    protected void storeHandledData(Relationship rel, BaseTransferData element) {
    }

    @Override
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "serv_name", getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_SERV));
        defineHeader(headers, "serv_no", getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_SERV));
        defineHeader(headers, "neigh_name", getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_NEIB));
        defineHeader(headers, "neigh_no", getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_NEIB));
    }

    @Override
    public NodeToNodeRelationModel getModel(String neighbourName) {
        return new NetworkModel(rootNode).getTransmission(neighbourName);
    }

}
