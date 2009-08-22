package org.amanzi.awe.catalog.neo;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

/**
 * This implementation of IService can resolve a File containing a Neo4j database.
 * The NeoServiceExtension will already have filtered for directories with the right
 * contents, a file called 'neostore',
 * and will construct this class on the URL for that resource. It can then
 * be used to actually access the data in the resource, using the Neo4J library in the org.neo4j plugin.
 * 
 * We expect the neo4j database to contain nodes connected to the reference node, containing the properties:
 * <ul>
 * <li>'type' = 'gis'</li>
 * <li>'crs' = text description of CRS</li>
 * </ul>
 * This nodes are also expected to have a single reference to the first node of a list of linked nodes containing
 * GIS data.
 * 
 * @author craig@amanzi.com
 */
public class NeoService extends IService {

	private Map<String, Serializable> params;
    private URL url;    // original URL created with
    private Throwable msg;
    private NeoServiceInfo info;
    private Class<Object> type;

    @SuppressWarnings("unchecked")
    NeoService( Map<String, Serializable> params ){
        this.params = params;
        url = (URL) params.get(NeoServiceExtension.URL_KEY);
        type = (Class<Object>) params.get(NeoServiceExtension.CLASS_KEY);
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return (type!=null && adaptee.isAssignableFrom(type)) || super.canResolve(adaptee);
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee.isAssignableFrom(URL.class)) {
            return adaptee.cast(url);
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
	public Map<String, Serializable> getConnectionParams() {
		return params;
	}

	@Override
	public NeoServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new NeoServiceInfo(this);
                }
            }
        }       
        return info;
    }

	private List<NeoGeoResource> members;
    private org.neo4j.api.core.NeoService neo;
    /**
     * This method returns a list containing all NeoGeoResource objects which contains the
     * actual data to be displayed. This method is called when the user expands the service
     * node in the catalog to view the resources the service can offer. 
     * This list should represent all 'gis' nodes referenced by the neo4j reference node.
     * @see org.amanzi.awe.catalog.neo.NeoGeoResource
     */
    @Override
    public List<NeoGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            synchronized (this) {
                if (members == null) {
                    checkNeo(); // check we have a connection to the database
                    Transaction transaction = neo.beginTx();
                    try {
                        members = new ArrayList<NeoGeoResource>();
                        for(Relationship relationship:neo.getReferenceNode().getRelationships(Direction.OUTGOING)){
                            Node node = relationship.getEndNode();
                            if(node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME) && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(INeoConstants.GIS_TYPE_NAME)){
                                members.add(new NeoGeoResource(this,neo,node));
                            }
                        }
                        transaction.success();
                    } finally {
                        transaction.finish();
                    }
                }
            }
        }
        return members;
    }

    private void checkNeo() {
        if(neo == null) {
            neo = NeoServiceProvider.getProvider().getService();
            //TODO: Support actual URL
            //neo = new EmbeddedNeo(url.getPath());
        }
    }
    
    
    public URL getIdentifier() {
		return url;
	}

    
	public Throwable getMessage() {
		return msg;
	}

    
    public Status getStatus() {
        // did an error occur
        if (msg != null)
            return Status.BROKEN;
        // has the file been parsed yet
        if (url == null)
            return Status.NOTCONNECTED;
        return Status.CONNECTED;
    }

	public URL getURL(){
	    return this.url;
	}

}
