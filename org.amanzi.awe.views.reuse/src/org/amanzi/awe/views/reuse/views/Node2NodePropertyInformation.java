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
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class Node2NodePropertyInformation implements IPropertyInformation {

    private final ISinglePropertyStat stat;
    private final String propName;
    private final NodeToNodeRelationModel model;

    /**
     * @param model
     * @param stat
     * @param propName
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
        //ignore selection rules in network
        Traverser td = model.getNeighTraverser(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path paramPath) {
                boolean includes=paramPath.lastRelationship()!=null&&paramPath.lastRelationship().hasProperty(propName);
                return Evaluation.of(includes, true);
            }
        });
        return new WrapperedIterable(td,propName);
    }
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
                    return new SourceImpl(null, rel.getProperty(name,null));
                }

                @Override
                public void remove() {
                    it.remove();
                }
            };
        }
        
    }
}
