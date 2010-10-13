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

package org.amanzi.awe.statistics.template;

import org.amanzi.awe.statistics.engine.IStatisticsHeader;
import org.amanzi.awe.statistics.functions.AggregationFunctions;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class TemplateColumn {
    private String name;
    private IStatisticsHeader header;
    private AggregationFunctions function;
    private Threshold threshold;

    /**
     * @param header
     * @param function
     * @param name TODO
     */
    public TemplateColumn(IStatisticsHeader header, AggregationFunctions function, String name) {
        this.header = header;
        this.function = function;
        this.name=name;
    }

    /**
     * @param header
     * @param function
     * @param threshold
     * @param name TODO
     */
    public TemplateColumn(IStatisticsHeader header, AggregationFunctions function, Threshold threshold, String name) {
        this.header = header;
        this.function = function;
        this.threshold = threshold;
        this.name=name;
    }

    /**
     * @return Returns the header.
     */
    public IStatisticsHeader getHeader() {
        return header;
    }

    /**
     * @return Returns the function.
     */
    public AggregationFunctions getFunction() {
        return function;
    }

    /**
     * @return Returns the threshold.
     */
    public Threshold getThreshold() {
        return threshold;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}
