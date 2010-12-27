/**
 * 
 */
package org.amanzi.neo.loader.core.saver.network;

import java.util.Arrays;
import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * @author Kasnitskij_V
 *
 */
public class TrafficDataSaver extends AbstractHeaderSaver<BaseTransferData> {

	private boolean headerNotHandled;
	private String networkName;
	
    @Override
    public void init(BaseTransferData element) {
    	super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
    }
    
	@Override
	public void save(BaseTransferData element) {
        if (headerNotHandled) {
        	networkName = element.getFileName();
            definePropertyMap(element);
            startMainTx(1000);
            headerNotHandled = false;
        }
        saveRow(element);
	}
	
    protected void saveRow(BaseTransferData element) {
        String sectorName = getStringValue("Sector", element);
        Double traffic = getNumberValue(Double.class, "Traffic", element);
        Node sector = service.findSector(rootNode, null, null, sectorName, true);
        if (sector != null) {
        	sector.setProperty("Traffic", traffic);
        	updateProperty(networkName, NodeTypes.SECTOR.getId(), sector, "Traffic", traffic);
        }
        else {
        	// TODO: need write "sector not found" to outputStream
        }
    }

    /**
     * Define property map.
     * 
     * @param element the element
     */
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "Sector", new String[] {"Sector"});
        defineHeader(headers, "Traffic", new String[] {"Traffic"});
    }
    
	@Override
	public Iterable<MetaData> getMetaData() {
		return Arrays.asList(new MetaData[0]);
	}

	@Override
	protected void fillRootNode(Node rootNode, BaseTransferData element) {
	}

	@Override
	protected String getRootNodeType() {
		return NodeTypes.NETWORK.getId();
	}

	@Override
	protected String getTypeIdForGisCount(GisProperties gis) {
		return NodeTypes.SECTOR.getId();
	}

}
