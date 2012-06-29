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

import org.amanzi.neo.services.internal.IService;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class MultiConstructorService implements IService {

    private final AnotherTestService service1;

    private final TestService service2;

    private final GraphDatabaseService graphDb;

    private Integer i;

    private String s;

    private Float f;

    public MultiConstructorService() {
        this((Integer)null, (String)null, (Float)null);
    }

    public MultiConstructorService(GraphDatabaseService graphDb) {
        this(null, null, graphDb);
    }

    public MultiConstructorService(AnotherTestService service1, GraphDatabaseService graphDb) {
        this(service1, null, graphDb);
    }

    public MultiConstructorService(TestService service2, GraphDatabaseService graphDb) {
        this(null, service2, graphDb);
    }

    public MultiConstructorService(Integer i, String s, Float f) {
        this((AnotherTestService)null, null, null);

        this.i = i;
        this.s = s;
        this.f = f;
    }

    public MultiConstructorService(AnotherTestService service1, TestService service2, GraphDatabaseService graphDb) {
        this.service1 = service1;
        this.service2 = service2;
        this.graphDb = graphDb;
    }

    /**
     * @return Returns the service1.
     */
    public AnotherTestService getService1() {
        return service1;
    }

    /**
     * @return Returns the service2.
     */
    public TestService getService2() {
        return service2;
    }

    /**
     * @return Returns the graphDb.
     */
    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    /**
     * @return Returns the i.
     */
    public Integer getI() {
        return i;
    }

    /**
     * @return Returns the s.
     */
    public String getS() {
        return s;
    }

    /**
     * @return Returns the f.
     */
    public Float getF() {
        return f;
    }

}
