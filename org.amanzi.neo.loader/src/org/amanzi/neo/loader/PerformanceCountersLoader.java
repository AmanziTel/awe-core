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

package org.amanzi.neo.loader;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.enums.OssType;
import org.amanzi.neo.services.enums.SectorIdentificationType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.Pair;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class PerformanceCountersLoader extends AbstractLoader {
    private static boolean needParceHeader;

    private LuceneIndexService luceneService;
    private String luceneIndexName;
    private final ArrayList<String> possibleCellNames = new ArrayList<String>();

    private Node ossRoot;
    private Node fileNode;
    private Node lastChild;

    private final OssType ossType;

    private SimpleDateFormat dateFormat;

    private SimpleDateFormat timeFormat;

    public PerformanceCountersLoader(OssType ossType, String directory, String datasetName, Display display) {
        initialize("Performance Counter", null, directory, display);
        basename = datasetName;
        this.ossType = ossType;
        needParceHeader = true;
        getHeaderMap(1);
        init();
    }

    private void init() {
        final String SITE = "site";
        possibleCellNames.add(SITE);
        addKnownHeader(1, SITE, getPossibleHeaders(DataLoadPreferences.NH_SITE), true);
        useMapper(1, SITE, new StringMapper());

        final String CELL = "sector";
        possibleCellNames.add(CELL);
        addKnownHeader(1, CELL, getPossibleHeaders(DataLoadPreferences.NH_SECTOR), true);
        useMapper(1, CELL, new StringMapper());

//        addKnownHeader(1, "Date", "date", false);
        dateFormat = new SimpleDateFormat("MM.dd.yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
//        addKnownHeader(1, "time", ".*time.*", false);
//        useMapper(1, "time", new LongDateMapper("HH:mm:ss"));

        try {
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(basename));
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    protected class LongDateMapper implements PropertyMapper {
        private SimpleDateFormat format;

        protected LongDateMapper(String format) {
            try {
                this.format = new SimpleDateFormat(format);
            } catch (Exception e) {
                this.format = new SimpleDateFormat("HH:mm:ss");
            }
        }

        @Override
        public Object mapValue(String time) {
            long timeStamp;
            try {
                timeStamp = format.parse(time).getTime();
            } catch (ParseException e) {
                error(e.getLocalizedMessage());
                return 0L;
            }
            return timeStamp;
        }
    }

    @Override
    protected boolean needParceHeaders() {
        if (needParceHeader) {
            needParceHeader = false;
            return true;
        }
        return false;
    }

    @Override
    protected int parseLine(String line) {
        if (fileNode == null) {
            ossRoot = LoaderUiUtils.findOrCreateOSSNode(ossType, basename, neo);// TODO target
            // type
            Pair<Boolean, Node> fileNodePair = NeoUtils.findOrCreateFileNode(neo, ossRoot, new File(basename).getName(), new File(
                    basename).getName());
            fileNode = fileNodePair.getRight();
            lastChild = null;

            initializeLucene(ossRoot);
            Transaction transaction = neo.beginTx();
            try {
                initializeIndexes();
            } finally {
                transaction.finish();
            }
        }
        List<String> fields = splitLine(line);
        if (fields.size() < 2)
            return 0;
        if (this.isOverLimit())
            return 0;
        Map<String, Object> lineData = makeDataMap(fields);
        return saveStatistic(lineData);
    }

    private int saveStatistic(Map<String, Object> lineData) {
        Transaction transaction = neo.beginTx();
        try {
            Node node = neo.createNode();
            NodeTypes.M.setNodeType(node, neo);
            long timestamp;
            for (Map.Entry<String, Object> entry : lineData.entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue());
                if ("date".equalsIgnoreCase(entry.getKey())) {
//                    System.out.println(lineData.get("date") + " " + entry.getValue());
                    String date = (String)entry.getValue();
                    String time = (String)lineData.get("time");

                    GregorianCalendar cal0 = new GregorianCalendar();
                    long dateAsMillisecs = dateFormat.parse(date).getTime();
                    long timeAsMillisecs = time==null?0:timeFormat.parse(time).getTime();
                    cal0.setTimeInMillis(dateAsMillisecs + timeAsMillisecs);
                                        
                    timestamp = cal0.getTimeInMillis();
                    node.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
                    updateTimestampMinMax(1, timestamp);
                }
                indexData(entry, node);
            }
            node.setProperty(INeoConstants.PROPERTY_NAME_NAME, "performance counter");
            index(node);

            NeoUtils.addChild(fileNode, node, lastChild, neo);
            lastChild = node;
            storingProperties.values().iterator().next().incSaved();
            return 1;
        } catch (ParseException e) {
            // TODO Handle ParseException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } finally {
            transaction.finish();
        }
    }

    private void indexData(Map.Entry<String, Object> entry, Node curentNode) {
        if (possibleCellNames.contains(entry.getKey())) {
            luceneService.index(curentNode, luceneIndexName, entry.getValue());
        }
    }

    private void initializeLucene(Node baseNode) {
        luceneService = NeoServiceProviderUi.getProvider().getIndexService();
        luceneIndexName = NeoUtils.getLuceneIndexKeyByProperty(baseNode, INeoConstants.SECTOR_ID_PROPERTIES, NodeTypes.M);
    }

    @Override
    protected String getPrymaryType(Integer key) {
        return NodeTypes.M.getId();
    }

    @Override
    protected Node getStoringNode(Integer key) {
        return ossRoot;
    }

    @Override
    public Node[] getRootNodes() {
        return new Node[] {ossRoot};
    }

    @Override
    public void finishUp() {
        // Pechko_E what does this property mean?
        getStoringNode(1).setProperty(INeoConstants.SECTOR_ID_TYPE, SectorIdentificationType.NAME.toString());
        super.finishUp();
    }

}
