package org.amanzi.neo.loader.core.saver;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.data.generator.Nemo2Generator;
import org.amanzi.neo.loader.core.parser.CSVContainer;
import org.amanzi.neo.loader.core.parser.CommonCSVParser;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.testing.AbstractAWETest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Tests for Nemo2xSaver
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class Nemo2xSaverTesting extends AbstractAWETest {
    private static final Logger LOGGER = Logger.getLogger(Nemo2xSaverTesting.class);

    private Nemo2xSaver nemo2xSaver;

    private static DataLoadPreferenceInitializer initializer;

    private static DriveModel model;
    
    private static DriveModel virtualModel;

    private static GraphDatabaseService service;

    private static File testFile;

    private IConfiguration config;

    private static Long startTime;

    private int minColumnSize = 2;

    private static final String NETWORK_KEY = "Network";
    private static final String NETWORK_NAME = "testNetwork";
    private static final String PROJECT_KEY = "Project";
    private static final String PROJECT_NAME = "project";

    private CSVContainer rowContainer = new CSVContainer(minColumnSize);

    @BeforeClass
    public static void prepare() {
        new LogStarter().earlyStartup();
        clearDb();
        initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
        startTime = System.currentTimeMillis();
        Nemo2Generator nemo2Generator = new Nemo2Generator();
        testFile = nemo2Generator.generateNemo2File();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
        LOGGER.info("Nemo2xSaverTesting finished in " + (System.currentTimeMillis() - startTime));
    }

    @SuppressWarnings("rawtypes")
    @Before
    public void onStart() throws AWEException {
        model = mock(DriveModel.class);
        virtualModel = mock(DriveModel.class);
        service = mock(GraphDatabaseService.class);
        config = new ConfigurationDataImpl();
        config.getDatasetNames().put(NETWORK_KEY, NETWORK_NAME);
        config.getDatasetNames().put(PROJECT_KEY, PROJECT_NAME);
        List<File> fileList = new LinkedList<File>();
        fileList.add(testFile);
        config.setSourceFile(fileList);
        nemo2xSaver = new Nemo2xSaver(model, (ConfigurationDataImpl)config, service);
        //CommonCSVParser objCommonCSVParser = new CommonCSVParser(testFile);
        //rowContainer = objCommonCSVParser.parseElement();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingNewElement() {
        try {
            when(model.addFile(eq(rowContainer.getFile()))).thenReturn(new DataElement(new HashMap<String, Object>()));
            nemo2xSaver.saveElement(rowContainer);
            verify(model, atLeastOnce()).addFile(eq(rowContainer.getFile()));
            verify(model, atLeastOnce()).addMeasurement(eq(rowContainer.getFile().getName()), any(Map.class), any(Boolean.class));
            //verify(model, times(5)).getLocations();
            // verify(model, times(5)).addMeasurement(any(String.class),
            // any(Map.class),any(Boolean.class));
            // verify(model).getLocations(new DataElement(eq(createdMainElement)));
        } catch (Exception e) {
            LOGGER.error(" testSavingAllElement error", e);
            Assert.fail("Exception while saving row");
        }
    }
    
    
}
