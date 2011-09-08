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
import java.io.InputStreamReader;
import java.net.URL;

/**
 * <p>
 * NemoFileGeneratorLauncher
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class NemoFileGeneratorLauncher {

    /**
     * The main method.
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Started.");

            URL url = new URL("http://gritinnovation.com/misc/geoptima/DownloadLogs_plain.php");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();

            // NemoDataGenerator dataGenerator = new NemoDataGenerator("1.86.00", 100500);
            // String result = dataGenerator.generate();
            // FileWriter ryt = new FileWriter("D:/nemo" + System.currentTimeMillis() + ".dt1");
            // BufferedWriter out = new BufferedWriter(ryt);
            // out.write(result);
            // out.close();

            System.out.println("Finished.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Aborted.");
        }
    }    
}
