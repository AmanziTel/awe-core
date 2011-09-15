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
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.cassidian.collector.CallCollector;
import org.amanzi.awe.cassidian.collector.CallPreparator;
import org.amanzi.awe.cassidian.datagenerator.DataGenerator;
import org.amanzi.awe.cassidian.loader.FileLoader;
import org.amanzi.awe.cassidian.loader.parser.AMSXMLParser;
import org.amanzi.awe.cassidian.saver.AMSXMLSaver;
import org.amanzi.awe.cassidian.structure.CellReselection;
import org.amanzi.awe.cassidian.structure.CommonTestData;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.Handover;
import org.amanzi.awe.cassidian.structure.ItsiAttach;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.awe.cassidian.structure.PESQResultElement;
import org.amanzi.awe.cassidian.structure.RecieveMsg;
import org.amanzi.awe.cassidian.structure.SendMsg;
import org.amanzi.awe.cassidian.structure.SendReport;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.amanzi.awe.cassidian.structure.TTCElement;
import org.amanzi.awe.cassidian.writer.AMSXMLWriter;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for AMSXML Preparator ant Collector
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class AMSXMLCollectorTest {
    private static Logger LOGGER = Logger.getLogger(AMSXMLCollectorTest.class);
    private static DataGenerator dg;
    private static FileLoader fileloader;
    private static File root;
    private static final String DIRECTORY_PATH = System.getProperty("user.home") + "/.amanzi/xmlTest/";
    private static final String TEMP_FILENAME = "/temp.tmp";
    private static final String EMPTY_XML_FILE_NAME_1 = "/tempXML.xml";
    private static final String EMPTY_XML_FILE_NAME_2 = "/tempXML.xml";
    private static final String XML_EXTENSION = ".xml";
    private static final String FILE_NAME = "collectortest";
    private AMSXMLWriter xmlWriter;
    private static final String TEMP_DIRECORY_1 = "tempDir1/";
    private static final String TEMP_DIRECORY_2 = "tempDir2/";
    private static final String COMMON_NAME = "Common";
    private static final String INDIVIDUAL_NAME = "Individual";
    private static final String SDS_NAME = "SDS";
    private static final String HANDOVER_NAME = "Handover";
    private static final String CELL_RESELECTION_NAME = "Cell_Reselection";
    private static final String ITSI_ATTACH_NAME = "itsi_Attach";
    private static final String SAVER_NAME = " Saver";
    private static final float[] PESQ_VALUE_ARRAY = {22f, 13f, 10f};
    private static final long[] DELAY_VALUE_ARRAY = {12l, 11l, 14l};

    // TODO not used yet
    /*
     * private static final Pattern COORD_PAT_LAT =
     * Pattern.compile("^(\\d{2})(\\d{2}\\.\\d{4,5})$"); private static final Pattern COORD_PAT_LON
     * = Pattern.compile("^(\\d{3})(\\d{2}\\.\\d{4,5})$"); private static final Calendar calendar =
     * Calendar.getInstance();
     */
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
     * generate Cell reselection call
     * 
     * @return
     */
    private EventsElement generateEventRecelection() {
        EventsElement event = dg.generateEventsElement();
        CellReselection resel = dg.generateCellReselection();
        event.setCellReselection(resel);
        return event;
    }

    /**
     * generate itsiAttach event
     * 
     * @return
     */
    private EventsElement generateEventItsiAttach() {
        EventsElement event = dg.generateEventsElement();
        ItsiAttach itsi = dg.geneItsiAttach();
        event.setItsiAttach(itsi);
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
                CallCollector collector = preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(),
                        tns2.getEvents(), tns2.getGps());
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
     * generate predefined pesq list
     * 
     * @return
     */
    private List<PESQResultElement> generatePredefinedPesq() {
        List<PESQResultElement> pesqList = new LinkedList<PESQResultElement>();
        PESQResultElement pesq;
        int pesqArraySize = PESQ_VALUE_ARRAY.length;
        int delayArraySize = DELAY_VALUE_ARRAY.length;
        while (pesqArraySize != 0 || delayArraySize != 0) {
            delayArraySize--;
            pesqArraySize--;
            pesq = dg.generatePESQ();
            pesq.setDelay(DELAY_VALUE_ARRAY[delayArraySize]);
            pesq.setPesq(PESQ_VALUE_ARRAY[pesqArraySize]);
            pesqList.add(pesq);

        }

        return pesqList;
    }

    /**
     * calculate average from float array
     * 
     * @param array
     * @return
     */
    private float calculateAvgFromFloatArray(float[] array) {
        int size = array.length;
        float sum = 0;
        int i = 0;
        while (i != size) {
            sum += array[i];
            i++;
        }
        return sum / size;

    }

    /**
     * calculate average from float array
     * 
     * @param array
     * @return
     */
    private float calculateAvgFromLongArray(long[] array) {
        int size = array.length;
        float sum = 0;
        int i = 0;
        while (i != size) {
            sum += array[i];
            i++;
        }
        return sum / size;

    }

    /**
     * check event collector of Individual Call;
     */
    @Test
    public void testEventCollectorIndividual() {
        long begin = System.currentTimeMillis();
        final int expectedIndividualCallCount = 1;
        final int expectedEventsCount = 3;
        final float expectedAVGLQ = calculateAvgFromFloatArray(PESQ_VALUE_ARRAY);
        final float expectedAVGDelay = calculateAvgFromLongArray(DELAY_VALUE_ARRAY);
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME + INDIVIDUAL_NAME);
        TNSElement tns = generateFile();
        tns.addMembertoEventsList(generateEventIndividual());
        EventsElement event = dg.generateEventsElement();
        TTCElement ttc = dg.generateTTCElement();
        ttc.setPesqResult(generatePredefinedPesq());
        event.setTocttc(ttc);
        tns.addMembertoEventsList(event);
        event = dg.generateEventsElement();
        event.setTocttc(dg.generateTTCElement());
        tns.addMembertoEventsList(event);
        tns.addMembertoCommonTestList(generateCommonTestData());
        tns.addMembertoGPSList(generateGpsData());

        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        TNSElement tns2 = parser.parse(DIRECTORY_PATH + FILE_NAME + INDIVIDUAL_NAME + "#" + xmlWriter.getTime() + XML_EXTENSION);
        CallPreparator preparator = new CallPreparator();

        CallCollector collector = preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(), tns2.getEvents(),
                tns2.getGps());

        Assert.assertEquals("Unexpected collector size", expectedIndividualCallCount, collector.getRealCalls().size());
        Assert.assertEquals("Unexpected event size", expectedEventsCount, collector.getRealCalls().get(0).getEventsCollector()
                .size());
        Assert.assertEquals("Unexpected value", expectedAVGDelay, collector.getRealCalls().get(0).getAverageDelay());
        Assert.assertEquals("Unexpected value", expectedAVGLQ, collector.getRealCalls().get(0).getAverageLQ());
        LOGGER.info("testEventCollectorIndividual finished in " + (System.currentTimeMillis() - begin));
    }

    /**
     * check event collector of SDS call
     */
    @Test
    public void testEventCollectorSDS() {
        long begin = System.currentTimeMillis();
        final int expectedSDSCallCount = 1;
        final int expectedEventsCount = 2;
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME + SDS_NAME);
        TNSElement tns = generateFile();
        tns.addMembertoEventsList(generateEventSDS());
        EventsElement event = dg.generateEventsElement();
        RecieveMsg recieveMsg = dg.generateRecieveMsg();
        event.setSendRecieveMsg(recieveMsg);
        tns.addMembertoEventsList(event);
        tns.addMembertoCommonTestList(generateCommonTestData());
        tns.addMembertoGPSList(generateGpsData());

        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        TNSElement tns2 = parser.parse(DIRECTORY_PATH + FILE_NAME + SDS_NAME + "#" + xmlWriter.getTime() + XML_EXTENSION);
        CallPreparator preparator = new CallPreparator();

        CallCollector collector = preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(), tns2.getEvents(),
                tns2.getGps());

        Assert.assertEquals("Unexpected collector size", expectedSDSCallCount, collector.getSDSCalls().size());
        Assert.assertEquals("Unexpected event size", expectedEventsCount, collector.getSDSCalls().get(0).getEventsCollector()
                .size());
        LOGGER.info("testEventCollectorSDS finished in " + (System.currentTimeMillis() - begin));
    }

    /**
     * check event collector of handover call
     */
    @Test
    public void testEventCollectorHandover() {
        long begin = System.currentTimeMillis();
        final int expectedHandoverCallCount = 1;
        final int expectedEventsCount = 1;
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME + HANDOVER_NAME);
        TNSElement tns = generateFile();
        tns.addMembertoEventsList(generateEventIndividual());
        EventsElement event = dg.generateEventsElement();
        Handover handover = dg.generateHandover();
        event.setHandover(handover);
        tns.addMembertoEventsList(event);
        tns.addMembertoCommonTestList(generateCommonTestData());
        tns.addMembertoGPSList(generateGpsData());

        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        TNSElement tns2 = parser.parse(DIRECTORY_PATH + FILE_NAME + HANDOVER_NAME + "#" + xmlWriter.getTime() + XML_EXTENSION);
        CallPreparator preparator = new CallPreparator();

        CallCollector collector = preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(), tns2.getEvents(),
                tns2.getGps());

        Assert.assertEquals("Unexpected collector size", expectedHandoverCallCount, collector.getHandovers().size());
        Assert.assertEquals("Unexpected event size", expectedEventsCount, collector.getHandovers().get(0).getEventsCollector()
                .size());
        Handover newhandover = collector.getHandovers().get(0).getEventsCollector().get(0).getHandover();
        Assert.assertEquals("Unexpected value", handover, newhandover);
        LOGGER.info("testEventCollectorHandover finished in " + (System.currentTimeMillis() - begin));
    }

    /**
     * check event collector of itsi attach call
     */
    @Test
    public void testEventCollectorItsiAttach() {
        long begin = System.currentTimeMillis();
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME + ITSI_ATTACH_NAME);
        TNSElement tns = generateFile();
        EventsElement event = generateEventItsiAttach();
        tns.addMembertoEventsList(event);
        final long EXPECTED_UPDATE_TIME = event.getItsiAttach().getItsiAccept().getTimeInMillis()
                - event.getItsiAttach().getItsiAttReq().getTimeInMillis();

        tns.addMembertoCommonTestList(generateCommonTestData());
        tns.addMembertoGPSList(generateGpsData());

        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        TNSElement tns2 = parser.parse(DIRECTORY_PATH + FILE_NAME + ITSI_ATTACH_NAME + "#" + xmlWriter.getTime() + XML_EXTENSION);
        CallPreparator preparator = new CallPreparator();

        CallCollector collector = preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(), tns2.getEvents(),
                tns2.getGps());

        Assert.assertTrue("Unexpected value", EXPECTED_UPDATE_TIME == collector.getItsiAttachCalls().get(0).getCallDuration());
        LOGGER.info("testEventCollectorResel finished in " + (System.currentTimeMillis() - begin));

    }

    /**
     * check event collector of reselection call
     */
    @Test
    public void testEventCollectorResel() {
        long begin = System.currentTimeMillis();
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME + CELL_RESELECTION_NAME);
        TNSElement tns = generateFile();
        EventsElement event = generateEventRecelection();
        tns.addMembertoEventsList(event);
        final long EXPECTED_RESELECTION_TIME = event.getCellReselection().getCellReselAccept().getTimeInMillis()
                - event.getCellReselection().getCellReselReq().getTimeInMillis();
        tns.addMembertoCommonTestList(generateCommonTestData());
        tns.addMembertoGPSList(generateGpsData());

        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        TNSElement tns2 = parser.parse(DIRECTORY_PATH + FILE_NAME + CELL_RESELECTION_NAME + "#" + xmlWriter.getTime()
                + XML_EXTENSION);
        CallPreparator preparator = new CallPreparator();
        CallCollector collector = preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(), tns2.getEvents(),
                tns2.getGps());

        Assert.assertTrue("Unexpected value", EXPECTED_RESELECTION_TIME == collector.getCellResels().get(0).getCellReselection());
        LOGGER.info("testEventCollectorResel finished in " + (System.currentTimeMillis() - begin));

    }
    //
    // /**
    // * check collector SAVER of Individual Call;
    // */
    // @Test
    // public void testEventCollectorSaverIndividual() {
    // long begin = System.currentTimeMillis();
    //
    // xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME + SAVER_NAME);
    // TNSElement tns = generateFile();
    // tns.addMembertoEventsList(generateEventIndividual());
    // EventsElement event = dg.generateEventsElement();
    // TTCElement ttc = dg.generateTTCElement();
    // ttc.setPesqResult(generatePredefinedPesq());
    // event.setTocttc(ttc);
    // tns.addMembertoEventsList(event);
    // event = dg.generateEventsElement();
    // event.setTocttc(dg.generateTTCElement());
    // tns.addMembertoEventsList(event);
    // tns.addMembertoCommonTestList(generateCommonTestData());
    // tns.addMembertoGPSList(generateGpsData());
    //
    // xmlWriter.buildTree(tns);
    // xmlWriter.saveFile();
    // List<File> files = fileloader.getRootsFiles(root);
    // AMSXMLParser parser = new AMSXMLParser();
    // TNSElement tns2;
    //
    // AMSXMLSaver saver = new AMSXMLSaver("project", root.getName());
    // CallPreparator preparator = new CallPreparator();
    // CallCollector collector;
    // for (File currentFile : files) {
    // tns2 = parser.parse(currentFile);
    // if (tns2 != null) {
    // collector =
    // preparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(),tns2.getEvents(),
    // tns2.getGps(), tns2.getCtd().get(0).getNtpq());
    // saver.saveCallColection(collector);
    // }
    // }
    // LOGGER.info("testEventCollectorSaverIndividual finished in " + (System.currentTimeMillis() -
    // begin));
    // }
}
