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

package org.amanzi.awe.tool.star;

import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * remove all network filters from map
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class FilterToolRemove extends AbstractModalTool {
    //$NON-NLS-N$
    public static String  BLACKBOARD_KEY="Network Filtering";
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        IMap map = getContext().getMap();
        String key = map.getBlackboard().getString(BLACKBOARD_KEY);
        final String newKey = getKey();
        if (!StringUtils.equals(key, newKey)){
            map.getBlackboard().putString(BLACKBOARD_KEY,newKey);
            try {
                updateNetworksLayer(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
    }
    /**
     *update map
     * @param map map
     * @throws IOException 
     */
    private void updateNetworksLayer(IMap map) throws IOException {
        GraphDatabaseService service = NeoServiceProvider.getProvider().getService();
        Transaction tx = service.beginTx();
        try{
        for (ILayer layer : map.getMapLayers()) {
            if (layer.getGeoResource().canResolve(Node.class)) {
                Node node = layer.getGeoResource().resolve(Node.class, new NullProgressMonitor());
                if (NeoUtils.getGisType(node, null)==GisTypes.NETWORK){
                    layer.refresh(null);
                }
            }
        }
        }finally{
            tx.finish();
        }
    }
       
    /**
     * set filer
     * @param map  - map
     *
     */
    protected String getKey() {
        return "";
    }


}
