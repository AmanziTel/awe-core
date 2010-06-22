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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.views.calls.Messages;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.awe.views.calls.statistics.CallStatisticsInconclusive;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * States for build statistics with inconclusive calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum InclInconclusiveStates {
    /**
     * Exclude all inconclusive calls.
     */
    EXCLUDE(Messages.CAV_SEL_EXCLUDE) {
        @Override
        public CallStatistics getStatistics(Node drive, GraphDatabaseService service, IProgressMonitor monitor) throws IOException {
            if (monitor!=null) {
                return new CallStatistics(drive, service, monitor);
            }
            return new CallStatistics(drive, service);
        }
    },
    /**
     * Include inconclusive calls into attempts statistics.
     */
    INCLUDE_ATTEMPTS(Messages.CAV_SEL_INCLUDE_ATTEMPT),
    /**
     * Include inconclusive calls with NTP event.
     */
    INCLUDE_NTP(Messages.CAV_SEL_INCLUDE_NTP),
    /**
     * Include all inconclusive.
     */
    INCLUDE_ALL(Messages.CAV_SEL_INCLUDE_ALL);
    
    private String id;
    
    private InclInconclusiveStates(String name) {
        id = name;
    }
    
    /**
     * Get statistics by state.
     *
     * @param drive Node
     * @param service GraphDatabaseService
     * @param monitor IProgressMonitor
     * @return CallStatistics.
     * @throws IOException
     */
    public CallStatistics getStatistics(Node drive, GraphDatabaseService service, IProgressMonitor monitor) throws IOException{
        if (monitor!=null) {
            return new CallStatisticsInconclusive(drive, service, monitor,this);
        }
        return new CallStatisticsInconclusive(drive, service,this);
    }
    
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get state by id.
     *
     * @param key String id.
     * @return InclInconclusiveStates.
     */
    public static InclInconclusiveStates getStateById(String key){
        for(InclInconclusiveStates state : values()){
            if(state.id.equals(key)){
                return state;
            }
        }
        return null;
    }
    
    /**
     * @return All states as string array.
     */
    public static String[] getAllStatesForSelect(){
        List<String> all = new ArrayList<String>();
        for(InclInconclusiveStates state : values()){
            all.add(state.id);
        }
        return all.toArray(new String[0]);
    }
}
