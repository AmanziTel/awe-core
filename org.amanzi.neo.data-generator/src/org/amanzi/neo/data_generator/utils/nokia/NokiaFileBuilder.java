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

import java.io.IOException;

import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;
import org.amanzi.neo.data_generator.utils.xml_data.XMLFileBuilder;

/**
 * <p>
 * Data saver for generated data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class NokiaFileBuilder extends XMLFileBuilder{
    
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
     * Save generated data.
     *
     * @param aRoot SavedTag (root tag for data)
     */
    public void saveData(SavedTag aRoot)throws IOException{
        saveFile(path, fileName, aRoot);
    }

    @Override
    protected String getPrefix() {
        return FILE_PREFIX;
    }
    
}
