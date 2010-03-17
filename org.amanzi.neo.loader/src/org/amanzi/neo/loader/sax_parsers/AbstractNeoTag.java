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

package org.amanzi.neo.loader.sax_parsers;

import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Node;
import org.xml.sax.Attributes;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public abstract class AbstractNeoTag extends AbstractTag {
    Node node;
    Node lastChild;

    /**
     * @param tagName
     * @param parent
     */
    protected AbstractNeoTag(String tagName, AbstractNeoTag parent, Attributes attributes) {
        super(tagName, parent);
        lastChild = null;
        node = createNode(attributes);
        parent.addChild(this);
    }

    /**
     * @param abstractNeoTag
     */
    private void addChild(AbstractNeoTag childNode) {
        NeoUtils.addChild(node, childNode.node, lastChild, null);
        lastChild = childNode.node;
    }

    protected AbstractNeoTag(String tagName, Node parent, Node lastChild, Attributes attributes) {
        super(tagName, null);
        lastChild = null;
        node = createNode(attributes);
        NeoUtils.addChild(parent, node, lastChild, null);
    }

    protected void storeAttributes(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            node.setProperty(attributes.getLocalName(i), attributes.getValue(i));
        }
    }
    /**
     * @param attributes
     * @return
     */
    protected abstract Node createNode(Attributes attributes);


}
