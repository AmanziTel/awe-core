package org.amanzi.neo.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkElementTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.NeoServiceProviderEventAdapter;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

/**
 * This class was written to handle CSV (tab delimited) network data from ice.net in Sweden.
 * It has been written in a partially generic way so as to be possible to change to various
 * other data sources, but some things are hard coded, like the names of key columns and the 
 * assumption of RT90 projection for non-angular coordinates.
 * 
 * It also assumes the data is structured as BSC->Site->Sector in a tree layout.
 * 
 * @author craig
 */
public class NetworkLoader extends NeoServiceProviderEventAdapter {
	/** String LOAD_NETWORK_TITLE field */
    private static final String LOAD_NETWORK_TITLE = "Load Network";
    private static final String LOAD_NETWORK_MSG = "This network is already loaded into a database. Do you wish to rewrite data?";

    /**
	 * This class handles the CRS specification.
	 * Currently it is hard coded to return WGS84 (EPSG:4326) for data that looks like lat/long
     * and RT90 2.5 gon V (EPSG:3021) for data that looks like it is in meters.
	 * @author craig
	 */
	public static class CRS {
		private String type = null;
		private String epsg = null;
		private CRS(){}
		public String getType(){return type;}
		public String toString(){return epsg;}
		public static CRS fromLocation(float lat, float lon){
			CRS crs = new CRS();
			crs.type = INeoConstants.CRS_TYPE_GEOGRAPHIC;
			crs.epsg = INeoConstants.CRS_EPSG_4326;
			if((lat>90 || lat<-90) && (lon>180 || lon<-180)) {
				crs.type=INeoConstants.CRS_TYPE_PROJECTED;
				crs.epsg = INeoConstants.CRS_EPSG_3021;
			}
			return crs;
		}
	}
	private NeoService neo;
	private NeoServiceProvider neoProvider;
	private String siteName = null;
	private String bscName = null;
	private Node site = null;
	private Node bsc = null;
	private Node network = null;
	private Node gis = null;
	private CRS crs = null;
	private String[] headers = null;
	private HashMap<String,Integer> headerIndex = null;
	private int[] mainIndexes = null;
	private int[] stringIndexes = null;
	private int[] intIndexes = null;
	private String filename;
	private String basename;
	private double[] bbox;

	public NetworkLoader(String filename) {
		this(null, filename);
	}

	public NetworkLoader(NeoService neo, String filename) {
		this.neo = neo;
		if(this.neo == null) {
		    //Lagutko 21.07.2009, using of neo.core plugin
		    neoProvider = NeoServiceProvider.getProvider();
            this.neo = neoProvider.getService();  // Call this first as it initializes everything
            neoProvider.addServiceProviderListener(this);
		}
		this.filename = filename;
		this.basename = (new File(filename)).getName();
	}

	//Lagutko 21.07.2009, using of neo.core plugin
    public void onNeoStop(Object source) {        
        unregisterNeoManager();        
    }
    
    //Lagutko 21.07.2009, using of neo.core plugin
    private void unregisterNeoManager(){        
        neoProvider.commit();
        neoProvider.removeServiceProviderListener(this);        
    }

