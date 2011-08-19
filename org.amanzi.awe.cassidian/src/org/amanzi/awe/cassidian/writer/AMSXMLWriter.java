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
import org.amanzi.awe.cassidian.structure.AbstractTOCTTC;
import org.amanzi.awe.cassidian.structure.CommonTestData;
import org.amanzi.awe.cassidian.structure.CompleteGpsData;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.PESQResultElement;
import org.amanzi.awe.cassidian.structure.ProbeIDNumberMap;
import org.amanzi.awe.cassidian.structure.ServingData;
import org.amanzi.awe.cassidian.structure.TNSElement;
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
    private String directory = ".home/volad/.xmltestDebug/";
    private String fileName = "xmlfile";
    private String extension = ".xml";

    public AMSXMLWriter() {
        // We need a Document
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
        // We need a Document
        dbfac = DocumentBuilderFactory.newInstance();
        this.directory = directoryPath;
        this.fileName = fileName;
        try {
            docBuilder = dbfac.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Handle ParserConfigurationException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        doc = docBuilder.newDocument();
    }

    /**
     * build TNSTree
     * 
     * @return
     */
    public Document buildTree(TNSElement tns) {
        // create the root element and add it to the document

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
        }
    }
	/**
	 * 
	 *
	 * @param commonTestData
	 * @param servingData
	 */
    private void buildServingData(Element root,
			List<ServingData> list) {
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
            buildTTCTOCElement(eventElement, event.getTOCList());
            buildTTCTOCElement(eventElement, event.getTTCList());

        }
    }

    private void buildTTCTOCElement(Element root, List<AbstractTOCTTC> list) {
        Element newElement;
        for (AbstractTOCTTC t : list) {
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
     * save buildet xml Doc into a tree
     */
    public void saveFile() {
        // System.out.println(ChildTypes.TOC.getTitle());
        Source domSource = new DOMSource(doc);
        Result fileResult = new StreamResult(new File(directory + fileName + extension));
        TransformerFactory factory = TransformerFactory.newInstance();

        try {
            Transformer transformer = factory.newTransformer();
            transformer.transform(domSource, fileResult);
        } catch (TransformerException e) {
            // TODO Handle TransformerException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
}
