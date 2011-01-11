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

package org.amanzi.awe.afp.ericsson.saver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.awe.afp.ericsson.parser.NetworkConfigurationFileTypes;
import org.amanzi.awe.afp.ericsson.parser.NetworkConfigurationTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Saver for Network configuration data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkConfigurationSaver extends AbstractHeaderSaver<NetworkConfigurationTransferData> implements IStructuredSaver<NetworkConfigurationTransferData> {
    private final MetaData metadata = new MetaData("network", MetaData.SUB_TYPE, "radio");
    private NetworkConfigurationFileTypes type;
    private NetworkService networkService;
    private Node neighbourRoot;
    private String neighName;
    private Object bsc;
    private Map<String, String> tgProperty = new HashMap<String, String>();
    private Pattern tgPat = Pattern.compile("(^.*)(-)(\\d+$)", Pattern.CASE_INSENSITIVE);
    private Pattern trxPat = Pattern.compile("(^.*)(-)(\\d+)(-)(\\d+$)", Pattern.CASE_INSENSITIVE);

    @Override
    public void init(NetworkConfigurationTransferData element) {
        super.init(element);
        networkService = NeoServiceFactory.getInstance().getNetworkService();
        startMainTx(2000);
    }

    @Override
    public void save(NetworkConfigurationTransferData element) {
        if (type == NetworkConfigurationFileTypes.CNA) {
            saveCNALine(element);
        } else {
            saveBSMLine(element);
        }
    }

    /**
     * @param element
     */
    private void saveCNALine(NetworkConfigurationTransferData element) {
        String bscName = getStringValue("BSC", element);
        // TODO add vendor as property of BSC if necessary - but not from file - maybe from parser

        if (StringUtils.isEmpty(bscName)) {
            info(String.format("Line N%s not parsed - field '%s' is empty", element.getLine(), "BSC"));
            return;
        }
        Node bscNode = networkService.getBscNode(rootNode, bscName, rootNode);
        String sectorName = getStringValue("CELL", element);
        if (StringUtils.isEmpty(sectorName)) {
            info(String.format("Line N%s - field '%s' is empty", element.getLine(), "cell"));
            return;
        }
        String siteName = getStringValue("SITE", element);
        if (siteName == null) {
            siteName = sectorName.substring(0, sectorName.length() - 1);
        }
        if (StringUtils.isEmpty(siteName)) {
            info(String.format("Line N%s - field '%s' is empty", element.getLine(), "SITE"));
            return;
        }
        Node site = networkService.getSite(rootNode, siteName, bscNode);
        Integer ci = getNumberValue(Integer.class, "ci", element);
        Integer lac = getNumberValue(Integer.class, "lac", element);

        Node sector = networkService.findSector(rootNode, ci, lac, sectorName, true);
        if (sector == null) {
            sector = networkService.createSector(rootNode, site, sectorName, ci, lac);
            updateTx(1, 1);
            if (ci != null) {
                statistic.indexValue(rootname, NodeTypes.SECTOR.getId(), INeoConstants.PROPERTY_SECTOR_CI, ci);
            }
            if (lac != null) {
                statistic.indexValue(rootname, NodeTypes.SECTOR.getId(), INeoConstants.PROPERTY_SECTOR_LAC, lac);
            }
        }
        String bcc = getStringValue("bcc", element);
        boolean notEmptyBcc = StringUtils.isNotEmpty(bcc);
        String ncc = getStringValue("ncc", element);
        boolean notEmptyNcc = StringUtils.isNotEmpty(ncc);
        if (notEmptyBcc) {
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "bcc", bcc);
        }
        if (notEmptyNcc) {
            updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "ncc", ncc);
        }
        if (notEmptyBcc && notEmptyNcc) {
            String bsic = bcc.matches("0+") ? ncc : bcc + ncc;
            networkService.indexProperty(rootNode, sector, "BSIC", bsic);
        }
        Integer bcchno = getNumberValue(Integer.class, "bcchno", element);
        updateProperty(rootname, NodeTypes.SECTOR.getId(), sector, "bcchno", bcchno);
        for (int i = 0; i < 16; i++) {
            if (getStringValue("ch_group_" + i, element) != null) {
                storeChannalInfo(sector, i, element);
            }
        }
        // TODO NEIGHBOUR creation should be refactored if physical network file will be loaded
        // AFTER CNA files...
        for (int i = 0; i <= 63; i++) {
            String neighbour = getStringValue("n_cell_" + i, element);
            if (StringUtils.isNotEmpty(neighbour)) {
                Node neighbourSector = networkService.findSector(rootNode, null, null, neighbour, true);
                if (neighbourSector == null) {
                    info(String.format("Line %s: Neighbour sector with name %s not found", element.getLine(), neighbour));
                    continue;
                }
                getNeighbourRelation(neighbourRoot, sector, neighbourSector);
            }
        }
        updateTx(5, 5);// not real values. for real values methods getXXX should return NodeResult
                       // values

    }

    /**
     * @param neighbourRoot
     * @param sector
     * @param neighbourSector
     * @return
     */
    public Relationship getNeighbourRelation(Node neighbourRoot, Node sector, Node neighbourSector) {
        DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
        NodeResult proxyServ = service.getNeighbourProxy(neighbourRoot, sector);
        if (proxyServ.isCreated()) {
            updateTx(1, 1);
            statistic.updateTypeCount(neighName, NodeTypes.SECTOR_SECTOR_RELATIONS.getId(), 1);
        }
        NodeResult proxyNeigh = service.getNeighbourProxy(neighbourRoot, neighbourSector);
        if (proxyNeigh.isCreated()) {
            updateTx(1, 1);
            statistic.updateTypeCount(neighName, NodeTypes.SECTOR_SECTOR_RELATIONS.getId(), 1);
        }
        Relationship rel;
        if (proxyServ.isCreated() || proxyNeigh.isCreated()) {
            rel = proxyServ.createRelationshipTo(proxyNeigh, NetworkRelationshipTypes.NEIGHBOUR);
            updateTx(0, 1);
        } else {
            Iterator<Relationship> it = Utils.getRelations(proxyServ, proxyNeigh, NetworkRelationshipTypes.NEIGHBOUR).iterator();
            rel = it.hasNext() ? it.next() : null;
            if (rel == null) {
                rel = proxyServ.createRelationshipTo(proxyNeigh, NetworkRelationshipTypes.NEIGHBOUR);
                updateTx(0, 1);
            }
        }
        return rel;

    }

    /**
     * Store channal info.
     * 
     * @param sector the sector
     * @param i the i
     * @param element the element
     */
    private void storeChannalInfo(Node sector, int i, NetworkConfigurationTransferData element) {
        Node channalGr = networkService.getChannelNode(sector, i);
        String tg = getStringValue(i, "chgr_tg", element);
        if (StringUtils.isNotEmpty(tg)) {
            channalGr.setProperty("tg", tg);
        }
        String sctype = getStringValue(i, "sctype", element);
        if (StringUtils.isNotEmpty(sctype)) {
            channalGr.setProperty("sctype", sctype);
        }
        String band = getStringValue(i, "band", element);
        if (StringUtils.isNotEmpty(band)) {
            channalGr.setProperty("band", band);
        }
        String hop = getStringValue(i, "hop", element);
        if (StringUtils.isNotEmpty(hop)) {
            channalGr.setProperty("hop", hop);
        }
        int[] dccno = new int[64];
        int j = 0;
        for (int ind = 0; ind < 64; ind++) {
            String valStr = getStringValue(i, "dchno_" + ind, element);
            if (valStr != null) {
                try {
                    int valInt = Integer.parseInt(valStr);
                    dccno[j++] = valInt;
                } catch (NumberFormatException e) {
                    // do nothing
                }

            }
        }
        int[] dccnoArr = Arrays.copyOf(dccno, j);
        channalGr.setProperty("dchno", dccnoArr);
        for (int mId = 0; mId < 16; mId++) {
            String propName = "maio_" + mId;
            String strVal = getStringValue(i, propName, element);
            if (strVal != null && !"default".equalsIgnoreCase(strVal)) {
                try {
                    Integer val = Integer.parseInt(strVal);
                    channalGr.setProperty(propName, val);
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }
        // TODO implement;
    }

    /**
     * Gets the string value.
     * 
     * @param num the num
     * @param header the header
     * @param element the element
     * @return the string value
     */
    private String getStringValue(int num, String header, NetworkConfigurationTransferData element) {
        int ind = -1;
        for (int i = 0; i < element.getHeaders().length; i++) {
            if (element.getHeaders()[i].equals(header)) {
                if (++ind == num) {
                    String value = element.getValuesData()[i];
                    return value.equalsIgnoreCase("NULL") ? null : value;
                }
            }
        }
        return null;
    }

    /**
     * Save bsm line.
     * 
     * @param element the element
     */
    private void saveBSMLine(NetworkConfigurationTransferData element) {
        switch (element.getMode()) {
        case TG:
            String tgfull = element.getTg();
            Matcher matcher = tgPat.matcher(tgfull);
            if (!matcher.find(0)) {
                error(String.format("Incorrect TG format: %s", tgfull));
                return;
            }
            tgProperty.put(matcher.group(3), element.getFhop());
            return;

        default:
            // trx
            // TODO find only for child of bsc field?
            Node sector = networkService.findSector(rootNode, null, null, element.getCell(), true);
            if (sector == null) {
                error(String.format("Line %s: Sector with name %s not found", element.getLine(), element.getCell()));
                return;
            }
            tgfull = element.getTg();
            matcher = trxPat.matcher(tgfull);
            if (!matcher.find(0)) {
                error(String.format("Incorrect TG format: %s", tgfull));
                return;
            }
            String tg = matcher.group(3);
            String trxId = matcher.group(5);
            Integer channelGr = element.getChGroup();
            Node trx = networkService.getTRXNode(sector, trxId, channelGr);
            updateTx(1, 1);
            Node channalGr = networkService.getChannelNode(sector, channelGr);
            updateProperty(rootname, NodeTypes.TRX.getId(), trx, "band", channalGr.getProperty("band", null));
            int hoptype;
            if ("ON".equalsIgnoreCase((String)channalGr.getProperty("hop", null))) {
                String fchop = tgProperty.get(tg);
                hoptype = "BB".equalsIgnoreCase(fchop) ? 1 : 2;
            } else {
                hoptype = 0;
            }
            updateProperty(rootname, NodeTypes.TRX.getId(), trx, "hopping_type", hoptype);
            updateProperty(rootname, NodeTypes.TRX.getId(), trx, "band", channalGr.getProperty("band", null));
            boolean isBcch = 0 == channelGr && "0".equals(trxId);
            updateProperty(rootname, NodeTypes.TRX.getId(), trx, "bcch", isBcch);
            Node plan = networkService.getPlanNode(trx, element.getFileName());
            updateProperty(rootname, NodeTypes.FREQUENCY_PLAN.getId(), trx, "hsn", hoptype);
            Integer bcchno = (Integer)sector.getProperty("bcchno");
            if (!plan.hasProperty("arfcn")) {
                int[] arfcn = null;
                if (isBcch) {
                    if (bcchno != null) {
                        arfcn = new int[1];
                        arfcn[0] = bcchno;
                    }
                } else {
                    int[] dchno = (int[])channalGr.getProperty("dchno", null);
                    if (dchno != null) {
                        if (hoptype < 2 && bcchno != null) {
                            arfcn = new int[dchno.length];
                            int j = 0;
                            for (int i = 0; i < dchno.length; i++) {
                                if (dchno[i] != bcchno) {
                                    arfcn[j++] = dchno[i];
                                }
                            }
                            arfcn = Arrays.copyOf(arfcn, j);
                        } else {
                            arfcn = dchno;
                        }
                    }
                }
                if (arfcn != null) {
                    plan.setProperty("arfcn", arfcn);
                }
            }
            if (!plan.hasProperty("maio")) {
                String maioPr = "maio_" + trxId;
                Integer maioInt = (Integer)channalGr.getProperty(maioPr,null);

                if (maioInt == null) {
                    int[] arfcn = (int[])plan.getProperty("arfcn", null);
                    if (arfcn != null) {
                        int maxVal = arfcn.length;
                        int numTrx = Integer.valueOf(trxId);
                        if (channelGr == 0) {
                            numTrx++;
                        }
                        if (numTrx > maxVal) {
                            error("Can't create maio property for trx with id=" + trxId);
                        } else {
                            maxVal--;
                            int oddVal = (numTrx - 1) * 2;
                            // TODO check formula
                            maioInt = oddVal <= maxVal ? oddVal : numTrx - (maxVal + 1) / 2;
                        }
                    }
                }
                updateProperty(rootname, NodeTypes.FREQUENCY_PLAN.getId(), plan, "maio", maioInt);
            }
            break;
        }
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[] {metadata});
    }

    @Override
    public boolean beforeSaveNewElement(NetworkConfigurationTransferData element) {
        tgProperty.clear();
        type = element.getType();
        if (type == NetworkConfigurationFileTypes.CNA) {
            neighName = rootname + "neigh";
            neighbourRoot = service.getNeighbour(rootNode, neighName);
        } else {
            String bscName = element.getBsc();
            if (StringUtils.isEmpty(bscName)) {
                return true;
            }
            bsc = networkService.findBscNode(rootNode, bscName);
            if (bsc == null) {
                error(String.format("BSC with name %s not found", bscName));
                return true;
            }

        }
        return false;
    }

    @Override
    public void finishSaveNewElement(NetworkConfigurationTransferData element) {
    }

    @Override
    protected void fillRootNode(Node rootNode, NetworkConfigurationTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SITE.getId();
    }

    public static void main(String[] args) {

    }
}
