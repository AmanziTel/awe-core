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

package org.amanzi.neo.loader.savers;

import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.ui.preferences.DataLoadPreferences;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Saver for network SITE data
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NetworkSiteSaver extends NetworkSaver {
    /** String SITE_ID_KEY field. */
    private static final String SITE_ID_KEY = "site_id"; 
    @Override
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, SITE_ID_KEY, getPossibleHeaders(DataLoadPreferences.NH_SITE));
    }
    @Override
    protected void saveRow(BaseTransferData element) {
        String siteField = getStringValue(SITE_ID_KEY,element);
        if (StringUtils.isEmpty(siteField)) {
            error("Missing sector name on line " + element.getLine());
            return;
        }
        Node site=service.getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteField);
        if (site==null){
            site=addSimpleChild(rootNode, NodeTypes.SITE, siteField);
        }
        String mtypeId=NodeTypes.SITE.getId();
        
        Map<String, Object> sectorData = getNotHandledData(element, rootname, mtypeId);
        for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
            String key = entry.getKey();
            setProperty(rootname, mtypeId, site, key, entry.getValue());
        }
    }
}
