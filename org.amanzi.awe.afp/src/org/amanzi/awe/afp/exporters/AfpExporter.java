package org.amanzi.awe.afp.exporters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.afp.Activator;
import org.amanzi.awe.afp.PreferenceInitializer;
import org.amanzi.awe.afp.filters.AfpRowFilter;
import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.models.AfpModel.ScalingFactors;
import org.amanzi.awe.afp.models.AfpModelUtils;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.TransactionWrapper;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.INodeToNodeType;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService.NodeToNodeRelationshipTypes;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.kernel.Traversal;

/**
 * Writes the data from the neo4j database to external file
 * 
 * @author Rahul
 */

public class AfpExporter extends Job {
    private Node afpRoot;
    private Node afpDataset;

    protected static final String AMANZI_STR = ".amanzi";
    private static final String DATA_SAVER_DIR = "AfpTemp";
    public static final String PATH_SEPARATOR = File.separator;
    public static final String tmpAfpFolder = getTmpFolderPath();

    public static final int CONTROL = 0;
    public static final int CELL = 1;
    public static final int INTERFERENCE = 2;
    public static final int NEIGHBOUR = 3;
    public static final int FORBIDDEN = 4;
    public static final int EXCEPTION = 5;
    public static final int CLIQUES = 6;

    /** The Control File */
    public final String[] fileNames = {"InputControlFile.awe", "InputCellFile.awe", "InputInterferenceFile.awe",
            "InputNeighboursFile.awe", "InputForbiddenFile.awe", "InputExceptionFile.awe", "InputCliquesFile.awe"};

    public String[] domainDirPaths;

    public final String logFileName = "logfile.awe";
    public final String outputFileName = "outputFile.awe";

    private int maxTRX = -1;
    private File[] files;
    private File[][] inputFiles;
    private AfpModel model;
    private AfpFrequencyDomainModel models[];
    private boolean useTraffic[];
    int currentDomainIndex;

    public static final int NEIGH = 0;
    public static final int INTERFER = 1;
    public static final int TRIANGULATION = 2;
    public static final int SHADOWING = 3;

    public static final int CoA = 0;
    public static final int CoT = 1;
    public static final int AdA = 2;
    public static final int AdT = 3;

    public static final float CO_SITE_SCALING_FACTOR = 1;
    public static final float CO_SECTOR_SCALING_FACTOR = 1;

    // default values of the Control file
    int defaultGMaxRTperCell = 1;
    int defaultSiteSpacing = 2;
    int defaultCellSpacing = 0;
    int defaultRegNbrSpacing = 1;
    int defaultMinNbrSpacing = 0;
    int defaultSecondNbrSpacing = 1;
    int defaultRecalculateAll = 1;
    int defaultUseTraffic = 1;
    int defaultUseSONbrs = 0;
    int defaultQuality = 100;
    int defaultDecomposeInCliques = 0;
    int defaultExistCliques = 0;
    int defaultHoppingType = 0;
    int defaultUseGrouping = 0;
    int defaultNrOfGroups = 1;

    int count;
    private double minCo;
    private NodeToNodeRelationModel impact;
    public NodeToNodeRelationModel impacts[];
    private TransactionWrapper tx;
    private int globalCount;
    private IStatistic statistic;
    private float[] contributions_co;
    private float[] contributions_cot;
    private float[] contributions_adj;
    private float[] contributions_adjt;
    private Map<NodeToNodeTypes, List<String>> lists = new HashMap<NodeToNodeTypes, List<String>>();
    private Map<NodeToNodeTypes, Integer> index = new HashMap<NodeToNodeTypes, Integer>();
    private int totalIndex;
    private List<String> names = new ArrayList<String>();
    private FrequencyPlanModel fp;

