package org.amanzi.neo.loader.core.newsaver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.data.generator.Nemo2Generator;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

public class Nemo2xSaverTesting extends AbstractAWETest{
	private static final Logger LOGGER = Logger.getLogger(Nemo2xSaverTesting.class);
	
	private Nemo2xSaver nemo2xSaver;
	
	private static DataLoadPreferenceInitializer initializer;
	
	private static DriveModel model;
	
	private static GraphDatabaseService service;
	
	private static File testFile;
	
	private IConfiguration config;
	
	private static Long startTime;
	
	private int minColumnSize = 2;
	
	private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";
	
	@BeforeClass
    public static void prepare() {
        new LogStarter().earlyStartup();
        clearDb();
        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
        startTime = System.currentTimeMillis();
        testFile = Nemo2Generator.generateNemo2File();

    }
	
	@AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("Nemo2xSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

	@Before
    public void onStart() throws AWEException {
        model = mock(DriveModel.class);
        service = mock(GraphDatabaseService.class);
        config = new ConfigurationDataImpl();
        config.getDatasetNames().put(NETWORK_KEY, NETWORK_NAME);
        config.getDatasetNames().put(PROJECT_KEY, PROJECT_NAME);
        List<File> fileList = new LinkedList<File>();
        fileList.add(testFile);
        config.setSourceFile(fileList);
        nemo2xSaver = new Nemo2xSaver(model, (ConfigurationDataImpl)config, service);
    }
	
	@Test
    public void testCreatingNewElement() {
        CSVContainer rowContainer = new CSVContainer(minColumnSize);
        //List<String> header = new LinkedList<String>();
        rowContainer.setFile(config.getFilesToLoad().get(0));
        //rowContainer.setHeaders();
        try {
            nemo2xSaver.saveElement(rowContainer);
            //rowContainer.setValues(values);
            
            //nemo2xSaver.saveElement(rowContainer);

            //verify(model, times(5)).addMeasurement(any(String.class), any(Map.class),any(Boolean.class));
            //verify(model).getLocations(new DataElement(eq(createdMainElement)));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }
}
