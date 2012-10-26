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

package org.amanzi.awe.nem.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.nem.exceptions.NemManagerOperationException;
import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.wizard.pages.InitialNetworkPage;
import org.amanzi.awe.nem.ui.wizard.pages.PropertyEditorPage;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkCreationWizard extends Wizard {

    private NetworkDataContainer container;
    private List<String> pagesOrder;

    public NetworkCreationWizard() {
        setForcePreviousAndNextButtons(true);
    }

    @Override
    public void addPages() {
        addPage(new InitialNetworkPage());
    }

    /**
     * @param name
     * @return
     */
    private IWizardPage computeNextPage(final String name) {
        if (name.equals(NEMMessages.CREATE_NEW_NETWORK)) {
            return getPage(pagesOrder.get(0));
        }
        int currentPageIndex = pagesOrder.indexOf(name);
        if (currentPageIndex == pagesOrder.size() - 1) {
            return null;
        }
        String nextPageName = pagesOrder.get(++currentPageIndex);
        return getPage(nextPageName);
    }

    /**
     * @param name
     * @return
     */
    private IWizardPage computePreviousPage(final String name) {
        int currentPageIndex = pagesOrder.indexOf(name);
        if (currentPageIndex <= 0) {
            return getPage(NEMMessages.CREATE_NEW_NETWORK);
        }
        String previousPageName = pagesOrder.get(--currentPageIndex);
        return getPage(previousPageName);
    }

    protected NetworkDataContainer getDataContainer() {
        if (container == null) {
            container = new NetworkDataContainer();
        }
        return container;
    }

    @Override
    public IWizardPage getNextPage(final IWizardPage page) {
        if (page instanceof InitialNetworkPage) {
            initContainerFromStartPage((InitialNetworkPage)page);
            initializeNewPages((InitialNetworkPage)page, false);
        } else {
            handlePropertyPage((PropertyEditorPage)page);

        }
        return computeNextPage(page.getName());
    }

    @Override
    public IWizardPage getPreviousPage(final IWizardPage page) {
        if (page instanceof InitialNetworkPage) {
            return null;
        } else {
            return computePreviousPage(page.getName());
        }
    }

    /**
     * @param iWizardPage
     */
    protected void handleFirstPageOnFinish(final IWizardPage iWizardPage) {
        if (getPages()[0] instanceof InitialNetworkPage) {
            initContainerFromStartPage((InitialNetworkPage)getPages()[0]);
            initializeNewPages((InitialNetworkPage)getPages()[0], true);
        }
    }

    protected void handleModelRefreshing(final List<INodeType> types, final IProgressMonitor monitor) {
        try {
            InitialNetworkPage firstPage = (InitialNetworkPage)getPages()[0];
            NetworkElementManager.getInstance().createModel(container.getName(), types, container.getTypeProperties(),
                    firstPage.getCrs(), monitor);
        } catch (NemManagerOperationException e) {
            return;
        }
    }

    /**
     * @param page
     */
    protected void handlePropertyPage(final PropertyEditorPage page) {
        container.putToTypeProperties(page.getType(), page.getProperties());
    }

    /**
     * @param page
     */
    private void initContainerFromStartPage(final InitialNetworkPage page) {
        getDataContainer().setName(page.getNetworkName());
        getDataContainer().setStructure(page.getNetworkStructure());

    }

    /**
     * @param page
     * @param b
     */
    private void initializeNewPages(final InitialNetworkPage page, final boolean isFinished) {
        pagesOrder = new ArrayList<String>();
        for (int i = 1; i < page.getNetworkStructure().size(); i++) {
            INodeType type = page.getNetworkStructure().get(i);
            pagesOrder.add(type.getId());
            if (getPage(type.getId()) == null) {
                PropertyEditorPage propertyPage = new PropertyEditorPage(type);
                propertyPage.initializeTypes();
                if (isFinished) {
                    handlePropertyPage(propertyPage);
                } else {
                    addPage(propertyPage);
                }
            }
        }
    }

    @Override
    public boolean performFinish() {
        if (getPages().length == 1) {
            handleFirstPageOnFinish(getPages()[0]);
        }

        final List<INodeType> types = NetworkElementManager.getInstance().updateNodeTypes(
                getDataContainer().getStructure().toArray(new INodeType[getDataContainer().getStructure().size()]));
        Job job = new Job("Finishup NEM opertion") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                handleModelRefreshing(types, monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        return true;
    }

}
