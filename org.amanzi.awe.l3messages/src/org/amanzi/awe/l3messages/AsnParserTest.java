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
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.amanzi.awe.l3.messages.streaming.schema.SchemaGenerator;
import org.amanzi.awe.l3.messages.streaming.schema.nodes.SchemaNode;
import org.amanzi.awe.l3messages.rrc.CellMeasuredResults;
import org.amanzi.awe.l3messages.rrc.GSM_MeasuredResults;
import org.amanzi.awe.l3messages.rrc.InterFreqMeasuredResults;
import org.amanzi.awe.l3messages.rrc.InterFreqMeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.InterRATMeasuredResults;
import org.amanzi.awe.l3messages.rrc.InterRATMeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.IntraFreqMeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.MeasuredResults;
import org.amanzi.awe.l3messages.rrc.MeasurementReport;
import org.amanzi.awe.l3messages.rrc.UE_InternalMeasuredResults;
import org.amanzi.awe.l3messages.rrc.UL_DCCH_Message;
import org.amanzi.awe.l3messages.rrc.UL_DCCH_MessageType;
import org.amanzi.awe.l3messages.rrc.CellMeasuredResults.ModeSpecificInfoChoiceType.FddSequenceType;
import org.amanzi.awe.parser.gpeh.Events;
import org.amanzi.awe.parser.gpeh.GPEHParser;
import org.amanzi.awe.parser.internal.core.GPEHDataElement;
import org.bn.utils.BitArrayInputStream;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class AsnParserTest {
    
    private static class EventListener implements IAsnParserListener {

        @Override
        public boolean processEvent(AsnParserEvent event) {
            switch (event.getClassType()) {
            case INTEGER:
                if (event.getClassName().equals("PrimaryScramblingCode")) {
                    asnEventMap.put(GpehReportUtil.GPEH_RRC_SCRAMBLING_PREFIX + ++rscpIndex, event.getValue().toString());
                }
                else {
                    int value = ((Long)event.getValue()).intValue();
                    if (event.getClassName().equals("CPICH-RSCP")) {
                        asnEventMap.put(GpehReportUtil.GPEH_RRC_MR_RSCP_PREFIX + rscpIndex, value);
                    }
                    else if (event.getClassName().equals("CPICH-Ec-N0")) {
                        asnEventMap.put(GpehReportUtil.GPEH_RRC_MR_ECNO_PREFIX + rscpIndex, value);
                    }
                    else if (event.getElementName().equals("verifiedBSIC")) {
                        asnEventMap.put(GpehReportUtil.GPEH_RRC_MR_BSIC_PREFIX + ++rscpIndex, value);
                    }
                    else if (event.getElementName().equals("UE-TransmittedPower")) {
                        asnEventMap.put(GpehReportUtil.GPEH_RRC_MR_UE_TX_POWER_PREFIX, value);
                    }
                }
                break;
            case CHOICE:
                if (event.getClassName().equals("UL-DCCH-MessageType")) {
                    if (!event.getValue().equals("MeasurementReport")) {
                        return false;
                    }
                }
                else if (event.getClassName().startsWith("MeasuredResults") ||
                         event.getClassName().startsWith("EventResult")) {
                    String value = (String)event.getValue();
                    if (value.contains("InterFreq")) {
                        if (!asnEventMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_INTERF);
                        }
                    }
                    else if (value.contains("InterRAT")) {
                        if (!asnEventMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_IRAT);
                        }
                    }
                    else if (value.contains("IntraFreq")) {
                        if (!asnEventMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_INTRAF);
                        }
                    }
                    else if (value.contains("UE-Internal")) {
                        if (!asnEventMap.containsKey(GpehReportUtil.MR_TYPE)) {
                            asnEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_UE_INTERNAL);
                        }
                    }
                    else {
                        return false;
                    }
                }      
                break;
            }
            return true;
        }
        
    }
    
    private static HashMap<String, Object> asnEventMap = new HashMap<String, Object>();
    
    private static HashMap<String, Object> notesEventMap = new HashMap<String, Object>();
    
    private static SchemaNode message;
    
    private static PrintStream failedEvents;
    
    private static ASNParser parser = new ASNParser();
    
    private static int rscpIndex;
    
    public static void main(String[] args) {
        String schemaDirectory = "D:/projects/awe/org.amanzi.awe.l3messages/schema";
        SchemaGenerator generator = new SchemaGenerator(new File(schemaDirectory));
        SchemaNode root = generator.parse();
        
        parser.addListener(new EventListener());
        
        try {
            Scanner scanner = new Scanner(new File(schemaDirectory + "/failedEvents.txt"));
        
            for (SchemaNode childNode : root.getChildren().values()) {
                if (childNode.getName().equals("UL-DCCH-Message")) {
                    message = childNode;
                    break;
                }
            }
            
            HashSet<Events> possibleEvents = new HashSet<Events>(1);
            possibleEvents.add(Events.RRC_MEASUREMENT_REPORT);
            
            while (scanner.hasNextLine()) {
                processEvent(scanner.nextLine());                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main2(String[] args) {
        String schemaDirectory = "D:/projects/awe/org.amanzi.awe.l3messages/schema";
        String gpehDirectory = "D:/Download/GPEH unzipped";
        SchemaGenerator generator = new SchemaGenerator(new File(schemaDirectory));
        SchemaNode root = generator.parse();
        
        parser.addListener(new EventListener());
        
        try {
            failedEvents = new PrintStream(new File(schemaDirectory + "/failedEvents.txt"));
        
            for (SchemaNode childNode : root.getChildren().values()) {
                if (childNode.getName().equals("UL-DCCH-Message")) {
                    message = childNode;
                    break;
                }
            }
            
            HashSet<Events> possibleEvents = new HashSet<Events>(1);
            possibleEvents.add(Events.RRC_MEASUREMENT_REPORT);
            
            long before = System.currentTimeMillis();
            for (File gpehFile : new File(gpehDirectory).listFiles()) {
                GPEHParser parser = new GPEHParser();
                parser.init(gpehFile);
                parser.setPossibleIds(possibleEvents);
                
                while (parser.hasNext()) {
                    processEvent(parser.next());
                }
            }
            System.out.println(System.currentTimeMillis() - before);
            
            failedEvents.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    private static void processEvent(String stringContent) {
        byte[] content = stringContent.getBytes();
        
        asnEventMap.clear();
        notesEventMap.clear();
        
        UL_DCCH_Message ulMessage = null;
        try {
            ulMessage = MessageDecoder.getInstance().parseRRCMeasurementReport(content);
            processMessage(ulMessage);
        }
        catch (Exception e) {
            //do nothing
        }
        
        try {
            parser.decode(new BitArrayInputStream(new ByteArrayInputStream(content)), message);                    
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        check();
    }
    
    private static void processEvent(GPEHDataElement event) {
        if (event != null) {
            if (event.containsKey("EVENT_PARAM_MESSAGE_CONTENTS")) {
                byte[] content = (byte[])event.get("EVENT_PARAM_MESSAGE_CONTENTS");
                
                asnEventMap.clear();
                notesEventMap.clear();
                
                try {
                    UL_DCCH_Message message = MessageDecoder.getInstance().parseRRCMeasurementReport(content);
                    processMessage(message);
                }
                catch (Exception e) {
                    //do nothing
                }
                
                rscpIndex = 0;
                
                try {
                    parser.decode(new BitArrayInputStream(new ByteArrayInputStream(content)), message);                    
                }
                catch (Exception e) {
                    failedEvents.println(new String(content));
//                    e.printStackTrace();
                }
                
                if (!check()) {
                    failedEvents.println(new String(content));
                }
            }
        }
    }
    
    private static boolean check() {
        boolean result = true;
        
        for (String key : notesEventMap.keySet()) {
            Object notesValue = notesEventMap.get(key);
            
            if (!asnEventMap.containsKey(key)) {
                System.out.println("ASN Parser didn't parse " + key + " element");
                return false;
            }
            else {
                if (!asnEventMap.get(key).equals(notesValue)) {
                    System.out.println("Wrong value of " + key + " element");
                    return false;
                }
            }
        }
        
        return result;
    }

    private static void processMessage(UL_DCCH_Message message) {
        if (message == null) {
            return;
        }

        UL_DCCH_MessageType messageType = message.getMessage();

        if (messageType == null) {
            return;
        }
        
        rscpIndex = 0;

        if (messageType.isMeasurementReportSelected()) {
            // get a MeasurementReport
            MeasurementReport report = messageType.getMeasurementReport();

            if (report.isMeasuredResultsPresent()) {
                // get MeasuredResults
                MeasuredResults result = report.getMeasuredResults();

                if (result.isInterFreqMeasuredResultsListSelected()) {
                    // process InterFreq Results
                    InterFreqMeasuredResultsList interFreqResultList = result.getInterFreqMeasuredResultsList();
                    notesEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_INTERF);
                    for (InterFreqMeasuredResults singleInterFreqResult : interFreqResultList.getValue()) {
                        if (singleInterFreqResult.isInterFreqCellMeasuredResultsListPresent()) {
                            for (CellMeasuredResults singleCellMeasuredResult : singleInterFreqResult.getInterFreqCellMeasuredResultsList().getValue()) {
                                rscpIndex++;
                                saveCellMeasuredResults(singleCellMeasuredResult);
                            }
                        }
                    }
                } else if (result.isInterRATMeasuredResultsListSelected()) {
                    // process InterRAT Results
                    InterRATMeasuredResultsList interRATResultList = result.getInterRATMeasuredResultsList();
                    notesEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_IRAT);
                    for (InterRATMeasuredResults singleInterRatResults : interRATResultList.getValue()) {
                        if (singleInterRatResults.isGsmSelected()) {
                            for (GSM_MeasuredResults singleGSMResults : singleInterRatResults.getGsm().getValue()) {
                                if (singleGSMResults.getBsicReported().isVerifiedBSICSelected()) {
                                    rscpIndex++;
                                    notesEventMap.put(GpehReportUtil.GPEH_RRC_MR_BSIC_PREFIX + rscpIndex, singleGSMResults.getBsicReported().getVerifiedBSIC());
                                }
                            }
                        }
                    }
                } else if (result.isIntraFreqMeasuredResultsListSelected()) {
                    // process IntraFreq Results
                    IntraFreqMeasuredResultsList intraFreqResultList = result.getIntraFreqMeasuredResultsList();
                    notesEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_INTRAF);
                    for (CellMeasuredResults singleCellMeasuredResults : intraFreqResultList.getValue()) {
                        rscpIndex++;
                        saveCellMeasuredResults(singleCellMeasuredResults);
                    } 
                } else if (result.isUe_InternalMeasuredResultsSelected()) {
                    // process UE Internal Results
                    notesEventMap.put(GpehReportUtil.MR_TYPE, GpehReportUtil.MR_TYPE_UE_INTERNAL);
                    UE_InternalMeasuredResults ueInternalResults = result.getUe_InternalMeasuredResults();
                    if (ueInternalResults.getModeSpecificInfo().isFddSelected()) {
                        notesEventMap.put(GpehReportUtil.GPEH_RRC_MR_UE_TX_POWER_PREFIX, ueInternalResults.getModeSpecificInfo().getFdd().getUe_TransmittedPowerFDD().getValue());
                    }
                }
            }
        }
    }
    
    private static void saveCellMeasuredResults(CellMeasuredResults results) {
        if (results.getModeSpecificInfo().isFddSelected()) {
            FddSequenceType fdd = results.getModeSpecificInfo().getFdd();
            Integer scramblingCode = fdd.getPrimaryCPICH_Info().getPrimaryScramblingCode().getValue();
            // store scramblingCode like string
            notesEventMap.put(GpehReportUtil.GPEH_RRC_SCRAMBLING_PREFIX + rscpIndex, scramblingCode.toString());
            if (fdd.isCpich_RSCPPresent()) {
                Integer value = fdd.getCpich_RSCP().getValue();

                notesEventMap.put(GpehReportUtil.GPEH_RRC_MR_RSCP_PREFIX + rscpIndex, value);
            }
            if (fdd.isCpich_Ec_N0Present()) {
                Integer value = fdd.getCpich_Ec_N0().getValue();
                notesEventMap.put(GpehReportUtil.GPEH_RRC_MR_ECNO_PREFIX + rscpIndex, value);
            }
        }
    }
}
