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

package org.amanzi.awe.wizards.geoptima.export;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Implementation of AbstractNeoExportModel
 * </p>
 * 
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NeoExportModelImpl extends AbstractNeoExportModel {

    /** The property map. */
    protected final LinkedHashSet<String>[] propertyMap;

    /**
     * Instantiates a new neo export model impl.
     * 
     * @param service the service
     */
    public NeoExportModelImpl(GraphDatabaseService service, int groupCount) {
        super(service);
        propertyMap = new LinkedHashSet[groupCount];
        for (int i = 0; i < propertyMap.length; i++) {
            propertyMap[i] = new LinkedHashSet<String>();
        }
    }

    /**
     * Gets the group count.
     * 
     * @return the group count
     */
    public int getGroupCount() {
        return propertyMap.length;
    }
    /**
     * Adds the property list.
     * 
     * @param key the key
     * @param properties the properties
     */
    public void addPropertyList(Integer id, Collection<String> properties) {
        propertyMap[id].addAll(properties);
    }

    /**
     * Sets the property list.
     * 
     * @param id the id
     * @param properties the properties
     */
    public void setPropertyList(Integer id, Collection<String> properties) {
        propertyMap[id].clear();
        addPropertyList(id, properties);
    }

    /**
     * Gets the headers.
     * 
     * @return the headers
     */
    @Override
    public List<String> getHeaders() {
        List<String> results = new LinkedList<String>();
        for (LinkedHashSet<String> groupProp : propertyMap) {
            results.addAll(groupProp);
        }
        return results;
    }

    /**
     * Gets the results.
     * 
     * @param parameter the parameter
     * @return the results
     */
    @Override
    public List<Object> getResults(IExportParameter parameter) {
        assert parameter != null && parameter instanceof NeoExportParameter;
        List<Object> result = new LinkedList<Object>();
        NeoExportParameter neoParam = (NeoExportParameter)parameter;
        if (propertyMap.length != neoParam.getNodeList().size()) {
            throw new IllegalArgumentException("Wrong size of parameters");
        }
        Transaction tx = service.beginTx();
        Iterator<Node> neoParamIterator = neoParam.getNodeList().iterator();
        try {
            for (LinkedHashSet<String> groupProp : propertyMap) {
                Node handleNode = neoParamIterator.next();
                for (String property : groupProp) {
                    result.add(handleNode.getProperty(property, null));
                }
            }
        } finally {
            tx.finish();
        }
        return result;
    }

}
