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

package org.amanzi.neo.services.networkModel;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class PropertyEvaluator implements Evaluator {
    String propertyName = "";
    String propertyValue="";
    /**
     * 
     * @param property  propertyName
     * @param propertyValue propertyValue
     */
    public PropertyEvaluator(String property,String propertyValue) {
        this.propertyValue=propertyValue;
        this.propertyName = property;
    }

    public Evaluation evaluate(Path curentPath) {
        Node currentNode = curentPath.endNode();
        if (currentNode.hasProperty(propertyName)&&currentNode.getProperty(propertyName).equals(propertyValue)) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }
        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

}
