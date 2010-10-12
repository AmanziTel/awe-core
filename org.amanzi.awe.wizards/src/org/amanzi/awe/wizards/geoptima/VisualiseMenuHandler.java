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

package org.amanzi.awe.wizards.geoptima;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.preferences.DataLoadPreferences;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class VisualiseMenuHandler extends CompoundContributionItem {

    @Override
    protected IContributionItem[] getContributionItems() {
        GraphDatabaseService service=NeoServiceProvider.getProvider().getService();
        List<IContributionItem> list = new LinkedList<IContributionItem>();

        String property = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA);
        Set<Node> storedData = new LinkedHashSet<Node>();
        if (StringUtils.isNotEmpty(property)) {
            Transaction tx = service.beginTx();
            try {
                StringTokenizer st = new StringTokenizer(property, DataLoadPreferences.CRS_DELIMETERS);
                while (st.hasMoreTokens()) {
                    String nodeId = st.nextToken();
                    Node node = service.getNodeById(Long.parseLong(nodeId));
                    //TODO check correct gis node
                    if (node.hasRelationship(GeoNeoRelationshipTypes.NEXT,Direction.INCOMING)){
                        storedData.add(node);
                    }
                }
                int i=1;
                for (Node node:storedData){
                    NodeTypes type = NodeTypes.getNodeType(node, service);
                    ImageDescriptor image=null;
                    if (type!=null){
                        image=ImageDescriptor.createFromImage(type.getImage());
                    }
                    Map parms = new HashMap();
                    parms.put("nodeId", String.valueOf(node.getId()));
                    String name=NeoUtils.getNodeName(node, service);
                    CommandContributionItem com = new CommandContributionItem(PlatformUI.getWorkbench(),
                            "org.amanzi.awe.wizards.geoptima.visualise"+i++,
                            "org.amanzi.awe.wizards.geoptima.visualise", 
                            parms, image, null, null, name, null,
                            null, CommandContributionItem.STYLE_PUSH);
                    list.add(com);  
                }
            } finally {
                tx.finish();
            }
        }


        return list.toArray(new IContributionItem[0]);

    }

}
