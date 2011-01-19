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

import java.util.Iterator;

import org.amanzi.awe.views.reuse.Select;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;

/**
 * <p>
 *Contains information for analyse node2node models
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class Node2NodePropertyInformation implements IPropertyInformation {

    private final ISinglePropertyStat stat;
    private final String propName;
    private final NodeToNodeRelationModel model;


    /**
     * Instantiates a new node2 node property information.
     *
     * @param model the model
     * @param stat the stat
     * @param propName the prop name
     */
    public Node2NodePropertyInformation(NodeToNodeRelationModel model, ISinglePropertyStat stat, String propName) {
        this.model = model;
        this.stat = stat;
        this.propName = propName;
    }

    @Override
    public ISinglePropertyStat getStatistic() {
        return stat;
    }

    @Override
    public String getPropertyName() {
        return propName;
    }

    @Override
    public Iterable<ISource> getValueIterable(Select rules) {
        switch (rules) {
        case EXISTS:
            Traverser td = model.getNeighTraverser(new Evaluator() {
                
                @Override
                public Evaluation evaluate(Path paramPath) {
                    boolean includes=paramPath.lastRelationship()!=null&&paramPath.lastRelationship().hasProperty(propName);
                    return Evaluation.of(includes, true);
                }
            });
            return new WrapperedIterable(td,propName);     
        default:
            td = model.getServTraverser(NodeToNodeRelationService.getServExistPropertyEvaluator(propName));
            return new AggregatedIterable(td,propName,rules,stat.getType()); 
        }

    }
    /**
     * 
     * <p>
     *Iterable of relationships
     * </p>
     * @author tsinkel_a
     * @since 1.0.0
     */
    private static class AggregatedIterable implements Iterable<ISource> {

        private final Traverser td;
        private final String name;
        private NodeToNodeRelationService n2n;
        @SuppressWarnings("rawtypes")
        private final Class klass;
        private final Select rules;

        /**
         * @param td
         * @param propName 
         * @param rules 
         * @param klass 
         */
        public AggregatedIterable(Traverser td, String propName, Select rules, @SuppressWarnings("rawtypes") Class klass) {
            this.td = td;
            name = propName;
            this.rules = rules;
            this.klass = klass;
            n2n=NeoServiceFactory.getInstance().getNodeToNodeRelationService();
        }

        @Override
        public Iterator<ISource> iterator() {
           final  Iterator<Node> it = td.nodes().iterator();
            return new Iterator<ISource>() {

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public ISource next() {
                    Node serv=it.next();
                    return new SourceImpl(serv, n2n.getServAggregatedValues(klass,rules.getRule(),serv,name));
                }

                @Override
                public void remove() {
                    it.remove();
                }
            };
        }
        
    }
    /**
     * 
     * <p>
     *Iterable of relationships
     * </p>
     * @author tsinkel_a
     * @since 1.0.0
     */
    private static class WrapperedIterable implements Iterable<ISource> {

        private final Traverser td;
        private final String name;

        /**
         * @param td
         * @param propName 
         */
        public WrapperedIterable(Traverser td, String propName) {
            this.td = td;
            name = propName;
        }

        @Override
        public Iterator<ISource> iterator() {
           final  Iterator<Relationship> it = td.relationships().iterator();
            return new Iterator<ISource>() {

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public ISource next() {
                    Relationship rel=it.next();
                    return new SourceImpl(rel.getStartNode(), rel.getProperty(name,null));
                }

                @Override
                public void remove() {
                    it.remove();
                }
            };
        }
        
    }
}
