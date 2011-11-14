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
    private NewDatasetService newDatasetService = null;
    private NewNetworkService newNetworkService = null;
    private NewStatisticsService newStatisticsService = null;
    private ProjectService newProjectService = null;
    private org.amanzi.neo.services.CorrelationService newCorrelationService = null;
    
    private ExportSynonymsService exportSynonymsService = null;
    private Object exportSynonymsMonitor = new Object();
    
    private DistributionService distributionService = null;
    private Object distributionServiceMonitor = new Object();
    
    private IndexService indexService = null;
    private Object indexServiceMonitor = new Object();

    public static NeoServiceFactory getInstance() {
        return instance;
    }

    public CorrelationService getCorrelationService() {
        if (correlationService == null) {
            synchronized (datasetMon) {
                if (correlationService == null) {
                    correlationService = new CorrelationService();
                }
            }
        }
        return correlationService;
    }

    public NewDatasetService getNewDatasetService() {
        if (newDatasetService == null) {
            synchronized (datasetMon) {
                if (newDatasetService == null) {
                    newDatasetService = new NewDatasetService();
                }
            }
        }
        return newDatasetService;
    }

    public NewNetworkService getNewNetworkService() {
        if (newNetworkService == null) {
            synchronized (datasetMon) {
                if (newNetworkService == null) {
                    newNetworkService = new NewNetworkService();
                }
            }
        }
        return newNetworkService;
    }

    public NewStatisticsService getNewStatisticsService() {
        if (newStatisticsService == null) {
            synchronized (datasetMon) {
                if (newStatisticsService == null) {
                    newStatisticsService = new NewStatisticsService();
                }
            }
        }
        return newStatisticsService;
    }

    public ProjectService getNewProjectService() {
        if (newProjectService == null) {
            synchronized (datasetMon) {
                if (newProjectService == null) {
                    newProjectService = new ProjectService();
                }
            }
        }
        return newProjectService;
    }

    /**
     * @return
     */
    public org.amanzi.neo.services.CorrelationService getNewCorrelationService() {
        if (newCorrelationService == null) {
            synchronized (datasetMon) {
                if (newCorrelationService == null) {
                    newCorrelationService = new org.amanzi.neo.services.CorrelationService();
                }
            }
        }
        return newCorrelationService;
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
