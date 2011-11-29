package org.amanzi.neo.services;

import org.amanzi.neo.services.synonyms.ExportSynonymsService;

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
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class NeoServiceFactory {

    private static NeoServiceFactory instance = new NeoServiceFactory();
    private final Object datasetMon = new Object();
    private CorrelationService correlationService = null;
    
    // new services
    private DatasetService datasetService = null;
    private NetworkService networkService = null;
    private StatisticsService statisticsService = null;
    private ProjectService projectService = null;
    
    private ExportSynonymsService exportSynonymsService = null;
    private Object exportSynonymsMonitor = new Object();
    
    private DistributionService distributionService = null;
    private Object distributionServiceMonitor = new Object();
    
    private IndexService indexService = null;
    private Object indexServiceMonitor = new Object();

    public static NeoServiceFactory getInstance() {
        return instance;
    }

    public DatasetService getDatasetService() {
        if (datasetService == null) {
            synchronized (datasetMon) {
                if (datasetService == null) {
                    datasetService = new DatasetService();
                }
            }
        }
        return datasetService;
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

    public StatisticsService getStatisticsService() {
        if (statisticsService == null) {
            synchronized (datasetMon) {
                if (statisticsService == null) {
                    statisticsService = new StatisticsService();
                }
            }
        }
        return statisticsService;
    }

    public ProjectService getProjectService() {
        if (projectService == null) {
            synchronized (datasetMon) {
                if (projectService == null) {
                    projectService = new ProjectService();
                }
            }
        }
        return projectService;
    }

    /**
     * @return
     */
    public CorrelationService getCorrelationService() {
        if (correlationService == null) {
            synchronized (datasetMon) {
                if (correlationService == null) {
                    correlationService = new org.amanzi.neo.services.CorrelationService();
                }
            }
        }
        return correlationService;
    }
    
    public ExportSynonymsService getExportSynonymsService() {
        if (exportSynonymsService == null) {
            synchronized (exportSynonymsMonitor) {
                if (exportSynonymsService == null) {
                    exportSynonymsService = new ExportSynonymsService();
                }
            }
        }
        
        return exportSynonymsService;
    }
    
    public DistributionService getDistributionService() {
        if (distributionService == null) {
            synchronized (distributionServiceMonitor) {
                if (distributionService == null) {
                    distributionService = new DistributionService();
                }
            }
        }
        
        return distributionService;
    }
    
    public IndexService getIndexService() {
        if (indexService == null) {
            synchronized (indexServiceMonitor) { 
                if (indexService == null) {
                    indexService = new IndexService();
                }
            }
        }
        
        return indexService;
    }

}
