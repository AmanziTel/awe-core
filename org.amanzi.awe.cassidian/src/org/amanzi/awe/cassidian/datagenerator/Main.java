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

package org.amanzi.awe.cassidian.datagenerator;

import java.text.ParseException;

import javax.xml.transform.TransformerException;

import org.amanzi.awe.cassidian.loader.parser.AMSXMLParser;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.amanzi.awe.cassidian.writer.AMSXMLWriter;

public class Main {

    /**
     * @param args
     * @throws TransformerException
     * @throws ParseException
     */
    public static void main(String[] args) throws TransformerException, ParseException {
        // // TODO Auto-generated method stub
        String DIRECTORY_PATH = "/home/volad/.xmltest2/";
        String FILE_NAME = "testxml";
        DataGenerator dg = new DataGenerator();
        EventsElement evEl = dg.generateEventsElement();
        TNSElement tns = new TNSElement();
        tns.addMembertoEventsList(evEl);
        tns.addMembertoEventsList(dg.generateEventsElement());
        tns.addMembertoGPSList(dg.generateGPSDATA());
        tns.addMembertoCommonTestList(dg.generateCommonTestData());
        AMSXMLWriter xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME);
        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();
        AMSXMLParser parser = new AMSXMLParser();
        TNSElement parsedTns = parser.parse(DIRECTORY_PATH + FILE_NAME + ".xml");
        for (EventsElement ev : parsedTns.getEvents()) {
            System.out.println(ev.getTOCList().get(0).getProbeID());
            System.out.println(ev.getTTCList().get(0).getProbeID());
        }

        for (GPSData ev : parsedTns.getGps()) {
            System.out.println(ev.getCompleteGpsDataList().get(0).getCompleteGpsData().get(0).getProbeId());
            System.out.println(ev.getCompleteGpsDataList().get(0).getCompleteGpsData().get(1).getProbeId());
            System.out.println(ev.getCompleteGpsDataList().get(0).getCompleteGpsData().get(0).getLat());
            System.out.println(ev.getCompleteGpsDataList().get(0).getCompleteGpsData().get(0).getLon());
            System.out.println(ev.getCompleteGpsDataList().get(0).getCompleteGpsData().get(1).getLon());
            System.out.println(ev.getCompleteGpsDataList().get(0).getCompleteGpsData().get(1).getLon());
        }
    }
}
