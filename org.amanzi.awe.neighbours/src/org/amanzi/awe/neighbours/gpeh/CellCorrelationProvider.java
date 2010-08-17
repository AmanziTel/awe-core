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

package org.amanzi.awe.neighbours.gpeh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * CellCorrelationProvider - provider for CellCorrelation report
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CellCorrelationProvider extends IntraMatrixProvider {
    static final Logger LOGGER = Logger.getLogger(CellCorrelationProvider.class);
    private ArrayList<org.amanzi.awe.neighbours.gpeh.CellCorrelationProvider.IntRange> ecnoRangeNames;
    private ArrayList<org.amanzi.awe.neighbours.gpeh.CellCorrelationProvider.IntRange> rscpRangeNames;
    protected SimpleDateFormat dateFormat;
    protected SimpleDateFormat dateFormat2;

    /**
     * Instantiates a new cell correlation provider.
     * 
     * @param dataset the dataset
     * @param network the network
     * @param service the service
     * @param period the period
     * @param luceneService the lucene service
     */
    public CellCorrelationProvider(Node dataset, Node network, GraphDatabaseService service, CallTimePeriods period, LuceneIndexService luceneService) {
        super(dataset, network, service, period, luceneService);
    }

    @Override
    protected void init() {
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat2 = new SimpleDateFormat("HHmm");
        super.init();
    }

    @Override
    protected void defineHandler() {

        ecnoRangeNames = new ArrayList<IntRange>();
        ecnoRangeNames.add(new IntRange("ECNO-18", 0, 12));
        ecnoRangeNames.add(new IntRange("ECNO-15", 13, 18));
        ecnoRangeNames.add(new IntRange("ECNO-12", 19, 24));
        ecnoRangeNames.add(new IntRange("ECNO-9", 25, 30));
        ecnoRangeNames.add(new IntRange("ECNO-6", 31, 36));
        ecnoRangeNames.add(new IntRange("ECNO-0", 37, 48));// TODO check for 49?
        rscpRangeNames = new ArrayList<IntRange>();
        rscpRangeNames.add(new IntRange("RSCP-105", 0, 10));
        rscpRangeNames.add(new IntRange("RSCP-100", 11, 15));
        rscpRangeNames.add(new IntRange("RSCP-95", 16, 20));
        rscpRangeNames.add(new IntRange("RSCP-90", 21, 25));
        rscpRangeNames.add(new IntRange("RSCP-80", 26, 35));
        rscpRangeNames.add(new IntRange("RSCP-70", 36, 45));
        rscpRangeNames.add(new IntRange("RSCP-25", 46, 90));// TODO check 91

        modelHandler = new CellCorrelationHandler(period, service, ecnoRangeNames, rscpRangeNames, dateFormat, dateFormat2);
    }

    @Override
    protected void defineRowIterator() {
        rowIter = new BestCellIterator(statRoot);
    }

    @Override
    protected void defineModel() {
        model = new CellCorrelationModel(modelHandler);
    }

    @Override
    public String getDataName() {
        return "Cell RSCP analysis";
    }

    @Override
    protected void createHeader() {
        headers = new LinkedList<String>();
        headers.add("Cell Name");
        headers.add("Date");
        headers.add("Time");
        headers.add("Resolution");
        for (IntRange rscpName : rscpRangeNames) {
            for (IntRange ecnoName : ecnoRangeNames) {
                headers.add(new StringBuilder(ecnoName.getName()).append("_").append(rscpName.getName()).toString());
            }
        }
    }

    /**
     * <p>
     * Contains information about 3GPP range
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static class IntRange {
        private String name;
        private int min;
        private int max;

        /**
         * @param name
         * @param min
         * @param max
         */
        public IntRange(String name, int min, int max) {
            super();
            this.name = name;
            this.min = min;
            this.max = max;
        }

        /**
         * Gets the name.
         * 
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name.
         * 
         * @param name the new name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the min.
         * 
         * @return the min
         */
        public int getMin() {
            return min;
        }

        /**
         * Sets the min.
         * 
         * @param min the new min
         */
        public void setMin(int min) {
            this.min = min;
        }

        /**
         * Gets the max.
         * 
         * @return the max
         */
        public int getMax() {
            return max;
        }

        /**
         * Sets the max.
         * 
         * @param max the new max
         */
        public void setMax(int max) {
            this.max = max;
        }

    }

}
