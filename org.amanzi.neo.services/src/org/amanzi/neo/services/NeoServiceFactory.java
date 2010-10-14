package org.amanzi.neo.services;


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
    private Object datasetMon=new Object();
    private DatasetService datasetService = null;
    
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
//    public synchronized DatasetService getDatasetService(GraphDatabaseService neo) {
//        if (datasetService == null) {
//            datasetService = new DatasetService(neo);
//        }
//        return datasetService;
//    }
    
    
}
