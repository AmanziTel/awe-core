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

package org.amanzi.neo.loader.core.dataGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    /**
     * Generate #AG row
     * 
     * @return row
     */
    private String generateAG() {
        Float ag = generateFloat(0, 100);
        String str = NemoEvents.AG.getEventId() + ",,," + ag;
        return str;
    }

    /**
     * Generate #BF row
     * 
     * @return row
     */
    private String generateBF() {
        String btsFile = "BTS filename";
        String str = NemoEvents.BF.getEventId() + ",,," + returnWordSoCalled(btsFile);
        return str;
    }

    /**
     * Generate #CI row
     * 
     * @return row
     */
    private String generateCI() {
        String converterName = "Converter name";
        String converterVersion = "Converter version";
        String convertedFile = "Converted filename";
        String str = NemoEvents.CInf.getEventId() + ",,," + returnWordSoCalled(converterName) + ","
                + returnWordSoCalled(converterVersion) + "," + returnWordSoCalled(convertedFile);
        return str;
    }

    /**
     * Generate #CL row
     * 
     * @return row
     */
    private String generateCL() {
        Float cl = generateFloat(0, 100);
        String str = NemoEvents.CL.getEventId() + ",,," + cl;
        return str;
    }

    /**
     * Generate #DL row
     * 
     * @return row
     */
    private String generateDL() {
        String deviceLabel = "Device label";
        String str = NemoEvents.DL.getEventId() + ",,," + returnWordSoCalled(deviceLabel);
        return str;
    }

    /**
     * Generate #DN row
     * 
     * @return row
     */
    private String generateDN() {
        String deviceName = "Device name";
        String str = NemoEvents.DN.getEventId() + ",,," + returnWordSoCalled(deviceName);
        return str;
    }

    /**
     * Generate #DS row
     * 
     * @return row
     */
    private String generateDS() {
        Integer numberOfSystems = systems.size();
        String numberOfSupportedSystems = numberOfSystems.toString();
        String[] supportedSystems = new String[numberOfSystems];
        for (int i = 0; i < numberOfSystems; i++) {
            supportedSystems[i] = systems.get(i).toString();
        }
        String str = NemoEvents.DS.getEventId() + ",,," + numberOfSupportedSystems;
        for (String system : supportedSystems) {
            str = str + "," + system;
        }
        return str;
    }

    /**
     * Generate #DT row
     * 
     * @return row
     */
    private String generateDT() {
        Integer deviceType = generateInteger(0, 1);
        String str = NemoEvents.DT.getEventId() + ",,," + deviceType;
        return str;
    }

    /**
     * Generate #FF row
     * 
     * @return row
     */
    private String generateFF() {
        String fileFormatVersion = "File format version";
        String str = NemoEvents.FF.getEventId() + ",,," + returnWordSoCalled(fileFormatVersion);
        return str;
    }

    /**
     * Generate #EI row
     * 
     * @return row
     */
    private String generateEI() {
        String deviceIdentity = "Device identity";
        String str = NemoEvents.EI.getEventId() + ",,," + returnWordSoCalled(deviceIdentity);
        return str;
    }

    /**
     * Generate #HV row
     * 
     * @return row
     */
    private String generateHV() {
        String handlerVersion = "Handler version";
        String str = NemoEvents.HV.getEventId() + ",,," + returnWordSoCalled(handlerVersion);
        return str;
    }

    /**
     * Generate #HW row
     * 
     * @return row
     */
    private String generateHW() {
        String hardwareVersion = "Hardware version";
        String str = NemoEvents.HW.getEventId() + ",,," + returnWordSoCalled(hardwareVersion);
        return str;
    }

    /**
     * Generate #ID row
     * 
     * @return row
     */
    private String generateID() {
        String measurementID = "Measurement id";
        String str = NemoEvents.ID.getEventId() + ",,," + returnWordSoCalled(measurementID);
        return str;
    }

    /**
     * Generate #MF row
     * 
     * @return row
     */
    private String generateMF() {
        String mapFile = "Map filename";
        String str = NemoEvents.MF.getEventId() + ",,," + returnWordSoCalled(mapFile);
        return str;
    }

    /**
     * Generate #ML row
     * 
     * @return row
     */
    private String generateML() {
        String measurementLabel = "Measurement label";
        String str = NemoEvents.ML.getEventId() + ",,," + returnWordSoCalled(measurementLabel);
        return str;
    }

    /**
     * Generate #NN row
     * 
     * @return row
     */
    private String generateNN() {
        String networkName = "Network name";
        String str = NemoEvents.NN.getEventId() + ",,," + returnWordSoCalled(networkName);
        return str;
    }

    /**
     * Generate #PC row
     * 
     * @return row
     */
    private String generatePC() {
        Integer packetCaptureState = generateInteger(0, 1);
        String str = NemoEvents.PC.getEventId() + ",,," + packetCaptureState;
        return str;
    }

    /**
     * Generate #PRODUCT row
     * 
     * @return row
     */
    private String generatePRODUCT() {
        String productName = "Product name";
        String productVersion = "Product version";
        String str = NemoEvents.PRODUCT.getEventId() + ",,," + returnWordSoCalled(productName) + ","
                + returnWordSoCalled(productVersion);
        return str;
    }

    /**
     * Generate #SI row
     * 
     * @return row
     */
    private String generateSI() {
        String subscriberIdentity = "Subscriber identity";
        String str = NemoEvents.SI.getEventId() + ",,," + returnWordSoCalled(subscriberIdentity);
        return str;
    }

    /**
     * Generate #SP row
     * 
     * @return row
     */
    private String generateSP() {
        String subscriberPhoneNumber = "Subscriber phone number";
        String str = NemoEvents.SP.getEventId() + ",,," + returnWordSoCalled(subscriberPhoneNumber);
        return str;
    }

    /**
     * Generate #SW row
     * 
     * @return row
     */
    private String generateSW() {
        String deviceSoftwareVersion = "Device software version";
        String str = NemoEvents.SW.getEventId() + ",,," + returnWordSoCalled(deviceSoftwareVersion);
        return str;
    }

    /**
     * Generate #TS row
     * 
     * @return row
     */
    private String generateTS() {
        String testScriptFilename = "Test script filename";
        String str = NemoEvents.TS.getEventId() + ",,," + returnWordSoCalled(testScriptFilename);
        return str;
    }

    /**
     * Generate #UT row
     * 
     * @return row
     */
    private String generateUT() {
        Integer gapToUTC = generateInteger(-720, 720);
        String str = NemoEvents.UT.getEventId() + ",,," + gapToUTC;
        return str;
    }

    /**
     * Generate #VQ row
     * 
     * @return row
     */
    private String generateVQ() {
        Integer vqType = generateInteger(0, 4);
        String vqVersion = "Voice quality version";
        String str = NemoEvents.VQ.getEventId() + ",,," + vqType + "," + returnWordSoCalled(vqVersion);
        return str;
    }

    /**
     * Generate #START row
     * 
     * @return row
     */
    private String generateSTART() {
        String timestamp = generateTimestamp();
        String date = generateDate();
        String str = NemoEvents.START.getEventId() + "," + timestamp + ",," + returnWordSoCalled(date);
        return str;
    }

    /**
     * Generate #STOP row
     * 
     * @return row
     */
    private String generateSTOP() {
        String timestamp = generateTimestamp();
        String date = generateDate();
        String str = NemoEvents.STOP.getEventId() + "," + timestamp + ",," + returnWordSoCalled(date);
        return str;
    }

    /**
     * Generate CAA row
     * 
     * @return row
     */
    private String generateCAA() {
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
        String str = NemoEvents.DAF.getEventId() + "," + generateTimestamp() + "," + generateContext(1) + generateDataOfProtocol();
        return str;
    }

    /**
     * Generate DAD row
     * 
     * @return row
     */
    private String generateDAD() {
        String str = NemoEvents.DAD.getEventId() + "," + generateTimestamp() + "," + generateContext(1) + generateDataOfProtocol();
        return str;
    }

    /**
     * Generate DREQ row
     * 
     * @return row
     */
    private String generateDREQ() {
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
        String str = NemoEvents.DCONTENT.getEventId() + "," + generateTimestamp() + ",";
        Integer protocol = generateInteger(0, 14);
        str = str + "," + protocol;
        if (protocol == 8 || protocol == 10) {
            str = str + generateContext(1);
            Integer numberOfContentElements = generateInteger(0, 10);
            str = str + "," + numberOfContentElements;
            for (int i = 0; i < numberOfContentElements; i++) {
                String numberOfParametersPerContent = "3";
                String contentURL = "Content URL";
                Integer contentType = generateInteger(1, 3);
                Integer contentSize = generateInteger(0, maxIntegerValue);
                str = str + "," + numberOfParametersPerContent + "," + returnWordSoCalled(contentURL) + "," + contentType + ","
                        + contentSize;
            }
        }
        return str;
    }

    private String generateCELLMEAS() {
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
        str = str + "," + system.toString();
        if (system == 1) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String arfcn = generateInteger(0, maxIntegerValue).toString();
            String bsic = generateInteger(0, 63).toString();
            String rxLevFull = generateFloat(-120, -11).toString();
            String rxLevSub = generateFloat(-120, -11).toString();
            String c1 = generateFloat(0, 100).toString();
            String c2 = generateFloat(0, 100).toString();
            String c31 = generateFloat(0, 100).toString();
            String c32 = generateFloat(0, 100).toString();
            String hcsPriority = generateInteger(0, 7).toString();
            String hcsThr = generateFloat(-110, -49).toString();
            String cellID = generateInteger(0, 65535).toString();
            String lac = generateInteger(0, 65535).toString();
            String rac = generateInteger(0, maxIntegerValue).toString();
            String srxlev = generateFloat(-107, -91).toString();
            str = str + "," + headersParams + "," + cells + "," + parametersPerCell + "," + cellType + "," + band + "," + arfcn
                    + "," + bsic + "," + rxLevFull + "," + rxLevSub + "," + c1 + "," + c2 + "," + c31 + "," + c32 + ","
                    + hcsPriority + "," + hcsThr + "," + cellID + "," + lac + "," + rac + "," + srxlev;
        }
        if (system == 2) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String arfcn = generateInteger(0, maxIntegerValue).toString();
            String lac = generateInteger(0, 65535).toString();
            String rssi = generateFloat(-111, -11).toString();
            String c1 = generateFloat(0, 100).toString();
            String c2 = generateFloat(0, 100).toString();
            String cc = generateInteger(0, 63).toString();
            str = str + "," + headersParams + "," + cells + "," + parametersPerCell + "," + cellType + "," + band + "," + arfcn
                    + "," + lac + "," + rssi + "," + c1 + "," + c2 + "," + cc;
        }
        if (system == 5) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfChs = generateInteger(0, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParametersPerChs = numberOfHeadersParams / numberOfChs;
            String parametrsPerChs = numberOfParametersPerChs.toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String rssi = generateFloat(0, 100).toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 3).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String ch2 = generateInteger(0, maxIntegerValue).toString();
            String scr = generateInteger(0, 511).toString();
            String ecn0 = generateFloat(-26, -1).toString();
            String sttd = generateInteger(0, 1).toString();
            String rscp = generateFloat(-150, -20).toString();
            String secondaryScr = generateInteger(0, 15).toString();
            String squal = generateFloat(-24, 23).toString();
            String srxlev = generateFloat(-107, 89).toString();
            String hqual = generateFloat(-32, 23).toString();
            String hrxlev = generateFloat(-115, 89).toString();
            String rqual = generateFloat(-200, 49).toString();
            String rrxlev = generateFloat(-191, 24).toString();
            String off = generateInteger(0, 255).toString();
            String tm = generateFloat(0, 38399).toString();
            String pathloss = generateFloat(0, 119).toString();
            str = str + "," + headersParams + "," + chs + "," + parametrsPerChs + "," + ch + "," + rssi + "," + cells + ","
                    + parametersPerCell + "," + cellType + "," + band + "," + ch2 + "," + scr + "," + ecn0 + "," + sttd + ","
                    + rscp + "," + secondaryScr + "," + squal + "," + srxlev + "," + hqual + "," + hrxlev + "," + rqual + ","
                    + rrxlev + "," + off + "," + tm + "," + pathloss;
        }
        if (system == 6) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfChs = generateInteger(0, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParametersPerChs = numberOfHeadersParams / numberOfChs;
            String parametrsPerChs = numberOfParametersPerChs.toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String rssi = generateFloat(0, 100).toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band2 = generateInteger(20001, 20015).toString();
            String ch2 = generateInteger(0, maxIntegerValue).toString();
            String cellParamsID = generateInteger(0, 127).toString();
            String rscp = generateFloat(-116, -21).toString();
            String srxlev = generateFloat(-107, 89).toString();
            String hrxlev = generateFloat(-115, 89).toString();
            String rrxlev = generateFloat(-191, 24).toString();
            String pathloss = generateFloat(46, 147).toString();
            str = str + "," + headersParams + "," + chs + "," + parametrsPerChs + "," + band + "," + ch + "," + rssi + "," + cells
                    + "," + parametersPerCell + "," + cellType + "," + band2 + "," + ch2 + "," + cellParamsID + "," + rscp + ","
                    + srxlev + "," + hrxlev + "," + rrxlev + "," + pathloss;
        }
        if (system == 10 || system == 11) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfChs = generateInteger(0, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParametersPerChs = numberOfHeadersParams / numberOfChs;
            String parametrsPerChs = numberOfParametersPerChs.toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String rxPower = generateFloat(-120, 29).toString();
            String rx0Power = generateFloat(-120, 29).toString();
            String rx1Power = generateFloat(-120, 29).toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String set = generateInteger(0, 3).toString();
            // 0,...
            String band2 = generateInteger(20001, 20015).toString();
            String ch2 = generateInteger(0, maxIntegerValue).toString();
            String pn = generateInteger(0, maxIntegerValue).toString();
            String eci0 = generateFloat(-32, -1).toString();
            String walsh = generateInteger(0, maxIntegerValue).toString();
            String rscp = generateFloat(-150, -21).toString();
            str = str + "," + headersParams + "," + chs + "," + parametrsPerChs + "," + band + "," + ch + "," + rxPower + ","
                    + rx0Power + "," + rx1Power + "," + cells + "," + parametersPerCell + "," + set + "," + band2 + "," + ch2 + ","
                    + pn + "," + eci0 + "," + walsh + "," + rscp;
        }
        if (system == 12) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfChs = generateInteger(0, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParametersPerChs = numberOfHeadersParams / numberOfChs;
            String parametrsPerChs = numberOfParametersPerChs.toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String rxPower = generateFloat(-120, 29).toString();
            String rx0Power = generateFloat(-120, 29).toString();
            String rx1Power = generateFloat(-120, 29).toString();
            Integer numberOfChs2 = generateInteger(0, maxIntegerValue);
            String chs2 = numberOfChs2.toString();
            Integer numberOfParametersPerChs2 = numberOfHeadersParams / numberOfChs2;
            String parametrsPerChs2 = numberOfParametersPerChs2.toString();
            String set = generateInteger(0, 3).toString();
            String band2 = generateInteger(20001, 20015).toString();
            String ch2 = generateInteger(0, maxIntegerValue).toString();
            String pn = generateInteger(0, maxIntegerValue).toString();
            String eci0 = generateFloat(-32, -1).toString();
            String rscp = generateFloat(-150, -21).toString();
            str = str + "," + headersParams + "," + chs + "," + parametrsPerChs + "," + band + "," + ch + "," + rxPower + ","
                    + rx0Power + "," + rx1Power + "," + chs2 + "," + parametrsPerChs2 + "," + set + "," + band2 + "," + ch2 + ","
                    + pn + "," + eci0 + "," + rscp;
        }
        if (system == 20) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String quality = generateFloat(0, 99).toString();
            String channel = generateInteger(0, maxIntegerValue).toString();
            String rssi = generateFloat(-100, 19).toString();
            String ssid = returnWordSoCalled("WLAN service set identifier");
            String macAddr = returnWordSoCalled("WLAN MAC address");
            String security = generateInteger(0, 4).toString();
            String maxTransferRate = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + headersParams + "," + cells + "," + parametersPerCell + "," + cellType + "," + band + "," + quality
                    + "," + channel + "," + rssi + "," + ssid + "," + macAddr + "," + security + "," + maxTransferRate;
        }
        if (system == 21) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String quality = generateFloat(0, 99).toString();
            String channel = generateInteger(0, maxIntegerValue).toString();
            String rssi = generateFloat(-100, 19).toString();
            String ssid = returnWordSoCalled("WLAN service set identifier");
            String macAddr = returnWordSoCalled("WLAN MAC address");
            str = str + "," + headersParams + "," + cells + "," + parametersPerCell + "," + cellType + "," + band + "," + ","
                    + quality + "," + channel + "," + rssi + "," + ssid + "," + macAddr;
        }
        if (system == 25) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String frequency = generateFloat(-100, 100).toString();
            String preambleIndex = generateInteger(0, 113).toString();
            String bsID = returnWordSoCalled("WiMAX base station ID");
            String rssi = generateFloat(-120, 19).toString();
            String rssiDev = generateFloat(0, 49).toString();
            String cinr = generateFloat(-32, 39).toString();
            String cinrDev = generateFloat(0, 39).toString();
            str = str + "," + headersParams + "," + cells + "," + parametersPerCell + "," + cellType + "," + band + "," + frequency
                    + "," + preambleIndex + "," + bsID + "," + rssi + "," + rssiDev + "," + cinr + "," + cinrDev;
        }
        if (system == 51 || system == 52) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String sat = generateInteger(0, 6).toString();
            String rxLev = generateFloat(-120, -11).toString();
            str = str + "," + headersParams + "," + cells + "," + parametersPerCell + "," + cellType + "," + band + "," + ch + ","
                    + sat + "," + rxLev;
        }
        if (system == 53) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headersParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParametersPerCell = numberOfHeadersParams / numberOfCells;
            String parametersPerCell = numberOfParametersPerCell.toString();
            String cellType = generateInteger(0, 1).toString();
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String dcc = generateInteger(0, 255).toString();
            String rxLev = generateFloat(-120, -11).toString();
            str = str + "," + headersParams + "," + cells + "," + parametersPerCell + "," + cellType + "," + band + "," + ch + ","
                    + dcc + "," + rxLev;
        }
        return str;
    }

    private String generateADJMEAS() {
        String str = NemoEvents.ADJMEAS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        str = str + "," + system.toString();
        if (system == 1) {
            String headersParams = "0";
            Integer numberOfChs = generateInteger(0, 10);
            String chs = numberOfChs.toString();
            str = str + "," + headersParams + "," + chs;
            for (int i = 0; i < numberOfChs; i++) {
                String parametrsPerChs = "11";
                String caChannel = generateInteger(0, maxIntegerValue).toString();
                String caMinimum = generateFloat(-100, 99).toString();
                String rssi = generateFloat(-120, -11).toString();
                String ca1 = generateFloat(-100, 99).toString();
                String rssi1 = generateFloat(-120, -11).toString();
                String ca11 = generateFloat(-100, 99).toString();
                String rssi11 = generateFloat(-120, -11).toString();
                String ca2 = generateFloat(-100, 99).toString();
                String rssi2 = generateFloat(-120, -11).toString();
                String ca22 = generateFloat(-100, 99).toString();
                String rssi22 = generateFloat(-120, -11).toString();
                str = str + "," + parametrsPerChs + "," + caChannel + "," + caMinimum + "," + rssi + "," + ca1 + "," + rssi1 + ","
                        + ca11 + "," + rssi11 + "," + ca2 + "," + rssi2 + "," + ca22 + "," + rssi22;
            }
        }
        return str;
    }

    private String generateRXQ() {
        String str = NemoEvents.RXQ.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        Integer choice = generateInteger(1, 2);
        if (choice == 1) {
            system = 1;
        } else {
            system = 53;
        }
        str = str + "," + system.toString();
        if (system == 1) {
            String rxqFull = generateInteger(0, maxIntegerValue).toString();
            String rxqSub = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + rxqFull + "," + rxqSub;
        }
        if (system == 53) {
            String berClass = generateInteger(0, 7).toString();
            str = str + "," + berClass;
        }
        return str;
    }

    private String generatePRXQ() {
        String str = NemoEvents.PRXQ.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        str = str + "," + system.toString();
        if (system == 1) {
            String rxq = generateInteger(0, 7).toString();
            String cValue = generateFloat(-120, -49).toString();
            String signVar = generateFloat(0, 15).toString();
            String tslResults = generateInteger(0, maxIntegerValue).toString();
            String tslInterf = generateFloat(-28, -1).toString();
            str = str + "," + rxq + "," + cValue + "," + signVar + "," + tslResults + "," + tslInterf;
        }
        return str;
    }

    private String generateFER() {
        String str = NemoEvents.FER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        Integer choice = generateInteger(1, 2);
        if (choice != 1) {
            Integer systemID = generateInteger(2, 5);
            system = systems.get(systemID);
        }
        str = str + "," + system.toString();
        if (system == 1) {
            String ferFull = generateFloat(0, 99).toString();
            String ferSub = generateFloat(0, 99).toString();
            String ferTch = generateFloat(0, 99).toString();
            str = str + "," + ferFull + "," + ferSub + "," + ferTch;
        }
        if (system == 5 || system == 6) {
            String fer = generateFloat(0, 99).toString();
            str = str + "," + fer;
        }
        if (system == 10 || system == 11) {
            String ferDec = generateFloat(0, 99).toString();
            String ferFFCHTarget = generateFloat(0, 99).toString();
            String ferFSCHTarget = generateFloat(0, 99).toString();
            str = str + "," + ferDec + "," + ferFFCHTarget + "," + ferFSCHTarget;
        }
        return str;
    }

    private String generateMSP() {
        String str = NemoEvents.MSP.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            Integer systemID = generateInteger(11, 13);
            system = systems.get(systemID);
        }
        str = str + "," + system.toString();
        if (system == 1 || system == 51 || system == 52 || system == 53) {
            String msp = generateInteger(0, 32).toString();
            str = str + "," + msp;
        }
        return str;
    }

    private String generateRLT() {
        String str = NemoEvents.RLT.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        str = str + "," + system.toString();
        if (system == 1) {
            String rlt = generateInteger(0, 64).toString();
            str = str + "," + rlt;
        }
        return str;
    }

    private String generateTAD() {
        String str = NemoEvents.TAD.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = systems.get(3);
        }
        if (choice == 3) {
            system = systems.get(13);
        }
        str = str + "," + system.toString();
        if (system == 1) {
            String ta = generateInteger(0, 63).toString();
            str = str + "," + ta;
        }
        if (system == 6) {
            String ta = generateFloat(-16, 239).toString();
            str = str + "," + ta;
        }
        if (system == 53) {
            String tal = generateInteger(0, 30).toString();
            str = str + "," + tal;
        }
        return str;
    }

    private String generateDSC() {
        String str = NemoEvents.DSC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        str = str + "," + system.toString();
        if (system == 1) {
            String dscCurrent = generateInteger(0, 45).toString();
            String dscMax = generateInteger(0, 45).toString();
            str = str + "," + dscCurrent + "," + dscMax;
        }
        return str;
    }

    private String generateBEP() {
        String str = NemoEvents.BEP.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        str = str + "," + system.toString();
        if (system == 1) {
            String gmskMeanBEP = generateInteger(0, 31).toString();
            String gmskCvBEP = generateInteger(0, 7).toString();
            String pskMeanBEP = generateInteger(0, 31).toString();
            String pskCvBEP = generateInteger(0, 7).toString();
            str = str + "," + gmskMeanBEP + "," + gmskCvBEP + "," + pskMeanBEP + "," + pskCvBEP;
        }
        return str;
    }

    private String generateCIEvent() {
        String str = NemoEvents.CI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 1;
        Integer choice = generateInteger(1, 3);
        if (choice == 2) {
            system = systems.get(3);
        }
        if (choice == 3) {
            system = systems.get(6);
        }
        str = str + "," + system.toString();
        if (system == 1) {
            String ci = generateFloat(-10, 39).toString();
            Integer tslResults = generateInteger(0, 10);
            str = str + "," + ci + "," + tslResults;
            for (int i = 0; i < tslResults; i++) {
                String timeslotCI = generateFloat(-10, 39).toString();
                str = str + "," + timeslotCI;
            }
            Integer numberOfChs = generateInteger(0, 10);
            String chs = numberOfChs.toString();
            str = str + "," + chs;
            for (int j = 0; j < numberOfChs; j++) {
                Integer numberOfParametersPerChs = 3;
                String parametrsPerChs = numberOfParametersPerChs.toString();
                String arfcn = generateInteger(0, maxIntegerValue).toString();
                String ci2 = generateFloat(-10, 39).toString();
                String rssi = generateFloat(0, 100).toString();
                str = str + "," + parametrsPerChs + "," + arfcn + "," + ci2 + "," + rssi;
            }
        }
        if (system == 6) {
            String ci = generateFloat(-30, 39).toString();
            String headersParams = "0";
            Integer numberOfActSetPNs = generateInteger(0, 10);
            String chs = numberOfActSetPNs.toString();
            str = str + "," + ci + "," + headersParams + "," + chs;
            for (int i = 0; i < numberOfActSetPNs; i++) {
                Integer numberOfParametersPerPilots = 7;
                String parametersPerPilots = numberOfParametersPerPilots.toString();
                String pn = generateInteger(0, 511).toString();
                String sinr = generateFloat(-28, 14).toString();
                String macIndex = generateInteger(0, 255).toString();
                String drcCover = generateInteger(0, 7).toString();
                String rpcCellIndex = generateInteger(0, 15).toString();
                String drcLock = generateInteger(0, 1).toString();
                String rab = generateInteger(0, 1).toString();
                str = str + "," + parametersPerPilots + "," + pn + "," + sinr + "," + macIndex + "," + drcCover + ","
                        + rpcCellIndex + "," + drcLock + "," + rab;
            }
        }
        return str;
    }

    private String generateTXPC() {
        String str = NemoEvents.TXPC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 25;
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            Integer systemID = generateInteger(2, 6);
            system = systems.get(systemID);
        }
        str = str + "," + system.toString();
        if (system == 2) {
            String txPower = generateFloat(-120, 29).toString();
            String pwrCtrlAlg = generateInteger(0, 1).toString();
            String txPowerChange = generateFloat(-30, 29).toString();
            str = str + "," + txPower + "," + pwrCtrlAlg + "," + txPowerChange;
        }
        if (system == 5) {
            String txPower = generateFloat(-120, 29).toString();
            String pwrCtrlAlg = generateInteger(0, 1).toString();
            String pwrCtrlStep = generateInteger(1, 2).toString();
            String comprMode = generateInteger(0, 1).toString();
            String ulPwrUp = generateInteger(0, maxIntegerValue).toString();
            String ulPwrDown = generateInteger(0, maxIntegerValue).toString();
            String ulPwrUpProcent = generateFloat(0, 99).toString();
            str = str + "," + txPower + "," + pwrCtrlAlg + "," + pwrCtrlStep + "," + comprMode + "," + ulPwrUp + "," + ulPwrDown
                    + "," + ulPwrUpProcent;
        }
        if (system == 6) {
            String txPower = generateFloat(-99, 98).toString();
            String pwrCtrlStep = generateInteger(1, 3).toString();
            String ulPwrUp = generateInteger(0, maxIntegerValue).toString();
            String ulPwrDown = generateInteger(0, maxIntegerValue).toString();
            String ulPwrUpProcent = generateFloat(0, 99).toString();
            str = str + "," + txPower + "," + pwrCtrlStep + "," + ulPwrUp + "," + ulPwrDown + "," + ulPwrUpProcent;
        }
        if (system == 11) {
            String txPower = generateFloat(-99, 98).toString();
            String pwrCtrlStep = generateInteger(0, 2).toString();
            String ulPwrUp = generateInteger(0, maxIntegerValue).toString();
            String ulPwrDown = generateInteger(0, maxIntegerValue).toString();
            String ulPwrUpProcent = generateFloat(0, 99).toString();
            String txAdjust = generateFloat(0, 100).toString();
            String txPwrLimit = generateFloat(0, 100).toString();
            String maxPowerLimited = generateInteger(0, 2).toString();
            String r1 = generateFloat(0, 100).toString();
            String r2 = generateFloat(0, 100).toString();
            String r3 = generateFloat(0, 100).toString();
            String r4 = generateFloat(0, 100).toString();
            str = str + "," + txPower + "," + pwrCtrlStep + "," + ulPwrUp + "," + ulPwrDown + "," + ulPwrUpProcent + "," + txAdjust
                    + "," + txPwrLimit + "," + maxPowerLimited + "," + r1 + "," + r2 + "," + r3 + "," + r4;
        }
        if (system == 12) {
            String txPower = generateFloat(-99, 98).toString();
            String ulPwrUp = generateInteger(0, maxIntegerValue).toString();
            String ulPwrHold = generateInteger(0, maxIntegerValue).toString();
            String ulPwrDown = generateInteger(0, maxIntegerValue).toString();
            String ulPwrUpProcent = generateFloat(0, 99).toString();
            String txAdjust = generateFloat(0, 100).toString();
            String txPilot = generateFloat(-99, 98).toString();
            String txOpenLoopPower = generateFloat(-99, 98).toString();
            String drcPilot = generateFloat(0, 100).toString();
            String ackPilot = generateFloat(0, 100).toString();
            String dataPilot = generateFloat(0, 100).toString();
            String paMax = generateFloat(0, 100).toString();
            String drcLockPeriod = generateInteger(8, 8).toString();
            String txThrottle = generateFloat(0, 98).toString();
            String txMaxPowerUsage = generateFloat(0, 99).toString();
            String txMinPowerUsage = generateFloat(0, 99).toString();
            String transmissionMode = generateInteger(0, 1).toString();
            String physicalLayerPacketSize = generateInteger(0, maxIntegerValue).toString();
            String rriPilot = generateFloat(0, 100).toString();
            String dscPilot = generateFloat(0, 100).toString();
            String auxData = generateFloat(0, 100).toString();
            str = str + "," + txPower + "," + ulPwrUp + "," + ulPwrHold + "," + ulPwrDown + "," + ulPwrUpProcent + "," + txAdjust
                    + "," + txPilot + "," + txOpenLoopPower + "," + drcPilot + "," + ackPilot + "," + dataPilot + "," + paMax + ","
                    + drcLockPeriod + "," + txThrottle + "," + txMaxPowerUsage + "," + txMinPowerUsage + "," + transmissionMode
                    + "," + physicalLayerPacketSize + "," + rriPilot + "," + dscPilot + "," + auxData;
        }
        if (system == 25) {
            String txPower = generateFloat(-99, 98).toString();
            String txRefPower = generateFloat(-99, 98).toString();
            String txPowerHeadroom = generateFloat(0, 98).toString();
            String txPowerBSOffset = generateFloat(-99, 98).toString();
            String txPowerIrMax = generateFloat(-99, 98).toString();
            String bsEIRP = generateFloat(-99, 98).toString();
            String bsN = generateFloat(-128, -2).toString();
            str = str + "," + txPower + "," + txRefPower + "," + txPowerHeadroom + "," + txPowerBSOffset + "," + txPowerIrMax + ","
                    + bsEIRP + "," + bsN;
        }
        return str;
    }

    private String generateRXPC() {
        String str = NemoEvents.RXPC.getEventId() + "," + generateTimestamp() + ",";
        Integer systemID = generateInteger(2, 5);
        Integer system = systems.get(systemID);
        str = str + "," + system.toString();
        if (system == 5) {
            String sirTarget = generateFloat(-32, 29).toString();
            String sir = generateFloat(-32, 29).toString();
            String bsDivState = generateInteger(0, 2).toString();
            String dlPwrUp = generateInteger(0, maxIntegerValue).toString();
            String dlPwrDown = generateInteger(0, maxIntegerValue).toString();
            String dlPwrUpProcent = generateFloat(0, 99).toString();
            String dpcMode = generateInteger(0, 1).toString();
            str = str + "," + sirTarget + "," + sir + "," + bsDivState + "," + dlPwrUp + "," + dlPwrDown + "," + dlPwrUpProcent
                    + "," + dpcMode;
        }
        if (system == 6) {
            String headerParams = "0";
            str = str + "," + headerParams;
            String sirTarget = generateFloat(-32, 29).toString();
            String sir = generateFloat(-32, 29).toString();
            String dlPwrUp = generateInteger(0, maxIntegerValue).toString();
            String dlPwrDown = generateInteger(0, maxIntegerValue).toString();
            String dlPwrUpProcent = generateFloat(0, 99).toString();
            Integer numberOfTimeslots = generateInteger(0, maxIntegerValue);
            String timeslots = numberOfTimeslots.toString();
            str = str + "," + sirTarget + "," + sir + "," + dlPwrUp + "," + dlPwrDown + "," + dlPwrUpProcent + "," + timeslots;
            for (int i = 0; i < numberOfTimeslots; i++) {
                Integer numberOfParametersPerTimeslots = 3;
                String parametersPerTimeslots = numberOfParametersPerTimeslots.toString();
                String tsl = generateInteger(0, 6).toString();
                String iscp = generateFloat(-116, -26).toString();
                String rscp = generateFloat(-116, -26).toString();
                str = str + "," + parametersPerTimeslots + "," + tsl + "," + iscp + "," + rscp;
            }
        }
        if (system == 11) {
            String fpcMode = generateInteger(0, 7).toString();
            String fpcSubChannel = generateInteger(0, 1).toString();
            String fpcGain = generateFloat(0, 100).toString();
            String dlPwrUp = generateInteger(0, maxIntegerValue).toString();
            String dlPwrDown = generateInteger(0, maxIntegerValue).toString();
            String dlPwrUpProcent = generateFloat(0, 99).toString();
            String f1 = generateFloat(0, 99).toString();
            String f2 = generateFloat(0, 99).toString();
            String f3 = generateFloat(0, 99).toString();
            String f4 = generateFloat(0, 99).toString();
            String f5 = generateFloat(0, 99).toString();
            String f6 = generateFloat(0, 99).toString();
            String f7 = generateFloat(0, 99).toString();
            String f8 = generateFloat(0, 99).toString();
            String f9 = generateFloat(0, 99).toString();
            String f10 = generateFloat(0, 99).toString();
            String f11 = generateFloat(0, 99).toString();
            String f12 = generateFloat(0, 99).toString();
            str = str + "," + fpcMode + "," + fpcSubChannel + "," + fpcGain + "," + dlPwrUp + "," + dlPwrDown + ","
                    + dlPwrUpProcent + "," + f1 + "," + f2 + "," + f3 + "," + f4 + "," + f5 + "," + f6 + "," + f7 + "," + f8 + ","
                    + f9 + "," + f10 + "," + f11 + "," + f12;
        }
        return str;
    }

    private String generateBER() {
        String str = NemoEvents.BER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = 2;
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = systems.get(2);
        }
        str = str + "," + system.toString();
        if (system == 2) {
            String ber = generateFloat(0, 99).toString();
            str = str + "," + ber;
        }
        if (system == 5) {
            String pilotBer = generateFloat(0, 99).toString();
            String tfciBer = generateFloat(0, 99).toString();
            str = str + "," + pilotBer + "," + tfciBer;
        }
        return str;
    }

    private String generatePHRATE() {
        String str = NemoEvents.PHRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(5, 6);
        str = str + "," + system.toString();
        if (system == 5 || system == 6) {
            String dpdchRateUL = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + dpdchRateUL;
        }
        return str;
    }

    private String generateWLANRATE() {
        String str = NemoEvents.WLANRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GAN_WLAN.getId();
        str = str + "," + system.toString();
        String wlanRateUl = generateInteger(0, maxIntegerValue).toString();
        String wlanRateDl = generateInteger(0, maxIntegerValue).toString();
        str = str + "," + wlanRateUl + "," + wlanRateDl;
        return str;
    }

    private String generatePPPRATE() {
        String str = NemoEvents.PPPRATE.getEventId() + "," + generateTimestamp() + ",";
        String pppRateUl = generateInteger(0, maxIntegerValue).toString();
        String pppRateDl = generateInteger(0, maxIntegerValue).toString();
        String sentPppBytes = generateInteger(0, maxIntegerValue).toString();
        String recvPppBytes = generateInteger(0, maxIntegerValue).toString();
        str = str + "," + pppRateUl + "," + pppRateDl + "," + sentPppBytes + "," + recvPppBytes;
        return str;
    }

    private String generateRLPRATE() {
        String str = NemoEvents.RLPRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 12);
        str = str + "," + system.toString();
        String rlpRevRate = generateInteger(0, maxIntegerValue).toString();
        String rlpForRate = generateInteger(0, maxIntegerValue).toString();
        String rlpRevRetrRate = generateInteger(0, maxIntegerValue).toString();
        String rlpFwdRetrRate = generateInteger(0, maxIntegerValue).toString();
        str = str + "," + rlpRevRate + "," + rlpForRate + "," + rlpRevRetrRate + "," + rlpFwdRetrRate;
        return str;
    }

    private String generateRLPSTATISTICS() {
        String str = NemoEvents.RLPSTATISTICS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 12);
        str = str + "," + system.toString();
        if (system == 10 || system == 11) {
            String serviceID = generateInteger(0, maxIntegerValue).toString();
            String resets = generateInteger(0, maxIntegerValue).toString();
            String aborts = generateInteger(0, maxIntegerValue).toString();
            String lastRTT = generateInteger(0, maxIntegerValue).toString();
            String blockOfBytesUsed = generateInteger(0, 3).toString();
            String rxNaks = generateInteger(0, maxIntegerValue).toString();
            String largestConErasures = generateInteger(0, maxIntegerValue).toString();
            String retransNotFound = generateInteger(0, maxIntegerValue).toString();
            String rxRetransFrames = generateInteger(0, maxIntegerValue).toString();
            String rxIdleFrames = generateInteger(0, maxIntegerValue).toString();
            String rxFillFrames = generateInteger(0, maxIntegerValue).toString();
            String rxBlankFrames = generateInteger(0, maxIntegerValue).toString();
            String rxNullFrames = generateInteger(0, maxIntegerValue).toString();
            String rxNewFrames = generateInteger(0, maxIntegerValue).toString();
            String rxFundFrames = generateInteger(0, maxIntegerValue).toString();
            String rxBytes = generateInteger(0, maxIntegerValue).toString();
            String rxRLPErasures = generateInteger(0, maxIntegerValue).toString();
            String rxMUXErasures = generateInteger(0, maxIntegerValue).toString();
            String txNAKs = generateInteger(0, maxIntegerValue).toString();
            String txRetransFrames = generateInteger(0, maxIntegerValue).toString();
            String txIdleFrames = generateInteger(0, maxIntegerValue).toString();
            String txNewFrames = generateInteger(0, maxIntegerValue).toString();
            String txFundFrames = generateInteger(0, maxIntegerValue).toString();
            String txBytes = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + serviceID + "," + resets + "," + aborts + "," + lastRTT + "," + blockOfBytesUsed + "," + rxNaks + ","
                    + largestConErasures + "," + retransNotFound + "," + rxRetransFrames + "," + rxIdleFrames + "," + rxFillFrames
                    + "," + rxBlankFrames + "," + rxNullFrames + "," + rxNewFrames + "," + rxFundFrames + "," + rxBytes + ","
                    + rxRLPErasures + "," + rxMUXErasures + "," + txNAKs + "," + txRetransFrames + "," + txIdleFrames + ","
                    + txNewFrames + "," + txFundFrames + "," + txBytes;
        }
        if (system == 12) {
            String serviceID = generateInteger(0, maxIntegerValue).toString();
            String rxNaks = generateInteger(0, maxIntegerValue).toString();
            String rxNaksInBytes = generateInteger(0, maxIntegerValue).toString();
            String retransNotFound = generateInteger(0, maxIntegerValue).toString();
            String rxDupBytes = generateInteger(0, maxIntegerValue).toString();
            String rxRetransBytes = generateInteger(0, maxIntegerValue).toString();
            String rxNewBytes = generateInteger(0, maxIntegerValue).toString();
            String rxBytes = generateInteger(0, maxIntegerValue).toString();
            String rxNaks2 = generateInteger(0, maxIntegerValue).toString();
            String txNaksInBytes = generateInteger(0, maxIntegerValue).toString();
            String txRetransBytes = generateInteger(0, maxIntegerValue).toString();
            String txNewBytes = generateInteger(0, maxIntegerValue).toString();
            String txBytes = generateInteger(0, maxIntegerValue).toString();
            String nakTimeouts = generateInteger(0, maxIntegerValue).toString();
            String resetCount = generateInteger(0, maxIntegerValue).toString();
            String atResetRequestCount = generateInteger(0, maxIntegerValue).toString();
            String atResetAckCount = generateInteger(0, maxIntegerValue).toString();
            String anResetRequestCount = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + serviceID + "," + rxNaks + "," + rxNaksInBytes + "," + retransNotFound + "," + rxDupBytes + ","
                    + rxRetransBytes + "," + rxNewBytes + "," + rxBytes + "," + rxNaks2 + "," + txNaksInBytes + ","
                    + txRetransBytes + "," + txNewBytes + "," + txBytes + "," + nakTimeouts + "," + resetCount + ","
                    + atResetRequestCount + "," + atResetAckCount + "," + anResetRequestCount;
        }
        return str;
    }

    private String generateMEI() {
        String str = NemoEvents.MEI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(5, 6);
        str = str + "," + system.toString();
        if (system == 5 || system == 6) {
            // 21,22....
            String measurementEvent = generateInteger(1, 10).toString();
            str = str + "," + measurementEvent;
        }
        return str;
    }

    private String generateCQI() {
        String str = NemoEvents.CQI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String params = "4";
            String sampleDur = generateInteger(1, maxIntegerValue).toString();
            String phReqRate = generateInteger(1, maxIntegerValue).toString();
            String cqiRepetitions = generateInteger(1, 4).toString();
            String cqiCucle = generateInteger(0, 160).toString();
            Integer numberOfValues = generateInteger(1, 10);
            String cqiValues = numberOfValues.toString();
            Integer numberOfParamsPerCqi = 2;
            String paramsPerCqi = numberOfParamsPerCqi.toString();
            str = str + "," + params + "," + sampleDur + "," + phReqRate + "," + cqiRepetitions + "," + cqiCucle + "," + cqiValues
                    + "," + paramsPerCqi;
            for (int i = 0; i < numberOfValues; i++) {
                String percentage = generateFloat(0, 99).toString();
                String cqi = generateInteger(0, 30).toString();
                str = str + "," + percentage + "," + cqi;
            }
        }
        return str;
    }

    private String generateHARQI() {
        String str = NemoEvents.HARQI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String headerParams = "0";
            Integer numberOfHarqProcesses = generateInteger(0, 10);
            String harqProcesses = numberOfHarqProcesses.toString();
            Integer numberOfParamsPerHarqProcesses = 5;
            String paramsPerHarqProcesses = numberOfParamsPerHarqProcesses.toString();
            str = str + "," + headerParams + "," + harqProcesses + "," + paramsPerHarqProcesses;
            for (int i = 0; i < numberOfHarqProcesses; i++) {
                String harqID = generateInteger(0, 7).toString();
                String harqDir = generateInteger(1, 2).toString();
                String harqRate = generateInteger(0, maxIntegerValue).toString();
                String harqPackets = generateInteger(0, maxIntegerValue).toString();
                String harqBler = generateFloat(0, 99).toString();
                str = str + "," + harqID + "," + harqDir + "," + harqRate + "," + harqPackets + "," + harqBler;
            }
        }
        return str;
    }

    private String generateHSSCCHI() {
        String str = NemoEvents.HSSCCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String headerParams = "0";
            Integer numberOfChs = generateInteger(1, 10);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = 2;
            String paramsPerHarqProcesses = numberOfParamsPerChs.toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerHarqProcesses;
            for (int i = 0; i < numberOfChs; i++) {
                String hsscchCode = generateInteger(0, 127).toString();
                String hsdpaHSSCCHUsage = generateFloat(0, 99).toString();
                str = str + "," + hsscchCode + "," + hsdpaHSSCCHUsage;
            }
        }
        return str;
    }

    private String generatePLAID() {
        String str = NemoEvents.PLAID.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.WIMAX.getId();
        }
        str = str + "," + system.toString();
        if (system == 5) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            String sampleDuration = generateInteger(1, maxIntegerValue).toString();
            String hsPDSCHRate = generateInteger(1, maxIntegerValue).toString();
            Integer numberOfPLASets = generateInteger(1, maxIntegerValue);
            String plaSets = numberOfPLASets.toString();
            Integer numberOfParamsPerPLASets = numberOfHeaderParams / numberOfPLASets;
            String paramsPerPLASets = numberOfParamsPerPLASets.toString();
            String percentage = generateFloat(0, 99).toString();
            String modulation = generateInteger(1, 2).toString();
            String effectiveCoding = generateFloat(0, 0).toString();
            String tbSize = generateInteger(1, maxIntegerValue).toString();
            String stChCode = generateInteger(0, 15).toString();
            String codes = generateInteger(1, 15).toString();
            String retr = generateFloat(0, 99).toString();
            str = str + "," + headerParams + "," + sampleDuration + "," + hsPDSCHRate + "," + plaSets + "," + paramsPerPLASets
                    + "," + percentage + "," + modulation + "," + effectiveCoding + "," + tbSize + "," + stChCode + "," + codes
                    + "," + retr;
        }
        if (system == 25) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            String sampleDuration = generateInteger(1, maxIntegerValue).toString();
            String burstCount = generateInteger(1, maxIntegerValue).toString();
            Integer numberOfPLASets = generateInteger(1, maxIntegerValue);
            String plaSets = numberOfPLASets.toString();
            Integer numberOfParamsPerPLASets = numberOfHeaderParams / numberOfPLASets;
            String paramsPerPLASets = numberOfParamsPerPLASets.toString();
            String percentage = generateFloat(0, 99).toString();
            String modulation = generateInteger(1, 2).toString();
            String codingRate = generateInteger(1, 7).toString();
            String codingType = generateInteger(1, 4).toString();
            // 4,6
            String repetitionCoding = generateInteger(1, 2).toString();
            str = str + "," + headerParams + "," + sampleDuration + "," + burstCount + "," + plaSets + "," + paramsPerPLASets + ","
                    + percentage + "," + modulation + "," + codingRate + "," + codingType + "," + repetitionCoding;
        }
        return str;
    }

    private String generatePLAIU() {
        String str = NemoEvents.PLAIU.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.WIMAX.getId();
        }
        str = str + "," + system.toString();
        if (system == 5) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            String sampleDuration = generateInteger(1, maxIntegerValue).toString();
            String eDPDCHRate = generateInteger(1, maxIntegerValue).toString();
            String limMaxPower = generateFloat(0, 99).toString();
            String limGrant = generateFloat(0, 99).toString();
            String limLackOfData = generateFloat(0, 99).toString();
            String limByMux = generateFloat(0, 99).toString();
            String limByHARQ = generateFloat(0, 99).toString();
            Integer numberOfPLASets = generateInteger(1, maxIntegerValue);
            String plaSets = numberOfPLASets.toString();
            Integer numberOfParamsPerPLASets = numberOfHeaderParams / numberOfPLASets;
            String paramsPerPLASets = numberOfParamsPerPLASets.toString();
            String percentage = generateFloat(0, 99).toString();
            String modulation = generateInteger(1, 2).toString();
            String tbSize = generateInteger(1, maxIntegerValue).toString();
            String eTFCI = generateInteger(0, 127).toString();
            String sfs = generateInteger(1, 10).toString();
            String retr = generateFloat(0, 99).toString();
            String avgSGIndex = generateInteger(-1, 37).toString();
            String avgSG = generateFloat(-10, 29).toString();
            str = str + "," + headerParams + "," + sampleDuration + "," + eDPDCHRate + "," + limMaxPower + "," + limGrant + ","
                    + limLackOfData + "," + limByMux + "," + limByHARQ + "," + plaSets + "," + paramsPerPLASets + "," + percentage
                    + "," + modulation + "," + tbSize + "," + eTFCI + "," + sfs + "," + retr + "," + avgSGIndex + "," + avgSG;
        }
        if (system == 25) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            String sampleDuration = generateInteger(1, maxIntegerValue).toString();
            String burstCount = generateInteger(1, maxIntegerValue).toString();
            Integer numberOfPLASets = generateInteger(1, maxIntegerValue);
            String plaSets = numberOfPLASets.toString();
            Integer numberOfParamsPerPLASets = numberOfHeaderParams / numberOfPLASets;
            String paramsPerPLASets = numberOfParamsPerPLASets.toString();
            String percentage = generateFloat(0, 99).toString();
            String modulation = generateInteger(1, 3).toString();
            String codingRate = generateInteger(1, 7).toString();
            String codingType = generateInteger(1, 4).toString();
            // 4,6
            String repetitionCoding = generateInteger(1, 2).toString();
            str = str + "," + headerParams + "," + sampleDuration + "," + burstCount + "," + plaSets + "," + paramsPerPLASets + ","
                    + percentage + "," + modulation + "," + codingRate + "," + codingType + "," + repetitionCoding;
        }
        return str;
    }

    private String generateHBI() {
        String str = NemoEvents.HBI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String reportingInterval = generateInteger(1, maxIntegerValue).toString();
            String happyBit = generateFloat(0, 99).toString();
            String dtx = generateFloat(0, 99).toString();
            str = str + "," + reportingInterval + "," + happyBit + "," + dtx;
        }
        return str;
    }

    private String generateMACERATE() {
        String str = NemoEvents.MACERATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String macEBitrate = generateInteger(1, maxIntegerValue).toString();
            String macEBlockrate = generateInteger(1, maxIntegerValue).toString();
            String macEFirstRetr = generateFloat(0, 99).toString();
            String macESecondRetr = generateFloat(0, 99).toString();
            String macEThirdRetr = generateFloat(0, 99).toString();
            str = str + "," + macEBitrate + "," + macEBlockrate + "," + macEFirstRetr + "," + macESecondRetr + "," + macEThirdRetr;
        }
        return str;
    }

    private String generateAGRANT() {
        String str = NemoEvents.AGRANT.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String agchIndex = generateInteger(0, 31).toString();
            String agchGrant = generateFloat(-10, 29).toString();
            String agchScope = generateInteger(-1, 7).toString();
            String agchSelector = generateInteger(1, 2).toString();
            String eRNTISelector = generateInteger(1, 2).toString();
            str = str + "," + agchIndex + "," + agchGrant + "," + agchScope + "," + agchSelector + "," + eRNTISelector;
        }
        return str;
    }

    private String generateSGRANT() {
        String str = NemoEvents.SGRANT.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String headerParams = "0";
            String sampleDur = generateInteger(1, maxIntegerValue).toString();
            String grantedRate = generateInteger(1, maxIntegerValue).toString();
            Integer numberOfSGSets = generateInteger(1, 10);
            String sgSets = numberOfSGSets.toString();
            Integer numberOfParamsPerSGSets = 3;
            String paramsPerSgSets = numberOfParamsPerSGSets.toString();
            str = str + "," + headerParams + "," + sampleDur + "," + grantedRate + "," + sgSets + "," + paramsPerSgSets;
            for (int i = 0; i < numberOfParamsPerSGSets; i++) {
                String distribution = generateFloat(0, 99).toString();
                String sgIndex = generateInteger(-1, 37).toString();
                String servingGrant = generateFloat(-10, 29).toString();
                str = str + "," + distribution + "," + sgIndex + "," + servingGrant;
            }
        }
        return str;
    }

    private String generateEDCHI() {
        String str = NemoEvents.EDCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String headerParams = "0";
            String nsACKs = generateFloat(0, 99).toString();
            String nsGrantDown = generateFloat(0, 99).toString();
            Integer numberOfCells = generateInteger(1, 10);
            String cells = numberOfCells.toString();
            Integer numberOfParamsPerCells = 9;
            String paramsPerCells = numberOfParamsPerCells.toString();
            str = str + "," + headerParams + "," + nsACKs + "," + nsGrantDown + "," + cells + "," + paramsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                String hsupaChannel = generateInteger(1, maxIntegerValue).toString();
                String hsupaSc = generateInteger(0, 511).toString();
                String hsupaRls = generateInteger(0, 5).toString();
                String ack = generateFloat(0, 99).toString();
                String nack = generateFloat(0, 99).toString();
                String dtx = generateFloat(0, 99).toString();
                String grantUp = generateFloat(0, 99).toString();
                String grantHold = generateFloat(0, 99).toString();
                String grantDown = generateFloat(0, 99).toString();
                str = str + "," + hsupaChannel + "," + hsupaSc + "," + hsupaRls + "," + ack + "," + nack + "," + dtx + ","
                        + grantUp + "," + grantHold + "," + grantDown;
            }
        }
        return str;
    }

    private String generateHSUPASI() {
        String str = NemoEvents.HSUPASI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            String dur = generateInteger(1, maxIntegerValue).toString();
            String siCount = generateInteger(1, maxIntegerValue).toString();
            String hlid = generateInteger(0, 15).toString();
            String hlbs = generateInteger(0, 15).toString();
            String tebs = generateInteger(0, 31).toString();
            String tebsMin = generateInteger(0, 31).toString();
            String tebsMax = generateInteger(0, 31).toString();
            String uph = generateInteger(0, 31).toString();
            String uphMin = generateInteger(0, 31).toString();
            String uphMax = generateInteger(0, 31).toString();
            str = str + "," + dur + "," + siCount + "," + hlid + "," + hlbs + "," + tebs + "," + tebsMin + "," + tebsMax + ","
                    + uph + "," + uphMin + "," + uphMax;
        }
        return str;
    }

    private String generateDRCI() {
        String str = NemoEvents.DRCI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system.toString();
        String headerParams = "0";
        String sampleDuration = generateInteger(1, maxIntegerValue).toString();
        Integer numberOfDRCSets = generateInteger(1, 10);
        String drcSets = numberOfDRCSets.toString();
        Integer numberOfParamsPerDRCSets = 3;
        String paramsPerDRCSets = numberOfParamsPerDRCSets.toString();
        str = str + "," + headerParams + "," + sampleDuration + "," + drcSets + "," + paramsPerDRCSets;
        for (int i = 0; i < numberOfDRCSets; i++) {
            String percentage = generateFloat(0, 99).toString();
            String requestedRate = generateInteger(1, maxIntegerValue).toString();
            String packetLength = generateInteger(0, 1).toString();
            str = str + "," + percentage + "," + requestedRate + "," + packetLength;
        }
        return str;
    }

    private String generateRDRC() {
        String str = NemoEvents.RDRC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system.toString();
        if (system == 12) {
            String txRateLimit = generateInteger(0, 153600).toString();
            String txCurrentRate = generateInteger(0, 153600).toString();
            String combRAB = generateInteger(0, 1).toString();
            String paMax = generateInteger(0, 153600).toString();
            String randomVariable = generateInteger(0, 255).toString();
            String transitionProbability = generateInteger(0, maxIntegerValue).toString();
            String conditionRRI = generateInteger(0, 153600).toString();
            String actualRRI = generateInteger(0, 153600).toString();
            String paddingBytes = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + txRateLimit + "," + txCurrentRate + "," + combRAB + "," + paMax + "," + randomVariable + ","
                    + transitionProbability + "," + conditionRRI + "," + actualRRI + "," + paddingBytes;
        }
        return str;
    }

    private String generateFDRC() {
        String str = NemoEvents.FDRC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system.toString();
        if (system == 12) {
            String drcIndex = generateInteger(0, maxIntegerValue).toString();
            String drcCover = generateInteger(0, 7).toString();
            String dscValue = generateInteger(0, 7).toString();
            String drcBoost = generateInteger(0, 1).toString();
            String drcLockUpdSlot = generateInteger(0, maxIntegerValue).toString();
            String ackChannelStatus = generateInteger(0, 1).toString();
            String forcedACKNAKRatio = generateFloat(0, 99).toString();
            String ackRatio = generateFloat(0, 99).toString();
            String multiuserACKRatio = generateFloat(0, 99).toString();
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

    private String generateMARKOVMUX() {
        String str = NemoEvents.MARKOVMUX.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 11);
        str = str + "," + system.toString();
        if (system == 10 || system == 11) {
            String headerParams = "0";
            Integer numberOfFrames = generateInteger(1, 10);
            String frames = numberOfFrames.toString();
            Integer numberOfParamsPerFrames = 2;
            String paramsPerFrames = numberOfParamsPerFrames.toString();
            str = str + "," + headerParams + "," + frames + "," + paramsPerFrames;
            for (int i = 0; i < numberOfFrames; i++) {
                String mExpectetedMux = generateInteger(0, 9).toString();
                String mActualMux = generateInteger(0, 38).toString();
                str = str + "," + mExpectetedMux + "," + mActualMux;
            }
        }
        return str;
    }

    private String generateMARKOVSTATS() {
        String str = NemoEvents.MARKOVSTATS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateInteger(10, 11);
        str = str + "," + system.toString();
        String headerParams = "0";
        String mFer = generateFloat(0, 99).toString();
        Integer numberOfExpectedValues = generateInteger(1, 10);
        String expectedValues = numberOfExpectedValues.toString();
        Integer numberOfParams = 6;
        String params = numberOfParams.toString();
        str = str + "," + headerParams + "," + mFer + "," + expectedValues + "," + params;
        for (int i = 0; i < numberOfExpectedValues; i++) {
            String mExpected = generateInteger(1, 4).toString();
            String m11 = generateInteger(1, 4).toString();
            String m12 = generateInteger(1, 4).toString();
            String m14 = generateInteger(1, 4).toString();
            String m18 = generateInteger(1, 4).toString();
            String mErasures = generateInteger(1, 4).toString();
            str = str + "," + mExpected + "," + m11 + "," + m12 + "," + m14 + "," + m18 + "," + mErasures;
        }
        return str;
    }

    private String generateMER() {
        String str = NemoEvents.MER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.TETRA.getId();
        str = str + "," + system.toString();
        if (system == 2) {
            String mer = generateFloat(0, 99).toString();
            str = str + "," + mer;
        }
        return str;
    }

    private String generateDVBI() {
        String str = NemoEvents.DVBI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system.toString();
        if (system == 65) {
            String serviceState = generateInteger(1, 2).toString();
            String frequency = generateFloat(0, 100).toString();
            String bandwidth = generateFloat(0, 100).toString();
            String cellID = generateInteger(1, maxIntegerValue).toString();
            // 4,8...
            String txMode = generateInteger(2, 2).toString();
            String modulation = generateInteger(1, 3).toString();
            String codeRateLP = generateInteger(1, 5).toString();
            String codeRateHP = generateInteger(0, 5).toString();
            String guardTime = generateInteger(1, 4).toString();
            String mpeFECCodeRateLP = generateInteger(0, 5).toString();
            String mpeFECCodeRateHP = generateInteger(0, 5).toString();
            String hierarchy = generateInteger(0, 1).toString();
            str = str + "," + serviceState + "," + frequency + "," + bandwidth + "," + cellID + "," + txMode + "," + modulation
                    + "," + codeRateLP + "," + codeRateHP + "," + guardTime + "," + mpeFECCodeRateLP + "," + mpeFECCodeRateHP + ","
                    + hierarchy;
        }
        return str;
    }

    private String generateDVBFER() {
        String str = NemoEvents.DVBFER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system.toString();
        if (system == 65) {
            String fer = generateFloat(0, 99).toString();
            String mfer = generateFloat(0, 99).toString();
            String frameCount = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + fer + "," + mfer + "," + frameCount;
        }
        return str;
    }

    private String generateDVBBER() {
        String str = NemoEvents.DVBBER.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system.toString();
        if (system == 65) {
            String ber = generateFloat(0, 99).toString();
            String vber = generateFloat(0, 99).toString();
            str = str + "," + ber + "," + vber;
        }
        return str;
    }

    private String generateDVBRXL() {
        String str = NemoEvents.DVBRXL.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system.toString();
        if (system == 65) {
            String headerParams = "0";
            Integer numberOfChs = generateInteger(1, 10);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = 4;
            String paramsPerChs = numberOfParamsPerChs.toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs;
            for (int i = 0; i < numberOfChs; i++) {
                String frequency = generateFloat(0, 100).toString();
                String rxLev = generateFloat(-111, -11).toString();
                String cn = generateFloat(0, 39).toString();
                String signalQuality = generateFloat(0, 99).toString();
                str = str + "," + frequency + "," + rxLev + "," + cn + "," + signalQuality;
            }
        }
        return str;
    }

    private String generateDVBRATE() {
        String str = NemoEvents.DVBRATE.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.DVB_H.getId();
        str = str + "," + system.toString();
        if (system == 65) {
            String dvbHRate = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + dvbHRate;
        }
        return str;
    }

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
        str = str + "," + system.toString();
        if (system == 1) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfChs = generateInteger(1, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = numberOfHeaderParams / numberOfChs;
            String paramsPerChs = numberOfParamsPerChs.toString();
            String arfcn = generateInteger(1, maxIntegerValue).toString();
            String bsic = generateInteger(0, 63).toString();
            String rxLevel = generateFloat(-120, -11).toString();
            String ci = generateFloat(-10, 39).toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs + "," + arfcn + "," + bsic + "," + rxLevel + "," + ci;
        }
        if (system == 5 || system == 6) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfChs = generateInteger(1, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = numberOfHeaderParams / numberOfChs;
            String paramsPerChs = numberOfParamsPerChs.toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String rssi = generateFloat(-120, -11).toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs + "," + ch + "," + rssi;
        }
        if (system == 10 || system == 11) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfChs = generateInteger(1, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = numberOfHeaderParams / numberOfChs;
            String paramsPerChs = numberOfParamsPerChs.toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String rssi = generateFloat(-120, -1).toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs + "," + ch + "," + rssi;
        }
        if (system == 12) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfChs = generateInteger(1, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = numberOfHeaderParams / numberOfChs;
            String paramsPerChs = numberOfParamsPerChs.toString();
            String carrier = generateInteger(1, maxIntegerValue).toString();
            String rssi = generateFloat(-120, -1).toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs + "," + carrier + "," + rssi;
        }
        if (system == 25) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfChs = generateInteger(1, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = numberOfHeaderParams / numberOfChs;
            String paramsPerChs = numberOfParamsPerChs.toString();
            String frequency = generateInteger(1, maxIntegerValue).toString();
            String rssi = generateFloat(-120, -1).toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs + "," + frequency + "," + rssi;
        }
        if (system == 51 || system == 52) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfChs = generateInteger(1, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = numberOfHeaderParams / numberOfChs;
            String paramsPerChs = numberOfParamsPerChs.toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String sat = generateInteger(0, 6).toString();
            String rxLevel = generateFloat(-120, -11).toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs + "," + ch + "," + sat + "," + rxLevel;
        }
        if (system == 53) {
            Integer numberOfHeaderParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfChs = generateInteger(1, maxIntegerValue);
            String chs = numberOfChs.toString();
            Integer numberOfParamsPerChs = numberOfHeaderParams / numberOfChs;
            String paramsPerChs = numberOfParamsPerChs.toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String dcc = generateInteger(0, 255).toString();
            String rxLevel = generateFloat(-120, -11).toString();
            str = str + "," + headerParams + "," + chs + "," + paramsPerChs + "," + ch + "," + dcc + "," + rxLevel;
        }
        return str;
    }

    private String generateSPECTRUMSCAN() {
        String str = NemoEvents.SPECTRUMSCAN.getEventId() + "," + generateTimestamp() + ",";
        String scanningMode = generateInteger(1, 1).toString();
        String headerParams = "0";
        String bandwidth = generateFloat(0, 100).toString();
        String sweepBandwidth = generateFloat(0, 100).toString();
        String sweepFrequency = generateFloat(0, 100).toString();
        String sweepTotalRxLevel = generateFloat(-120, -11).toString();
        Integer numberOfFrequencies = generateInteger(1, maxIntegerValue);
        String frequencies = numberOfFrequencies.toString();
        Integer numberParamsPerFrequencies = 2;
        String paramsPerFrequencies = numberParamsPerFrequencies.toString();
        str = str + "," + scanningMode + "," + headerParams + "," + bandwidth + "," + sweepBandwidth + "," + sweepFrequency + ","
                + sweepTotalRxLevel + "," + frequencies + "," + paramsPerFrequencies;
        for (int i = 0; i < numberOfFrequencies; i++) {
            String frequency = generateFloat(0, 100).toString();
            String rxLevel = generateFloat(-120, -11).toString();
            str = str + "," + frequency + "," + rxLevel;
        }
        return str;
    }

    private String generatePILOTSCAN() {
        String str = NemoEvents.PILOTSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer systemID = generateInteger(2, 6);
        Integer system = systems.get(systemID);
        str = str + "," + system.toString();
        if (system == 5) {
            Integer numberOfHeadersParams = 0;
            String headerParams = numberOfHeadersParams.toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String chType = generateInteger(1, 5).toString();
            String rssi = generateFloat(-120, -11).toString();
            Integer numberOfCells = generateInteger(1, 10);
            String cells = numberOfCells.toString();
            Integer numberParamsPerCells = 6;
            String paramsPerCells = numberParamsPerCells.toString();
            str = str + "," + headerParams + "," + ch + "," + chType + "," + rssi + "," + cells + "," + paramsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                String scr = generateInteger(0, 511).toString();
                String ecn0 = generateFloat(-26, -1).toString();
                String rscp = generateFloat(-150, -21).toString();
                String sir = generateFloat(0, 29).toString();
                String delay = generateFloat(0, 38399).toString();
                String delaySpread = generateFloat(0, 100).toString();
                str = str + "," + scr + "," + ecn0 + "," + rscp + "," + sir + "," + delay + "," + delaySpread;
            }
        }
        if (system == 6) {
            Integer numberOfHeadersParams = 0;
            String headerParams = numberOfHeadersParams.toString();
            String channelType = generateInteger(1, 2).toString();
            Integer numberOfCells = generateInteger(1, 10);
            String cells = numberOfCells.toString();
            Integer numberParamsPerCells = 7;
            String paramsPerCells = numberParamsPerCells.toString();
            str = str + "," + headerParams + "," + channelType + "," + cells + "," + paramsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                String ch = generateInteger(1, maxIntegerValue).toString();
                String cellParamsID = generateInteger(0, 127).toString();
                String eci0 = generateFloat(-30, -1).toString();
                String timeOffset = generateFloat(0, 6499).toString();
                String sir = generateFloat(-30, 24).toString();
                String rscp = generateFloat(-116, -21).toString();
                String rssi = generateFloat(-120, -11).toString();
                str = str + "," + ch + "," + cellParamsID + "," + eci0 + "," + timeOffset + "," + sir + "," + rscp + "," + rssi;
            }
        }
        if (system == 10 || system == 11 || system == 12) {
            Integer numberOfHeadersParams = 0;
            String headerParams = numberOfHeadersParams.toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String rssi = generateFloat(-120, -11).toString();
            Integer numberOfCells = generateInteger(1, 10);
            String cells = numberOfCells.toString();
            Integer numberParamsPerCells = 3;
            String paramsPerCells = numberParamsPerCells.toString();
            str = str + "," + headerParams + "," + ch + "," + rssi + "," + cells + "," + paramsPerCells;
            for (int i = 0; i < numberOfCells; i++) {
                String pn = generateInteger(1, maxIntegerValue).toString();
                String eci0 = generateFloat(-35, 2).toString();
                String delay = generateFloat(0, 38399).toString();
                str = str + "," + pn + "," + eci0 + "," + delay;
            }
        }
        return str;
    }

    private String generateOFDMSCAN() {
        String str = NemoEvents.OFDMSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.WIMAX.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.DVB_H.getId();
        }
        str = str + "," + system.toString();
        if (system == 25) {
            Integer numberOfHeadersParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeadersParams.toString();
            Integer numberOfFrequencies = generateInteger(1, maxIntegerValue);
            String frequencies = numberOfFrequencies.toString();
            Integer numberParamsPerFrequencies = numberOfHeadersParams / numberOfFrequencies;
            String paramsPerFrequencies = numberParamsPerFrequencies.toString();
            String frequency = generateFloat(0, 100).toString();
            String rssi = generateFloat(-120, -11).toString();
            Integer numberOfPreambles = generateInteger(1, maxIntegerValue);
            String preambles = numberOfPreambles.toString();
            Integer numberParamsPerPreambles = numberOfHeadersParams / numberOfPreambles;
            String paramsPerPreambles = numberParamsPerPreambles.toString();
            String frequency2 = generateFloat(0, 100).toString();
            String preambleIndex = generateInteger(0, 113).toString();
            String preambleRSSI = generateFloat(-120, -1).toString();
            String cinr = generateFloat(-32, 39).toString();
            String delay = generateFloat(0, 1054).toString();
            str = str + "," + headerParams + "," + frequencies + "," + paramsPerFrequencies + "," + frequency + "," + rssi + ","
                    + preambles + "," + paramsPerPreambles + "," + frequency2 + "," + preambleIndex + "," + preambleRSSI + ","
                    + cinr + "," + delay;
        }
        if (system == 65) {
            Integer numberOfHeadersParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeadersParams.toString();
            Integer numberOfFrequencies = generateInteger(1, maxIntegerValue);
            String frequencies = numberOfFrequencies.toString();
            Integer numberParamsPerFrequencies = numberOfHeadersParams / numberOfFrequencies;
            String paramsPerFrequencies = numberParamsPerFrequencies.toString();
            String frequency = generateFloat(0, 100).toString();
            String rssi = generateFloat(0, 100).toString();
            String mer = generateFloat(0, 59).toString();
            str = str + "," + headerParams + "," + frequencies + "," + paramsPerFrequencies + "," + frequency + "," + rssi + ","
                    + mer;
        }
        return str;
    }

    private String generateTPROFSCAN() {
        String str = NemoEvents.TPROFSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        if (system == 5) {
            Integer numberOfHeadersParams = 0;
            String headerParams = numberOfHeadersParams.toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String rssi = generateFloat(-120, -1).toString();
            String chType = generateInteger(1, 5).toString();
            Integer numberOfSamples = generateInteger(1, 10);
            Integer numberParamsPerSamples = numberOfHeadersParams / numberOfSamples;
            String paramsPerSamples = numberParamsPerSamples.toString();
            String samples = numberOfSamples.toString();
            String chip = generateInteger(0, maxIntegerValue).toString();
            String ecn0 = generateFloat(-26, -1).toString();
            str = str + "," + headerParams + "," + ch + "," + rssi + "," + chType + "," + rssi + "," + paramsPerSamples + ","
                    + samples + "," + chip + "," + ecn0;
        }
        return str;
    }

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
        str = str + "," + system.toString();
        if (system == 5) {
            Integer numberOfHeadersParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeadersParams.toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String scr = generateInteger(0, 511).toString();
            String chType = generateInteger(1, 5).toString();
            Integer numberOfSamples = generateInteger(1, maxIntegerValue);
            Integer numberParamsPerSamples = numberOfHeadersParams / numberOfSamples;
            String paramsPerSamples = numberParamsPerSamples.toString();
            String samples = numberOfSamples.toString();
            String sampleOffset = generateFloat(-550, 549).toString();
            String sample = generateFloat(0, 100).toString();
            str = str + "," + headerParams + "," + ch + "," + scr + "," + chType + "," + chType + "," + paramsPerSamples + ","
                    + samples + "," + sampleOffset + "," + sample;
        }
        if (system == 10 || system == 11 || system == 12) {
            Integer numberOfHeadersParams = generateInteger(1, maxIntegerValue);
            String headerParams = numberOfHeadersParams.toString();
            Integer numberOfSamples = generateInteger(1, maxIntegerValue);
            Integer numberParamsPerSamples = numberOfHeadersParams / numberOfSamples;
            String paramsPerSamples = numberParamsPerSamples.toString();
            String samples = numberOfSamples.toString();
            String sampleOffset = generateInteger(0, 32768).toString();
            String sampleEnergy = generateFloat(-35, 2).toString();
            str = str + "," + headerParams + "," + paramsPerSamples + "," + samples + "," + sampleOffset + "," + sampleEnergy;
        }
        return str;
    }

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
        str = str + "," + system.toString();
        if (system == 5) {
            Integer numberOfHeaderParams = generateInteger(0, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            Integer numberOfFingers = generateInteger(0, maxIntegerValue);
            String fingers = numberOfFingers.toString();
            Integer numberHeaderParamsPerFingers = numberOfHeaderParams / numberOfFingers;
            String paramsPerFingers = numberHeaderParamsPerFingers.toString();
            String ch = generateInteger(0, maxIntegerValue).toString();
            String scr = generateInteger(0, 511).toString();
            String secondaryScr = generateInteger(0, 15).toString();
            String ecn0 = generateFloat(-26, -1).toString();
            String fingerAbsOffset = generateFloat(0, 100).toString();
            String fingerRelOffset = generateFloat(0, 100).toString();
            String fingerRSCP = generateFloat(0, 100).toString();
            str = str + "," + headerParams + "," + fingers + "," + paramsPerFingers + "," + ch + "," + scr + "," + secondaryScr
                    + "," + ecn0 + "," + fingerAbsOffset + "," + fingerRelOffset + "," + fingerRSCP;
        }
        if (system == 10 || system == 11) {
            Integer numberOfHeaderParams = generateInteger(0, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            String aggEcI0 = generateFloat(-32, -1).toString();
            String antConfig = generateInteger(0, 4).toString();
            Integer numberOfFingers = generateInteger(0, maxIntegerValue);
            String fingers = numberOfFingers.toString();
            Integer numberHeaderParamsPerFingers = numberOfHeaderParams / numberOfFingers;
            String paramsPerFingers = numberHeaderParamsPerFingers.toString();
            String pn = generateInteger(0, maxIntegerValue).toString();
            String fingerAbsOffset = generateFloat(0, 100).toString();
            String fingerLocked = generateInteger(0, 1).toString();
            String ecI0 = generateFloat(-32, -1).toString();
            String refFinger = generateInteger(0, 1).toString();
            String assignedFinger = generateInteger(0, 1).toString();
            String tdMode = generateInteger(0, 3).toString();
            String tdPower = generateFloat(-9, -1).toString();
            Integer s = (int)Math.pow(2, generateInteger(0, 21).doubleValue());
            String subchannel = s.toString();
            String lockedAntennas = generateInteger(0, 1).toString();
            String rx0EcI0 = generateFloat(-32, -1).toString();
            String rx1EcI0 = generateFloat(-32, -1).toString();
            str = str + "," + headerParams + "," + aggEcI0 + "," + antConfig + "," + fingers + "," + paramsPerFingers + "," + pn
                    + "," + fingerAbsOffset + "," + fingerLocked + "," + ecI0 + "," + refFinger + "," + assignedFinger + ","
                    + tdMode + "," + tdPower + "," + subchannel + "," + lockedAntennas + "," + rx0EcI0 + "," + rx1EcI0;
        }
        if (system == 12) {
            Integer numberOfHeaderParams = generateInteger(0, maxIntegerValue);
            String headerParams = numberOfHeaderParams.toString();
            String searcherState = generateInteger(0, 12).toString();
            String mstr = generateInteger(0, maxIntegerValue).toString();
            String mstrError = generateInteger(0, maxIntegerValue).toString();
            String mstrPN = generateInteger(0, 511).toString();
            String antConfig = generateInteger(0, 4).toString();
            Integer numberOfFingers = generateInteger(0, maxIntegerValue);
            String fingers = numberOfFingers.toString();
            Integer numberHeaderParamsPerFingers = numberOfHeaderParams / numberOfFingers;
            String paramsPerFingers = numberHeaderParamsPerFingers.toString();
            String pn = generateInteger(0, maxIntegerValue).toString();
            String fingerIndex = generateInteger(0, 11).toString();
            String rpcCellIndex = generateInteger(1, 6).toString();
            String aspIndex = generateInteger(1, 6).toString();
            String ecI0 = generateFloat(-32, -1).toString();
            String rx0EcI0 = generateFloat(-32, -1).toString();
            String rx1EcI0 = generateFloat(-32, -1).toString();
            String fingerLocked = generateInteger(0, 1).toString();
            String fingerAbsOffset = generateFloat(0, 100).toString();
            str = str + "," + headerParams + "," + searcherState + "," + mstr + "," + mstrError + "," + mstrPN + "," + antConfig
                    + "," + fingers + "," + fingers + "," + paramsPerFingers + "," + pn + "," + fingerIndex + "," + rpcCellIndex
                    + "," + aspIndex + "," + ecI0 + "," + rx0EcI0 + "," + rx1EcI0 + "," + fingerLocked + "," + fingerAbsOffset;
        }
        return str;
    }

    private String generateUISCAN() {
        String str = NemoEvents.UISCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        str = str + "," + system.toString();
        String arfcn = generateInteger(1, maxIntegerValue).toString();
        String scr = generateInteger(0, 511).toString();
        String ulInterf = generateFloat(-110, -53).toString();
        str = str + "," + arfcn + "," + scr + "," + ulInterf;
        return str;
    }

    private String generateCELLSCAN() {
        String str = NemoEvents.CELLSCAN.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        str = str + "," + system.toString();
        if (system == 1) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headerParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParamsPerCells = numberOfHeadersParams / numberOfCells;
            String paramsPerCells = numberOfParamsPerCells.toString();
            String arfcn = generateInteger(0, maxIntegerValue).toString();
            String bsic = generateInteger(0, 63).toString();
            String mcc = generateInteger(0, 4095).toString();
            String mnc = generateInteger(0, 999).toString();
            String lac = generateInteger(0, 65535).toString();
            String cellID = generateInteger(0, 65535).toString();
            str = str + "," + headerParams + "," + cells + "," + paramsPerCells + "," + arfcn + "," + bsic + "," + mcc + "," + mnc
                    + "," + lac + "," + cellID;
        }
        if (system == 5) {
            Integer numberOfHeadersParams = generateInteger(0, maxIntegerValue);
            String headerParams = numberOfHeadersParams.toString();
            Integer numberOfCells = generateInteger(0, maxIntegerValue);
            String cells = numberOfCells.toString();
            Integer numberOfParamsPerCells = numberOfHeadersParams / numberOfCells;
            String paramsPerCells = numberOfParamsPerCells.toString();
            String arfcn = generateInteger(0, maxIntegerValue).toString();
            String scr = generateInteger(0, 511).toString();
            String mcc = generateInteger(0, 4095).toString();
            String mnc = generateInteger(0, 999).toString();
            String lac = generateInteger(0, 65535).toString();
            String cellID = generateInteger(0, 268435455).toString();
            str = str + "," + headerParams + "," + cells + "," + paramsPerCells + "," + arfcn + "," + scr + "," + mcc + "," + mnc
                    + "," + lac + "," + cellID;
        }
        return str;
    }

    private String generateHOA() {
        String str = NemoEvents.HOA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateTechnologySystems();
        String headerParams = generateInteger(0, 10).toString();
        // 201,202,...
        String hoaType = generateInteger(101, 104).toString();
        String numberOfCurrentSystemParams = generateInteger(1, 2).toString();
        str = str + "," + headerParams + "," + hoaType + "," + system.toString() + "," + numberOfCurrentSystemParams;
        if (system == 1 || system == 2 || system == 53) {
            String chNumber = generateInteger(1, maxIntegerValue).toString();
            String tsl = generateInteger(0, 7).toString();
            str = str + "," + chNumber + "," + tsl;
        }
        if (system == 5) {
            String chNumber = generateInteger(1, maxIntegerValue).toString();
            String sc = generateInteger(0, 511).toString();
            str = str + "," + chNumber + "," + sc;
        }
        if (system == 6) {
            String chNumber = generateInteger(1, maxIntegerValue).toString();
            String cellParamsID = generateInteger(0, 127).toString();
            str = str + "," + chNumber + "," + cellParamsID;
        }
        if (system == 10 || system == 11 || system == 12) {
            String chNumber = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + chNumber;
        }
        system = generateTechnologySystems();
        String numberOfAttemptedSystemParams = generateInteger(1, 2).toString();
        str = str + "," + system.toString() + numberOfAttemptedSystemParams;
        if (system == 1 || system == 2 || system == 53) {
            String attCh = generateInteger(1, maxIntegerValue).toString();
            String attTsl = generateInteger(0, 7).toString();
            str = str + "," + attCh + "," + attTsl;
        }
        if (system == 5) {
            String attCh = generateInteger(1, maxIntegerValue).toString();
            String attSc = generateInteger(0, 511).toString();
            str = str + "," + attCh + "," + attSc;
        }
        if (system == 6) {
            String attCh = generateInteger(1, maxIntegerValue).toString();
            String attCellParamsID = generateInteger(0, 127).toString();
            str = str + "," + attCh + "," + attCellParamsID;
        }
        return str;
    }

    private String generateHOS() {
        String str = NemoEvents.HOS.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        return str;
    }

    private String generateHOF() {
        String str = NemoEvents.HOF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = generateTechnologySystems();
        str = str + "," + system.toString();
        if (system == 1 || system == 21) {
            // 65,95,...
            String rrCause = generateInteger(0, 12).toString();
            str = str + "," + rrCause;
        }
        if (system == 2 || system == 11 || system == 53) {
            String reserved = "n/a";
            str = str + "," + reserved;
        }
        if (system == 5 || system == 6) {
            String rrcCause = generateInteger(0, 10).toString();
            str = str + "," + rrcCause;
        }
        return str;
    }

    private String generateCREL() {
        String str = NemoEvents.CREL.getEventId() + "," + generateTimestamp() + ",";
        Integer headerParams = generateInteger(0, 10);
        str = str + "," + headerParams.toString();
        for (int i = 0; i < headerParams; i++) {
            Integer param = generateInteger(0, 10);
            String strParam = param.toString();
            if (param == 0) {
                strParam = "";
            }
            str = str + "," + strParam;
        }
        Integer oldSystem = TechnologySystems.GAN_WLAN.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            oldSystem = systems.get(generateInteger(0, 3));
        }
        str = str + "," + oldSystem.toString();
        String params = "2";
        String oldLAC = generateInteger(0, 65535).toString();
        String oldCI = generateInteger(0, maxIntegerValue).toString();
        str = str + "," + params + "," + oldLAC + "," + oldCI;
        Integer system = TechnologySystems.GAN_WLAN.getId();
        Integer choice2 = generateInteger(1, 2);
        if (choice2 == 2) {
            system = systems.get(generateInteger(0, 3));
        }
        str = str + "," + system.toString();
        String params2 = "2";
        String lac = generateInteger(0, 65535).toString();
        String ci = generateInteger(0, maxIntegerValue).toString();
        str = str + "," + params2 + "," + lac + "," + ci;
        return str;
    }

    private String generateSHO() {
        String str = NemoEvents.SHO.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = generateInteger(10, 11);
        }
        str = str + "," + system.toString();
        if (system == 5) {
            String shoStatus = generateInteger(1, 2).toString();
            String rrcCause = generateInteger(0, 10).toString();
            Integer numberOfSCsAdded = generateInteger(1, 10);
            Integer numberOfSCsRemoved = generateInteger(1, 10);
            str = str + "," + shoStatus + "," + rrcCause + "," + numberOfSCsAdded + "," + numberOfSCsRemoved;
            for (int i = 0; i < numberOfSCsAdded; i++) {
                String addedSC = generateInteger(1, 100).toString();
                str = str + "," + addedSC;
            }
            for (int j = 0; j < numberOfSCsRemoved; j++) {
                String removeSC = generateInteger(1, 100).toString();
                str = str + "," + removeSC;
            }
        }
        if (system == 10 || system == 11) {
            Integer numberOfPilotAdded = generateInteger(1, 10);
            Integer numberOfPilotRemoved = generateInteger(1, 10);
            str = str + "," + numberOfPilotAdded + "," + numberOfPilotRemoved;
            for (int i = 0; i < numberOfPilotAdded; i++) {
                String addedPN = generateInteger(1, 100).toString();
                str = str + "," + addedPN;
            }
            for (int j = 0; j < numberOfPilotRemoved; j++) {
                String removePN = generateInteger(1, 100).toString();
                str = str + "," + removePN;
            }
        }
        return str;
    }

    private String generateLUA() {
        String str = NemoEvents.LUA.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = systems.get(generateInteger(0, 3));
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system.toString();
        String luaType;
        if (system == 2) {
            luaType = generateInteger(5, 10).toString();
        } else {
            luaType = generateInteger(1, 4).toString();
        }
        str = str + "," + luaType;
        return str;
    }

    private String generateLUS() {
        String str = NemoEvents.LUS.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = systems.get(generateInteger(0, 3));
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system.toString();
        String oldLAC = generateInteger(0, 65535).toString();
        String lac = generateInteger(0, 65535).toString();
        String mcc = generateInteger(0, 4095).toString();
        String mnc = generateInteger(0, 999).toString();
        str = str + "," + oldLAC + "," + lac + "," + mcc + "," + mnc;
        return str;
    }

    private String generateLUF() {
        String str = NemoEvents.LUF.getEventId() + "," + generateTimestamp() + "," + generateContext(1);
        Integer system = systems.get(generateInteger(0, 3));
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.GAN_WLAN.getId();
        }
        str = str + "," + system.toString();
        String lufStatus = generateInteger(1, 3).toString();
        String oldLAC = generateInteger(0, 65535).toString();
        str = str + "," + oldLAC + "," + lufStatus + "," + oldLAC;
        if (system == 1 || system == 2 || system == 5 || system == 6 || system == 21) {
            String mmCause = generateInteger(1, 17).toString();
            str = str + "," + mmCause;
        }
        return str;
    }

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
            String band = generateInteger(20001, 20015).toString();
            String chType = generateInteger(1, 2).toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String cellID = generateInteger(0, 65535).toString();
            String dtxUL = generateInteger(0, 1).toString();
            String rltMax = generateInteger(4, 64).toString();
            // 10,11,...
            String extChType = generateInteger(1, 5).toString();
            String tn = generateInteger(1, maxIntegerValue).toString();
            String bcchCh = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + band + "," + chType + "," + ch + "," + cellID + "," + dtxUL + "," + rltMax + "," + extChType + ","
                    + tn + "," + bcchCh;
        }
        if (system == 2) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String subchannel = generateInteger(1, 2).toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String lac = generateInteger(0, 65535).toString();
            String extendedSubchannel = generateInteger(0, 7).toString();
            String encryption = generateInteger(0, 1).toString();
            String slotNumber = generateInteger(1, 4).toString();
            str = str + "," + band + "," + subchannel + "," + ch + "," + ch + "," + lac + "," + extendedSubchannel + ","
                    + encryption + "," + slotNumber;
        }
        if (system == 5) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String rrcState = generateInteger(1, 5).toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String cellID = generateInteger(0, 268435455).toString();
            String lac = generateInteger(0, 65535).toString();
            String additionWindow = generateFloat(-2, 13).toString();
            String timeToTrigger1A = generateInteger(0, 5000).toString();
            String dropWindow = generateFloat(0, 16).toString();
            String timeToTrigger1B = generateInteger(0, 5000).toString();
            String replacementWindow = generateFloat(0, 1).toString();
            String timeToTrigger1C = generateInteger(0, 5000).toString();
            String dlSF = generateInteger(0, 512).toString();
            String minUlSF = generateInteger(4, 256).toString();
            String drxCycle = generateInteger(0, 512).toString();
            String maxTXPower = generateFloat(-50, 32).toString();
            String treselection = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + band + "," + rrcState + "," + ch + "," + cellID + "," + lac + "," + additionWindow + ","
                    + timeToTrigger1A + "," + dropWindow + "," + timeToTrigger1B + "," + replacementWindow + "," + timeToTrigger1C
                    + "," + dlSF + "," + minUlSF + "," + drxCycle + "," + maxTXPower + "," + treselection;
        }
        if (system == 6) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String rrcState = generateInteger(1, 5).toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String cellParamsID = generateInteger(0, 127).toString();
            String cellID = generateInteger(0, 268435455).toString();
            String lac = generateInteger(0, 65535).toString();
            String drxCycle = generateInteger(0, 512).toString();
            String maxTXPower = generateFloat(-50, 32).toString();
            String treselection = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + band + "," + rrcState + "," + ch + "," + cellParamsID + "," + cellID + "," + lac + "," + drxCycle
                    + "," + maxTXPower + "," + drxCycle + "," + maxTXPower + "," + treselection;
        }
        if (system == 10 || system == 11) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String chType = generateInteger(1, 4).toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String mcc = generateInteger(0, 4095).toString();
            String sid = generateInteger(1, maxIntegerValue).toString();
            String nid = generateInteger(1, maxIntegerValue).toString();
            String slottedMode = generateInteger(0, 1).toString();
            String search_WIN_A = generateInteger(1, maxIntegerValue).toString();
            String search_WIN_N = generateInteger(1, maxIntegerValue).toString();
            String search_WIN_R = generateInteger(1, maxIntegerValue).toString();
            String tADD = generateInteger(1, maxIntegerValue).toString();
            String tDROP = generateInteger(1, maxIntegerValue).toString();
            String tTDROP = generateInteger(1, maxIntegerValue).toString();
            String tCOMP = generateInteger(1, maxIntegerValue).toString();
            String pREV = generateInteger(1, 11).toString();
            String minPREV = generateInteger(1, 11).toString();
            str = str + "," + band + "," + chType + "," + ch + "," + mcc + "," + sid + "," + nid + "," + slottedMode + ","
                    + search_WIN_A + "," + search_WIN_N + "," + search_WIN_R + "," + tADD + "," + tDROP + "," + tTDROP + ","
                    + tCOMP + "," + pREV + "," + minPREV;
        }
        if (system == 12) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            str = str + "," + band;
        }
        if (system == 21) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String cellID = generateInteger(0, 268435455).toString();
            String lac = generateInteger(0, 65535).toString();
            str = str + "," + band + "," + cellID + "," + lac;
        }
        if (system == 25) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            // 0,1,2,...
            String macState = generateInteger(10, 17).toString();
            String frequency = generateFloat(0, 100).toString();
            String bsID = returnWordSoCalled("WiMAX base station ID");
            String fttSize = generateInteger(1, maxIntegerValue).toString();
            String bandwidth = generateFloat(-1, 100).toString();
            String frameRatioDL = generateInteger(0, 100).toString();
            String frameRatioUL = generateInteger(0, 100).toString();
            String mapCoding = generateInteger(1, 5).toString();
            Integer repetition = generateInteger(1, 3) * 2;
            // 1
            String mapRepetition = repetition.toString();
            str = str + "," + band + "," + macState + "," + frequency + "," + bsID + "," + fttSize + "," + bandwidth + ","
                    + frameRatioDL + "," + frameRatioUL + "," + mapCoding + "," + mapRepetition;
        }
        if (system == 51 || system == 52) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String chType = generateInteger(1, 2).toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + band + "," + chType + "," + ch;
        }
        if (system == 53) {
            // 0,...
            String band = generateInteger(20001, 20015).toString();
            String chType = generateInteger(1, 3).toString();
            String ch = generateInteger(1, maxIntegerValue).toString();
            String nwType = generateInteger(1, maxIntegerValue).toString();
            String psid1 = generateInteger(1, maxIntegerValue).toString();
            String psid2 = generateInteger(1, maxIntegerValue).toString();
            String psid3 = generateInteger(1, maxIntegerValue).toString();
            String psid4 = generateInteger(1, maxIntegerValue).toString();
            String lareg = generateInteger(0, 1).toString();
            String rnum = generateInteger(1, maxIntegerValue).toString();
            String pegPeriod = generateInteger(1, maxIntegerValue).toString();
            str = str + "," + band + "," + chType + "," + ch + "," + nwType + "," + psid1 + "," + psid2 + "," + psid3 + "," + psid4
                    + "," + lareg + "," + rnum + "," + pegPeriod;
        }
        return str;
    }

    private String generateGANCHI() {
        String str = NemoEvents.GANCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GAN_WLAN.getId();
        str = str + "," + system.toString();
        if (system == 21) {
            String headerParams = "7";
            String ganState = generateInteger(1, 4).toString();
            String ganChannel = generateInteger(0, 1023).toString();
            String ganBSIC = generateInteger(0, 63).toString();
            String ganCI = generateInteger(0, 65535).toString();
            String ganLAC = generateInteger(0, 65535).toString();
            String gancIP = returnWordSoCalled("GANC IP address");
            String segwIP = returnWordSoCalled("GANC security gateway IP address");
            str = str + "," + headerParams + "," + ganState + "," + ganChannel + "," + ganBSIC + "," + ganCI + "," + ganLAC + ","
                    + gancIP + "," + segwIP;
        }
        return str;
    }

    private String generateSEI() {
        String str = NemoEvents.SEI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = generateTechnologySystems();
        str = str + "," + system.toString();
        String serviceStatus = generateInteger(1, 2).toString();
        str = str + "," + serviceStatus;
        if (system == 1 || system == 5 || system == 6) {
            String lac = generateInteger(0, 65535).toString();
            String mcc = generateInteger(0, 4095).toString();
            String mnc = generateInteger(0, 999).toString();
            str = str + "," + lac + "," + mcc + "," + mnc;
        }
        return str;
    }

    private String generateROAM() {
        String str = NemoEvents.ROAM.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = generateInteger(5, 6);
        }
        str = str + "," + system.toString();
        String roamingStatus = generateInteger(1, 2).toString();
        str = str + "," + roamingStatus;
        return str;
    }

    private String generateDCHR() {
        String str = NemoEvents.DCHR.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.UMTS_FDD.getId();
        }
        str = str + "," + system.toString();
        if (system == 1) {
            String initiator = generateInteger(1, 2).toString();
            String requestedCoding = generateInteger(1, 2).toString();
            String requestedDataMode = generateInteger(0, 1).toString();
            String requestedTSLUL = generateInteger(1, maxIntegerValue).toString();
            String requestedTSLDL = generateInteger(1, maxIntegerValue).toString();
            String modemType = generateInteger(0, 2).toString();
            String compression = generateInteger(0, 3).toString();
            str = str + "," + initiator + "," + requestedCoding + "," + requestedDataMode + "," + requestedTSLUL + ","
                    + requestedTSLDL + "," + modemType + "," + compression;
        }
        if (system == 5) {
            String initiator = generateInteger(1, 2).toString();
            String reqCSRate = generateInteger(1, maxIntegerValue).toString();
            String requestedDataMode = generateInteger(0, 1).toString();
            String modemType = generateInteger(0, 2).toString();
            String compression = generateInteger(0, 3).toString();
            str = str + "," + initiator + "," + reqCSRate + "," + requestedDataMode + "," + modemType + "," + compression;
        }
        return str;
    }

    private String generateDCHI() {
        String str = NemoEvents.DCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.GSM.getId();
        str = str + "," + system.toString();
        if (system == 1) {
            String coding = generateInteger(1, 2).toString();
            String dataMode = generateInteger(0, 1).toString();
            String csTSLUL = generateInteger(1, maxIntegerValue).toString();
            String csTSLDL = generateInteger(1, maxIntegerValue).toString();
            String csTNs = generateInteger(0, 7).toString();
            String csTNs2 = generateInteger(0, 7).toString();
            str = str + "," + coding + "," + dataMode + "," + csTSLUL + "," + csTSLDL + "," + csTNs + "," + csTNs2;
        }
        return str;
    }

    private String generateHOP() {
        String str = NemoEvents.HOP.getEventId() + "," + generateTimestamp() + ",";
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

    private String generateSERVCONF() {
        String str = NemoEvents.SERVCONF.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.CDMA_ONE.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.CDMA_ONE_X.getId();
        }
        str = str + "," + system.toString();
        if (system == 10 || system == 11) {
            String so = generateInteger(1, maxIntegerValue).toString();
            String ftType = generateInteger(0, 2).toString();
            String rtType = generateInteger(0, 2).toString();
            String encryptionMode = generateInteger(0, 1).toString();
            String fFCHMUX = generateInteger(1, maxIntegerValue).toString();
            String rFCHMUX = generateInteger(1, maxIntegerValue).toString();
            String fFCHBitsPerFrame = generateInteger(1, maxIntegerValue).toString();
            String rFCHBitsPerFrame = generateInteger(1, maxIntegerValue).toString();
            String fFCHRC = generateInteger(1, 10).toString();
            String rFCHRC = generateInteger(1, 10).toString();
            String fDCCHRC = generateInteger(1, 10).toString();
            String rDCCHRadioConfiguration = generateInteger(1, 10).toString();
            String fSCHMUX = generateInteger(1, maxIntegerValue).toString();
            String fSCHRC = generateInteger(1, 10).toString();
            String fSCHCoding = generateInteger(0, 1).toString();
            String fSCHFrameSize = generateInteger(0, 2).toString();
            String fSCHFrameOffset = generateInteger(1, maxIntegerValue).toString();
            Integer number = (int)Math.pow(2, Double.parseDouble(generateInteger(0, 5).toString()));
            String fSCHMaxRate = number.toString();
            String rSCHMUX = generateInteger(1, maxIntegerValue).toString();
            String rSCHRC = generateInteger(1, maxIntegerValue).toString();
            String rSCHCoding = generateInteger(0, 1).toString();
            String rSCHFrameSize = generateInteger(0, 2).toString();
            String rSCHFrameOffset = generateInteger(1, maxIntegerValue).toString();
            Integer number2 = (int)Math.pow(2, Double.parseDouble(generateInteger(0, 5).toString()));
            String rSCHMaxRate = number2.toString();
            str = str + "," + so + "," + ftType + "," + rtType + "," + encryptionMode + "," + fFCHMUX + "," + rFCHMUX + ","
                    + fFCHBitsPerFrame + "," + rFCHBitsPerFrame + "," + fFCHRC + "," + rFCHRC + "," + fDCCHRC + ","
                    + rDCCHRadioConfiguration + "," + fSCHMUX + "," + fSCHRC + "," + fSCHCoding + "," + fSCHFrameSize + ","
                    + fSCHFrameOffset + "," + fSCHMaxRate + "," + rSCHMUX + "," + rSCHRC + "," + rSCHCoding + "," + rSCHFrameSize
                    + "," + rSCHFrameOffset + "," + rSCHMaxRate;
        }
        return str;
    }

    private String generateRACHI() {
        String str = NemoEvents.RACHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = systems.get(generateInteger(2, 6));
        str = str + "," + system.toString();
        if (system == 5) {
            String initTXPower = generateFloat(-99, 98).toString();
            String preambleStep = generateFloat(0, 100).toString();
            String preambleCount = generateInteger(0, 65).toString();
            String rachTXPower = generateFloat(-99, 98).toString();
            String maxPreamble = generateInteger(0, 64).toString();
            String ulInterf = generateFloat(-110, -71).toString();
            String aichStatus = generateInteger(0, 2).toString();
            String dataGain = generateInteger(0, 15).toString();
            String ctrlGain = generateInteger(0, 15).toString();
            String powerOffset = generateFloat(-5, 9).toString();
            String messageLength = generateInteger(5, 19).toString();
            String preambleCycles = generateInteger(1, 32).toString();
            str = str + "," + initTXPower + "," + preambleStep + "," + preambleCount + "," + rachTXPower + "," + maxPreamble + ","
                    + ulInterf + "," + aichStatus + "," + dataGain + "," + ctrlGain + "," + powerOffset + "," + messageLength + ","
                    + preambleCycles;
        }
        if (system == 6) {
            String syncULInitPwr = generateFloat(-99, 98).toString();
            String syncULStep = generateFloat(0, 2).toString();
            String syncULCount = generateInteger(1, 8).toString();
            String maxSyncULCount = generateInteger(1, 8).toString();
            String syncULPower = generateFloat(-99, 98).toString();
            String rachTXPower = generateFloat(-99, 98).toString();
            String pccpchPathloss = generateFloat(46, 157).toString();
            String rachStatus = generateInteger(0, 2).toString();
            String desiredUpPCHRXPower = generateFloat(-120, -59).toString();
            String desiredUpRACHRXPower = generateFloat(-120, -59).toString();
            String messageLength = generateInteger(5, 20).toString();
            String preambleCycles = generateInteger(1, 32).toString();
            str = str + "," + syncULInitPwr + "," + syncULStep + "," + syncULCount + "," + maxSyncULCount + "," + syncULPower + ","
                    + rachTXPower + "," + pccpchPathloss + "," + rachStatus + "," + desiredUpPCHRXPower + ","
                    + desiredUpRACHRXPower + "," + messageLength + "," + preambleCycles;
        }
        if (system == 10 || system == 11) {
            String nomPWR = generateInteger(1, maxIntegerValue).toString();
            String initPWR = generateInteger(1, maxIntegerValue).toString();
            String pwrStep = generateInteger(1, maxIntegerValue).toString();
            String numStep = generateInteger(1, maxIntegerValue).toString();
            String txLevel = generateFloat(0, 100).toString();
            String probeCount = generateInteger(1, maxIntegerValue).toString();
            String probeSeqCount = generateInteger(1, maxIntegerValue).toString();
            String accessChNumber = generateInteger(1, maxIntegerValue).toString();
            String randomDelay = generateInteger(1, maxIntegerValue).toString();
            String accessRxLevel = generateFloat(0, 100).toString();
            String psist = generateInteger(0, 255).toString();
            String seqBackoff = generateInteger(0, 255).toString();
            String probBackoff = generateInteger(0, 255).toString();
            String interCorr = generateInteger(0, 255).toString();
            String accessTXAdj = generateFloat(0, 100).toString();
            str = str + "," + nomPWR + "," + initPWR + "," + pwrStep + "," + numStep + "," + txLevel + "," + probeCount + ","
                    + probeSeqCount + "," + accessChNumber + "," + randomDelay + "," + accessRxLevel + "," + psist + ","
                    + seqBackoff + "," + probBackoff + "," + interCorr + "," + accessTXAdj;
        }
        if (system == 12) {
            String maxProbes = generateInteger(1, maxIntegerValue).toString();
            String maxProbeSeqs = generateInteger(1, maxIntegerValue).toString();
            String result = generateInteger(0, 3).toString();
            String probes = generateInteger(1, maxIntegerValue).toString();
            String probeSeqs = generateInteger(1, maxIntegerValue).toString();
            String duration = generateInteger(1, maxIntegerValue).toString();
            String accessPN = generateInteger(1, maxIntegerValue).toString();
            String accessSectorId = generateInteger(0, 16777215).toString();
            String accessColorCode = generateInteger(0, 255).toString();
            str = str + "," + maxProbes + "," + maxProbeSeqs + "," + result + "," + probes + "," + probeSeqs + "," + duration + ","
                    + accessPN + "," + accessSectorId + "," + accessColorCode;
        }
        return str;
    }

    private String generateVOCS() {
        String str = NemoEvents.VOCS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.CDMA_ONE.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.CDMA_ONE_X.getId();
        }
        str = str + "," + system.toString();
        String vocRateFor = generateInteger(0, 6).toString();
        String vocRateRev = generateInteger(0, 6).toString();
        str = str + "," + vocRateFor + "," + vocRateRev;
        return str;
    }

    private String generatePHCHI() {
        String str = NemoEvents.PHCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = systems.get(generateInteger(3, 5));
        str = str + "," + system.toString();
        if (system == 6) {
            Integer headersParams = generateInteger(1, maxIntegerValue);
            String strHeadersParams = headersParams.toString();
            String ulRepetitionLenght = generateInteger(1, maxIntegerValue).toString();
            String ulRepetitionPeriod = generateInteger(1, 64).toString();
            String dlRepetitionLenght = generateInteger(1, maxIntegerValue).toString();
            String dlRepetitionPeriod = generateInteger(1, 64).toString();
            Integer numberOfChannel = generateInteger(1, maxIntegerValue);
            String physicalChannels = numberOfChannel.toString();
            Integer paramsPerChannel = headersParams / numberOfChannel;
            String strParamsPerChannel = paramsPerChannel.toString();
            String phChType = "1";
            String direction = generateInteger(1, 3).toString();
            String tsl = generateInteger(0, 6).toString();
            Double sf = Math.pow(2, generateInteger(0, 4));
            String strSf = sf.toString();
            String chCode = generateInteger(0, 15).toString();
            String midambleConfig = generateInteger(2, 16).toString();
            String midambleShift = generateInteger(0, 15).toString();
            str = str + "," + strHeadersParams + "," + ulRepetitionLenght + "," + ulRepetitionPeriod + "," + dlRepetitionLenght
                    + "," + dlRepetitionPeriod + "," + physicalChannels + "," + strParamsPerChannel + "," + phChType + ","
                    + direction + "," + tsl + "," + strSf + "," + chCode + "," + midambleConfig + "," + midambleShift;
        }
        if (system == 10 || system == 11) {
            Integer headersParams = generateInteger(1, maxIntegerValue);
            String strHeadersParams = headersParams.toString();
            Integer numberOfChannel = generateInteger(1, maxIntegerValue);
            String physicalChannels = numberOfChannel.toString();
            Integer paramsPerChannel = headersParams / numberOfChannel;
            String strParamsPerChannel = paramsPerChannel.toString();
            String phType = generateInteger(0, 3).toString();
            String direction = generateInteger(1, 3).toString();
            String pn = generateInteger(0, 511).toString();
            String walshCode = generateInteger(0, 127).toString();
            String phRate = generateInteger(0, 4).toString();
            String qofMaskId = generateInteger(0, 3).toString();
            str = str + "," + strHeadersParams + "," + physicalChannels + "," + strParamsPerChannel + "," + phType + ","
                    + direction + "," + pn + "," + walshCode + "," + phRate + "," + qofMaskId;
        }
        return str;
    }

    private String generateQPCHI() {
        String str = NemoEvents.QPCHI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.CDMA_ONE_X.getId();
        str = str + "," + system.toString();
        if (system == 11) {
            Integer headersParams = generateInteger(1, maxIntegerValue);
            String strHeadersParams = headersParams.toString();
            String rate = generateInteger(4800, 9600).toString();
            String slotNumber = generateInteger(0, 2047).toString();
            String transferReason = generateInteger(0, 13).toString();
            Integer numberOfConfigurations = generateInteger(1, maxIntegerValue);
            String configurations = numberOfConfigurations.toString();
            Integer paramsPerConfigurations = headersParams / numberOfConfigurations;
            String strParamsPerConfigurations = paramsPerConfigurations.toString();
            String pn = generateInteger(0, 511).toString();
            String piWalsh = generateInteger(0, 128).toString();
            String piPowerOffset = generateFloat(0, 100).toString();
            String biSupported = generateInteger(0, 1).toString();
            String biWalsh = generateInteger(0, 128).toString();
            String biPwrLvl = generateFloat(0, 100).toString();
            String cciSupported = generateInteger(0, 1).toString();
            String cciWalsh = generateInteger(0, 128).toString();
            String cciPwrLvl = generateFloat(0, 100).toString();
            Integer numberOfIndicators = generateInteger(1, maxIntegerValue);
            String indicators = numberOfIndicators.toString();
            Integer paramsPerIndicators = headersParams / numberOfIndicators;
            String strParamsPerIndicators = paramsPerIndicators.toString();
            String status = generateInteger(0, 4).toString();
            String type = generateInteger(0, 5).toString();
            String thb = generateInteger(0, 255).toString();
            String thi = generateInteger(0, 255).toString();
            String position = generateInteger(0, 768).toString();
            String indIAmp = generateInteger(0, maxIntegerValue).toString();
            String indQAmp = generateInteger(0, maxIntegerValue).toString();
            String comPilotEnergy = generateFloat(-35, 2).toString();
            String divPilotEnergy = generateFloat(-35, 2).toString();
            str = str + "," + strHeadersParams + "," + rate + "," + slotNumber + "," + transferReason + "," + configurations + ","
                    + strParamsPerConfigurations + "," + pn + "," + piWalsh + "," + piPowerOffset + "," + biSupported + ","
                    + biWalsh + "," + biPwrLvl + "," + cciSupported + "," + cciWalsh + "," + cciPwrLvl + "," + indicators + ","
                    + strParamsPerIndicators + "," + status + "," + type + "," + thb + "," + thi + "," + position + "," + indIAmp
                    + "," + indQAmp + "," + comPilotEnergy + "," + divPilotEnergy;
        }
        return str;
    }

    private String generateFCHPACKETS() {
        String str = NemoEvents.FCHPACKETS.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system.toString();
        if (system == 12) {
            String cC38400Good = generateInteger(0, maxIntegerValue).toString();
            String cC38400Bad = generateInteger(0, maxIntegerValue).toString();
            String cC76800Good = generateInteger(0, maxIntegerValue).toString();
            String cC76800Bad = generateInteger(0, maxIntegerValue).toString();
            String tC38400Good = generateInteger(0, maxIntegerValue).toString();
            String tC38400Bad = generateInteger(0, maxIntegerValue).toString();
            String tC76800Good = generateInteger(0, maxIntegerValue).toString();
            String tC76800Bad = generateInteger(0, maxIntegerValue).toString();
            String tC153600Good = generateInteger(0, maxIntegerValue).toString();
            String tC153600Bad = generateInteger(0, maxIntegerValue).toString();
            String t307200ShortGood = generateInteger(0, maxIntegerValue).toString();
            String tC307200ShortBad = generateInteger(0, maxIntegerValue).toString();
            String tC307200LongGood = generateInteger(0, maxIntegerValue).toString();
            String tC307200LongBad = generateInteger(0, maxIntegerValue).toString();
            String tC614400ShortGood = generateInteger(0, maxIntegerValue).toString();
            String tC614400ShortBad = generateInteger(0, maxIntegerValue).toString();
            String tC614400LongGood = generateInteger(0, maxIntegerValue).toString();
            String tC614400LongBad = generateInteger(0, maxIntegerValue).toString();
            String tC921600Good = generateInteger(0, maxIntegerValue).toString();
            String tC921600Bad = generateInteger(0, maxIntegerValue).toString();
            String tC1228800ShortGood = generateInteger(0, maxIntegerValue).toString();
            String tC1228800ShortBad = generateInteger(0, maxIntegerValue).toString();
            String tC1228800LongGood = generateInteger(0, maxIntegerValue).toString();
            String tC1228800LongBad = generateInteger(0, maxIntegerValue).toString();
            String tC1843200Good = generateInteger(0, maxIntegerValue).toString();
            String tC1843200Bad = generateInteger(0, maxIntegerValue).toString();
            String tC2457600Good = generateInteger(0, maxIntegerValue).toString();
            String tC2457600Bad = generateInteger(0, maxIntegerValue).toString();
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

    private String generateCONNECTIONC() {
        String str = NemoEvents.CONNECTIONC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system.toString();
        if (system == 12) {
            String transactionID = generateInteger(0, maxIntegerValue).toString();
            String messageSeq = generateInteger(0, maxIntegerValue).toString();
            String connectionResult = generateInteger(0, 13).toString();
            String recStatus = generateInteger(0, 2).toString();
            String duration = generateInteger(0, maxIntegerValue).toString();
            String pn = generateInteger(0, 511).toString();
            String sectorID = generateInteger(0, maxIntegerValue).toString();
            String cc = generateInteger(0, maxIntegerValue).toString();
            String pnChanges = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + transactionID + "," + messageSeq + "," + connectionResult + "," + recStatus + "," + duration + ","
                    + pn + "," + sectorID + "," + cc + "," + pnChanges;
        }
        return str;
    }

    private String generateCONNECTIOND() {
        String str = NemoEvents.CONNECTIOND.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system.toString();
        String reason = generateInteger(0, 9).toString();
        str = str + "," + reason;
        return str;
    }

    private String generateSESSIONC() {
        String str = NemoEvents.SESSIONC.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.EVDO.getId();
        str = str + "," + system.toString();
        String transactionID = generateInteger(1, maxIntegerValue).toString();
        String result = generateInteger(0, 1).toString();
        String rATI = generateInteger(1, maxIntegerValue).toString();
        String duration = generateInteger(1, maxIntegerValue).toString();
        String pn = generateInteger(0, 511).toString();
        String cc = generateInteger(1, maxIntegerValue).toString();
        String fullUATI = returnWordSoCalled("Session attempt full UATI");
        str = str + "," + transactionID + "," + result + "," + rATI + "," + duration + "," + pn + "," + cc + "," + fullUATI;
        return str;
    }

    private String generateRBI() {
        String str = NemoEvents.RBI.getEventId() + "," + generateTimestamp() + ",";
        Integer system = TechnologySystems.UMTS_FDD.getId();
        Integer choice = generateInteger(1, 2);
        if (choice == 2) {
            system = TechnologySystems.UMTS_TD_SCDMA.getId();
        }
        str = str + "," + system.toString();
        String transactionID = generateInteger(1, maxIntegerValue).toString();
        String result = generateInteger(0, 1).toString();
        String rATI = generateInteger(1, maxIntegerValue).toString();
        String duration = generateInteger(1, maxIntegerValue).toString();
        String pn = generateInteger(0, 511).toString();
        String cc = generateInteger(1, maxIntegerValue).toString();
        String fullUATI = returnWordSoCalled("Session attempt full UATI");
        str = str + "," + transactionID + "," + result + "," + rATI + "," + duration + "," + pn + "," + cc + "," + fullUATI;
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
        Integer trChs = 1;
        str = str + "," + headersParams + "," + paramsPerTrChs + "," + trChs;
        if (system == 5 || system == 6) {
            String trChID = generateInteger(0, 32).toString();
            String cCTrChID = generateInteger(0, 5).toString();
            String direction = generateInteger(0, 2).toString();
            String trChType = generateInteger(0, 7).toString();
            String trChCoding = generateInteger(0, 3).toString();
            String crcLength = generateInteger(0, 24).toString();
            String tti = generateInteger(0, 80).toString();
            String rateMAttr = generateInteger(1, 256).toString();
            str = str + "," + trChID + "," + cCTrChID + "," + direction + "," + trChType + "," + trChCoding + "," + crcLength + ","
                    + tti + "," + rateMAttr;
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
        str = str + "," + system.toString();
        if (system == 5 || system == 6) {
            String rrcEstCause = generateInteger(0, 19).toString();
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
        str = str + "," + system.toString();
        if (system == 5 || system == 6) {
            String rrcAtt = generateInteger(1, 10).toString();
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
        str = str + "," + system.toString();
        if (system == 5 || system == 6) {
            String rrcRelStatus = generateInteger(1, 2).toString();
            // 1000
            String rrcRelCause = generateInteger(0, 6).toString();
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
        str = str + "," + system.toString();
        if (system == 2) {
            String airEncryption = generateInteger(0, 2).toString();
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
        str = str + "," + system.toString();
        if (system == 1) {
            String direction = generateInteger(1, 3).toString();
            String l3Msg = returnWordSoCalled("Layer3 message");
            String subchannel = returnWordSoCalled("Layer3 subchannel");
            String channel = generateInteger(1, maxIntegerValue).toString();
            String bsic = generateInteger(0, 63).toString();
            String type = generateInteger(1, 5).toString();
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + subchannel + "," + channel + "," + bsic + "," + type + "," + l3Data;
        }
        if (system == 2) {
            String direction = generateInteger(1, 3).toString();
            String l3Msg = returnWordSoCalled("Layer3 message");
            String subchannel = returnWordSoCalled("Layer3 subchannel");
            String bsic = returnWordSoCalled("Layer3 BSIC");
            str = str + "," + direction + "," + l3Msg + "," + subchannel + "," + bsic;
        }
        if (system == 5 || system == 6) {
            String direction = generateInteger(1, 3).toString();
            String l3Msg = returnWordSoCalled("Layer3 message");
            String subchannel = returnWordSoCalled("Layer3 subchannel");
            String channel = generateInteger(1, maxIntegerValue).toString();
            String sc = generateInteger(0, 511).toString();
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + subchannel + "," + channel + "," + sc + "," + l3Data;
        }
        if (system == 10 || system == 11) {
            String direction = generateInteger(1, 3).toString();
            String l3Msg = returnWordSoCalled("Layer3 message");
            String channelType = returnWordSoCalled("Layer3 channel type");
            String pREV = generateInteger(1, 11).toString();
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + channelType + "," + pREV + "," + l3Data;
        }
        if (system == 21) {
            String direction = generateInteger(1, 3).toString();
            String l3Msg = returnWordSoCalled("Layer3 message");
            String l3Data = returnWordSoCalled("Layer3 data");
            str = str + "," + direction + "," + l3Msg + "," + l3Data;
        }
        if (system == 51 || system == 53) {
            String direction = generateInteger(1, 3).toString();
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
        str = str + "," + system.toString();
        if (system == 1) {
            String direction = generateInteger(1, 3).toString();
            String l2Msg = returnWordSoCalled("Layer2 message");
            String subchannel = returnWordSoCalled("Layer2 subchannel");
            String arfcn = generateInteger(1, maxIntegerValue).toString();
            String bsic = generateInteger(0, 63).toString();
            String type = generateInteger(1, 5).toString();
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
        str = str + "," + system.toString();
        if (system == 5 || system == 6) {
            String direction = generateInteger(1, 3).toString();
            String rpcMsg = returnWordSoCalled("RRC message");
            String subchannel = returnWordSoCalled("RRC subchannel");
            String uarfcn = generateInteger(1, maxIntegerValue).toString();
            String sc = generateInteger(0, 511).toString();
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
        str = str + "," + system.toString();
        if (system == 5) {
            String direction = generateInteger(1, 3).toString();
            String rlcMsg = returnWordSoCalled("RLC message");
            String subchannel = returnWordSoCalled("RLC subchannel");
            String rb = generateInteger(0, 32).toString();
            String rlcMode = generateInteger(0, 2).toString();
            String lengthIndicator = generateInteger(0, 15).toString();
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
        str = str + "," + system.toString();
        if (system == 1) {
            String direction = generateInteger(1, 3).toString();
            String rlcMacMsg = returnWordSoCalled("RLC/MAC message");
            String subchannel = returnWordSoCalled("RLC/MAC subchannel");
            String type = generateInteger(1, 5).toString();
            String rlcMacData = returnWordSoCalled("RLC/MAC data");
            str = str + "," + direction + "," + rlcMacMsg + "," + subchannel + "," + type + "," + rlcMacData;
        }
        if (system == 25) {
            String direction = generateInteger(1, 3).toString();
            String macMsg = returnWordSoCalled("MAC message");
            String frameNumber = generateInteger(1, maxIntegerValue).toString();
            String macData = returnWordSoCalled("MAC data");
            String macVer = generateInteger(5, 6).toString();
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
        str = str + "," + system.toString();
        if (system == 1 || system == 2) {
            String direction = generateInteger(1, 3).toString();
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
        str = str + "," + system.toString();
        if (system == 12) {
            String direction = generateInteger(1, 3).toString();
            String snpMsgName = returnWordSoCalled("SNP message name");
            String snpChType = returnWordSoCalled("SNP channel type");
            String snpLayer = returnWordSoCalled("SNP layer");
            String protocolSubtype = generateInteger(1, maxIntegerValue).toString();
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
        str = str + "," + system.toString();
        if (system == 1) {
            String direction = generateInteger(1, 3).toString();
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
        str = str + "," + system.toString();
        if (system == 1) {
            String direction = generateInteger(1, 3).toString();
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
        str = str + "," + system.toString();
        if (system == 1 || system == 5) {
            String direction = generateInteger(1, 3).toString();
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
        str = str + "," + system.toString();
        if (system == 1 || system == 5 || system == 21) {
            String direction = generateInteger(1, 3).toString();
            String rtpMsgName = returnWordSoCalled("RTP message name");
            String rtpMsgNr = generateInteger(0, 65535).toString();
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
        str = str + "," + system.toString();
        if (system == 1 || system == 5 || system == 6) {
            String initiator = generateInteger(1, 2).toString();
            String protocolType = "1";
            String apn = returnWordSoCalled("Access point name");
            String statticIP = returnWordSoCalled("Requested packet protocol address");
            String headerCompr = generateInteger(0, 4).toString();
            String compression = generateInteger(0, 3).toString();
            str = str + "," + initiator + "," + protocolType + "," + apn + "," + statticIP + "," + headerCompr + "," + compression;
        }
        if (system == 11 || system == 12) {
            String initiator = generateInteger(1, 2).toString();
            String protocolType = "1";
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
        str = str + "," + system.toString();
        String failStatus = generateInteger(1, 6).toString();
        // 8,81,95,...
        String deactCause = generateInteger(24, 47).toString();
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
        String system = generateTechnologySystems().toString();
        String packetActState = generateInteger(1, 2).toString();
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
        str = str + "," + system.toString();
        String deactStatus = generateInteger(1, 6).toString();
        // 8,81,95,...
        String deactCause = generateInteger(24, 47).toString();
        String deactTime = generateInteger(1, maxIntegerValue).toString();
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
        str = str + "," + system.toString();
        // 31
        String avgTPutClass = generateInteger(0, 18).toString();
        String peakTputClass = generateInteger(0, 9).toString();
        String delayClass = generateInteger(0, 4).toString();
        String priorityClass = generateInteger(0, 3).toString();
        String reliabClass = generateInteger(0, 5).toString();
        str = str + "," + avgTPutClass + "," + peakTputClass + "," + delayClass + "," + priorityClass + "," + reliabClass;
        // 31
        String minAvgTPut = generateInteger(0, 18).toString();
        String minPeakTPut = generateInteger(0, 9).toString();
        String minDelay = generateInteger(0, 4).toString();
        String minPriorityClass = generateInteger(0, 3).toString();
        String minReliability = generateInteger(0, 5).toString();
        String reqTrafficClass = generateInteger(0, 4).toString();
        String reqMaxULTPut = generateInteger(0, 16000).toString();
        String reqMaxDLTPut = generateInteger(0, 16000).toString();
        String reqDelivOrder = generateInteger(0, 2).toString();
        String reqMaxSDUSize = generateInteger(0, 1500).toString();
        String reqSDUErrRatio = returnWordSoCalled("Requested SDU error ratio");
        String reqResidBER = returnWordSoCalled("Requested residual bit error ratio");
        String reqDevilErrSDU = generateInteger(0, 3).toString();
        String reqTransferDelay = generateInteger(0, 4100).toString();
        String reqTHP = generateInteger(0, 3).toString();
        String minTrafficClass = generateInteger(0, 4).toString();
        String minMaxULTPut = generateInteger(0, 16000).toString();
        String minMaxDLTPut = generateInteger(0, 16000).toString();
        String minGrULTPut = generateInteger(0, 16000).toString();
        String minGrDLTPut = generateInteger(0, 16000).toString();
        String minDevilOrder = generateInteger(0, 2).toString();
        String minMaxSDUSize = generateInteger(0, 1500).toString();
        String minSDUErr = returnWordSoCalled("Minimum accepted SDU error ratio");
        String minResidBER = returnWordSoCalled("Minimum accepted residual bit error ratio");
        String minDelErrSDU = generateInteger(0, 3).toString();
        String minTranferDelay = generateInteger(0, 4100).toString();
        String minTHR = generateInteger(0, 3).toString();
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
        str = str + "," + system.toString();
        // 31
        String avgTPutClass = generateInteger(0, 18).toString();
        String peakTputClass = generateInteger(1, 9).toString();
        String delayClass = generateInteger(1, 4).toString();
        String priorityClass = generateInteger(1, 3).toString();
        String reliabClass = generateInteger(1, 5).toString();
        str = str + "," + avgTPutClass + "," + peakTputClass + "," + delayClass + "," + priorityClass + "," + reliabClass;
        // 31
        String trafficClass = generateInteger(0, 3).toString();
        String maxULTPut = generateInteger(0, 16000).toString();
        String maxDLTPut = generateInteger(0, 16000).toString();
        String grULTPut = generateInteger(0, 16000).toString();
        String grDLTPut = generateInteger(0, 16000).toString();
        String devilOrder = generateInteger(0, 1).toString();
        String maxSDUSize = generateInteger(0, 1500).toString();
        String sduErrRatio = returnWordSoCalled("Negotiated SDU error ratio");
        String minResidBER = returnWordSoCalled("Negotiated residual bit error ratio");
        String tranfDelay = generateInteger(0, 4100).toString();
        String thp = generateInteger(0, 3).toString();
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
        str = str + "," + system.toString();
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
        String attachFail = generateInteger(1, 6).toString();
        // ...
        String attFailCause = generateInteger(7, 17).toString();
        str = str + "," + system.toString() + "," + attachFail + "," + attFailCause;
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
        str = str + "," + system.toString();
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
        String amrQualEst = generateFloat(0, 39).toString();
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
            Integer mmsFiles = generateInteger(1, maxIntegerValue);
            String mmsFilename = returnWordSoCalled("MMS filename");
            str = str + "," + mmsMsgType + "," + mmsSerCenter + "," + mmsTrProtocol + "," + mmsFiles + "," + mmsFilename;
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
        String str = NemoEvents.GPS.getEventId() + "," + generateTimestamp() + ",";
        Float lon = generateFloat(0, 100);
        Float lat = generateFloat(0, 100);
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
        String str = NemoEvents.PAUSE.getEventId() + "," + generateTimestamp() + ",,";
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

    private void generateAllEvents(boolean isPredefined, FileWriter wr) {
        if (isPredefined == false) {
            addRowInFile(generatePRODUCT(), wr);
            addRowInFile(generateAG(), wr);
            addRowInFile(generateBF(), wr);
            addRowInFile(generateCI(), wr);
            addRowInFile(generateCL(), wr);
            addRowInFile(generateDL(), wr);
            addRowInFile(generateDN(), wr);
            addRowInFile(generateDS(), wr);
            addRowInFile(generateDT(), wr);
            addRowInFile(generateFF(), wr);
            addRowInFile(generateEI(), wr);
            addRowInFile(generateHV(), wr);
            addRowInFile(generateHW(), wr);
            addRowInFile(generateSI(), wr);
            addRowInFile(generateID(), wr);
            addRowInFile(generateMF(), wr);
            addRowInFile(generateML(), wr);
            addRowInFile(generateNN(), wr);
            addRowInFile(generatePC(), wr);
            addRowInFile(generateSP(), wr);
            addRowInFile(generateSW(), wr);
            addRowInFile(generateTS(), wr);
            addRowInFile(generateUT(), wr);
            addRowInFile(generateVQ(), wr);
            addRowInFile(generateSTART(), wr);
            addRowInFile(generateCAA(), wr);
            addRowInFile(generateCAC(), wr);
            // addRowInFile(generateCAF(), wr);
            // addRowInFile(generateCAD(), wr);
            // addRowInFile(generateVCHI(), wr);
            // addRowInFile(generateDAA(), wr);
            addRowInFile(generateDAC(), wr);
            addRowInFile(generateDAF(), wr);
            addRowInFile(generateDAD(), wr);
            addRowInFile(generateDREQ(), wr);
            addRowInFile(generateDCOMP(), wr);
            addRowInFile(generateDRATE(), wr);
            addRowInFile(generatePER(), wr);
            addRowInFile(generateRTT(), wr);
            addRowInFile(generateJITTER(), wr);
            addRowInFile(generateDSS(), wr);
            addRowInFile(generateDCONTENT(), wr);

            /*
             * addRowInFile(generateCELLMEAS(), wr); addRowInFile(generateADJMEAS(), wr);
             * addRowInFile(generateRXQ(), wr); addRowInFile(generatePRXQ(), wr);
             * addRowInFile(generateFER(), wr); addRowInFile(generateMSP(), wr);
             * addRowInFile(generateRLT(), wr); addRowInFile(generateTAD(), wr);
             * addRowInFile(generateDSC(), wr); addRowInFile(generateBEP(), wr);
             * addRowInFile(generateCIEvent(), wr); addRowInFile(generateTXPC(), wr);
             * addRowInFile(generateRXPC(), wr); addRowInFile(generateBER(), wr);
             * addRowInFile(generatePHRATE(), wr); addRowInFile(generateWLANRATE(), wr);
             * addRowInFile(generatePPPRATE(), wr); addRowInFile(generateRLPRATE(), wr);
             * addRowInFile(generateRLPSTATISTICS(), wr); addRowInFile(generateMEI(), wr);
             * addRowInFile(generateCQI(), wr); addRowInFile(generateHARQI(), wr);
             * addRowInFile(generateHSSCCHI(), wr); addRowInFile(generatePLAID(), wr);
             * addRowInFile(generatePLAIU(), wr); addRowInFile(generateHBI(), wr);
             * addRowInFile(generateMACERATE(), wr); addRowInFile(generateAGRANT(), wr);
             * addRowInFile(generateSGRANT(), wr); addRowInFile(generateEDCHI(), wr);
             * addRowInFile(generateHSUPASI(), wr); addRowInFile(generateDRCI(), wr);
             * addRowInFile(generateRDRC(), wr); addRowInFile(generateFDRC(), wr); //
             * addRowInFile(generatePHREF(), wr); addRowInFile(generateMARKOVMUX(), wr);
             * addRowInFile(generateMARKOVSTATS(), wr); addRowInFile(generateMER(), wr);
             * addRowInFile(generateDVBI(), wr); addRowInFile(generateDVBFER(), wr);
             * addRowInFile(generateDVBBER(), wr); addRowInFile(generateDVBRXL(), wr);
             * addRowInFile(generateDVBRATE(), wr); addRowInFile(generateFREQSCAN(), wr);
             * addRowInFile(generateSPECTRUMSCAN(), wr); addRowInFile(generatePILOTSCAN(), wr);
             * addRowInFile(generateOFDMSCAN(), wr); addRowInFile(generateTPROFSCAN(), wr);
             * addRowInFile(generateDPROFSCAN(), wr); addRowInFile(generateFINGER(), wr);
             * addRowInFile(generateHOS(), wr); addRowInFile(generateLUA(), wr);
             * addRowInFile(generateLUS(), wr); addRowInFile(generateLUF(), wr);
             */

            addRowInFile(generateRRA(), wr);
            addRowInFile(generateRRC(), wr);
            addRowInFile(generateRRF(), wr);
            addRowInFile(generateRRD(), wr);
            addRowInFile(generateCIPI(), wr);
            addRowInFile(generateL3SM(), wr);
            addRowInFile(generateL2SM(), wr);
            addRowInFile(generateRRCSM(), wr);
            addRowInFile(generateRLCSM(), wr);
            addRowInFile(generateMACSM(), wr);
            addRowInFile(generateLLCSM(), wr);
            addRowInFile(generateSNPSM(), wr);
            addRowInFile(generateRRLPSM(), wr);
            addRowInFile(generateGANSM(), wr);
            addRowInFile(generateSIPSM(), wr);
            addRowInFile(generateRTPSM(), wr);
            addRowInFile(generatePAA(), wr);
            addRowInFile(generatePAF(), wr);
            addRowInFile(generatePAC(), wr);
            addRowInFile(generatePAD(), wr);
            addRowInFile(generateQSPR(), wr);
            addRowInFile(generateQSPN(), wr);
            // addRowInFile(generatePCHI(), wr);
            addRowInFile(generateGAA(), wr);
            addRowInFile(generateGAF(), wr);
            addRowInFile(generateGAC(), wr);
            addRowInFile(generateGAD(), wr);
            addRowInFile(generateRLCBLER(), wr);
            addRowInFile(generateRLCRATE(), wr);
            addRowInFile(generateLLCRATE(), wr);
            addRowInFile(generateRUA(), wr);
            addRowInFile(generateRUS(), wr);
            addRowInFile(generateRUF(), wr);
            addRowInFile(generateTBFI(), wr);
            addRowInFile(generateTBFULE(), wr);
            addRowInFile(generateMACRATE(), wr);
            addRowInFile(generateMACBLER(), wr);
            addRowInFile(generateAMRI(), wr);
            addRowInFile(generateAMRQ(), wr);
            addRowInFile(generateAQUL(), wr);
            addRowInFile(generateAQDL(), wr);
            addRowInFile(generateAMRS(), wr);
            addRowInFile(generateAQI(), wr);
            addRowInFile(generateVQDL(), wr);
            addRowInFile(generateVRATE(), wr);
            addRowInFile(generateMSGA(), wr);
            addRowInFile(generateMSGS(), wr);
            addRowInFile(generateMSGF(), wr);
            addRowInFile(generatePTTA(), wr);
            addRowInFile(generatePTTF(), wr);
            addRowInFile(generatePTTC(), wr);
            addRowInFile(generatePTTD(), wr);
            addRowInFile(generatePTTI(), wr);
            addRowInFile(generateRTPJITTER(), wr);
            addRowInFile(generateGPS(), wr);
            addRowInFile(generateTNOTE(), wr);
            addRowInFile(generateQNOTE(), wr);
            addRowInFile(generateQTRIGGER(), wr);
            addRowInFile(generateMARK(), wr);
            addRowInFile(generateERR(), wr);
            addRowInFile(generateDATE(), wr);
            addRowInFile(generatePAUSE(), wr);
            addRowInFile(generateAPP(), wr);
            addRowInFile(generateLOCK(), wr);
            addRowInFile(generateSTOP(), wr);
        }
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
        FileWriter wr;
        try {
            createListOfSystems();
            wr = new FileWriter(nemoFile);
            generateAllEvents(false, wr);
            wr.close();
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public void addRowInFile(String row, FileWriter wr) {
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
        String applicationProtocol = protocol.toString();
        Integer dataFailStatus = generateInteger(1, 5);
        str = str + "," + applicationProtocol + "," + dataFailStatus.toString();
        if (dataFailStatus == 5) {
            String reserved = "n/a";
            str = str + "," + reserved;
        }
        if (dataFailStatus == 2) {
            // 10004,...
            String socketCause = generateInteger(10035, 11031).toString();
            str = str + "," + socketCause;
        }
        if (protocol == 0 || protocol == 1 || protocol == 2) {
            String dataTransferCause = "1";
            str = str + "," + dataTransferCause;
        }
        if (protocol == 3) {
            // 1,2,...
            String ftpCause = generateInteger(500, 504).toString();
            str = str + "," + ftpCause;
        }
        if (protocol == 4) {
            // 1,2,...
            String httpCause = generateInteger(200, 206).toString();
            str = str + "," + httpCause;
        }
        if (protocol == 5) {
            // 1,2,5,...
            String smptCause = generateInteger(500, 504).toString();
            str = str + "," + smptCause;
        }
        if (protocol == 6) {
            // 1,2,6,...
            String pop3Cause = generateInteger(1, 2).toString();
            str = str + "," + pop3Cause;
        }
        if (protocol == 7 || protocol == 8) {
            // 100,101,200,...
            String cause = generateInteger(0, 9).toString();
            str = str + "," + cause;
        }
        if (protocol == 9) {
            // ...
            String streamingCause = generateInteger(65489, 65535).toString();
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
        String applicationProtocol = protocol.toString();
        Integer dataFailStatus = generateInteger(1, 4);
        str = str + "," + applicationProtocol + "," + dataFailStatus.toString();
        if (dataFailStatus == 1) {
            String reserved = "n/a";
            str = str + "," + reserved;
        }
        if (dataFailStatus == 2) {
            // 10004,...
            String socketCause = generateInteger(10035, 11031).toString();
            str = str + "," + socketCause;
        }
        if (protocol == 0 || protocol == 1 || protocol == 2) {
            String dataTransferCause = "1";
            str = str + "," + dataTransferCause;
        }
        if (protocol == 3) {
            // 1,2,...
            String ftpCause = generateInteger(500, 504).toString();
            str = str + "," + ftpCause;
        }
        if (protocol == 4) {
            // 1,2,...
            String httpCause = generateInteger(200, 206).toString();
            str = str + "," + httpCause;
        }
        if (protocol == 5) {
            // 1,2,5,...
            String smptCause = generateInteger(500, 504).toString();
            str = str + "," + smptCause;
        }
        if (protocol == 6) {
            // 1,2,6,...
            String pop3Cause = generateInteger(1, 2).toString();
            str = str + "," + pop3Cause;
        }
        if (protocol == 7 || protocol == 8) {
            // 100,101,200,...
            String cause = generateInteger(0, 9).toString();
            str = str + "," + cause;
        }
        if (protocol == 9) {
            // ...
            String streamingCause = generateInteger(65489, 65535).toString();
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
        int index = generateInteger(0, systems.size() - 1);
        return systems.get(index);
    }

    /**
     * Return word so-called
     * 
     * @param word
     * @return word so-called
     */
    private String returnWordSoCalled(String word) {
        return "\"" + word + "\"";
    }

    public static void main(String[] args) throws IOException {
        Nemo2Generator obj = new Nemo2Generator();
        File nemoFile = obj.createNemoFile();
        obj.fillNemoFile(nemoFile);
    }

}
