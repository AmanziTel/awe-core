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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * content provider for project explorer
 * 
 * @author Vladislav_Kondratenko
 */
public class ProjectTreeContentProvider extends AbstractContentProvider<IProjectModel> {

    private static final Iterable<IDataElement> EMPTY_ELEMENTS_COLLECTION = new ArrayList<IDataElement>();
    private final IDriveModelProvider driveModelProvider;
    private final INetworkModelProvider networkModelProvider;

    public ProjectTreeContentProvider() {
        this(AWEUIPlugin.getDefault().getDriveModelProvider(), AWEUIPlugin.getDefault().getNetworkModelProvider(), AWEUIPlugin
                .getDefault().getProjectModelProvider());

    }

    /**
     * @param networkModelProvider
     * @param projectModelProvider
     */
    protected ProjectTreeContentProvider(final IDriveModelProvider driveModelProvider,
            final INetworkModelProvider networkModelProvider, final IProjectModelProvider projectModelProvider) {
        super(projectModelProvider);
        this.networkModelProvider = networkModelProvider;
        this.driveModelProvider = driveModelProvider;
    }

    @Override
    public Object getParent(final Object element) {
        // TODO Need implement
        return null;
    }

    @Override
    protected boolean additionalCheckChild(final Object element) throws ModelException {
        return true;
    }

    @Override
    protected void handleInnerElements(final ITreeItem<IProjectModel> item) throws ModelException {
        List<IDataElement> models = new ArrayList<IDataElement>();
        if (!item.getParent().asDataElement().equals(item.getDataElement())) {
            setChildren(EMPTY_ELEMENTS_COLLECTION);
            return;
        }
        for (INetworkModel model : networkModelProvider.findAll(item.getParent())) {
            models.add(model.asDataElement());
        }
        for (IDriveModel model : driveModelProvider.findAll(item.getParent())) {
            models.add(model.asDataElement());
        }

        setChildren(models);
    }

    @Override
    protected void handleRoot(final ITreeItem<IProjectModel> item) throws ModelException {
        handleInnerElements(item);
    }

    @Override
    protected List<IProjectModel> getRootElements() throws ModelException {
        return getProjectModelProvider().findAll();
    }

}
