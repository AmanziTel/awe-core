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

package org.amanzi.neo.loader.ui.validators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.IValidator;

/**
 * IValidator implementation contain methods for validation ams xml data
 * 
 * @author Kondratenko_Vladislav
 */
public class AMSXMLDataValidator implements IValidator {
    Result result;
    String message;

    /**
     * check correct file extension
     * 
     * @param fileToLoad
     * @return
     */
    private void checkFileExtension(List<File> fileToLoad, String extension) {

        List<File> tempFileToLoad = new LinkedList<File>(fileToLoad);
        for (File f : tempFileToLoad) {
            if (f.getName().lastIndexOf(extension) == -1) {
                String fileName = f.getName();
                message += fileName + " has incorrect extension\n";
                // System.out.println(fileName + " has incorrect extension");
                fileToLoad.remove(f);
            }
        }
    }

    @Override
    public boolean isAppropriate(File[] fileToLoad) {
        return false;
    }

    @Override
    public Result isValid(List<File> fileToLoad) {
        if (fileToLoad == null) {
            message = "select correct directory";

            return Result.FAIL;
        }
        checkFileExtension(fileToLoad, ".xml");
        checkFileContent(fileToLoad);
        if (fileToLoad.size() > 0) {
            return Result.SUCCESS;
        } else {
            return Result.FAIL;
        }
    }

    /**
     * check for file has correct format
     * 
     * @param fileToLoad
     */
    private void checkFileContent(List<File> fileToLoad) {

        try {
            List<File> tempFileToLoad = new LinkedList<File>(fileToLoad);
            for (File f : tempFileToLoad) {
                BufferedReader in = new BufferedReader(new FileReader(f));
                String str;
                for (int i = 0; i < 2; i++) {
                    str = in.readLine();
                    if (str == null || str.equals("")) {
                        message += f.getName() + " has incorrect format\n";
                        fileToLoad.remove(f);
                        break;
                    }
                    if (i == 1) {
                        if (str.indexOf("<tns") == -1) {
                            fileToLoad.remove(f);
                            break;
                        }
                    }
                }
                in.close();
            }
        }

        catch (IOException e) {
        }
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String getMessages() {
        return message;
    }
}
