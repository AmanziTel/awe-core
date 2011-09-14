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

package org.amanzi.awe.cassidian.loader.parser.amsxmltests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.cassidian.collector.CallCollector;
import org.amanzi.awe.cassidian.collector.CallPreparator;
import org.amanzi.awe.cassidian.datagenerator.DataGenerator;
import org.amanzi.awe.cassidian.loader.FileLoader;
import org.amanzi.awe.cassidian.loader.parser.AMSXMLParser;
import org.amanzi.awe.cassidian.structure.CommonTestData;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.awe.cassidian.structure.SendMsg;
import org.amanzi.awe.cassidian.structure.SendReport;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.amanzi.awe.cassidian.writer.AMSXMLWriter;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.loader.ui.validators.AMSXMLDataValidator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for ams xml validator
 * 
 * @author Kondratenko_Vladislav
 */
public class AMSXMLValidatorTest {
    private static Logger LOGGER = Logger.getLogger(AMSXMLCollectorTest.class);
    private static DataGenerator dg;
    private static FileLoader fileloader;
    private static File root;
    private static final String DIRECTORY_PATH = System.getProperty("user.home") + "/.amanzi/xmlTest/";
    private static final String TEMP_FILENAME = "/temp.tmp";
    private static final String EMPTY_XML_FILE_NAME_1 = "/tempXML.xml";
    private static final String EMPTY_XML_FILE_NAME_2 = "/tempXML.xml";
    private static final String FILE_NAME = "collectortest";
    private AMSXMLWriter xmlWriter;
    private static final String TEMP_DIRECORY_1 = "tempDir1/";
    private static final String TEMP_DIRECORY_2 = "tempDir2/";
    private static final String COMMON_NAME = "Common";

    @BeforeClass
    public static void initVariables() throws IOException {
        prepareDirectory();
        dg = new DataGenerator();
        fileloader = new FileLoader(DIRECTORY_PATH);
    }

    /**
     * prepare directory for generate and parse files
     * 
     * @throws IOException
     */
    private static void prepareDirectory() throws IOException {
        root = new File(DIRECTORY_PATH);
        if (root.exists()) {
            deleteDirectory(root);
        }

        createTempRootDirectory();
    }

    /**
     * remove generated files and directories
     * 
     * @param file
     */
    private static void deleteDirectory(File file) {
        if (file.listFiles() != null) {
            for (File subFile : file.listFiles()) {
                if (subFile.isDirectory()) {
                    deleteDirectory(subFile);
                } else {
                    subFile.delete();
                }
            }
        }

    }

    /**
     * generate temporary directories structure
     * 
     * @throws IOException
     */
    private static void createTempRootDirectory() throws IOException {
        File dir1 = new File(DIRECTORY_PATH);
        dir1.mkdir();
        File dir2 = new File(DIRECTORY_PATH + TEMP_DIRECORY_1);
        dir2.mkdir();
        new File(dir2.getAbsolutePath() + TEMP_FILENAME).createNewFile();
        dir2 = new File(DIRECTORY_PATH + TEMP_DIRECORY_2);
        dir2.mkdir();
        new File(dir2.getAbsolutePath() + EMPTY_XML_FILE_NAME_1).createNewFile();
        new File(DIRECTORY_PATH + EMPTY_XML_FILE_NAME_2).createNewFile();
    }

    private EventsElement generateEventIndividual() {
        EventsElement event = dg.generateEventsElement();
        TOCElement toc = dg.generateTOCElement();
        toc.setHook(0);
        toc.setSimplex(0);
        event.setTocttc(toc);
        return event;
    }

    private CommonTestData generateCommonTestData() {
        CommonTestData ctd = dg.generateCommonTestData();
        ctd.addMemberToMPTSync(dg.generateMptSync());
        ctd.addMembertoNeighborData(dg.generateNeighborData());
        ctd.addMemberToNtpq(dg.generateNtpq());
        ctd.addMemberToNtpq(dg.generateNtpq());
        Ntpq ntpq = dg.generateNtpq();
        ntpq.setProbeId("probe ID 11");
        ctd.addMemberToNtpq(ntpq);
        ntpq = dg.generateNtpq();
        ntpq.setProbeId("probe ID 11");
        ctd.addMemberToNtpq(ntpq);
        ctd.addMemberToProbeIdNumberMap(dg.generateProbeIDNumberMap());
        ctd.addMemberToServingData(dg.generateServingData());
        return ctd;
    }

    private GPSData generateGpsData() {
        GPSData gps = dg.generateGPSDATA();
        CompleteGpsDataList cgpsList = dg.generateCompleateGPSDataList();
        cgpsList.addMemberToCompleateGpsData(dg.generateCompleateGPSData());
        gps.addMember(cgpsList);
        return gps;
    }

    private TNSElement generateFile() {
        TNSElement tns = new TNSElement();

        return tns;
    }

