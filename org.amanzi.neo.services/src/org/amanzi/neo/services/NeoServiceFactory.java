package org.amanzi.neo.services;

import org.amanzi.neo.services.correlation.CorrelationService;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;


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

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class NeoServiceFactory {
    
    private static NeoServiceFactory instance = new NeoServiceFactory();
    private final Object datasetMon=new Object();
    private DatasetService datasetService = null;
    private CorrelationService correlationService = null;
    private AweProjectService projectService=null;
    private StatisticService statisticService = null;
    
    private NodeToNodeRelationService node2nodeRelationService = null;
    private final Object node2nodeServiceMonitor = new Object();
    private NetworkService networkService;
    
    public static  NeoServiceFactory getInstance() {
        return instance;
    }
    
    public  DatasetService getDatasetService() {
        if (datasetService == null) {
            synchronized (datasetMon) {
                if (datasetService == null) {
                    datasetService = new DatasetService();
                }
            }
        }
        return datasetService;
    }
    
    public  CorrelationService getCorrelationService() {
        if (correlationService == null) {
            synchronized (datasetMon) {
                if (correlationService == null) {
                    correlationService = new CorrelationService();
                }
            }
        }
        return correlationService;
    }
//    public synchronized DatasetService getDatasetService(GraphDatabaseService neo) {
//        if (datasetService == null) {
//            datasetService = new DatasetService(neo);
//        }
//        return datasetService;
//    }

    /**
     *
     * @return
     */
    public AweProjectService getProjectService() {
        if (projectService == null) {
            synchronized (datasetMon) {
                if (projectService == null) {
                    projectService = new AweProjectService();
                }
            }
        }
        return projectService;
    }
    
    public StatisticService getStatisticService() {
        if (statisticService == null) {
            synchronized (datasetMon) {
                if (statisticService == null) {
                    statisticService = new StatisticService();
                }
            }
        }
        return statisticService;
    }

    public NodeToNodeRelationService getNodeToNodeRelationService() {
    	if (node2nodeRelationService == null) {
    		synchronized (node2nodeServiceMonitor) {
    			if (node2nodeRelationService == null) {
    				node2nodeRelationService = new NodeToNodeRelationService();
    			}
			}
    	}
    	
    	return node2nodeRelationService;
    }
    public NetworkService getNetworkService() {
        if (networkService == null) {
            synchronized (datasetMon) {
                if (networkService == null) {
                    networkService = new NetworkService();
                }
            }
        }
        return networkService;
    }
    
}