	public void run() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!parseLine(line)) {
                    break;
                }
            }
        } finally {
            // Close the file reader
            reader.close();
            // Save the bounding box
            if(gis!=null){
                Transaction transaction = neo.beginTx();
                try {
                    gis.setProperty(INeoConstants.PROPERTY_BBOX_NAME, bbox);
                    transaction.success();
                }finally{
                    transaction.finish();
                }
            }
            //Lagutko 21.07.2009, using of neo.core plugin
            unregisterNeoManager();
            // Register the database in the uDIG catalog            
            String databaseLocation = neoProvider.getDefaultDatabaseLocation();
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(new URL("file://"+databaseLocation));
            for (IService service : services) {
                System.out.println("TEMS Found catalog service: " + service);
                if (catalog.getById(IService.class, service.getIdentifier(), new NullProgressMonitor()) != null) {
                    catalog.replace(service.getIdentifier(), service);
                } else {
                    catalog.add(service);
                }
            }
            NeoCorePlugin.getDefault().getUpdateDatabaseManager()
                    .fireUbdateDatabase(new UpdateDatabaseEvent(UpdateDatabaseEventType.GIS));
            // if(services.size()>0) catalog.add(services.get(0));
            
            //Lagutko, 21.07.2009, show NeworkTree
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NetworkTreeView.NETWORK_TREE_VIEW_ID);
            }
            catch (PartInitException e) {
                NeoCorePlugin.error(null, e);
            }
        }
    }

	private static void debug(String line){
		NeoLoaderPlugin.debug(line);
	}
	
	private static void info(String line){
		NeoLoaderPlugin.info(line);
	}
	
	private static void error(String line){
		System.err.println(line);
	}
	
	private boolean parseLine(String line){
		debug(line);
		String fields[] = line.split("\\t");
		if(fields.length>2){
			if(headers==null){	// first line
				headers = fields;
				headerIndex = new HashMap<String,Integer>();
				mainIndexes = new int[5];
				ArrayList<Integer> strings = new ArrayList<Integer>();
				ArrayList<Integer> ints = new ArrayList<Integer>();
				int index=0;
				for(String header:headers){
					debug("Added header["+index+"] = "+header);
					if(header.equals(NetworkElementTypes.BSC.getHeader())) mainIndexes[0]=index;
					else if(header.equals(NetworkElementTypes.SITE.getHeader())) mainIndexes[1]=index;
					else if(header.equals(NetworkElementTypes.SECTOR.getHeader())) mainIndexes[2]=index;
					else if(header.toLowerCase().startsWith(INeoConstants.PROPERTY_LAT_NAME)) mainIndexes[3]=index;
					else if(header.toLowerCase().startsWith(INeoConstants.PROPERTY_LONG_NAME)) mainIndexes[4]=index;
					else if(header.toLowerCase().contains(INeoConstants.PROPERTY_TYPE_NAME)) strings.add(index);
					else if(header.toLowerCase().contains(INeoConstants.NETWORK_HEADER_STATUS_NAME)) strings.add(index);
					else ints.add(index);
					headerIndex.put(header,index++);
				}
				stringIndexes = new int[strings.size()];
				for(int i=0;i<strings.size();i++) stringIndexes[i] = strings.get(i);
				intIndexes = new int[ints.size()];
				for(int i=0;i<ints.size();i++) intIndexes[i] = ints.get(i);
			}else{
				Transaction transaction = neo.beginTx();
				try {
					String bscField = fields[mainIndexes[0]];
					String siteField = fields[mainIndexes[1]];
					String sectorField = fields[mainIndexes[2]];
					if (!bscField.equals(bscName)) {
						bscName = bscField;
						debug("New BSC: " + bscName);
						if (network==null){
						    network = getNetwork(neo, basename);
						    if (network==null){
						        return false;
						    }
						}
						gis = getGISNode(neo, network);
						network.setProperty(INeoConstants.PROPERTY_FILENAME_NAME, filename);
						deleteTree(network);
						bsc = addChild(network, NetworkElementTypes.BSC.toString(), bscName);
					}
					if (!siteField.equals(siteName)) {
						siteName = siteField;
						debug("New site: " + siteName);
						Node newSite = addChild(bsc, NetworkElementTypes.SITE.toString(), siteName);
				        if(site!=null){
				            site.createRelationshipTo(newSite, GeoNeoRelationshipTypes.NEXT);
				        }else{
				            network.createRelationshipTo(newSite, GeoNeoRelationshipTypes.NEXT);
				        }
				        site = newSite;
						float lat = Float.parseFloat(fields[mainIndexes[3]]);
						float lon = Float.parseFloat(fields[mainIndexes[4]]);
						if(crs==null){
							crs = CRS.fromLocation(lat, lon);
                            network.setProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, crs.getType());
                            network.setProperty(INeoConstants.PROPERTY_CRS_NAME, crs.toString());
                            gis.setProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, crs.getType());
                            gis.setProperty(INeoConstants.PROPERTY_CRS_NAME, crs.toString());
						}
						site.setProperty(INeoConstants.PROPERTY_LAT_NAME, lat);
						site.setProperty(INeoConstants.PROPERTY_LON_NAME, lon);
						if(bbox==null) {
						    bbox = new double[]{lon,lon,lat,lat};
						}else{
                            if(bbox[0]>lon) bbox[0]=lon;
                            if(bbox[1]<lon) bbox[1]=lon;
                            if(bbox[2]>lat) bbox[2]=lat;
                            if(bbox[3]<lat) bbox[3]=lat;
						}
					}
					debug("New Sector: " + sectorField);
					Node sector = addChild(site, NetworkElementTypes.SECTOR.toString(), sectorField);
					for (int i : stringIndexes) sector.setProperty(headers[i], fields[i]);
					for (int i : intIndexes) sector.setProperty(headers[i], Integer.parseInt(fields[i]));
					transaction.success();
					return true;
				} finally {
					transaction.finish();
				}
			}
		}
        return true;
	}

	private void deleteTree(Node root){
		if(root!=null){
			for (Relationship relationship : root.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
				Node node = relationship.getEndNode();
				deleteTree(node);
				node.delete();
				relationship.delete();
			}
		}
	}
	
	/**
	 * This code finds the specified network node in the database, creating its own transaction for that.
	 */
	public static Node getNetwork(NeoService neo, String basename) {
		Node network = null;
		Transaction transaction = neo.beginTx();
		try {
			Node reference = neo.getReferenceNode();
			for (Relationship relationship : reference.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
				Node node = relationship.getEndNode();
				if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NetworkElementTypes.NETWORK.toString()) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)
						&& node.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(basename)){
                    int resultMsg = (Integer)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {
                        int result;
                        @Override
                        public void run() {
                            MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK
                                    | SWT.CANCEL);
                            msg.setText(LOAD_NETWORK_TITLE);
                            msg.setMessage(LOAD_NETWORK_MSG);
                            result = msg.open();

                        }

                        @Override
                        public Object getValue() {
                            return new Integer(result);
                        }
                    });
                    if (resultMsg != SWT.OK) {
                        return null;
                    }
                    // delete network - begin - gis node.
                    Node nodeGis = getGISNode(neo, node);
                    NeoCorePlugin.getDefault().getProjectService().deleteNode(nodeGis);
                    break;
                }
			}
			network = neo.createNode();
			network.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementTypes.NETWORK.toString());
			network.setProperty(INeoConstants.PROPERTY_NAME_NAME, basename);
			reference.createRelationshipTo(network, NetworkRelationshipTypes.CHILD);
			transaction.success();
		}catch (Exception e){
		    e.printStackTrace();
		} finally {
			transaction.finish();
		}
		if(network!=null) getGISNode(neo,network);
		return network;
	}

	private static Node getGISNode(NeoService neo, Node network) {
        Node gis = null;
        Transaction transaction = neo.beginTx();
        try {
            Node reference = neo.getReferenceNode();
            for (Relationship relationship : reference.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(INeoConstants.GIS_TYPE_NAME) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME) && node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().equals(INeoConstants.GIS_PREFIX+network.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString()))
                    return node;
            }
            gis = neo.createNode();
            gis.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.GIS_TYPE_NAME);
            gis.setProperty(INeoConstants.PROPERTY_NAME_NAME, INeoConstants.GIS_PREFIX + network.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString());
            gis.setProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, GisTypes.Network.getHeader());
            reference.createRelationshipTo(gis, NetworkRelationshipTypes.CHILD);
            gis.createRelationshipTo(network, GeoNeoRelationshipTypes.NEXT);
            transaction.success();
        } finally {
            transaction.finish();
        }
        return gis;
	}

	/**
	 * This code expects you to create a transaction around it, so don't forget to do that.
	 * @param parent
	 * @param type
	 * @param name
	 * @return
	 */
	private Node addChild(Node parent, String type, String name) {
		Node child = null;
		child = neo.createNode();
		child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, type);
		child.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
		if (parent != null) {
			parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
			debug("Added '" + name + "' as child of '" + parent.getProperty(INeoConstants.PROPERTY_NAME_NAME));
		}
		return child;
	}
	
	public void printStats(){
		if(bsc!=null){
			printChildren(bsc,0);
		}else{
			error("No BSC node found");
		}
	}

	public static void printChildren(Node node, int depth){
		if(node==null || depth > 4 || !node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)) return;
		StringBuffer tab = new StringBuffer();
		for(int i=0;i<depth;i++) tab.append("    ");
		StringBuffer properties = new StringBuffer();
		for(String property:node.getPropertyKeys()) {
			if(!property.equals(INeoConstants.PROPERTY_NAME_NAME)) properties.append(" - ").append(property).append(" => ").append(node.getProperty(property));
		}
		info(tab.toString()+node.getProperty(INeoConstants.PROPERTY_NAME_NAME)+properties);
		for(Relationship relationship:node.getRelationships(NetworkRelationshipTypes.CHILD,Direction.OUTGOING)){
			//debug(tab.toString()+"("+relationship.toString()+") - "+relationship.getStartNode().getProperty("name")+" -("+relationship.getType()+")-> "+relationship.getEndNode().getProperty("name"));
			printChildren(relationship.getEndNode(),depth+1);
		}
	}
	
	//TODO: Lagutko: is this method required?
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EmbeddedNeo neo = new EmbeddedNeo("var/neo");
		try{
			NetworkLoader networkLoader = new NetworkLoader(neo,args.length>0 ? args[0] : "amanzi/network.csv");
			networkLoader.run();
			networkLoader.printStats();
		} catch (IOException e) {
			System.err.println("Failed to load network: "+e);
			e.printStackTrace(System.err);
		}finally{
			neo.shutdown();
		}
	}
}
