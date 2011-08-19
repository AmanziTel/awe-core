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

package org.amanzi.awe.cassidian.tests;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.loader.FileLoader;
import org.amanzi.awe.cassidian.loader.parser.AMSXMLParser;
import org.amanzi.awe.cassidian.structure.*;
import org.amanzi.awe.cassidian.datagenerator.DataGenerator;
import org.amanzi.awe.cassidian.writer.AMSXMLWriter;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class Tests {
    private static DataGenerator dg;
    private static FileLoader fileloader;
    private static File rootDirectory;
    private static final String DIRECTORY_PATH = "/home/volad/.xmltest/";
    private static final String FILE_NAME = "testxml";
    private AMSXMLWriter xmlWriter;
    private static List<File> xmlFiles;
    public static final Pattern COORD_PAT_LAT = Pattern.compile("^(\\d{2})(\\d{2}\\.\\d{4,5})$");
    public static final Pattern COORD_PAT_LON = Pattern.compile("^(\\d{3})(\\d{2}\\.\\d{4,5})$");

    @BeforeClass
    public static void initVariables() throws IOException {
        prepareDirectory();
        fileloader = new FileLoader(DIRECTORY_PATH);
        rootDirectory = fileloader.getRoot();

        dg = new DataGenerator();
    }

    private static void prepareDirectory() throws IOException {
        File file = new File(DIRECTORY_PATH);
        deleteDirectory(file);
        createTempRootDirectory();
    }

    private static void deleteDirectory(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                deleteDirectory(f);
            file.delete();
        } else {
            file.delete();
        }
    }

    private static void createTempRootDirectory() throws IOException {
        File dir1 = new File(DIRECTORY_PATH);
        dir1.mkdir();
        File dir2 = new File(DIRECTORY_PATH + "tempDir1/");
        dir2.mkdir();
        new File(dir2.getAbsolutePath() + "/temp.temp").createNewFile();
        dir2 = new File(DIRECTORY_PATH + "tempDir2/");
        dir2.mkdir();
        new File(dir2.getAbsolutePath() + "/tempXML.xml").createNewFile();
        new File(DIRECTORY_PATH + "/tempXML2.xml").createNewFile();
    }

    private Double parseLat(String latStr) {
        final Matcher matcher = COORD_PAT_LAT.matcher(latStr);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60d;
        } else {

            return null;
        }
    }

    /**
     * Parse longitude.
     * 
     * @param latStr String
     * @return Double
     */
    private Double parseLon(String latStr) {
        final Matcher matcher = COORD_PAT_LON.matcher(latStr);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60d;
        } else {
            return null;
        }
    }

    @Test
    public void testCorrectNumberOfXMLSelection() {

        xmlFiles = fileloader.getRootsFile(rootDirectory);
        Assert.assertEquals("Invalid expected files count.", xmlFiles.size(), 2);
    }

    @Test
    public void testCreationOfXml() {
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME);
        TNSElement tns = new TNSElement();
        tns.addMembertoEventsList(dg.generateEventsElement());
        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();
        xmlFiles = fileloader.getRootsFile(rootDirectory);
        boolean consistFlag = false;
        for (File f : xmlFiles) {
            if (f.getName().equals(FILE_NAME + ".xml"))
                consistFlag = true;
        }
        Assert.assertTrue("File not found", consistFlag);
    }

    @Test
    public void testParsingOfXml() {
        Calendar calendar = Calendar.getInstance();
        xmlWriter = new AMSXMLWriter(DIRECTORY_PATH, FILE_NAME);
        TNSElement tns = new TNSElement();

        EventsElement event = new EventsElement();
        TOCElement toc = new TOCElement();
        toc.setCalledNumber("0123456789");
        toc.setCauseForTermination(1);
        toc.setConfigTime(calendar);
        toc.setConnectTime(calendar);
        toc.setDisconnectTime(calendar);
        toc.setHook(2);
        toc.setPriority(3);
        toc.setProbeID("Probe1");
        toc.setReleaseTime(calendar);
        toc.setSetupTime(calendar);
        toc.setSimplex(4);

        PESQResultElement pesqres = new PESQResultElement();
        pesqres.setDelay(Long.parseLong("1111"));
        pesqres.setSendSampleStart(calendar);
        pesqres.setPesq(Float.parseFloat("33.0"));
        toc.addPesqMember(pesqres);
        event.addATTCTOC(toc);

        GPSData gpsData = new GPSData();
        CompleteGpsDataList cgdl = new CompleteGpsDataList();
        CompleteGpsData cgd = new CompleteGpsData();
        cgd.setDeliveryTime(calendar);
        cgd.setProbeId("Probe12");
        String type = "GPGLL";
        Double lat = 1234.5678;
        double expectedLat = parseLat(lat.toString());

        Double lon = 22333.1112;
        double expectedLon = parseLon(lon.toString());
        double minNorth = 12.0;
        double minWest = 13.0;
        double speed = 14.0;
        double courseMade = 15.0;
        double magnaticVariation = 100.0;
        int checkSum = 55;
        cgd.setLocation(type, lat, lon, calendar, minNorth, minWest, speed, courseMade, calendar, magnaticVariation, checkSum);
        cgdl.setValueByTagType(ChildTypes.COMPLEATE_GPS_DATA.getId(), cgd);
        gpsData.addMember(cgdl);

        tns.addMembertoEventsList(event);
        tns.addMembertoGPSList(gpsData);

        xmlWriter.buildTree(tns);
        xmlWriter.saveFile();

        AMSXMLParser parser = new AMSXMLParser();
        TNSElement tns2 = parser.parse(DIRECTORY_PATH + FILE_NAME + ".xml");
        TOCElement toc2 = (TOCElement)tns2.getEvents().get(0).getTOCList().get(0);
        CompleteGpsData cgd2 = tns2.getGps().get(0).getCompleteGpsDataList().get(0).getCompleteGpsData().get(0);

        Assert.assertEquals("Parsed and generated objects are not equals", toc.getProbeID(), toc2.getProbeID());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getCalledNumber(), toc2.getCalledNumber());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getCauseForTermination(),
                toc2.getCauseForTermination());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getConfigTime(), toc2.getConfigTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getConnectTime(), toc2.getConnectTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getDisconnectTime(), toc2.getDisconnectTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getHook(), toc2.getHook());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getPriority(), toc2.getPriority());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getReleaseTime(), toc2.getReleaseTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getSetupTime(), toc2.getSetupTime());
        Assert.assertEquals("Parsed and generated objects are not equals", toc.getSimplex(), toc2.getSimplex());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getDelay(), toc2.getPesqResult().get(0)
                .getDelay());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getPesq(), toc2.getPesqResult().get(0).getPesq());
        Assert.assertEquals("Parsed and generated objects are not equals", pesqres.getSendSampleStart(), toc2.getPesqResult()
                .get(0).getSendSampleStart());

        Assert.assertEquals("Parsed and generated objects are not equals", expectedLat, cgd2.getLat());
        Assert.assertEquals("Parsed and generated objects are not equals", expectedLon, -cgd2.getLon());
        Assert.assertEquals("Parsed and generated objects are not equals", cgd.getProbeId(), cgd2.getProbeId());
        Assert.assertEquals("Parsed and generated objects are not equals", cgd.getDeliveryTime(), cgd2.getDeliveryTime());
    }

}
