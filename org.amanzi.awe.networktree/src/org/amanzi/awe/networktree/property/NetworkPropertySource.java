package org.amanzi.awe.networktree.property;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.networktree.proxy.NeoNode;
import org.amanzi.awe.networktree.proxy.Root;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.PropertyContainer;

/**
 * Class that creates a properties of given Node
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NetworkPropertySource implements IPropertySource {
    
    /*
     * Name of Node category
     */
    private static final String NODE_CATEGORY = "Node";
    
    /*
     * Name of Database category
     */
    private static final String DATABASE_CATEGORY = "Database";
    
    /*
     * Name of Properties category
     */
    protected static final String PROPERTIES_CATEGORY = "Properties";
    
    /*
     * Id of Node Id property
     */
    private static final String NODE_ID = "Id";
    
    /*
     * Key of Node Id
     */
    private static final String ID_KEY = "neoclipse.id";
    
    /*
     * Key of Root Node
     */
    private static final String ROOT_KEY = "neoclipse.root";
    
    /*
     * Id of Root Location property
     */
    private static final String ROOT_LOCATION_ID = "Location";
     
    /*
     * Property container for Node
     */
    private PropertyContainer container;
    
    /*
     * Node proxy
     */
    private NeoNode node;
    
    /*
     * Is this Node is Root
     */
    private boolean isRoot = false;
    
    /**
     * Constructor that create PropertySource
     * 
     * @param node Node for properties
     */
    
    public NetworkPropertySource(NeoNode node) {
        container = node.getNode();
        this.node = node;
        
        isRoot = node instanceof Root;
    }
    
    /**
     * Is this value is editable
     */

    public Object getEditableValue() {
        return false;
    }
    
    /**
     * Creates Category for ID of Node 
     *
     * @return PropertyDescriptors of Node Id
     */
    
    protected List<IPropertyDescriptor> getHeadPropertyDescriptors()
    {
        List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
        // standard properties for nodes
        descs.add( new PropertyDescriptor( ID_KEY, NODE_ID, NODE_CATEGORY ) );
        return descs;
    }

    /**
     * Returns the descriptors for the properties of the relationship.
     */
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
        if (isRoot) {
            descs.addAll( getRootPropertyDescriptors((Root)node) );            
        }
        else {
            descs.addAll( getHeadPropertyDescriptors() );
        }
        
        Iterable<String> keys = container.getPropertyKeys();        
        for ( String key : keys )
        {
            Object value = container.getProperty( (String) key );
            Class<?> c = value.getClass();
            descs.add( new PropertyDescriptor( key, key, PROPERTIES_CATEGORY, c) );
        }
        return descs.toArray( new IPropertyDescriptor[descs.size()] );
    }
    
    /**
     * Computes property value by given id
     * 
     * @param id id of property
     */
    public Object getPropertyValue(Object id) {
        if ( id == ID_KEY ) {
            return ((Node)container).getId();
        }
        else if (id == ROOT_KEY) {
            return ((Root)node).getDatabaseLocation();
        }
        else {
            return container.getProperty((String)id);
        }
    }
    
    /**
     * Creates PropertyDescriptors for Root Node
     *
     * @param root root node
     * @return property descriptors
     */
    
    protected List<IPropertyDescriptor> getRootPropertyDescriptors(Root root)
    {
        List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
        // standard properties for nodes
        descs.add( new PropertyDescriptor(ROOT_KEY, ROOT_LOCATION_ID, DATABASE_CATEGORY) );
        return descs;
    }
    
    /**
     * Check is this property is set
     */

    public boolean isPropertySet(Object id) {
        if ( id == ID_KEY )
        {
            return true;
        }
        else
        {
            return container.hasProperty((String)id);
        }
    }
    
    /**
     * Resets value of property
     */
    public void resetPropertyValue(Object arg0) {
        //do nothing
    }

    /**
     * Sets value for property
     */
    public void setPropertyValue(Object arg0, Object arg1) {
        //do nothing
    }

}
