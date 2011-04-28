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

package org.amanzi.awe.afp.exporters;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Hsn Impact matrix
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class HSNImpactMatrix {
    private final FrequencyPlanModel freq;
    private Set<Node>plans=new LinkedHashSet<Node>();

    public HSNImpactMatrix(FrequencyPlanModel freq) {
        this.freq = freq;
        Iterator<Node> it = freq.getFrequencyPlans();
        while (it.hasNext()) {
            Node plan = it.next();
            if (plan.hasProperty(INeoConstants.PROPERTY_MAL)) {
                plans.add(plan);
            }
        }
    }
}
