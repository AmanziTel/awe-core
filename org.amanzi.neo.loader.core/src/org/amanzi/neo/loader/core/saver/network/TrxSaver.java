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
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.neo4j.graphdb.Node;

/**
 * Class to load trx-data 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class TrxSaver extends AbstractHeaderSaver<BaseTransferData> {

    private static final String SECTOR = "Sector";
    private static final String SUBCELL = "Subcell";
    private static final String TRX_ID = "TRX_ID";
    private static final String BAND = "Band";
    private static final String EXTENDED = "Extended";
    private static final String HOPPING_TYPE = "HoppingType";
    private static final String BCCH = "BCCH";
    private static final String HSN = "HSN";
    private static final String MAIO = "MAIO";
    private static final String ARFCN = "ARFCN";
    private boolean headerNotHandled;
    private String networkName;
    private NetworkModel networkModel;
    private int countNotFounded = 0;
    private Integer[] arfcnArray;
    
    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
        networkModel = new NetworkModel(rootNode);
    }
    
    @Override
    public void save(BaseTransferData element) {
        if (headerNotHandled) {
            networkName = rootname;//element.getFileName();
            definePropertyMap(element);
            startMainTx(1000);
            headerNotHandled = false;
        }
        arfcnArray = new Integer[63];
        saveRow(element);
    }
    
    protected void saveRow(BaseTransferData element) {
        String sectorName = getStringValue(SECTOR, element);
        String subcell = getStringValue(SUBCELL, element);
        Integer trxId = getNumberValue(Integer.class, TRX_ID, element);
        String band = getStringValue(BAND, element);
        String extended = null;
        if (band.equals("900")) {
            extended = getStringValue(EXTENDED, element);
        }
        Integer hoppingType = getNumberValue(Integer.class, HOPPING_TYPE, element);
        Byte bcchByte = getNumberValue(Byte.class, BCCH, element);
        boolean bcch = false;
        if (bcchByte == 1) {
            bcch = true;
        }
        String hsn = getStringValue(HSN, element);
        if (hsn.equals("N/A")) {
            hsn = null;
        }
        String maio = getStringValue(MAIO, element);
        if (maio.equals("N/A")) {
            maio = null;
        }
        
        String arfcn = null;
        for (int i = 0; i < 63; i++) {
            arfcn = getStringValue(ARFCN + (i + 1), element);
            if (arfcn != null) {
                if (arfcn.equals("N/A")) {
                    arfcnArray[i] = -1;
                }
                else {
                    arfcnArray[i] = Integer.parseInt(arfcn);
                }
            }
        }
        
        //find sector
        Node sector = service.findSector(rootNode, null, null, sectorName, true);
        
        if (sector != null) {
            updateProperty(networkName, NodeTypes.SECTOR.getId(), sector, SUBCELL, subcell);
            
            NodeResult carrierNode = networkModel.getCarrier(sector, String.valueOf(trxId), null);
            if (carrierNode.isCreated()){
                statistic.updateTypeCount(rootname, NodeTypes.TRX.getId(), 1);
            }
            updateProperty(networkName, NodeTypes.TRX.getId(), carrierNode, TRX_ID, trxId);
            updateProperty(networkName, NodeTypes.TRX.getId(), carrierNode, BAND, band);
            updateProperty(networkName, NodeTypes.TRX.getId(), carrierNode, EXTENDED, extended);
            updateProperty(networkName, NodeTypes.TRX.getId(), carrierNode, HOPPING_TYPE, hoppingType);
            updateProperty(networkName, NodeTypes.TRX.getId(), carrierNode, BCCH, bcch);
            
            NodeResult planNode = networkModel.getPlan(carrierNode, networkName);
            if (planNode.isCreated()){
                statistic.updateTypeCount(networkName, NodeTypes.FREQUENCY_PLAN.getId(), 1);
            }
            updateProperty(networkName, NodeTypes.FREQUENCY_PLAN.getId(), planNode, HSN, hsn);
            updateProperty(networkName, NodeTypes.FREQUENCY_PLAN.getId(), planNode, MAIO, maio);
            updateProperty(networkName, NodeTypes.FREQUENCY_PLAN.getId(), planNode, ARFCN, arfcnArray);
        }
        else {
            getPrintStream().println("Sector with name " + sectorName + " not found!");
            System.out.println(countNotFounded++ + " nodes didn't found!");
        }
        
        updateTx(2, 2);
    }
    /**
     * Define property map.
     * 
     * @param element the element
     */
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, SECTOR, new String[] {SECTOR});
        defineHeader(headers, SUBCELL, new String[] {SUBCELL});
        defineHeader(headers, TRX_ID, new String[] {TRX_ID});
        defineHeader(headers, BAND, new String[] {BAND});
        defineHeader(headers, EXTENDED, new String[] {EXTENDED});
        defineHeader(headers, HOPPING_TYPE, new String[] {HOPPING_TYPE});
        defineHeader(headers, BCCH, new String[] {BCCH});
        defineHeader(headers, HSN, new String[] {HSN});
        defineHeader(headers, MAIO, new String[] {MAIO});
        
        for (int i = 1; i < 64; i++) {
            defineHeader(headers, ARFCN + i, new String[] {ARFCN + i});
        }
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
        return NodeTypes.TRX.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SECTOR.getId();
    }

}
