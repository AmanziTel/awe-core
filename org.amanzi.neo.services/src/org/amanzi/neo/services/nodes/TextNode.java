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

package org.amanzi.neo.services.nodes;

import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author user
 * @since 1.0.0
 */
public class TextNode extends AbstractNode {

    private static final String TEXT_NODE_NAME = "Report next";
    private static final String TEXT_NODE_TEXT = "Text";

    public TextNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, TEXT_NODE_NAME);
    }

    public void setText(String text) {
        setParameter(TEXT_NODE_TEXT, text);
    }

    public String getText() {
        return (String)getParameter(TEXT_NODE_TEXT);
    }

}
