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

package org.amanzi.neo.data_generator.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.data.calls.ProbeData;

/**
 * Save call data to files.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class FileBuilder {
    
    private static final String PAIR_DIRECTORY_PREFIX = "NA";
    private static final String PAIR_DIRECTORY_SEPARATOR = "_";
    private static final String PAIR_DIRECTORY_PROBE = "P";
    private static final String PAIR_DIRECTORY_PROBE_SEPARATOR = "-";
    
    private static final String PROBE_FILE_PREFIX = "PROBE";
    private static final String PROBE_FILE_SEPARATOR = "#";
    private static final String PROBE_FILE_POSTFIX = "2";
    private static final String PROBE_FILE_EXTENSION = ".log";
    
    private String path;
    private String pairDirectoryPostfix;
    
    /**
     * Constructor.
     * @param aPath String (path to save)
     * @param postfix String (pair directory postfix)
     */
    public FileBuilder(String aPath, String postfix){
        path = aPath;
        pairDirectoryPostfix = postfix;
    }

    /**
     * Saves call data.
     *
     * @param aData list of CallPairs.
     * @throws IOException (problem with file creating)
     */
    public void saveData(List<CallGroup> aData)throws IOException{
       File mainPath = initPath();
       for(CallGroup group : aData){
           String firstProbe = group.getFirstName();
           File pairDir = initPairDirectory(mainPath, firstProbe, group.getReceiverNames());
           for(CallData call : group.getData()){
               File callDir = getCallDirectory(pairDir, call.getKey());
               buildCallFile(call.getSourceProbe(), callDir);
               for(ProbeData receiverProbe : call.getReceiverProbes()){
                   buildCallFile(receiverProbe, callDir);
               }               
           }
       }
    }
    
    /**
     * Initialize path.
     *
     * @return File
     */
    private File initPath(){
        File result = new File(path);
        if(result.exists()){
            if(result.isDirectory()){
                return result;
            }
            throw new IllegalArgumentException("Path <"+path+"> is not directory!");
        }
        result.mkdir();
        return result;
    }
    
    /**
     * Initialize directory for call pair. 
     *
     * @param aPath File
     * @param probe1 String (first probe name)
     * @param probe2 String (second probe name)
     * @return File
     */
    private File initPairDirectory(File aPath, String probe1, List<String> probe2){
        String dirName = buildCallDirectoryName(probe1, probe2);
        return initNewDirectory(aPath, dirName);
    }
    
    /**
     * Form name of directory for call pair.
     *
     * @param probe1 String (first probe name)
     * @param probe2 String (second probe name)
     * @return String
     */
    private String buildCallDirectoryName(String probe1, List<String> probe2){
        StringBuilder result = new StringBuilder(PAIR_DIRECTORY_PREFIX)
                                            .append(getPairDirNumber())
                                            .append(PAIR_DIRECTORY_SEPARATOR)
                                            .append(PAIR_DIRECTORY_PROBE).append(probe1);
        for(String curProbe : probe2){
            result.append(PAIR_DIRECTORY_PROBE_SEPARATOR)
            .append(PAIR_DIRECTORY_PROBE).append(curProbe);
        }
        result.append(PAIR_DIRECTORY_SEPARATOR)
              .append(pairDirectoryPostfix);             
        return result.toString();
    }
    
    /**
     * Gets number of pair directory.
     *
     * @return int.
     */
    private int getPairDirNumber(){
        return 0;
    }
    
    /**
     * Initialize directory for call. 
     *
     * @param aPath File
     * @param callKey Long
     * @return File
     */
    private File getCallDirectory(File aPath,Long callKey){
        return initNewDirectory(aPath, callKey.toString());
    }
    
    /**
     * Build file for probe.
     *
     * @param data ProbeData (log data)
     * @param directory File
     * @throws IOException (problem with file creating)
     */
    private void buildCallFile(ProbeData data, File directory)throws IOException{
        String fileName = buildFileName(data.getName(), data.getKey());
        File file = new File(directory,fileName);
        if(file.exists()){
            throw new IllegalStateException("Dublicate file name <"+fileName+">.");
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter out = new PrintWriter(fos);
        try{
            for(CommandRow row : data.getCommands()){
               out.println(row.getCommandAsString()); 
            }
        }
        finally{
            out.flush();
            out.close();
        }
    }
    
    /**
     * Form name of probe file.
     *
     * @param probeName String (probe name)
     * @param key Long
     * @return String
     */
    private String buildFileName(String probeName, Long key){
        StringBuilder result = new StringBuilder(PROBE_FILE_PREFIX).append(probeName)
                                    .append(PROBE_FILE_SEPARATOR).append(key)
                                    .append(PROBE_FILE_SEPARATOR).append(PROBE_FILE_POSTFIX)
                                    .append(PROBE_FILE_EXTENSION);
        return result.toString();
    }
    
    /**
     * Create new directory.
     * If directory with this name exists, than throws {@link IllegalStateException#}.
     *
     * @param aPath File
     * @param dirName String (directory name)
     * @return File
     */
    private File initNewDirectory(File aPath,String dirName){
        File result = new File(aPath,dirName);
        if(result.exists()){
            throw new IllegalStateException("Dublicate directory name <"+dirName+">.");
        }
        result.mkdir();
        return result;
    }
}
