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

package org.amanzi.neo.loader;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

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
        GPEHMainFile result = new GPEHMainFile();
        DataInputStream input = new DataInputStream(new GZIPInputStream(new FileInputStream(mainFile)));
        try {
            while (true) {
                parseData(input, result);
            }

        }catch (EOFException e) {
            //normal behavior
        } finally {
            input.close();
        }
        return result;
    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    private static void parseData(DataInputStream input, GPEHMainFile result) throws IOException {
        int recordLen = input.readUnsignedShort();
        int recordType = input.readByte();
        if (recordType == 0) {
            parseMainHeader(input, result);
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
            throw new IllegalArgumentException();
        }

    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    private static void pareseError(DataInputStream input, IGPEHBlock result) throws IOException {
        GPEHError error = new GPEHError();
        result.setEndRecord(error);
        error.hour = input.readUnsignedByte();
        error.minute = input.readUnsignedByte();
        error.second = input.readUnsignedByte();
        error.millisecond = input.readUnsignedShort();
        error.errorType = input.readUnsignedByte();
    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    private static void pareseFooter(DataInputStream input, IGPEHBlock result) throws IOException {
        GPEHFooter footer = new GPEHFooter();
        result.setEndRecord(footer);
        footer.year = input.readUnsignedShort();
        footer.month = input.readUnsignedByte();
        footer.day = input.readUnsignedByte();
        footer.hour = input.readUnsignedByte();
        footer.minute = input.readUnsignedByte();
        footer.second = input.readUnsignedByte();
    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    private static void parseLink(DataInputStream input, GPEHMainFile result) throws IOException {
        GPEHMainFile.Link link = new GPEHMainFile.Link();
        result.addLink(link);
        link.filePath = readString(input, 256);
        System.out.println(link.filePath);
    }

    /**
     * @param input
     * @param result
     * @param recordLen
     * @throws IOException
     */
    private static void parseProtocol(DataInputStream input, GPEHMainFile result) throws IOException {
        GPEHMainFile.Protocol protocol = new GPEHMainFile.Protocol();
        result.addProtocol(protocol);
        protocol.id = input.readUnsignedByte();
        protocol.name = readString(input, 50);
        protocol.objectIdentifier = readString(input, 30);
    }

    /**
     * @param input
     * @param result
     * @param recordLen
     * @throws IOException
     */
    private static void parseRecord(DataInputStream input, GPEHMainFile result, int recordLen) throws IOException {
        GPEHMainFile.Record record = new GPEHMainFile.Record();
        result.addRecord(record);
        record.filterType = input.readUnsignedByte();
        if (recordLen > 4) {
            byte[] buffer = new byte[recordLen - 4];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length && (numRead = input.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            record.saveFilters(buffer);
        }
    }

    /**
     * @param input
     * @param result
     * @throws IOException
     */
    private static void parseMainHeader(DataInputStream input, GPEHMainFile result) throws IOException {
        result.header.fileVer = readString(input, 5);
        result.header.year = input.readUnsignedShort();
        result.header.month = input.readUnsignedByte();
        result.header.day = input.readUnsignedByte();
        result.header.hour = input.readUnsignedByte();
        result.header.minute = input.readUnsignedByte();
        result.header.second = input.readUnsignedByte();
        result.header.neUserLabel = readString(input, 200);
        result.header.neLogicalName = readString(input, 200);
    }

    /**
     * @param input
     * @param len
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream input, int len) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte readByte = input.readByte();
            if (readByte != 0) {
                result.append((char)readByte);
            }
        }
        return result.toString();
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
//        parseMainFile(new File("d://AWE/GPEH/Avea_Niste01_30min_GPEH_trace/A20090706.1700+0300-1715+0300_SubNetwork=NISTE01,MeContext=NISTE01_rnc_gpehfile_Mp0.bin.gz"));
        parseEventFile(new File("d://AWE/GPEH/Avea_Niste01_30min_GPEH_trace/A20090706.1700+0300-1715+0300_SubNetwork=NISTE01,MeContext=NISTE01_rnc_gpehfile_Mp1.bin.gz"));
    }

    /**
     *
     * @param file
     * @throws IOException 
     * @throws  
     */
    public static GPEHEvent parseEventFile(File file) throws  IOException {
        GPEHEvent result = new GPEHEvent();
            DataInputStream input = new DataInputStream(new GZIPInputStream(new FileInputStream(file)));
            try {
                while (true) {
                    parseSubFile(input, result);
                }
            }catch (EOFException e) {
                //normal behavior
            } finally {
                input.close();
            }
            return result;
        }

    /**
     *
     * @param input
     * @param result
     */
    private static void parseSubFile(DataInputStream input, GPEHEvent result) throws  IOException {
        int recordLen = input.readUnsignedShort();
        int recordType = input.readByte();
        if (recordType == 4) {
            parseEvent(input, result,recordLen);
        }else if (recordType == 7) {
            pareseFooter(input, result);
        } else if (recordType == 6) {
            pareseError(input, result);
        } else {
            // wrong file format!
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param input
     * @param result
     * @param recordLen 
     * @throws IOException 
     */
    private static void parseEvent(DataInputStream input, GPEHEvent result, int recordLen) throws IOException {
        GPEHEvent.Event event=new GPEHEvent.Event();
        result.addEvent(event);
        StringBuilder bits=new StringBuilder("");
        String readBits= readBits(bits,input,5);
        event.hour=Integer.valueOf(readBits, 2);
        readBits= readBits(bits,input,6);
        event.minute=Integer.valueOf(readBits, 2);       
        readBits= readBits(bits,input,6);
        event.second=Integer.valueOf(readBits, 2);       
        readBits= readBits(bits,input,11);
        event.millisecond=Integer.valueOf(readBits, 2);       
        readBits= readBits(bits,input,11);
        event.id=Integer.valueOf(readBits, 2);  
        System.out.println(event.id);
        int len = (recordLen-3)*8-5-6-6-22;
        System.out.println(len);
        event.notParsed=readBits(bits,input,len);
        System.out.println(bits.toString());

    }

    /**
     *
     * @param bits
     * @param input
     * @param i
     * @return
     * @throws IOException 
     */
    private static String readBits(StringBuilder bits, DataInputStream input, int len) throws IOException {
        while (bits.length()<len){
             int readByte = input.readUnsignedByte();
            StringBuilder binaryString = new StringBuilder(Integer.toString(readByte,2));
            while (binaryString.length()<8){
                binaryString.insert(0, "0");
            }
            bits.append(binaryString);
        }
        String result=bits.substring(0,len);
        bits.delete(0, len);
        return result;
    }

}
