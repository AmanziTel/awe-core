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

package org.amanzi.neo.data_generator;

import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.data_generator.generate.calls.GroupCallsGenerator;
import org.amanzi.neo.data_generator.generate.calls.IndividualCallsGenerator;
import org.amanzi.neo.data_generator.generate.nokia.NokiaTopologyGenerator;


/**
 * <p>
 * Manager for getting generators for different data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class DataGenerateManager {

    /**
     * Returns AMS data generator for individual calls.
     *
     * @param aDirectory String (path to save data)
     * @param aHours Integer (count of hours)
     * @param aHourDrift Integer (drift of start time)
     * @param aCallsPerHour Integer (call count in hour)
     * @param aCallPerHourVariance Integer (call variance in hour)
     * @param aProbes Integer (probes count)
     * @return AmsDataGenerator.
     */
    public static IDataGenerator getIndividualAmsGenerator(String aDirectory, Integer aHours, Integer aHourDrift,
            Integer aCallsPerHour, Integer aCallPerHourVariance, Integer aProbes){
        return new IndividualCallsGenerator(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }
    
    /**
     * Returns AMS data generator for group calls.
     *
     * @param aDirectory String (path to save data)
     * @param aHours Integer (count of hours)
     * @param aHourDrift Integer (drift of start time)
     * @param aCallsPerHour Integer (call count in hour)
     * @param aCallPerHourVariance Integer (call variance in hour)
     * @param aProbes Integer (probes count)
     * @return AmsDataGenerator.
     */
    public static IDataGenerator getGroupAmsGenerator(String aDirectory, Integer aHours, Integer aHourDrift,
            Integer aCallsPerHour, Integer aCallPerHourVariance, Integer aProbes, Integer aMaxGroupSize){
        return new GroupCallsGenerator(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes, aMaxGroupSize);
    }
    
    /**
     * Returns Nokia Topology data generator
     *
     * @param aPath String (path to save file)
     * @param aFileName String (file name)
     * @param bscs Integer (BSCs count) 
     * @param sites Integer (maximum sites count for one BSC)
     * @param sectors Integer (maximum sectors count for one site)
     * @param extUmtsCount Integer (external UMTS sectors count)
     * @param latBorders Float[] (must be like {min_latitude,max_latitude}) 
     * @param lonBorders Float[] (must be like {min_longitude,max_longitude})
     * @return NokiaTopologyGenerator
     */
    public static IDataGenerator getNokiaTopologyGenerator(String aPath, String aFileName, Integer bscs, Integer sites, Integer sectors, 
            Integer extUmtsCount, Float[] latBorders, Float[] lonBorders){
        return new NokiaTopologyGenerator(aPath, aFileName, bscs, sites, sectors, extUmtsCount, latBorders, lonBorders);
    }
}
