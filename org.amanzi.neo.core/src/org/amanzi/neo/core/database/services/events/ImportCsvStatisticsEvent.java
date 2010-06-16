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

package org.amanzi.neo.core.database.services.events;

/**
 * <p>
 * Event to start import AMS statistics from .csv files.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ImportCsvStatisticsEvent extends ShowViewEvent{
    
    private String directory;
    private String dataset;
    private String network;

    /**
     * @param aType
     */
    public ImportCsvStatisticsEvent(String aDirectory, String aDataset, String aNetwork ) {
        super("",UpdateViewEventType.IMPORT_STATISTICS);
        directory = aDirectory;
        dataset = aDataset;
        network = aNetwork;
    }
    
    /**
     * @return Returns the directory.
     */
    public String getDirectory() {
        return directory;
    }
    
    /**
     * @return Returns the dataset.
     */
    public String getDataset() {
        return dataset;
    }
    
    /**
     * @return Returns the network.
     */
    public String getNetwork() {
        return network;
    }

}
