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

package org.amanzi.neo.data_generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * <p>
 * GSMFileGenerator
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GSMFileGenerator {
    private static final String[] targetHeaders = new String[] {"S.No.", "TimeStamp", "Mobile Number", "IMEI", "IMSI", "HandSet", "Model", "CurrentPLMN", "Manufacturer",
            "BatteryPercentage", "SignalStrength", "CellId", "Lac", "Mcc", "Mnc", "RxQuality", "Latitude", "Longitude", "lastCallStatus", "lastCallStartTime",
            "lastCallEndTime", "RadioLinkAvailability"};
    private static final String[] mobileManufacturer = new String[] {"Samsung", "Nokia", "HTC", "Sony Ericsson", "Motorola", "etc"};
    private static final String[] mobNum = new String[] {"19258756123", "24687531223", "12355231020", "17886986210", "15442100030"};
    private static final String[] IMEI = new String[] {"354731020630622", "123679800364498", "703144571103630", "787435178954234", "741000322256030"};
    private static final String[] IMSI = new String[] {"310410291513807", "456546870646566", "879809456549680", "406346434547840", "121320136846879"};

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Started.");
        try {
            // String filename = args[0];
            String filename = "C:/1.txt";
            FileInputStream is;
            is = new FileInputStream(new File(filename));
            String characterSet = "UTF-8";
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, characterSet));

            String line = reader.readLine();
            String[] headers = line.split("\t");
            int cellIdNum = -1;
            int latNum = -1;
            int lonNum = -1;
            int lacNum = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase("CellId")) {
                    cellIdNum = i;
                }
                if (headers[i].equalsIgnoreCase("SIT_wgs84_X")) {
                    latNum = i;
                }
                if (headers[i].equalsIgnoreCase("SIT_wgs84_Y")) {
                    lonNum = i;
                }
                if (headers[i].equalsIgnoreCase("La_LacId")) {
                    lacNum = i;
                }
            }
            Random generator = new Random();

            StringBuilder str2Write = new StringBuilder();
            for (String header : targetHeaders) {
                str2Write.append(header).append("\t");
            }
            int rowNum = 0;

            String pattern = "dd.MM.yyyy hh:mm:ss";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            Date testDate = new Date();

            long timestampDelta = 0;

            while ((line = reader.readLine()) != null) {
                str2Write.append("\n");
                String[] data = line.split("\t");
                // S.No.
                str2Write.append(++rowNum).append("\t");
                // TimeStamp
                str2Write.append(sf.format(new Date(testDate.getTime() + timestampDelta))).append("\t");
                timestampDelta += 1000;
                // Mobile Number
                int n = generator.nextInt(5);
                str2Write.append(mobNum[n]).append("\t");
                // IMEI
                str2Write.append(IMEI[n]).append("\t");
                // IMSI
                str2Write.append(IMSI[n]).append("\t");
                // HandSet
                int curManufacturer = generator.nextInt(mobileManufacturer.length);
                str2Write.append(mobileManufacturer[curManufacturer]).append("\t");
                // Model
                str2Write.append("MITs").append("\t");
                // CurrentPLMN
                str2Write.append("Cellcom").append("\t");
                // Manufacturer
                str2Write.append(mobileManufacturer[curManufacturer]).append("\t");
                // BatteryPercentage
                str2Write.append(generator.nextInt(100)).append("\t");
                // SignalStrength
                str2Write.append(100 - generator.nextInt(100)).append("\t");
                // CellId
                str2Write.append(data[cellIdNum]).append("\t");
                // Lac
                str2Write.append(data[lacNum]).append("\t");
                // Mcc
                str2Write.append("310").append("\t");
                // Mnc
                str2Write.append("410").append("\t");
                // RxQuality
                str2Write.append("14[raw data]").append("\t");
                // Longitude
                str2Write.append(new BigDecimal(data[lonNum]).add(new BigDecimal(generator.nextInt(6) - 2).divide(BigDecimal.valueOf(200)))).append("\t");
                // Latitude
                str2Write.append(new BigDecimal(data[latNum]).add(new BigDecimal(generator.nextInt(6) - 2).divide(BigDecimal.valueOf(200)))).append("\t");
                // lastCallStatus
                str2Write.append("Idle").append("\t");
                // lastCallStartTime
                str2Write.append("NA").append("\t");
                // lastCallEndTime
                str2Write.append("NA").append("\t");
                // RadioLinkAvailability
                str2Write.append("Available").append("\t");
            }

            // FileWriter ryt=new FileWriter(args[1]);
            FileWriter ryt = new FileWriter("C:/2.gps");
            BufferedWriter out = new BufferedWriter(ryt);
            out.write(str2Write.toString());
            out.close();
        } catch (Exception e) {
            System.out.println("Aborted:");
            e.printStackTrace();
            return;
        }
        System.out.println("Finished.");
    }
}
