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

package org.amanzi.awe.cassidian.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.amanzi.awe.cassidian.constants.LoaderConstants;
import org.amanzi.awe.cassidian.structure.Attachment;
import org.amanzi.awe.cassidian.structure.CellReselection;
import org.amanzi.awe.cassidian.structure.CommonTestData;
import org.amanzi.awe.cassidian.structure.CompleteGpsData;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.GroupAttach;
import org.amanzi.awe.cassidian.structure.Handover;
import org.amanzi.awe.cassidian.structure.InconclusiveElement;
import org.amanzi.awe.cassidian.structure.ItsiAttach;
import org.amanzi.awe.cassidian.structure.MPTSync;
import org.amanzi.awe.cassidian.structure.NeighborData;
import org.amanzi.awe.cassidian.structure.NeighborDetails;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.awe.cassidian.structure.PESQResultElement;
import org.amanzi.awe.cassidian.structure.ProbeIDNumberMap;
import org.amanzi.awe.cassidian.structure.RecieveMsg;
import org.amanzi.awe.cassidian.structure.SendMsg;
import org.amanzi.awe.cassidian.structure.SendReport;
import org.amanzi.awe.cassidian.structure.ServingData;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.amanzi.awe.cassidian.structure.TTCElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * <p>
 * Record classes data into xml
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */

public class AMSXMLWriter {

    private DocumentBuilderFactory dbfac;
    private DocumentBuilder docBuilder;
    private Text text;
    private Document doc;
    private String directory = System.getProperty("user.home");
    private String fileName = "xmlfile";
    private String extension = ".xml";
    private File savedFile;
    FileWriter fw;
    private long time = System.currentTimeMillis();

