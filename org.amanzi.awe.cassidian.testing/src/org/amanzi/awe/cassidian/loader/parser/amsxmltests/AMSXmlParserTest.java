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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.loader.FileLoader;
import org.amanzi.awe.cassidian.loader.parser.AMSXMLParser;
import org.amanzi.awe.cassidian.structure.*;
import org.amanzi.awe.cassidian.datagenerator.DataGenerator;
import org.amanzi.awe.cassidian.writer.AMSXMLWriter;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * test for parser
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class AMSXmlParserTest {
    private static DataGenerator dg;
    private static FileLoader fileloader;
    private static File rootDirectory;
    private static final String DIRECTORY_PATH = System.getProperty("user.home") + "/.amanzi/xmlTest/";
    private static final String FILE_NAME = "parsertest";
    private AMSXMLWriter xmlWriter;
    private double generatedLat;
    private double generatedLon;
    private static List<File> xmlFiles;
    public static final Pattern COORD_PAT_LAT = Pattern.compile("^(\\d{2})(\\d{2}\\.\\d{4,5})$");
    public static final Pattern COORD_PAT_LON = Pattern.compile("^(\\d{3})(\\d{2}\\.\\d{4,5})$");
    public static final Calendar calendar = Calendar.getInstance();

    @BeforeClass
    public static void initVariables() throws IOException {
        prepareDirectory();
        fileloader = new FileLoader(DIRECTORY_PATH);
        rootDirectory = fileloader.getRoot();

        dg = new DataGenerator();
    }

    private static void prepareDirectory() throws IOException {
        File file = new File(DIRECTORY_PATH);
        deleteDirectory(file);
        createTempRootDirectory();
    }

    private static void deleteDirectory(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                deleteDirectory(f);
            file.delete();
        } else {
            file.delete();
        }
    }

    private static void createTempRootDirectory() throws IOException {
        File dir1 = new File(DIRECTORY_PATH);
        dir1.mkdir();
        File dir2 = new File(DIRECTORY_PATH + "tempDir1/");
        dir2.mkdir();
        new File(dir2.getAbsolutePath() + "/temp.temp").createNewFile();
        dir2 = new File(DIRECTORY_PATH + "tempDir2/");
        dir2.mkdir();
        new File(dir2.getAbsolutePath() + "/tempXML.xml").createNewFile();
        new File(DIRECTORY_PATH + "/tempXML2.xml").createNewFile();
    }

    private Double parseLat(String latStr) {
        final Matcher matcher = COORD_PAT_LAT.matcher(latStr);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60d;
        } else {

            return null;
        }
    }

    /**
     * Parse longitude.
     * 
     * @param latStr String
     * @return Double
     */
    private Double parseLon(String latStr) {
        final Matcher matcher = COORD_PAT_LON.matcher(latStr);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60d;
        } else {
            return null;
        }
    }

    @Test
    public void testCorrectNumberOfXMLSelection() {
        xmlFiles = fileloader.getRootsFiles(rootDirectory);
        Assert.assertEquals("Invalid expected files count.", xmlFiles.size(), 2);
    }

    @Test
    public void testCreationOfXml() {
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME);
        TNSElement tns = new TNSElement();
        tns.addMembertoEventsList(dg.generateEventsElement());
        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();
        xmlFiles = fileloader.getRootsFiles(rootDirectory);
        boolean consistFlag = false;
        for (File f : xmlFiles) {
            if (f.getName().equals(FILE_NAME + ".xml"))
                consistFlag = true;
        }
        Assert.assertTrue("File not found", consistFlag);
    }

    @Test
    public void testParsingOfXml() {
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME);
        TNSElement tns = new TNSElement();
        GPSData gpsData = generatePredefinedGPSDataTag();
        List<EventsElement> event = generatePredefinedEventsElementTag();
        CommonTestData ctd = generatePredefinedCTDTag();

        tns.setEvents(event);
        tns.addMembertoGPSList(gpsData);
        tns.addMembertoCommonTestList(ctd);
        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        TNSElement tns2 = parser.parse(new File(DIRECTORY_PATH + FILE_NAME + xmlWriter.getTime() + ".xml"));
        TOCElement toc2 = (TOCElement)tns2.getEvents().get(0).getTocttc();
        TTCElement ttc2 = (TTCElement)tns2.getEvents().get(1).getTocttc();
        ItsiAttach itsi2 = tns2.getEvents().get(4).getItsiAttach();
        SendMsg sendmsg = (SendMsg)tns2.getEvents().get(3).getSendRecieveMsg();
        RecieveMsg recimsg = (RecieveMsg)tns2.getEvents().get(2).getSendRecieveMsg();
        GroupAttach ga2 = tns2.getEvents().get(5).getGroupAttach();
        CompleteGpsData cgd2 = tns2.getGps().get(0).getCompleteGpsDataList().get(0).getCompleteGpsData().get(0);
        CommonTestData ctd2 = tns2.getCtd().get(0);
        CellReselection cell = tns2.getEvents().get(6).getCellReselection();
        Handover handover = tns2.getEvents().get(7).getHandover();

        checkHandvoer(tns.getEvents().get(7).getHandover(), handover);
        checkCellReselection(tns.getEvents().get(6).getCellReselection(), cell);
        checkItsiAttach(event.get(4).getItsiAttach(), itsi2);
        checkSendMsg((SendMsg)event.get(3).getSendRecieveMsg(), sendmsg);
        checkReciveMsg((RecieveMsg)event.get(2).getSendRecieveMsg(), recimsg);
        checkGroupAttach(event.get(5).getGroupAttach(), ga2);
        checkTOCElement((TOCElement)event.get(0).getTocttc(), toc2, event.get(0).getTocttc().getPesqResult().get(0));
        checkTTCElement((TTCElement)event.get(1).getTocttc(), ttc2, event.get(1).getTocttc().getPesqResult().get(0), event.get(1)
                .getTocttc().getInconclusive());
        checkCommonTestData(ctd, ctd2.getProbeIdNumberMap().get(0), ctd2.getServingData().get(0), ctd2.getNeighborDatas().get(0),
                ctd2.getNtpq().get(0), ctd2.getMptsync().get(0));
        checkCompleateGPSData(gpsData.getCompleteGpsDataList().get(0).getCompleteGpsData().get(0), cgd2, generatedLat, generatedLon);

    }

    /**
     * @param handover
     * @param handover2
     */
    private void checkHandvoer(Handover hanvdover, Handover extractedHandover) {
        Assert.assertEquals("Parsed and generated objects are not equals", hanvdover.getProbeId(), extractedHandover.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", hanvdover.getHoAccept(), extractedHandover.getHoAccept());
        Assert.assertEquals("Parsed and generated objects are not equals", hanvdover.getHoReq(), extractedHandover.getHoReq());
        Assert.assertEquals("Parsed and generated objects are not equals", hanvdover.getLocationAreaAfter(),
                extractedHandover.getLocationAreaAfter());
        Assert.assertEquals("Parsed and generated objects are not equals", hanvdover.getLocationAreaBefore(),
                extractedHandover.getLocationAreaBefore());
    }

    /**
     * @param cellReselection
     * @param cell
     */
    private void checkCellReselection(CellReselection cellReselection, CellReselection cell) {
        Assert.assertEquals("Parsed and generated objects are not equals", cellReselection.getProbeId(), cell.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", cellReselection.getCellReselAccept(),
                cell.getCellReselAccept());
        Assert.assertEquals("Parsed and generated objects are not equals", cellReselection.getCellReselReq(),
                cell.getCellReselReq());
        Assert.assertEquals("Parsed and generated objects are not equals", cellReselection.getLocationAreaAfter(),
                cell.getLocationAreaAfter());
        Assert.assertEquals("Parsed and generated objects are not equals", cellReselection.getLocationAreaBefore(),
                cell.getLocationAreaBefore());
    }

    /**
     * @param itsiAttach
     * @param itsi2
     */
    private void checkItsiAttach(ItsiAttach itsiAttach, ItsiAttach itsi2) {
        Assert.assertEquals("Parsed and generated objects are not equals", itsiAttach.getProbeId(), itsi2.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", itsiAttach.getItsiAccept(), itsi2.getItsiAccept());
        Assert.assertEquals("Parsed and generated objects are not equals", itsiAttach.getItsiAttReq(), itsi2.getItsiAttReq());
        Assert.assertEquals("Parsed and generated objects are not equals", itsiAttach.getLocationAreaAfter(),
                itsi2.getLocationAreaAfter());
        Assert.assertEquals("Parsed and generated objects are not equals", itsiAttach.getLocationAreaBefore(),
                itsi2.getLocationAreaBefore());
    }

    /**
     * @param sendMsg
     * @param sendmsg2
     */
    private void checkSendMsg(SendMsg sendMsg, SendMsg sendmsg2) {
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getProbeId(), sendmsg2.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getCalledNumber(), sendmsg2.getCalledNumber());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getDataLength(), sendmsg2.getDataLength());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getDataTxt(), sendmsg2.getDataTxt());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getInconclusive().getReason(), sendmsg2
                .getInconclusive().getReason());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getInconclusive().getErrCode(), sendmsg2
                .getInconclusive().getErrCode());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getMsgType(), sendmsg2.getMsgType());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getSendTime(), sendmsg2.getSendTime());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getSendReport().get(0).getReportTime(), sendmsg2
                .getSendReport().get(0).getReportTime());
        Assert.assertEquals("Parsed and generated objects are not equals", sendMsg.getSendReport().get(0).getStatus(), sendmsg2
                .getSendReport().get(0).getStatus());
    }

    /**
     * @param recieveMsgExpected
     * @param recieveMsgParsed
     */
    private void checkReciveMsg(RecieveMsg recieveMsgExpected, RecieveMsg recieveMsgParsed) {
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getProbeId(),
                recieveMsgParsed.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getCallingNumber(),
                recieveMsgParsed.getCallingNumber());
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getDataLength(),
                recieveMsgParsed.getDataLength());
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getDataTxt(),
                recieveMsgParsed.getDataTxt());
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getInconclusive().getReason(),
                recieveMsgParsed.getInconclusive().getReason());
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getInconclusive().getErrCode(),
                recieveMsgParsed.getInconclusive().getErrCode());
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getMsgType(),
                recieveMsgParsed.getMsgType());
        Assert.assertEquals("Parsed and generated objects are not equals", recieveMsgExpected.getSendTime(),
                recieveMsgParsed.getSendTime());
    }

    private void checkTTCElement(TTCElement ttc, TTCElement parsedTTC, PESQResultElement pesqres, InconclusiveElement inconclusive) {
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getProbeID(), parsedTTC.getProbeID());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getCallingNumber(), parsedTTC.getCallingNumber());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getCauseForTermination(),
                parsedTTC.getCauseForTermination());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getAnswerTime(), parsedTTC.getAnswerTime());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getConnectTime(), parsedTTC.getConnectTime());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getIndicationTime(), parsedTTC.getIndicationTime());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getHook(), parsedTTC.getHook());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getReleaseTime(), parsedTTC.getReleaseTime());
        Assert.assertEquals("Parsed and generated objects are not equals", ttc.getSimplex(), parsedTTC.getSimplex());

        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getDelay(), parsedTTC.getPesqResult().get(0)
                .getDelay());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getPesq(), parsedTTC.getPesqResult().get(0)
                .getPesq());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getSendSampleStart(), parsedTTC.getPesqResult()
                .get(0).getSendSampleStart());

        Assert.assertEquals("Parsed and generated objects are not equals", inconclusive.getReason(), parsedTTC.getInconclusive()
                .getReason());
        Assert.assertEquals("Parsed and generated objects are not equals", inconclusive.getErrCode(), parsedTTC.getInconclusive()
                .getErrCode());
    }

    private void checkGroupAttach(GroupAttach groupAttach, GroupAttach ga2) {
        Assert.assertEquals("Parsed and generated objects are not equals", groupAttach.getProbeId(), ga2.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", groupAttach.getGroupAttachTime(),
                ga2.getGroupAttachTime());

        Assert.assertEquals("Parsed and generated objects are not equals", groupAttach.getAttachment().get(0).getGroupType(), ga2
                .getAttachment().get(0).getGroupType());
        Assert.assertEquals("Parsed and generated objects are not equals", groupAttach.getAttachment().get(0).getGssi(), ga2
                .getAttachment().get(0).getGssi());
    }

    private NeighborData generatrPredifinedNeighborData() {
        NeighborData nd = new NeighborData();
        nd.setDeliveryTime(calendar);
        nd.setProbeId("probeId 11");
        nd.addMemberToNeighborDetails(generatePredifinedNeighborDetails());
        return nd;
    }

    private NeighborDetails generatePredifinedNeighborDetails() {
        NeighborDetails nd2 = new NeighborDetails();
        nd2.setC2(2);
        nd2.setFrequency(123d);
        nd2.setRssi(124);
        return nd2;
    }

    private SendMsg generatePredifinedSendMsg() {
        SendMsg sm = new SendMsg();
        sm.setCalledNumber(1l);
        sm.setDataLength(2);
        sm.setDataTxt(3);
        sm.setInconclusive(generatePredefinedInconclusiveElement());
        sm.setMsgType(4);
        sm.setProbeId("Probe pp");
        sm.setSendTime(calendar);
        sm.addMemberToSendReport(generateSendReport());
        return sm;
    }

    private RecieveMsg generatePredifinedRecieveMsg() {
        RecieveMsg rm = new RecieveMsg();
        rm.setCallingNumber(1l);
        rm.setDataLength(2);
        rm.setDataTxt(3);
        rm.setInconclusive(generatePredefinedInconclusiveElement());
        rm.setMsgType(4);
        rm.setProbeId("Probe pp2");
        rm.setSendTime(calendar);
        return rm;
    }

    private SendReport generateSendReport() {
        SendReport sr = new SendReport();
        sr.setReportTime(calendar);
        sr.setStatus(4);
        return sr;
    }

    private CommonTestData generatePredefinedCTDTag() {
        CommonTestData ctd = new CommonTestData();
        ServingData servingData = new ServingData();
        servingData.setCl(123);
        servingData.setDeliveryTime(calendar);
        servingData.setFrequency(12.3d);
        servingData.setLocationArea(321);
        servingData.setProbeId("ProbeId 1");
        servingData.setRssi(222);

        ProbeIDNumberMap pidnm = new ProbeIDNumberMap();
        pidnm.setFrequency(11.0d);
        pidnm.setLocationArea(444);
        pidnm.setPhoneNumber(21);
        pidnm.setProbeId("ProbeId 555");

        ctd.addMemberToServingData(servingData);
        ctd.addMemberToProbeIdNumberMap(pidnm);
        ctd.addMembertoNeighborData(generatrPredifinedNeighborData());
        ctd.addMemberToNtpq(generatePredifinedNtpq());
        ctd.addMemberToMPTSync(generatePredefinedMPTSync());
        return ctd;
    }

    private Ntpq generatePredifinedNtpq() {
        Ntpq ntpq = new Ntpq();
        ntpq.setJitter(33.2d);
        ntpq.setNtpqTime(calendar);
        ntpq.setProbeId("Probe nt");
        ntpq.setOffset(1233d);
        return ntpq;
    }

    private List<EventsElement> generatePredefinedEventsElementTag() {
        List<EventsElement> eventList = new LinkedList<EventsElement>();
        EventsElement event = new EventsElement();
        event.setTocttc(generatePredefinedTOClement(false));
        eventList.add(event);

        event = new EventsElement();
        event.setTocttc(generatePredefinedTTClement(true));
        eventList.add(event);

        event = new EventsElement();
        event.setSendRecieveMsg(generatePredifinedRecieveMsg());
        eventList.add(event);

        event = new EventsElement();
        event.setSendRecieveMsg(generatePredifinedSendMsg());
        eventList.add(event);

        event = new EventsElement();
        event.setItsiAttach(generatePredifinedItsiAttach());
        eventList.add(event);

        event = new EventsElement();
        event.setGroupAttach(generatePredefinedGroupAttachTag());
        eventList.add(event);

        event = new EventsElement();
        event.setCellReselection(generatePredefinedCellReselection());
        eventList.add(event);

        event = new EventsElement();
        event.setHandover(generatePredefinedHandover());
        eventList.add(event);
        return eventList;

    }

    /**
     * @return
     */
    private Handover generatePredefinedHandover() {
        Handover handover = new Handover();
        handover.setHoAccept(calendar);
        handover.setHoReq(calendar);
        handover.setLocationAreaAfter(12l);
        handover.setLocationAreaBefore(66l);
        handover.setProbeId("Probe handover");
        return handover;
    }

    /**
     * @return
     */
    private CellReselection generatePredefinedCellReselection() {
        CellReselection cell = new CellReselection();
        cell.setCellReselAccept(calendar);
        cell.setCellReselReq(calendar);
        cell.setLocationAreaAfter(22l);
        cell.setLocationAreaBefore(11l);
        cell.setProbeId("Probe reselection");
        return cell;
    }

    /**
     * @return
     */
    private ItsiAttach generatePredifinedItsiAttach() {
        ItsiAttach itsi = new ItsiAttach();
        itsi.setProbeId("probe itsi");
        itsi.setLocationAreaBefore(2222l);
        itsi.setLocationAreaAfter(11111l);
        itsi.setItsiAccept(calendar);
        itsi.setItsiAttReq(calendar);
        return itsi;
    }

    private TOCElement generatePredefinedTOClement(boolean inconclusive) {
        TOCElement toc = new TOCElement();

        toc.setCalledNumber("023344");
        toc.setCauseForTermination(1);
        toc.setConfigTime(calendar);
        toc.setConnectTime(calendar);
        toc.setDisconnectTime(calendar);
        toc.setHook(2);
        toc.setPriority(3);
        toc.setProbeId("Probe1");
        toc.setReleaseTime(calendar);
        toc.setSetupTime(calendar);
        toc.setSimplex(4);
        if (inconclusive) {
            toc.setInconclusive(generatePredefinedInconclusiveElement());
        }
        PESQResultElement pesqres = new PESQResultElement();
        pesqres.setDelay(Long.parseLong("1111"));
        pesqres.setSendSampleStart(calendar);
        pesqres.setPesq(Float.parseFloat("33.0"));
        toc.addPesqMember(pesqres);
        return toc;
    }

    private TTCElement generatePredefinedTTClement(boolean inconclusive) {
        TTCElement ttc = new TTCElement();

        ttc.setCallingNumber("023344");
        ttc.setCauseForTermination(1);
        ttc.setAnswerTime(calendar);
        ttc.setConnectTime(calendar);
        ttc.setIndicationTime(calendar);
        ttc.setHook(2);
        ttc.setProbeId("Probe1");
        ttc.setReleaseTime(calendar);
        ttc.setSimplex(4);
        if (inconclusive) {
            ttc.setInconclusive(generatePredefinedInconclusiveElement());
        }
        PESQResultElement pesqres = new PESQResultElement();
        pesqres.setDelay(Long.parseLong("1111"));
        pesqres.setSendSampleStart(calendar);
        pesqres.setPesq(Float.parseFloat("33.0"));
        ttc.addPesqMember(pesqres);
        return ttc;
    }

    private InconclusiveElement generatePredefinedInconclusiveElement() {
        InconclusiveElement inc = new InconclusiveElement();
        inc.setErrCode(222);
        inc.setReason("test inconclusive");
        return inc;
    }

    private GroupAttach generatePredefinedGroupAttachTag() {
        GroupAttach gr = new GroupAttach();
        gr.setGroupAttachTime(calendar);
        gr.setProbeId("Probe gr");
        gr.addMemberToAttachment(generatePredefinedAttachmentTag());

        return gr;
    }

    private Attachment generatePredefinedAttachmentTag() {
        Attachment att = new Attachment();
        att.setGroupType(12);
        att.setGssi(444l);
        return att;
    }

    private MPTSync generatePredefinedMPTSync() {
        MPTSync mptSync = new MPTSync();
        mptSync.setMptSyncTime(calendar);
        mptSync.setProbeId("Probe sync");
        mptSync.setSyncId(111111l);
        mptSync.setTimeOut(22222l);
        List<Object> ob = new LinkedList<Object>();
        ob.add(calendar);
        mptSync.setProbeList(ob);
        return mptSync;
    }

    private GPSData generatePredefinedGPSDataTag() {
        GPSData gpsData = new GPSData();
        CompleteGpsDataList cgdl = new CompleteGpsDataList();
        CompleteGpsData cgd = new CompleteGpsData();
        cgd.setDeliveryTime(calendar);
        cgd.setProbeId("Probe12");
        String type = "GPGLL";
        Double lat = 1234.5678;
        generatedLat = lat;
        Double lon = 22333.1112;
        generatedLon = lon;
        double minNorth = 12.0;
        double minWest = 13.0;
        double speed = 14.0;
        double courseMade = 15.0;
        double magnaticVariation = 100.0;
        int checkSum = 55;
        cgd.setLocation(type, lat, lon, calendar, minNorth, minWest, speed, courseMade, calendar, magnaticVariation, checkSum);
        cgdl.setValueByTagType(ChildTypes.COMPLEATE_GPS_DATA.getId(), cgd);
        gpsData.addMember(cgdl);
        return gpsData;
    }

    private void checkCommonTestData(CommonTestData ctd, ProbeIDNumberMap expectedProbeIdNumMap, ServingData expectedServingData,
            NeighborData expectedNeighbor, Ntpq ntpq, MPTSync mptSync) {
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getProbeId(),
                expectedProbeIdNumMap.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getFrequency(),
                expectedProbeIdNumMap.getFrequency());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getLocationArea(),
                expectedProbeIdNumMap.getLocationArea());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getPhoneNumber(),
                expectedProbeIdNumMap.getPhoneNumber());

        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNeighborDatas().get(0).getProbeId(),
                expectedNeighbor.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNeighborDatas().get(0).getDeliveryTime(),
                expectedNeighbor.getDeliveryTime());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNeighborDatas().get(0).getNeighborDetails()
                .get(0).getC2(), expectedNeighbor.getNeighborDetails().get(0).getC2());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNeighborDatas().get(0).getNeighborDetails()
                .get(0).getFrequency(), expectedNeighbor.getNeighborDetails().get(0).getFrequency());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNeighborDatas().get(0).getNeighborDetails()
                .get(0).getRssi(), expectedNeighbor.getNeighborDetails().get(0).getRssi());

        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getProbeId(),
                expectedProbeIdNumMap.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getFrequency(),
                expectedProbeIdNumMap.getFrequency());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getLocationArea(),
                expectedProbeIdNumMap.getLocationArea());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getProbeIdNumberMap().get(0).getPhoneNumber(),
                expectedProbeIdNumMap.getPhoneNumber());

        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNtpq().get(0).getProbeId(), ntpq.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNtpq().get(0).getJitter(), ntpq.getJitter());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNtpq().get(0).getNtpqTime(), ntpq.getNtpqTime());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getNtpq().get(0).getOffset(), ntpq.getOffset());

        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getMptsync().get(0).getProbeId(),
                mptSync.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getMptsync().get(0).getMptSyncTime(),
                mptSync.getMptSyncTime());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getMptsync().get(0).getSyncId(), mptSync.getSyncId());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getMptsync().get(0).getProbeList(),
                mptSync.getProbeList());
        Assert.assertEquals("Parsed and generated objects are not equals", ctd.getMptsync().get(0).getTimeOut(),
                mptSync.getTimeOut());
    }

    private void checkCompleateGPSData(CompleteGpsData cgd, CompleteGpsData parsedCompleateGPSData, Double lat, Double lon) {
        double expectedLat = parseLat(lat.toString());
        double expectedLon = parseLon(lon.toString());
        Assert.assertEquals("Parsed and generated objects are not equals", expectedLat, parsedCompleateGPSData.getLat());
        Assert.assertEquals("Parsed and generated objects are not equals", expectedLon, -parsedCompleateGPSData.getLon());
        Assert.assertEquals("Parsed and generated objects are not equals", cgd.getProbeId(), parsedCompleateGPSData.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", cgd.getDeliveryTime(),
                parsedCompleateGPSData.getDeliveryTime());
    }

    private void checkTOCElement(TOCElement toc, TOCElement parsedTOC, PESQResultElement pesqres) {
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getProbeID(), parsedTOC.getProbeID());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getCalledNumber(), parsedTOC.getCalledNumber());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getCauseForTermination(),
                parsedTOC.getCauseForTermination());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getConfigTime(), parsedTOC.getConfigTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getConnectTime(), parsedTOC.getConnectTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getDisconnectTime(), parsedTOC.getDisconnectTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getHook(), parsedTOC.getHook());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getPriority(), parsedTOC.getPriority());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getReleaseTime(), parsedTOC.getReleaseTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getSetupTime(), parsedTOC.getSetupTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getSimplex(), parsedTOC.getSimplex());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getDelay(), parsedTOC.getPesqResult().get(0)
                .getDelay());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getPesq(), parsedTOC.getPesqResult().get(0)
                .getPesq());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getSendSampleStart(), parsedTOC.getPesqResult()
                .get(0).getSendSampleStart());
    }
}
