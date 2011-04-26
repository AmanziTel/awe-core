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

import java.text.DecimalFormat;
import java.text.Format;

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
    private Format format=new DecimalFormat();
    public TemplateColumn(String name){
        this.name=name;
    }
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
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @param header The header to set.
     */
    public void setHeader(IStatisticsHeader header) {
        this.header = header;
    }
    
    /**
     * @param function The function to set.
     */
    public void setFunction(String name) {
        this.function = AggregationFunctions.getFunctionByName(name);
    }
    /**
     * @param threshold The threshold to set.
     */
    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }
    
    /**
     * @return Returns the format.
     */
    public Format getFormat() {
        return format;
    }
    /**
     * @param format The format to set.
     */
    public void setFormat(Format format) {
        this.format = format;
    }
    @Override
    public String toString() {
        return String.format("\nTemplateColumn[%s, %s, %s, %s]",name,header,function,threshold);
    }

}
