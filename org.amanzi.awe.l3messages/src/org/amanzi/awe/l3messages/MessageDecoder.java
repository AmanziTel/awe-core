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

import org.amanzi.awe.l3.messages.streaming.schema.SchemaGenerator;
import org.amanzi.awe.l3.messages.streaming.schema.nodes.SchemaNode;
import org.amanzi.awe.l3messages.rrc.UL_DCCH_Message;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.utils.BitArrayInputStream;

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
    
    private static SchemaNode message = null;
    
    private static ASNParser parser = new ASNParser();
    
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
                
                String schemaDirectory = "D:/projects/awe/org.amanzi.awe.l3messages/schema";
                SchemaGenerator generator = new SchemaGenerator(new File(schemaDirectory));
                SchemaNode root = generator.parse();
                
                for (SchemaNode childNode : root.getChildren().values()) {
                    if (childNode.getName().equals("UL-DCCH-Message")) {
                        message = childNode;
                        break;
                    }
                }
            }
            catch (Exception e) {
                L3MessagesPlugin.getDefault().logError(e);
            }
        }
        
        return instance;
    }
    
    public void parseRRCMeasurementReport(byte[] byteArray, IAsnParserListener eventListener) {
        try {
            parser.addListener(eventListener);
            parser.decode(new BitArrayInputStream(new ByteArrayInputStream(byteArray)), message);    
        }
        catch (Exception e){ 
            L3MessagesPlugin.getDefault().logError(e);
        }
        finally {
            parser.removeListener(eventListener);            
        }
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
