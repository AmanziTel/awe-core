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
    
    private static NeoServiceFactory instance = null;
    
    private DatasetService datasetService = null;
    
    public static synchronized NeoServiceFactory getInstance() {
        if (instance == null) {
            instance = new NeoServiceFactory();
        }
        return instance;
    }
    
    public synchronized DatasetService getDatasetService() {
        if (datasetService == null) {
            datasetService = new DatasetService();
        }
        return datasetService;
    }
    
    
}
