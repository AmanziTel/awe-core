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

package org.amanzi.neo.loader.core.data.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.loader.core.saver.nemo.TechnologySystems;

/**
 * Class for generate nemo2 file
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class Nemo2Generator {

    // random object
    private Random rand = new Random();

    // min integer value
    public static Integer minIntegerValue = Integer.MIN_VALUE;

    // max integer value
    public static Integer maxIntegerValue = Integer.MAX_VALUE - 1;

    // home property
    protected static final String USER_HOME = "user.home";

    // list of technology systems
    private List<Integer> systems = new ArrayList<Integer>();

    // version
    protected static final String version = "2.01";

    // lon
    public static Double longitude;

    // lat
    public static Double latitude;

    // map for tests
    public static Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

    private FileWriter wr;

    public Nemo2Generator() {
    }

    /**
     * Generate #AG row
     * 
     * @return row
     */
    private List<String> generateAG() {
        List<String> params = new ArrayList<String>();
        Float ag = generateFloat(0, 100);
        params.add(ag.toString());
        return params;
    }

    /**
     * Generate #BF row
     * 
     * @return row
     */
    private List<String> generateBF() {
        List<String> params = new ArrayList<String>();
        String btsFile = "BTS filename";
        params.add(returnWordSoCalled(btsFile));
        return params;
    }

    /**
     * Generate #CI row
     * 
     * @return row
     */
    private List<String> generateCInf() {
        List<String> params = new ArrayList<String>();
        String converterName = "Converter name";
        String converterVersion = "Converter version";
        String convertedFile = "Converted filename";
        params.add(returnWordSoCalled(converterName));
        params.add(returnWordSoCalled(converterVersion));
        params.add(returnWordSoCalled(convertedFile));
        return params;
    }

    /**
     * Generate #CL row
     * 
     * @return row
     */
    private List<String> generateCL() {
        List<String> params = new ArrayList<String>();
        Float cl = generateFloat(0, 100);
        params.add(cl.toString());
        return params;
    }

    /**
     * Generate #DL row
     * 
     * @return row
     */
    private List<String> generateDL() {
        List<String> params = new ArrayList<String>();
        String deviceLabel = "Device label";
        params.add(returnWordSoCalled(deviceLabel));
        return params;
    }

    /**
     * Generate #DN row
     * 
     * @return row
     */
    private List<String> generateDN() {
        List<String> params = new ArrayList<String>();
        String deviceName = "Device name";
        params.add(returnWordSoCalled(deviceName));
        return params;
    }

    /**
     * Generate #DS row
     * 
     * @return row
     */
    private List<String> generateDS() {
        List<String> params = new ArrayList<String>();
        Integer numberOfSystems = TechnologySystems.values().length;
        params.add(numberOfSystems.toString());
        for (TechnologySystems system : TechnologySystems.values()) {
            params.add(system.getId().toString());
        }
        return params;
    }

    /**
     * Generate #DT row
     * 
     * @return row
     */
    private List<String> generateDT() {
        List<String> params = new ArrayList<String>();
        Integer deviceType = generateInteger(0, 1);
        params.add(deviceType.toString());
        return params;
    }

    /**
     * Generate #FF row
     * 
     * @return row
     */
    private List<String> generateFF() {
        List<String> params = new ArrayList<String>();
        String fileFormatVersion = "File format version";
        params.add(returnWordSoCalled(fileFormatVersion));
        return params;
    }

    /**
     * Generate #EI row
     * 
     * @return row
     */
    private List<String> generateEI() {
        List<String> params = new ArrayList<String>();
        String deviceIdentity = "Device identity";
        params.add(returnWordSoCalled(deviceIdentity));
        return params;
    }

    /**
     * Generate #HV row
     * 
     * @return row
     */
    private List<String> generateHV() {
        List<String> params = new ArrayList<String>();
        String handlerVersion = "Handler version";
        params.add(returnWordSoCalled(handlerVersion));
        return params;
    }

    /**
     * Generate #HW row
     * 
     * @return row
     */
    private List<String> generateHW() {
        List<String> params = new ArrayList<String>();
        String hardwareVersion = "Hardware version";
        params.add(returnWordSoCalled(hardwareVersion));
        return params;
    }

    /**
     * Generate #ID row
     * 
     * @return row
     */
    private List<String> generateID() {
        List<String> params = new ArrayList<String>();
        String measurementID = "Measurement id";
        params.add(returnWordSoCalled(measurementID));
        return params;
    }

    /**
     * Generate #MF row
     * 
     * @return row
     */
    private List<String> generateMF() {
        List<String> params = new ArrayList<String>();
        String mapFile = "Map filename";
        params.add(returnWordSoCalled(mapFile));
        return params;
    }

    /**
     * Generate #ML row
     * 
     * @return row
     */
    private List<String> generateML() {
        List<String> params = new ArrayList<String>();
        String measurementLabel = "Measurement label";
        params.add(returnWordSoCalled(measurementLabel));
        return params;
    }

    /**
     * Generate #NN row
     * 
     * @return row
     */
    private List<String> generateNN() {
        List<String> params = new ArrayList<String>();
        String networkName = "Network name";
        params.add(returnWordSoCalled(networkName));
        return params;
    }

    /**
     * Generate #PC row
     * 
     * @return row
     */
    private List<String> generatePC() {
        List<String> params = new ArrayList<String>();
        Integer packetCaptureState = generateInteger(0, 1);
        params.add(packetCaptureState.toString());
        return params;
    }

    /**
     * Generate #PRODUCT row
     * 
     * @return row
     */
    private List<String> generatePRODUCT() {
        List<String> params = new ArrayList<String>();
        String productName = "Product name";
        String productVersion = "Product version";
        params.add(returnWordSoCalled(productName));
        params.add(returnWordSoCalled(productVersion));
        return params;
    }

    /**
     * Generate #SI row
     * 
     * @return row
     */
    private List<String> generateSI() {
        List<String> params = new ArrayList<String>();
        String subscriberIdentity = "Subscriber identity";
        params.add(returnWordSoCalled(subscriberIdentity));
        return params;
    }

    /**
     * Generate #SP row
     * 
     * @return row
     */
    private List<String> generateSP() {
        List<String> params = new ArrayList<String>();
        String subscriberPhoneNumber = "Subscriber phone number";
        params.add(returnWordSoCalled(subscriberPhoneNumber));
        return params;
    }

    /**
     * Generate #SW row
     * 
     * @return row
     */
    private List<String> generateSW() {
        List<String> params = new ArrayList<String>();
        String deviceSoftwareVersion = "Device software version";
        params.add(returnWordSoCalled(deviceSoftwareVersion));
        return params;
    }

    /**
     * Generate #TS row
     * 
     * @return row
     */
    private List<String> generateTS() {
        List<String> params = new ArrayList<String>();
        String testScriptFilename = "Test script filename";
        params.add(returnWordSoCalled(testScriptFilename));
        return params;
    }

    /**
     * Generate #UT row
     * 
     * @return row
     */
    private List<String> generateUT() {
        List<String> params = new ArrayList<String>();
        Integer gapToUTC = generateInteger(-720, 720);
        params.add(gapToUTC.toString());
        return params;
    }

    /**
     * Generate #VQ row
     * 
     * @return row
     */
    private List<String> generateVQ() {
        List<String> params = new ArrayList<String>();
        Integer vqType = generateInteger(0, 4);
        String vqVersion = "Voice quality version";
        params.add(vqType.toString());
        params.add(returnWordSoCalled(vqVersion));
        return params;
    }

    /**
     * Generate #START row
     * 
     * @return row
     */
    private List<String> generateSTART() {
        List<String> params = new ArrayList<String>();
        String date = generateDate();
        params.add(returnWordSoCalled(date));
        return params;
    }

    /**
     * Generate #STOP row
     * 
     * @return row
     */
    private List<String> generateSTOP() {
        List<String> params = new ArrayList<String>();
        String date = generateDate();
        params.add(returnWordSoCalled(date));
        return params;
    }

    /**
     * Generate CAA row
     * 
     * @return row
     */
    private String generateCAA() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.CAA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateTechnologySystems();
        Integer callType = generateInteger(1, 9);
        Integer direction = generateInteger(1, 2);
        String number = "Called number";
        str = str + "," + system + "," + callType + "," + direction + "," + returnWordSoCalled(number);
        return str;
    }

    /**
     * Generate CAC row
     * 
     * @return row
     */
    private String generateCAC() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.CAC.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateTechnologySystems();
        Integer callType = generateInteger(1, 9);
        Integer callStatus = generateInteger(1, 4);
        str = str + "," + system + "," + callType + "," + callStatus;
        String parameters = "0";
        if (system == 1 || system == 2) {
            parameters = "1";
        }
        str = str + "," + parameters;
        if (system == 1) {
            Integer tn = generateInteger(0, 7);
            str = str + "," + tn;
        }
        if (system == 2) {
            Integer tn = generateInteger(1, 4);
            str = str + "," + tn;
        }
        return str;
    }

    /*
     * private String generateCAF(){ String str = NemoEvents.CAF.getEventId() + "," +
     * generateTimestamp() + "," + generateContext(1); Integer system = generateTechnologySystems();
     * String callType = generateInteger(1, 9).toString(); //11,20 String csFailStatus =
     * generateInteger(1, 9).toString(); str = str + "," + system.toString() + "," + callType + ","
     * + csFailStatus; if(system==1||system==5||system==6||system==21){ //1,3,... String csDiscCause
     * = generateInteger(16,19).toString(); str=str+","+csDiscCause; } if(system==2){ String
     * csDiscCause = generateInteger(0,15).toString(); str=str+","+csDiscCause; }
     * if(system==10||system==11||system==12){ //0 String csDiscCause =
     * generateInteger(20,48).toString(); str=str+","+csDiscCause; } return str; }
     */

    /*
     * private String generateCAD(){ String str = NemoEvents.CAD.getEventId() + "," +
     * generateTimestamp() + "," + generateContext(1); Integer system = generateTechnologySystems();
     * String callType = generateInteger(1, 9).toString(); //20 String csDiscStatus =
     * generateInteger(1, 11).toString(); str = str + "," + system.toString() + "," + callType + ","
     * + csDiscStatus; if(system==1||system==5||system==6||system==21){ //1,3,... String csDiscCause
     * = generateInteger(16,19).toString(); str=str+","+csDiscCause; } if(system==2){ String
     * csDiscCause = generateInteger(0,15).toString(); str=str+","+csDiscCause; }
     * if(system==10||system==11||system==12){ //0 String csDiscCause =
     * generateInteger(20,48).toString(); str=str+","+csDiscCause; } return str; }
     */

    /*
     * private String generateVCHI() { String str = NemoEvents.VCHI.getEventId() + "," +
     * generateTimestamp(); Integer system = TechnologySystems.TETRA.getId(); str = str + "," +
     * system.toString(); if (system == 2) { str = str + "," + generateContext(1); String pttState =
     * generateInteger(1, 4).toString(); String pttCommType = generateInteger(0, 3).toString();
     * String pttUserIdentity = returnWordSoCalled("Push-to-talk user identity"); str = str + "," +
     * pttState+","+pttCommType+","+pttUserIdentity; } return str; }
     */

    /*
     * private String generateDAA() { String str = NemoEvents.DAA.getEventId() + "," +
     * generateTimestamp() + "," + generateContext(3); String applicationProtocol =
     * generateInteger(0, 14).toString(); String hostAddress =
     * returnWordSoCalled("Data transfer host address"); String hostPort =
     * generateInteger(1,maxIntegerValue).toString(); str = str + "," + applicationProtocol + "," +
     * hostAddress + "," + hostPort; return str; }
     */

    /**
     * Generate DAC row
     * 
     * @return row
     */
    private String generateDAC() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DAC.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer applicationProtocol = generateInteger(0, 14);
        str = str + "," + applicationProtocol;
        return str;
    }

    /**
     * Generate DAF row
     * 
     * @return row
     */
    private String generateDAF() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DAF.getEventId() + "," + generateTimestamp() + "," + generateContext(1) + generateDataOfProtocol();
        return str;
    }

    /**
     * Generate DAD row
     * 
     * @return row
     */
    private String generateDAD() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DAD.getEventId() + "," + generateTimestamp() + "," + generateContext(1) + generateDataOfProtocol();
        return str;
    }

    /**
     * Generate DREQ row
     * 
     * @return row
     */
    private String generateDREQ() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DREQ.getEventId() + "," + generateTimestamp() + "," + generateContext(2);
        Integer protocol = generateInteger(0, 14);
        str = str + "," + protocol;
        Integer transfDir = generateInteger(1, 3);
        str = str + "," + transfDir;
        if (protocol == 0 || protocol == 1 || protocol == 2) {
            Integer fileSize = generateInteger(0, maxIntegerValue);
            Integer packetSize = generateInteger(0, maxIntegerValue);
            Integer rateLimit = generateInteger(0, maxIntegerValue);
            Integer pingSize = generateInteger(0, 100000);
            Integer pingRate = generateInteger(0, maxIntegerValue);
            Integer pingTimeout = generateInteger(0, maxIntegerValue);
            str = str + "," + fileSize + "," + packetSize + "," + rateLimit + "," + pingSize + "," + pingRate + "," + pingTimeout;
        }
        if (protocol == 3 || protocol == 4) {
            Integer fileSize = generateInteger(0, maxIntegerValue);
            String fileName = "Data transfer filename";
            Integer transfAtt = generateInteger(0, maxIntegerValue);
            str = str + "," + fileSize + "," + returnWordSoCalled(fileName) + "," + transfAtt;
        }
        if (protocol == 5 || protocol == 6 || protocol == 7 || protocol == 8 || protocol == 9 || protocol == 10) {
            Integer fileSize = generateInteger(0, maxIntegerValue);
            String fileName = "Data transfer filename";
            str = str + "," + fileSize + "," + returnWordSoCalled(fileName);
        }
        if (protocol == 11) {
            String fileName = "Data transfer filename";
            str = str + "," + returnWordSoCalled(fileName);
        }
        if (protocol == 12) {
            Integer pingSize = generateInteger(0, 100000);
            Integer pingRate = generateInteger(0, maxIntegerValue);
            Integer pingTimeout = generateInteger(0, maxIntegerValue);
            str = str + "," + pingSize + "," + pingRate + "," + pingTimeout;
        }
        if (protocol == 13 || protocol == 14) {
            Integer dataSize = generateInteger(0, maxIntegerValue);
            str = str + "," + dataSize;
        }
        return str;
    }

    /**
     * Generate DCOMP row
     * 
     * @return row
     */
    private String generateDCOMP() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DCOMP.getEventId() + "," + generateTimestamp() + "," + generateContext(1)
                + generateDataOfProtocol2();
        Integer ipAccessTime = generateInteger(0, maxIntegerValue);
        Integer ipTermTime = generateInteger(0, maxIntegerValue);
        Integer bytesUL = generateInteger(0, maxIntegerValue);
        Integer bytesDL = generateInteger(0, maxIntegerValue);
        str = str + "," + ipAccessTime + "," + ipTermTime + "," + bytesUL + "," + bytesDL;
        return str;
    }

    /**
     * Generate DRATE row
     * 
     * @return row
     */
    private String generateDRATE() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DRATE.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer applicationProtocol = generateInteger(0, 14);
        Integer appRateUL = generateInteger(0, maxIntegerValue);
        Integer appRateDL = generateInteger(0, maxIntegerValue);
        Integer bytesUL = generateInteger(0, maxIntegerValue);
        Integer bytesDL = generateInteger(0, maxIntegerValue);
        str = str + "," + applicationProtocol + "," + appRateUL + "," + appRateDL + "," + bytesUL + "," + bytesDL;
        return str;
    }

    /**
     * Generate PER row
     * 
     * @return row
     */
    private String generatePER() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.PER.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer applicationProtocol = generateInteger(0, 14);
        Float perUL = generateFloat(0, 99);
        Float perDL = generateFloat(0, 99);
        str = str + "," + applicationProtocol + "," + perUL + "," + perDL;
        return str;
    }

    /**
     * Generate RTT row
     * 
     * @return row
     */
    private String generateRTT() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.RTT.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer protocol = generateInteger(0, 14);
        str = str + "," + protocol;
        if (protocol == 12) {
            Integer pingSize = generateInteger(0, 100000);
            Integer pingRTT = generateInteger(0, maxIntegerValue);
            str = str + "," + pingSize + "," + pingRTT;
        }
        return str;
    }

    /**
     * Generate JITTER row
     * 
     * @return row
     */
    private String generateJITTER() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.JITTER.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer protocol = generateInteger(0, 14);
        str = str + "," + protocol;
        if (protocol == 13 || protocol == 14) {
            Integer jitterUl = generateInteger(0, maxIntegerValue);
            Integer jitterDl = generateInteger(0, maxIntegerValue);
            str = str + "," + jitterUl + "," + jitterDl;
        }
        return str;
    }

    /**
     * Generate DSS row
     * 
     * @return row
     */
    private String generateDSS() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DSS.getEventId() + "," + generateTimestamp() + ",";
        Integer protocol = generateInteger(0, 14);
        str = str + "," + protocol;
        if (protocol == 9) {
            str = str + generateContext(1);
            Integer streamState = generateInteger(1, 3);
            Integer streamBandwidth = generateInteger(0, maxIntegerValue);
            str = str + "," + streamState + "," + streamBandwidth;
        }
        return str;
    }

    /**
     * Generate DCONTENT row
     * 
     * @return row
     */
    private String generateDCONTENT() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.DCONTENT.getEventId() + "," + generateTimestamp() + ",";
        Integer protocol = generateInteger(0, 14);
        str = str + "," + protocol;
        if (protocol == 8 || protocol == 10) {
            str = str + generateContext(1);
            Integer numberOfContentElements = generateInteger(0, 10);
            Integer numberOfParametersPerContent = 3;
            str = str + "," + numberOfContentElements + "," + numberOfParametersPerContent;
            for (int i = 0; i < numberOfContentElements; i++) {
                String contentURL = "Content URL";
                Integer contentType = generateInteger(1, 3);
                Integer contentSize = generateInteger(0, maxIntegerValue);
                str = str + "," + returnWordSoCalled(contentURL) + "," + contentType + "," + contentSize;
            }
        }
        return str;
    }

    /**
     * Generate CELLMEAS row
     * 
     * @return row
     */
    private String generateCELLMEAS() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.CELLMEAS.getEventId() + "," + generateTimestamp() + ",";
        Integer choice = generateInteger(1, 2);
        Integer idSystem = 0;
        if (choice == 1) {
            idSystem = generateInteger(0, 9);
        }
        if (choice == 2) {
            idSystem = generateInteger(11, 13);
        }
        Integer system = systems.get(idSystem);
        str = str + "," + system;
        if (system == 1) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 16;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int i = 0; i < numberOfCells; i++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer arfcn = generateInteger(0, maxIntegerValue);
                Integer bsic = generateInteger(0, 63);
                Float rxLevFull = generateFloat(-120, -11);
                Float rxLevSub = generateFloat(-120, -11);
                Float c1 = generateFloat(0, 100);
                Float c2 = generateFloat(0, 100);
                Float c31 = generateFloat(0, 100);
                Float c32 = generateFloat(0, 100);
                Integer hcsPriority = generateInteger(0, 7);
                Float hcsThr = generateFloat(-110, -49);
                Integer cellID = generateInteger(0, 65535);
                Integer lac = generateInteger(0, 65535);
                Integer rac = generateInteger(0, maxIntegerValue);
                Float srxlev = generateFloat(-107, -91);
                str = str + "," + cellType + "," + band + "," + arfcn + "," + bsic + "," + rxLevFull + "," + rxLevSub + "," + c1
                        + "," + c2 + "," + c31 + "," + c32 + "," + hcsPriority + "," + hcsThr + "," + cellID + "," + lac + ","
                        + rac + "," + srxlev;
            }
        }
        if (system == 2) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 8;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int i = 0; i < numberOfCells; i++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer arfcn = generateInteger(0, maxIntegerValue);
                Integer lac = generateInteger(0, 65535);
                Float rssi = generateFloat(-111, -11);
                Float c1 = generateFloat(0, 100);
                Float c2 = generateFloat(0, 100);
                Integer cc = generateInteger(0, 63);
                str = str + "," + cellType + "," + band + "," + arfcn + "," + lac + "," + rssi + "," + c1 + "," + c2 + "," + cc;
            }
        }
        if (system == 5) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfChs = generateInteger(0, 10);
            Integer numberOfParametersPerChs = 2;
            str = str + "," + numberOfHeadersParams + "," + numberOfChs + "," + numberOfParametersPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer ch = generateInteger(0, maxIntegerValue);
                Float rssi = generateFloat(0, 100);
                str = str + "," + ch + "," + rssi;
            }
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 17;
            str = str + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int j = 0; j < numberOfCells; j++) {
                Integer cellType = generateInteger(0, 3);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer ch2 = generateInteger(0, maxIntegerValue);
                Integer scr = generateInteger(0, 511);
                Float ecn0 = generateFloat(-26, -1);
                Integer sttd = generateInteger(0, 1);
                Float rscp = generateFloat(-150, -20);
                Integer secondaryScr = generateInteger(0, 15);
                Float squal = generateFloat(-24, 23);
                Float srxlev = generateFloat(-107, 89);
                Float hqual = generateFloat(-32, 23);
                Float hrxlev = generateFloat(-115, 89);
                Float rqual = generateFloat(-200, 49);
                Float rrxlev = generateFloat(-191, 24);
                Integer off = generateInteger(0, 255);
                Float tm = generateFloat(0, 38399);
                Float pathloss = generateFloat(0, 119);
                str = str + "," + cellType + "," + band + "," + ch2 + "," + scr + "," + ecn0 + "," + sttd + "," + rscp + ","
                        + secondaryScr + "," + squal + "," + srxlev + "," + hqual + "," + hrxlev + "," + rqual + "," + rrxlev + ","
                        + off + "," + tm + "," + pathloss;
            }
        }
        if (system == 6) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfChs = generateInteger(0, 10);
            Integer numberOfParametersPerChs = 3;
            str = str + "," + numberOfHeadersParams + "," + numberOfChs + "," + numberOfParametersPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer ch = generateInteger(0, maxIntegerValue);
                Float rssi = generateFloat(0, 100);
                str = str + "," + band + "," + ch + "," + rssi;
            }
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 9;
            str = str + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int j = 0; j < numberOfCells; j++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band2 = generateInteger(20001, 20015);
                Integer ch2 = generateInteger(0, maxIntegerValue);
                Integer cellParamsID = generateInteger(0, 127);
                Float rscp = generateFloat(-116, -21);
                Float srxlev = generateFloat(-107, 89);
                Float hrxlev = generateFloat(-115, 89);
                Float rrxlev = generateFloat(-191, 24);
                Float pathloss = generateFloat(46, 147);
                str = str + "," + cellType + "," + band2 + "," + ch2 + "," + cellParamsID + "," + rscp + "," + srxlev + ","
                        + hrxlev + "," + rrxlev + "," + pathloss;
            }
        }
        if (system == 10 || system == 11) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfChs = generateInteger(0, 10);
            Integer numberOfParametersPerChs = 5;
            str = str + "," + numberOfHeadersParams + "," + numberOfChs + "," + numberOfParametersPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer ch = generateInteger(0, maxIntegerValue);
                Float rxPower = generateFloat(-120, 29);
                Float rx0Power = generateFloat(-120, 29);
                Float rx1Power = generateFloat(-120, 29);
                str = str + "," + band + "," + ch + "," + rxPower + "," + rx0Power + "," + rx1Power;
            }
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 7;
            str = str + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int j = 0; j < numberOfCells; j++) {
                Integer set = generateInteger(0, 3);
                // 0,...
                Integer band2 = generateInteger(20001, 20015);
                Integer ch2 = generateInteger(0, maxIntegerValue);
                Integer pn = generateInteger(0, maxIntegerValue);
                Float eci0 = generateFloat(-32, -1);
                Integer walsh = generateInteger(0, maxIntegerValue);
                Float rscp = generateFloat(-150, -21);
                str = str + "," + set + "," + band2 + "," + ch2 + "," + pn + "," + eci0 + "," + walsh + "," + rscp;
            }
        }
        if (system == 12) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfChs = generateInteger(0, 10);
            Integer numberOfParametersPerChs = 5;
            str = str + "," + numberOfHeadersParams + "," + numberOfChs + "," + numberOfParametersPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer ch = generateInteger(0, maxIntegerValue);
                Float rxPower = generateFloat(-120, 29);
                Float rx0Power = generateFloat(-120, 29);
                Float rx1Power = generateFloat(-120, 29);
                str = str + "," + band + "," + ch + "," + rxPower + "," + rx0Power + "," + rx1Power;
            }
            Integer numberOfChs2 = generateInteger(0, 10);
            Integer numberOfParametersPerChs2 = 6;
            str = str + "," + numberOfChs2 + "," + numberOfParametersPerChs2;
            for (int j = 0; j < numberOfChs2; j++) {
                Integer set = generateInteger(0, 3);
                Integer band2 = generateInteger(20001, 20015);
                Integer ch2 = generateInteger(0, maxIntegerValue);
                Integer pn = generateInteger(0, maxIntegerValue);
                Float eci0 = generateFloat(-32, -1);
                Float rscp = generateFloat(-150, -21);
                str = str + "," + set + "," + band2 + "," + ch2 + "," + pn + "," + eci0 + "," + rscp;
            }
        }
        if (system == 20) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 9;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int i = 0; i < numberOfCells; i++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Float quality = generateFloat(0, 99);
                Integer channel = generateInteger(0, maxIntegerValue);
                Float rssi = generateFloat(-100, 19);
                String ssid = returnWordSoCalled("WLAN service set identifier");
                String macAddr = returnWordSoCalled("WLAN MAC address");
                Integer security = generateInteger(0, 4);
                Integer maxTransferRate = generateInteger(0, maxIntegerValue);
                str = str + "," + cellType + "," + band + "," + quality + "," + channel + "," + rssi + "," + ssid + "," + macAddr
                        + "," + security + "," + maxTransferRate;
            }
        }
        if (system == 21) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 7;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int i = 0; i < numberOfCells; i++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Float quality = generateFloat(0, 99);
                Integer channel = generateInteger(0, maxIntegerValue);
                Float rssi = generateFloat(-100, 19);
                String ssid = returnWordSoCalled("WLAN service set identifier");
                String macAddr = returnWordSoCalled("WLAN MAC address");
                str = str + "," + cellType + "," + band + "," + "," + quality + "," + channel + "," + rssi + "," + ssid + ","
                        + macAddr;
            }
        }
        if (system == 25) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 9;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int i = 0; i < numberOfCells; i++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Float frequency = generateFloat(-100, 100);
                Integer preambleIndex = generateInteger(0, 113);
                String bsID = returnWordSoCalled("WiMAX base station ID");
                Float rssi = generateFloat(-120, 19);
                Float rssiDev = generateFloat(0, 49);
                Float cinr = generateFloat(-32, 39);
                Float cinrDev = generateFloat(0, 39);
                str = str + "," + cellType + "," + band + "," + frequency + "," + preambleIndex + "," + bsID + "," + rssi + ","
                        + rssiDev + "," + cinr + "," + cinrDev;
            }
        }
        if (system == 51 || system == 52) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 5;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int i = 0; i < numberOfCells; i++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer ch = generateInteger(0, maxIntegerValue);
                Integer sat = generateInteger(0, 6);
                Float rxLev = generateFloat(-120, -11);
                str = str + "," + cellType + "," + band + "," + ch + "," + sat + "," + rxLev;
            }
        }
        if (system == 53) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParametersPerCell = 5;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParametersPerCell;
            for (int i = 0; i < numberOfCells; i++) {
                Integer cellType = generateInteger(0, 1);
                // 0,...
                Integer band = generateInteger(20001, 20015);
                Integer ch = generateInteger(0, maxIntegerValue);
                Integer dcc = generateInteger(0, 255);
                Float rxLev = generateFloat(-120, -11);
                str = str + "," + cellType + "," + band + "," + ch + "," + dcc + "," + rxLev;
            }
        }
        return str;
    }

    /**
     * Generate ADJMEAS row
     * 
     * @return row
     */
    private String generateADJMEAS() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.ADJMEAS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        Integer headersParams = 0;
        Integer numberOfChs = generateInteger(0, 10);
        Integer parametrsPerChs = 11;
        str = str + "," + headersParams + "," + numberOfChs + "," + parametrsPerChs;
        for (int i = 0; i < numberOfChs; i++) {
            Integer caChannel = generateInteger(0, maxIntegerValue);
            Float caMinimum = generateFloat(-100, 99);
            Float rssi = generateFloat(-120, -11);
            Float ca1 = generateFloat(-100, 99);
            Float rssi1 = generateFloat(-120, -11);
            Float ca11 = generateFloat(-100, 99);
            Float rssi11 = generateFloat(-120, -11);
            Float ca2 = generateFloat(-100, 99);
            Float rssi2 = generateFloat(-120, -11);
            Float ca22 = generateFloat(-100, 99);
            Float rssi22 = generateFloat(-120, -11);
            str = str + "," + caChannel + "," + caMinimum + "," + rssi + "," + ca1 + "," + rssi1 + "," + ca11 + "," + rssi11 + ","
                    + ca2 + "," + rssi2 + "," + ca22 + "," + rssi22;
        }
        return str;
    }

    /**
     * Generate RXQ row
     * 
     * @return row
     */
    private String generateRXQ() {
        List<String> params = new ArrayList<String>();
        String str = NemoEvents.RXQ.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 2);
        if (choice == 1) {
            system = TechnologySystems.GSM.getId();
        } else {
            system = TechnologySystems.DAMPS.getId();
        }
        str = str + "," + system.toString();
        if (system == 1) {
            Integer rxqFull = generateInteger(0, maxIntegerValue);
            Integer rxqSub = generateInteger(0, maxIntegerValue);
            str = str + "," + rxqFull + "," + rxqSub;
        }
        if (system == 53) {
            Integer berClass = generateInteger(0, 7);
            str = str + "," + berClass;
        }
        return str;
    }

    /**
     * Generate PRXQ row
     * 
     * @return row
     */
    private String generatePRXQ() {
        String str = NemoEvents.PRXQ.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        if (system == 1) {
            Integer rxq = generateInteger(0, 7);
            Float cValue = generateFloat(-120, -49);
            Float signVar = generateFloat(0, 15);
            Integer tslResults = generateInteger(0, 10);
            str = str + "," + rxq + "," + cValue + "," + signVar + "," + tslResults;
            for (int i = 0; i < tslResults; i++) {
                Float tslInterf = generateFloat(-28, -1);
                str = str + "," + tslInterf;
            }
        }
        return str;
    }

    private String generateFER() {
        String str = NemoEvents.FER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice != 1) {
            Integer systemID = generateInteger(2, 5);
            system = systems.get(systemID);
        }
        str = str + "," + system;
        if (system == 1) {
            Float ferFull = generateFloat(0, 99);
            Float ferSub = generateFloat(0, 99);
            Float ferTch = generateFloat(0, 99);
            str = str + "," + ferFull + "," + ferSub + "," + ferTch;
        }
        if (system == 5 || system == 6) {
            Float fer = generateFloat(0, 99);
            str = str + "," + fer;
        }
        if (system == 10 || system == 11) {
            Float ferDec = generateFloat(0, 99);
            Float ferFFCHTarget = generateFloat(0, 99);
            Float ferFSCHTarget = generateFloat(0, 99);
            str = str + "," + ferDec + "," + ferFFCHTarget + "," + ferFSCHTarget;
        }
        return str;
    }

    /**
     * Generate MSP row
     * 
     * @return row
     */
    private String generateMSP() {
        String str = NemoEvents.MSP.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 3);
        if (choice == 1) {
            system = TechnologySystems.GSM.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.DAMPS.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.AMPS.getId();
        }
        str = str + "," + system;
        if (system == 1 || system == 51 || system == 53) {
            Integer msp = generateInteger(0, 32);
            str = str + "," + msp;
        }
        return str;
    }

    /**
     * Generate RLT row
     * 
     * @return row
     */
    private String generateRLT() {
        String str = NemoEvents.RLT.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        Integer rlt = generateInteger(0, 64);
        str = str + "," + rlt;
        return str;
    }

    /**
     * Generate TAD row
     * 
     * @return row
     */
    private String generateTAD() {
        String str = NemoEvents.TAD.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 3);
        if (choice == 1) {
            system = TechnologySystems.GSM.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.UMTS_TD_SCDMA.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.DAMPS.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Integer ta = generateInteger(0, 63);
            str = str + "," + ta;
        }
        if (system == 6) {
            Float ta = generateFloat(-16, 239);
            str = str + "," + ta;
        }
        if (system == 53) {
            Integer tal = generateInteger(0, 30);
            str = str + "," + tal;
        }
        return str;
    }

    /**
     * Generate DSC row
     * 
     * @return row
     */
    private String generateDSC() {
        String str = NemoEvents.DSC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        Integer dscCurrent = generateInteger(0, 45);
        Integer dscMax = generateInteger(0, 45);
        str = str + "," + dscCurrent + "," + dscMax;
        return str;
    }

    /**
     * Generate BEP row
     * 
     * @return row
     */
    private String generateBEP() {
        String str = NemoEvents.BEP.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        if (system == 1) {
            Integer gmskMeanBEP = generateInteger(0, 31);
            Integer gmskCvBEP = generateInteger(0, 7);
            Integer pskMeanBEP = generateInteger(0, 31);
            Integer pskCvBEP = generateInteger(0, 7);
            str = str + "," + gmskMeanBEP + "," + gmskCvBEP + "," + pskMeanBEP + "," + pskCvBEP;
        }
        return str;
    }

    /**
     * Generate CI row
     * 
     * @return row
     */
    private String generateCIEvent() {
        String str = NemoEvents.CI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = TechnologySystems.UMTS_TD_SCDMA.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.EVDO.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Float ci = generateFloat(-10, 39);
            Integer tslResults = generateInteger(0, 10);
            str = str + "," + ci + "," + tslResults;
            for (int i = 0; i < tslResults; i++) {
                Float timeslotCI = generateFloat(-10, 39);
                str = str + "," + timeslotCI;
            }
            Integer numberOfChs = generateInteger(0, 10);
            Integer numberOfParametersPerChs = 3;
            str = str + "," + numberOfChs + "," + numberOfParametersPerChs;
            for (int j = 0; j < numberOfChs; j++) {
                Integer arfcn = generateInteger(0, maxIntegerValue);
                Float ci2 = generateFloat(-10, 39);
                Float rssi = generateFloat(0, 100);
                str = str + "," + arfcn + "," + ci2 + "," + rssi;
            }
        }
        if (system == 6) {
            Float ci = generateFloat(-30, 39);
            Integer headersParams = 0;
            Integer numberOfActSetPNs = generateInteger(0, 10);
            Integer numberOfParametersPerPilots = 7;
            str = str + "," + ci + "," + headersParams + "," + numberOfActSetPNs + "," + numberOfParametersPerPilots;
            for (int i = 0; i < numberOfActSetPNs; i++) {
                Integer pn = generateInteger(0, 511);
                Float sinr = generateFloat(-28, 14);
                Integer macIndex = generateInteger(0, 255);
                Integer drcCover = generateInteger(0, 7);
                Integer rpcCellIndex = generateInteger(0, 15);
                Integer drcLock = generateInteger(0, 1);
                Integer rab = generateInteger(0, 1);
                str = str + "," + pn + "," + sinr + "," + macIndex + "," + drcCover + "," + rpcCellIndex + "," + drcLock + ","
                        + rab;
            }
        }
        return str;
    }

    /**
     * Generate TXPC row
     * 
     * @return row
     */
    private String generateTXPC() {
        String str = NemoEvents.TXPC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 25;
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            Integer systemID = generateInteger(2, 6);
            system = systems.get(systemID);
        }
        str = str + "," + system;
        if (system == 2) {
            Float txPower = generateFloat(-120, 29);
            Integer pwrCtrlAlg = generateInteger(0, 1);
            Float txPowerChange = generateFloat(-30, 29);
            str = str + "," + txPower + "," + pwrCtrlAlg + "," + txPowerChange;
        }
        if (system == 5) {
            Float txPower = generateFloat(-120, 29);
            Integer pwrCtrlAlg = generateInteger(0, 1);
            Integer pwrCtrlStep = generateInteger(1, 2);
            Integer comprMode = generateInteger(0, 1);
            Integer ulPwrUp = generateInteger(0, maxIntegerValue);
            Integer ulPwrDown = generateInteger(0, maxIntegerValue);
            Float ulPwrUpProcent = generateFloat(0, 99);
            str = str + "," + txPower + "," + pwrCtrlAlg + "," + pwrCtrlStep + "," + comprMode + "," + ulPwrUp + "," + ulPwrDown
                    + "," + ulPwrUpProcent;
        }
        if (system == 6) {
            Float txPower = generateFloat(-99, 98);
            Integer pwrCtrlStep = generateInteger(1, 3);
            Integer ulPwrUp = generateInteger(0, maxIntegerValue);
            Integer ulPwrDown = generateInteger(0, maxIntegerValue);
            Float ulPwrUpProcent = generateFloat(0, 99);
            str = str + "," + txPower + "," + pwrCtrlStep + "," + ulPwrUp + "," + ulPwrDown + "," + ulPwrUpProcent;
        }
        if (system == 11) {
            Float txPower = generateFloat(-99, 98);
            Integer pwrCtrlStep = generateInteger(0, 2);
            Integer ulPwrUp = generateInteger(0, maxIntegerValue);
            Integer ulPwrDown = generateInteger(0, maxIntegerValue);
            Float ulPwrUpProcent = generateFloat(0, 99);
            Float txAdjust = generateFloat(0, 100);
            Float txPwrLimit = generateFloat(0, 100);
            Integer maxPowerLimited = generateInteger(0, 2);
            Float r1 = generateFloat(0, 100);
            Float r2 = generateFloat(0, 100);
            Float r3 = generateFloat(0, 100);
            Float r4 = generateFloat(0, 100);
            str = str + "," + txPower + "," + pwrCtrlStep + "," + ulPwrUp + "," + ulPwrDown + "," + ulPwrUpProcent + "," + txAdjust
                    + "," + txPwrLimit + "," + maxPowerLimited + "," + r1 + "," + r2 + "," + r3 + "," + r4;
        }
        if (system == 12) {
            Float txPower = generateFloat(-99, 98);
            Integer ulPwrUp = generateInteger(0, maxIntegerValue);
            Integer ulPwrHold = generateInteger(0, maxIntegerValue);
            Integer ulPwrDown = generateInteger(0, maxIntegerValue);
            Float ulPwrUpProcent = generateFloat(0, 99);
            Float txAdjust = generateFloat(0, 100);
            Float txPilot = generateFloat(-99, 98);
            Float txOpenLoopPower = generateFloat(-99, 98);
            Float drcPilot = generateFloat(0, 100);
            Float ackPilot = generateFloat(0, 100);
            Float dataPilot = generateFloat(0, 100);
            Float paMax = generateFloat(0, 100);
            Integer drcLockPeriod = generateInteger(8, 8);
            Float txThrottle = generateFloat(0, 98);
            Float txMaxPowerUsage = generateFloat(0, 99);
            Float txMinPowerUsage = generateFloat(0, 99);
            Integer transmissionMode = generateInteger(0, 1);
            Integer physicalLayerPacketSize = generateInteger(0, maxIntegerValue);
            Float rriPilot = generateFloat(0, 100);
            Float dscPilot = generateFloat(0, 100);
            Float auxData = generateFloat(0, 100);
            str = str + "," + txPower + "," + ulPwrUp + "," + ulPwrHold + "," + ulPwrDown + "," + ulPwrUpProcent + "," + txAdjust
                    + "," + txPilot + "," + txOpenLoopPower + "," + drcPilot + "," + ackPilot + "," + dataPilot + "," + paMax + ","
                    + drcLockPeriod + "," + txThrottle + "," + txMaxPowerUsage + "," + txMinPowerUsage + "," + transmissionMode
                    + "," + physicalLayerPacketSize + "," + rriPilot + "," + dscPilot + "," + auxData;
        }
        if (system == 25) {
            Float txPower = generateFloat(-99, 98);
            Float txRefPower = generateFloat(-99, 98);
            Float txPowerHeadroom = generateFloat(0, 98);
            Float txPowerBSOffset = generateFloat(-99, 98);
            Float txPowerIrMax = generateFloat(-99, 98);
            Float bsEIRP = generateFloat(-99, 98);
            Float bsN = generateFloat(-128, -2);
            str = str + "," + txPower + "," + txRefPower + "," + txPowerHeadroom + "," + txPowerBSOffset + "," + txPowerIrMax + ","
                    + bsEIRP + "," + bsN;
        }
        return str;
    }

    /**
     * Generate RXPC row
     * 
     * @return row
     */
    private String generateRXPC() {
        String str = NemoEvents.RXPC.getEventId() + "," + generateTimestamp() + ",";
        Integer systemID = generateInteger(2, 5);
        Integer system = systems.get(systemID);
        str = str + "," + system.toString();
        if (system == 5) {
            Float sirTarget = generateFloat(-32, 29);
            Float sir = generateFloat(-32, 29);
            Integer bsDivState = generateInteger(0, 2);
            Integer dlPwrUp = generateInteger(0, maxIntegerValue);
            Integer dlPwrDown = generateInteger(0, maxIntegerValue);
            Float dlPwrUpProcent = generateFloat(0, 99);
            Integer dpcMode = generateInteger(0, 1);
            str = str + "," + sirTarget + "," + sir + "," + bsDivState + "," + dlPwrUp + "," + dlPwrDown + "," + dlPwrUpProcent
                    + "," + dpcMode;
        }
        if (system == 6) {
            Integer headerParams = 0;
            str = str + "," + headerParams;
            Float sirTarget = generateFloat(-32, 29);
            Float sir = generateFloat(-32, 29);
            Integer dlPwrUp = generateInteger(0, maxIntegerValue);
            Integer dlPwrDown = generateInteger(0, maxIntegerValue);
            Float dlPwrUpProcent = generateFloat(0, 99);
            Integer numberOfTimeslots = generateInteger(0, maxIntegerValue);
            Integer numberOfParametersPerTimeslots = 3;
            str = str + "," + sirTarget + "," + sir + "," + dlPwrUp + "," + dlPwrDown + "," + dlPwrUpProcent + ","
                    + numberOfTimeslots + "," + numberOfParametersPerTimeslots;
            for (int i = 0; i < numberOfTimeslots; i++) {
                Integer tsl = generateInteger(0, 6);
                Float iscp = generateFloat(-116, -26);
                Float rscp = generateFloat(-116, -26);
                str = str + "," + tsl + "," + iscp + "," + rscp;
            }
        }
        if (system == 11) {
            Integer fpcMode = generateInteger(0, 7);
            Integer fpcSubChannel = generateInteger(0, 1);
            Float fpcGain = generateFloat(0, 100);
            Integer dlPwrUp = generateInteger(0, maxIntegerValue);
            Integer dlPwrDown = generateInteger(0, maxIntegerValue);
            Float dlPwrUpProcent = generateFloat(0, 99);
            Float f1 = generateFloat(0, 99);
            Float f2 = generateFloat(0, 99);
            Float f3 = generateFloat(0, 99);
            Float f4 = generateFloat(0, 99);
            Float f5 = generateFloat(0, 99);
            Float f6 = generateFloat(0, 99);
            Float f7 = generateFloat(0, 99);
            Float f8 = generateFloat(0, 99);
            Float f9 = generateFloat(0, 99);
            Float f10 = generateFloat(0, 99);
            Float f11 = generateFloat(0, 99);
            Float f12 = generateFloat(0, 99);
            str = str + "," + fpcMode + "," + fpcSubChannel + "," + fpcGain + "," + dlPwrUp + "," + dlPwrDown + ","
                    + dlPwrUpProcent + "," + f1 + "," + f2 + "," + f3 + "," + f4 + "," + f5 + "," + f6 + "," + f7 + "," + f8 + ","
                    + f9 + "," + f10 + "," + f11 + "," + f12;
        }
        return str;
    }

    /**
     * Generate BER row
     * 
     * @return row
     */
    private String generateBER() {
        String str = NemoEvents.BER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 2);
        if (choice == 1) {
            system = TechnologySystems.TETRA.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        str = str + "," + system;
        if (system == 2) {
            Float ber = generateFloat(0, 99);
            str = str + "," + ber;
        }
        if (system == 5) {
            Float pilotBer = generateFloat(0, 99);
            Float tfciBer = generateFloat(0, 99);
            str = str + "," + pilotBer + "," + tfciBer;
        }
        return str;
    }

    /**
     * Generate PHRATE row
     * 
     * @return row
     */
    private String generatePHRATE() {
        String str = NemoEvents.PHRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(5, 6);
        str = str + "," + system;
        if (system == 5 || system == 6) {
            Integer dpdchRateUL = generateInteger(0, maxIntegerValue);
            str = str + "," + dpdchRateUL;
        }
        return str;
    }

    /**
     * Generate WLANRATE row
     * 
     * @return row
     */
    private String generateWLANRATE() {
        String str = NemoEvents.WLANRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GAN_WLAN.getId();
        str = str + "," + system;
        Integer wlanRateUl = generateInteger(0, maxIntegerValue);
        Integer wlanRateDl = generateInteger(0, maxIntegerValue);
        str = str + "," + wlanRateUl + "," + wlanRateDl;
        return str;
    }

    /**
     * Generate PPPRATE row
     * 
     * @return row
     */
    private String generatePPPRATE() {
        String str = NemoEvents.PPPRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer pppRateUl = generateInteger(0, maxIntegerValue);
        Integer pppRateDl = generateInteger(0, maxIntegerValue);
        Integer sentPppBytes = generateInteger(0, maxIntegerValue);
        Integer recvPppBytes = generateInteger(0, maxIntegerValue);
        str = str + "," + pppRateUl + "," + pppRateDl + "," + sentPppBytes + "," + recvPppBytes;
        return str;
    }

    /**
     * Generate RLPRATE row
     * 
     * @return row
     */
    private String generateRLPRATE() {
        String str = NemoEvents.RLPRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 12);
        str = str + "," + system;
        Integer rlpRevRate = generateInteger(0, maxIntegerValue);
        Integer rlpForRate = generateInteger(0, maxIntegerValue);
        Integer rlpRevRetrRate = generateInteger(0, maxIntegerValue);
        Integer rlpFwdRetrRate = generateInteger(0, maxIntegerValue);
        str = str + "," + rlpRevRate + "," + rlpForRate + "," + rlpRevRetrRate + "," + rlpFwdRetrRate;
        return str;
    }

    /**
     * Generate RLPSTATISTICS row
     * 
     * @return row
     */
    private String generateRLPSTATISTICS() {
        String str = NemoEvents.RLPSTATISTICS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 12);
        str = str + "," + system;
        if (system == 10 || system == 11) {
            Integer serviceID = generateInteger(0, maxIntegerValue);
            Integer resets = generateInteger(0, maxIntegerValue);
            Integer aborts = generateInteger(0, maxIntegerValue);
            Integer lastRTT = generateInteger(0, maxIntegerValue);
            Integer blockOfBytesUsed = generateInteger(0, 3);
            Integer rxNaks = generateInteger(0, maxIntegerValue);
            Integer largestConErasures = generateInteger(0, maxIntegerValue);
            Integer retransNotFound = generateInteger(0, maxIntegerValue);
            Integer rxRetransFrames = generateInteger(0, maxIntegerValue);
            Integer rxIdleFrames = generateInteger(0, maxIntegerValue);
            Integer rxFillFrames = generateInteger(0, maxIntegerValue);
            Integer rxBlankFrames = generateInteger(0, maxIntegerValue);
            Integer rxNullFrames = generateInteger(0, maxIntegerValue);
            Integer rxNewFrames = generateInteger(0, maxIntegerValue);
            Integer rxFundFrames = generateInteger(0, maxIntegerValue);
            Integer rxBytes = generateInteger(0, maxIntegerValue);
            Integer rxRLPErasures = generateInteger(0, maxIntegerValue);
            Integer rxMUXErasures = generateInteger(0, maxIntegerValue);
            Integer txNAKs = generateInteger(0, maxIntegerValue);
            Integer txRetransFrames = generateInteger(0, maxIntegerValue);
            Integer txIdleFrames = generateInteger(0, maxIntegerValue);
            Integer txNewFrames = generateInteger(0, maxIntegerValue);
            Integer txFundFrames = generateInteger(0, maxIntegerValue);
            Integer txBytes = generateInteger(0, maxIntegerValue);
            str = str + "," + serviceID + "," + resets + "," + aborts + "," + lastRTT + "," + blockOfBytesUsed + "," + rxNaks + ","
                    + largestConErasures + "," + retransNotFound + "," + rxRetransFrames + "," + rxIdleFrames + "," + rxFillFrames
                    + "," + rxBlankFrames + "," + rxNullFrames + "," + rxNewFrames + "," + rxFundFrames + "," + rxBytes + ","
                    + rxRLPErasures + "," + rxMUXErasures + "," + txNAKs + "," + txRetransFrames + "," + txIdleFrames + ","
                    + txNewFrames + "," + txFundFrames + "," + txBytes;
        }
        if (system == 12) {
            Integer serviceID = generateInteger(0, maxIntegerValue);
            Integer rxNaks = generateInteger(0, maxIntegerValue);
            Integer rxNaksInBytes = generateInteger(0, maxIntegerValue);
            Integer retransNotFound = generateInteger(0, maxIntegerValue);
            Integer rxDupBytes = generateInteger(0, maxIntegerValue);
            Integer rxRetransBytes = generateInteger(0, maxIntegerValue);
            Integer rxNewBytes = generateInteger(0, maxIntegerValue);
            Integer rxBytes = generateInteger(0, maxIntegerValue);
            Integer rxNaks2 = generateInteger(0, maxIntegerValue);
            Integer txNaksInBytes = generateInteger(0, maxIntegerValue);
            Integer txRetransBytes = generateInteger(0, maxIntegerValue);
            Integer txNewBytes = generateInteger(0, maxIntegerValue);
            Integer txBytes = generateInteger(0, maxIntegerValue);
            Integer nakTimeouts = generateInteger(0, maxIntegerValue);
            Integer resetCount = generateInteger(0, maxIntegerValue);
            Integer atResetRequestCount = generateInteger(0, maxIntegerValue);
            Integer atResetAckCount = generateInteger(0, maxIntegerValue);
            Integer anResetRequestCount = generateInteger(0, maxIntegerValue);
            str = str + "," + serviceID + "," + rxNaks + "," + rxNaksInBytes + "," + retransNotFound + "," + rxDupBytes + ","
                    + rxRetransBytes + "," + rxNewBytes + "," + rxBytes + "," + rxNaks2 + "," + txNaksInBytes + ","
                    + txRetransBytes + "," + txNewBytes + "," + txBytes + "," + nakTimeouts + "," + resetCount + ","
                    + atResetRequestCount + "," + atResetAckCount + "," + anResetRequestCount;
        }
        return str;
    }

    /**
     * Generate MEI row
     * 
     * @return row
     */
    private String generateMEI() {
        String str = NemoEvents.MEI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(5, 6);
        str = str + "," + system;
        if (system == 5 || system == 6) {
            // 21,22....
            Integer measurementEvent = generateInteger(1, 10);
            str = str + "," + measurementEvent;
        }
        return str;
    }

    /**
     * Generate CQI row
     * 
     * @return row
     */
    private String generateCQI() {
        String str = NemoEvents.CQI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer params = 4;
            Integer sampleDur = generateInteger(1, maxIntegerValue);
            Integer phReqRate = generateInteger(1, maxIntegerValue);
            Integer cqiRepetitions = generateInteger(1, 4);
            Integer cqiCucle = generateInteger(0, 160);
            Integer numberOfValues = generateInteger(1, 10);
            Integer numberOfParamsPerCqi = 2;
            str = str + "," + params + "," + sampleDur + "," + phReqRate + "," + cqiRepetitions + "," + cqiCucle + ","
                    + numberOfValues + "," + numberOfParamsPerCqi;
            for (int i = 0; i < numberOfValues; i++) {
                Float percentage = generateFloat(0, 99);
                Integer cqi = generateInteger(0, 30);
                str = str + "," + percentage + "," + cqi;
            }
        }
        return str;
    }

    /**
     * Generate HARQI row
     * 
     * @return row
     */
    private String generateHARQI() {
        String str = NemoEvents.HARQI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer headerParams = 0;
            Integer numberOfHarqProcesses = generateInteger(0, 10);
            Integer numberOfParamsPerHarqProcesses = 5;
            str = str + "," + headerParams + "," + numberOfHarqProcesses + "," + numberOfParamsPerHarqProcesses;
            for (int i = 0; i < numberOfHarqProcesses; i++) {
                Integer harqID = generateInteger(0, 7);
                Integer harqDir = generateInteger(1, 2);
                Integer harqRate = generateInteger(0, maxIntegerValue);
                Integer harqPackets = generateInteger(0, maxIntegerValue);
                Float harqBler = generateFloat(0, 99);
                str = str + "," + harqID + "," + harqDir + "," + harqRate + "," + harqPackets + "," + harqBler;
            }
        }
        return str;
    }

    /**
     * Generate HSSCCHI row
     * 
     * @return row
     */
    private String generateHSSCCHI() {
        String str = NemoEvents.HSSCCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer headerParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 2;
            str = str + "," + headerParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer hsscchCode = generateInteger(0, 127);
                Float hsdpaHSSCCHUsage = generateFloat(0, 99);
                str = str + "," + hsscchCode + "," + hsdpaHSSCCHUsage;
            }
        }
        return str;
    }

    /**
     * Generate PLAID row
     * 
     * @return row
     */
    private String generatePLAID() {
        String str = NemoEvents.PLAID.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.WIMAX.getId();
        }
        str = str + "," + system;
        if (system == 5) {
            Integer numberOfHeaderParams = 2;
            Integer sampleDuration = generateInteger(1, maxIntegerValue);
            Integer hsPDSCHRate = generateInteger(1, maxIntegerValue);
            Integer numberOfPLASets = generateInteger(0, 10);
            Integer numberOfParamsPerPLASets = 7;
            str = str + "," + numberOfHeaderParams + "," + sampleDuration + "," + hsPDSCHRate + "," + numberOfPLASets + ","
                    + numberOfParamsPerPLASets;
            for (int i = 0; i < numberOfPLASets; i++) {
                Float percentage = generateFloat(0, 99);
                Integer modulation = generateInteger(1, 2);
                Float effectiveCoding = generateFloat(0, 0);
                Integer tbSize = generateInteger(1, maxIntegerValue);
                Integer stChCode = generateInteger(0, 15);
                Integer codes = generateInteger(1, 15);
                Float retr = generateFloat(0, 99);
                str = str + "," + percentage + "," + modulation + "," + effectiveCoding + "," + tbSize + "," + stChCode + ","
                        + codes + "," + retr;
            }
        }
        if (system == 25) {
            Integer numberOfHeaderParams = 2;
            Integer sampleDuration = generateInteger(1, maxIntegerValue);
            Integer burstCount = generateInteger(1, maxIntegerValue);
            Integer numberOfPLASets = generateInteger(1, 10);
            Integer numberOfParamsPerPLASets = 5;
            str = str + "," + numberOfHeaderParams + "," + sampleDuration + "," + burstCount + "," + numberOfPLASets + ","
                    + numberOfParamsPerPLASets;
            for (int i = 0; i < numberOfPLASets; i++) {
                Float percentage = generateFloat(0, 99);
                Integer modulation = generateInteger(1, 2);
                Integer codingRate = generateInteger(1, 7);
                Integer codingType = generateInteger(1, 4);
                // 4,6
                Integer repetitionCoding = generateInteger(1, 2);
                str = str + "," + percentage + "," + modulation + "," + codingRate + "," + codingType + "," + repetitionCoding;
            }
        }
        return str;
    }

    /**
     * Generate PLAIU row
     * 
     * @return row
     */
    private String generatePLAIU() {
        String str = NemoEvents.PLAIU.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.WIMAX.getId();
        }
        str = str + "," + system;
        if (system == 5) {
            Integer numberOfHeaderParams = 7;
            Integer sampleDuration = generateInteger(1, maxIntegerValue);
            Integer eDPDCHRate = generateInteger(1, maxIntegerValue);
            Float limMaxPower = generateFloat(0, 99);
            Float limGrant = generateFloat(0, 99);
            Float limLackOfData = generateFloat(0, 99);
            Float limByMux = generateFloat(0, 99);
            Float limByHARQ = generateFloat(0, 99);
            Integer numberOfPLASets = generateInteger(1, 10);
            Integer numberOfParamsPerPLASets = 8;
            str = str + "," + numberOfHeaderParams + "," + sampleDuration + "," + eDPDCHRate + "," + limMaxPower + "," + limGrant
                    + "," + limLackOfData + "," + limByMux + "," + limByHARQ + "," + numberOfPLASets + ","
                    + numberOfParamsPerPLASets;
            for (int i = 0; i < numberOfPLASets; i++) {
                Float percentage = generateFloat(0, 99);
                Integer modulation = generateInteger(1, 2);
                Integer tbSize = generateInteger(1, maxIntegerValue);
                Integer eTFCI = generateInteger(0, 127);
                Integer sfs = generateInteger(1, 10);
                Float retr = generateFloat(0, 99);
                Integer avgSGIndex = generateInteger(-1, 37);
                Float avgSG = generateFloat(-10, 29);
                str = str + "," + percentage + "," + modulation + "," + tbSize + "," + eTFCI + "," + sfs + "," + retr + ","
                        + avgSGIndex + "," + avgSG;
            }
        }
        if (system == 25) {
            Integer numberOfHeaderParams = 2;
            Integer sampleDuration = generateInteger(1, maxIntegerValue);
            Integer burstCount = generateInteger(1, maxIntegerValue);
            Integer numberOfPLASets = generateInteger(1, 10);
            Integer numberOfParamsPerPLASets = 5;
            str = str + "," + numberOfHeaderParams + "," + sampleDuration + "," + burstCount + "," + numberOfPLASets + ","
                    + numberOfParamsPerPLASets;
            for (int i = 0; i < numberOfPLASets; i++) {
                Float percentage = generateFloat(0, 99);
                Integer modulation = generateInteger(1, 3);
                Integer codingRate = generateInteger(1, 7);
                Integer codingType = generateInteger(1, 4);
                // 4,6
                Integer repetitionCoding = generateInteger(1, 2);
                str = str + "," + percentage + "," + modulation + "," + codingRate + "," + codingType + "," + repetitionCoding;
            }
        }
        return str;
    }

    /**
     * Generate HBI row
     * 
     * @return row
     */
    private String generateHBI() {
        String str = NemoEvents.HBI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer reportingInterval = generateInteger(1, maxIntegerValue);
            Float happyBit = generateFloat(0, 99);
            Float dtx = generateFloat(0, 99);
            str = str + "," + reportingInterval + "," + happyBit + "," + dtx;
        }
        return str;
    }

    /**
     * Generate MACERATE row
     * 
     * @return row
     */
    private String generateMACERATE() {
        String str = NemoEvents.MACERATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            Integer macEBitrate = generateInteger(1, maxIntegerValue);
            Integer macEBlockrate = generateInteger(1, maxIntegerValue);
            Float macEFirstRetr = generateFloat(0, 99);
            Float macESecondRetr = generateFloat(0, 99);
            Float macEThirdRetr = generateFloat(0, 99);
            str = str + "," + macEBitrate + "," + macEBlockrate + "," + macEFirstRetr + "," + macESecondRetr + "," + macEThirdRetr;
        }
        return str;
    }

    /**
     * Generate AGRANT row
     * 
     * @return row
     */
    private String generateAGRANT() {
        String str = NemoEvents.AGRANT.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            Integer agchIndex = generateInteger(0, 31);
            Float agchGrant = generateFloat(-10, 29);
            Integer agchScope = generateInteger(-1, 7);
            Integer agchSelector = generateInteger(1, 2);
            Integer eRNTISelector = generateInteger(1, 2);
            str = str + "," + agchIndex + "," + agchGrant + "," + agchScope + "," + agchSelector + "," + eRNTISelector;
        }
        return str;
    }

    /**
     * Generate SGRANT row
     * 
     * @return row
     */
    private String generateSGRANT() {
        String str = NemoEvents.SGRANT.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer headerParams = 2;
            Integer sampleDur = generateInteger(1, maxIntegerValue);
            Integer grantedRate = generateInteger(1, maxIntegerValue);
            Integer numberOfSGSets = generateInteger(1, 10);
            Integer numberOfParamsPerSGSets = 3;
            str = str + "," + headerParams + "," + sampleDur + "," + grantedRate + "," + numberOfSGSets + ","
                    + numberOfParamsPerSGSets;
            for (int i = 0; i < numberOfSGSets; i++) {
                Float distribution = generateFloat(0, 99);
                Integer sgIndex = generateInteger(-1, 37);
                Float servingGrant = generateFloat(-10, 29);
                str = str + "," + distribution + "," + sgIndex + "," + servingGrant;
            }
        }
        return str;
    }

    /**
     * Generate EDCHI row
     * 
     * @return row
     */
    private String generateEDCHI() {
        String str = NemoEvents.EDCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer headerParams = 2;
            Float nsACKs = generateFloat(0, 99);
            Float nsGrantDown = generateFloat(0, 99);
            Integer numberOfCells = generateInteger(1, 10);
            Integer numberOfParamsPerCells = 9;
            str = str + "," + headerParams + "," + nsACKs + "," + nsGrantDown + "," + numberOfCells + "," + numberOfParamsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                Integer hsupaChannel = generateInteger(1, maxIntegerValue);
                Integer hsupaSc = generateInteger(0, 511);
                Integer hsupaRls = generateInteger(0, 5);
                Float ack = generateFloat(0, 99);
                Float nack = generateFloat(0, 99);
                Float dtx = generateFloat(0, 99);
                Float grantUp = generateFloat(0, 99);
                Float grantHold = generateFloat(0, 99);
                Float grantDown = generateFloat(0, 99);
                str = str + "," + hsupaChannel + "," + hsupaSc + "," + hsupaRls + "," + ack + "," + nack + "," + dtx + ","
                        + grantUp + "," + grantHold + "," + grantDown;
            }
        }
        return str;
    }

    /**
     * Generate HSUPASI row
     * 
     * @return row
     */
    private String generateHSUPASI() {
        String str = NemoEvents.HSUPASI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer dur = generateInteger(1, maxIntegerValue);
            Integer siCount = generateInteger(1, maxIntegerValue);
            Integer hlid = generateInteger(0, 15);
            Integer hlbs = generateInteger(0, 15);
            Integer tebs = generateInteger(0, 31);
            Integer tebsMin = generateInteger(0, 31);
            Integer tebsMax = generateInteger(0, 31);
            Integer uph = generateInteger(0, 31);
            Integer uphMin = generateInteger(0, 31);
            Integer uphMax = generateInteger(0, 31);
            str = str + "," + dur + "," + siCount + "," + hlid + "," + hlbs + "," + tebs + "," + tebsMin + "," + tebsMax + ","
                    + uph + "," + uphMin + "," + uphMax;
        }
        return str;
    }

    /**
     * Generate DRCI row
     * 
     * @return row
     */
    private String generateDRCI() {
        String str = NemoEvents.DRCI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        Integer headerParams = 1;
        Integer sampleDuration = generateInteger(1, maxIntegerValue);
        Integer numberOfDRCSets = generateInteger(1, 10);
        Integer numberOfParamsPerDRCSets = 3;
        str = str + "," + headerParams + "," + sampleDuration + "," + numberOfDRCSets + "," + numberOfParamsPerDRCSets;
        for (int i = 0; i < numberOfDRCSets; i++) {
            Float percentage = generateFloat(0, 99);
            Integer requestedRate = generateInteger(1, maxIntegerValue);
            Integer packetLength = generateInteger(0, 1);
            str = str + "," + percentage + "," + requestedRate + "," + packetLength;
        }
        return str;
    }

    /**
     * Generate RDRC row
     * 
     * @return row
     */
    private String generateRDRC() {
        String str = NemoEvents.RDRC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        if (system == 12) {
            Integer txRateLimit = generateInteger(0, 153600);
            Integer txCurrentRate = generateInteger(0, 153600);
            Integer combRAB = generateInteger(0, 1);
            Integer paMax = generateInteger(0, 153600);
            Integer randomVariable = generateInteger(0, 255);
            Integer transitionProbability = generateInteger(0, maxIntegerValue);
            Integer conditionRRI = generateInteger(0, 153600);
            Integer actualRRI = generateInteger(0, 153600);
            Integer paddingBytes = generateInteger(0, maxIntegerValue);
            str = str + "," + txRateLimit + "," + txCurrentRate + "," + combRAB + "," + paMax + "," + randomVariable + ","
                    + transitionProbability + "," + conditionRRI + "," + actualRRI + "," + paddingBytes;
        }
        return str;
    }

    /**
     * Generate FDRC row
     * 
     * @return row
     */
    private String generateFDRC() {
        String str = NemoEvents.FDRC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        if (system == 12) {
            Integer drcIndex = generateInteger(0, maxIntegerValue);
            Integer drcCover = generateInteger(0, 7);
            Integer dscValue = generateInteger(0, 7);
            Integer drcBoost = generateInteger(0, 1);
            Integer drcLockUpdSlot = generateInteger(0, maxIntegerValue);
            Integer ackChannelStatus = generateInteger(0, 1);
            Float forcedACKNAKRatio = generateFloat(0, 99);
            Float ackRatio = generateFloat(0, 99);
            Float multiuserACKRatio = generateFloat(0, 99);
            str = str + "," + drcIndex + "," + drcCover + "," + dscValue + "," + drcBoost + "," + drcLockUpdSlot + ","
                    + ackChannelStatus + "," + forcedACKNAKRatio + "," + ackRatio + "," + multiuserACKRatio;
        }
        return str;
    }

    /*
     * private String generatePHFER() { String str = NemoEvents.PHFER.getEventId() + "," +
     * generateTimestamp() + ","; Integer system = TechnologySystems.EVDO.getId(); Integer choice =
     * generateInteger(1,2); if(choice==2){ system=TechnologySystems.WIMAX.getId(); } str = str +
     * "," + system.toString(); if (system == 12) { String perInst = generateFloat(0,
     * 99).toString(); String perShort = generateFloat(0, 99).toString(); String perLong =
     * generateFloat(0, 99).toString(); str = str + "," + perInst + "," + perShort + "," + perLong;
     * } if(system==25){ String fer = generateFloat(0, 99).toString(); str=str+","+fer; } return
     * str; }
     */

    /**
     * Generate MARKOVMUX row
     * 
     * @return row
     */
    private String generateMARKOVMUX() {
        String str = NemoEvents.MARKOVMUX.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 11);
        str = str + "," + system;
        if (system == 10 || system == 11) {
            Integer headerParams = 0;
            Integer numberOfFrames = generateInteger(1, 10);
            Integer numberOfParamsPerFrames = 2;
            str = str + "," + headerParams + "," + numberOfFrames + "," + numberOfParamsPerFrames;
            for (int i = 0; i < numberOfFrames; i++) {
                Integer mExpectetedMux = generateInteger(0, 9);
                Integer mActualMux = generateInteger(0, 38);
                str = str + "," + mExpectetedMux + "," + mActualMux;
            }
        }
        return str;
    }

    /**
     * Generate MARKOVSTATS row
     * 
     * @return row
     */
    private String generateMARKOVSTATS() {
        String str = NemoEvents.MARKOVSTATS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 11);
        str = str + "," + system;
        Integer headerParams = 1;
        Float mFer = generateFloat(0, 99);
        Integer numberOfExpectedValues = generateInteger(1, 10);
        Integer numberOfParams = 6;
        str = str + "," + headerParams + "," + mFer + "," + numberOfExpectedValues + "," + numberOfParams;
        for (int i = 0; i < numberOfExpectedValues; i++) {
            Integer mExpected = generateInteger(1, 4);
            Integer m11 = generateInteger(1, 4);
            Integer m12 = generateInteger(1, 4);
            Integer m14 = generateInteger(1, 4);
            Integer m18 = generateInteger(1, 4);
            Integer mErasures = generateInteger(1, 4);
            str = str + "," + mExpected + "," + m11 + "," + m12 + "," + m14 + "," + m18 + "," + mErasures;
        }
        return str;
    }

    /**
     * Generate MER row
     * 
     * @return row
     */
    private String generateMER() {
        String str = NemoEvents.MER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.TETRA.getId();
        str = str + "," + system;
        if (system == 2) {
            Float mer = generateFloat(0, 99);
            str = str + "," + mer;
        }
        return str;
    }

    /**
     * Generate DVBI row
     * 
     * @return row
     */
    private String generateDVBI() {
        String str = NemoEvents.DVBI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system;
        if (system == 65) {
            Integer serviceState = generateInteger(1, 2);
            Float frequency = generateFloat(0, 100);
            Float bandwidth = generateFloat(0, 100);
            Integer cellID = generateInteger(1, maxIntegerValue);
            // 4,8...
            Integer txMode = generateInteger(2, 2);
            Integer modulation = generateInteger(1, 3);
            Integer codeRateLP = generateInteger(1, 5);
            Integer codeRateHP = generateInteger(0, 5);
            Integer guardTime = generateInteger(1, 4);
            Integer mpeFECCodeRateLP = generateInteger(0, 5);
            Integer mpeFECCodeRateHP = generateInteger(0, 5);
            Integer hierarchy = generateInteger(0, 1);
            str = str + "," + serviceState + "," + frequency + "," + bandwidth + "," + cellID + "," + txMode + "," + modulation
                    + "," + codeRateLP + "," + codeRateHP + "," + guardTime + "," + mpeFECCodeRateLP + "," + mpeFECCodeRateHP + ","
                    + hierarchy;
        }
        return str;
    }

    /**
     * Generate DVBFER row
     * 
     * @return row
     */
    private String generateDVBFER() {
        String str = NemoEvents.DVBFER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system;
        if (system == 65) {
            Float fer = generateFloat(0, 99);
            Float mfer = generateFloat(0, 99);
            Integer frameCount = generateInteger(1, maxIntegerValue);
            str = str + "," + fer + "," + mfer + "," + frameCount;
        }
        return str;
    }

    /**
     * Generate DVBBER row
     * 
     * @return row
     */
    private String generateDVBBER() {
        String str = NemoEvents.DVBBER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system;
        if (system == 65) {
            Float ber = generateFloat(0, 99);
            Float vber = generateFloat(0, 99);
            str = str + "," + ber + "," + vber;
        }
        return str;
    }

    /**
     * Generate DVBRXL row
     * 
     * @return row
     */
    private String generateDVBRXL() {
        String str = NemoEvents.DVBRXL.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system;
        if (system == 65) {
            Integer headerParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 4;
            str = str + "," + headerParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Float frequency = generateFloat(0, 100);
                Float rxLev = generateFloat(-111, -11);
                Float cn = generateFloat(0, 39);
                Float signalQuality = generateFloat(0, 99);
                str = str + "," + frequency + "," + rxLev + "," + cn + "," + signalQuality;
            }
        }
        return str;
    }

    /**
     * Generate DVBRATE row
     * 
     * @return row
     */
    private String generateDVBRATE() {
        String str = NemoEvents.DVBRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system;
        if (system == 65) {
            Integer dvbHRate = generateInteger(1, maxIntegerValue);
            str = str + "," + dvbHRate;
        }
        return str;
    }

    /**
     * Generate FREQSCAN row
     * 
     * @return row
     */
    private String generateFREQSCAN() {
        String str = NemoEvents.FREQSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer choice = generateInteger(1, 4);
        Integer system = TechnologySystems.GSM.getId();
        if (choice == 2) {
            Integer systemID = generateInteger(2, 6);
            system = systems.get(systemID);
        }
        if (choice == 3) {
            system = TechnologySystems.WIMAX.getId();
        }
        if (choice == 4) {
            Integer systemID = generateInteger(11, 13);
            system = systems.get(systemID);
        }
        str = str + "," + system;
        if (system == 1) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 4;
            str = str + "," + numberOfHeaderParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer arfcn = generateInteger(1, maxIntegerValue);
                Integer bsic = generateInteger(0, 63);
                Float rxLevel = generateFloat(-120, -11);
                Float ci = generateFloat(-10, 39);
                str = str + "," + arfcn + "," + bsic + "," + rxLevel + "," + ci;
            }
        }
        if (system == 5 || system == 6) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 2;
            str = str + "," + numberOfHeaderParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer ch = generateInteger(1, maxIntegerValue);
                Float rssi = generateFloat(-120, -11);
                str = str + "," + ch + "," + rssi;
            }
        }
        if (system == 10 || system == 11) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 2;
            str = str + "," + numberOfHeaderParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer ch = generateInteger(1, maxIntegerValue);
                Float rssi = generateFloat(-120, -1);
                str = str + "," + ch + "," + rssi;
            }
        }
        if (system == 12) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 2;
            str = str + "," + numberOfHeaderParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer carrier = generateInteger(1, maxIntegerValue);
                Float rssi = generateFloat(-120, -1);
                str = str + "," + carrier + "," + rssi;
            }
        }
        if (system == 25) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 2;
            str = str + "," + numberOfHeaderParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer frequency = generateInteger(1, maxIntegerValue);
                Float rssi = generateFloat(-120, -1);
                str = str + "," + frequency + "," + rssi;
            }
        }
        if (system == 51 || system == 52) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 3;
            str = str + "," + numberOfHeaderParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer ch = generateInteger(1, maxIntegerValue);
                Integer sat = generateInteger(0, 6);
                Float rxLevel = generateFloat(-120, -11);
                str = str + "," + ch + "," + sat + "," + rxLevel;
            }
        }
        if (system == 53) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfChs = generateInteger(1, 10);
            Integer numberOfParamsPerChs = 3;
            str = str + "," + numberOfHeaderParams + "," + numberOfChs + "," + numberOfParamsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                Integer ch = generateInteger(1, maxIntegerValue);
                Integer dcc = generateInteger(0, 255);
                Float rxLevel = generateFloat(-120, -11);
                str = str + "," + ch + "," + dcc + "," + rxLevel;
            }
        }
        return str;
    }

    /**
     * Generate SPECTRUMSCAN row
     * 
     * @return row
     */
    private String generateSPECTRUMSCAN() {
        String str = NemoEvents.SPECTRUMSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer scanningMode = generateInteger(1, 1);
        Integer headerParams = 4;
        Float bandwidth = generateFloat(0, 100);
        Float sweepBandwidth = generateFloat(0, 100);
        Float sweepFrequency = generateFloat(0, 100);
        Float sweepTotalRxLevel = generateFloat(-120, -11);
        Integer numberOfFrequencies = generateInteger(1, 10);
        Integer numberParamsPerFrequencies = 2;
        str = str + "," + scanningMode + "," + headerParams + "," + bandwidth + "," + sweepBandwidth + "," + sweepFrequency + ","
                + sweepTotalRxLevel + "," + numberOfFrequencies + "," + numberParamsPerFrequencies;
        for (int i = 0; i < numberOfFrequencies; i++) {
            Float frequency = generateFloat(0, 100);
            Float rxLevel = generateFloat(-120, -11);
            str = str + "," + frequency + "," + rxLevel;
        }
        return str;
    }

    /**
     * Generate PILOTSCAN row
     * 
     * @return row
     */
    private String generatePILOTSCAN() {
        String str = NemoEvents.PILOTSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer systemID = generateInteger(2, 6);
        Integer system = systems.get(systemID);
        str = str + "," + system;
        if (system == 5) {
            Integer numberOfHeadersParams = 3;
            Integer ch = generateInteger(1, maxIntegerValue);
            Integer chType = generateInteger(1, 5);
            Float rssi = generateFloat(-120, -11);
            Integer numberOfCells = generateInteger(1, 10);
            Integer numberParamsPerCells = 6;
            str = str + "," + numberOfHeadersParams + "," + ch + "," + chType + "," + rssi + "," + numberOfCells + ","
                    + numberParamsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                Integer scr = generateInteger(0, 511);
                Float ecn0 = generateFloat(-26, -1);
                Float rscp = generateFloat(-150, -21);
                Float sir = generateFloat(0, 29);
                Float delay = generateFloat(0, 38399);
                Float delaySpread = generateFloat(0, 100);
                str = str + "," + scr + "," + ecn0 + "," + rscp + "," + sir + "," + delay + "," + delaySpread;
            }
        }
        if (system == 6) {
            Integer numberOfHeadersParams = 1;
            Integer channelType = generateInteger(1, 2);
            Integer numberOfCells = generateInteger(1, 10);
            Integer numberParamsPerCells = 7;
            str = str + "," + numberOfHeadersParams + "," + channelType + "," + numberOfCells + "," + numberParamsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                Integer ch = generateInteger(1, maxIntegerValue);
                Integer cellParamsID = generateInteger(0, 127);
                Float eci0 = generateFloat(-30, -1);
                Float timeOffset = generateFloat(0, 6499);
                Float sir = generateFloat(-30, 24);
                Float rscp = generateFloat(-116, -21);
                Float rssi = generateFloat(-120, -11);
                str = str + "," + ch + "," + cellParamsID + "," + eci0 + "," + timeOffset + "," + sir + "," + rscp + "," + rssi;
            }
        }
        if (system == 10 || system == 11 || system == 12) {
            Integer numberOfHeadersParams = 2;
            Integer ch = generateInteger(1, maxIntegerValue);
            Float rssi = generateFloat(-120, -11);
            Integer numberOfCells = generateInteger(1, 10);
            Integer numberParamsPerCells = 3;
            str = str + "," + numberOfHeadersParams + "," + ch + "," + rssi + "," + numberOfCells + "," + numberParamsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                Integer pn = generateInteger(1, maxIntegerValue);
                Float eci0 = generateFloat(-35, 2);
                Float delay = generateFloat(0, 38399);
                str = str + "," + pn + "," + eci0 + "," + delay;
            }
        }
        return str;
    }

    /**
     * Generate OFDMSCAN row
     * 
     * @return row
     */
    private String generateOFDMSCAN() {
        String str = NemoEvents.OFDMSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.WIMAX.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.DVB_H.getId();
        }
        str = str + "," + system;
        if (system == 25) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfFrequencies = generateInteger(1, 10);
            Integer numberParamsPerFrequencies = 2;
            str = str + "," + numberOfHeadersParams + "," + numberOfFrequencies + "," + numberParamsPerFrequencies;
            for (int i = 0; i < numberOfFrequencies; i++) {
                Float frequency = generateFloat(0, 100);
                Float rssi = generateFloat(-120, -11);
                str = str + "," + frequency + "," + rssi;
            }
            Integer numberOfPreambles = generateInteger(1, 10);
            Integer numberParamsPerPreambles = 5;
            str = str + "," + numberOfPreambles + "," + numberParamsPerPreambles;
            for (int j = 0; j < numberOfPreambles; j++) {
                Float frequency2 = generateFloat(0, 100);
                Integer preambleIndex = generateInteger(0, 113);
                Float preambleRSSI = generateFloat(-120, -1);
                Float cinr = generateFloat(-32, 39);
                Float delay = generateFloat(0, 1054);
                str = str + "," + frequency2 + "," + preambleIndex + "," + preambleRSSI + "," + cinr + "," + delay;
            }
        }
        if (system == 65) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfFrequencies = generateInteger(1, 10);
            Integer numberParamsPerFrequencies = 3;
            str = str + "," + numberOfHeadersParams + "," + numberOfFrequencies + "," + numberParamsPerFrequencies;
            for (int i = 0; i < numberOfFrequencies; i++) {
                Float frequency = generateFloat(0, 100);
                Float rssi = generateFloat(0, 100);
                Float mer = generateFloat(0, 59);
                str = str + "," + frequency + "," + rssi + "," + mer;
            }
        }
        return str;
    }

    /**
     * Generate TPROFSCAN row
     * 
     * @return row
     */
    private String generateTPROFSCAN() {
        String str = NemoEvents.TPROFSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer numberOfHeadersParams = 3;
            Integer ch = generateInteger(0, maxIntegerValue);
            Float rssi = generateFloat(-120, -1);
            Integer chType = generateInteger(1, 5);
            Integer numberParamsPerSamples = 2;
            Integer numberOfSamples = generateInteger(1, 10);
            str = str + "," + numberOfHeadersParams + "," + ch + "," + rssi + "," + chType + "," + numberParamsPerSamples + ","
                    + numberOfSamples;
            for (int i = 0; i < numberOfSamples; i++) {
                Integer chip = generateInteger(0, maxIntegerValue);
                Float ecn0 = generateFloat(-26, -1);
                str = str + "," + chip + "," + ecn0;
            }
        }
        return str;
    }

    /**
     * Generate DPROFSCAN row
     * 
     * @return row
     */
    private String generateDPROFSCAN() {
        String str = NemoEvents.DPROFSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = TechnologySystems.CDMA_ONE.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.CDMA_ONE_X.getId();
        }
        str = str + "," + system;
        if (system == 5) {
            Integer numberOfHeadersParams = 3;
            Integer ch = generateInteger(0, maxIntegerValue);
            Integer scr = generateInteger(0, 511);
            Integer chType = generateInteger(1, 5);
            Integer numberParamsPerSamples = 2;
            Integer numberOfSamples = generateInteger(1, 10);
            str = str + "," + numberOfHeadersParams + "," + ch + "," + scr + "," + chType + "," + numberParamsPerSamples + ","
                    + numberOfSamples;
            for (int i = 0; i < numberOfSamples; i++) {
                Float sampleOffset = generateFloat(-550, 549);
                Float sample = generateFloat(0, 100);
                str = str + "," + sampleOffset + "," + sample;
            }
        }
        if (system == 10 || system == 11 || system == 12) {
            Integer numberOfHeadersParams = 0;
            Integer numberParamsPerSamples = 2;
            Integer numberOfSamples = generateInteger(1, 10);
            str = str + "," + numberOfHeadersParams + "," + numberParamsPerSamples + "," + numberOfSamples;
            for (int i = 0; i < numberOfSamples; i++) {
                Integer sampleOffset = generateInteger(0, 32768);
                Float sampleEnergy = generateFloat(-35, 2);
                str = str + "," + sampleOffset + "," + sampleEnergy;
            }
        }
        return str;
    }

    /**
     * Generate FINGER row
     * 
     * @return row
     */
    private String generateFINGER() {
        String str = NemoEvents.FINGER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 4);
        if (choice == 2) {
            system = TechnologySystems.CDMA_ONE.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.CDMA_ONE_X.getId();
        }
        if (choice == 4) {
            system = TechnologySystems.EVDO.getId();
        }
        str = str + "," + system;
        if (system == 5) {
            Integer numberOfHeaderParams = 0;
            Integer numberOfFingers = generateInteger(0, 10);
            Integer numberHeaderParamsPerFingers = 7;
            str = str + "," + numberOfHeaderParams + "," + numberOfFingers + "," + numberHeaderParamsPerFingers;
            for (int i = 0; i < numberOfFingers; i++) {
                Integer ch = generateInteger(0, maxIntegerValue);
                Integer scr = generateInteger(0, 511);
                Integer secondaryScr = generateInteger(0, 15);
                Float ecn0 = generateFloat(-26, -1);
                Float fingerAbsOffset = generateFloat(0, 100);
                Float fingerRelOffset = generateFloat(0, 100);
                Float fingerRSCP = generateFloat(0, 100);
                str = str + "," + ch + "," + scr + "," + secondaryScr + "," + ecn0 + "," + fingerAbsOffset + "," + fingerRelOffset
                        + "," + fingerRSCP;
            }
        }
        if (system == 10 || system == 11) {
            Integer numberOfHeaderParams = 2;
            Float aggEcI0 = generateFloat(-32, -1);
            Integer antConfig = generateInteger(0, 4);
            Integer numberOfFingers = generateInteger(0, 10);
            Integer numberHeaderParamsPerFingers = 12;
            str = str + "," + numberOfHeaderParams + "," + aggEcI0 + "," + antConfig + "," + numberOfFingers + ","
                    + numberHeaderParamsPerFingers;
            for (int i = 0; i < numberOfFingers; i++) {
                Integer pn = generateInteger(0, maxIntegerValue);
                Float fingerAbsOffset = generateFloat(0, 100);
                Integer fingerLocked = generateInteger(0, 1);
                Float ecI0 = generateFloat(-32, -1);
                Integer refFinger = generateInteger(0, 1);
                Integer assignedFinger = generateInteger(0, 1);
                Integer tdMode = generateInteger(0, 3);
                Float tdPower = generateFloat(-9, -1);
                Integer subchannel = 1;
                Integer lockedAntennas = generateInteger(0, 1);
                Float rx0EcI0 = generateFloat(-32, -1);
                Float rx1EcI0 = generateFloat(-32, -1);
                str = str + "," + pn + "," + fingerAbsOffset + "," + fingerLocked + "," + ecI0 + "," + refFinger + ","
                        + assignedFinger + "," + tdMode + "," + tdPower + "," + subchannel + "," + lockedAntennas + "," + rx0EcI0
                        + "," + rx1EcI0;
            }
        }
        if (system == 12) {
            Integer numberOfHeaderParams = 5;
            Integer searcherState = generateInteger(0, 12);
            Integer mstr = generateInteger(0, maxIntegerValue);
            Integer mstrError = generateInteger(0, maxIntegerValue);
            Integer mstrPN = generateInteger(0, 511);
            Integer antConfig = generateInteger(0, 4);
            Integer numberOfFingers = generateInteger(0, 10);
            Integer numberHeaderParamsPerFingers = 9;
            str = str + "," + numberOfHeaderParams + "," + searcherState + "," + mstr + "," + mstrError + "," + mstrPN + ","
                    + antConfig + "," + numberOfFingers + "," + numberHeaderParamsPerFingers;
            for (int i = 0; i < numberOfFingers; i++) {
                Integer pn = generateInteger(0, maxIntegerValue);
                Integer fingerIndex = generateInteger(0, 11);
                Integer rpcCellIndex = generateInteger(1, 6);
                Integer aspIndex = generateInteger(1, 6);
                Float ecI0 = generateFloat(-32, -1);
                Float rx0EcI0 = generateFloat(-32, -1);
                Float rx1EcI0 = generateFloat(-32, -1);
                Integer fingerLocked = generateInteger(0, 1);
                Float fingerAbsOffset = generateFloat(0, 100);
                str = str + "," + pn + "," + fingerIndex + "," + rpcCellIndex + "," + aspIndex + "," + ecI0 + "," + rx0EcI0 + ","
                        + rx1EcI0 + "," + fingerLocked + "," + fingerAbsOffset;
            }
        }
        return str;
    }

    /**
     * Generate UISCAN row
     * 
     * @return row
     */
    private String generateUISCAN() {
        String str = NemoEvents.UISCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        Integer paramsPerCells = 3;
        Integer cells = generateInteger(0, 10);
        str = str + "," + paramsPerCells + "," + cells;
        for (int i = 0; i < cells; i++) {
            Integer arfcn = generateInteger(1, maxIntegerValue);
            Integer scr = generateInteger(0, 511);
            Float ulInterf = generateFloat(-110, -53);
            str = str + "," + arfcn + "," + scr + "," + ulInterf;
        }
        return str;
    }

    /**
     * Generate CELLSCAN row
     * 
     * @return row
     */
    private String generateCELLSCAN() {
        String str = NemoEvents.CELLSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParamsPerCells = 6;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParamsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                Integer arfcn = generateInteger(0, maxIntegerValue);
                Integer bsic = generateInteger(0, 63);
                Integer mcc = generateInteger(0, 4095);
                Integer mnc = generateInteger(0, 999);
                Integer lac = generateInteger(0, 65535);
                Integer cellID = generateInteger(0, 65535);
                str = str + "," + arfcn + "," + bsic + "," + mcc + "," + mnc + "," + lac + "," + cellID;
            }
        }
        if (system == 5) {
            Integer numberOfHeadersParams = 0;
            Integer numberOfCells = generateInteger(0, 10);
            Integer numberOfParamsPerCells = 6;
            str = str + "," + numberOfHeadersParams + "," + numberOfCells + "," + numberOfParamsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                Integer arfcn = generateInteger(0, maxIntegerValue);
                Integer scr = generateInteger(0, 511);
                Integer mcc = generateInteger(0, 4095);
                Integer mnc = generateInteger(0, 999);
                Integer lac = generateInteger(0, 65535);
                Integer cellID = generateInteger(0, 268435455);
                str = str + "," + arfcn + "," + scr + "," + mcc + "," + mnc + "," + lac + "," + cellID;
            }
        }
        return str;
    }

    /**
     * Generate HOA row
     * 
     * @return row
     */
    private String generateHOA() {
        String str = NemoEvents.HOA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer headerParams = 1;
        // 201,202,...
        Integer hoaType = generateInteger(101, 104);
        Integer system = generateTechnologySystems();
        str = str + "," + headerParams + "," + hoaType + "," + system;
        if (system == 1 || system == 2 || system == 53) {
            Integer numberOfCurrentSystemParameters = 2;
            Integer chNumber = generateInteger(1, maxIntegerValue);
            Integer tsl = generateInteger(0, 7);
            str = str + "," + numberOfCurrentSystemParameters + "," + chNumber + "," + tsl;
        }
        if (system == 5) {
            Integer numberOfCurrentSystemParameters = 2;
            Integer chNumber = generateInteger(1, maxIntegerValue);
            Integer sc = generateInteger(0, 511);
            str = str + "," + numberOfCurrentSystemParameters + "," + chNumber + "," + sc;
        }
        if (system == 6) {
            Integer numberOfCurrentSystemParameters = 2;
            Integer chNumber = generateInteger(1, maxIntegerValue);
            Integer cellParamsID = generateInteger(0, 127);
            str = str + "," + numberOfCurrentSystemParameters + "," + chNumber + "," + cellParamsID;
        }
        if (system == 10 || system == 11 || system == 12) {
            Integer numberOfCurrentSystemParameters = 1;
            Integer chNumber = generateInteger(1, maxIntegerValue);
            str = str + "," + numberOfCurrentSystemParameters + "," + chNumber;
        }
        if (system == 21) {
            Integer numberOfCurrentSystemParameters = 0;
            str = str + "," + numberOfCurrentSystemParameters;
        }
        system = generateTechnologySystems();
        str = str + "," + system;
        if (system == 1 || system == 2 || system == 53) {
            Integer numberOfAttemptedSystemParams = 2;
            Integer attCh = generateInteger(1, maxIntegerValue);
            Integer attTsl = generateInteger(0, 7);
            str = str + "," + numberOfAttemptedSystemParams + "," + attCh + "," + attTsl;
        }
        if (system == 5) {
            Integer numberOfAttemptedSystemParams = 2;
            Integer attCh = generateInteger(1, maxIntegerValue);
            Integer attSc = generateInteger(0, 511);
            str = str + "," + numberOfAttemptedSystemParams + "," + attCh + "," + attSc;
        }
        if (system == 6) {
            Integer numberOfAttemptedSystemParams = 2;
            Integer attCh = generateInteger(1, maxIntegerValue);
            Integer attCellParamsID = generateInteger(0, 127);
            str = str + "," + numberOfAttemptedSystemParams + "," + attCh + "," + attCellParamsID;
        }
        if (system == 10 || system == 11 || system == 12) {
            Integer numberOfAttemptedSystemParams = 1;
            Integer attCh = generateInteger(1, maxIntegerValue);
            str = str + "," + numberOfAttemptedSystemParams + "," + attCh;
        }
        if (system == 21) {
            Integer numberOfAttemptedSystemParams = 0;
            str = str + "," + numberOfAttemptedSystemParams;
        }
        return str;
    }

    /**
     * Generate HOS row
     * 
     * @return row
     */
    private String generateHOS() {
        String str = NemoEvents.HOS.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        return str;
    }

    private String generateHOF() {
        String str = NemoEvents.HOF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateTechnologySystems();
        if (system == 1 || system == 21) {
            // 65,95,...
            Integer rrCause = generateInteger(0, 12);
            str = str + "," + rrCause;
        }
        if (system == 2 || system == 11 || system == 53) {
            String reserved = "n/a";
            str = str + "," + reserved;
        }
        if (system == 5 || system == 6) {
            Integer rrcCause = generateInteger(0, 10);
            str = str + "," + rrcCause;
        }
        return str;
    }

    /**
     * Generate CREL row
     * 
     * @return row
     */
    private String generateCREL() {
        String str = NemoEvents.CREL.getEventId() + "," + generateTimestamp() + ",";
        Integer headerParams = 0;
        str = str + "," + headerParams;
        Integer oldSystem = TechnologySystems.GAN_WLAN.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            oldSystem = systems.get(generateInteger(0, 3));
        }
        str = str + "," + oldSystem;
        Integer params = 2;
        Integer oldLAC = generateInteger(0, 65535);
        Integer oldCI = generateInteger(0, maxIntegerValue);
        str = str + "," + params + "," + oldLAC + "," + oldCI;
        Integer system = TechnologySystems.GAN_WLAN.getId();
        Integer choice2 = generateInteger(1, 2);
        if (choice2 == 2) {
            system = systems.get(generateInteger(0, 3));
        }
        str = str + "," + system;
        Integer params2 = 2;
        Integer lac = generateInteger(0, 65535);
        Integer ci = generateInteger(0, maxIntegerValue);
        str = str + "," + params2 + "," + lac + "," + ci;
        return str;
    }

    /**
     * Generate SHO row
     * 
     * @return row
     */
    private String generateSHO() {
        String str = NemoEvents.SHO.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = generateInteger(10, 11);
        }
        str = str + "," + system;
        if (system == 5) {
            Integer shoStatus = generateInteger(1, 2);
            Integer rrcCause = generateInteger(0, 10);
            Integer numberOfSCsAdded = generateInteger(1, 10);
            Integer numberOfSCsRemoved = generateInteger(1, 10);
            str = str + "," + shoStatus + "," + rrcCause + "," + numberOfSCsAdded + "," + numberOfSCsRemoved;
            for (int i = 0; i < numberOfSCsAdded; i++) {
                Integer addedSC = generateInteger(1, 100);
                str = str + "," + addedSC;
            }
            for (int j = 0; j < numberOfSCsRemoved; j++) {
                Integer removeSC = generateInteger(1, 100);
                str = str + "," + removeSC;
            }
        }
        if (system == 10 || system == 11) {
            Integer numberOfPilotAdded = generateInteger(1, 10);
            Integer numberOfPilotRemoved = generateInteger(1, 10);
            str = str + "," + numberOfPilotAdded + "," + numberOfPilotRemoved;
            for (int i = 0; i < numberOfPilotAdded; i++) {
                Integer addedPN = generateInteger(1, 100);
                str = str + "," + addedPN;
            }
            for (int j = 0; j < numberOfPilotRemoved; j++) {
                Integer removePN = generateInteger(1, 100);
                str = str + "," + removePN;
            }
        }
        return str;
    }

    /**
     * Generate LUA row
     * 
     * @return row
     */
    private String generateLUA() {
        String str = NemoEvents.LUA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = systems.get(generateInteger(0, 3));
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        Integer luaType = null;
        if (system == 2) {
            luaType = generateInteger(5, 10);
        } else {
            luaType = generateInteger(1, 4);
        }
        str = str + "," + luaType;
        return str;
    }

    /**
     * Generate LUS row
     * 
     * @return row
     */
    private String generateLUS() {
        String str = NemoEvents.LUS.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = systems.get(generateInteger(0, 3));
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        Integer oldLAC = generateInteger(0, 65535);
        Integer lac = generateInteger(0, 65535);
        Integer mcc = generateInteger(0, 4095);
        Integer mnc = generateInteger(0, 999);
        str = str + "," + oldLAC + "," + lac + "," + mcc + "," + mnc;
        return str;
    }

    /**
     * Generate LUF row
     * 
     * @return
     */
    private String generateLUF() {
        String str = NemoEvents.LUF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = systems.get(generateInteger(0, 3));
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        Integer lufStatus = generateInteger(1, 3);
        Integer oldLAC = generateInteger(0, 65535);
        str = str + "," + oldLAC + "," + lufStatus + "," + oldLAC;
        if (system == 1 || system == 2 || system == 5 || system == 6 || system == 21) {
            Integer mmCause = generateInteger(1, 17);
            str = str + "," + mmCause;
        }
        return str;
    }

    /**
     * Generate CHI row
     * 
     * @return row
     */
    private String generateCHI() {
        String str = NemoEvents.CHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = systems.get(generateInteger(0, 6));
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = systems.get(generateInteger(8, 9));
        }
        if (choice == 3) {
            system = systems.get(generateInteger(11, 13));
        }
        if (system == 1) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer chType = generateInteger(1, 2);
            Integer ch = generateInteger(1, maxIntegerValue);
            Integer cellID = generateInteger(0, 65535);
            Integer dtxUL = generateInteger(0, 1);
            Integer rltMax = generateInteger(4, 64);
            // 10,11,...
            Integer extChType = generateInteger(1, 5);
            Integer tn = generateInteger(1, maxIntegerValue);
            Integer bcchCh = generateInteger(1, maxIntegerValue);
            str = str + "," + band + "," + chType + "," + ch + "," + cellID + "," + dtxUL + "," + rltMax + "," + extChType + ","
                    + tn + "," + bcchCh;
        }
        if (system == 2) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer subchannel = generateInteger(1, 2);
            Integer ch = generateInteger(1, maxIntegerValue);
            Integer lac = generateInteger(0, 65535);
            Integer extendedSubchannel = generateInteger(0, 7);
            Integer encryption = generateInteger(0, 1);
            Integer slotNumber = generateInteger(1, 4);
            str = str + "," + band + "," + subchannel + "," + ch + "," + ch + "," + lac + "," + extendedSubchannel + ","
                    + encryption + "," + slotNumber;
        }
        if (system == 5) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer rrcState = generateInteger(1, 5);
            Integer ch = generateInteger(1, maxIntegerValue);
            Integer cellID = generateInteger(0, 268435455);
            Integer lac = generateInteger(0, 65535);
            Float additionWindow = generateFloat(-2, 13);
            Integer timeToTrigger1A = generateInteger(0, 5000);
            Float dropWindow = generateFloat(0, 16);
            Integer timeToTrigger1B = generateInteger(0, 5000);
            Float replacementWindow = generateFloat(0, 1);
            Integer timeToTrigger1C = generateInteger(0, 5000);
            Integer dlSF = generateInteger(0, 512);
            Integer minUlSF = generateInteger(4, 256);
            Integer drxCycle = generateInteger(0, 512);
            Float maxTXPower = generateFloat(-50, 32);
            Integer treselection = generateInteger(1, maxIntegerValue);
            str = str + "," + band + "," + rrcState + "," + ch + "," + cellID + "," + lac + "," + additionWindow + ","
                    + timeToTrigger1A + "," + dropWindow + "," + timeToTrigger1B + "," + replacementWindow + "," + timeToTrigger1C
                    + "," + dlSF + "," + minUlSF + "," + drxCycle + "," + maxTXPower + "," + treselection;
        }
        if (system == 6) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer rrcState = generateInteger(1, 5);
            Integer ch = generateInteger(1, maxIntegerValue);
            Integer cellParamsID = generateInteger(0, 127);
            Integer cellID = generateInteger(0, 268435455);
            Integer lac = generateInteger(0, 65535);
            Integer drxCycle = generateInteger(0, 512);
            Float maxTXPower = generateFloat(-50, 32);
            Integer treselection = generateInteger(1, maxIntegerValue);
            str = str + "," + band + "," + rrcState + "," + ch + "," + cellParamsID + "," + cellID + "," + lac + "," + drxCycle
                    + "," + maxTXPower + "," + drxCycle + "," + maxTXPower + "," + treselection;
        }
        if (system == 10 || system == 11) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer chType = generateInteger(1, 4);
            Integer ch = generateInteger(1, maxIntegerValue);
            Integer mcc = generateInteger(0, 4095);
            Integer sid = generateInteger(1, maxIntegerValue);
            Integer nid = generateInteger(1, maxIntegerValue);
            Integer slottedMode = generateInteger(0, 1);
            Integer search_WIN_A = generateInteger(1, maxIntegerValue);
            Integer search_WIN_N = generateInteger(1, maxIntegerValue);
            Integer search_WIN_R = generateInteger(1, maxIntegerValue);
            Integer tADD = generateInteger(1, maxIntegerValue);
            Integer tDROP = generateInteger(1, maxIntegerValue);
            Integer tTDROP = generateInteger(1, maxIntegerValue);
            Integer tCOMP = generateInteger(1, maxIntegerValue);
            Integer pREV = generateInteger(1, 11);
            Integer minPREV = generateInteger(1, 11);
            str = str + "," + band + "," + chType + "," + ch + "," + mcc + "," + sid + "," + nid + "," + slottedMode + ","
                    + search_WIN_A + "," + search_WIN_N + "," + search_WIN_R + "," + tADD + "," + tDROP + "," + tTDROP + ","
                    + tCOMP + "," + pREV + "," + minPREV;
        }
        if (system == 12) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            str = str + "," + band;
        }
        if (system == 21) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer cellID = generateInteger(0, 268435455);
            Integer lac = generateInteger(0, 65535);
            str = str + "," + band + "," + cellID + "," + lac;
        }
        if (system == 25) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            // 0,1,2,...
            Integer macState = generateInteger(10, 17);
            Float frequency = generateFloat(0, 100);
            String bsID = returnWordSoCalled("WiMAX base station ID");
            Integer fttSize = generateInteger(1, maxIntegerValue);
            Float bandwidth = generateFloat(-1, 100);
            Integer frameRatioDL = generateInteger(0, 100);
            Integer frameRatioUL = generateInteger(0, 100);
            Integer mapCoding = generateInteger(1, 5);
            Integer repetition = generateInteger(1, 3) * 2;
            // 1
            String mapRepetition = repetition.toString();
            str = str + "," + band + "," + macState + "," + frequency + "," + bsID + "," + fttSize + "," + bandwidth + ","
                    + frameRatioDL + "," + frameRatioUL + "," + mapCoding + "," + mapRepetition;
        }
        if (system == 51 || system == 52) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer chType = generateInteger(1, 2);
            Integer ch = generateInteger(1, maxIntegerValue);
            str = str + "," + band + "," + chType + "," + ch;
        }
        if (system == 53) {
            // 0,...
            Integer band = generateInteger(20001, 20015);
            Integer chType = generateInteger(1, 3);
            Integer ch = generateInteger(1, maxIntegerValue);
            Integer nwType = generateInteger(1, maxIntegerValue);
            Integer psid1 = generateInteger(1, maxIntegerValue);
            Integer psid2 = generateInteger(1, maxIntegerValue);
            Integer psid3 = generateInteger(1, maxIntegerValue);
            Integer psid4 = generateInteger(1, maxIntegerValue);
            Integer lareg = generateInteger(0, 1);
            Integer rnum = generateInteger(1, maxIntegerValue);
            Integer pegPeriod = generateInteger(1, maxIntegerValue);
            str = str + "," + band + "," + chType + "," + ch + "," + nwType + "," + psid1 + "," + psid2 + "," + psid3 + "," + psid4
                    + "," + lareg + "," + rnum + "," + pegPeriod;
        }
        return str;
    }

    /**
     * Generate GANCHI row
     * 
     * @return row
     */
    private String generateGANCHI() {
        String str = NemoEvents.GANCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GAN_WLAN.getId();
        str = str + "," + system;
        if (system == 21) {
            Integer headerParams = 7;
            Integer ganState = generateInteger(1, 4);
            Integer ganChannel = generateInteger(0, 1023);
            Integer ganBSIC = generateInteger(0, 63);
            Integer ganCI = generateInteger(0, 65535);
            Integer ganLAC = generateInteger(0, 65535);
            String gancIP = returnWordSoCalled("GANC IP address");
            String segwIP = returnWordSoCalled("GANC security gateway IP address");
            str = str + "," + headerParams + "," + ganState + "," + ganChannel + "," + ganBSIC + "," + ganCI + "," + ganLAC + ","
                    + gancIP + "," + segwIP;
        }
        return str;
    }

    /**
     * Generate SEI row
     * 
     * @return row
     */
    private String generateSEI() {
        String str = NemoEvents.SEI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateTechnologySystems();
        str = str + "," + system;
        Integer serviceStatus = generateInteger(1, 2);
        str = str + "," + serviceStatus;
        if (system == 1 || system == 5 || system == 6) {
            Integer lac = generateInteger(0, 65535);
            Integer mcc = generateInteger(0, 4095);
            Integer mnc = generateInteger(0, 999);
            str = str + "," + lac + "," + mcc + "," + mnc;
        }
        return str;
    }

    /**
     * Generate ROAM row
     * 
     * @return row
     */
    private String generateROAM() {
        String str = NemoEvents.ROAM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        str = str + "," + system;
        Integer roamingStatus = generateInteger(1, 2);
        str = str + "," + roamingStatus;
        return str;
    }

    /**
     * Generate DCHR row
     * 
     * @return row
     */
    private String generateDCHR() {
        String str = NemoEvents.DCHR.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Integer initiator = generateInteger(1, 2);
            Integer requestedCoding = generateInteger(1, 2);
            Integer requestedDataMode = generateInteger(0, 1);
            Integer requestedTSLUL = generateInteger(1, maxIntegerValue);
            Integer requestedTSLDL = generateInteger(1, maxIntegerValue);
            Integer modemType = generateInteger(0, 2);
            Integer compression = generateInteger(0, 3);
            str = str + "," + initiator + "," + requestedCoding + "," + requestedDataMode + "," + requestedTSLUL + ","
                    + requestedTSLDL + "," + modemType + "," + compression;
        }
        if (system == 5) {
            Integer initiator = generateInteger(1, 2);
            Integer reqCSRate = generateInteger(1, maxIntegerValue);
            Integer requestedDataMode = generateInteger(0, 1);
            Integer modemType = generateInteger(0, 2);
            Integer compression = generateInteger(0, 3);
            str = str + "," + initiator + "," + reqCSRate + "," + requestedDataMode + "," + modemType + "," + compression;
        }
        return str;
    }

    /**
     * Generate DCHI row
     * 
     * @return row
     */
    private String generateDCHI() {
        String str = NemoEvents.DCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        if (system == 1) {
            Integer coding = generateInteger(1, 2);
            Integer dataMode = generateInteger(0, 1);
            Integer csTSLUL = generateInteger(1, 10);
            Integer csTSLDL = generateInteger(1, 10);
            str = str + "," + coding + "," + dataMode + "," + csTSLUL + "," + csTSLDL;
            for (int i = 0; i < csTSLUL; i++) {
                Integer csTNs = generateInteger(0, 7);
                str = str + "," + csTNs;
            }
            for (int i = 0; i < csTSLDL; i++) {
                Integer csTNs = generateInteger(0, 7);
                str = str + "," + csTNs;
            }
        }
        return str;
    }

    /**
     * Generate HOP row
     * 
     * @return row
     */
    private String generateHOP() {
        String str = NemoEvents.HOP.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        if (system == 1) {
            Integer hopping = generateInteger(1, 2);
            str = str + "," + hopping;
            if (hopping == 1) {
                Integer hsn = generateInteger(1, maxIntegerValue);
                Integer maio = generateInteger(1, maxIntegerValue);
                Integer numberOfHoppingChs = generateInteger(1, 50);
                str = str + "," + hsn + "," + maio + "," + numberOfHoppingChs;
                for (int i = 0; i < numberOfHoppingChs; i++) {
                    Integer channel = generateInteger(1, 100);
                    str = str + "," + channel;
                }
            }
            if (hopping == 2) {
                Integer channel = generateInteger(1, 100);
                str = str + "," + channel;
            }
        }
        return str;
    }

    private String generateNLIST() {
        String str = NemoEvents.NLIST.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system.toString();
        if (system == 1) {
            Integer hopping = generateInteger(1, 2);
            String strHopping = hopping.toString();
            str = str + "," + strHopping;
            if (hopping == 1) {
                String hsn = generateInteger(1, maxIntegerValue).toString();
                String maio = generateInteger(1, maxIntegerValue).toString();
                Integer numberOfHoppingChs = generateInteger(1, 50);
                String hoppingChs = numberOfHoppingChs.toString();
                str = str + "," + hsn + "," + maio + "," + hoppingChs;
                for (int i = 0; i < numberOfHoppingChs; i++) {
                    String channel = generateInteger(1, 100).toString();
                    str = str + "," + channel;
                }
            }
            if (hopping == 2) {
                Integer numberOfHoppingChs = generateInteger(1, 50);
                for (int i = 0; i < numberOfHoppingChs; i++) {
                    String channel = generateInteger(1, 100).toString();
                    str = str + "," + channel;
                }
            }
        }
        return str;
    }

    /**
     * Generate SERVCONF row
     * 
     * @return row
     */
    private String generateSERVCONF() {
        String str = NemoEvents.SERVCONF.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.CDMA_ONE.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.CDMA_ONE_X.getId();
        }
        str = str + "," + system;
        if (system == 10 || system == 11) {
            Integer so = generateInteger(1, maxIntegerValue);
            Integer ftType = generateInteger(0, 2);
            Integer rtType = generateInteger(0, 2);
            Integer encryptionMode = generateInteger(0, 1);
            Integer fFCHMUX = generateInteger(1, maxIntegerValue);
            Integer rFCHMUX = generateInteger(1, maxIntegerValue);
            Integer fFCHBitsPerFrame = generateInteger(1, maxIntegerValue);
            Integer rFCHBitsPerFrame = generateInteger(1, maxIntegerValue);
            Integer fFCHRC = generateInteger(1, 10);
            Integer rFCHRC = generateInteger(1, 10);
            Integer fDCCHRC = generateInteger(1, 10);
            Integer rDCCHRadioConfiguration = generateInteger(1, 10);
            Integer fSCHMUX = generateInteger(1, maxIntegerValue);
            Integer fSCHRC = generateInteger(1, 10);
            Integer fSCHCoding = generateInteger(0, 1);
            Integer fSCHFrameSize = generateInteger(0, 2);
            Integer fSCHFrameOffset = generateInteger(1, maxIntegerValue);
            Integer fSCHMaxRate = (int)Math.pow(2, Double.parseDouble(generateInteger(0, 5).toString()));
            Integer rSCHMUX = generateInteger(1, maxIntegerValue);
            Integer rSCHRC = generateInteger(1, maxIntegerValue);
            Integer rSCHCoding = generateInteger(0, 1);
            Integer rSCHFrameSize = generateInteger(0, 2);
            Integer rSCHFrameOffset = generateInteger(1, maxIntegerValue);
            Integer rSCHMaxRate = (int)Math.pow(2, Double.parseDouble(generateInteger(0, 5).toString()));
            str = str + "," + so + "," + ftType + "," + rtType + "," + encryptionMode + "," + fFCHMUX + "," + rFCHMUX + ","
                    + fFCHBitsPerFrame + "," + rFCHBitsPerFrame + "," + fFCHRC + "," + rFCHRC + "," + fDCCHRC + ","
                    + rDCCHRadioConfiguration + "," + fSCHMUX + "," + fSCHRC + "," + fSCHCoding + "," + fSCHFrameSize + ","
                    + fSCHFrameOffset + "," + fSCHMaxRate + "," + rSCHMUX + "," + rSCHRC + "," + rSCHCoding + "," + rSCHFrameSize
                    + "," + rSCHFrameOffset + "," + rSCHMaxRate;
        }
        return str;
    }

    /**
     * Generate RACHI row
     * 
     * @return row
     */
    private String generateRACHI() {
        String str = NemoEvents.RACHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = systems.get(generateInteger(2, 6));
        str = str + "," + system;
        if (system == 5) {
            Float initTXPower = generateFloat(-99, 98);
            Float preambleStep = generateFloat(0, 100);
            Integer preambleCount = generateInteger(0, 65);
            Float rachTXPower = generateFloat(-99, 98);
            Integer maxPreamble = generateInteger(0, 64);
            Float ulInterf = generateFloat(-110, -71);
            Integer aichStatus = generateInteger(0, 2);
            Integer dataGain = generateInteger(0, 15);
            Integer ctrlGain = generateInteger(0, 15);
            Float powerOffset = generateFloat(-5, 9);
            Integer messageLength = generateInteger(5, 19);
            Integer preambleCycles = generateInteger(1, 32);
            str = str + "," + initTXPower + "," + preambleStep + "," + preambleCount + "," + rachTXPower + "," + maxPreamble + ","
                    + ulInterf + "," + aichStatus + "," + dataGain + "," + ctrlGain + "," + powerOffset + "," + messageLength + ","
                    + preambleCycles;
        }
        if (system == 6) {
            Float syncULInitPwr = generateFloat(-99, 98);
            Float syncULStep = generateFloat(0, 2);
            Integer syncULCount = generateInteger(1, 8);
            Integer maxSyncULCount = generateInteger(1, 8);
            Float syncULPower = generateFloat(-99, 98);
            Float rachTXPower = generateFloat(-99, 98);
            Float pccpchPathloss = generateFloat(46, 157);
            Integer rachStatus = generateInteger(0, 2);
            Float desiredUpPCHRXPower = generateFloat(-120, -59);
            Float desiredUpRACHRXPower = generateFloat(-120, -59);
            Integer messageLength = generateInteger(5, 20);
            Integer preambleCycles = generateInteger(1, 32);
            str = str + "," + syncULInitPwr + "," + syncULStep + "," + syncULCount + "," + maxSyncULCount + "," + syncULPower + ","
                    + rachTXPower + "," + pccpchPathloss + "," + rachStatus + "," + desiredUpPCHRXPower + ","
                    + desiredUpRACHRXPower + "," + messageLength + "," + preambleCycles;
        }
        if (system == 10 || system == 11) {
            Integer nomPWR = generateInteger(1, maxIntegerValue);
            Integer initPWR = generateInteger(1, maxIntegerValue);
            Integer pwrStep = generateInteger(1, maxIntegerValue);
            Integer numStep = generateInteger(1, maxIntegerValue);
            Float txLevel = generateFloat(0, 100);
            Integer probeCount = generateInteger(1, maxIntegerValue);
            Integer probeSeqCount = generateInteger(1, maxIntegerValue);
            Integer accessChNumber = generateInteger(1, maxIntegerValue);
            Integer randomDelay = generateInteger(1, maxIntegerValue);
            Float accessRxLevel = generateFloat(0, 100);
            Integer psist = generateInteger(0, 255);
            Integer seqBackoff = generateInteger(0, 255);
            Integer probBackoff = generateInteger(0, 255);
            Integer interCorr = generateInteger(0, 255);
            Float accessTXAdj = generateFloat(0, 100);
            str = str + "," + nomPWR + "," + initPWR + "," + pwrStep + "," + numStep + "," + txLevel + "," + probeCount + ","
                    + probeSeqCount + "," + accessChNumber + "," + randomDelay + "," + accessRxLevel + "," + psist + ","
                    + seqBackoff + "," + probBackoff + "," + interCorr + "," + accessTXAdj;
        }
        if (system == 12) {
            Integer maxProbes = generateInteger(1, maxIntegerValue);
            Integer maxProbeSeqs = generateInteger(1, maxIntegerValue);
            Integer result = generateInteger(0, 3);
            Integer probes = generateInteger(1, maxIntegerValue);
            Integer probeSeqs = generateInteger(1, maxIntegerValue);
            Integer duration = generateInteger(1, maxIntegerValue);
            Integer accessPN = generateInteger(1, maxIntegerValue);
            Integer accessSectorId = generateInteger(0, 16777215);
            Integer accessColorCode = generateInteger(0, 255);
            str = str + "," + maxProbes + "," + maxProbeSeqs + "," + result + "," + probes + "," + probeSeqs + "," + duration + ","
                    + accessPN + "," + accessSectorId + "," + accessColorCode;
        }
        return str;
    }

    /**
     * Generate VOCS row
     * 
     * @return row
     */
    private String generateVOCS() {
        String str = NemoEvents.VOCS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.CDMA_ONE.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.CDMA_ONE_X.getId();
        }
        str = str + "," + system;
        Integer vocRateFor = generateInteger(0, 6);
        Integer vocRateRev = generateInteger(0, 6);
        str = str + "," + vocRateFor + "," + vocRateRev;
        return str;
    }

    /**
     * Generate PHCHI row
     * 
     * @return row
     */
    private String generatePHCHI() {
        String str = NemoEvents.PHCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = systems.get(generateInteger(3, 5));
        str = str + "," + system;
        if (system == 6) {
            Integer headersParams = 5;
            Integer ulRepetitionLenght = generateInteger(1, maxIntegerValue);
            Integer ulRepetitionPeriod = generateInteger(1, 64);
            Integer dlRepetitionLenght = generateInteger(1, maxIntegerValue);
            Integer dlRepetitionPeriod = generateInteger(1, 64);
            Integer numberOfChannel = generateInteger(1, 10);
            Integer paramsPerChannel = 7;
            str = str + "," + headersParams + "," + ulRepetitionLenght + "," + ulRepetitionPeriod + "," + dlRepetitionLenght + ","
                    + dlRepetitionPeriod + "," + numberOfChannel + "," + paramsPerChannel;
            for (int i = 0; i < numberOfChannel; i++) {
                Integer phChType = 1;
                Integer direction = generateInteger(1, 3);
                Integer tsl = generateInteger(0, 6);
                Double strSf = Math.pow(2, generateInteger(0, 4));
                Integer chCode = generateInteger(0, 15);
                Integer midambleConfig = generateInteger(2, 16);
                Integer midambleShift = generateInteger(0, 15);
                str = str + "," + phChType + "," + direction + "," + tsl + "," + strSf + "," + chCode + "," + midambleConfig + ","
                        + midambleShift;
            }
        }
        if (system == 10 || system == 11) {
            Integer headersParams = 0;
            Integer numberOfChannel = generateInteger(1, 10);
            Integer paramsPerChannel = 6;
            str = str + "," + headersParams + "," + numberOfChannel + "," + paramsPerChannel;
            for (int i = 0; i < numberOfChannel; i++) {
                Integer phType = generateInteger(0, 3);
                Integer direction = generateInteger(1, 3);
                Integer pn = generateInteger(0, 511);
                Integer walshCode = generateInteger(0, 127);
                Integer phRate = generateInteger(0, 4);
                Integer qofMaskId = generateInteger(0, 3);
                str = str + "," + phType + "," + direction + "," + pn + "," + walshCode + "," + phRate + "," + qofMaskId;
            }
        }
        return str;
    }

    /**
     * Generate QPCHI row
     * 
     * @return row
     */
    private String generateQPCHI() {
        String str = NemoEvents.QPCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.CDMA_ONE_X.getId();
        str = str + "," + system;
        if (system == 11) {
            Integer headersParams = 3;
            Integer rate = generateInteger(4800, 9600);
            Integer slotNumber = generateInteger(0, 2047);
            Integer transferReason = generateInteger(0, 13);
            Integer numberOfConfigurations = generateInteger(1, 10);
            Integer paramsPerConfigurations = 9;
            str = str + "," + headersParams + "," + rate + "," + slotNumber + "," + transferReason + "," + numberOfConfigurations
                    + "," + paramsPerConfigurations;
            for (int i = 0; i < numberOfConfigurations; i++) {
                Integer pn = generateInteger(0, 511);
                Integer piWalsh = generateInteger(0, 128);
                Float piPowerOffset = generateFloat(0, 100);
                Integer biSupported = generateInteger(0, 1);
                Integer biWalsh = generateInteger(0, 128);
                Float biPwrLvl = generateFloat(0, 100);
                Integer cciSupported = generateInteger(0, 1);
                Integer cciWalsh = generateInteger(0, 128);
                Float cciPwrLvl = generateFloat(0, 100);
                str = str + "," + pn + "," + piWalsh + "," + piPowerOffset + "," + biSupported + "," + biWalsh + "," + biPwrLvl
                        + "," + cciSupported + "," + cciWalsh + "," + cciPwrLvl;
            }
            Integer numberOfIndicators = generateInteger(1, 10);
            Integer paramsPerIndicators = 9;
            str = str + "," + numberOfIndicators + "," + paramsPerIndicators;
            for (int j = 0; j < numberOfIndicators; j++) {
                Integer status = generateInteger(0, 4);
                Integer type = generateInteger(0, 5);
                Integer thb = generateInteger(0, 255);
                Integer thi = generateInteger(0, 255);
                Integer position = generateInteger(0, 768);
                Integer indIAmp = generateInteger(0, maxIntegerValue);
                Integer indQAmp = generateInteger(0, maxIntegerValue);
                Float comPilotEnergy = generateFloat(-35, 2);
                Float divPilotEnergy = generateFloat(-35, 2);
                str = str + "," + status + "," + type + "," + thb + "," + thi + "," + position + "," + indIAmp + "," + indQAmp
                        + "," + comPilotEnergy + "," + divPilotEnergy;
            }
        }
        return str;
    }

    /**
     * Generate FCHPACKETS row
     * 
     * @return row
     */
    private String generateFCHPACKETS() {
        String str = NemoEvents.FCHPACKETS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        if (system == 12) {
            Integer cC38400Good = generateInteger(0, maxIntegerValue);
            Integer cC38400Bad = generateInteger(0, maxIntegerValue);
            Integer cC76800Good = generateInteger(0, maxIntegerValue);
            Integer cC76800Bad = generateInteger(0, maxIntegerValue);
            Integer tC38400Good = generateInteger(0, maxIntegerValue);
            Integer tC38400Bad = generateInteger(0, maxIntegerValue);
            Integer tC76800Good = generateInteger(0, maxIntegerValue);
            Integer tC76800Bad = generateInteger(0, maxIntegerValue);
            Integer tC153600Good = generateInteger(0, maxIntegerValue);
            Integer tC153600Bad = generateInteger(0, maxIntegerValue);
            Integer t307200ShortGood = generateInteger(0, maxIntegerValue);
            Integer tC307200ShortBad = generateInteger(0, maxIntegerValue);
            Integer tC307200LongGood = generateInteger(0, maxIntegerValue);
            Integer tC307200LongBad = generateInteger(0, maxIntegerValue);
            Integer tC614400ShortGood = generateInteger(0, maxIntegerValue);
            Integer tC614400ShortBad = generateInteger(0, maxIntegerValue);
            Integer tC614400LongGood = generateInteger(0, maxIntegerValue);
            Integer tC614400LongBad = generateInteger(0, maxIntegerValue);
            Integer tC921600Good = generateInteger(0, maxIntegerValue);
            Integer tC921600Bad = generateInteger(0, maxIntegerValue);
            Integer tC1228800ShortGood = generateInteger(0, maxIntegerValue);
            Integer tC1228800ShortBad = generateInteger(0, maxIntegerValue);
            Integer tC1228800LongGood = generateInteger(0, maxIntegerValue);
            Integer tC1228800LongBad = generateInteger(0, maxIntegerValue);
            Integer tC1843200Good = generateInteger(0, maxIntegerValue);
            Integer tC1843200Bad = generateInteger(0, maxIntegerValue);
            Integer tC2457600Good = generateInteger(0, maxIntegerValue);
            Integer tC2457600Bad = generateInteger(0, maxIntegerValue);
            str = str + "," + cC38400Good + "," + cC38400Bad + "," + cC76800Good + "," + cC76800Bad + "," + tC38400Good + ","
                    + tC38400Bad + "," + tC76800Good + "," + tC76800Bad + "," + tC153600Good + "," + tC153600Bad + ","
                    + t307200ShortGood + "," + tC307200ShortBad + "," + tC307200LongGood + "," + tC307200LongBad + ","
                    + tC614400ShortGood + "," + tC614400ShortBad + "," + tC614400LongGood + "," + tC614400LongBad + ","
                    + tC921600Good + "," + tC921600Bad + "," + tC1228800ShortGood + "," + tC1228800ShortBad + ","
                    + tC1228800LongGood + "," + tC1228800LongBad + "," + tC1843200Good + "," + tC1843200Bad + "," + tC2457600Good
                    + "," + tC2457600Bad;
        }
        return str;
    }

    /**
     * Generate CONNECTIONC row
     * 
     * @return row
     */
    private String generateCONNECTIONC() {
        String str = NemoEvents.CONNECTIONC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        if (system == 12) {
            Integer transactionID = generateInteger(0, maxIntegerValue);
            Integer messageSeq = generateInteger(0, maxIntegerValue);
            Integer connectionResult = generateInteger(0, 13);
            Integer recStatus = generateInteger(0, 2);
            Integer duration = generateInteger(0, maxIntegerValue);
            Integer pn = generateInteger(0, 511);
            Integer sectorID = generateInteger(0, maxIntegerValue);
            Integer cc = generateInteger(0, maxIntegerValue);
            Integer pnChanges = generateInteger(0, maxIntegerValue);
            str = str + "," + transactionID + "," + messageSeq + "," + connectionResult + "," + recStatus + "," + duration + ","
                    + pn + "," + sectorID + "," + cc + "," + pnChanges;
        }
        return str;
    }

    /**
     * Generate CONNECTIOND row
     * 
     * @return row
     */
    private String generateCONNECTIOND() {
        String str = NemoEvents.CONNECTIOND.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        Integer reason = generateInteger(0, 9);
        str = str + "," + reason;
        return str;
    }

    /**
     * Generate SESSIONC row
     * 
     * @return row
     */
    private String generateSESSIONC() {
        String str = NemoEvents.SESSIONC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        Integer transactionID = generateInteger(1, maxIntegerValue);
        Integer result = generateInteger(0, 1);
        Integer rATI = generateInteger(1, maxIntegerValue);
        Integer duration = generateInteger(1, maxIntegerValue);
        Integer pn = generateInteger(0, 511);
        Integer cc = generateInteger(1, maxIntegerValue);
        String fullUATI = returnWordSoCalled("Session attempt full UATI");
        str = str + "," + transactionID + "," + result + "," + rATI + "," + duration + "," + pn + "," + cc + "," + fullUATI;
        return str;
    }

    /**
     * Generate RBI row
     * 
     * @return row
     */
    private String generateRBI() {
        String str = NemoEvents.RBI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.UMTS_TD_SCDMA.getId();
        }
        str = str + "," + system;
        Integer headerParams = 0;
        Integer paramsPerRB = 8;
        Integer rbs = generateInteger(0, 10);
        str = str + "," + headerParams + "," + paramsPerRB + "," + rbs;
        for (int i = 0; i < rbs; i++) {
            Integer rbID = generateInteger(0, 32);
            Integer rlcID = generateInteger(0, 20);
            Integer trChID = generateInteger(0, 32);
            Integer direction = generateInteger(0, 1);
            Integer logicalCh = generateInteger(0, 6);
            Integer rlcMode = generateInteger(0, 2);
            Integer radioBearerCiphering = generateInteger(0, 1);
            Integer trChType = generateInteger(0, 7);
            str = str + "," + rbID + "," + rlcID + "," + trChID + "," + direction + "," + logicalCh + "," + rlcMode + ","
                    + radioBearerCiphering + "," + trChType;
        }
        return str;
    }

    /**
     * generate TRCHI row
     * 
     * @return row
     */
    private String generateTRCHI() {
        String str = NemoEvents.TRCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(5, 6);
        str = str + "," + system.toString();
        Integer headersParams = 0;
        Integer paramsPerTrChs = 8;
        Integer trChs = generateInteger(0, 10);
        str = str + "," + headersParams + "," + paramsPerTrChs + "," + trChs;
        if (system == 5 || system == 6) {
            for (int i = 0; i < trChs; i++) {
                Integer trChID = generateInteger(0, 32);
                Integer cCTrChID = generateInteger(0, 5);
                Integer direction = generateInteger(0, 2);
                Integer trChType = generateInteger(0, 7);
                Integer trChCoding = generateInteger(0, 3);
                Integer crcLength = generateInteger(0, 24);
                Integer tti = generateInteger(0, 80);
                Integer rateMAttr = generateInteger(1, 256);
                str = str + "," + trChID + "," + cCTrChID + "," + direction + "," + trChType + "," + trChCoding + "," + crcLength
                        + "," + tti + "," + rateMAttr;
            }
        }
        return str;
    }

    /**
     * Generate RRA row
     * 
     * @return row
     */
    private String generateRRA() {
        String str = NemoEvents.RRA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateInteger(5, 6);
        str = str + "," + system;
        if (system == 5 || system == 6) {
            Integer rrcEstCause = generateInteger(0, 19);
            str = str + "," + rrcEstCause;
        }
        return str;
    }

    /**
     * Generate RRC row
     * 
     * @return row
     */
    private String generateRRC() {
        String str = NemoEvents.RRC.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateInteger(5, 6);
        str = str + "," + system;
        if (system == 5 || system == 6) {
            Integer rrcAtt = generateInteger(1, 10);
            str = str + "," + rrcAtt;
        }
        return str;
    }

    /**
     * Generate RRF row
     * 
     * @return row
     */
    private String generateRRF() {
        String str = NemoEvents.RRF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateInteger(5, 6);
        str = str + "," + system;
        if (system == 5 || system == 6) {
            Integer rrcAttAbort = 1;
            Integer rrcRejStatus = 1;
            Integer rrcRejCause = generateInteger(0, 1);
            str = str + "," + rrcAttAbort + "," + rrcRejStatus + "," + rrcRejCause;
        }
        return str;
    }

    /**
     * Generate RRD row
     * 
     * @return row
     */
    private String generateRRD() {
        String str = NemoEvents.RRD.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateInteger(5, 6);
        str = str + "," + system;
        if (system == 5 || system == 6) {
            Integer rrcRelStatus = generateInteger(1, 2);
            // 1000
            Integer rrcRelCause = generateInteger(0, 6);
            str = str + "," + rrcRelStatus + "," + rrcRelCause;
        }
        return str;
    }

    /**
     * Generate CIPI row
     * 
     * @return row
     */
    private String generateCIPI() {
        String str = NemoEvents.CIPI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.TETRA.getId();
        str = str + "," + system;
        if (system == 2) {
            Integer airEncryption = generateInteger(0, 2);
            String ksg = returnWordSoCalled("Ciphering KSG");
            String sck = returnWordSoCalled("Ciphering SCK");
            str = str + "," + airEncryption + "," + ksg + "," + sck;
        }
        return str;
    }

    /**
     * Generate L3SM row
     * 
     * @return row
     */
    private String generateL3SM() {
        String str = NemoEvents.L3SM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = systems.get(generateInteger(0, 5));
        Integer choice = generateInteger(1, 4);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.AMPS.getId();
        }
        if (choice == 4) {
            system = TechnologySystems.DAMPS.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Integer direction = generateInteger(1, 3);
            String l3Msg = returnWordSoCalled("Layer3 message");
            String subchannel = returnWordSoCalled("Layer3 subchannel");
            Integer channel = generateInteger(1, maxIntegerValue);
            Integer bsic = generateInteger(0, 63);
            Integer type = generateInteger(1, 5);
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + subchannel + "," + channel + "," + bsic + "," + type + "," + l3Data;
        }
        if (system == 2) {
            Integer direction = generateInteger(1, 3);
            String l3Msg = returnWordSoCalled("Layer3 message");
            String subchannel = returnWordSoCalled("Layer3 subchannel");
            String bsic = returnWordSoCalled("Layer3 BSIC");
            str = str + "," + direction + "," + l3Msg + "," + subchannel + "," + bsic;
        }
        if (system == 5 || system == 6) {
            Integer direction = generateInteger(1, 3);
            String l3Msg = returnWordSoCalled("Layer3 message");
            String subchannel = returnWordSoCalled("Layer3 subchannel");
            Integer channel = generateInteger(1, maxIntegerValue);
            Integer sc = generateInteger(0, 511);
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + subchannel + "," + channel + "," + sc + "," + l3Data;
        }
        if (system == 10 || system == 11) {
            Integer direction = generateInteger(1, 3);
            String l3Msg = returnWordSoCalled("Layer3 message");
            String channelType = returnWordSoCalled("Layer3 channel type");
            Integer pREV = generateInteger(1, 11);
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + channelType + "," + pREV + "," + l3Data;
        }
        if (system == 21) {
            Integer direction = generateInteger(1, 3);
            String l3Msg = returnWordSoCalled("Layer3 message");
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + l3Data;
        }
        if (system == 51 || system == 53) {
            Integer direction = generateInteger(1, 3);
            String l3Msg = returnWordSoCalled("Layer3 message");
            String channelType = returnWordSoCalled("Layer3 channel type");
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + channelType + "," + l3Data;
        }
        return str;
    }

    /**
     * Generate L2SM row
     * 
     * @return row
     */
    private String generateL2SM() {
        String str = NemoEvents.L2SM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        if (system == 1) {
            Integer direction = generateInteger(1, 3);
            String l2Msg = returnWordSoCalled("Layer2 message");
            String subchannel = returnWordSoCalled("Layer2 subchannel");
            Integer arfcn = generateInteger(1, maxIntegerValue);
            Integer bsic = generateInteger(0, 63);
            Integer type = generateInteger(1, 5);
            String l2Data = returnWordSoCalled("Layer2 data");
            str = str + "," + direction + "," + l2Msg + "," + subchannel + "," + arfcn + "," + bsic + "," + type + "," + l2Data;
        }
        return str;
    }

    /**
     * Generate RRCSM row
     * 
     * @return row
     */
    private String generateRRCSM() {
        String str = NemoEvents.RRCSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(5, 6);
        str = str + "," + system;
        if (system == 5 || system == 6) {
            Integer direction = generateInteger(1, 3);
            String rpcMsg = returnWordSoCalled("RRC message");
            String subchannel = returnWordSoCalled("RRC subchannel");
            Integer uarfcn = generateInteger(1, maxIntegerValue);
            Integer sc = generateInteger(0, 511);
            String rrcData = returnWordSoCalled("RRC data");
            str = str + "," + direction + "," + rpcMsg + "," + subchannel + "," + uarfcn + "," + sc + "," + rrcData;
        }
        return str;
    }

    /**
     * Generate RLCSM row
     * 
     * @return row
     */
    private String generateRLCSM() {
        String str = NemoEvents.RLCSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system;
        if (system == 5) {
            Integer direction = generateInteger(1, 3);
            String rlcMsg = returnWordSoCalled("RLC message");
            String subchannel = returnWordSoCalled("RLC subchannel");
            Integer rb = generateInteger(0, 32);
            Integer rlcMode = generateInteger(0, 2);
            Integer lengthIndicator = generateInteger(0, 15);
            String rlcData = returnWordSoCalled("RLC data");
            str = str + "," + direction + "," + rlcMsg + "," + subchannel + "," + rb + "," + rlcMode + "," + lengthIndicator + ","
                    + rlcData;
        }
        return str;
    }

    /**
     * Generate MACSM row
     * 
     * @return row
     */
    private String generateMACSM() {
        String str = NemoEvents.MACSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.WIMAX.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Integer direction = generateInteger(1, 3);
            String rlcMacMsg = returnWordSoCalled("RLC/MAC message");
            String subchannel = returnWordSoCalled("RLC/MAC subchannel");
            Integer type = generateInteger(1, 5);
            String rlcMacData = returnWordSoCalled("RLC/MAC data");
            str = str + "," + direction + "," + rlcMacMsg + "," + subchannel + "," + type + "," + rlcMacData;
        }
        if (system == 25) {
            Integer direction = generateInteger(1, 3);
            String macMsg = returnWordSoCalled("MAC message");
            Integer frameNumber = generateInteger(1, maxIntegerValue);
            String macData = returnWordSoCalled("MAC data");
            Integer macVer = generateInteger(5, 6);
            str = str + "," + direction + "," + macMsg + "," + frameNumber + "," + macData + "," + macVer;
        }
        return str;
    }

    /**
     * Generate LLCSM row
     * 
     * @return row
     */
    private String generateLLCSM() {
        String str = NemoEvents.LLCSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.TETRA.getId();
        }
        str = str + "," + system;
        if (system == 1 || system == 2) {
            Integer direction = generateInteger(1, 3);
            String llcMsg = returnWordSoCalled("LLC message");
            String llcData = returnWordSoCalled("LLC data");
            str = str + "," + direction + "," + llcMsg + "," + llcData;
        }
        return str;
    }

    /**
     * Generate SNPSM row
     * 
     * @return row
     */
    private String generateSNPSM() {
        String str = NemoEvents.SNPSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system;
        if (system == 12) {
            Integer direction = generateInteger(1, 3);
            String snpMsgName = returnWordSoCalled("SNP message name");
            String snpChType = returnWordSoCalled("SNP channel type");
            String snpLayer = returnWordSoCalled("SNP layer");
            Integer protocolSubtype = generateInteger(1, maxIntegerValue);
            String snpData = returnWordSoCalled("SNP data");
            str = str + "," + direction + "," + snpMsgName + "," + snpChType + "," + snpLayer + "," + protocolSubtype + ","
                    + snpData;
        }
        return str;
    }

    /**
     * Generate RRLPSM row
     * 
     * @return row
     */
    private String generateRRLPSM() {
        String str = NemoEvents.RRLPSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system;
        if (system == 1) {
            Integer direction = generateInteger(1, 3);
            String rrlpMsg = returnWordSoCalled("RRLP message");
            String subchannel = returnWordSoCalled("RRLP subchannel");
            String rrlpData = returnWordSoCalled("RRLP data");
            str = str + "," + direction + "," + rrlpMsg + "," + subchannel + "," + rrlpData;
        }
        return str;
    }

    /**
     * Generate GANSM row
     * 
     * @return row
     */
    private String generateGANSM() {
        String str = NemoEvents.GANSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GAN_WLAN.getId();
        str = str + "," + system;
        if (system == 1) {
            Integer direction = generateInteger(1, 3);
            String ganMsg = returnWordSoCalled("GAN message");
            String subchannel = returnWordSoCalled("GAN subchannel");
            String ganMsgData = returnWordSoCalled("GAN message data");
            str = str + "," + direction + "," + ganMsg + "," + subchannel + "," + ganMsgData;
        }
        return str;
    }

    /**
     * Generate SIPSM row
     * 
     * @return row
     */
    private String generateSIPSM() {
        String str = NemoEvents.SIPSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        str = str + "," + system;
        if (system == 1 || system == 5) {
            Integer direction = generateInteger(1, 3);
            String sipMsgName = returnWordSoCalled("SIP message name");
            String sipMsg = returnWordSoCalled("SIP message");
            str = str + "," + direction + "," + sipMsgName + "," + sipMsg;
        }
        return str;
    }

    /**
     * Generate RTPSM row
     * 
     * @return row
     */
    private String generateRTPSM() {
        String str = NemoEvents.RTPSM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        if (system == 1 || system == 5 || system == 21) {
            Integer direction = generateInteger(1, 3);
            String rtpMsgName = returnWordSoCalled("RTP message name");
            Integer rtpMsgNr = generateInteger(0, 65535);
            String rtpMsg = returnWordSoCalled("RTP message");
            str = str + "," + direction + "," + rtpMsgName + "," + rtpMsgNr + "," + rtpMsg;
        }
        return str;
    }

    /**
     * Generate PAA row
     * 
     * @return row
     */
    private String generatePAA() {
        String str = NemoEvents.PAA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 4);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = generateInteger(11, 12);
        }
        if (choice == 4) {
            system = systems.get(generateInteger(8, 9));
        }
        str = str + "," + system;
        if (system == 1 || system == 5 || system == 6) {
            Integer initiator = generateInteger(1, 2);
            Integer protocolType = 1;
            String apn = returnWordSoCalled("Access point name");
            String statticIP = returnWordSoCalled("Requested packet protocol address");
            Integer headerCompr = generateInteger(0, 4);
            Integer compression = generateInteger(0, 3);
            str = str + "," + initiator + "," + protocolType + "," + apn + "," + statticIP + "," + headerCompr + "," + compression;
        }
        if (system == 11 || system == 12) {
            Integer initiator = generateInteger(1, 2);
            Integer protocolType = 1;
            str = str + "," + initiator + "," + protocolType;
        }
        return str;
    }

    /**
     * Generate PAF row
     * 
     * @return row
     */
    private String generatePAF() {
        String str = NemoEvents.PAF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 4);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = generateInteger(11, 12);
        }
        if (choice == 4) {
            system = systems.get(generateInteger(8, 9));
        }
        str = str + "," + system;
        Integer failStatus = generateInteger(1, 6);
        // 8,81,95,...
        Integer deactCause = generateInteger(24, 47);
        str = str + "," + failStatus + "," + deactCause;
        return str;
    }

    /**
     * Generate PAC row
     * 
     * @return row
     */
    private String generatePAC() {
        String str = NemoEvents.PAC.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateTechnologySystems();
        Integer packetActState = generateInteger(1, 2);
        String ip = returnWordSoCalled("Packet protocol address");
        str = str + "," + system + "," + packetActState + "," + ip;
        return str;
    }

    /**
     * Generate PAD row
     * 
     * @return row
     */
    private String generatePAD() {
        String str = NemoEvents.PAD.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 4);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = generateInteger(11, 12);
        }
        if (choice == 4) {
            system = systems.get(generateInteger(8, 9));
        }
        str = str + "," + system;
        Integer deactStatus = generateInteger(1, 6);
        // 8,81,95,...
        Integer deactCause = generateInteger(24, 47);
        Integer deactTime = generateInteger(1, maxIntegerValue);
        str = str + "," + deactStatus + "," + deactCause + "," + deactTime;
        return str;
    }

    /**
     * Generate QSPR row
     * 
     * @return row
     */
    private String generateQSPR() {
        String str = NemoEvents.QSPR.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        // 31
        Integer avgTPutClass = generateInteger(0, 18);
        Integer peakTputClass = generateInteger(0, 9);
        Integer delayClass = generateInteger(0, 4);
        Integer priorityClass = generateInteger(0, 3);
        Integer reliabClass = generateInteger(0, 5);
        str = str + "," + avgTPutClass + "," + peakTputClass + "," + delayClass + "," + priorityClass + "," + reliabClass;
        // 31
        Integer minAvgTPut = generateInteger(0, 18);
        Integer minPeakTPut = generateInteger(0, 9);
        Integer minDelay = generateInteger(0, 4);
        Integer minPriorityClass = generateInteger(0, 3);
        Integer minReliability = generateInteger(0, 5);
        Integer reqTrafficClass = generateInteger(0, 4);
        Integer reqMaxULTPut = generateInteger(0, 16000);
        Integer reqMaxDLTPut = generateInteger(0, 16000);
        Integer reqDelivOrder = generateInteger(0, 2);
        Integer reqMaxSDUSize = generateInteger(0, 1500);
        String reqSDUErrRatio = returnWordSoCalled("Requested SDU error ratio");
        String reqResidBER = returnWordSoCalled("Requested residual bit error ratio");
        Integer reqDevilErrSDU = generateInteger(0, 3);
        Integer reqTransferDelay = generateInteger(0, 4100);
        Integer reqTHP = generateInteger(0, 3);
        Integer minTrafficClass = generateInteger(0, 4);
        Integer minMaxULTPut = generateInteger(0, 16000);
        Integer minMaxDLTPut = generateInteger(0, 16000);
        Integer minGrULTPut = generateInteger(0, 16000);
        Integer minGrDLTPut = generateInteger(0, 16000);
        Integer minDevilOrder = generateInteger(0, 2);
        Integer minMaxSDUSize = generateInteger(0, 1500);
        String minSDUErr = returnWordSoCalled("Minimum accepted SDU error ratio");
        String minResidBER = returnWordSoCalled("Minimum accepted residual bit error ratio");
        Integer minDelErrSDU = generateInteger(0, 3);
        Integer minTranferDelay = generateInteger(0, 4100);
        Integer minTHR = generateInteger(0, 3);
        str = str + "," + minAvgTPut + "," + minPeakTPut + "," + minDelay + "," + minPriorityClass + "," + minReliability + ","
                + reqTrafficClass + "," + reqMaxULTPut + "," + reqMaxDLTPut + "," + reqDelivOrder + "," + reqMaxSDUSize + ","
                + reqSDUErrRatio + "," + reqResidBER + "," + reqDevilErrSDU + "," + reqTransferDelay + "," + reqTHP + ","
                + minTrafficClass + "," + minMaxULTPut + "," + minMaxDLTPut + "," + minGrULTPut + "," + minGrDLTPut + ","
                + minDevilOrder + "," + minMaxSDUSize + "," + minSDUErr + "," + minResidBER + "," + minDelErrSDU + ","
                + minTranferDelay + "," + minTHR;
        return str;
    }

    /**
     * Generate QSPN row
     * 
     * @return row
     */
    private String generateQSPN() {
        String str = NemoEvents.QSPN.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        // 31
        Integer avgTPutClass = generateInteger(0, 18);
        Integer peakTputClass = generateInteger(1, 9);
        Integer delayClass = generateInteger(1, 4);
        Integer priorityClass = generateInteger(1, 3);
        Integer reliabClass = generateInteger(1, 5);
        str = str + "," + avgTPutClass + "," + peakTputClass + "," + delayClass + "," + priorityClass + "," + reliabClass;
        // 31
        Integer trafficClass = generateInteger(0, 3);
        Integer maxULTPut = generateInteger(0, 16000);
        Integer maxDLTPut = generateInteger(0, 16000);
        Integer grULTPut = generateInteger(0, 16000);
        Integer grDLTPut = generateInteger(0, 16000);
        Integer devilOrder = generateInteger(0, 1);
        Integer maxSDUSize = generateInteger(0, 1500);
        String sduErrRatio = returnWordSoCalled("Negotiated SDU error ratio");
        String minResidBER = returnWordSoCalled("Negotiated residual bit error ratio");
        Integer tranfDelay = generateInteger(0, 4100);
        Integer thp = generateInteger(0, 3);
        str = str + "," + trafficClass + "," + maxULTPut + "," + maxDLTPut + "," + grULTPut + "," + grDLTPut + "," + devilOrder
                + "," + maxSDUSize + "," + sduErrRatio + "," + minResidBER + "," + tranfDelay + "," + thp;
        return str;
    }

    // PCHI

    /**
     * Generate GAA row
     * 
     * @return row
     */
    private String generateGAA() {
        String str = NemoEvents.GAA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        return str;
    }

    /**
     * Generate GAF row
     * 
     * @return row
     */
    private String generateGAF() {
        String str = NemoEvents.GAF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        Integer attachFail = generateInteger(1, 6);
        // ...
        Integer attFailCause = generateInteger(7, 17);
        str = str + "," + system + "," + attachFail + "," + attFailCause;
        return str;
    }

    /**
     * Generate GAC row
     * 
     * @return row
     */
    private String generateGAC() {
        String str = NemoEvents.GAC.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        return str;
    }

    /**
     * Generate GAD row
     * 
     * @return row
     */
    private String generateGAD() {
        String str = NemoEvents.GAD.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        Integer detachStatus = generateInteger(1, 3);
        // 2,3,...
        Integer detachCause = generateInteger(6, 17);
        Integer detachTime = generateInteger(1, maxIntegerValue);
        str = str + "," + detachStatus + "," + detachCause + "," + detachTime;
        return str;
    }

    /**
     * Generate RLCBLER row
     * 
     * @return row
     */
    private String generateRLCBLER() {
        String str = NemoEvents.RLCBLER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 3);
        if (choice == 1) {
            system = TechnologySystems.GSM.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.UMTS_TD_SCDMA.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Float bler = generateFloat(0, 99);
            Integer rlcBlocks = generateInteger(1, maxIntegerValue);
            Integer rlcErrors = generateInteger(1, maxIntegerValue);
            str = str + "," + bler + "," + rlcBlocks + "," + rlcErrors;
        }
        if (system == 5 || system == 6) {
            Float bler = generateFloat(0, 99);
            Integer trchBlocks = generateInteger(1, maxIntegerValue);
            Integer trchErrors = generateInteger(1, maxIntegerValue);
            Integer chs = generateInteger(0, 10);
            Integer paramsPerRB = 4;
            str = str + "," + bler + "," + trchBlocks + "," + trchErrors + "," + chs + "," + paramsPerRB;
            for (int i = 0; i < chs; i++) {
                Integer trchID = generateInteger(0, 32);
                Float trchBLER = generateFloat(0, 99);
                Integer trchBlocks2 = generateInteger(1, maxIntegerValue);
                Integer trchErrors2 = generateInteger(1, maxIntegerValue);
                str = str + "," + trchID + "," + trchBLER + "," + trchBlocks2 + "," + trchErrors2;
            }
        }
        return str;
    }

    /**
     * Generate RLCRATE row
     * 
     * @return row
     */
    private String generateRLCRATE() {
        String str = NemoEvents.RLCRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 3);
        if (choice == 1) {
            system = TechnologySystems.GSM.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.UMTS_TD_SCDMA.getId();
        }
        str = str + "," + system;
        if (system == 1) {
            Integer rlcRateUL = generateInteger(1, maxIntegerValue);
            Integer rlcRateDL = generateInteger(1, maxIntegerValue);
            Float rlcRetransUL = generateFloat(0, 99);
            str = str + "," + rlcRateUL + "," + rlcRateDL + "," + rlcRetransUL;
        }
        if (system == 5 || system == 6) {
            Integer rlcRateUL = generateInteger(1, maxIntegerValue);
            Integer rlcRateDL = generateInteger(1, maxIntegerValue);
            Float rlcRetransUL = generateFloat(0, 99);
            str = str + "," + rlcRateUL + "," + rlcRateDL + "," + rlcRetransUL;
            Integer rbs = generateInteger(0, 10);
            Integer paramsPerRB = 4;
            str = str + "," + rbs + "," + paramsPerRB;
            for (int i = 0; i < rbs; i++) {
                Integer rbID = generateInteger(0, 32);
                Integer rlcRateUL2 = generateInteger(1, maxIntegerValue);
                Integer rlcRateDL2 = generateInteger(1, maxIntegerValue);
                Float rlcRetransUL2 = generateFloat(0, 99);
                str = str + "," + rbID + "," + rlcRateUL2 + "," + rlcRateDL2 + "," + rlcRetransUL2;
            }
        }
        return str;
    }

    /**
     * Generate LLCRATE row
     * 
     * @return row
     */
    private String generateLLCRATE() {
        String str = NemoEvents.LLCRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        Integer llcRateUL = generateInteger(1, maxIntegerValue);
        Integer llcRateDL = generateInteger(1, maxIntegerValue);
        Float llcRetransUL = generateFloat(0, 99);
        str = str + "," + llcRateUL + "," + llcRateDL + "," + llcRetransUL;
        return str;
    }

    /**
     * Generate RUA row
     * 
     * @return row
     */
    private String generateRUA() {
        String str = NemoEvents.RUA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        Integer ruaType = generateInteger(1, 4);
        str = str + "," + ruaType;
        return str;
    }

    /**
     * Generate RUS row
     * 
     * @return row
     */
    private String generateRUS() {
        String str = NemoEvents.RUS.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system.toString();
        Integer oldRAC = generateInteger(0, 255);
        Integer oldLAC = generateInteger(0, 65535);
        Integer rac = generateInteger(0, 255);
        Integer lac = generateInteger(0, 65535);
        str = str + "," + oldRAC + "," + oldLAC + "," + rac + "," + lac;
        return str;
    }

    /**
     * Generate RUF row
     * 
     * @return row
     */
    private String generateRUF() {
        String str = NemoEvents.RUF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        if (choice == 3) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system.toString();
        Integer attRAC = generateInteger(0, 255);
        Integer attLAC = generateInteger(0, 65535);
        // 2,3,...
        Integer rauFailCause = generateInteger(6, 17);
        str = str + "," + attRAC + "," + attLAC + "," + rauFailCause;
        return str;
    }

    /**
     * Generate TBFI row
     * 
     * @return row
     */
    private String generateTBFI() {
        String str = NemoEvents.TBFI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer headerParams = 1;
        String tlli = returnWordSoCalled("Temporary logical link identity");
        Integer paramsPerTBF = 2;
        Integer ulTBFs = generateInteger(0, 10);
        str = str + "," + system + "," + headerParams + "," + tlli + "," + paramsPerTBF + "," + ulTBFs;
        for (int i = 0; i < ulTBFs; i++) {
            Integer tfi = generateInteger(0, 31);
            Integer rlcWin = generateInteger(64, 1024);
            str = str + "," + tfi + "," + rlcWin;
        }
        Integer dlTBFs = generateInteger(0, 10);
        for (int j = 0; j < dlTBFs; j++) {
            Integer tfi = generateInteger(0, 31);
            Integer rlcWin = generateInteger(64, 1024);
            str = str + "," + tfi + "," + rlcWin;
        }
        return str;
    }

    /**
     * Generate TBFULE row
     * 
     * @return row
     */
    private String generateTBFULE() {
        String str = NemoEvents.TBFULE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        // 1,2,...
        Integer ulTBFEstCause = generateInteger(11, 17);
        Integer ulTBFEstType = generateInteger(1, 5);
        Integer ulTBFEstStatus = generateInteger(1, 4);
        Integer ulTBFEstReq = generateInteger(1, maxIntegerValue);
        str = str + "," + system + "," + ulTBFEstCause + "," + ulTBFEstType + "," + ulTBFEstStatus + "," + ulTBFEstReq;
        return str;
    }

    /**
     * Generate MACRATE row
     * 
     * @return row
     */
    private String generateMACRATE() {
        String str = NemoEvents.MACRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 2);
        if (choice == 1) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.WIMAX.getId();
        }
        str = str + "," + system;
        if (system == 5) {
            Integer headerParams = 0;
            Integer trch = generateInteger(0, 10);
            Integer paramsPerTRCH = 7;
            str = str + "," + headerParams + "," + trch + "," + paramsPerTRCH;
            for (int i = 0; i < trch; i++) {
                Integer trchID = generateInteger(0, 32);
                Integer trchType = generateInteger(0, 7);
                Integer machsBitrate = generateInteger(1, maxIntegerValue);
                Integer machsBlockrate = generateInteger(1, maxIntegerValue);
                Float machs1stRetr = generateFloat(0, 99);
                Float machs2ndRetr = generateFloat(0, 99);
                Float machs3rdRetr = generateFloat(0, 99);
                str = str + "," + trchID + "," + trchType + "," + machsBitrate + "," + machsBlockrate + "," + machs1stRetr + ","
                        + machs2ndRetr + "," + machs3rdRetr;
            }
        }
        if (system == 25) {
            Integer macHeaderParams = 4;
            Integer macRateUL = generateInteger(1, maxIntegerValue);
            Integer macRateDL = generateInteger(1, maxIntegerValue);
            Integer macPacketRateUL = generateInteger(1, maxIntegerValue);
            Integer macPacketRateDL = generateInteger(1, maxIntegerValue);
            str = str + "," + macHeaderParams + "," + macRateUL + "," + macRateDL + "," + macPacketRateUL + "," + macPacketRateDL;
        }
        return str;
    }

    /**
     * Generate MACBLER row
     * 
     * @return row
     */
    private String generateMACBLER() {
        String str = NemoEvents.MACBLER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 2);
        if (choice == 1) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.WIMAX.getId();
        }
        str = str + "," + system;
        if (system == 5) {
            Integer headerParams = 0;
            Integer trch = generateInteger(0, 10);
            Integer paramsPerTRCH = 4;
            str = str + "," + headerParams + "," + trch + "," + paramsPerTRCH;
            for (int i = 0; i < trch; i++) {
                Integer trchID = generateInteger(0, 32);
                Integer trchType = generateInteger(0, 7);
                Integer ackNACK = generateInteger(1, maxIntegerValue);
                Float machsBLERDL = generateFloat(0, 99);
                str = str + "," + trchID + "," + trchType + "," + ackNACK + "," + machsBLERDL;
            }
        }
        if (system == 25) {
            Float per = generateFloat(0, 99);
            str = str + "," + per;
        }
        return str;
    }

    /**
     * Generate AMRI row
     * 
     * @return row
     */
    private String generateAMRI() {
        String str = NemoEvents.AMRI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 2);
        if (choice == 1) {
            system = TechnologySystems.GSM.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        if (system == 1) {
            // 100,101,...
            Integer amrInitMode = generateInteger(0, 7);
            Integer amrICMI = generateInteger(1, maxIntegerValue);
            Float amrTH1 = generateFloat(0, 31);
            Float amrHYS1 = generateFloat(0, 7);
            Float amrTH2 = generateFloat(0, 31);
            Float amrHYS2 = generateFloat(0, 7);
            Float amrTH3 = generateFloat(0, 31);
            Float amrHYS3 = generateFloat(0, 7);
            Integer activeCodecs = generateInteger(1, 10);
            str = str + "," + amrInitMode + "," + amrICMI + "," + amrTH1 + "," + amrHYS1 + "," + amrTH2 + "," + amrHYS2 + ","
                    + amrTH3 + "," + amrHYS3 + "," + activeCodecs;
            for (int i = 0; i < activeCodecs; i++) {
                // 100,101,...
                Integer armCodecs = generateInteger(0, 7);
                str = str + "," + armCodecs;
            }
        }
        if (system == 21) {
            // 100,101,...
            Integer amrInitMode = generateInteger(0, 7);
            Integer amrICMI = generateInteger(1, maxIntegerValue);
            Float amrTH1 = generateFloat(0, 49);
            Float amrHYS1 = generateFloat(0, 17);
            Float amrTH2 = generateFloat(0, 49);
            Float amrHYS2 = generateFloat(0, 17);
            Float amrTH3 = generateFloat(0, 49);
            Float amrHYS3 = generateFloat(0, 17);
            Integer activeCodecs = generateInteger(1, 10);
            str = str + "," + amrInitMode + "," + amrICMI + "," + amrTH1 + "," + amrHYS1 + "," + amrTH2 + "," + amrHYS2 + ","
                    + amrTH3 + "," + amrHYS3 + "," + activeCodecs;
            for (int i = 0; i < activeCodecs; i++) {
                // 100,101,...
                Integer armCodecs = generateInteger(0, 7);
                str = str + "," + armCodecs;
            }
        }
        return str;
    }

    /**
     * Generate AMRQ row
     * 
     * @return row
     */
    private String generateAMRQ() {
        String str = NemoEvents.AMRQ.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Float amrQualEst = generateFloat(0, 39);
        str = str + "," + system + "," + amrQualEst;
        return str;
    }

    /**
     * Generate AQUL row
     * 
     * @return row
     */
    private String generateAQUL() {
        String str = NemoEvents.AQUL.getEventId() + "," + generateTimestamp() + ",";
        Integer aqTypeUL = generateInteger(1, 5);
        str = str + "," + aqTypeUL;
        Float aqMOS = generateFloat(0, 4);
        String aqSampleFile = returnWordSoCalled("Audio quality sample filename UL");
        String aqRefFile = returnWordSoCalled("Audio quality reference sample filename UL");
        String aqTimestamp = returnWordSoCalled("Audio quality timestamp UL");
        Integer aqSampleDuration = generateInteger(1, maxIntegerValue);
        Float aqActivity = generateFloat(0, 99);
        Float aqDelay = generateFloat(0, 100);
        Float aqMinDelay = generateFloat(0, 100);
        Float aqMaxDelay = generateFloat(0, 100);
        Float aqStdevDelay = generateFloat(0, 100);
        Float aqSNR = generateFloat(0, 100);
        Float aqInsertionGain = generateFloat(0, 100);
        Float aqNoiseGain = generateFloat(0, 100);
        str = str + "," + aqMOS + "," + aqSampleFile + "," + aqRefFile + "," + aqTimestamp + "," + aqSampleDuration + ","
                + aqActivity + "," + aqDelay + "," + aqMinDelay + "," + aqMaxDelay + "," + aqStdevDelay + "," + aqSNR + ","
                + aqInsertionGain + "," + aqNoiseGain;
        return str;
    }

    /**
     * Generate AQDL row
     * 
     * @return row
     */
    private String generateAQDL() {
        String str = NemoEvents.AQDL.getEventId() + "," + generateTimestamp() + ",";
        Integer aqTypeDL = generateInteger(1, 5);
        str = str + "," + aqTypeDL;
        if (aqTypeDL == 1 || aqTypeDL == 2 || aqTypeDL == 3) {
            Float aqMOS = generateFloat(0, 4);
            String aqSampleFile = returnWordSoCalled("Audio quality sample filename DL");
            String aqRefFile = returnWordSoCalled("Audio quality reference sample filename DL");
            String aqTimestamp = returnWordSoCalled("Audio quality timestamp DL");
            Integer aqSampleDuration = generateInteger(1, maxIntegerValue);
            Float aqActivity = generateFloat(0, 99);
            Float aqDelay = generateFloat(0, 100);
            Float aqMinDelay = generateFloat(0, 100);
            Float aqMaxDelay = generateFloat(0, 100);
            Float aqStdevDelay = generateFloat(0, 100);
            Float aqSNR = generateFloat(0, 100);
            Float aqInsertionGain = generateFloat(0, 100);
            Float aqNoiseGain = generateFloat(0, 100);
            str = str + "," + aqMOS + "," + aqSampleFile + "," + aqRefFile + "," + aqTimestamp + "," + aqSampleDuration + ","
                    + aqActivity + "," + aqDelay + "," + aqMinDelay + "," + aqMaxDelay + "," + aqStdevDelay + "," + aqSNR + ","
                    + aqInsertionGain + "," + aqNoiseGain;
        }
        if (aqTypeDL == 4) {
            Float aqMOSStreaming = generateFloat(1, 4);
            str = str + "," + aqMOSStreaming;
        }
        if (aqTypeDL == 5) {
            Float aqMOSDL = generateFloat(0, 4);
            str = str + "," + aqMOSDL;
        }
        return str;
    }

    /**
     * generate AMRS row
     * 
     * @return row
     */
    private String generateAMRS() {
        String str = NemoEvents.AMRS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = null;
        Integer choice = generateInteger(1, 4);
        if (choice == 1) {
            system = TechnologySystems.GSM.getId();
        }
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        if (choice == 3) {
            system = TechnologySystems.UMTS_TD_SCDMA.getId();
        }
        if (choice == 4) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system;
        if (system == 1 || system == 21) {
            // 100,101,...
            Integer amrModeUL = generateInteger(0, 7);
            // 100,101,...
            Integer amrModeDL = generateInteger(0, 7);
            // 100,101,...
            Integer amrModeCmd = generateInteger(0, 7);
            // 100,101,...
            Integer amrModeReq = generateInteger(0, 7);
            str = str + "," + amrModeUL + "," + amrModeDL + "," + amrModeCmd + "," + amrModeReq;
        }
        if (system == 5 || system == 6) {
            // 100,101,...
            Integer amrModeUL = generateInteger(0, 7);
            // 100,101,...
            Integer amrModeDL = generateInteger(0, 7);
            str = str + "," + amrModeUL + "," + amrModeDL;
        }
        return str;
    }

    /**
     * Generate AQI row
     * 
     * @return row
     */
    private String generateAQI() {
        String str = NemoEvents.AQI.getEventId() + "," + generateTimestamp() + ",";
        Integer aqTypeDL = generateInteger(1, 5);
        Integer aqType = generateInteger(1, 5);
        Integer aqActivity = generateInteger(1, 2);
        Integer aqSynch = generateInteger(0, 1);
        str = str + "," + aqTypeDL + "," + aqType + "," + aqActivity + "," + aqSynch;
        return str;
    }

    /**
     * Generate VQDL row
     * 
     * @return row
     */
    private String generateVQDL() {
        String str = NemoEvents.VQDL.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer vqType = generateInteger(1, 3);
        str = str + "," + vqType;
        if (vqType == 1) {
            Float vqMOS = generateFloat(0, 4);
            Float vqBlockiness = generateFloat(0, 99);
            Float vqBlurriness = generateFloat(0, 99);
            Float vqJerkiness = generateFloat(0, 99);
            str = str + "," + vqMOS + "," + vqBlockiness + "," + vqBlurriness + "," + vqJerkiness;
        }
        if (vqType == 2) {
            Float vqMOS = generateFloat(0, 4);
            Integer vqJitter = generateInteger(1, maxIntegerValue);
            Float vqPER = generateFloat(0, 99);
            str = str + "," + vqMOS + "," + vqJitter + "," + vqPER;
        }
        if (vqType == 3) {
            Float vqMOS = generateFloat(0, 4);
            Integer vqJitter = generateInteger(1, maxIntegerValue);
            Float vqPER = generateFloat(0, 99);
            Float mosDegradation = generateFloat(0, 4);
            Float degDuePER = generateFloat(0, 99);
            Float degDueCompress = generateFloat(0, 99);
            Float videoFrameRate = generateFloat(0, 49);
            String videoProtocol = returnWordSoCalled("Video protocol");
            String videoCodec = returnWordSoCalled("Video codec");
            str = str + "," + vqMOS + "," + vqJitter + "," + vqPER + "," + mosDegradation + "," + degDuePER + "," + degDueCompress
                    + "," + videoFrameRate + "," + videoProtocol + "," + videoCodec;
        }
        return str;
    }

    /**
     * Generate VRATE row
     * 
     * @return row
     */
    private String generateVRATE() {
        String str = NemoEvents.VRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            Integer videoProtocol = generateInteger(1, maxIntegerValue);
            Integer videoRateUL = generateInteger(1, maxIntegerValue);
            Integer videoRateDL = generateInteger(1, maxIntegerValue);
            Integer videoFrameRateUL = generateInteger(1, maxIntegerValue);
            Integer videoFrameRateDL = generateInteger(1, maxIntegerValue);
            Float videoFer = generateFloat(0, 99);
            Float vqi = generateFloat(1, 4);
            str = str + "," + videoProtocol + "," + videoRateUL + "," + videoRateDL + "," + videoFrameRateUL + ","
                    + videoFrameRateDL + "," + videoFer + "," + vqi;
        }
        return str;
    }

    /**
     * Generate MSGA row
     * 
     * @return row
     */
    private String generateMSGA() {
        String str = NemoEvents.MSGA.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        str = str + "," + system.toString();
        Integer messageType = generateInteger(1, 2);
        str = str + "," + messageType;
        if (messageType == 1) {
            str = str + "," + generateContext(1);
            Integer smsMsgType = generateInteger(1, 5);
            String smsNumber = returnWordSoCalled("SMS phone number");
            String smsc = returnWordSoCalled("SMS service center address");
            Integer smsCodingSch = generateInteger(0, 255);
            String smsMsgData = returnWordSoCalled("SMS message data");
            str = str + "," + smsMsgType + "," + smsNumber + "," + smsc + "," + smsCodingSch + "," + smsMsgData;
        }
        if (messageType == 2) {
            str = str + "," + generateContext(1);
            Integer mmsMsgType = generateInteger(1, 4);
            String mmsSerCenter = returnWordSoCalled("MMS service center");
            Integer mmsTrProtocol = generateInteger(1, 3);
            Integer mmsFiles = generateInteger(1, 10);
            str = str + "," + mmsMsgType + "," + mmsSerCenter + "," + mmsTrProtocol + "," + mmsFiles;
            for (int i = 0; i < mmsFiles; i++) {
                String mmsFilename = returnWordSoCalled("MMS filename");
                str = str + "," + mmsFilename;
            }
        }
        return str;
    }

    /**
     * Generate MSGS row
     * 
     * @return row
     */
    private String generateMSGS() {
        String str = NemoEvents.MSGS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        str = str + "," + system.toString();
        Integer messageType = generateInteger(1, 2);
        str = str + "," + messageType;
        if (messageType == 1) {
            str = str + "," + generateContext(1);
            Integer refNumber = generateInteger(1, maxIntegerValue);
            Integer smsMsgType = generateInteger(1, 5);
            str = str + "," + refNumber + "," + smsMsgType;
        }
        if (messageType == 2) {
            str = str + "," + generateContext(1);
            // ...
            String mmsMsgID = returnWordSoCalled("MMS message ID");
            Integer mmsMsgType = generateInteger(1, 4);
            str = str + "," + mmsMsgID + "," + mmsMsgType;
        }
        return str;
    }

    /**
     * Generate MSGF row
     * 
     * @return row
     */
    private String generateMSGF() {
        String str = NemoEvents.MSGF.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        str = str + "," + system.toString();
        Integer messageType = generateInteger(1, 2);
        str = str + "," + messageType;
        if (messageType == 1) {
            str = str + "," + generateContext(1);
            // ...
            Integer smsCause = generateInteger(27, 30);
            Integer smsMsgType = generateInteger(1, 5);
            str = str + "," + smsCause + "," + smsMsgType;
        }
        if (messageType == 2) {
            str = str + "," + generateContext(1);
            // ...
            Integer mmsCause = generateInteger(129, 136);
            Integer mmsMsgType = generateInteger(1, 4);
            str = str + "," + mmsCause + "," + mmsMsgType;
        }
        return str;
    }

    /**
     * Generate PTTA row
     * 
     * @return row
     */
    private String generatePTTA() {
        String str = NemoEvents.PTTA.getEventId() + "," + generateTimestamp() + ",";
        Integer pttTech = generateInteger(1, 2);
        str = str + "," + pttTech;
        if (pttTech == 1) {
            str = str + "," + generateContext(1);
            String pocServer = returnWordSoCalled("POC server address");
            str = str + "," + pocServer;
        }
        return str;
    }

    /**
     * Generate PTTF row
     * 
     * @return row
     */
    private String generatePTTF() {
        String str = NemoEvents.PTTF.getEventId() + "," + generateTimestamp() + ",";
        Integer pttTech = generateInteger(1, 2);
        str = str + "," + pttTech;
        if (pttTech == 1) {
            str = str + "," + generateContext(1);
            Integer failStatus = generateInteger(1, 5);
            Integer failCause = generateInteger(1, maxIntegerValue);
            str = str + "," + failStatus + "," + failCause;
        }
        return str;
    }

    /**
     * Generate PTTC row
     * 
     * @return row
     */
    private String generatePTTC() {
        String str = NemoEvents.PTTC.getEventId() + "," + generateTimestamp() + ",";
        Integer pttTech = generateInteger(1, 2);
        str = str + "," + pttTech;
        if (pttTech == 1) {
            str = str + "," + generateContext(1);
            Integer loginTime = generateInteger(1, maxIntegerValue);
            Integer groupAttachTime = generateInteger(1, maxIntegerValue);
            String pocServer = returnWordSoCalled("POC server address");
            str = str + "," + loginTime + "," + groupAttachTime + "," + pocServer;
        }
        return str;
    }

    /**
     * Generate PTTD row
     * 
     * @return row
     */
    private String generatePTTD() {
        String str = NemoEvents.PTTD.getEventId() + "," + generateTimestamp() + ",";
        Integer pttTech = generateInteger(1, 2);
        str = str + "," + pttTech;
        if (pttTech == 1) {
            str = str + "," + generateContext(1);
            Integer deactStatus = generateInteger(1, 3);
            Integer deactCause = generateInteger(1, maxIntegerValue);
            Integer deactTime = generateInteger(1, maxIntegerValue);
            str = str + "," + deactStatus + "," + deactCause + "," + deactTime;
        }
        return str;
    }

    /**
     * Generate PTTI row
     * 
     * @return row
     */
    private String generatePTTI() {
        String str = NemoEvents.PTTI.getEventId() + "," + generateTimestamp() + ",";
        Integer systemID = generateInteger(0, 3);
        Integer system = systems.get(systemID);
        str = str + "," + system;
        Integer pttTech = generateInteger(1, 2);
        str = str + "," + pttTech;
        if (pttTech == 1) {
            str = str + "," + generateContext(1);
            Integer pttState = generateInteger(1, 4);
            String pttUserIdentity = returnWordSoCalled("Push-to-talk user identity");
            // 1,2,...
            Integer pttStatus = generateInteger(21, 24);
            str = str + "," + pttState + "," + pttUserIdentity + "," + pttStatus;
        }
        return str;
    }

    /**
     * Generate RTPJITTER row
     * 
     * @return row
     */
    private String generateRTPJITTER() {
        String str = NemoEvents.RTPJITTER.getEventId() + "," + generateTimestamp() + ",";
        Integer rtpJitterType = 1;
        Integer rtpJitterDL = generateInteger(1, maxIntegerValue);
        Integer rtpJitterUL = generateInteger(1, maxIntegerValue);
        Integer rtpInterarrDL = generateInteger(1, maxIntegerValue);
        Integer rtpInterarrUL = generateInteger(1, maxIntegerValue);
        str = str + "," + rtpJitterType + "," + rtpJitterDL + "," + rtpJitterUL + "," + rtpInterarrDL + "," + rtpInterarrUL;
        return str;
    }

    /**
     * Generate GPS row
     * 
     * @return row
     */
    private String generateGPS() {
        String timestamp = generateTimestamp();
        String str = NemoEvents.GPS.getEventId() + "," + timestamp + ",";
        Float lon = generateFloat(0, 100);
        longitude = Double.parseDouble(lon.toString());
        Float lat = generateFloat(0, 100);
        latitude = Double.parseDouble(lat.toString());
        Integer height = generateInteger(1, maxIntegerValue);
        Integer distance = generateInteger(1, maxIntegerValue);
        Integer gpsFix = generateInteger(-1, 4);
        Integer satellites = generateInteger(1, maxIntegerValue);
        Integer velocity = generateInteger(1, maxIntegerValue);
        str = str + "," + lon + "," + lat + "," + height + "," + distance + "," + gpsFix + "," + satellites + "," + velocity;
        return str;
    }

    /**
     * Generate TNOTE row
     * 
     * @return row
     */
    private String generateTNOTE() {
        String str = NemoEvents.TNOTE.getEventId() + "," + generateTimestamp() + ",";
        String tNote = returnWordSoCalled("Textual note");
        str = str + "," + tNote;
        return str;
    }

    /**
     * Generate QNOTE row
     * 
     * @return row
     */
    private String generateQNOTE() {
        String str = NemoEvents.QNOTE.getEventId() + "," + generateTimestamp() + ",";
        Integer id = generateInteger(0, maxIntegerValue);
        Integer parentID = generateInteger(0, maxIntegerValue) - 1;
        String question = returnWordSoCalled("Service quality question");
        String answer = returnWordSoCalled("Service quality answer");
        String description = returnWordSoCalled("Service quality description");
        str = str + "," + id + "," + parentID + "," + question + "," + answer + "," + description;
        return str;
    }

    /**
     * Generate QTRIGGER row
     * 
     * @return row
     */
    private String generateQTRIGGER() {
        String str = NemoEvents.QTRIGGER.getEventId() + "," + generateTimestamp() + ",";
        String description = returnWordSoCalled("Service trigger description");
        str = str + "," + description;
        return str;
    }

    /**
     * Generate MARK row
     * 
     * @return row
     */
    private String generateMARK() {
        String str = NemoEvents.MARK.getEventId() + "," + generateTimestamp() + ",";
        Integer markerSeq = generateInteger(0, maxIntegerValue);
        Integer marker = generateInteger(1, 9);
        str = str + "," + markerSeq + "," + marker;
        return str;
    }

    /**
     * Generate ERR row
     * 
     * @return row
     */
    private String generateERR() {
        String str = NemoEvents.ERR.getEventId() + "," + generateTimestamp() + ",";
        String error = returnWordSoCalled("Error text");
        str = str + "," + error;
        return str;
    }

    /**
     * Generate DATE row
     * 
     * @return row
     */
    private String generateDATE() {
        String str = NemoEvents.DATE.getEventId() + "," + generateTimestamp() + ",";
        String date = returnWordSoCalled("Date");
        str = str + "," + date;
        return str;
    }

    /**
     * Generate PAUSE row
     * 
     * @return row
     */
    private String generatePAUSE() {
        String str = NemoEvents.PAUSE.getEventId() + "," + generateTimestamp() + ",";
        return str;
    }

    /**
     * Generate APP row
     * 
     * @return row
     */
    private String generateAPP() {
        String str = NemoEvents.APP.getEventId() + "," + generateTimestamp() + ",";
        Integer extAppState = generateInteger(1, 3);
        Integer extAppLaunch = generateInteger(0, 10);
        str = str + "," + extAppState + "," + extAppLaunch;
        for (int i = 0; i < extAppLaunch; i++) {
            String extAppName = returnWordSoCalled("External application name");
            String extAppParams = returnWordSoCalled("External application parameters");
            str = str + "," + extAppName + "," + extAppParams;
        }
        return str;
    }

    /**
     * Generate LOCK row
     * 
     * @return row
     */
    private String generateLOCK() {
        String str = NemoEvents.LOCK.getEventId() + "," + generateTimestamp() + ",";
        Integer forcings = 1;
        Integer lockType = generateInteger(1, 6);
        str = str + "," + forcings + "," + lockType;
        if (lockType == 1) {
            Integer params = 2;
            Integer lockedChannel = generateInteger(1, maxIntegerValue);
            // 0,10850,...
            Integer lockedBand = generateInteger(20001, 20015);
            str = str + "," + params + "," + lockedChannel + "," + lockedBand;
        }
        if (lockType == 2) {
            Integer params = 3;
            Integer lockedScr = generateInteger(0, 511);
            Integer lockedChannel = generateInteger(1, maxIntegerValue);
            // 0,10850,...
            Integer lockedBand = generateInteger(20001, 20015);
            str = str + "," + params + "," + lockedScr + "," + lockedChannel + "," + lockedBand;
        }
        if (lockType == 3) {
            Integer params = 1;
            Integer choice = generateInteger(1, 3);
            Integer lockedSystem = null;
            if (choice == 1) {
                lockedSystem = TechnologySystems.GSM.getId();
            }
            if (choice == 2) {
                lockedSystem = TechnologySystems.UMTS_FDD.getId();
            }
            if (choice == 3) {
                lockedSystem = TechnologySystems.UMTS_TD_SCDMA.getId();
            }
            str = str + "," + params + "," + lockedSystem;
        }
        if (lockType == 4) {
            Integer params = 1;
            // 0,10850,...
            Integer lockedBand = generateInteger(20001, 20015);
            str = str + "," + params + "," + lockedBand;
        }
        if (lockType == 5) {
            Integer params = 1;
            Integer cellBarringState = generateInteger(1, 3);
            str = str + "," + params + "," + cellBarringState;
        }
        if (lockType == 6) {
            Integer params = 0;
            str = str + "," + params;
        }
        return str;
    }

    private void generateAllEvents() {
        
        List<String> listParamsOfAG = generateAG();
        Map<String, Object> parsedParametersAG = NemoEvents.AG.fill(version, deleteInvertedCommas(listParamsOfAG));
        map.put(NemoEvents.AG.name(), parsedParametersAG);
        addRowInFile(createLineWithoutContext(NemoEvents.AG.name(), "", listParamsOfAG));

        List<String> listParamsOfBF = generateBF();
        Map<String, Object> parsedParametersBF = NemoEvents.BF.fill(version, deleteInvertedCommas(listParamsOfBF));
        map.put(NemoEvents.BF.name(), parsedParametersBF);
        addRowInFile(createLineWithoutContext(NemoEvents.BF.name(), "", listParamsOfBF));

        List<String> listParamsOfCInf = generateCInf();
        Map<String, Object> parsedParametersCInf = NemoEvents.CInf.fill(version, deleteInvertedCommas(listParamsOfCInf));
        map.put(NemoEvents.CInf.name(), parsedParametersCInf);
        addRowInFile(createLineWithoutContext(NemoEvents.CInf.name(), "", listParamsOfCInf));

        List<String> listParamsOfCL = generateCL();
        Map<String, Object> parsedParametersCL = NemoEvents.CL.fill(version, deleteInvertedCommas(listParamsOfCL));
        map.put(NemoEvents.CL.name(), parsedParametersCL);
        addRowInFile(createLineWithoutContext(NemoEvents.CL.name(), "", listParamsOfCL));

        List<String> listParamsOfDL = generateDL();
        Map<String, Object> parsedParametersDL = NemoEvents.DL.fill(version, deleteInvertedCommas(listParamsOfDL));
        map.put(NemoEvents.DL.name(), parsedParametersDL);
        addRowInFile(createLineWithoutContext(NemoEvents.DL.name(), "", listParamsOfDL));

        List<String> listParamsOfDN = generateDN();
        Map<String, Object> parsedParametersDN = NemoEvents.DN.fill(version, deleteInvertedCommas(listParamsOfDN));
        map.put(NemoEvents.DN.name(), parsedParametersDN);
        addRowInFile(createLineWithoutContext(NemoEvents.DN.name(), "", listParamsOfDN));

        List<String> listParamsOfDS = generateDS();
        Map<String, Object> parsedParametersDS = NemoEvents.DS.fill(version, deleteInvertedCommas(listParamsOfDS));
        map.put(NemoEvents.DS.name(), parsedParametersDS);
        addRowInFile(createLineWithoutContext(NemoEvents.DS.name(), "", listParamsOfDS));

        List<String> listParamsOfDT = generateDT();
        Map<String, Object> parsedParametersDT = NemoEvents.DT.fill(version, deleteInvertedCommas(listParamsOfDT));
        map.put(NemoEvents.DT.name(), parsedParametersDT);
        addRowInFile(createLineWithoutContext(NemoEvents.DT.name(), "", listParamsOfDT));

        List<String> listParamsOfFF = generateFF();
        Map<String, Object> parsedParametersFF = NemoEvents.FF.fill(version, deleteInvertedCommas(listParamsOfFF));
        map.put(NemoEvents.FF.name(), parsedParametersFF);
        addRowInFile(createLineWithoutContext(NemoEvents.FF.name(), "", listParamsOfFF));

        List<String> listParamsOfEI = generateEI();
        Map<String, Object> parsedParametersEI = NemoEvents.EI.fill(version, deleteInvertedCommas(listParamsOfEI));
        map.put(NemoEvents.EI.name(), parsedParametersEI);
        addRowInFile(createLineWithoutContext(NemoEvents.EI.name(), "", listParamsOfEI));

        List<String> listParamsOfHV = generateHV();
        Map<String, Object> parsedParametersHV = NemoEvents.HV.fill(version, deleteInvertedCommas(listParamsOfHV));
        map.put(NemoEvents.HV.name(), parsedParametersHV);
        addRowInFile(createLineWithoutContext(NemoEvents.HV.name(), "", listParamsOfHV));

        List<String> listParamsOfHW = generateHW();
        Map<String, Object> parsedParametersHW = NemoEvents.HW.fill(version, deleteInvertedCommas(listParamsOfHW));
        map.put(NemoEvents.HW.name(), parsedParametersHW);
        addRowInFile(createLineWithoutContext(NemoEvents.HW.name(), "", listParamsOfHW));

        List<String> listParamsOfID = generateID();
        Map<String, Object> parsedParametersID = NemoEvents.ID.fill(version, deleteInvertedCommas(listParamsOfID));
        map.put(NemoEvents.ID.name(), parsedParametersID);
        addRowInFile(createLineWithoutContext(NemoEvents.ID.name(), "", listParamsOfID));

        List<String> listParamsOfML = generateML();
        Map<String, Object> parsedParametersML = NemoEvents.ML.fill(version, deleteInvertedCommas(listParamsOfML));
        map.put(NemoEvents.ML.name(), parsedParametersML);
        addRowInFile(createLineWithoutContext(NemoEvents.ML.name(), "", listParamsOfML));

        List<String> listParamsOfNN = generateNN();
        Map<String, Object> parsedParametersNN = NemoEvents.NN.fill(version, deleteInvertedCommas(listParamsOfNN));
        map.put(NemoEvents.NN.name(), parsedParametersNN);
        addRowInFile(createLineWithoutContext(NemoEvents.NN.name(), "", listParamsOfNN));
        
        List<String> listParamsOfMF = generateMF();
        Map<String,Object> parsedParametersMF = NemoEvents.MF.fill(version, deleteInvertedCommas(listParamsOfMF));
        map.put(NemoEvents.MF.name(),parsedParametersMF);
        addRowInFile(createLineWithoutContext(NemoEvents.MF.name(),"",listParamsOfMF));

        List<String> listParamsOfPC = generatePC();
        Map<String,Object> parsedParametersPC = NemoEvents.PC.fill(version, deleteInvertedCommas(listParamsOfPC));
        map.put(NemoEvents.PC.name(),parsedParametersPC);
        addRowInFile(createLineWithoutContext(NemoEvents.PC.name(),"",listParamsOfPC));

        List<String> listParamsOfPRODUCT = generatePRODUCT();
        Map<String,Object> parsedParametersPRODUCT = NemoEvents.PRODUCT.fill(version, deleteInvertedCommas(listParamsOfPRODUCT));
        map.put(NemoEvents.PRODUCT.name(),parsedParametersPRODUCT);
        addRowInFile(createLineWithoutContext(NemoEvents.PRODUCT.name(),"",listParamsOfPRODUCT));

        List<String> listParamsOfSI = generateSI();
        Map<String,Object> parsedParametersSI = NemoEvents.SI.fill(version, deleteInvertedCommas(listParamsOfSI));
        map.put(NemoEvents.SI.name(),parsedParametersSI);
        addRowInFile(createLineWithoutContext(NemoEvents.SI.name(),"",listParamsOfSI));

        List<String> listParamsOfSP = generateSP();
        Map<String,Object> parsedParametersSP = NemoEvents.SP.fill(version, deleteInvertedCommas(listParamsOfSP));
        map.put(NemoEvents.SP.name(),parsedParametersSP);
        addRowInFile(createLineWithoutContext(NemoEvents.SP.name(),"",listParamsOfSP));

        List<String> listParamsOfSW= generateSW();
        Map<String,Object> parsedParametersSW = NemoEvents.SW.fill(version, deleteInvertedCommas(listParamsOfSW));
        map.put(NemoEvents.SW.name(),parsedParametersSW);
        addRowInFile(createLineWithoutContext(NemoEvents.SW.name(),"",listParamsOfSW));

        List<String> listParamsOfTS = generateTS();
        Map<String,Object> parsedParametersTS = NemoEvents.TS.fill(version, deleteInvertedCommas(listParamsOfTS));
        map.put(NemoEvents.TS.name(),parsedParametersTS);
        addRowInFile(createLineWithoutContext(NemoEvents.TS.name(),"",listParamsOfTS));

        List<String> listParamsOfUT = generateUT();
        Map<String,Object> parsedParametersUT = NemoEvents.UT.fill(version, deleteInvertedCommas(listParamsOfUT));
        map.put(NemoEvents.UT.name(),parsedParametersUT);
        addRowInFile(createLineWithoutContext(NemoEvents.UT.name(),"",listParamsOfUT));

        List<String> listParamsOfVQ = generateVQ();
        Map<String,Object> parsedParametersVQ = NemoEvents.VQ.fill(version, deleteInvertedCommas(listParamsOfVQ));
        map.put(NemoEvents.VQ.name(),parsedParametersVQ);
        addRowInFile(createLineWithoutContext(NemoEvents.VQ.name(),"",listParamsOfVQ));

        List<String> listParamsOfSTART = generateSTART();
        Map<String,Object> parsedParametersSTART = NemoEvents.START.fill(version, deleteInvertedCommas(listParamsOfSTART));
        map.put(NemoEvents.START.name(),parsedParametersSTART);
        addRowInFile(createLineWithoutContext(NemoEvents.START.name(),generateTimestamp(),listParamsOfSTART));
    }

    /**
     * Create line of event without context
     * 
     * @param name
     * @param timestamp
     * @param listParameters
     * @return line
     */
    private String createLineWithoutContext(String name, String timestamp, List<String> listParameters) {
        String str = name + "," + timestamp + ",";
        for (String parameter : listParameters) {
            str = str + "," + parameter;
        }
        return str;
    }

    /**
     * delete inverted commas from parameter
     *
     * @param listParameters
     * @return list without inverted commas
     */
    private List<String> deleteInvertedCommas(List<String> listParameters) {
        List<String> listWithoutInvertedCommas = new ArrayList();
        for (String parameter : listParameters) {
            if (parameter.startsWith("\"")) {
                parameter = parameter.replace("\"", "");
            }
            listWithoutInvertedCommas.add(parameter);
        }
        return listWithoutInvertedCommas;
    }

    /**
     * Create dir and file
     * 
     * @return file
     */
    public File createNemoFile() {
        File dir = new File(System.getProperty("user.home") + File.separatorChar + "generated_files");
        dir.mkdir();
        File nemoFile = new File(dir, "nemo2x.nmf");
        try {
            nemoFile.createNewFile();
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return nemoFile;
    }

    public void fillNemoFile(File nemoFile) {
        try {
            createListOfSystems();
            wr = new FileWriter(nemoFile);
            generateAllEvents();
            wr.close();
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Addes row in file
     * 
     * @param row
     * @param wr
     */
    public void addRowInFile(String row) {
        try {
            wr.write(row);
            wr.write("\n");
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Generate Integer value by range
     * 
     * @param minValue
     * @param maxValue
     * @return Integer value
     */
    private Integer generateInteger(Integer minValue, Integer maxValue) {
        int range = maxValue - minValue + 1;
        int integerValue = rand.nextInt(range) + minValue;
        return integerValue;
    }

    /**
     * Generate float value
     * 
     * @param minIntegerValue
     * @param maxIntegerValue
     * @return float value
     */
    private Float generateFloat(Integer minIntegerValue, Integer maxIntegerValue) {
        Integer intValue = generateInteger(minIntegerValue, maxIntegerValue);
        Float floatValue = rand.nextFloat();
        Float f = new Float(intValue);
        return f + floatValue;
    }

    /**
     * Generate timestamp
     * 
     * @return timestamp
     */
    @SuppressWarnings("deprecation")
    private String generateTimestamp() {
        Date date = new Date(System.currentTimeMillis());
        Long millis = date.getTime();
        Long m = millis % 1000;
        String strDate = MessageFormat.format("{0}:{1}:{2}.{3}", date.getHours(), date.getMinutes(), date.getSeconds(), m);
        return strDate;
    }

    /**
     * Generate date
     * 
     * @return date
     */
    @SuppressWarnings("deprecation")
    private String generateDate() {
        Date date = new Date(System.currentTimeMillis());
        String strDate = MessageFormat.format("{0}.{1}.{2}", date.getDay(), date.getMonth(), date.getYear());
        return strDate;
    }

    /**
     * Generate context
     * 
     * @return context row
     */
    private String generateContext(Integer numberOfContextIDs) {
        String str = "";
        if (numberOfContextIDs == 0) {
            str = str + "";
        } else {
            str = str + numberOfContextIDs.toString();
            String[] contextIDs = new String[numberOfContextIDs];
            for (int i = 0; i < numberOfContextIDs; i++) {
                Integer id = generateInteger(0, 10);
                if (id == 0) {
                    contextIDs[i] = "";
                } else {
                    contextIDs[i] = id.toString();
                }
            }
            for (String contextID : contextIDs) {
                str = str + "," + contextID;
            }
        }
        return str;
    }

    private String generateDataOfProtocol() {
        String str = "";
        Integer protocol = generateInteger(0, 14);
        Integer dataFailStatus = generateInteger(1, 5);
        str = str + "," + protocol + "," + dataFailStatus;
        if (dataFailStatus == 5) {
            String reserved = "n/a";
            str = str + "," + reserved;
        }
        if (dataFailStatus == 2) {
            // 10004,...
            Integer socketCause = generateInteger(10035, 11031);
            str = str + "," + socketCause;
        }
        if (protocol == 0 || protocol == 1 || protocol == 2) {
            Integer dataTransferCause = 1;
            str = str + "," + dataTransferCause;
        }
        if (protocol == 3) {
            // 401,...
            Integer ftpCause = generateInteger(1, 5);
            str = str + "," + ftpCause;
        }
        if (protocol == 4) {
            // 1,2,...
            Integer httpCause = generateInteger(200, 206);
            str = str + "," + httpCause;
        }
        if (protocol == 5) {
            // 1,2,5,...
            Integer smptCause = generateInteger(500, 504);
            str = str + "," + smptCause;
        }
        if (protocol == 6) {
            // 1,2,6,...
            Integer pop3Cause = generateInteger(1, 2);
            str = str + "," + pop3Cause;
        }
        if (protocol == 7 || protocol == 8) {
            // 100,101,200,...
            Integer cause = generateInteger(0, 9);
            str = str + "," + cause;
        }
        if (protocol == 9) {
            // ...
            Integer streamingCause = generateInteger(65489, 65535);
            str = str + "," + streamingCause;
        }
        if (protocol == 11) {
            String dataTransferCause = "1";
            str = str + "," + dataTransferCause;
        }
        if (protocol == 12) {
            String icmpPingCause = "2";
            str = str + "," + icmpPingCause;
        }
        if (protocol == 13 || protocol == 14) {
            String dataTransferCause = "1";
            str = str + "," + dataTransferCause;
        }
        return str;
    }

    private String generateDataOfProtocol2() {
        String str = "";
        Integer protocol = generateInteger(0, 14);
        Integer dataFailStatus = generateInteger(1, 4);
        str = str + "," + protocol + "," + dataFailStatus;
        if (dataFailStatus == 1) {
            String reserved = "n/a";
            str = str + "," + reserved;
        }
        if (dataFailStatus == 2) {
            // 10004,...
            Integer socketCause = generateInteger(10035, 11031);
            str = str + "," + socketCause;
        }
        if (protocol == 0 || protocol == 1 || protocol == 2) {
            Integer dataTransferCause = 1;
            str = str + "," + dataTransferCause;
        }
        if (protocol == 3) {
            // 421,...
            Integer ftpCause = generateInteger(1, 5);
            str = str + "," + ftpCause;
        }
        if (protocol == 4) {
            // 1,2,...
            Integer httpCause = generateInteger(200, 206);
            str = str + "," + httpCause;
        }
        if (protocol == 5) {
            // 1,2,5,...
            Integer smptCause = generateInteger(500, 504);
            str = str + "," + smptCause;
        }
        if (protocol == 6) {
            // 1,2,6,...
            Integer pop3Cause = generateInteger(1, 2);
            str = str + "," + pop3Cause;
        }
        if (protocol == 7 || protocol == 8) {
            // 100,101,200,...
            Integer cause = generateInteger(0, 9);
            str = str + "," + cause;
        }
        if (protocol == 9) {
            // ...
            Integer streamingCause = generateInteger(65489, 65535);
            str = str + "," + streamingCause;
        }
        if (protocol == 11) {
            Integer dataTransferCause = 1;
            str = str + "," + dataTransferCause;
        }
        if (protocol == 12) {
            Integer icmpPingCause = 2;
            str = str + "," + icmpPingCause;
        }
        if (protocol == 13 || protocol == 14) {
            Integer dataTransferCause = 1;
            str = str + "," + dataTransferCause;
        }
        return str;
    }

    private void createListOfSystems() {
        for (TechnologySystems system : TechnologySystems.values()) {
            systems.add(system.getId());
        }
    }

    /**
     * Generate Technology System
     * 
     * @return id of system
     */
    private Integer generateTechnologySystems() {
        return TechnologySystems.values()[rand.nextInt(TechnologySystems.values().length)].getId();
    }

    /**
     * Return word so-called
     * 
     * @param word
     * @return word so-called
     */
    public static String returnWordSoCalled(String word) {
        return "\"" + word + "\"";
    }

    public File generateNemo2File() {
        File nemoFile = createNemoFile();
        fillNemoFile(nemoFile);
        return nemoFile;
    }

    public static void main(String[] args) {
        Nemo2Generator nemo2Generator = new Nemo2Generator();
        Map<String, Object> parsedParametersCInf = new LinkedHashMap<String, Object>();
        parsedParametersCInf = NemoEvents.CInf.fill("2.01", nemo2Generator.generateCInf());
        for (String parametr : parsedParametersCInf.keySet()) {
            System.out.println(parametr + " " + parsedParametersCInf.get(parametr));
        }
    }

}
