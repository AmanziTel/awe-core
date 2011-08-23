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

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * <p>
 * Enum generator for data from file_format document
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NemoEnumGenerator {

    public static void main(String[] args) {

        System.out.println("Started.");
        try {
            // String filename = args[0];
            String targetFileName = "D:/result_enum.java";
            StringBuilder str2Write = new StringBuilder();

            // FileWriter ryt=new FileWriter(args[1]);
            FileWriter ryt = new FileWriter(targetFileName);
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
