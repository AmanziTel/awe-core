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

package org.amanzi.neo.loader.core;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 *Utilits methods for loaders
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class LoaderUtils {
    /**
     * Convert dBm values to milliwatts
     * 
     * @param dbm
     * @return milliwatts
     */
    public static final double dbm2mw(int dbm) {
        return Math.pow(10.0, ((dbm) / 10.0));
    }
    /**
     * Convert milliwatss values to dBm
     * 
     * @param milliwatts
     * @return dBm
     */
    public static final float mw2dbm(double mw) {
        return (float)(10.0 * Math.log10(mw));
    }
    /**
     * Calculates list of files 
     *
     * @param directoryName directory to import
     * @param filter - filter (if filter teturn true for directory this directory will be handled also  )
     * @return list of files to import
     */
    public static List<File> getAllFiles(String directoryName, FileFilter filter) {
        File directory = new File(directoryName);
        return getAllFiles(directory,filter);
    }
    /**
     * Calculates list of files 
     *
     * @param directory -  directory to import
     * @param filter - filter (if filter teturn true for directory this directory will be handled also  )
     * @return list of files to import
     */
    public static List<File> getAllFiles(File directory, FileFilter filter) {
        LinkedList<File> result = new LinkedList<File>();
        for (File childFile : directory.listFiles(filter)) {
            if (childFile.isDirectory()) {
                result.addAll(getAllFiles(childFile,filter));
            }
            else  {
                result.add(childFile);
            }
        }
        return result;
    }
    /**
     * get file extension
     *
     * @param fileName - file name
     * @return file extension
     */
    public static String getFileExtension(String fileName) {
        int idx = fileName.lastIndexOf(".");
        return idx < 1 ? "" : fileName.substring(idx);
    }



    public static File getFirstFile(String dirName) {
        File file = new File(dirName);
        if (file.isFile()){
            return file;
        }
        File[] list = file.listFiles();
        if (list.length>0){
            return list[0];
        }else{
            //TODO optimize
          List<File> all = getAllFiles(dirName, new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return true;
                }
            });
          if (all.isEmpty()){
              return null;
          }else{
              return all.iterator().next();
          }
        }
    }
}
