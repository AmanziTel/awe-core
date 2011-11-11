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
import java.util.Calendar;
import java.util.Date;
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
    public static Integer maxIntegerValue = Integer.MAX_VALUE-1;

    // home property
    protected static final String USER_HOME = "user.home";

    /**
     * Generate #AG row
     * 
     * @return row
     */
    private String generateAG() {
        String ag = generateFloat().toString();
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
        String cl = generateFloat().toString();
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

    // םוע
    private String generateSTOP() {
        String str = "11:10:30.065,,\"19.10.2009\"RRD,11:10:29.943,1,11,5,1,";
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

    // CAF

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

    // בבבנננ
    private String generateDREQ() {
        String str = ",10:30:31.351,2,5,5,3,2,20971520,\"ftp.adsl.hinet.net/test_020m.zip\",1";
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
            // ןמסלמענועü
        }
        return str;
    }

    private String generateCELLMEAS() {
        String str = "CELLMEAS,10:19:59.946,,5,0,1,2,10762,-73.1,3,17,0,50001,10762,399,-7.7,0,-80.8,,,,,,,,,,114.0,1,50001,10762,428,-19.4,0,-92.5,,,,,,,,,,,1,50001,10762,269,-6.7,0,-79.8,,,,,,,,,,";
        // ןמסלמענועü

        return str;
    }
    
    private String generateADJMEAS(){
        //ןמסלמענועü
        return null;
    }

    //RXQ,PRXQ,FER,MSP,RLT,TAD,DSC,BEP,CI,TXPC
    
    private void generateAllEvents(boolean isPredefined,FileWriter wr) {
        if (isPredefined == false) {
            addRowInFile(generatePRODUCT(),wr);
            addRowInFile(generateAG(),wr);
            addRowInFile(generateBF(),wr);
            addRowInFile(generateCI(),wr);
            addRowInFile(generateCL(),wr);
            addRowInFile(generateDL(),wr);
            addRowInFile(generateDN(),wr);
            addRowInFile(generateDS(),wr);
            addRowInFile(generateDT(),wr);
            addRowInFile(generateFF(),wr);
            addRowInFile(generateEI(),wr);
            addRowInFile(generateHV(),wr);
            addRowInFile(generateHW(),wr);
            addRowInFile(generateSI(),wr);
            addRowInFile(generateID(),wr);
            addRowInFile(generateMF(),wr);
            addRowInFile(generateML(),wr);
            addRowInFile(generateNN(),wr);
            addRowInFile(generatePC(),wr);
            addRowInFile(generateSP(),wr);
            addRowInFile(generateSW(),wr);
            addRowInFile(generateTS(),wr);
            addRowInFile(generateUT(),wr);
            addRowInFile(generateVQ(),wr);
            addRowInFile(generateSTART(),wr);
            addRowInFile(generateDAC(),wr);
            addRowInFile(generateDAF(),wr);
            addRowInFile(generateDAD(),wr);
            addRowInFile(generateDCOMP(),wr);
            addRowInFile(generateDRATE(),wr);
            addRowInFile(generatePER(),wr);
            addRowInFile(generateRTT(),wr);
            addRowInFile(generateSTOP(),wr);
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
    
    public void addRowInFile(String row,FileWriter wr){
        try {
            wr.write(row);
            wr.write("\n");
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
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
    private Float generateFloat() {
        Integer intValue = generateInteger(-100, 100);
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
