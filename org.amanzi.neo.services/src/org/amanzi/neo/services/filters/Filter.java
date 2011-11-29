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

package org.amanzi.neo.services.filters;

import java.io.Serializable;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.exceptions.FilterTypeException;
import org.amanzi.neo.services.filters.exceptions.NotComparebleException;
import org.amanzi.neo.services.filters.exceptions.NotComparebleRuntimeException;
import org.amanzi.neo.services.filters.exceptions.NullValueException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.neo4j.graphdb.Node;


/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class Filter implements IFilter {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -8231241185471872716L;

    private FilterType filterType;

    private ExpressionType expressionType;
    
    private INodeType nodeType;
    
    private String propertyName;
    
    private Serializable value;
    
    private IFilter underlyingFilter; 

    public Filter(FilterType filterType, ExpressionType expressionType) {
        this.filterType = filterType;
        this.expressionType = expressionType;
    }

    public Filter(FilterType filterType) {
        this(filterType, ExpressionType.AND);
    }

    public Filter(ExpressionType expressionType) {
        this(FilterType.EQUALS, expressionType);
    }

    public Filter() {
        this(FilterType.EQUALS, ExpressionType.AND);
    }
    
    @Override
    public void setExpression(INodeType nodeType, String propertyName, Serializable value) throws NotComparebleRuntimeException{
        if (!(value instanceof Comparable<?>))
            throw new NotComparebleRuntimeException();
        this.nodeType = nodeType;
        this.propertyName = propertyName;
        this.value = value;
    }
    @Override
    public void setExpression(INodeType nodeType, String propertyName)throws FilterTypeException{
        if (filterType != FilterType.EMPTY && filterType != FilterType.NOT_EMPTY)
            throw new FilterTypeException();
        this.nodeType = nodeType;
        this.propertyName = propertyName;                            
    }
    
    @Override
    public void addFilter(IFilter additionalFilter) {
        this.underlyingFilter = additionalFilter;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean check(Node node) throws NotComparebleException, NullValueException{
        boolean result = false;
        boolean supportedType = true;
        
        INodeType currentType = NodeTypeManager.getType(AbstractService.getNodeType(node));
        
        if ((currentType != null) && (nodeType != null) &&
                (!currentType.equals(nodeType))) {
                supportedType = false;
            }
        
        //check is node has this property
        if (supportedType) {
            boolean hasProperty = node.hasProperty(propertyName);
            Object propertyValue = null;
            Object compValue = null;
            //get property value
            if (hasProperty){
                propertyValue = node.getProperty(propertyName);
                compValue = propertyValue;
                if (compValue instanceof Number) {
                    compValue = new Double(((Number)compValue).doubleValue());
                }
            }
            //compare
            switch (filterType) {
            case EQUALS:
                if (!hasProperty)
                    break;
                //two variants:
                //1. filter value is NULL - compare both values with null 
                //2. filter value is not NULL - use equals()
                result = ((value == null) && (propertyValue == null)) ||
                ((value != null) && value.equals(propertyValue));
                break;
            case LIKE:
                if (!hasProperty)
                    break;
                result = ((value == null) && (propertyValue == null)) ||
                (propertyValue.toString().matches(value.toString()));
                break;

            case MORE:
                if (!hasProperty)
                    break;

                if (propertyValue == null || value == null)
                    throw new NullValueException();

                if (!(propertyValue instanceof Comparable<?> && value instanceof Comparable<?>)){
                    throw new NotComparebleException();
                }

                result = false;
                if (((Comparable<Serializable>)compValue).compareTo(value) > 0)
                    result = true;          

                break;

            case LESS:
                if (!hasProperty)
                    break;

                if (propertyValue == null || value == null)
                    throw new NullValueException();

                if (!(propertyValue instanceof Comparable<?> && value instanceof Comparable<?>)){
                    throw new NotComparebleException();
                }

                result = false;
                if (((Comparable<Serializable>)compValue).compareTo(value) < 0)
                    result = true;                     

                break;
            case MORE_OR_EQUALS:
                if (!hasProperty)
                    break;

                result = ((value == null) && (propertyValue == null)) ||
                ((value != null) && value.equals(propertyValue));
                if (!(propertyValue instanceof Comparable<?> && value instanceof Comparable<?>)){
                    throw new NotComparebleException();
                }

                if (((Comparable<Serializable>)compValue).compareTo(value) > 0)
                    result = true;                     
                break;
            case LESS_OR_EQUALS:
                if (!hasProperty)
                    break;
                try{
                    result = ((value == null) && (propertyValue == null)) ||
                    ((value != null) && value.equals(propertyValue));
                    if (!(propertyValue instanceof Comparable<?> && value instanceof Comparable<?>)){
                        throw new NotComparebleException();
                    }
                }
                catch(Exception e){

                }
                if (((Comparable<Serializable>)compValue).compareTo(value) < 0)
                    result = true;
                break;
            case EMPTY:
                result = propertyValue == null || propertyValue.toString().isEmpty() || !hasProperty;
                break;
            case NOT_EMPTY:
                result = !(propertyValue == null || propertyValue.toString().isEmpty() || !hasProperty);
                break;
            }
        }

        if (underlyingFilter != null) {
            switch (expressionType) {
            case AND:
                result = result && underlyingFilter.check(node);
                break;
            case OR:
                result = result || underlyingFilter.check(node);
                break;
            }
        }    

        return result;
    }



    @Override
    public INodeType getNodeType() {
        return nodeType;
    }

    public FilterType getFilterType() {
        return filterType;
    }


    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Serializable getValue() {
        return value;
    }

    public IFilter getUnderlyingFilter() {
        return underlyingFilter;
    }

    @Override
    public boolean check(IDataElement dataElement) throws NullValueException, NotComparebleException {
        return check(((DataElement)dataElement).getNode());
    }

}
