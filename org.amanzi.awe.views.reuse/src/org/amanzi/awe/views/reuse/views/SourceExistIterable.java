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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final ISourceFinder finder;
    private String nameArr = null;
    private Iterator<?> arrIter = null;
    private Node arrNode = null;
    private Node nextNode = null;

    /**
     * @param td
     */
    public SourceExistIterable(Traverser td,String name) {
        this.td = td;
        this.name = name;
        finder=null;
    }
    public SourceExistIterable(Traverser td,String name,ISourceFinder source) {
        this.td = td;
        this.name = name;
        finder=source;
    }

    /**
     * @param traverser
     * @param propertyName
     * @param propertyNameArr
     * @param iSourceFinder
     */
    public SourceExistIterable(Traverser traverser, String propertyName, String propertyNameArr, ISourceFinder iSourceFinder) {
        this(traverser, propertyName, iSourceFinder);
        this.nameArr = propertyNameArr;

    }
    @Override
    public Iterator<ISource> iterator() {
        final Iterator<Node> it=td.nodes().iterator();
        return new Iterator<ISource>() {

            private int len;

            @Override
            public boolean hasNext() {
                if (nameArr != null) {
                    do {
                        if ((arrIter != null && arrIter.hasNext())) {
                            return true;
                        }
                        if (nextNode != null) {
                            return true;
                        }
                        if (it.hasNext()) {
                            nextNode = it.next();
                            if (nextNode.hasProperty(nameArr)) {
                                Object arr = nextNode.getProperty(nameArr);
                                List<Object> l = new ArrayList<Object>();
                                len = Array.getLength(arr);
                                for (int i = 0; i < len; i++) {
                                    l.add(Array.get(arr, i));
                                }
                                arrIter = l.iterator();
                                arrNode = nextNode;
                                nextNode = null;
                                continue;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } while (true);
                } else {
                    return it.hasNext();
                }
            }

            @Override
            public ISource next() {
                if (nameArr != null && hasNext()) {
                    boolean isArrIter = arrIter != null && arrIter.hasNext();
                    Node node = isArrIter ? arrNode : nextNode;
                    SourceImpl res;
                    if (isArrIter) {
                        if (finder!=null){
                            res= new SourceImpl(finder.getMultySource(node), finder.getSource(node), arrIter.next());
                            
                        }else{
                            res= new SourceImpl(node , arrIter.next());
                        }
                        res.setCount(1d/len);
                        return res;
                     } else {
                        nextNode = null;
                        if (finder!=null){
                            return new SourceImpl(finder.getMultySource(node), finder.getSource(node), node.getProperty(name, null));
                        }else{
                            return new SourceImpl(node , node.getProperty(name, null));
                        }
                    }
                }
                Node node = it.next();
                if (finder!=null){
                    return new SourceImpl(finder.getMultySource(node), finder.getSource(node), node.getProperty(name, null));
                }else{
                    return new SourceImpl( node , node.getProperty(name, null));
                }
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
    }

    // public static void main(String[] args) {
    // int[] k = new int[2];
    // List l = new ArrayList();
    // for (int i = 0; i < Array.getLength(k); i++) {
    // l.add(Array.get(k, i));
    // System.out.println(Array.get(k, i));
    // }
    // }
}
