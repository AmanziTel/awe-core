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

package org.amanzi.testing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map.Entry;

import org.amanzi.log4j.LogStarter;
import org.junit.BeforeClass;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractTest {

    @BeforeClass
    public static void setUpClass() {
        new LogStarter().earlyStartup();
    }

    protected Node getNodeMock() {
        return mock(Node.class);
    }

    protected Node getNodeMock(HashMap<String, Object> values) {
        Node result = getNodeMock();

        for (Entry<String, Object> singleEntry : values.entrySet()) {
            when(result.getProperty(singleEntry.getKey())).thenReturn(singleEntry.getValue());
        }

        return result;
    }
}
