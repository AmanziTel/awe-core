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
package org.amanzi.awe.catalog.neo;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.INeoServiceProviderListener;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

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
public class NeoService extends IService  implements INeoServiceProviderListener {
    //TODO ZNN need main solution for using class NeoServiceProviderListener instead of interface INeoServiceProviderListener  

	private final Map<String, Serializable> params;
    private final URL url;    // original URL created with
    private Throwable msg;
    private NeoServiceInfo info;
    private final Class<Object> type;

    @SuppressWarnings("unchecked")
    NeoService( Map<String, Serializable> params ){
        this.params = params;
        url = (URL) params.get(NeoServiceExtension.URL_KEY);
        type = (Class<Object>) params.get(NeoServiceExtension.CLASS_KEY);
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        return (type!=null && adaptee.isAssignableFrom(type)) || super.canResolve(adaptee);
    }

    @Override
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

	private List<IGeoResource> members;
    private GraphDatabaseService graphDatabaseService;
    /**
     * This method returns a list containing all NeoGeoResource objects which contains the
     * actual data to be displayed. This method is called when the user expands the service
     * node in the catalog to view the resources the service can offer. 
     * This list should represent all 'gis' nodes referenced by the neo4j reference node.
     * @see org.amanzi.awe.catalog.neo.NeoGeoResource
     */
    @Override
    public List<IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            synchronized (this) {
                if (members == null) {
                    checkNeo(); // check we have a connection to the database
                    Transaction transaction = graphDatabaseService.beginTx();
                    try {
                        members = new ArrayList<IGeoResource>();
                        for(Relationship relationship:graphDatabaseService.getReferenceNode().getRelationships(Direction.OUTGOING)){
                            Node node = relationship.getEndNode();
                            if(node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME) && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(NodeTypes.GIS.getId())){
                                GisTypes gistype = GisTypes.findGisTypeByHeader(node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME).toString());
//                                members.add(gistype==GisTypes.NETWORK?new NeoNetworkGeoRes(this,graphDatabaseService,node):new NeoDriveGeoRes(this, graphDatabaseService, node));
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
        if(graphDatabaseService == null) {
            graphDatabaseService = NeoServiceProviderUi.getProvider().getService();
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
	
	   @Override
	    public void onNeoStop(Object source) {
	        graphDatabaseService = null;
	    }

	    @Override
	    public void onNeoStart(Object source) {
	        graphDatabaseService = NeoServiceProviderUi.getProvider().getService();
	    }

	    @Override
	    public void onNeoCommit(Object source) {
	    }

	    @Override
	    public void onNeoRollback(Object source) {
	    }


        public void updateResource() {
            members=null;
        }

        @Override
        protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
            return null;
        }

}