    public AfpExporter(Node afpRoot, Node afpDataset, AfpModel model) {
        super("Preparing AFP scenarios");
        this.afpRoot = afpRoot;
        this.afpDataset = afpDataset;
        this.model = model;
        totalIndex=0;

        for (Entry<NodeToNodeTypes, Map<String, ScalingFactors>> entry : model.scaling.entrySet()) {
            ArrayList<String> list = new ArrayList<String>();
            list.addAll(entry.getValue().keySet());
            lists.put(entry.getKey(), list);
            index.put(entry.getKey(), totalIndex);
            names.addAll(list);
            totalIndex += list.size();
        }
        minCo = Activator.getDefault().getPreferenceStore().getDouble(PreferenceInitializer.AFP_MIN_CO);
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {
        createFiles();
        writeFilesNew(monitor);
        return Status.OK_STATUS;
    }

    private void createFiles() {
        createTmpFolder();

        models = model.getFrequencyDomainQueue().toArray(new AfpFrequencyDomainModel[0]);
        inputFiles = new File[models.length][fileNames.length];
        domainDirPaths = new String[models.length];
        impacts = new NodeToNodeRelationModel[models.length];
        useTraffic = new boolean[models.length];
        for (int i = 0; i < models.length; i++) {
            String dirName = models[i].getName();
            useTraffic[i] = true;
            try {
                File modelDir = new File(tmpAfpFolder + dirName);
                if (!modelDir.exists())
                    modelDir.mkdir();
                domainDirPaths[i] = modelDir.getAbsolutePath() + PATH_SEPARATOR;
                for (int j = 0; j < fileNames.length; j++) {
                    inputFiles[i][j] = new File(tmpAfpFolder + dirName + PATH_SEPARATOR + fileNames[j]);

                    if (inputFiles[i][j].exists()) {
                        inputFiles[i][j].delete();
                    }

                    inputFiles[i][j].createNewFile();
                }
            } catch (IOException e) {
                AweConsolePlugin.exception(e);
            }
        }

    }

    public void writeFilesNew(IProgressMonitor monitor) {
        NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
        
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        
        monitor.beginTask("Write Files", model.getTotalTRX());

        HashMap<String, String> bandFilters = new HashMap<String, String>();
        for (int i = 0; i < model.getFrequencyBands().length; i++) {
            if (model.getFrequencyBands()[i])
                if (bandFilters.get("band") == null)
                    bandFilters.put("band", model.BAND_NAMES[i]);
                else
                    bandFilters.put("band", bandFilters.get("band") + "," + model.BAND_NAMES[i]);
        }

        Iterable<Node> sectorTraverser = model.getTRXList(bandFilters);

        try {

            BufferedWriter[] cellWriters = new BufferedWriter[models.length];
            BufferedWriter[] intWriters = new BufferedWriter[models.length];
            NetworkModel networkModel = new NetworkModel(model.getDatasetNode());
            fp = networkModel.findFrequencyModel("original");
             if (fp==null){
                 //no plan
                 return;
             }
            // if (models.length>0){
            // impact= new
            // NetworkModel(networkNode).getImpactMatrix(inputFiles[0][INTERFERENCE].getName());
            // }else{
            // impact=null;
            // }
             tx = new TransactionWrapper();
             globalCount = 0;
            for (int i = 0; i < models.length; i++) {
                cellWriters[i] = new BufferedWriter(new FileWriter(inputFiles[i][CELL]), 8 * 1024);
                intWriters[i] = new BufferedWriter(new FileWriter(inputFiles[i][INTERFERENCE]), 8 * 1024);
                // TODO add time to impact name?
                impacts[i] = networkModel.getImpactMatrix("Impact_" + i + " " + inputFiles[i][INTERFERENCE].getName());
                tx.submit(new Runnable() {

                    @Override
                    public void run() {
                        impact.getRootNode().setProperty("names", names.toArray(new String[0]));
                    }
                });
            }


            for (Node sectorNode : sectorTraverser) {
                HashMap<Node, SectorValues> sectorIntValues = getSectorIntValues(sectorNode);
                Traverser trxTraverser = AfpModelUtils.getTrxTraverser(sectorNode);
                boolean analyseSy=false;
                Node gr=ns.findSYGroup(sectorNode);
                for (Node trxNode : trxTraverser) {
                    boolean syTrx = ns.getHopType(trxNode)==2&&!(Boolean)trxNode.getProperty("bcch",false);
                    if (syTrx&&analyseSy){
                        continue;
                    }
                    analyseSy=analyseSy||syTrx;
                    if (syTrx&&gr==null){
                        continue;
                    }
                    count++;
                    monitor.worked(1);
                    if (count % 100 == 0)
                        AweConsolePlugin.info(count + " trxs processed");
                    boolean filtered = false;
                    for (int i = 0; i < models.length; i++) {
                        impact=impacts[i];
                        if (filtered) {
                            break;
                        }
                        AfpFrequencyDomainModel mod = models[i];
                        String filterString = mod.getFilters();
                        if (filterString != null && !filterString.trim().isEmpty()) {
                            AfpRowFilter rf = AfpRowFilter.getFilter(mod.getFilters());
                            if (rf != null) {
                                if (rf.equal(trxNode)) {
                                    filtered = true;
                                    currentDomainIndex = i;
                                    Node plan=fp.findPlanNode(trxNode);
                                    if (plan==null){
                                        continue;
                                    }
                                    int[] frequencies;
                                    if (syTrx){
                                        frequencies = (int[])plan.getProperty(INeoConstants.PROPERTY_MAL, new int[0]); 
                                        if (frequencies.length==0){
                                            continue;
                                        }
                                    }else{
                                        frequencies=new int[1];
                                        frequencies[0]=(Integer)plan.getProperty(INeoConstants.PROPERTY_SECTOR_ARFCN,0);
                                        //TODO if null?
                                    }
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(Long.toString(syTrx ? gr.getId() : trxNode.getId()));
                                    sb.append(" ");
                                    sb.append(1);
                                    sb.append(" ");
                                    sb.append(1);// non-relevant
                                    sb.append(" ");
                                    sb.append(frequencies.length);// required
                                    for (int id = 0; id < frequencies.length; id++) {
                                        sb.append(" ");
                                        sb.append(0);
                                    }
                                    sb.append("\n");
                                    cellWriters[i].write(sb.toString());


                                    statistic = StatisticManager.getStatistic(model.getDatasetNode());
                                    float kf1=1f;
                                    if (syTrx){
                                        kf1=(float)ns.getTrxOfSyGroup(gr)/frequencies.length;
                                    }
                                    writeInterferenceForTrx(kf1,sectorNode, syTrx,syTrx?gr:trxNode, intWriters[i], sectorIntValues, rf);

                                }
                            }
                        }
                    }

                }

            }
            tx.stop(true);
            // close the writers and create control files
            for (int i = 0; i < models.length; i++) {
                cellWriters[i].close();
                intWriters[i].close();
                createControlFile(i);
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void writeInterferenceForTrx(float kf1,Node sector, boolean isSyTrx, final Node trx, BufferedWriter intWriter,
            HashMap<Node, SectorValues> sectorIntValues, AfpRowFilter rf) throws IOException {
        NetworkService ns=NeoServiceFactory.getInstance().getNetworkService();
        DecimalFormat df = new DecimalFormat("0.0000000000");
        DecimalFormatSymbols dfSymbols = df.getDecimalFormatSymbols();
        dfSymbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfSymbols);
        StringBuilder trxSb = new StringBuilder();
        trxSb.append("SUBCELL 0 0 1 1 ");
        int numberofinterferers = 0;
        StringBuilder sbAllInt = new StringBuilder();
        for (Node intSector : sectorIntValues.keySet()) {
            float kf2=1f;
            boolean analyseSy=false;
            Node gr=ns.findSYGroup(intSector);
            for (Relationship rel: intSector.getRelationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING)){
                final Node intTrx =rel.getOtherNode(intSector);
                boolean syTrx = ns.getHopType(intTrx)==2&&!(Boolean)intTrx.getProperty("bcch",false);
                if (syTrx&&analyseSy){
                    continue;
                }
                if (syTrx){
                    Node plan=fp.findPlanNode(gr);
                    if (plan==null){
                        continue;
                    }
                    int []arr=(int[])plan.getProperty(INeoConstants.PROPERTY_MAL,new int[0]);
                    if (arr.length==0){
                        continue;
                    }
                    kf2=(float)ns.getTrxOfSyGroup(gr)/arr.length;
                }
                analyseSy=analyseSy||syTrx;
                // TODO debug (2nd part of old condition do not necessary...)
                // String trxId = (String)trx.getProperty(INeoConstants.PROPERTY_NAME_NAME, "0");
                // if (sector.equals(intSector) &&
                // trxId.equals((String)trx.getProperty(INeoConstants.PROPERTY_NAME_NAME, "0")))
                if (syTrx&&gr==null){
                    continue;
                }
                if (syTrx&&gr.equals(trx)){
                    continue;
                }
                if (trx.equals(intTrx))
                    continue;
                if (rf != null) {
                    if (!(rf.equal(intTrx))) {
                        continue;
                    }
                }

                // char c = trxId.charAt(0);
                // if (Character.isDigit(c)){
                // c = (char)((c- '1') + 'A');
                // }
                StringBuilder sbSubCell = new StringBuilder();
                sbSubCell.append("INT 0\t0\t");
                // String[] values = sectorIntValues.get(intSector)[1];
                contributions_co = new float[totalIndex + 2];
                contributions_cot = new float[totalIndex + 2];
                contributions_adj = new float[totalIndex + 2];
                contributions_adjt = new float[totalIndex + 2];

                final float[] trxValues = calculateInterference(kf1,isSyTrx,trx,kf2,syTrx,syTrx?gr: intTrx, sectorIntValues.get(intSector));
                boolean includeSubcell = false;
                for (int i = 0; i < trxValues.length; i++) {
                    if (trxValues[i] > 0.002)
                        includeSubcell = true;
                    sbSubCell.append(df.format(trxValues[i]) + " ");
                }
                float co=useTraffic[currentDomainIndex]?trxValues[CoT]:trxValues[CoA];
                if (co < minCo) {
                    includeSubcell = false;
                }
                sbSubCell.append(intTrx.getId());
                sbSubCell.append("A");
                if (includeSubcell) {
                    tx.submit(new Runnable() {

                        @Override
                        public void run() {
                            Relationship relation = impact.getRelation(trx, intTrx);
                            float co;
                            float ad;
                            float[]arr;
                            float[]arr2;
                            if (useTraffic[currentDomainIndex]){
                                co=trxValues[CoT];
                                ad=trxValues[AdT];
                                arr= contributions_cot;
                                arr2= contributions_adjt;
                            }else{
                                co=trxValues[CoA];
                                ad=trxValues[AdA];
                                arr= contributions_co;
                                arr2= contributions_adj;
                            }
                            relation.setProperty("co", co);
                            relation.setProperty("adj",ad);
                            relation.setProperty("contributions_co",arr);
                            relation.setProperty("contributions_adj", arr2);                                
                            statistic.indexValue(impact.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), "co", co);
                            statistic.indexValue(impact.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), "adj", ad);
                            statistic.indexValue(impact.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), "contributions_adj",
                                    arr);
                            statistic.indexValue(impact.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), "contributions_co",
                                    arr2);
                        }
                    });
                    globalCount += 4;
                    if (globalCount >= 1000) {
                        globalCount = 0;
                        tx.commit();
                    }
                    sbAllInt.append(sbSubCell);
                    sbAllInt.append("\n");
                }
                numberofinterferers++;
            }
        }
        tx.submit(new Runnable() {

            @Override
            public void run() {
                statistic.updateTypeCount(impact.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), impact.getRelationCount());
                statistic.setTypeCount(impact.getName(), NodeTypes.PROXY.getId(), impact.getProxyCount());
                statistic.save();
            }
        });
        globalCount = 0;
        tx.commit();
        trxSb.append(numberofinterferers);
        trxSb.append(" ");
        trxSb.append(trx.getId());
        trxSb.append("A");
        trxSb.append("\n");
        if (numberofinterferers > 0) {
            intWriter.write(trxSb.toString());
            intWriter.write(sbAllInt.toString());
        }

    }

    private float[] calculateInterference(float kf1, boolean isSyTrx1, Node trx1, float kf2, boolean isSyTrx2, Node trx2, SectorValues sectorValues) {
        NetworkService ns=NeoServiceFactory.getInstance().getNetworkService();
        Node sector1 =isSyTrx1?ns.findSectorOfSyGroup(trx1):ns.findSectorOfTRX(trx1);
        Node sector2 =isSyTrx2?ns.findSectorOfSyGroup(trx2):ns.findSectorOfTRX(trx2);
        Node site1 = sector1.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
        Node site2 = sector2.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
        float[] calculatedValues = new float[4];
        int index = 0;
        boolean isBCCH1 = (Boolean)trx1.getProperty(INeoConstants.PROPERTY_BCCH_NAME, false);
        boolean isBCCH2 = (Boolean)trx2.getProperty(INeoConstants.PROPERTY_BCCH_NAME, false);
        if (isBCCH1){
            index=isBCCH2?AfpModel.BCCHBCCH:isSyTrx2?AfpModel.BCCHSFH:AfpModel.BCCHNHBB;
        }else if (isSyTrx1){
            index=isBCCH2?AfpModel.SFHBCCH:isSyTrx2?AfpModel.SFHSFH:AfpModel.SFHNHBB;
        }else{
            index=isBCCH2?AfpModel.NHBBBCCH:isSyTrx2?AfpModel.NHBBSFH:AfpModel.NHBBNHBB;
        }


        for (int j = 0; j < 4; j++) {
            float[] arr=j==CoA?contributions_co:j==CoT?contributions_cot:j==AdA?contributions_adj:contributions_adjt;
            // CoA

            float val = 0;
            for (Entry<NodeToNodeTypes, Map<String, List<String[]>>> entry : sectorValues.map.entrySet()) {
                int id = -1;
                for (Entry<String, List<String[]>> entryList : entry.getValue().entrySet()) {
                    id = this.index.get(entry.getKey()) + lists.get(entry.getKey()).indexOf(entryList.getKey());
                    ScalingFactors sf = model.findScalingFactor(entry.getKey(), entryList.getKey());
                    for (String[] values : entryList.getValue()) {
                        try {
                            val = Float.parseFloat(values[j]);
                        } catch (Exception e) {
                            val = 0;
                        }
                        if (j == CoT || j == AdT) {
                            if (!useTraffic[currentDomainIndex])
                                val = 0;
                            else if (val < 0) {
                                useTraffic[currentDomainIndex] = false;
                                val = 0;
                            }
                        }
                        float scalingFactor = 0;

                        if (j == CoA || j == CoT) {
                            scalingFactor = sf.getCo()[index] / 100;
                        } else if (j == AdA || j == AdT) {
                            scalingFactor = sf.getAdj()[index] / 100;
                        }
                        arr[id]+=val * scalingFactor*kf1*kf2;
                        calculatedValues[j] += val * scalingFactor*kf1*kf2;
                    }

                }
            }

            // co-site
            if (j == CoA || j == CoT) {
                if (site1.equals(site2)) {
                    arr[totalIndex] += CO_SITE_SCALING_FACTOR * model.siteSeparation[index] / 100;
                    calculatedValues[j] += CO_SITE_SCALING_FACTOR * model.siteSeparation[index] / 100;
                }
                if (sector1.equals(sector2)) {
                    arr[totalIndex+1] += CO_SECTOR_SCALING_FACTOR * model.sectorSeparation[index] / 100;
                    calculatedValues[j] += CO_SECTOR_SCALING_FACTOR * model.sectorSeparation[index] / 100;
                }
            }
        }

        return calculatedValues;

    }

    public HashMap<Node, String[][]> getSectorInterferenceValues(Node sector) {

        DecimalFormat df = new DecimalFormat("0.0000000000");

        // values in 2-D array for each interfering node
        // array[neighbourArray, intArray, TriArray, shadowArray]
        // neighbourArray[CoA, AdjA, CoT, AdjT]
        HashMap<Node, String[][]> intValues = new HashMap<Node, String[][]>();

        // Add this sector to calculate co-sector TRXs
        String[][] coSectorTrxValues = new String[][] {
                {Float.toString(1), Float.toString(1), Float.toString(1), Float.toString(1)}, {}, {}, {}};
        intValues.put(sector, coSectorTrxValues);

        for (Node proxySector : sector.traverse(
                Order.DEPTH_FIRST,
                StopEvaluator.DEPTH_ONE,
                new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition pos) {
                        if (pos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME)
                                .equals(NodeTypes.SECTOR_SECTOR_RELATIONS.getId())
                                || pos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.PROXY.getId()))
                            return true;

                        return false;
                    }
                }, NetworkRelationshipTypes.INTERFERENCE, Direction.OUTGOING, NetworkRelationshipTypes.NEIGHBOURS,
                Direction.OUTGOING,
                DatasetRelationshipTypes.PROXY, Direction.OUTGOING)) {
            for (Relationship relation : proxySector.getRelationships(NetworkRelationshipTypes.INTERFERS,
                    NetworkRelationshipTypes.NEIGHBOUR, NodeToNodeRelationshipTypes.PROXYS)) {
                if (relation.getEndNode().equals(proxySector))
                    continue;
                Node intProxySector = relation.getEndNode();

                Relationship relationship = null;
                relationship = intProxySector.getSingleRelationship(DatasetRelationshipTypes.PROXY, Direction.INCOMING);
                if (relationship == null) {
                    relationship = intProxySector.getSingleRelationship(NetworkRelationshipTypes.INTERFERENCE, Direction.INCOMING);
                }
                if (relationship == null) {
                    relationship = intProxySector.getSingleRelationship(NetworkRelationshipTypes.NEIGHBOURS, Direction.INCOMING);
                }
                if (relationship == null) {
                    continue;
                }
                Node intSector = null;
                intSector = relationship.getStartNode();
                RelationshipType type = relation.getType();
                boolean isProxy = false;
                int typeIndex = NEIGH;
                if (type.equals(NodeToNodeRelationshipTypes.PROXYS)) {
                    isProxy = true;
                    Node fileNode = intProxySector.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING)
                            .getStartNode();
                    if (fileNode.getProperty("node2node", "").equals(NodeToNodeTypes.NEIGHBOURS))
                        typeIndex = NEIGH;
                    else if (fileNode.getProperty("node2node", "").equals(NodeToNodeTypes.INTERFERENCE_MATRIX))
                        typeIndex = INTERFER;
                    else if (fileNode.getProperty("node2node", "").equals(NodeToNodeTypes.TRIANGULATION))
                        typeIndex = TRIANGULATION;
                    else if (fileNode.getProperty("node2node", "").equals(NodeToNodeTypes.SHADOWING))
                        typeIndex = SHADOWING;
                } else if (type.equals(NetworkRelationshipTypes.NEIGHBOUR))
                    typeIndex = NEIGH;
                else if (type.equals(NetworkRelationshipTypes.INTERFERS))
                    typeIndex = INTERFER;

                String[][] prevValue = new String[4][4];
                if (intValues.containsKey(intSector))
                    prevValue = intValues.get(intSector);

                String[] value = new String[4];

                if (isProxy) {
                    String traffic = null;
                    try {
                        traffic = df.format(intSector.getProperty("traffic", -1)).toString();
                    } catch (Exception e) {
                        traffic = (String)sector.getProperty("traffic", -1);
                    }
                    try {
                        value[CoA] = df.format(relation.getProperty("co", "0")).toString();
                    } catch (Exception e) {
                        value[CoA] = (String)relation.getProperty("co", "0");
                    }

                    try {
                        value[AdA] = df.format(relation.getProperty("adj", "0")).toString();
                    } catch (Exception e) {
                        value[AdA] = (String)relation.getProperty("adj", "0");
                    }

                    value[CoT] = traffic;
                    value[AdT] = traffic;

                } else if (typeIndex == INTERFER) {
                    try {
                        value[CoA] = df.format(relation.getProperty("CoA", "0")).toString();
                    } catch (Exception e) {
                        value[CoA] = (String)relation.getProperty("CoA", "0");
                    }

                    try {
                        value[AdA] = df.format(relation.getProperty("AdA", "0")).toString();
                    } catch (Exception e) {
                        value[AdA] = (String)relation.getProperty("AdA", "0");
                    }

                    try {
                        value[CoT] = df.format(relation.getProperty("CoT", "0")).toString();
                    } catch (Exception e) {
                        value[CoT] = (String)relation.getProperty("CoT", "0");
                    }

                    try {
                        value[AdT] = df.format(relation.getProperty("AdT", "0")).toString();
                    } catch (Exception e) {
                        value[AdT] = (String)relation.getProperty("AdT", "0");
                    }
                }// end if
                else if (typeIndex == NEIGH) {
                    value[CoA] = Double.toString(0.5);
                    value[AdA] = Double.toString(0.05);
                    value[CoT] = Double.toString(0.5);
                    value[AdT] = Double.toString(0.05);
                }

                prevValue[typeIndex] = value;

                intValues.put(intSector, prevValue);

            }
        }

        return intValues;

    }

    public HashMap<Node, SectorValues> getSectorIntValues(Node sector) {

        DecimalFormat df = new DecimalFormat("0.0000000000");
        DecimalFormatSymbols dfSymbols = df.getDecimalFormatSymbols();
        dfSymbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfSymbols);

        // values in 2-D array for each interfering node
        // array[neighbourArray, intArray, TriArray, shadowArray]
        // neighbourArray[CoA, AdjA, CoT, AdjT]
        HashMap<Node, SectorValues> intValues = new HashMap<Node, SectorValues>();

        // Add this sector to calculate co-sector TRXs
        // as I understand - not necessary
        // String[][] coSectorTrxValues = new String[][] {
        // {Float.toString(1), Float.toString(1), Float.toString(1), Float.toString(1)}, {}, {},
        // {}};
        // intValues.put(sector, coSectorTrxValues);

        for (Node proxySector : Traversal.description().depthFirst()
                .relationships(DatasetRelationshipTypes.PROXY, Direction.OUTGOING).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {

                        boolean includes = arg0.length() == 1;
                        return Evaluation.of(includes, arg0.length() == 0);
                    }
                }).traverse(sector).nodes()) {

            Node proxyRoot = proxySector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(
                    proxySector);
            NodeToNodeRelationModel prModel = new NodeToNodeRelationModel(proxyRoot);
            final INodeToNodeType proxytype = prModel.getType();
            final String prName = prModel.getName();
            if (proxytype != NodeToNodeTypes.INTERFERENCE_MATRIX && prModel.getType() != NodeToNodeTypes.SHADOWING
                    && prModel.getType() != NodeToNodeTypes.TRIANGULATION && prModel.getType() != NodeToNodeTypes.NEIGHBOURS) {
                continue;
            }

            for (Relationship relation : proxySector.getRelationships(NodeToNodeRelationshipTypes.PROXYS, Direction.OUTGOING)) {
                Node intProxySector = relation.getOtherNode(proxySector);

                Relationship relationship = null;
                relationship = intProxySector.getSingleRelationship(DatasetRelationshipTypes.PROXY, Direction.INCOMING);
                if (relationship == null) {
                    continue;
                }
                Node intSector = null;
                intSector = relationship.getStartNode();
                String[][] prevValue = new String[4][4];
                SectorValues data = intValues.get(intSector);
                if (data == null) {
                    data = new SectorValues();
                    intValues.put(intSector, data);
                }

                String[] value = new String[4];
                String traffic = null;
                try {
                    traffic = df.format(intSector.getProperty("traffic", -1)).toString();
                } catch (Exception e) {
                    traffic = (String)sector.getProperty("traffic", -1);
                }
                String propName;
                if (relation.hasProperty("CoA")) {
                    propName = "CoA";
                } else {
                    propName = "co";
                }
                try {
                    value[CoA] = df.format(relation.getProperty(propName, "0")).toString();
                } catch (Exception e) {
                    value[CoA] = (String)relation.getProperty(propName, "0");
                }
                if (relation.hasProperty("AdA")) {
                    propName = "AdA";
                } else {
                    propName = "adj";
                }
                try {
                    value[AdA] = df.format(relation.getProperty(propName, "0")).toString();
                } catch (Exception e) {
                    value[AdA] = (String)relation.getProperty(propName, "0");
                }
                if (relation.hasProperty("CoT")) {
                    try {
                        value[CoT] = df.format(relation.getProperty("CoT", "0")).toString();
                    } catch (Exception e) {
                        value[CoT] = (String)relation.getProperty("CoT", "0");
                    }
                } else {
                    value[CoT] = traffic;
                }

                if (relation.hasProperty("AdT")) {
                    try {
                        value[AdT] = df.format(relation.getProperty("AdT", "0")).toString();
                    } catch (Exception e) {
                        value[AdT] = (String)relation.getProperty("AdT", "0");
                    }
                } else {
                    value[AdT] = traffic;
                }

                data.addValues((NodeToNodeTypes)proxytype, prName, value);

                if (proxytype.equals(NodeToNodeTypes.NEIGHBOURS)) {
                    if (!intValues.containsKey(sector)) {
                        SectorValues sameSector = new SectorValues();
                        intValues.put(sector, sameSector);

                        sameSector.addValues(NodeToNodeTypes.NEIGHBOURS, prName, new String[] {"0.0", "0.0", "0.0", "0.0"});
                    }
                }
            }
        }

        return intValues;

    }

    /**
     * Creates the Control file to be given as input to the C++ engine
     */
    public void createControlFile(int domainIndex) {
        if (maxTRX < 0) {
            maxTRX = defaultGMaxRTperCell;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(inputFiles[domainIndex][CONTROL]));

            // LN, 23.03.2011, switching format of control file to new version
            writer.write("FileWithAllCells " + "\"" + this.inputFiles[domainIndex][CELL].getAbsolutePath() + "\"");
            writer.newLine();

            writer.write("DistanceSpacingConditionForSites " + defaultSiteSpacing);
            writer.newLine();

            writer.write("DistanceSpacingConditionForCells " + defaultCellSpacing);
            writer.newLine();

            writer.write("MaximumRTDemandperCell " + maxTRX);
            writer.newLine();

            writer.write("DistanceSpacingConditionRequiredForNeigbours " + defaultRegNbrSpacing);
            writer.newLine();

            writer.write("DistanceSpacingConditionForSecondOrderNeighbours " + defaultSecondNbrSpacing);
            writer.newLine();

            writer.write("NeighboursConditionFile " + "\"" + this.inputFiles[domainIndex][NEIGHBOUR].getAbsolutePath() + "\"");
            writer.newLine();

            writer.write("TrafficOrAreaInterference " + (useTraffic[domainIndex] ? "1" : "2"));
            writer.newLine();

            writer.write("IndicatorForSecondOrderNeighboursUsage " + defaultUseSONbrs);
            writer.newLine();

            writer.write("Quality " + defaultQuality);
            writer.newLine();

            writer.write("InterferenceMatrixFile " + "\"" + this.inputFiles[domainIndex][INTERFERENCE].getAbsolutePath() + "\"");
            writer.newLine();

            writer.write("PlanFile " + "\"" + this.domainDirPaths[domainIndex] + this.outputFileName + "\"");
            writer.newLine();

            writer.write("SetOfCarriers " + parseCarriers(getFrequencies(domainIndex)));
            writer.newLine();

            writer.close();
        } catch (Exception e) {
            AweConsolePlugin.exception(e);
        }
    }

    private String getFrequencies(int domainIndex) {
        StringBuffer carriers = new StringBuffer();
        int cnt = 0;
        boolean first = true;
        String[] franges = models[domainIndex].getFrequencies();

        String[] freqList = AfpModel.rangeArraytoArray(franges);
        for (String f : freqList) {
            if (!first) {
                carriers.append(",");
            }
            carriers.append(f);
            cnt++;
            first = false;
        }
        return carriers.toString();
    }

    private String parseCarriers(String commaSeparated) {
        int numCarriers = commaSeparated.split("\\,").length;
        String spaceSeparated = commaSeparated.replaceAll(",", " ");
        spaceSeparated = numCarriers + " " + spaceSeparated;

        return spaceSeparated;
    }

    /**
     * Gets the site name and sector no of the sector
     * 
     * @param sector the sector node
     * @return string array containg site name and sector no
     */
    public String[] parseSectorName(Node sector) {
        Node site = sector.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
        String siteName = site.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        String sectorValues[] = new String[2];
        sectorValues[0] = siteName;
        String sectorName = sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        if (sectorName.length() > siteName.length() && sectorName.substring(0, siteName.length()).equals(siteName)) {
            sectorValues[1] = sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().substring(siteName.length());
        } else
            sectorValues[1] = sectorName;

        char sectorNo = sectorValues[1].charAt(sectorValues[1].length() - 1);
        if (Character.isLetter(sectorNo))
            sectorValues[1] = // sectorValues[1].substring(0, sectorValues[1].length() - 1) +
            Integer.toString(Character.getNumericValue(sectorNo) - Character.getNumericValue('A') + 1);

        return sectorValues;
    }

    public String getSectorNameForInterList(Node sector) {
        Node site = sector.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
        String siteName = site.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();

        String sectorName = sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        if (sectorName.length() > siteName.length() && sectorName.substring(0, siteName.length()).equals(siteName)) {
            sectorName = sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().substring(siteName.length());
        }
        char sectorNo = sectorName.charAt(sectorName.length() - 1);
        if (Character.isDigit(sectorNo))
            sectorName = siteName + sectorNo;
        else
            sectorName = siteName + (Character.getNumericValue(sectorNo) - Character.getNumericValue('A') + 1);

        return sectorName;
    }

    public String[] getAllTrxNames(Node sector) {
        ArrayList<String> names = new ArrayList<String>();
        for (Node trx : sector.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition pos) {
                if (pos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.TRX.getId()))
                    return true;

                return false;
            }
        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
            String name = (String)trx.getProperty(INeoConstants.PROPERTY_NAME_NAME, "");
            if (Character.isDigit(name.charAt(0))) {
                // name = Integer.toString(Character.getNumericValue(name.charAt(0)) -
                // Character.getNumericValue('A')+ 1);
                name = Character.toString((char)(name.charAt(0) + 'A' - '1'));
                // name = Integer.toString();
            }
            names.add(name);

        }
        return names.toArray(new String[0]);
    }

    private void createTmpFolder() {
        File file = new File(this.tmpAfpFolder);
        if (!file.exists())
            file.mkdir();
    }

    public static String getTmpFolderPath() {

        File dir = new File(System.getProperty("user.home"));
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(dir, AMANZI_STR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(dir, DATA_SAVER_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir.getPath() + PATH_SEPARATOR;
    }

    public static class SectorValues {

        Map<NodeToNodeTypes, Map<String, List<String[]>>> map = new HashMap<NodeToNodeTypes, Map<String, List<String[]>>>();

        public void addValues(NodeToNodeTypes proxytype, String listName, String[] value) {
            Map<String, List<String[]>> maps = map.get(proxytype);
            if (maps == null) {
                maps = new HashMap<String, List<String[]>>();
                map.put(proxytype, maps);
            }
            List<String[]> list = maps.get(listName);
            if (list == null) {
                list = new ArrayList<String[]>();
                maps.put(listName, list);
            }
            list.add(value);

        }

    }
}
