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

package org.amanzi.awe.afp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.awe.afp.models.parameters.ChannelType;
import org.amanzi.awe.afp.models.parameters.FrequencyBand;
import org.amanzi.awe.afp.models.parameters.OptimizationType;
import org.amanzi.awe.afp.services.AfpService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.filters.ExpressionType;
import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.FilterType;
import org.amanzi.neo.services.network.INetworkTraversableModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.networkselection.SelectionModel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class AfpModelNew {
    
    public static final int BSIC_MAX_NUMBER = 7;
    
    private Node afpModelNode;

    private NetworkModel networkModel;

    private SelectionModel selectionModel;

    private String scenarioName;

    private AfpService afpService;

    private DatasetService datasetService;

    private HashMap<OptimizationType, Boolean> optimizationTypes = new HashMap<OptimizationType, Boolean>();

    private LinkedHashMap<String, FrequencyDomain> frequencyDomains = new LinkedHashMap<String, FrequencyDomain>();

    private HashMap<ChannelType, Boolean> channelTypes = new HashMap<ChannelType, Boolean>();
    
    private int siteCount;
    
    private int sectorCount;
    
    private int trxCount;
    
    private HashMap<String, Boolean> supportedBCC = new HashMap<String, Boolean>();
    
    private HashMap<String, Boolean> supportedNCC = new HashMap<String, Boolean>();
    
    private HashMap<ChannelType, Integer> channelCount = new HashMap<ChannelType, Integer>();

    private AfpModelNew() {
        afpService = AfpService.getService();
        datasetService = NeoServiceFactory.getInstance().getDatasetService();
    }

    public AfpModelNew(String scenarioName, NetworkModel network) {
        this();

        this.scenarioName = scenarioName;

        afpModelNode = afpService.createAfpNode(scenarioName, network.getRootNode());
        setNetworkModel(network);

        init();
    }

    public AfpModelNew(Node afpModelNode) {
        this();

        this.afpModelNode = afpModelNode;
        this.scenarioName = (String)afpModelNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);

        loadData();
    }

    public void setNetworkModel(NetworkModel networkModel) {
        this.networkModel = networkModel;
    }

    public void setSelectionModel(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    public String getName() {
        return scenarioName;
    }

    public void saveData() {
        // save optimization types
        ArrayList<OptimizationType> enabledTypes = new ArrayList<OptimizationType>();
        for (Entry<OptimizationType, Boolean> singleType : optimizationTypes.entrySet()) {
            if (singleType.getValue()) {
                enabledTypes.add(singleType.getKey());
            }
        }
        afpService.saveOptimizationTypes(afpModelNode, enabledTypes);

        // save selected bands
        ArrayList<FrequencyBand> enabledBands = new ArrayList<FrequencyBand>();
        for (FrequencyBand band : FrequencyBand.values()) {
            if (frequencyDomains.containsKey(band.getText())) {
                enabledBands.add(band);
            }
        }
        afpService.saveOptimizationBands(afpModelNode, enabledBands);

        //save channel types
        ArrayList<ChannelType> enabledChannels = new ArrayList<ChannelType>();
        for (Entry<ChannelType, Boolean> singleType : channelTypes.entrySet()) {
            if (singleType.getValue()) {
                enabledChannels.add(singleType.getKey());
            }
        }
        afpService.saveChannelTypes(afpModelNode, enabledChannels);
        
        //save supported BCC
        ArrayList<String> bcc = new ArrayList<String>();
        for (Entry<String, Boolean> singleBCC : supportedBCC.entrySet()) {
            if (singleBCC.getValue()) {
                bcc.add(singleBCC.getKey());
            }
        }
        afpService.saveBCC(afpModelNode, bcc);
        
        //save supported NCC
        ArrayList<String> ncc = new ArrayList<String>();
        for (Entry<String, Boolean> singleNCC : supportedNCC.entrySet()) {
            if (singleNCC.getValue()) {
                ncc.add(singleNCC.getKey());
            }
        }
        afpService.saveBCC(afpModelNode, ncc);
    }

    public void loadData() {
        //load OptimizationTypes
        initializeOptimizationTypes();
        List<OptimizationType> enabledTypes = afpService.loadOptimizationTypes(afpModelNode);
        for (OptimizationType type : optimizationTypes.keySet()) {
            if (!enabledTypes.contains(type)) {
                optimizationTypes.put(type, false);
            }
        }
        
        //load Channel Types
        initializeChannelTypes();
        List<ChannelType> enabledChannels = afpService.loadChannelTypes(afpModelNode);
        for (ChannelType type : channelTypes.keySet()) {
            if (!enabledChannels.contains(type)) {
                channelTypes.put(type, false);
            }
        }
        
        //load Optimization Bands
        List<FrequencyBand> enabledBands = afpService.loadOptimizationBands(afpModelNode);
        frequencyDomains.clear();
        for (FrequencyBand band : enabledBands) {
            frequencyDomains.put(band.getText(), createFrequencyDomain(band));
        }
    }

    private INetworkTraversableModel getTraversableModel() {
        if (selectionModel == null) {
            return networkModel;
        }

        return selectionModel;
    }
    
    private Iterable<Node> getElementTraverser(Evaluator filter, INodeType...nodeTypes) {
        return getTraversableModel().getAllElementsByType(filter, nodeTypes);
    }

    public static List<AfpModelNew> getAllAfpScenarios(NetworkModel model) {
        ArrayList<AfpModelNew> result = new ArrayList<AfpModelNew>();

        AfpService afpService = AfpService.getService();

        for (Node afpNode : afpService.getAllAfpNodes(model.getRootNode())) {
            AfpModelNew scenario = new AfpModelNew(afpNode);
            scenario.setNetworkModel(model);

            result.add(scenario);
        }

        return result;
    }

    public boolean isOptimizationSupported(OptimizationType optimizationType) {
        return optimizationTypes.get(optimizationType);
    }

    public void setOptimizationSupport(OptimizationType optimizationType, Boolean isSupported) {
        optimizationTypes.put(optimizationType, isSupported);
    }

    private void init() {
        initializeOptimizationTypes();
        initializeChannelTypes();
        addDefaultFrequencyDomains();
        initializeTotalCounts();
        initializeBCC();
        initializeNCC();
    }
    
    private void initializeBCC() {
        supportedBCC.clear();
        for (int i = 0; i < BSIC_MAX_NUMBER; i++) {
            supportedBCC.put(Integer.toString(i), Boolean.TRUE);
        }
    }
    
    private void initializeNCC() {
        supportedNCC.clear();
        for (int i = 0; i < BSIC_MAX_NUMBER; i++) {
            supportedNCC.put(Integer.toString(i), Boolean.TRUE);
        }
    }
    
    private void initializeTotalCounts() {
        siteCount = 0;
        sectorCount = 0;
        trxCount = 0;
    }

    private void addDefaultFrequencyDomains() {
        frequencyDomains.clear();
        for (FrequencyBand band : FrequencyBand.values()) {
            frequencyDomains.put(band.getText(), createFrequencyDomain(band));
        }
    }
    
    public Map<String, FrequencyDomain> getFreeFrequencyDomains() {
        HashMap<String, FrequencyDomain> result = new HashMap<String, FrequencyDomain>();
        for (FrequencyDomain domain : frequencyDomains.values()) {
            if (domain.isFree()) {
                result.put(domain.getName(), domain);
            }
        }
        
        return result;
    }

    private void initializeOptimizationTypes() {
        optimizationTypes.clear();
        for (OptimizationType type : OptimizationType.values()) {
            optimizationTypes.put(type, true);
        }
    }

    private void initializeChannelTypes() {
        channelTypes.clear();
        channelCount.clear();
        for (ChannelType type : ChannelType.values()) {
            channelTypes.put(type, true);
            channelCount.put(type, 0);
        }
    }

    private FrequencyDomain createFrequencyDomain(FrequencyBand band) {
        FrequencyDomain domain = new FrequencyDomain(band.getText(), true);

        Filter domainFilter = new Filter(FilterType.LIKE, ExpressionType.OR);
        domainFilter.setExpression(NodeTypes.TRX, INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, band.getRegExp());
        
        Filter sectorBandFilter = new Filter(FilterType.LIKE, ExpressionType.OR);
        sectorBandFilter.setExpression(NodeTypes.SECTOR, INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, band.getRegExp());
        
        Filter sectorLayerFilter = new Filter(FilterType.LIKE);
        sectorLayerFilter.setExpression(NodeTypes.SECTOR, INeoConstants.AFP_PROPERTY_LAYER_NAME, band.getRegExp());
        
        sectorBandFilter.addFilter(sectorLayerFilter);
        domainFilter.addFilter(sectorBandFilter);
        
        domain.setFilter(domainFilter);

        return domain;
    }

    public boolean isFrequencyBandSupported(FrequencyBand band) {
        return frequencyDomains.containsKey(band.getText());
    }

    public void setFrequencyBandSupport(FrequencyBand band, boolean isSupported) {
        boolean contains = frequencyDomains.containsKey(band);

        if (contains && !isSupported) {
            frequencyDomains.remove(band);
            return;
        }
        if (!contains && isSupported) {
            frequencyDomains.put(band.getText(), createFrequencyDomain(band));
        }
    }

    public boolean isChannelTypeSupported(ChannelType type) {
        return channelTypes.get(type);
    }

    public void setChannelTypeSupported(ChannelType type, boolean isSupported) {
        channelTypes.put(type, isSupported);
    }
 
    public void countFreeDomains() {
        for (Node node : getElementTraverser(null, NodeTypes.SECTOR, NodeTypes.SITE, NodeTypes.TRX)) {
            INodeType nodeType = datasetService.getNodeType(node);
            
            if (nodeType.equals(NodeTypes.SITE)) {
                siteCount++;
            }
            else if (nodeType.equals(NodeTypes.SECTOR)) {
                sectorCount++;
                
                for (FrequencyDomain freeDomain : getFreeFrequencyDomains().values()) {
                    if (freeDomain.getFilter().check(node)) {
                        freeDomain.setSectorCount(freeDomain.getSectorCount() + 1);
                        break;
                    }
                }
            }
            else if (nodeType.equals(NodeTypes.TRX)) {
                trxCount++;
                for (FrequencyDomain freeDomain : getFreeFrequencyDomains().values()) {
                    if (freeDomain.getFilter().check(node)) {
                        freeDomain.setTrxCount(freeDomain.getTrxCount() + 1);
                        
                        for (ChannelType channel : ChannelType.values()) {
                            if (channel.getFilter().check(node)) {
                                freeDomain.getChannelCount().put(channel, freeDomain.getChannelCount().get(channel) + 1);
                                channelCount.put(channel, channelCount.get(channel) + 1);
                                break;
                            }
                        }
                        
                        break;
                    }
                }
            }
        }
    }

    /**
     * @return Returns the siteCount.
     */
    public int getSiteCount() {
        return siteCount;
    }

    /**
     * @return Returns the sectorCount.
     */
    public int getSectorCount() {
        return sectorCount;
    }

    /**
     * @return Returns the channelCount.
     */
    public HashMap<ChannelType, Integer> getChannelCount() {
        return channelCount;
    }

    public int getTrxCount() {
        return trxCount;
    }
    
    public static Set<String> getAvailableBSIC() {
        HashSet<String> result = new HashSet<String>();
        
        for (int i = 0; i < BSIC_MAX_NUMBER; i++) {
            result.add(Integer.toString(i));
        }
        
        return result;
    }
    
    public boolean isBCCSupported(String bcc) {
        return supportedBCC.get(bcc);
    }
    
    public boolean isNCCSupported(String ncc) {
        return supportedNCC.get(ncc);
    }
    
    public void setSupportedBCC(String bcc, boolean isSupported) {
        supportedBCC.put(bcc, isSupported);
    }
    
    public void setSupportedNCC(String ncc, boolean isSupported) {
        supportedNCC.put(ncc, isSupported);
    }
}
