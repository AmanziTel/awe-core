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

package org.amanzi.neo.providers.context.internal;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.IService;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AnotherTestService implements IService {

    private final IGeneralNodeProperties generalNodeProperties;

    private final GraphDatabaseService dbService;

    private final ITestService testService;

    public AnotherTestService(ITestService testService, IGeneralNodeProperties generalNodeProperties, GraphDatabaseService dbService) {
        this.generalNodeProperties = generalNodeProperties;
        this.dbService = dbService;
        this.testService = testService;
    }

    /**
     * @return Returns the generalNodeProperties.
     */
    public IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }

    /**
     * @return Returns the dbService.
     */
    public GraphDatabaseService getDbService() {
        return dbService;
    }

    /**
     * @return Returns the testService.
     */
    public ITestService getTestService() {
        return testService;
    }

}
