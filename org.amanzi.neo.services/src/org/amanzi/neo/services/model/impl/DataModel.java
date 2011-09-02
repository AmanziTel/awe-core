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

package org.amanzi.neo.services.model.impl;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class DataModel extends AbstractModel implements IDataModel {

    private static Logger LOGGER = Logger.getLogger(DataModel.class);

    private NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

    protected void addChild(Node parent, Node child) {
        try {
            dsServ.addChild(parent, child);
        } catch (DatabaseException e) {
            LOGGER.error("Could not add child.", e);
        }
    }

    protected void addChild(Node parent, Node child, Node lastChild) {
        try {
            dsServ.addChild(parent, child, lastChild);
        } catch (DatabaseException e) {
            LOGGER.error("Could not add child.", e);
        }
    }

    @Override
    public IDataElement getParentElement(IDataElement childElement) {
        return null;
    }

    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        return null;
    }

    @Override
    public IDataElement[] getAllElementsByType(INodeType elementType) {
        return null;
    }

    @Override
    public void finishUp() {
    }

}
