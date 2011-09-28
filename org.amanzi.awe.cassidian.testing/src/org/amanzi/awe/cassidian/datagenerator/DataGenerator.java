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
package org.amanzi.awe.cassidian.datagenerator;

import java.util.Calendar;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;
import org.amanzi.awe.cassidian.structure.*;

/**
 * TODO move to test plugin.
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class DataGenerator {

    private Calendar calendar = Calendar.getInstance();

    public CommonTestData generateCommonTestData() {
        CommonTestData data = new CommonTestData();
        return data;
    }

    private Long generateLongDate() {
        Long milis = Math.round(Math.random() * 100000 + Calendar.getInstance().getTimeInMillis());
        return milis;
    }

    private Calendar generateCalendar() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(generateLongDate());
        return calendar;
    }

    public NeighborData generateNeighborData() {
        NeighborData nd = new NeighborData();

        nd.setDeliveryTime(generateCalendar());
        nd.setProbeId("Probe" + Math.round(Math.random() * 100));
        return nd;
    }

    public Ntpq generateNtpq() {
        Ntpq ntpq = new Ntpq();
        ntpq.setJitter(Math.round(Math.random() * 100));
        ntpq.setOffset(Math.round(Math.random() * 100));
        ntpq.setNtpqTime(generateCalendar());
        ntpq.setProbeId("Probe" + Math.round(Math.random() * 100));
        return ntpq;
    }

    public ItsiAttach geneItsiAttach() {
        ItsiAttach itsi = new ItsiAttach();
        itsi.setItsiAccept(generateCalendar());
        itsi.setItsiAttReq(generateCalendar());
        itsi.setLocationAreaAfter(Math.round(Math.random() * 100000));
        itsi.setLocationAreaBefore(Math.round(Math.random() * 100000));
        itsi.setProbeId("Probe " + Math.round(Math.random() * 100));
        return itsi;
    }

    public MPTSync generateMptSync() {
        MPTSync mptsync = new MPTSync();
        mptsync.setMptSyncTime(generateCalendar());
        mptsync.addMemberToProbeList(generateCalendar());
        mptsync.setSyncId((long)Math.round(Math.random() * 1000000));
        mptsync.setTimeOut((long)Math.round(Math.random() * 1000000));
        mptsync.setProbeId("Probe" + Math.round(Math.random() * 100));
        return mptsync;
    }

    public NeighborDetails generateNeighborDetails() {
        NeighborDetails nd = new NeighborDetails();
        nd.setC2((int)Math.round(Math.random() * 100));
        nd.setFrequency(Math.round(Math.random() * 100));
        nd.setRssi((int)Math.round(Math.random() * 10000));
        return nd;
    }

    public ServingData generateServingData() {
        ServingData servingData = new ServingData();
        servingData.setFrequency(Math.random() * 100);
        servingData.setDeliveryTime(generateCalendar());
        servingData.setProbeId("Probe" + Math.round(Math.random() * 100));
        servingData.setLocationArea((int)Math.round(Math.random() * 10000));
        servingData.setRssi((int)Math.round(Math.random() * 10000));
        servingData.setCl((int)Math.round(Math.random() * 10000));
        return servingData;
    }

    public ProbeIDNumberMap generateProbeIDNumberMap() {
        ProbeIDNumberMap pinm = new ProbeIDNumberMap();
        pinm.setFrequency(Math.random() * 100);
        pinm.setPhoneNumber((int)Math.round(Math.random() * 1000000));
        pinm.setProbeId("Probe" + Math.round(Math.random() * 100));
        pinm.setLocationArea((int)Math.round(Math.random() * 10000));
        return pinm;
    }

    public GPSData generateGPSDATA() {
        GPSData data = new GPSData();
        return data;
    }

    public CompleteGpsDataList generateCompleateGPSDataList() {
        CompleteGpsDataList datalist = new CompleteGpsDataList();
        datalist.setValueByTagType(ChildTypes.COMPLEATE_GPS_DATA.getId(), generateCompleateGPSData());
        datalist.setValueByTagType(ChildTypes.COMPLEATE_GPS_DATA.getId(), generateCompleateGPSData());
        datalist.setValueByTagType(ChildTypes.COMPLEATE_GPS_DATA.getId(), generateCompleateGPSData());
        return datalist;
    }

    public CompleteGpsData generateCompleateGPSData() {
        CompleteGpsData cgd = new CompleteGpsData();

        cgd.setDeliveryTime(generateCalendar());
        cgd.setProbeId(LoaderConstants.PROBE_ID + (int)Math.round(Math.random() * 100));
        Double lat = Math.random() * 10000;
        Double lon = Math.random() * 10000;

        lat = Double.parseDouble(lat.toString().substring(0, lat.toString().lastIndexOf(".") + 5));
        lon = Double.parseDouble(lon.toString().substring(0, lon.toString().lastIndexOf(".") + 5));

        double minNorth = Math.random();
        double minWest = Math.random();
        double speed = Math.random();
        double courseMade = Math.random();
        double magnaticVariation = Math.random();
        int checkSum = (int)Math.round(Math.random() * 100);
        cgd.setLocation("GPGLL", lat, lon, generateCalendar(), minNorth, minWest, speed, courseMade, generateCalendar(),
                magnaticVariation, checkSum);
        return cgd;

    }

    public PESQResultElement generatePESQ() {
        PESQResultElement pesq = new PESQResultElement();
        pesq.setDelay(Math.round((Math.random() * 100)));
        pesq.setPesq((float)Math.random() * 100);

        pesq.setSendSampleStart(generateCalendar());
        return pesq;
    }

    public SendMsg generateSendMsg() {
        SendMsg sm = new SendMsg();
        sm.setCalledNumber((long)Math.round(Math.random() * 10000));
        sm.setDataLength((int)Math.round(Math.random() * 100));
        sm.setDataTxt((int)Math.round(Math.random() * 100));
        sm.setMsgType((int)Math.round(Math.random() * 100));

        sm.setSendTime(generateCalendar());
        sm.setProbeId("ProbeId " + (int)Math.round(Math.random() * 100));
        return sm;
    }

    public CellReselection generateCellReselection() {
        CellReselection cell = new CellReselection();

        cell.setCellReselAccept(generateCalendar());
        cell.setLocationAreaAfter((long)Math.round(Math.random() * 10000));

        cell.setCellReselReq(generateCalendar());
        cell.setLocationAreaBefore((long)Math.round(Math.random() * 10000));
        cell.setProbeId("Probe id " + (long)Math.round(Math.random() * 10000));
        return cell;
    }

    public RecieveMsg generateRecieveMsg() {
        RecieveMsg rm = new RecieveMsg();
        rm.setCallingNumber((long)Math.round(Math.random() * 10000));
        rm.setDataLength((int)Math.round(Math.random() * 100));
        rm.setDataTxt((int)Math.round(Math.random() * 100));
        rm.setMsgType((int)Math.round(Math.random() * 100));

        rm.setSendTime(generateCalendar());
        rm.setProbeId("ProbeId " + (int)Math.round(Math.random() * 100));
        return rm;
    }

    public SendReport generateSendReport() {
        SendReport sr = new SendReport();

        sr.setReportTime(generateCalendar());
        sr.setStatus((int)Math.round(Math.random() * 100));
        return sr;
    }

    public TOCElement generateTOCElement() {
        TOCElement eventChild = new TOCElement();
        eventChild.setCalledNumber(generateCalendar().getTimeInMillis()+"");
        eventChild.setCauseForTermination((int)Math.round(Math.random()));
        eventChild.setConfigTime(generateCalendar());
        eventChild.setConnectTime(generateCalendar());

        eventChild.setDisconnectTime(generateCalendar());

        eventChild.setHook((int)Math.round(Math.random() * 10));
        eventChild.setProbeId("PROBE" + Math.round(Math.random() * 100));

        eventChild.setReleaseTime(generateCalendar());

        eventChild.setSetupTime(generateCalendar());
        eventChild.setSimplex((int)Math.round(Math.random() * 10));
        eventChild.setPriority((int)Math.round(Math.random() * 10));
        return eventChild;
    }

    public TTCElement generateTTCElement() {
        TTCElement eventChild = new TTCElement();
        eventChild.setCallingNumber(generateCalendar().getTimeInMillis()+"");
        eventChild.setCauseForTermination((int)Math.round(Math.random() * 10));

        eventChild.setAnswerTime(generateCalendar());

        eventChild.setConnectTime(generateCalendar());

        eventChild.setIndicationTime(generateCalendar());

        eventChild.setHook((int)Math.round(Math.random() * 10));
        eventChild.setProbeId("PROBE" + Math.round(Math.random() * 100));

        eventChild.setReleaseTime(generateCalendar());

        eventChild.setSimplex((int)Math.round(Math.random() * 10));

        return eventChild;
    }

    public InconclusiveElement generateInconclusiveElement() {
        InconclusiveElement inconclusiveElement = new InconclusiveElement();
        inconclusiveElement.setErrCode((int)Math.round(Math.random() * 100));
        inconclusiveElement.setReason("Reason " + (int)Math.round(Math.random() * 100));
        return inconclusiveElement;
    }

    public GroupAttach generateGroupAttachElement() {
        GroupAttach ga = new GroupAttach();

        ga.setGroupAttachTime(generateCalendar());
        ga.setProbeId("PROBE" + Math.round(Math.random() * 100));

        return ga;
    }

    public Attachment generateAttachment() {
        Attachment at = new Attachment();
        at.setGroupType((int)Math.round(Math.random() * 100));
        at.setGssi((long)Math.round(Math.random() * 10000));
        return at;
    }

    public EventsElement generateEventsElement() {
        EventsElement eventElement = new EventsElement();
        return eventElement;
    }

    public Handover generateHandover() {
        Handover handover = new Handover();

        handover.setHoAccept(generateCalendar());

        handover.setHoReq(generateCalendar());
        handover.setLocationAreaAfter((long)Math.round(Math.random() * 10000));
        handover.setLocationAreaBefore((long)Math.round(Math.random() * 10000));
        handover.setProbeId("ProbeId " + (int)Math.round(Math.random() * 10));
        return handover;
    }
}
