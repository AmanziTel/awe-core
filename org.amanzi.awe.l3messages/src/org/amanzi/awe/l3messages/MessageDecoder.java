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

import org.amanzi.awe.l3messages.rrc.UL_DCCH_Message;
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
    private static IDecoder unalignedDecoder;
    
    /*
     * Instance of this class 
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
            return (UL_DCCH_Message)unalignedDecoder.decode(new ByteArrayInputStream(byteArray), UL_DCCH_Message.class);
        }
        catch (Exception e) {
            L3MessagesPlugin.getDefault().logError(e);
            return null;
        }
    }    
}
