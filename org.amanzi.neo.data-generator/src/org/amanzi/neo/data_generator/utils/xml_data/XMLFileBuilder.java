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

package org.amanzi.neo.data_generator.utils.xml_data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * Abstract file builder for all xml files.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class XMLFileBuilder {
    
    /**
     * Save xml file.
     *
     * @param path String (path to save file)
     * @param fileName String (file name)
     * @param rootTag SavedTag (root of all tags)
     * @throws IOException (problem  in file saving)
     */
    protected void saveFile(String path, String fileName, SavedTag rootTag) throws IOException{
        File file = initFile(path,fileName);        
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter out = new PrintWriter(fos);
        try{
            out.println(getPrefix());
            printTag(rootTag, out,"");
        }
        finally{
            out.flush();
            out.close();
        }
    }

    /**
     * Initialize path.
     *
     * @return File
     */
    private File initFile(String path, String fileName)throws IOException{
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
     * Print tag with inner tags.
     *
     * @param tag SavedTag (tag for print)
     * @param out PrintWriter
     */
    private void printTag(SavedTag tag, PrintWriter out, String tabs){
        out.println(tabs+tag.getTagOpenString());
        if (!tag.isEmpty()&&tag.getData()==null) {
            for (SavedTag inner : tag.getInnerTags()) {
                printTag(inner, out, tabs+"\t");
            }
            out.println(tabs+tag.getTagCloseString());
        }
    }
    
    /**
     * @return file prefix.
     */
    protected abstract String getPrefix();
}
