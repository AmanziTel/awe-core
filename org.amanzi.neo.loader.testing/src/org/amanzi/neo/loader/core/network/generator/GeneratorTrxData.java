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

package org.amanzi.neo.loader.core.network.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class GeneratorTrxData {
    private static final ArrayList<String> NAMES = new ArrayList<String>();
    
    public GeneratorTrxData() {
        NAMES.add("BTS_Name");
        NAMES.add("Ant_Freq_Band");
    }
    /**
     * @param args args[0] - inputFile, args[1] - outputFile
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        new GeneratorTrxData();
        
        generate(args[0], args[1]);
    }
    
    private static void generate(String inputFile, String outputFile) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(inputFile)), (char)9);
        
        Integer[] neededIndexes = new Integer[2]; 
        Map<String, String> datas = new HashMap<String, String>();

        String[] data = reader.readNext();
        int k = 0;
        for (String name : NAMES) {
            for (int i = 0; i < data.length; i++) {
                if (data[i].equals(name)) {
                    neededIndexes[k++] = i;
                    break;
                }
            }
        }
        String sectorName = null, band = null;
        while (true) {
            data = reader.readNext();
            if (data == null) 
                break;
            
            sectorName = data[neededIndexes[0]];
            System.out.println(sectorName);
            band = data[neededIndexes[1]].substring(4);
            
            if (!sectorName.equals("")) {
                datas.put(sectorName, band); 
            }
        }
        
        CSVFile file = new CSVFile(new File(outputFile));
        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Sector");
        headers.add("Subcell");
        headers.add("TRX_ID");
        headers.add("Band");
        headers.add("Extended");
        headers.add("HoppingType");
        headers.add("BCCH");
        headers.add("HSN");
        headers.add("MAIO");
        for (int i = 1; i < 64; i++) {
            headers.add("ARFCN" + i);
        }
        
        file.writeHeaders(headers);
        
        Iterator<String> iteratorSectorName = datas.keySet().iterator();
        Iterator<String> iteratorBand = datas.values().iterator();
        
        ArrayList<String> values = new ArrayList<String>();
        
        for (int i = 0; i < datas.size(); i++) {
            sectorName = iteratorSectorName.next();
            band = iteratorBand.next();
            values.add(sectorName);                                 // sector
            values.add(MyRandom.randomString(3, 4));                // subcell
            values.add(Long.toString(MyRandom.randomLong(1, 16)));  // TRX_ID
            values.add(band);                                       // band
            if (band != null && Integer.parseInt(band) == 900) {
                values.add(MyRandom.randomExtended());              // extended
            }
            else {
                values.add(null);
            }
            values.add(Long.toString(MyRandom.randomLong(0, 2)));   // hopping type
            values.add(Integer.toString(MyRandom.randomBooleanInteger())); // BCCH
            Integer intOrNull = 0;
            // HSN and MAIO
            for (int m = 0; m < 2; m++) {
                intOrNull = MyRandom.randomIntOrNULL(0, 63);
                if (intOrNull == null) {
                    values.add("N/A");  
                }
                else {
                    values.add(Integer.toString(intOrNull));
                }
            }
            // array of ARFCN
            for (int j = 1; j < 64; j++) {
                intOrNull = MyRandom.randomIntOrNULL(0, 1023);
                if (intOrNull == null) {
                    values.add("N/A");  
                }
                else {
                    values.add(Integer.toString(intOrNull));
                }
            }
            
            file.writeData(values);
            values.clear();
        }
        
        file.close();
    }
}
