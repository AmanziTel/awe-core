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
package org.amanzi.neo.core.database.nodes;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.NeoServiceProviderListener;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * Abstract wrapper of Neo Database Node
 * 
 * @author Lagutko_N
 */

public abstract class AbstractNode extends NeoServiceProviderListener {

	/*
	 * Wrapped Node
	 */
	protected Node node;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

	/**
	 * Constructor of abstract Wrapper
	 * 
	 * @param node
	 *            wrapped Node
	 */

	protected AbstractNode(Node node) {
		this.node = node;		
	}

	/**
	 * Constructor of abstract Wrapper
	 * 
	 * @param node
	 *            wrapped Node
	 * @param nodeName
	 *            node name
	 * @param nodeType
	 *            node type
	 */
	protected AbstractNode(Node node, String nodeName, NodeTypes nodeType) {
		this(node);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, nodeName);
		nodeType.setNodeType(node, null);
	}

	/**
	 * Method that creates a relationship to other node
	 * 
	 * @param type
	 *            type of Relationship
	 * @param node
	 *            related node
	 * @return created Relationship
	 */

	protected Relationship addRelationship(RelationshipType type, Node node) {
		return this.node.createRelationshipTo(node, type);
	}

	/**
	 * Method that creates a relationship to other node
	 * 
	 * @param type
	 *            type of Relationship
	 * @param node
	 *            wrapper node
	 */

	protected void addRelationship(RelationshipType type, AbstractNode node) {
		this.node.createRelationshipTo(node.getUnderlyingNode(), type);
	}

	/**
	 * Returns wrapped node
	 * 
	 * @return Database Node
	 */

	public Node getUnderlyingNode() {
		return node;
	}

	/**
	 * Sets the parameter of Node
	 * 
	 * @param name
	 *            Name of Parameter
	 * @param value
	 *            Value of Parameter
	 */

	protected void setParameter(String name, Object value) {
		//Lagutko, 6.10.2009, if Node already has this value than wouldn't change it
		//this fix will resolve problem with deadlocks on read
	    if (node.hasProperty(name)) {
	        Object previousValue = node.getProperty(name);
	        if (previousValue.equals(value)) {
	            return;
	        }
	    }
		node.setProperty(name, value);
	}

	/**
	 * Returns cached value of Parameter of get it from Node
	 * 
	 * @param name
	 *            parameter name
	 * @return value of parameter
	 */
	
	protected Object getParameter(String name) {
	    Object value = null;
	    
	    if (node.hasProperty(name)) {
	        value = node.getProperty(name);
	    }
	    
	    return value;
	}
	/**
	 * Returns cached value of Parameter of get it from Node
	 * 
	 * @param name
	 *            parameter name
	 * @param defvalue - default value         
	 * @return value of parameter, or defvalue if property not exist
	 */

	protected Object getParameter(String name,Object defvalue) {
	    return getUnderlyingNode().getProperty(name,defvalue);
	}

	/**
	 * Returns Name of this Node
	 * 
	 * @return
	 */

	public String getName() {
		return (String) getParameter(INeoConstants.PROPERTY_NAME_NAME);
	}

	/**
	 * Deletes this Node
	 * 
	 */
	public void delete() {
		node.delete();
	}
	
	/**
	 *Get NeoService  
	 * @return NeoService. If NeoService id nod defined, then sets and return NeoServiceProvider.getProvider().getService()
	 */
    protected  GraphDatabaseService getService(){
        r.lock();
        try{
            if (graphDatabaseService==null){
                setService(NeoServiceProvider.getProvider().getService());
            }
            return graphDatabaseService;
        }finally{
            r.unlock();
        }
    }
    /**
     * set NeoService
     *
     * @param service - new NeoService
     */
    public void setService(GraphDatabaseService service){
        w.lock();
        try{
            this.graphDatabaseService=service;
        }finally{
            w.unlock();
        }
    }
}
