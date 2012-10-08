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

package org.amanzi.awe.distribution.model.type;

import java.util.Set;

import org.amanzi.neo.nodetypes.INodeType;

/**
 * Type of Distribution
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public interface IDistributionType<T extends IRange> {

    /**
     * Type of Chart
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public static enum ChartType {
        /*
         * Chart shows Counts of property for each bar
         */
        COUNTS("Counts"),

        /*
         * Chart shows Percents of count of property for each bar
         */
        PERCENTS("Percents"),

        /*
         * Chart show Logarithmic count of property for each bar
         */
        LOGARITHMIC("Logarithmic counts"),

        /*
         * Chart show CDF of this property
         */
        CDF("CDF Chart");

        /*
         * title of this type of chart
         */
        private final String title;

        /**
         * Constructor
         * 
         * @param title
         */
        private ChartType(final String title) {
            this.title = title;
        }

        /**
         * Returns title of this type of Chart
         * 
         * @return
         */
        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return getTitle();
        }

        /**
         * Searches corresponding ChartType enum by it's title
         * 
         * @param title
         * @return
         */
        public static ChartType findByTitle(final String title) {
            for (final ChartType singleType : values()) {
                if (singleType.title.equals(title)) {
                    return singleType;
                }
            }

            return null;
        }

        /**
         * Returns Default ChartType Default ChartType is COUNTS
         * 
         * @return
         */
        public static ChartType getDefault() {
            return COUNTS;
        }
    }

    /**
     * Returns name of this Distribution
     * 
     * @return
     */
    public String getName();

    /**
     * Returns list of Ranges of this Distribution
     * 
     * @return
     */
    public Set<T> getRanges();

    /**
     * Type of Node to Analyze
     * 
     * @return
     */
    public INodeType getNodeType();

    /**
     * Returns name of Analyzed Property
     * 
     * @return
     */
    public String getPropertyName();

}
