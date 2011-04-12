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

package org.amanzi.awe.gpeh.console.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.amanzi.awe.gpeh.console.parser.FileElement;
import org.amanzi.awe.gpeh.console.parser.GpehParser;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class ProjectRun {

    /**
     *
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Program started!!!");
//        getFiles(args[0], args[1]);
        getFiles("d://–¿¡Œ“¿/GPEH/gpeh test 2/", "d://–¿¡Œ“¿/GPEHResult/result002_2");
    }
    
    private static void getFiles(String inputDirectory, String outputDirectory) throws IOException {
        File directory = new File(inputDirectory);
        GpehParser parser = new GpehParser();
        parser.initPossibleIds(outputDirectory);
        int countOfFiles = directory.listFiles().length;
        int currentCount = 0;
        System.out.println("Count of files to parse = " + countOfFiles);
        for (File file : directory.listFiles()) {
            System.out.println(file.getName() + " start parsing!");
            FileElement element = new FileElement(file, ".gz");
            File outputDir = new File(outputDirectory);
            outputDir.mkdir();
            parser.parseElement(element);
            currentCount++;
            System.out.println(file.getName() + " done!");
            double percent = new BigDecimal(((double)currentCount / (double)countOfFiles) * 100).setScale(2, RoundingMode.UP).doubleValue();

            System.out.println(percent + " % HOLDS...");
        }
        parser.closeAllFiles();
        System.out.println("Parsing is executed!");
    }

}
