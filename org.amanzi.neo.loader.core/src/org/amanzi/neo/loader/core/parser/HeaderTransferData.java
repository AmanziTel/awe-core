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

package org.amanzi.neo.loader.core.parser;

import java.util.LinkedHashMap;

import org.amanzi.neo.loader.core.IMapBasedDataElement;

/**
 * <p>
 * TransferData containse information about headers (key - header(column name), value - value from table)
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class HeaderTransferData extends LinkedHashMap<String, String>implements IMapBasedDataElement<String,String>  {

    /** long serialVersionUID field. */
    private static final long serialVersionUID = -5686953917241479358L;
    
    /** The project name. */
    private String projectName;
    
    /** The root name. */
    private String rootName;
    
    /** The file name. */
    private String fileName;
    
    /**
     * Gets the project name.
     *
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Sets the project name.
     *
     * @param projectName the new project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * Gets the root name.
     *
     * @return the root name
     */
    public String getRootName() {
        return rootName;
    }
    
    /**
     * Sets the root name.
     *
     * @param rootName the new root name
     */
    public void setRootName(String rootName) {
        this.rootName = rootName;
    }
    
    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    

}
