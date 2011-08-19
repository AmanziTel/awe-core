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

public class DataGenerator {

    private Calendar calendar = Calendar.getInstance();
    private int dateSecond = Calendar.SECOND;

    public CommonTestData generateCommonTestData() {
        CommonTestData data = new CommonTestData();
        data.setValueByTagType(ChildTypes.PROBE_ID_NUMBER_MAP.getId(), generateProbeIDNumberMap());
        data.setValueByTagType(ChildTypes.SERVING_DATA.getId(), generateServingData());
        return data;
    }

    private ServingData generateServingData() {
    	ServingData servingData = new ServingData();
    	servingData.setFrequency(Math.random() * 100);
    	servingData.setDeliveryTime(calendar);
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
        data.setValueByTagType(ChildTypes.COMPLEATE_GPS_DATA_LIST.getId(), generateCompleateGPSDataList());

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
        cgd.setDeliveryTime(calendar);
        cgd.setProbeId(LoaderConstants.PROBE_ID + (int)Math.round(Math.random() * 100));
        Double lat = Math.random() * 10000;
        Double lon = Math.random() * 100000;

        lat = Double.parseDouble(lat.toString().substring(0, lat.toString().lastIndexOf(".") + 5));
        lon = Double.parseDouble(lon.toString().substring(0, lon.toString().lastIndexOf(".") + 5));
       
        double minNorth = Math.random();
        double minWest = Math.random();
        double speed = Math.random();
        double courseMade = Math.random();
        double magnaticVariation = Math.random();
        int checkSum = (int)Math.round(Math.random() * 100);
        cgd.setLocation("GPGLL", lat, lon, calendar, minNorth, minWest, speed, courseMade, calendar, magnaticVariation, checkSum);
        return cgd;

    }

    public PESQResultElement generatePESQ() {
        PESQResultElement pesq = new PESQResultElement();
        pesq.setDelay(Math.round((Math.random() * 100)));
        pesq.setPesq((float)Math.random() * 100);
        pesq.setSendSampleStart(calendar);
        return pesq;
    }

    public AbstractTOCTTC generateEventChild(ChildTypes childType) {
        switch (childType) {
        case TOC:
            return generateEventChild(new TOCElement());
        case TTC:
            return generateEventChild(new TTCElement());
        default:
            return null;
        }

    }

    public AbstractTOCTTC generateEventChild(AbstractTOCTTC eventChild) {
        eventChild.setCalledNumber("" + calendar.getTime().getTime());
        eventChild.setCauseForTermination((int)Math.round(Math.random() * 10));
        calendar.set(Calendar.SECOND, dateSecond += 1);
        eventChild.setConfigTime(calendar);
        calendar.set(Calendar.SECOND, dateSecond += 1);
        eventChild.setConnectTime(calendar);
        calendar.set(Calendar.SECOND, dateSecond += 1);
        eventChild.setDisconnectTime(calendar);
        calendar.set(Calendar.SECOND, dateSecond += 1);
        eventChild.setHook((int)Math.round(Math.random() * 10));
        eventChild.setProbeID("PROBE" + Math.round(Math.random() * 100));
        calendar.set(Calendar.SECOND, dateSecond += 1);
        eventChild.setReleaseTime(calendar);
        calendar.set(Calendar.SECOND, dateSecond += 1);
        eventChild.setSetupTime(calendar);
        eventChild.setSimplex((int)Math.round(Math.random() * 10));
        eventChild.addPesqMember(generatePESQ());
        eventChild.addPesqMember(generatePESQ());
        return eventChild;
    }

    public EventsElement generateEventsElement() {
        EventsElement eventElement = new EventsElement(generateEventChild(ChildTypes.TOC), generateEventChild(ChildTypes.TTC));
        return eventElement;
    }
}
