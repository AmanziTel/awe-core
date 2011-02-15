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

package org.amanzi.neo.services.node2node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.statistic.IPropertyInformation;
import org.amanzi.neo.services.statistic.ISelectionInformation;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.amanzi.neo.services.statistic.IStatistic;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Provide works with node2node models
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class Node2NodeSelectionInformation implements ISelectionInformation {

    private final String node2NodeDescripttion;
    private Node root;
    private String nodeType;
    private Map<String,IPropertyInformation> propertyMap=new HashMap<String,IPropertyInformation>();
    private String name;
    private final NodeToNodeRelationModel model;
    private boolean isAggregated;


    /**
     * Instantiates a new node2 node selection information.
     *
     * @param root the root
     * @param statistic the statistic
     * @param model the model
     * @param nodeType the node type
     * @param node2NodeDescripttion the node2 node descripttion
     */
    public Node2NodeSelectionInformation(Node root, IStatistic statistic, NodeToNodeRelationModel model, String nodeType, String node2NodeDescripttion) {
        this.model = model;
        this.node2NodeDescripttion = node2NodeDescripttion;
        this.name=model.getName();
        this.root = root;
        this.nodeType = nodeType;
        Collection<String> col = statistic.getPropertyNameCollection(name, nodeType, new Comparable<Class>() {

            @Override
            public int compareTo(Class o) {
                return Comparable.class.isAssignableFrom(o) ? 0 : -1;
            }
        });
        for (String propName : col) {
            ISinglePropertyStat stat = statistic.findPropertyStatistic(name, nodeType, propName);
            if (stat!=null){
                IPropertyInformation propInf=new Node2NodePropertyInformation(model,stat,propName);
                propertyMap.put(propName, propInf);
            }
        }
    }

    @Override
    public String getDescription() {
        return node2NodeDescripttion;
    }

    @Override
    public Set<String> getPropertySet() {
        return Collections.unmodifiableSet(propertyMap.keySet());
    }

    @Override
    public IPropertyInformation getPropertyInformation(String propertyName) {
        return propertyMap.get(propertyName);
    }

    @Override
    public boolean isAggregated() {
        return true;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public String getFullDescription() {
        return getDescription();
    }


}
