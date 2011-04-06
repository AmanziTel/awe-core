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

package org.amanzi.awe.afp.testing.engine;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.amanzi.awe.afp.testing.Activator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class TestDataLocator {
    
    public static enum DataType {
        ERICSSON, GENERAL_FORMAT, GERMANY;
    }
    
    public static File getNetworkFile(DataType dataType) throws IOException {
        URL url = FileLocator.find(Activator.getDefault().getBundle(), getNetworkFilePath(dataType), null);
        
        return new File(FileLocator.toFileURL(url).getPath());        
    }
    
    private static Path getNetworkFilePath(DataType dataType) {
        return new Path("files" + File.separator + "afp_engine" + File.separator + dataType.name().toLowerCase() + File.separator + "Network");
    }
    
    private static Path getNeighbourFilePath(DataType dataType) {
        return new Path("files" + File.separator + "afp_engine" + File.separator + dataType.name().toLowerCase() + File.separator + "Neighbours");
    }
    
    public static File getNeighbourFile(DataType dataType) throws IOException {
        URL url = FileLocator.find(Activator.getDefault().getBundle(), getNeighbourFilePath(dataType), null);
        
        return new File(FileLocator.toFileURL(url).getPath());
    }
    
    private static Path getIMFilePath(DataType dataType) {
        return new Path("files" + File.separator + "afp_engine" + File.separator + dataType.name().toLowerCase() + File.separator + "IM");
    }
    
    public static File getIMFile(DataType dataType) throws IOException {
        URL url = FileLocator.find(Activator.getDefault().getBundle(), getIMFilePath(dataType), null);
        
        return new File(FileLocator.toFileURL(url).getPath());
    }
    
    private static Path getNetworkConfigDirectoryPath(DataType dataType) {
        return new Path("files" + File.separator + "afp_engine" + File.separator + dataType.name().toLowerCase() + File.separator + "Config");
    }
    
    public static File getNetworkConfigDirectory(DataType dataType) throws IOException {
        URL url = FileLocator.find(Activator.getDefault().getBundle(), getNetworkConfigDirectoryPath(dataType), null);
        
        return new File(FileLocator.toFileURL(url).getPath());
    }
    
    private static Path getNetworkMeasurementsDirectoryPath(DataType dataType) {
        return new Path("files" + File.separator + "afp_engine" + File.separator + dataType.name().toLowerCase() + File.separator + "Measurements");
    }
    
    public static File getNetworkMeasurementsDirectory(DataType dataType) throws IOException {
        URL url = FileLocator.find(Activator.getDefault().getBundle(), getNetworkMeasurementsDirectoryPath(dataType), null);
        
        return new File(FileLocator.toFileURL(url).getPath());
    }

}
