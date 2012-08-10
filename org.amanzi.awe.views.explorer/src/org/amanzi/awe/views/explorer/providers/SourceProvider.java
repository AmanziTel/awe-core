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

package org.amanzi.awe.views.explorer.providers;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

/**
 * <p>
 * source provider
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SourceProvider extends AbstractSourceProvider {
    public final static String STATE = "org.amanzi.awe.views.explorer.view";
    private boolean curState = true;

    @Override
    public void dispose() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Map getCurrentState() {
        Map map = new HashMap(1);
        map.put(STATE, curState);
        return map;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] {STATE};
    }

    public void setShowInTreeMenuState(ITreeItem< ? extends IModel> item) {
        curState = !item.getParent().asDataElement().equals(item.getDataElement());
        fireSourceChanged(ISources.WORKBENCH, STATE, curState);
    }
}
