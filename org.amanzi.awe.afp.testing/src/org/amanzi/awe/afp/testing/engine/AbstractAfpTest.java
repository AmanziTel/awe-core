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

package org.amanzi.awe.afp.testing.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.amanzi.awe.afp.executors.AfpProcessExecutor;
import org.amanzi.awe.afp.exporters.AfpExporter;
import org.amanzi.awe.afp.loaders.AfpOutputFileLoader;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.testing.engine.internal.AfpModelFactory.AfpScenario;
import org.amanzi.awe.afp.testing.engine.internal.FakeAfpLoader;
import org.amanzi.awe.afp.testing.engine.internal.IDataset;
import org.amanzi.awe.afp.testing.engine.internal.LoadEricssonDataAction;
import org.amanzi.awe.afp.testing.engine.internal.LoadGermanyDataAction;
import org.amanzi.awe.afp.testing.engine.internal.TestDataLocator.DataType;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractAfpTest extends AbstractAWETest {
    
    private static Logger LOGGER = Logger.getLogger(AbstractAfpTest.class);
    
    protected static ArrayList<IDataset> datasets = new ArrayList<IDataset>();
    
    protected static HashMap<IDataset, HashMap<AfpScenario, AfpModel>> scenarios = new HashMap<IDataset, HashMap<AfpScenario, AfpModel>>();
    
    protected static HashMap<AfpModel, AfpExporter> exporterMap = new HashMap<AfpModel, AfpExporter>();
    
    private static HashMap<AfpModel, AfpOutputFileLoader> loaderMap = new HashMap<AfpModel, AfpOutputFileLoader>();
    
    protected static void initEnvironment() throws IOException {
        clearDb();
        initializeDb();
        initPreferences();
        
        LOGGER.info("Initialize Test datasets");
        for (DataType singleType : DataType.values()) {
            IDataset loader = getDatasetLoader(singleType);
            if (loader != null) {
                datasets.add(getDatasetLoader(singleType));
            }
        }
    }
    
    private static IDataset getDatasetLoader(DataType dataType) throws IOException {
        switch (dataType) {
        case ERICSSON:
            return new LoadEricssonDataAction("project");
        case GENERAL_FORMAT:
            return null;
        case GERMANY:
            return new LoadGermanyDataAction("project");
        }
        
        return null;
    }
    
    protected static void loadDataset() {
        LOGGER.info("Load Datasets");
        for (IDataset loader : datasets) {
            if (loader != null) {
                loader.run();
            }
        }
    }
    
    protected static void exportInputFiles() {
        LOGGER.info("Export input files for AFP Engine");
        for (IDataset dataset : datasets) {
            if (dataset == null) {
                continue;
            }
            for (AfpScenario scenario : AfpScenario.values()) {
                AfpModel model = dataset.getAfpModel(scenario);
                AfpExporter exporter = model.getExporter();
                
                AfpOutputFileLoader loader = new FakeAfpLoader(dataset.getRootNode(), model.getAfpNode(), exporter);
                loaderMap.put(model, loader);
                
                exporterMap.put(model, exporter);
                
                LOGGER.info("Writing files for Dataset <" + dataset.getName() + "> using " + scenario.name() + " scenario");
                long before = System.currentTimeMillis();
                exporter.run(null);
                long after = System.currentTimeMillis();
                LOGGER.info("Writing finished in " + (after - before) + " milliseconds");
                
                model.saveUserData();
                model.executeAfpEngine(null, exporter);
                
                if (!scenarios.containsKey(dataset)) {
                    scenarios.put(dataset, new HashMap<AfpScenario, AfpModel>());
                }
                scenarios.get(dataset).put(scenario, model);
            }
        }
    }
    
    protected static void runEngine() {
        LOGGER.info("Running AFP Engine");
        
        for (IDataset dataset : scenarios.keySet()) {
            for (AfpScenario scenario : scenarios.get(dataset).keySet()) {
                AfpModel model = scenarios.get(dataset).get(scenario);
                
                AfpProcessExecutor executor = model.getExecutor();
                LOGGER.info("AFP Engine started for dataset <" + dataset.getName() + "> with " + scenario.name() + " scenario");
                long before = System.currentTimeMillis();
                executor.run(null);
                long after = System.currentTimeMillis();
                LOGGER.info("AFP Engine finished. Spent time - " + toHourTime(after - before));
            }
        }
    }
    
    protected static String toHourTime(long milliseconds) {
        int seconds = (int)(milliseconds / 1000 % 60 );
        int minutes = (int)(milliseconds / 1000 / 60 % 60);
        int hours = (int)(milliseconds / 1000 / 60 / 60 % 24);
        return hours + " hours " + 
               minutes + " minutes " + 
               seconds + " seconds";
        
    }
    
    protected static void loadResults() throws IOException {
        LOGGER.info("Loading generated Frequency Plans back to Database");
        for (IDataset dataset : datasets) {
            if (dataset == null) {
                continue;
            }
            for (AfpScenario scenario : scenarios.get(dataset).keySet()) {
                AfpModel model = scenarios.get(dataset).get(scenario);
                AfpOutputFileLoader loader = loaderMap.get(model);
                
                long before = System.currentTimeMillis();
                loader.runAfpLoader(null);
                long after = System.currentTimeMillis();
                LOGGER.info("Generated Frequency Plan for datasets <" + dataset.getName() + "> and scenario <" + scenario.name() + "> was loaded in " + (after - before) + " milliseconds");
            }
        }
    }

}