    /**
     * @return
     */
    private EventsElement generateCellResel() {
        EventsElement event = dg.generateEventsElement();
        event.setCellReselection(dg.generateCellReselection());
        return event;
    }

    /**
     * @return
     */
    private EventsElement generateHandover() {
        EventsElement event = dg.generateEventsElement();
        event.setHandover(dg.generateHandover());
        return event;
    }

    /**
     * @return
     */
    private EventsElement generateEventSDS() {
        EventsElement event = new EventsElement();
        SendMsg sendmsg = dg.generateSendMsg();
        sendmsg.setMsgType(12);
        SendReport sendReport = dg.generateSendReport();
        sendmsg.addMemberToSendReport(sendReport);
        sendReport = dg.generateSendReport();
        sendmsg.addMemberToSendReport(sendReport);
        sendReport = dg.generateSendReport();
        sendmsg.addMemberToSendReport(sendReport);
        event.setSendRecieveMsg(sendmsg);

        return event;
    }

    /**
     * @return
     */
    private EventsElement generateEventTSM() {
        EventsElement event = new EventsElement();
        SendMsg senMsg = dg.generateSendMsg();
        senMsg.setMsgType(13);
        event.setSendRecieveMsg(senMsg);
        return event;
    }

    /**
     * @return
     */
    private EventsElement generateEventEmergency() {
        EventsElement event = dg.generateEventsElement();
        TOCElement toc = dg.generateTOCElement();
        toc.setHook(1);
        toc.setSimplex(1);
        toc.setPriority(16);
        event.setTocttc(toc);
        return event;
    }

    /**
     * @return
     */
    private EventsElement generateEventGroup() {
        EventsElement event = dg.generateEventsElement();
        TOCElement toc = dg.generateTOCElement();
        toc.setHook(1);
        toc.setSimplex(1);
        event.setTocttc(toc);
        return event;
    }

    /**
     * @return
     */
    private EventsElement generateEventHelp() {
        EventsElement event = dg.generateEventsElement();
        TOCElement toc = dg.generateTOCElement();
        toc.setHook(0);
        toc.setSimplex(0);
        toc.setPriority(0);
        event.setTocttc(toc);
        return event;
    }

   

    /**
     * test for checking common call count
     */
    @Test
    public void testParsingOfXml() {
        long begin = System.currentTimeMillis();
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME + COMMON_NAME);
        final int expectedIndividualCallCount = 2;
        final int expectedOthersCount = 1;
        TNSElement tns = generateFile();
        tns.addMembertoCommonTestList(generateCommonTestData());
        tns.addMembertoGPSList(generateGpsData());
        tns.addMembertoEventsList(generateEventSDS());
        tns.addMembertoEventsList(generateCellResel());
        tns.addMembertoEventsList(generateEventHelp());
        tns.addMembertoEventsList(generateEventEmergency());
        tns.addMembertoEventsList(generateEventIndividual());
        tns.addMembertoEventsList(generateEventIndividual());
        tns.addMembertoEventsList(generateEventGroup());
        tns.addMembertoEventsList(generateHandover());
        tns.addMembertoEventsList(generateEventTSM());
        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        List<File> files = fileloader.getRootsFiles(root);
        TNSElement tns2;
        CallPreparator preparator = new CallPreparator();
        for (File currentFile : files) {
            tns2 = parser.parse(currentFile);
            if (tns2 != null) {
                CallCollector collector = preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(),tns2.getEvents(), tns2.getGps(), tns2.getCtd().get(0)
                        .getNtpq());
                Assert.assertEquals("Unexpected collector size", expectedIndividualCallCount, collector.getIndividualCalls().size());
                Assert.assertEquals("Unexpected collector size", expectedOthersCount, collector.getHelpCalls().size());
                Assert.assertEquals("Unexpected collector size", expectedOthersCount, collector.getGroupCalls().size());
                Assert.assertEquals("Unexpected collector size", expectedOthersCount, collector.getEmergencyCalls().size());
                Assert.assertEquals("Unexpected collector size", expectedOthersCount, collector.getSDSCalls().size());
                Assert.assertEquals("Unexpected collector size", expectedOthersCount, collector.getTSMCalls().size());
                Assert.assertEquals("Unexpected collector size", expectedOthersCount, collector.getCellResels().size());
                Assert.assertEquals("Unexpected collector size", expectedOthersCount, collector.getHandovers().size());
            }

        }
        LOGGER.info("testParsingOfXml finished in " + (System.currentTimeMillis() - begin));
    }

    /**
     * Validator test
     */
    @Test
    public void validatorTest() {
        IConfiguration config = new ConfigurationDataImpl(DIRECTORY_PATH);
        IValidator validator = new AMSXMLDataValidator();
        List<File> files = config.getFilesToLoad();
        final int EXPECTED_FILES_COUNT = 1;
        validator.isValid(files);
        Assert.assertEquals("Unexpected files count", EXPECTED_FILES_COUNT, files.size());
    }
}
