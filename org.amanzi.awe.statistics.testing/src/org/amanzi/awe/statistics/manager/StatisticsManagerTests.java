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

package org.amanzi.awe.statistics.manager;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.exceptions.StatisticsException;
import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.testing.AbstractAWEDBTest;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsManagerTests extends AbstractAWEDBTest {
    private static IDriveModel driveModel;
    private static final StatisticsManager MANAGER = StatisticsManager.getInstance();
    private static IProjectModel PROJECT_MODEL;
    private static final String DRIVE_MODEL_NAME = "drive";
    private static final String FILE_NAME = "fileName";
    private static final int ARRAY_SIZE = 5;
    private static final String TEMPLATE_NAME = "netview:imei.t";
    private static final String SIGNAL_STRENGTH_PROPERTY = "signal_strength";
    private static final String CALL_STATUS_PROPERTY = "call_status";
    private static final String TRAFIC_RX_BYTES = "trafficcount_rxbytes";
    private static final String ANSWERING = "answering";
    private static final String PROJECT_NAME = "project";
    private static final String MODEL = "model";
    private static final String[] MODELS = {"Desire HD", "NX-A899", "GT-P1000", "GT-S5360", "GT-I8150", "GT-55830", "GT-I9100",
            "GT-I9210", "GT-N7000", "Galaxy Nexus", "HTC Desire HD A9191"};
    private static Calendar startTime = Calendar.getInstance();

    @BeforeClass
    public static void prepareDatabase() throws AWEException {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
        PROJECT_MODEL = ProjectModel.setActiveProject(PROJECT_NAME);
        driveModel = PROJECT_MODEL.getDriveModel(DRIVE_MODEL_NAME, DriveTypes.TEMS);
        driveModel.addFile(new File(FILE_NAME));
        buildDriveModel();
    }

    @Test
    public void testProcessStatistics() throws StatisticsException {
        MANAGER.processStatistics(TEMPLATE_NAME, driveModel, MODEL, Period.DAILY, new NullProgressMonitor());
    }

    /**
     * @throws AWEException
     */
    private static void buildDriveModel() throws AWEException {

        for (int i = 0; i < ARRAY_SIZE; i++) {
            Map<String, Object> measurement = generateMeasurement();
            driveModel.addMeasurement(FILE_NAME, measurement);
        }

        driveModel.finishUp();
    }

    /**
     * @return
     */
    private static Map<String, Object> generateMeasurement() {
        Map<String, Object> measurement = new HashMap<String, Object>();
        measurement.put(DriveModel.LONGITUDE, generateDouble());
        measurement.put(DriveModel.LATITUDE, generateDouble());
        startTime.add(Calendar.DATE, NumberUtils.INTEGER_ONE);
        measurement.put(DriveModel.TIMESTAMP, startTime.getTimeInMillis());
        measurement.put(MODEL, getRandomModel());
        measurement.put(CALL_STATUS_PROPERTY, ANSWERING);
        measurement.put(SIGNAL_STRENGTH_PROPERTY, generateDouble());
        measurement.put(TRAFIC_RX_BYTES, generateLong());
        return measurement;
    }

    /**
     * @return
     */
    private static Object getRandomModel() {
        int size = MODELS.length;
        int index = (int)(Math.random() * size);
        return MODELS[index];
    }

    /**
     * @return
     */
    private static Long generateLong() {
        return (long)(Math.random() * 10000);
    }

    /**
     * @return
     */
    private static Double generateDouble() {
        return Math.random() * 100;
    }
}
