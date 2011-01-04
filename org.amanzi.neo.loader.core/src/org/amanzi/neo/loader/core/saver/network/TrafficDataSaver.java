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
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class TrafficDataSaver extends AbstractHeaderSaver<BaseTransferData> {

    private static final String TRAFFIC = "Traffic";
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
        Double traffic = getNumberValue(Double.class, TRAFFIC, element);
        Node sector = service.findSector(rootNode, null, null, sectorName, true);
        if (sector != null) {
        	sector.setProperty(TRAFFIC, traffic);
        	updateProperty(networkName, NodeTypes.SECTOR.getId(), sector, TRAFFIC, traffic);
        }
        else {
            getPrintStream().println("Sector with name " + sectorName + " not found!");
        }
        
        updateTx(1, 0);
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
