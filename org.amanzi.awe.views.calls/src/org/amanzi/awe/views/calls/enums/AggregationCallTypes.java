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

package org.amanzi.awe.views.calls.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Second level statistics headers for all call types. 
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum AggregationCallTypes {

     INDIVIDUAL(StatisticsCallType.INDIVIDUAL,null,AggregationStatisticsHeaders.SC1,
                                            AggregationStatisticsHeaders.SC2_ZW2_AVG,
                                            AggregationStatisticsHeaders.SC2_ZW2_MIN,
                                            AggregationStatisticsHeaders.SC2_ZW2_MAX,
                                            AggregationStatisticsHeaders.SC3,
                                            AggregationStatisticsHeaders.SC4,
                                            AggregationStatisticsHeaders.SC4_ZW2_AVG,
                                            AggregationStatisticsHeaders.SC4_ZW2_MIN,
                                            AggregationStatisticsHeaders.SC4_ZW2_MAX,
                                            AggregationStatisticsHeaders.SC5_ZW1_AVG,
                                            AggregationStatisticsHeaders.SC5_ZW1_MIN,
                                            AggregationStatisticsHeaders.SC5_ZW1_MAX),
     GROUP(StatisticsCallType.GROUP,null,AggregationStatisticsHeaders.GC1,
                                    AggregationStatisticsHeaders.GC2_ZW2_AVG,
                                    AggregationStatisticsHeaders.GC2_ZW2_MIN,
                                    AggregationStatisticsHeaders.GC2_ZW2_MAX,
                                    AggregationStatisticsHeaders.GC3,
                                    AggregationStatisticsHeaders.GC4,
                                    AggregationStatisticsHeaders.GC4_ZW2_AVG,
                                    AggregationStatisticsHeaders.GC4_ZW2_MIN,
                                    AggregationStatisticsHeaders.GC4_ZW2_MAX,
                                    AggregationStatisticsHeaders.GC5_ZW1_AVG,
                                    AggregationStatisticsHeaders.GC5_ZW1_MIN,
                                    AggregationStatisticsHeaders.GC5_ZW1_MAX),
     ITSI_CC(StatisticsCallType.ITSI_CC,StatisticsCallType.ITSI_HO, AggregationStatisticsHeaders.INH_CC),
     TSM(StatisticsCallType.TSM,null, AggregationStatisticsHeaders.TSM),
     SDS(StatisticsCallType.SDS,null, AggregationStatisticsHeaders.SDS),
     ITSI_ATT(StatisticsCallType.ITSI_ATTACH,null, AggregationStatisticsHeaders.INH_AT);
     //EMERGENCY(StatisticsCallType.EMERGENCY,null,AggregationStatisticsHeaders.EC1),
     //HELP(StatisticsCallType.HELP,null,AggregationStatisticsHeaders.EC2),
     //CS_DATA(StatisticsCallType.CS_DATA,null,AggregationStatisticsHeaders.CSD),
     //PS_DATA(StatisticsCallType.PS_DATA,null,AggregationStatisticsHeaders.IP);
     
     private StatisticsCallType realType;
     private StatisticsCallType additionalType;
     private List<IAggrStatisticsHeaders> aggrHeaders;
     
     /**
      * Constructor.
      * @param real StatisticsCallType (real type)
      * @param utilCount int (count of utility headers)
      * @param allHeaders IAggrStatisticsHeaders (utility and aggregation headers)
      */
     private AggregationCallTypes(StatisticsCallType real, StatisticsCallType additional, IAggrStatisticsHeaders... allHeaders) {
         realType = real;
         aggrHeaders = Arrays.asList(allHeaders);
         additionalType = additional;
     }
     
     /**
      * @return Returns the realType.
      */
     public StatisticsCallType getRealType() {
         return realType;
     }
     
     /**
     * @return Returns the additionalType.
     */
    public StatisticsCallType getAdditionalType() {
        return additionalType;
    }
     
     /**
      * @return Returns the aggrHeaders.
      */
     public List<IAggrStatisticsHeaders> getAggrHeaders() {
         return aggrHeaders;
     }
     
     /**
      * @return Returns the aggrHeaders.
      */
     public List<IAggrStatisticsHeaders> getUtilHeaders() {
         Set<IAggrStatisticsHeaders> result = new HashSet<IAggrStatisticsHeaders>();
         for(IAggrStatisticsHeaders aggrHeader : aggrHeaders){
             for(IStatisticsHeader header : aggrHeader.getDependendHeaders()){
                 result.add((IAggrStatisticsHeaders)header);
             }
         }
         return new ArrayList<IAggrStatisticsHeaders>(result);
     }
     
}
