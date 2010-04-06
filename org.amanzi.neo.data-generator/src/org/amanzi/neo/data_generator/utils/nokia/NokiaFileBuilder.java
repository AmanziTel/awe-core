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

package org.amanzi.neo.data_generator.utils.nokia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * Data saver for generated data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class NokiaFileBuilder {
    
    private String path;
    private String fileName;
    
    private static String FILE_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE raml SYSTEM 'raml20.dtd'>";
    
    /**
     * Constructor.
     * @param aPath String (path to save file)
     * @param aFileName String (file name)
     */
    public NokiaFileBuilder(String aPath, String aFileName) {
        path = aPath;
        fileName = aFileName;
    }
    
    /**
     * Initialize path.
     *
     * @return File
     */
    private File initFile()throws IOException{
        File directory = new File(path);
        if(directory.exists()){
            if(!directory.isDirectory()){
                throw new IllegalArgumentException("Path <"+path+"> is not directory!");
            }
        }else{
            directory.mkdir();
        }        
        File file = new File(directory,fileName);
        if(file.exists()){
            throw new IllegalStateException("Dublicate file name <"+fileName+">.");
        }
        file.createNewFile();
        return file;
    }

    /**
     * Save generated data.
     *
     * @param aRoot SavedTag (root tag for data)
     */
    public void saveData(SavedTag aRoot)throws IOException{
        File file = initFile();        
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter out = new PrintWriter(fos);
        try{
            out.println(FILE_PREFIX);
            printTag(aRoot, out);
        }
        finally{
            out.flush();
            out.close();
        }
    }
    
    /**
     * Print tag with inner tags.
     *
     * @param tag SavedTag (tag for print)
     * @param out PrintWriter
     */
    private void printTag(SavedTag tag, PrintWriter out){
        out.println(tag.getTagOpenString());
        if (!tag.isEmpty()&&tag.getData()==null) {
            for (SavedTag inner : tag.getInnerTags()) {
                printTag(inner, out);
            }
            out.println(tag.getTagCloseString());
        }
    }
}
