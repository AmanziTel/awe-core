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

package org.amanzi.awe.statistics.factory;

import org.amanzi.awe.statistics.entities.impl.Dimension;
import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.model.StatisticsModel;
import org.amanzi.awe.statistics.template.TemplateColumn;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class EntityFactory {
    /**
     * static lazy initialization
     * <p>
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static final class SingletonHolder {
        public static final EntityFactory HOLDER_INSTANCE = new EntityFactory();

        private SingletonHolder() {
        }
    }

    public static EntityFactory getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    /**
     * create new {@link Dimension} from existed node;
     * 
     * @param parent
     * @param dimensionNode
     * @return
     */
    public Dimension createDimension(StatisticsModel parent, Node dimensionNode) {
        return new Dimension(parent.getRootNode(), dimensionNode);
    }

    /**
     * create new {@link Dimension} with dimensionType
     * 
     * @param parent
     * @param type
     * @return
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     */
    public Dimension createDimension(StatisticsModel parent, DimensionTypes type) throws DatabaseException,
            IllegalNodeDataException {
        return new Dimension(parent.getRootNode(), type);
    }

    /**
     * @param row
     * @param column
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    public void createScell(StatisticsRow row, TemplateColumn column) throws DuplicateNodeNameException, DatabaseException,
            IllegalNodeDataException {
        StatisticsCell cell = row.addSCell(column.getName());
        cell.setFunction(column.getFunction().newFunction());
    }
}
