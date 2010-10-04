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
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.URLUtils;

import org.amanzi.awe.l3.messages.streaming.schema.SchemaGenerator;
import org.amanzi.awe.l3.messages.streaming.schema.nodes.SchemaNode;
import org.amanzi.awe.l3messages.rrc.UL_DCCH_Message;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.utils.BitArrayInputStream;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

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
    
    private static String getSchemaLocation() throws IOException {
        URL schemaDir = Platform.getBundle(L3MessagesPlugin.PLUGIN_ID).getEntry("/");
        String schemaLocation = URLUtils.urlToString(FileLocator.toFileURL(schemaDir), false);
        if (schemaLocation.startsWith("jar:file:")) {
            //Lagutko, 2.12.2009, conflict between paths in Windows and Linux
            //in Windows path didn't starts with '/' so we should add it
            //but in Linux we have first char '/' and if we add another than path '//home' will be incorrect
            schemaLocation = schemaLocation.substring(9);
            if (!schemaLocation.startsWith(File.separator)) {                
                schemaLocation = File.separator + schemaLocation;
            }
            schemaLocation = "file:" + schemaLocation;
        }
        else if (schemaLocation.startsWith("file:")) {
            schemaLocation = schemaLocation.substring(5);
        }
        
        return schemaLocation + File.separator + "schema";
    }
    
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
                
                File schemaDirectoryName = new File(getSchemaLocation());
                SchemaNode root = null;
                SchemaGenerator generator = new SchemaGenerator(schemaDirectoryName);
                root = generator.parse();                
                
                for (SchemaNode childNode : root.getChildren().values()) {
                    if (childNode.getName().equals("UL-DCCH-Message")) {
                        message = childNode;
                        break;
                    }
                }
            }
            catch (Exception e) {
                L3MessagesPlugin.getDefault().logError(e);
                instance = null;
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
