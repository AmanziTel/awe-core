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

package org.amanzi.neo.loader.ui.wizards;

import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.saver.IData;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.FileSelection;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * Abstract class for common main loader page - with selection of root node, crs,select necessary
 * file/dir and select loader
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class AbstractNetworkLoaderMainPage<T extends IConfiguration> extends LoaderPage<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractNetworkLoaderMainPage.class);

    protected Group main;
    protected Combo cbNetwork;
    protected FileSelection fileSelectionViewer;
    protected Combo cbLoaders;
    protected static Integer selectedIteam = 0;
    protected Object editor;
    protected HashMap<String, INetworkModel> members;
    protected boolean isSetDefaultLoader = false;
    protected String rootName = StringUtils.EMPTY;

    /**
     * @param pageName
     */
    protected AbstractNetworkLoaderMainPage(String pageName) {
        super(pageName);
    }

    /**
     * create combo box with network models list
     */
    protected void createNetworkComboBox() {
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        String[] definedNetworks = getRootItems();
        cbNetwork = new Combo(main, SWT.DROP_DOWN);
        cbNetwork.setLayoutData(rootLayoutData);
        cbNetwork.setItems(definedNetworks);
        cbNetwork.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                changeRootName();
            }
        });
        cbNetwork.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeRootName();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * create view which allow to select files and directories
     */
    protected abstract void createEditor();

    /**
     * create loaders combobox;
     */
    protected void createLoadersCombobox() {

        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_DATA_TYPE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 3));

        String[] definedLoaders = getLoadersDescription();
        cbLoaders = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbLoaders.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 3));
        cbLoaders.setItems(definedLoaders);
        cbLoaders.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectLoader(cbLoaders.getSelectionIndex());
                update();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * create main group
     * 
     * @param parent
     */
    protected void createMainGroup(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_NETWORK);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    }

    @Override
    public void createControl(Composite parent) {
        createMainGroup(parent);
        createNetworkComboBox();
        createEditor();
        createLoadersCombobox();
        setProjectNamesToLoaders();
        addAdditionalComponents(parent, main);
        selectLoader(defineLoaders());
        setControl(main);
        update();
    }

    /**
     * put project attribute to config in all loaders elements
     */
    private void setProjectNamesToLoaders() {
        try {
            for (ILoader<IData, T> loader : loaders) {
                setSelectedLoader(loader);
                getConfigurationData().getDatasetNames().put(ConfigurationDataImpl.PROJECT_PROPERTY_NAME,
                        ProjectModel.getCurrentProjectModel().getName());
            }
        } catch (AWEException e1) {
            LOGGER.error("Error while get current project name", e1);
        }
    }

    /**
     * handle any editor changes
     * 
     * @param event
     */
    protected abstract void handleEditorModification(EventObject event);

    /**
     * Adds the additional components.
     * 
     * @param parent the parent
     * @param main the main
     */
    protected void addAdditionalComponents(Composite parent, Group main) {
    }

    @Override
    protected void update() {
        super.commonUpdate();
    }

    /**
     * Change root(networkName) name.
     */
    protected void changeRootName() {
        selectedIteam = cbNetwork.getSelectionIndex();
        rootName = cbNetwork.getText();
        getConfigurationData().getDatasetNames().put(ConfigurationDataImpl.NETWORK_PROPERTY_NAME, rootName);
        update();
    }

    /**
     * perform actions for loader selection
     */
    protected abstract void handleLoaderSelection();

    /**
     * find all network items
     */
    protected String[] getRootItems() {
        members = new HashMap<String, INetworkModel>();
        try {
            IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
            for (INetworkModel model : projectModel.findAllNetworkModels()) {
                String id = model.getName();
                members.put(id, model);
            }
        } catch (AWEException e) {
            LOGGER.error("Error while getRootItems work", e);
        }

        String[] result = members.keySet().toArray(new String[] {});
        Arrays.sort(result);
        DatabaseManagerFactory.getDatabaseManager().commitMainTransaction();
        return result;
    }

    /**
     * try to set default loader when page is open
     * 
     * @return
     */
    protected abstract Integer defineLoaders();
}
