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

    /**
     * Generate #AG row
     * 
     * @return row
     */
    private String generateAG() {
        String ag = generateFloat(-100, 100).toString();
        String str = NemoEvents.AG.getEventId() + ",,," + ag;
        return str;
    }

    /**
     * Generate #BF row
     * 
     * @return row
     */
    private String generateBF() {
        String btsFile = "BTS_name";
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
        String cl = generateFloat(-100, 100).toString();
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
        Integer numberOfSystems = generateInteger(1, 65);
        String numberOfSupportedSystems = numberOfSystems.toString();
        String[] supportedSystems = new String[numberOfSystems];
        for (int i = 0; i < numberOfSystems; i++) {
            supportedSystems[i] = generateInteger(1, 65).toString();
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
        String deviceType = generateInteger(0, 1).toString();
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

    private String generateMF() {
        String mapFile = "Map filename";
        String str = NemoEvents.MF.getEventId() + ",,," + returnWordSoCalled(mapFile);
        return str;
    }

    private String generateML() {
        String measurementLabel = "Measurement label";
        String str = NemoEvents.ML.getEventId() + ",,," + returnWordSoCalled(measurementLabel);
        return str;
    }

    private String generateNN() {
        String networkName = "Network name";
        String str = NemoEvents.NN.getEventId() + ",,," + returnWordSoCalled(networkName);
        return str;
    }

    private String generatePC() {
        String packetCaptureState = generateInteger(0, 1).toString();
        String str = NemoEvents.PC.getEventId() + ",,," + packetCaptureState;
        return str;
    }

    private String generatePRODUCT() {
        String productName = "Product name";
        String productVersion = "Product version";
        String str = NemoEvents.PRODUCT.getEventId() + ",,," + returnWordSoCalled(productName) + ","
                + returnWordSoCalled(productVersion);
        return str;
    }

    private String generateSI() {
        String subscriberIdentity = "Subscriber identity";
        String str = NemoEvents.SI.getEventId() + ",,," + returnWordSoCalled(subscriberIdentity);
        return str;
    }

    private String generateSP() {
        String subscriberPhoneNumber = "Subscriber phone number";
        String str = NemoEvents.SP.getEventId() + ",,," + returnWordSoCalled(subscriberPhoneNumber);
        return str;
    }

    private String generateSW() {
        String deviceSoftwareVersion = "Device software version";
        String str = NemoEvents.SW.getEventId() + ",,," + returnWordSoCalled(deviceSoftwareVersion);
        return str;
    }

    private String generateTS() {
        String testScriptFilename = "Test script filename";
        String str = NemoEvents.TS.getEventId() + ",,," + returnWordSoCalled(testScriptFilename);
        return str;
    }

    private String generateUT() {
        String gapToUTC = generateInteger(-720, 720).toString();
        String str = NemoEvents.UT.getEventId() + ",,," + gapToUTC;
        return str;
    }

    private String generateVQ() {
        String vqType = generateInteger(0, 4).toString();
        String vqVersion = "Voice quality version";
        String str = NemoEvents.VQ.getEventId() + ",,," + vqType + "," + returnWordSoCalled(vqVersion);
        return str;
    }

    private String generateSTART() {
        String timestamp = generateTimestamp();
        String date = generateDate();
        String str = NemoEvents.START.getEventId() + "," + timestamp + ",," + returnWordSoCalled(date);
        return str;
    }

    // ������
    private String generateSTOP() {
        String timestamp = generateTimestamp();
        String date = generateDate();
        String str = NemoEvents.STOP.getEventId() + "," + timestamp + ",," + returnWordSoCalled(date);
        return str;
    }

    private String generateCAA() {
        String str = NemoEvents.CAA.getEventId() + "," + generateTimestamp() + "," + generateContext();
        String system = generateInteger(1, 65).toString();
        String callType = generateInteger(1, 9).toString();
        String direction = generateInteger(1, 2).toString();
        String number = "Called number";
        str = str + "," + system + "," + callType + "," + direction + "," + returnWordSoCalled(number);
        return str;
    }

    private String generateCAC() {
        String str = NemoEvents.CAC.getEventId() + "," + generateTimestamp() + "," + generateContext();
        Integer system = generateInteger(1, 65);
        String strSystem = system.toString();
        String callType = generateInteger(1, 9).toString();
        String callStatus = generateInteger(1, 4).toString();
        str = str + "," + strSystem + "," + callType + "," + callStatus;
        if (system == 1) {
            String tn = generateInteger(0, 7).toString();
            str = str + "," + tn;
        }
        if (system == 2) {
            String tn = generateInteger(1, 4).toString();
            str = str + "," + tn;
        }
        return str;
    }

    private String generateDAC() {
        String str = NemoEvents.DAC.getEventId() + "," + generateTimestamp() + "," + generateContext();
        String applicationProtocol = generateInteger(0, 14).toString();
        str = str + "," + applicationProtocol;
        return str;
    }

    private String generateDAF() {
        String str = NemoEvents.DAF.getEventId() + "," + generateTimestamp() + "," + generateContext() + generateDataOfProtocol();
        return str;
    }

    private String generateDAD() {
        String str = NemoEvents.DAD.getEventId() + "," + generateTimestamp() + "," + generateContext() + generateDataOfProtocol();
        return str;
    }

    private String generateDREQ() {
        String str = NemoEvents.DREQ.getEventId() + "," + generateTimestamp() + "," + generateContext();
        Integer protocol = generateInteger(0, 14);
        str = str + "," + protocol.toString();
        String transfDir = generateInteger(1, 3).toString();
        str = str + "," + transfDir;
        if (protocol == 0 || protocol == 1 || protocol == 2) {
            String fileSize = generateInteger(0, maxIntegerValue).toString();
            String packetSize = generateInteger(0, maxIntegerValue).toString();
            String rateLimit = generateInteger(0, maxIntegerValue).toString();
            String pingSize = generateInteger(0, 100000).toString();
            String pingRate = generateInteger(0, maxIntegerValue).toString();
            String pingTimeout = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + fileSize + "," + packetSize + "," + rateLimit + "," + pingSize + "," + pingRate + "," + pingTimeout;
        }
        if (protocol == 3 || protocol == 4) {
            String fileSize = generateInteger(0, maxIntegerValue).toString();
            String fileName = "Data transfer filename";
            String transfAtt = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + fileSize + "," + returnWordSoCalled(fileName) + "," + transfAtt;
        }
        if (protocol == 5 || protocol == 6 || protocol == 7 || protocol == 8 || protocol == 9 || protocol == 10) {
            String fileSize = generateInteger(0, maxIntegerValue).toString();
            String fileName = "Data transfer filename";
            str = str + "," + fileSize + "," + returnWordSoCalled(fileName);
        }
        if (protocol == 11) {
            String fileName = "Data transfer filename";
            str = str + "," + returnWordSoCalled(fileName);
        }
        if (protocol == 12) {
            String pingSize = generateInteger(0, 100000).toString();
            String pingRate = generateInteger(0, maxIntegerValue).toString();
            String pingTimeout = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + pingSize + "," + pingRate + "," + pingTimeout;
        }
        if (protocol == 13 || protocol == 14) {
            String dataSize = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + dataSize;
        }
        return str;
    }

    private String generateDCOMP() {
        String str = NemoEvents.DCOMP.getEventId() + "," + generateTimestamp() + "," + generateContext()
                + generateDataOfProtocol2();
        String ipAccessTime = generateInteger(0, maxIntegerValue).toString();
        String ipTermTime = generateInteger(0, maxIntegerValue).toString();
        String bytesUL = generateInteger(0, maxIntegerValue).toString();
        String bytesDL = generateInteger(0, maxIntegerValue).toString();
        str = str + "," + ipAccessTime + "," + ipTermTime + "," + bytesUL + "," + bytesDL;
        return str;
    }

    private String generateDRATE() {
        String str = NemoEvents.DRATE.getEventId() + "," + generateTimestamp() + "," + generateContext();
        String applicationProtocol = generateInteger(0, 14).toString();
        String appRateUL = generateInteger(0, maxIntegerValue).toString();
        String appRateDL = generateInteger(0, maxIntegerValue).toString();
        String bytesUL = generateInteger(0, maxIntegerValue).toString();
        String bytesDL = generateInteger(0, maxIntegerValue).toString();
        str = str + "," + applicationProtocol + "," + appRateUL + "," + appRateDL + "," + bytesUL + "," + bytesDL;
        return str;
    }

    private String generatePER() {
        String str = NemoEvents.PER.getEventId() + "," + generateTimestamp() + "," + generateContext();
        String applicationProtocol = generateInteger(0, 14).toString();
        String perUL = generateInteger(0, 100).toString();
        String perDL = generateInteger(0, 100).toString();
        str = str + "," + applicationProtocol + "," + perUL + "," + perDL;
        return str;
    }

    private String generateRTT() {
        String str = NemoEvents.RTT.getEventId() + "," + generateTimestamp() + "," + generateContext();
        Integer protocol = generateInteger(0, 14);
        String applicationProtocol = protocol.toString();
        str = str + "," + applicationProtocol;
        if (protocol == 12) {
            String pingSize = generateInteger(0, 100000).toString();
            String pingRTT = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + pingSize + "," + pingRTT;
        }
        return str;
    }

    private String generateJITTER() {
        String str = NemoEvents.JITTER.getEventId() + "," + generateTimestamp() + "," + generateContext();
        Integer protocol = generateInteger(0, 14);
        String applicationProtocol = protocol.toString();
        str = str + "," + applicationProtocol;
        if (protocol == 13 || protocol == 14) {
            String jitterUl = generateInteger(0, maxIntegerValue).toString();
            String jitterDl = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + jitterUl + "," + jitterDl;
        }
        return str;
    }

    private String generateDSS() {
        String str = NemoEvents.DSS.getEventId() + "," + generateTimestamp() + ",,";
        Integer protocol = generateInteger(0, 14);
        String applicationProtocol = protocol.toString();
        str = str + "," + applicationProtocol;
        if (protocol == 9) {
            str = str + generateContext();
            String streamState = generateInteger(1, 3).toString();
            String streamBandwidth = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + streamState + "," + streamBandwidth;
        }
        return str;
    }

    private String generateDCONTENT() {
        String str = NemoEvents.DCONTENT.getEventId() + "," + generateTimestamp() + ",,";
        Integer protocol = generateInteger(0, 14);
        String applicationProtocol = protocol.toString();
        str = str + "," + applicationProtocol;
        if (protocol == 8 || protocol == 10) {
            str = str + generateContext();
            String numberOfContentElements = generateInteger(0, 10).toString();
            String numberOfParametersPerContent = generateInteger(0, 10).toString();
            String contentURL = "Content URL";
            String contentType = generateInteger(1, 3).toString();
            String contentSize = generateInteger(0, maxIntegerValue).toString();
            str = str + "," + numberOfContentElements + "," + numberOfParametersPerContent + "," + returnWordSoCalled(contentURL)
                    + "," + contentType + "," + contentSize;
        }
        return str;
    }

    private String generateCELLMEAS() {
        String str = NemoEvents.CELLMEAS.getEventId() + "," + generateTimestamp() + ",,";
        Integer system = generateTechnologySystems();
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
            String c1 = generateFloat(-100, 100).toString();
            String c2 = generateFloat(-100, 100).toString();
            String c31 = generateFloat(-100, 100).toString();
            String c32 = generateFloat(-100, 100).toString();
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
            String c1 = generateFloat(-100, 100).toString();
            String c2 = generateFloat(-100, 100).toString();
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
            String rssi = generateFloat(-100, 100).toString();
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
            String rssi = generateFloat(-100, 100).toString();
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
        // ����������
        return null;
    }

    // RXQ,PRXQ,FER,MSP,RLT,TAD,DSC,BEP,CI,TXPC

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
     * @return context
     */
    private String generateContext() {
        Integer numberOfContextIDs = generateInteger(0, 10);
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
        System.out.println(str);
        return str;
    }

    private String generateDataOfProtocol() {
        String str = "";
        Integer protocol = generateInteger(0, 14);
        String applicationProtocol = protocol.toString();
        Integer dataFailStatus = generateInteger(1, 5);
        str = str + "," + applicationProtocol + "," + dataFailStatus.toString();
        if (dataFailStatus == 5) {
            String reserved = "";
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
            String reserved = "";
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

    private Integer generateTechnologySystems() {
        List<Integer> systems = new ArrayList<Integer>();
        for (TechnologySystems system : TechnologySystems.values()) {
            systems.add(system.getId());
        }
        int index = generateInteger(0, systems.size());
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
        /*
         * File nemoFile = obj.createNemoFile(); obj.generateAllEvents(false);
         * obj.fillNemoFile(nemoFile);
         */
        File nemoFile = obj.createNemoFile();
        obj.fillNemoFile(nemoFile);

    }

}
