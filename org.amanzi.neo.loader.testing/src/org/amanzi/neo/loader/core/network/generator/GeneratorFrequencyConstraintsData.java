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
public class GeneratorFrequencyConstraintsData {
    private static final ArrayList<String> NAMES = new ArrayList<String>();
    
    public GeneratorFrequencyConstraintsData() {
        NAMES.add("BTS_Name");
        NAMES.add("BCCH");
        for (int i = 2; i < 7; i++) {
            NAMES.add("TRX" + i);
        }
    }
    /**
     * @param args args[0] - inputFile, args[1] - outputFile
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        new GeneratorFrequencyConstraintsData();
        
        generate(args[0], args[1]);
    }
    
    private static void generate(String inputFile, String outputFile) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(inputFile)), (char)9);
        
        Integer[] neededIndexes = new Integer[7]; 
        Map<String, ArrayList<String>> datas = new HashMap<String, ArrayList<String>>();

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
        int m = 0;
        while (true) {
            data = reader.readNext();
            m++;
            System.out.println(m);
            if (data == null) 
                break;
            
                String name = data[neededIndexes[0]];
                System.out.println(name);
                ArrayList<String> trxes = new ArrayList<String>();
                for (int i = 1; i < 7; i++) {
                    String value = data[neededIndexes[i]];
                    if (value.equals("")) {
                        // nothing adding
                    } 
                    else {
                        trxes.add(value);
                    }
                }
                if (!name.equals("")) {
                    if (datas.containsKey(name)) {
                        ArrayList<String> oldTrxes = datas.get(name);
                        oldTrxes.addAll(trxes);
                        datas.put(name, oldTrxes);
                    }
                    else {
                        datas.put(name, trxes);
                    } 
                }
//            sectorNames.add(data[neededIndex]);
//            System.out.println(data[neededIndex]);
        }
        
        CSVFile file = new CSVFile(new File(outputFile));
        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Sector");
        headers.add("TRX_ID");
        headers.add("ChannelType");
        headers.add("Frequency");
        headers.add("Type");
        headers.add("Penalty");
        
        file.writeHeaders(headers);
        
        Iterator<String> iterator = datas.keySet().iterator();
        ArrayList<String> trxData = new ArrayList<String>();
        Iterator<String> trxDataIterator = trxData.iterator();
        
        ArrayList<String> values = new ArrayList<String>();
        
        String sectorName = null;
        for (int i = 0; i < datas.size(); i++) {
            sectorName = iterator.next();
            trxData = datas.get(sectorName);
            trxDataIterator = trxData.iterator();
            for (int j = 0; j < trxData.size(); j++) {
                values.add(sectorName);
                values.add(MyRandom.randomCurrentIntOrStar(trxDataIterator.next()));                   //trxId
                values.add(MyRandom.randomChannelType());                       //channelType
                values.add(Long.toString(MyRandom.randomLong(0, 1023)));        //frequency
                values.add(Byte.toString(MyRandom.randomBooleanInteger()));     //type
                values.add(Double.toString(MyRandom.randomDouble(0, 100, 2)));  //penalty
                file.writeData(values);
                values.clear();
            }
        }
        
        file.close();
    }

}
