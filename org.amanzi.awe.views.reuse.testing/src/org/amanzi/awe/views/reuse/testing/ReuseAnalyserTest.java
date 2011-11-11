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

package org.amanzi.awe.views.reuse.testing;

import java.io.File;
import java.io.IOException;

import org.amanzi.awe.views.reuse.Distribute;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.testing.CommonTestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;


/**
 * <p>
 *Test case for Reuse analyser
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ReuseAnalyserTest {
    private static final String DATABASE_NAME = "neo_test";
    private static final String MAIN_DIR = "test";
    static CommonTestUtil util;
    @BeforeClass
    public static void init(){
        util=new CommonTestUtil(DATABASE_NAME, MAIN_DIR);
        
        NeoServiceProviderUi.initProvider(util.getNeo());
    }
    @Test
    public void testCalculation() throws IOException{
        Transaction tx = util.getNeo().beginTx();
        try{
            Node root=createStructure();
            Evaluator propertyReturnableEvalvator =createReturnableEvaluator(root);
            tx.success();
            tx.finish();
            tx=util.getNeo().beginTx();
//            ReuseAnalyserModel model = new ReuseAnalyserModel(new HashMap<String, String[]>(), propertyReturnableEvalvator, util.getNeo());
            
            for (String property: PropertyHeader.getPropertyStatistic(root).getNumericFields("-main-type-")){
                for (Distribute distribute:Distribute.values()){
                    if (distribute==Distribute.CUSTOM){
                        //need additional requirements - this case do not work for all properties.
                        continue;
                    }
//                    model.findOrCreateAggregateNode(root, property, false, distribute.toString(), Select.EXISTS.toString(), new NullProgressMonitor());
                }
            }
        }finally{
            tx.finish();
        }
    }

    private Evaluator createReturnableEvaluator(Node root) {
    	
        
        final String type= NeoUtils.getPrimaryType(root);
        return new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                boolean includes = arg0.endNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(type);
                
                return Evaluation.of(includes, true);
            }
        };        
    }

    /**
     * Creates the structure.
     *
     * @return the node
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Node createStructure() throws IOException {
        //TODO use data generator for create file
//        NetworkLoader loader = new NetworkLoader(util.getNeo(), "../org.amanzi.awe.views.reuse.testing/files/network.csv", util.getIndex());
//        loader.setup();
//        loader.run(new NullProgressMonitor());
//        return loader.getNetworkNode();
//        TEMSLoader loader = new TEMSLoader(util.getNeo(), "files/0904_90.FMT", "dataset",util.getIndex());
//        loader.run(new NullProgressMonitor());
//        return loader.getDatasetNode();
        return null;
    }
    /**
     * Finish all tests.
     */
    @AfterClass
    public static void finishAll(){
        util.shutdownNeo();
        CommonTestUtil.clearDirectory(new File(util.getMainDirectory(false)));
    }
}