    public AMSXMLWriter() {
        dbfac = DocumentBuilderFactory.newInstance();

        try {
            docBuilder = dbfac.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Handle ParserConfigurationException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        doc = docBuilder.newDocument();
    }

    public AMSXMLWriter(String directoryPath, String fileName) {
        dbfac = DocumentBuilderFactory.newInstance();
        this.directory = directoryPath;
        this.fileName = fileName + "#" + time;
        try {

            docBuilder = dbfac.newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            // TODO Handle ParserConfigurationException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        doc = docBuilder.newDocument();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * build TNSTree
     * 
     * @return
     */
    public Document buildTree(TNSElement tns) {
        Element tnsRoot = doc.createElement(tns.getType());
        tnsRoot.setAttribute(LoaderConstants.ATTRIBUTE_NAME_XMLNS_TNS, LoaderConstants.ATTRIBUTE_VALUE_XMLNS_TNS);
        tnsRoot.setAttribute(LoaderConstants.ATTRIBUTE_NAME_XMLNS_XSI, LoaderConstants.ATTRIBUTE_VALUE_XMLNS_XSI);
        tnsRoot.setAttribute(LoaderConstants.ATTRIBUTE_NAME_XSI_SCHEME_LOCATION,
                LoaderConstants.ATTRIBUTE_VALUE_XSI_SCHEME_LOCATION);
        doc.appendChild(tnsRoot);

        buildCommonTestElement(tnsRoot, tns.getCtd());
        buildEventElement(tnsRoot, tns.getEvents());
        buildGPSDataElement(tnsRoot, tns.getGps());

        return doc;
    }

    /**
     * @param tnsRoot
     * @param list
     */
    private void buildCommonTestElement(Element root, List<CommonTestData> list) {
        Element commonTestData;
        for (CommonTestData ctd : list) {
            commonTestData = doc.createElement(ctd.getType());
            root.appendChild(commonTestData);
            buildProbeIdNumberMap(commonTestData, ctd.getProbeIdNumberMap());
            buildServingData(commonTestData, ctd.getServingData());
            buildNeighborData(commonTestData, ctd.getNeighborDatas());
            buildNTPQData(commonTestData, ctd.getNtpq());
            buildMPTSyncData(commonTestData, ctd.getMptsync());
        }
    }

    private void buildNTPQData(Element root, List<Ntpq> list) {
        Element ntpq;
        for (Ntpq el : list) {
            ntpq = doc.createElement(el.getType());
            root.appendChild(ntpq);
            createNewElement(ntpq, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(ntpq, el.getTimeiInXMLformat(el.getNtpqTime()), LoaderConstants.NTPQ_TIME);
            createNewElement(ntpq, el.getJitter(), LoaderConstants.JITTER);
            createNewElement(ntpq, el.getOffset(), LoaderConstants.OFFSET);

        }
    }

    private void buildMPTSyncData(Element root, List<MPTSync> list) {
        Element mptSync;
        for (MPTSync el : list) {
            mptSync = doc.createElement(el.getType());
            root.appendChild(mptSync);
            createNewElement(mptSync, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(mptSync, el.getTimeiInXMLformat(el.getMptSyncTime()), LoaderConstants.MPT_SYNC_TIME);
            createNewElement(mptSync, el.getTimeOut(), LoaderConstants.TIMEOUT);
            String probeList = "";
            for (Object probe : el.getProbeList()) {
                if (probe instanceof Calendar) {
                    probeList += el.getTimeInXmlFormatWithioutZone((Calendar)probe);
                } else {
                    probeList += probe + ", ";
                }
            }
            createNewElement(mptSync, probeList, LoaderConstants.PROBE_LIST);
            createNewElement(mptSync, el.getSyncId(), LoaderConstants.SYNC_ID);

        }
    }

    private void buildNeighborData(Element root, List<NeighborData> list) {
        Element neighborData;
        for (NeighborData el : list) {
            neighborData = doc.createElement(el.getType());
            root.appendChild(neighborData);
            createNewElement(neighborData, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(neighborData, el.getTimeiInXMLformat(el.getDeliveryTime()), LoaderConstants.DELIVERY_TIME);
            buildNeighborDetails(neighborData, el.getNeighborDetails());
        }
    }

    private void buildNeighborDetails(Element root, List<NeighborDetails> list) {
        Element neighborDetails;
        for (NeighborDetails el : list) {
            neighborDetails = doc.createElement(el.getType());
            root.appendChild(neighborDetails);
            createNewElement(neighborDetails, el.getC2(), LoaderConstants.C2);
            createNewElement(neighborDetails, el.getFrequency(), LoaderConstants.FREQUENCY);
            createNewElement(neighborDetails, el.getRssi(), LoaderConstants.RSSI);
        }
    }

    /**
     * @param commonTestData
     * @param servingData
     */
    private void buildServingData(Element root, List<ServingData> list) {
        Element servData;
        for (ServingData el : list) {
            servData = doc.createElement(el.getType());
            root.appendChild(servData);
            createNewElement(servData, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(servData, el.getLocationArea(), LoaderConstants.LOCATION_AREA);
            createNewElement(servData, el.getCl(), LoaderConstants.CL);
            createNewElement(servData, el.getFrequency(), LoaderConstants.FREQUENCY);
            createNewElement(servData, el.getRssi(), LoaderConstants.RSSI);
            createNewElement(servData, el.getTimeiInXMLformat(el.getDeliveryTime()), LoaderConstants.DELIVERY_TIME);
        }
    }

    /**
     * @param commonTestData
     * @param probeIdNumberMap
     */
    private void buildProbeIdNumberMap(Element root, List<ProbeIDNumberMap> list) {
        Element completeGpsData;
        for (ProbeIDNumberMap el : list) {
            completeGpsData = doc.createElement(el.getType());
            root.appendChild(completeGpsData);
            createNewElement(completeGpsData, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(completeGpsData, el.getLocationArea(), LoaderConstants.LOCATION_AREA);
            createNewElement(completeGpsData, el.getPhoneNumber(), LoaderConstants.PHONE_NUMBER);
            createNewElement(completeGpsData, el.getFrequency(), LoaderConstants.FREQUENCY);
        }
    }

    /**
     * @param tnsRoot
     * @param gps
     */
    private void buildGPSDataElement(Element root, List<GPSData> gpsList) {
        Element gpsElement;
        for (GPSData gps : gpsList) {
            gpsElement = doc.createElement(gps.getType());
            root.appendChild(gpsElement);
            buildCompleateGpsDataList(gpsElement, gps.getCompleteGpsDataList());
        }
    }

    /**
     * @param gpsElement
     * @param completeGpsDataList
     */
    private void buildCompleateGpsDataList(Element root, List<CompleteGpsDataList> list) {
        Element newElement;
        for (CompleteGpsDataList compleateGPSDataList : list) {
            newElement = doc.createElement(compleateGPSDataList.getType());
            root.appendChild(newElement);
            buildGpsData(newElement, compleateGPSDataList.getCompleteGpsData());
        }

    }

    /**
     * @param newElement
     * @param completeGpsData
     */
    private void buildGpsData(Element root, List<CompleteGpsData> list) {
        Element completeGpsData;
        for (CompleteGpsData el : list) {
            completeGpsData = doc.createElement(el.getType());
            root.appendChild(completeGpsData);
            createNewElement(completeGpsData, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(completeGpsData, el.getTimeiInXMLformat(el.getDeliveryTime()), LoaderConstants.DELIVERY_TIME);
            createNewElement(completeGpsData, el.getLocation(), LoaderConstants.GPS_SENTENCE);
        }
    }

    private void buildEventElement(Element root, List<EventsElement> list) {
        Element eventElement;
        for (EventsElement event : list) {
            eventElement = doc.createElement(event.getType());
            root.appendChild(eventElement);
            if (event.getTocttc() instanceof TOCElement) {
                buildTocElement(eventElement, (TOCElement)event.getTocttc());
            } else if (event.getTocttc() instanceof TTCElement) {
                buildTtcElement(eventElement, (TTCElement)event.getTocttc());
            }

            buildGroupAttachElement(eventElement, event.getGroupAttach());
            if (event.getSendRecieveMsg() instanceof SendMsg) {
                buildSendMsg(eventElement, (SendMsg)event.getSendRecieveMsg());
            } else if (event.getSendRecieveMsg() instanceof RecieveMsg) {
                buildReciveMsg(eventElement, (RecieveMsg)event.getSendRecieveMsg());
            }

            buildCellReselectionElement(eventElement, event.getCellReselection());
            buildItsiAttacnElement(eventElement, event.getItsiAttach());
            buildHandoverElement(eventElement, event.getHandover());
        }
    }

    /**
     * @param eventElement
     * @param cellReselection
     */
    private void buildCellReselectionElement(Element eventElement, CellReselection cellReselection) {
        Element cell;
        if (cellReselection != null) {
            cell = doc.createElement(cellReselection.getType());
            eventElement.appendChild(cell);
            createNewElement(cell, cellReselection.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(cell, cellReselection.getLocationAreaAfter(), LoaderConstants.LOCATION_AREA_AFTER);
            createNewElement(cell, cellReselection.getLocationAreaBefore(), LoaderConstants.LOCATION_AREA_BEFORE);
            createNewElement(cell, cellReselection.getTimeiInXMLformat(cellReselection.getCellReselAccept()),
                    LoaderConstants.CELL_RESEL_ACCEPT);
            createNewElement(cell, cellReselection.getTimeiInXMLformat(cellReselection.getCellReselReq()),
                    LoaderConstants.CELL_RESEL_REQ);
            buildInconclusive(cell, cellReselection.getInconclusive());
        }
    }

    /**
     * @param eventElement
     * @param handover
     */
    private void buildHandoverElement(Element root, Handover handover) {
        Element hand;
        if (handover != null) {
            hand = doc.createElement(handover.getType());
            root.appendChild(hand);
            createNewElement(hand, handover.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(hand, handover.getLocationAreaAfter(), LoaderConstants.LOCATION_AREA_AFTER);
            createNewElement(hand, handover.getLocationAreaBefore(), LoaderConstants.LOCATION_AREA_BEFORE);
            createNewElement(hand, handover.getTimeiInXMLformat(handover.getHoAccept()), LoaderConstants.HO_ACCEPT);
            createNewElement(hand, handover.getTimeiInXMLformat(handover.getHoReq()), LoaderConstants.HO_REQ);
            buildInconclusive(hand, handover.getInconclusive());
        }
    }

    /**
     * @param eventElement
     * @param itsiAttach
     */
    private void buildItsiAttacnElement(Element root, ItsiAttach it) {
        Element itsi;
        if (it != null) {
            itsi = doc.createElement(it.getType());
            root.appendChild(itsi);
            createNewElement(itsi, it.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(itsi, it.getLocationAreaAfter(), LoaderConstants.LOCATION_AREA_AFTER);
            createNewElement(itsi, it.getLocationAreaBefore(), LoaderConstants.LOCATION_AREA_BEFORE);
            createNewElement(itsi, it.getTimeiInXMLformat(it.getItsiAccept()), LoaderConstants.ITSI_ATT_ACCEPT);
            createNewElement(itsi, it.getTimeiInXMLformat(it.getItsiAttReq()), LoaderConstants.ITSI_ATT_REQ);
        }
    }

    /**
     * @param eventElement
     * @param recieveMsgs
     */
    private void buildReciveMsg(Element root, RecieveMsg el) {
        Element reciveMsg;
        if (el != null) {
            reciveMsg = doc.createElement(el.getType());
            root.appendChild(reciveMsg);
            createNewElement(reciveMsg, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(reciveMsg, el.getCallingNumber(), LoaderConstants.CALLING_NUMBER);
            createNewElement(reciveMsg, el.getDataLength(), LoaderConstants.DATA_LENGTH);
            createNewElement(reciveMsg, el.getDataTxt(), LoaderConstants.DATA_TXT);
            createNewElement(reciveMsg, el.getMsgType(), LoaderConstants.MSG_TYPE);
            createNewElement(reciveMsg, el.getTimeiInXMLformat(el.getSendTime()), LoaderConstants.SEND_TIME);
            buildInconclusive(reciveMsg, el.getInconclusive());
        }
    }

    /**
     * @param eventElement
     * @param sendMsgs
     */
    private void buildSendMsg(Element root, SendMsg el) {
        Element senMsg;
        if (el != null) {
            senMsg = doc.createElement(el.getType());
            root.appendChild(senMsg);
            createNewElement(senMsg, el.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(senMsg, el.getCalledNumber(), LoaderConstants.CALLED_NUMBER);
            createNewElement(senMsg, el.getDataLength(), LoaderConstants.DATA_LENGTH);
            createNewElement(senMsg, el.getDataTxt(), LoaderConstants.DATA_TXT);
            createNewElement(senMsg, el.getMsgType(), LoaderConstants.MSG_TYPE);
            createNewElement(senMsg, el.getTimeiInXMLformat(el.getSendTime()), LoaderConstants.SEND_TIME);
            buildInconclusive(senMsg, el.getInconclusive());
            buildSendReport(senMsg, el.getSendReport());
        }
    }

    private void buildSendReport(Element root, List<SendReport> list) {
        Element sendReport;
        for (SendReport sr : list) {
            sendReport = doc.createElement(sr.getType());
            root.appendChild(sendReport);
            createNewElement(sendReport, sr.getTimeiInXMLformat(sr.getReportTime()), LoaderConstants.REPORT_TIME);
            createNewElement(sendReport, sr.getStatus(), LoaderConstants.STATUS);
        }

    }

    private void buildGroupAttachElement(Element root, GroupAttach ga) {
        Element groupAttachElement;
        if (ga != null) {
            groupAttachElement = doc.createElement(ga.getType());
            root.appendChild(groupAttachElement);
            createNewElement(groupAttachElement, ga.getProbeId(), LoaderConstants.PROBE_ID);
            createNewElement(groupAttachElement, ga.getTimeiInXMLformat(ga.getGroupAttachTime()), LoaderConstants.GROUP_ATTACH_TIME);
            buildAttachmentElement(groupAttachElement, ga.getAttachment());
        }
    }

    private void buildAttachmentElement(Element root, List<Attachment> list) {
        Element attachment;
        for (Attachment att : list) {
            attachment = doc.createElement(att.getType());
            root.appendChild(attachment);
            createNewElement(attachment, att.getGroupType(), LoaderConstants.GROUP_TYPE);
            createNewElement(attachment, att.getGssi(), LoaderConstants.GSSI);
        }
    }

    private void buildTocElement(Element root, TOCElement t) {
        Element newElement;
        newElement = doc.createElement(t.getType());
        root.appendChild(newElement);

        createNewElement(newElement, t.getProbeID(), LoaderConstants.PROBE_ID);
        createNewElement(newElement, t.getCalledNumber(), LoaderConstants.CALLED_NUMBER);
        createNewElement(newElement, t.getHook(), LoaderConstants.HOOK);
        createNewElement(newElement, t.getSimplex(), LoaderConstants.SIMPLEX);
        createNewElement(newElement, t.getPriority(), LoaderConstants.PRIORITY);

        createNewElement(newElement, t.getTimeiInXMLformat(t.getConfigTime()), LoaderConstants.CONFIG_TIME);
        createNewElement(newElement, t.getTimeiInXMLformat(t.getSetupTime()), LoaderConstants.SETUP_TIME);
        createNewElement(newElement, t.getTimeiInXMLformat(t.getConnectTime()), LoaderConstants.CONNECT_TIME);
        createNewElement(newElement, t.getTimeiInXMLformat(t.getDisconnectTime()), LoaderConstants.DISCONNECT_TIME);
        createNewElement(newElement, t.getTimeiInXMLformat(t.getReleaseTime()), LoaderConstants.RELEASE_TIME);
        createNewElement(newElement, t.getCauseForTermination(), LoaderConstants.CAUSE_FOR_TERMINATION);

        buildPESQResult(newElement, t.getPesqResult());
        buildInconclusive(newElement, t.getInconclusive());

    }

    private void buildTtcElement(Element root, TTCElement t) {
        Element newElement;
        newElement = doc.createElement(t.getType());
        root.appendChild(newElement);

        createNewElement(newElement, t.getProbeID(), LoaderConstants.PROBE_ID);
        createNewElement(newElement, t.getCallingNumber(), LoaderConstants.CALLED_NUMBER);
        createNewElement(newElement, t.getHook(), LoaderConstants.HOOK);
        createNewElement(newElement, t.getSimplex(), LoaderConstants.SIMPLEX);

        createNewElement(newElement, t.getTimeiInXMLformat(t.getAnswerTime()), LoaderConstants.ANSWER_TIME);
        createNewElement(newElement, t.getTimeiInXMLformat(t.getIndicationTime()), LoaderConstants.INDICATION_TIME);
        createNewElement(newElement, t.getTimeiInXMLformat(t.getConnectTime()), LoaderConstants.CONNECT_TIME);
        createNewElement(newElement, t.getTimeiInXMLformat(t.getReleaseTime()), LoaderConstants.RELEASE_TIME);
        createNewElement(newElement, t.getCauseForTermination(), LoaderConstants.CAUSE_FOR_TERMINATION);

        buildPESQResult(newElement, t.getPesqResult());
        buildInconclusive(newElement, t.getInconclusive());

    }

    private void buildInconclusive(Element root, InconclusiveElement t) {
        Element newElement;
        if (t != null) {
            newElement = doc.createElement(t.getType());
            root.appendChild(newElement);
            createNewElement(newElement, t.getReason(), LoaderConstants.REASON);
            createNewElement(newElement, t.getErrCode(), LoaderConstants.ERR_CODE);
        }

    }

    private void buildPESQResult(Element root, List<PESQResultElement> pesqResultList) {
        Element pesq;
        for (PESQResultElement el : pesqResultList) {
            pesq = doc.createElement(el.getType());
            root.appendChild(pesq);
            createNewElement(pesq, el.getTimeiInXMLformat(el.getSendSampleStart()), LoaderConstants.SEND_SAMPLE_START);
            createNewElement(pesq, el.getPesq(), LoaderConstants.PESQ);
            createNewElement(pesq, el.getDelay(), LoaderConstants.DELAY);
        }
    }

    private void createNewElement(Element root, Object t, String property) {
        Element child;
        child = doc.createElement(property);
        root.appendChild(child);
        if (t != null) {
            text = doc.createTextNode(t.toString());
            child.appendChild(text);
        }
        root.appendChild(child);

    }

    /**
     * set Path to xml directory
     * 
     * @param directory
     */
    public void setDirectoryPath(String directory) {
        if (directory != null) {
            this.directory = directory;
        }
    }

    /**
     * set name of xml file
     * 
     * @param fileName
     */
    public void setFileName(String fileName) {
        if (fileName != null) {
            this.fileName = fileName;
        }
    }

    /**
     * save builded xml Doc into a tree
     */
    public void saveFile() {
        Source domSource = new DOMSource(doc);
        Result fileResult;
        try {
            FileWriter fw = new FileWriter(new File(directory + fileName + extension));
            fileResult = new StreamResult(fw);
        } catch (IOException e1) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");;

            transformer.transform(domSource, fileResult);
        } catch (TransformerException e) {
            // TODO Handle TransformerException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public File getSavedFile() {
        return savedFile;
    }

    public void setSavedFile(File savedFile) {
        this.savedFile = savedFile;
    }
}
