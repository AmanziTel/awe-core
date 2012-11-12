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

package org.amanzi.awe.nem.export;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.nem.messages.NEMMessages;
import org.apache.commons.collections.CollectionUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum ExportedDataItems {
    EXPORT_NETWORK_DATA(NEMMessages.EXPORT_NETWORK_DATA_ITEM, NEMMessages.EXPORT_NETWORK_DATA_ITEM_FILE_NAME_FORMAT, 0), EXPOR_SELECTION_DATA(
            NEMMessages.EXPORT_SELECTION_DATA_ITEM, NEMMessages.EXPORT_SELECTION_DATA_ITEM_FILE_NAME_FORMAT, 1,
            new SynonymsWrapper("sector", "name", "Sector"));

    public static ExportedDataItems findByName(final String name) {
        for (ExportedDataItems item : ExportedDataItems.values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    private int index;

    private String name;

    private String fileNameFormat;

    private final List<SynonymsWrapper> properties = new ArrayList<SynonymsWrapper>();

    private ExportedDataItems(final String name, final String fileNameFormat, final int index) {
        this.name = name;
        this.index = index;
        this.fileNameFormat = fileNameFormat;
    }

    private ExportedDataItems(final String name, final String fileNameFormat, final int index, final SynonymsWrapper... properties) {
        this.name = name;
        this.index = index;
        this.fileNameFormat = fileNameFormat;
        CollectionUtils.addAll(this.properties, properties);
    }

    /**
     * @return Returns the fileNameFormat.
     */
    public String getFileNameFormat() {
        return fileNameFormat;
    }

    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the properties.
     */
    public List<SynonymsWrapper> getProperties() {
        return properties;
    }
}
