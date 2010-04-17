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

package org.amanzi.awe.l3messages;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.amanzi.awe.l3messages.rrc.UL_DCCH_Message;
import org.amanzi.awe.l3messages.rrc.UL_DCCH_MessageType;
import org.bn.CoderFactory;
import org.bn.IDecoder;

/**
 * Class for encoding NBAP and RRC Messages from Measurement Reports
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class MessageDecoder {
    
    /*
     * Decoder  
     */
    private static IDecoder alignedDecoder;
    
    private static IDecoder unalignedDecoder;
    
    /*
     * Instate of this class 
     */
    private static MessageDecoder instance = null;
    
    /**
     * Returns instance of Message Decoder
     *
     * @return
     */
    public static MessageDecoder getInstance() {
        if (instance == null) {
            instance = new MessageDecoder();
            try {
                alignedDecoder = CoderFactory.getInstance().newDecoder("PER/A");
                unalignedDecoder = CoderFactory.getInstance().newDecoder("PER/U");
            }
            catch (Exception e) {
                L3MessagesPlugin.getDefault().logError(e);
            }
        }
        
        return instance;
    }
    
    /**
     * Parses RRC Measurement Report message
     *
     * @param byteArray
     * @return
     */
    public UL_DCCH_Message parseRRCMeasurementReport(byte[] byteArray) {
        try {
            return (UL_DCCH_Message)alignedDecoder.decode(new ByteArrayInputStream(byteArray), UL_DCCH_Message.class);
        }
        catch (Exception e) {
            System.out.println(new String(byteArray));
            L3MessagesPlugin.getDefault().logError(e);
            return null;
        }
    }
    
    /**
     * Parses RRC Measurement Report message
     *
     * @param byteArray
     * @return
     */
    public UL_DCCH_Message parseRRCMeasurementReport2(byte[] byteArray) {
        try {
            return (UL_DCCH_Message)unalignedDecoder.decode(new ByteArrayInputStream(byteArray), UL_DCCH_Message.class);
        }
        catch (Exception e) {
            System.out.println(new String(byteArray));
            L3MessagesPlugin.getDefault().logError(e);
            return null;
        }
    }
    

    public static void main(String[] args) {
        getInstance();
        File dir = new File("D:/Download/m");
        int failed = 0;
        int success = 0;
        for (File file : dir.listFiles()) {
            try {
                UL_DCCH_Message message = unalignedDecoder.decode(new FileInputStream(file), UL_DCCH_Message.class);
                UL_DCCH_MessageType type = message.getMessage();
                if (type.getSpare1() == null) {
                    type.getMeasurementReport().getClass();
                }
                success++;
            }
            catch (Exception e) {
                System.out.println(file.getName());
                failed++;
            }
        }
        System.out.println("Successfully parsed " + success);
        System.out.println("Parsing failed " + failed);
    }
}
