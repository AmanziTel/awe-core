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

package org.amanzi.neo.loader.gpeh;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.UnexpectedException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.enums.gpeh.Parameters;
import org.amanzi.neo.core.utils.Pair;
import org.kc7bfi.jflac.io.BitInputStream;

/**
 * <p>
 * Parser of GPEH data
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHParser {
    public static GPEHMainFile parseMainFile(File mainFile) throws IOException {
        GPEHMainFile result = new GPEHMainFile(mainFile);
        InputStream in = new FileInputStream(mainFile);
        if (Pattern.matches("^.+\\.gz$", mainFile.getName())) {
            in = new GZIPInputStream(in);
        }
        BitInputStream input = new BitInputStream(in);
        try {
            while (true) {
                parseData(input, result);
            }

        } catch (EOFException e) {
            // normal behavior
        } finally {
            in.close();
        }
        for (org.amanzi.neo.loader.gpeh.GPEHMainFile.Record record : result.records) {
            for (Pair<String, String> pair : record.filters) {
                System.out.println(pair.left() + "\t" + pair.right());
            }
        }
        return result;
    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    private static void parseData(BitInputStream input, GPEHMainFile result) throws IOException {
        int recordLen = input.readRawUInt(16);
        int recordType = input.readRawUInt(8);
        if (recordType == 0) {
            parseMainHeader(input, result, recordLen - 3);
        } else if (recordType == 1) {
            parseRecord(input, result, recordLen);
        } else if (recordType == 2) {
            parseProtocol(input, result);
        } else if (recordType == 8) {
            parseLink(input, result);
        } else if (recordType == 7) {
            pareseFooter(input, result);
        } else if (recordType == 6) {
            pareseError(input, result);
        } else {
            // wrong file format!
            throw new IllegalArgumentException("Wrong blok!=" + recordType);
        }

    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    public static void pareseError(BitInputStream input, IGPEHBlock result) throws IOException {
        GPEHError error = new GPEHError();
        result.setEndRecord(error);
        error.hour = input.readRawUInt(8);
        error.minute = input.readRawUInt(8);
        error.second = input.readRawUInt(8);
        error.millisecond = input.readRawUInt(16);
        error.errorType = input.readRawUInt(8);
    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    public static void pareseFooter(BitInputStream input, IGPEHBlock result) throws IOException {
        GPEHFooter footer = new GPEHFooter();
        result.setEndRecord(footer);
        footer.year = input.readRawUInt(16);
        footer.month = input.readRawUInt(8);
        footer.day = input.readRawUInt(8);
        footer.hour = input.readRawUInt(8);
        footer.minute = input.readRawUInt(8);
        footer.second = input.readRawUInt(8);
    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    private static void parseLink(BitInputStream input, GPEHMainFile result) throws IOException {
        GPEHMainFile.Link link = new GPEHMainFile.Link();
        result.addLink(link);
        link.filePath = readAllString(input, 256);
        System.out.println(link.filePath);
    }

    /**
     * @param input
     * @param result
     * @param recordLen
     * @throws IOException
     */
    private static void parseProtocol(BitInputStream input, GPEHMainFile result) throws IOException {
        GPEHMainFile.Protocol protocol = new GPEHMainFile.Protocol();
        result.addProtocol(protocol);
        protocol.id = input.readRawUInt(8);
        protocol.name = readAllString(input, 50);
        protocol.objectIdentifier = readAllString(input, 30);
    }

    /**
     * @param input
     * @param result
     * @param recordLen
     * @throws IOException
     */
    private static void parseRecord(BitInputStream input, GPEHMainFile result, int recordLen) throws IOException {
        GPEHMainFile.Record record = new GPEHMainFile.Record();
        result.addRecord(record);
        record.filterType = input.readRawUInt(8);
        if (recordLen > 4) {
            byte[] buffer = new byte[recordLen - 4];
            for (int i=0;i<buffer.length;i++) {
                buffer[i]=(byte)input.readRawUInt(8);
            }
            record.saveFilters(buffer);
        }
    }

    /**
     * @param input
     * @param result
     * @param recordLen
     * @throws IOException
     */
    private static void parseMainHeader(BitInputStream input, GPEHMainFile result, int recordLen) throws IOException {
        int len = 0;
        result.header.fileVer = readAllString(input, 5);
        len += 5;
        result.header.year = input.readRawUInt(16);
        len += 2;
        result.header.month = input.readRawUInt(8);
        len += 1;
        result.header.day = input.readRawUInt(8);
        len += 1;
        result.header.hour = input.readRawUInt(8);
        len += 1;
        result.header.minute = input.readRawUInt(8);
        len += 1;
        result.header.second = input.readRawUInt(8);
        len += 1;
        result.header.neUserLabel = readAllString(input, 200);
        len += 200;
        result.header.neLogicalName = readAllString(input, 200);
        len += 200;
        if (len < recordLen) {
            System.err.append(String.format("Header have %d unparsed byts", recordLen - len));
            readAllString(input, recordLen - len);
        }
    }

    public static String readStringToDelim(InputStream input, int delim) throws IOException {
        StringBuilder result = new StringBuilder();
        int rb;
        while ((rb = input.read()) != -1) {
            if (rb == delim) {
                break;
            }
            result.append((char)rb);
        }
        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        // parseMainFile(new
        // File("d://AWE/GPEH/Avea_Niste01_30min_GPEH_trace/A20090706.1700+0300-1715+0300_SubNetwork=NISTE01,MeContext=NISTE01_rnc_gpehfile_Mp0.bin.gz"));
        parseEventFile(new File("d://AWE/GPEH/Avea_Niste01_30min_GPEH_trace/A20090706.1700+0300-1715+0300_SubNetwork=NISTE01,MeContext=NISTE01_rnc_gpehfile_Mp1.bin.gz"));
    }

    /**
     * @param file
     * @throws IOException
     * @throws
     */
    public static GPEHEvent parseEventFile(File file) throws IOException {
        GPEHEvent result = new GPEHEvent();
        InputStream in = new FileInputStream(file);
        if (Pattern.matches("^.+\\.gz$", file.getName())) {
            in = new GZIPInputStream(in);
        }
        BitInputStream input = new BitInputStream(in);
        try {
            while (true) {
                parseSubFile(input, result);
            }
        } catch (EOFException e) {
            // normal behavior
        } finally {
            in.close();
        }
        return result;
    }

    /**
     * Parse sub file
     * 
     * @param input input stream
     * @param result GPEHEvent
     */
    public static void parseSubFile(BitInputStream input, GPEHEvent result) throws IOException {
        int recordLen = input.readRawUInt(16) - 3;
        int recordType = input.readRawUInt(8);
        if (recordType == 4) {
            parseEvent(input, result, recordLen);
        } else if (recordType == 7) {
            pareseFooter(input, result);
        } else if (recordType == 6) {
            pareseError(input, result);
        } else {
            // wrong file format!
            throw new IllegalArgumentException();
        }
    }

    /**
     *Parse event
     * 
     * @param input input stream
     * @param result GPEHEvent
     * @param recordLen length of event
     * @throws IOException
     */
    public static void parseEvent(BitInputStream input, GPEHEvent result, int recordLen) throws IOException {
        GPEHEvent.Event event = new GPEHEvent.Event();

        StringBuilder bits = new StringBuilder("");
        // String buf= readBits(bits,input,recordLen*8);
        // bits.insert(0, buf);
        // System.out.println("--------");
        // System.out.println(bits.toString());
        event.scannerId = (Integer)readParameter(input, Parameters.EVENT_PARAM_SCANNER_ID).getLeft();
        event.hour = (Integer)readParameter(input, Parameters.EVENT_PARAM_TIMESTAMP_HOUR).getLeft();
        event.minute = (Integer)readParameter(input, Parameters.EVENT_PARAM_TIMESTAMP_MINUTE).getLeft();
        event.second = (Integer)readParameter(input, Parameters.EVENT_PARAM_TIMESTAMP_SECOND).getLeft();
        event.millisecond =(Integer)readParameter(input, Parameters.EVENT_PARAM_TIMESTAMP_MILLISEC).getLeft();
        event.id = (Integer)readParameter(input, Parameters.EVENT_PARAM_EVENT_ID).getLeft();
        int len = 24 + 5 + 6 + 6 + 11 + 11;
        Events events = Events.findById(event.id);
        final int recLen = recordLen * 8;
        boolean parseOk = false;
        if (events != null) {
            parseOk = true;
            event.setType(events);
            // TODO debug
            List<Parameters> allParameters = events.getAllParameters();
            for (Parameters parameter : allParameters) {
                int maxBitLen = recLen-len;
                if (parameter==Parameters.EVENT_PARAM_MESSAGE_CONTENTS){
                     Integer lenMsg=(Integer)event.getProperties().get(Parameters.EVENT_PARAM_MESSAGE_LENGTH);
                     if (lenMsg!=null){
                         maxBitLen=Math.min(lenMsg*8, maxBitLen);
                     }
                }
                final Pair<Object, Integer> readParameter = readParameter(input, parameter,maxBitLen);
                event.addProperty(parameter, readParameter.getLeft());
                int bitsLen = readParameter.getRight();
                len += bitsLen;
            }
        }
        if (parseOk) {
            result.addEvent(event);
        } else {
            // System.out.println("Event not parsed!\t"+event.id);
        }
        if (len < recLen) {
            input.skipBitsNoCRC(recLen - len);
            if (parseOk && len + 32 < recLen) {
                System.out.println("Wrong parsing !\t" + event.id);
            }
        }else if (len>recLen){
            throw new UnexpectedException("to large");
        }
        // System.out.println(event.id);
        // readBits= readBits(bits,input,16);
        // event.ueContextId=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,7);
        // event.rncModuleId=Integer.valueOf(readBits, 2);
        //        
        // readBits= readBits(bits,input,17);
        // event.cellID1=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,13);
        // event.rncID1=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,17);
        // event.cellID2=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,13);
        // event.rncID2=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,17);
        // event.cellID3=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,13);
        // event.rncID3=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,17);
        // event.cellID4=Integer.valueOf(readBits, 2);
        // readBits= readBits(bits,input,13);
        // event.rncID4=Integer.valueOf(readBits, 2);
        //        
        // int len = (recordLen-3)*8-5-6-6-22-16-7-17-13-17-13-17-13-17-13;
        // System.out.println(len);
        // event.notParsed=readBits(bits,input,len);
        // System.out.println(bits.toString());

    }

    /**
     *Read value
     * 
     * @param input - BitInputStream
     * @param parameter - parameter
     * @return readed value
     * @throws IOException 
     */
    private static Pair<Object,Integer> readParameter(BitInputStream input, Parameters parameter) throws IOException {
        return readParameter( input,  parameter,parameter.getBitsLen());
    }
    /**
     *Read value
     * 
     * @param input - BitInputStream
     * @param parameter - parameter
     * @param maxBitLen- maximum of readed bits
     * @return readed value
     * @throws IOException 
     */
    private static Pair<Object,Integer> readParameter(BitInputStream input, Parameters parameter,int maxBitLen) throws IOException {
        
        final int bitsLen = Math.min(parameter.getBitsLen(),maxBitLen);
        switch (parameter.getRule()) {
        case INTEGER:
            return new Pair<Object,Integer>(input.readRawUInt(bitsLen),bitsLen);
        case LONG:
            return new Pair<Object,Integer>(input.readRawULong(bitsLen),bitsLen);
        case STRING:
            Pair<String, Integer> result=readString(input, bitsLen);
            return new Pair<Object,Integer>(result.left(),result.right());
        default:
            break;
        }
        return null;
    }

    /**
     * @param bits
     * @param input
     * @param i
     * @return
     * @throws IOException
     * @throws NumberFormatException
     */
    private static Pair<String, Integer> readString( BitInputStream input, int len) throws NumberFormatException, IOException {
        int count = 0;
        StringBuilder result = new StringBuilder();
        while (count + 8 <= len) {
            int byteSymb = input.readRawInt(8);
            count += 8;
            if (byteSymb == 0) {
                break;
            }
            result.append((char)byteSymb);
        }
        return new Pair<String, Integer>(result.toString(), count);
    }
    /**
     * @param bits
     * @param input
     * @param i
     * @return
     * @throws IOException
     * @throws NumberFormatException
     */
    private static String readAllString( BitInputStream input, int len) throws NumberFormatException, IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int byteSymb = input.readRawInt(8);
            if (byteSymb == 0) {
                continue;
            }
            result.append((char)byteSymb);
        }
        return result.toString();
    }


}
