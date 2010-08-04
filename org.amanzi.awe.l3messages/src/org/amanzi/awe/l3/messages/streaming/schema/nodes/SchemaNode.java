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

package org.amanzi.awe.l3.messages.streaming.schema.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class SchemaNode {
    
    private ArrayList<SchemaNode> parents = new ArrayList<SchemaNode>();
    
    private LinkedHashMap<ChildInfo, SchemaNode> children = new LinkedHashMap<ChildInfo, SchemaNode>();
    
    private Long size;
    
    private NodeType type;
    
    private String name;
    
    private LinkedList<String> possibleValues;
    
    private String sizeExpression;
    
    private Integer contstantValue;
    
    private Long min;
    
    private Long max;

    /**
     * @param type
     */
    public SchemaNode(String name, NodeType type) {
        super();
        this.name = name;
        this.type = type;
    }
    
    /**
     * @param name
     */
    public SchemaNode(String name) {
        super();
        this.name = name;
    }
    
    /**
     * @param name
     */
    public SchemaNode(String name, Integer constantValue) {
        super();
        this.name = name;
        this.contstantValue = constantValue;
    }
    
    /**
     * @return Returns the size.
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size The size to set.
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * @return Returns the type.
     */
    public NodeType getType() {
        return type;
    }

    /**
     * @param parent The parent to set.
     */
    public void addParent(SchemaNode parent) {
        this.parents.add(parent);        
    }
    
    /**
     * @param type The type to set.
     */
    public void setType(NodeType type) {
        this.type = type;
    }

    /**
     * @param newChild The new chilto add
     */
    public void addChild(SchemaNode newChild, ChildInfo info) {
        this.children.put(info, newChild);
        newChild.addParent(this);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public String getName() {
        return name;
    }
    
    public HashMap<ChildInfo, SchemaNode> getChildren() {
        return this.children;
    }
    
    public void addPossibleValue(String value) {
        if (value.equals("OPTIONAL")) {
            return;
        }
        if (possibleValues == null) {
            possibleValues = new LinkedList<String>();
        }
        possibleValues.add(value);
        if (size == null) {
        	size = 0l;
        }
        size++;
    }
    
    public ArrayList<SchemaNode> getParents() {
        return parents;
    }

    /**
     * @return Returns the sizeExpression.
     */
    public String getSizeExpression() {
        return sizeExpression;
    }

    /**
     * @param sizeExpression The sizeExpression to set.
     */
    public void setSizeExpression(String sizeExpression) {
        this.sizeExpression = sizeExpression;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getConstantValue() {
        return contstantValue;
    }

    /**
     * @return Returns the max.
     */
    public Long getMax() {
        return max;
    }

    /**
     * @param max The max to set.
     */
    public void setMax(Long max) {
        this.max = max;
    }

    /**
     * @return Returns the min.
     */
    public Long getMin() {
        return min;
    }

    /**
     * @param min The min to set.
     */
    public void setMin(Long min) {
        this.min = min;
    }

    /**
     * @return Returns the possibleValues.
     */
    public LinkedList<String> getPossibleValues() {
        return possibleValues;
    }
    
    public void setContstantValue(Integer contstantValue) {
		this.contstantValue = contstantValue;
	}
    
    public void removeChild(String childClassName) {
    	for (ChildInfo child : getChildren().keySet()) {
    		SchemaNode node = getChildren().get(child);
    		if (node.getName().equals(childClassName)) {
    			getChildren().remove(child);
    			break;
    		}
    	}
    }
}
