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

package org.amanzi.awe.filters;

import java.util.LinkedList;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 *  Filter Chain
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class FilterChain extends AbstractFilter {
    private final LinkedList<AbstractFilter> subfilters;
    private final  ChainRule rule;
    private final boolean isValid;
    private final RGB color;

    protected FilterChain(Node node, GraphDatabaseService service) {
        super(node, service);
        type = NodeTypes.FILTER_GROUP;
        rule=getRuleFromNode();
        subfilters = new LinkedList<AbstractFilter>();
        for (Node child : NeoUtils.getChildTraverser(node)) {
            subfilters.add(AbstractFilter.getInstance(child, service));
        }
        color=NeoUtils.getColor(node, FilterUtil.PROPERTY_FILTER_COLOR, null, service);
        isValid = validateFilter();
    }
    /**
     *validate filter
     * @return is filter valid?
     */
    private boolean validateFilter() {
        return rule!=null&&!subfilters.isEmpty();
    }
    /**
     *gets rule from node
     * @return
     */
    private ChainRule getRuleFromNode() {
        Transaction tx = NeoUtils.beginTx(service);
        try{
            return ChainRule.getEnumById((String)node.getProperty(FilterUtil.PROPERTY_ORDER,null));
        }finally{
            tx.finish();
        }
    }
    @Override
    public FilterResult filterNode(Node node) {
        final FilterResult falseResult = new FilterResult(false, false, -1, subfilters.size(), node);
        if (!isValid) {
            return falseResult;
        }
        Transaction tx = NeoUtils.beginTx(service);
        try {
            for (int i = 0; i < subfilters.size(); i++) {
                final FilterResult result = subfilters.get(i).filterNode(node);
                switch (rule) {
                case OR:
                    if (result.isValid()){
                        return new FilterResult(true, false, i, subfilters.size(), node);
                    }                   
                    break;
                case AND:
                    if (!result.isValid()){
                        return falseResult;
                    }
                    break;
                }
            }
            return new FilterResult(rule==ChainRule.AND, false, -1, subfilters.size(), node);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    @Override
    public FilterResult filterValue(Object value) {
        final FilterResult falseResult = new FilterResult(false, false, -1, subfilters.size(), node);
        if (!isValid) {
            return falseResult;
        }
        for (int i = 0; i < subfilters.size(); i++) {
            final FilterResult result = subfilters.get(i).filterValue(value);
            switch (rule) {
            case OR:
                if (result.isValid()){
                    return new FilterResult(true, false, i, subfilters.size(), node);
                }                   
                break;
            case AND:
                if (!result.isValid()){
                    return falseResult;
                }
                break;
            }
        }
        return new FilterResult(rule==ChainRule.AND, false, -1, subfilters.size(), null);
    }

    @Override
    public FilterResult filterNodesByTraverser(Traverser traverser) {
        final FilterResult falseResult = new FilterResult(false, false, -1, subfilters.size(), node);
        if (!isValid) {
            return falseResult;
        }
        for (Node node : traverser) {
            FilterResult result = filterNode(node);
            if (result.isValid()){
                return result;
            }
        }
        return falseResult;
    }
    @Override
    public String toString() {
        return String.format("Chain filter: %s.",name);
    }
}
