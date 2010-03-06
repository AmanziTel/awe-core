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

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Tsinkel_A
 * @since 1.0.0
 */
public class Filter extends AbstractFilter {

    private String first;
    private String firstTXT;
    private String secondRel;
    private String second;
    private String secondTXT;
    private String property;
    private boolean isValid;

    /**
     * 
     */
    protected Filter(Node node, NeoService service) {
        super(node, service);
        type = NodeTypes.FILTER;
        Transaction tx = NeoUtils.beginTx(service);
        try {
            property = (String)node.getProperty(FilterUtil.PROPERTY_FILTERED_NAME, "");
            first = (String)node.getProperty(FilterUtil.PROPERTY_FIRST, "");
            firstTXT = (String)node.getProperty(FilterUtil.PROPERTY_FIRST_TXT, "");
            secondRel = (String)node.getProperty(FilterUtil.PROPERTY_SECOND_REL, "");
            second = (String)node.getProperty(FilterUtil.PROPERTY_SECOND, "");
            secondTXT = (String)node.getProperty(FilterUtil.PROPERTY_SECOND_TXT, "");
            isValid = validateFilter();
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    @Override
    public FilterResult filterNode(Node node) {
        final FilterResult falseResult = new FilterResult(false, false, -1, -1, node);
        if (!isValid) {
            return falseResult;
        }
        Transaction tx = NeoUtils.beginTx(service);
        try {
            if (!node.hasProperty(property)) {
                return falseResult;
            }
            Object value = node.getProperty(property);
            if (filterNode(value).isValid()) {
                return new FilterResult(true, false, -1, -1, node);
            } else {
                return falseResult;
            }
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    @Override
    public FilterResult filterNodesByTraverser(Traverser traverser) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            for (Node node : traverser) {
                FilterResult result = filterNode(node);
                if (result.isValid()) {
                    return result;
                }
            }
            return new FilterResult(false, false, -1, -1, null);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    public boolean validateFilter() {
        return !property.isEmpty() && !first.isEmpty() && !firstTXT.isEmpty();

    }

    /**
     * @return Returns the isValid.
     */
    public boolean isValid() {
        return isValid;
    }

    @Override
    public FilterResult filterNode(Object value) {
        final FilterResult falseResult = new FilterResult(false, false, -1, -1, null);
        if (!isValid()) {
            return falseResult;
        }
        boolean result = getResult(value,first, firstTXT);
        if (!isValid) {
            return falseResult;
        }
        if (secondRel.equals("&&")) {
            result = result && getResult(value,second, secondTXT);
        } else if ("||".equals(secondRel)) {
            result = result || getResult(value,second, secondTXT);
        }
        return new FilterResult(result&&isValid(), false, -1, -1, null);
    }

    /**
     * @param value2 
     * @param first2
     * @param firstTXT2
     * @return
     */
    private boolean getResult(Object value, String def, String comparedValue) {
        if (def.isEmpty()){
            return comparedValue.isEmpty();
        }
        if (value instanceof Number){
            Double value1=((Number)value).doubleValue();
           try {
            Double value2 = Double.parseDouble(comparedValue);
            if ("<".equals(def)){
                return value1.compareTo(value2)<0;
            }else if ("<=".equals(def)){
                return value1.compareTo(value2)<=0;
            }else if ("<=".equals(def)){
                return value1.compareTo(value2)<=0;
            }else if ("==".equals(def)){
                return value1.compareTo(value2)==0;
            }else if (">".equals(def)){
                return value1.compareTo(value2)>0;
            }else if (">=".equals(def)){
                return value1.compareTo(value2)>=0;
            }else if ("!=".equals(def)){
                return value1.compareTo(value2)!=0;
            }else {
                isValid=false;
                return false;                  
            }
        } catch (NumberFormatException e) {
            isValid=false;
            return false;        }
           
        }else if (value instanceof String){
            String value1=(String)value;
            if ("==".equals(def)){
                return value1.equals(comparedValue);  
            }else if ("!=".equals(def)){
                return !value1.equals(comparedValue);   
            }else{
                isValid=false;
                return false;               
            }
        }else{
            isValid=false;
            return false;
        }
    }

}
