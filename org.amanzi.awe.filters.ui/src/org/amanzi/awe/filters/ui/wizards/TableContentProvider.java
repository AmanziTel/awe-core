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

package org.amanzi.awe.filters.ui.wizards;

import java.util.ArrayList;
import java.util.Iterator;

import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.RenderableModel.GisModel;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Vladislav_Kondratenko
 */
public class TableContentProvider implements IContentProvider, IStructuredContentProvider {
    private static final Logger LOGGER = Logger.getLogger(TableContentProvider.class);

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    // Return the tasks as an array of Objects
    public Object[] getElements(Object parent) {
        if (parent instanceof IRenderableModel) {
            IRenderableModel model = (IRenderableModel)parent;
            return collectAllGis(model).toArray();
        }
        return null;
    }

    private ArrayList<GisModel> collectAllGis(IRenderableModel model) {
        ArrayList<GisModel> gisListReturnable = new ArrayList<GisModel>();
        Iterator<GisModel> gisList;
        try {
            gisList = model.getAllGisModels().iterator();

            while (gisList.hasNext()) {
                GisModel gisModel = gisList.next();
                if (!model.getName().equals(gisModel.getName())) {
                    gisListReturnable.add(gisModel);
                }
            }
        } catch (DatabaseException e) {
            LOGGER.error("cann't get gis for model " + model.getName(), e);
            return null;
        }
        return gisListReturnable;
    }

}
