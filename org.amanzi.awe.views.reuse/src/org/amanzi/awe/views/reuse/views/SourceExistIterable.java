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

import org.amanzi.neo.services.statistic.ISource;
import org.amanzi.neo.services.statistic.SourceImpl;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Traverser;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class SourceExistIterable implements Iterable<ISource> {

    private final Traverser td;
    private final String name;

    /**
     * @param td
     */
    public SourceExistIterable(Traverser td,String name) {
        this.td = td;
        this.name = name;
    }

    @Override
    public Iterator<ISource> iterator() {
        final Iterator<Node> it=td.nodes().iterator();
        return new Iterator<ISource>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public ISource next() {
                Node node=it.next();
                return new SourceImpl(node, node.getProperty(name,null));
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
    }

}
