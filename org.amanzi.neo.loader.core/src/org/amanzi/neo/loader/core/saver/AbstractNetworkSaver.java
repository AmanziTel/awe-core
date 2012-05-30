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

package org.amanzi.neo.loader.core.saver;

import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.INetworkModel;

/**
 * Abstract class for methods of network models
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public abstract class AbstractNetworkSaver<T1 extends IDataModel, T3 extends IConfiguration>
        extends
            AbstractMappedDataSaver<T1, T3> {

    /*
     * Network Model
     */
    protected INetworkModel networkModel;

    /**
     * find network element
     */
    protected IDataElement getNetworkElement(INodeType type, String propertyName, Map<String, Object> values) throws AWEException {

        Object oElementName = values.remove(propertyName);

        if (oElementName != null) {
            String elementName = oElementName.toString();
            return getNetworkElement(type, elementName);
        }
        return null;
    }

    /**
     * get sector node type
     */
    protected INodeType getSectorNodeType() {
        return NetworkElementNodeType.SECTOR;
    }

    /**
     * find network element by name
     */
    protected IDataElement getNetworkElement(INodeType type, String elementName) throws AWEException {
        if (elementName == null) {
            return null;
        }

        if (!elementName.isEmpty()) {
            Set<IDataElement> searchResult = networkModel.findElementByPropertyValue(type, AbstractService.NAME, elementName);

            if (!searchResult.isEmpty()) {
                if (searchResult.size() > 1) {
                    throw new DuplicateNodeNameException(elementName, type);
                } else {
                    return searchResult.iterator().next();
                }
            }
        }

        return null;
    }
}
