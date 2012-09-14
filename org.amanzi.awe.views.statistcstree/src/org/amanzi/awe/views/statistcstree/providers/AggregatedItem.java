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

package org.amanzi.awe.views.statistcstree.providers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.dto.IDataElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AggregatedItem {

    private String name;
    private Iterator<IDataElement> sources;

    private static final int THRESHOLD = 50;

    protected AggregatedItem(Iterator<IDataElement> sources) {
        this.sources = sources;
    }

    private AggregatedItem(String name, Iterator<IDataElement> sources) {
        this(sources);
        this.name = name;

    }

    public Iterable<Object> getNextSources() {
        int count = 0;
        List<Object> dataElements = new ArrayList<Object>();
        IDataElement element = null;
        while (sources.hasNext()) {
            element = sources.next();
            dataElements.add(element);
            count++;
            if (count == THRESHOLD) {
                break;
            }
        }
        if (hasNext()) {
            dataElements.add(new AggregatedItem(element.getName(), sources));
        }
        return dataElements;
    }

    /**
     * @return Returns the sources.
     */
    public Iterator<IDataElement> getSources() {
        return sources;
    }

    public boolean hasNext() {
        return sources.hasNext();
    }

    public String getName() {
        return name;
    }
}
