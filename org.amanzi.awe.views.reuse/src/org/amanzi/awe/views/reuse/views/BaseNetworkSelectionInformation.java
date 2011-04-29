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

package org.amanzi.awe.views.reuse.views;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.IPropertyInformation;
import org.amanzi.neo.services.statistic.ISelectionInformation;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.amanzi.neo.services.statistic.ISource;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.utils.AggregateRules;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class BaseNetworkSelectionInformation implements ISelectionInformation {

    private final String name;
    private final String nodeType;
    private Map<String,IPropertyInformation> propertyMap=new HashMap<String,IPropertyInformation>();
    private boolean isAggregated=false;
    private final Node root;

    /**
     * @param statistic
     * @param name
     * @param nodeType
     */
    public BaseNetworkSelectionInformation(Node root,IStatistic statistic, String name, String nodeType) {
        this.root = root;
        this.name = name;
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
                IPropertyInformation propInf=new BaseNetworkInformationImpl(stat, propName,root,nodeType);
                propertyMap.put(propName, propInf);
            }
        }
    }

    @Override
    public String getDescription() {
        return String.format("Network %s, node type %s", name,nodeType);
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
        return isAggregated;
    }

    @Override
    public Node getRootNode() {
        return root;
    }
    public static  class BaseNetworkInformationImpl implements IPropertyInformation {

        private ISinglePropertyStat statistic;
        private String name;
        private final Node root;
        private final String nodeType;
        private DatasetService ds;

        public BaseNetworkInformationImpl(ISinglePropertyStat statistic, String name, Node root, String nodeType) {
            super();
            this.statistic = statistic;
            this.name = name;
            this.root = root;
            this.nodeType = nodeType;
            ds=NeoServiceFactory.getInstance().getDatasetService();
        }

        @Override
        public ISinglePropertyStat getStatistic() {
            return statistic;
        }

        @Override
        public String getPropertyName() {
            return name;
        }

        @Override
        public Iterable<ISource> getValueIterable(AggregateRules rules) {
            //ignore selection rules in network
             Traverser td = Traversal.description().depthFirst().relationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING).relationships(GeoNeoRelationshipTypes.NEXT,Direction.OUTGOING).evaluator(new Evaluator() {
                
                @Override
                public Evaluation evaluate(Path arg0) {
                    Node node=arg0.endNode();
                    String typeId=ds.getTypeId(node);
                    boolean continues=typeId==null||!typeId.equals(nodeType);
                    boolean includes=!continues&&node.hasProperty(name);
                    return Evaluation.of(includes, continues);
                }
            }).traverse(root);
            return new SourceExistIterable(td,name,formSourceFinder(nodeType));
        }
        public ISourceFinder formSourceFinder(String nodeType) {

            if (NodeTypes.TRX.getId().equals(nodeType)){
                return new ISourceFinder() {
                    
                    @Override
                    public Node getSource(Node node) {
                        return node;
                    }
                    
                    @Override
                    public Iterable<Node> getMultySource(Node node) {
                        HashSet<Node>res=new HashSet<Node>();
                        res.add(node.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getStartNode());
                        return res;
                    }
                };
            } else if (!NodeTypes.SECTOR.getId().equals(nodeType)&&NodeTypes.SITE.getId().equals(nodeType)){
                return new ISourceFinder() {
                    
                    @Override
                    public Node getSource(Node node) {
                        return node;
                    }
                    
                    @Override
                    public Iterable<Node> getMultySource(Node node) {
                        return Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(GeoNeoRelationshipTypes.CHILD,Direction.INCOMING).evaluator(new Evaluator() {
                            
                            @Override
                            public Evaluation evaluate(Path arg0) {
                                boolean include = NodeTypes.SITE.checkNode(arg0.endNode());
                                return Evaluation.of(include, !include);
                            }
                        }).traverse(node).nodes();
                    }
                };
            }
            //TODO use network structure instead fixed types
            return null;
        }

    }
    @Override
    public String getFullDescription() {
        return getDescription();
    }

}
