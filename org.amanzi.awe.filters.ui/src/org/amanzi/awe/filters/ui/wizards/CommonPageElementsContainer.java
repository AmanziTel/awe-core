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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author volad
 */
public class CommonPageElementsContainer extends WizardPage {
    private Map<String, IRenderableModel> datasetModelMap = new HashMap<String, IRenderableModel>();
    private static String FILTER_CREATION_PAGE_MSG = "Choose dataset";

    /**
     * create main group
     * 
     * @param parent
     */
    protected void createMainLayout(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(1, false));

    }

    /**
     * create combo box with network models list
     */
    protected void createDatasetCombobox() {
        Label label = new Label(main, SWT.LEFT);
        label.setText(FILTER_CREATION_PAGE_MSG);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        getRootIteam();
        String[] result = datasetModelMap.keySet().toArray(new String[] {});
        Arrays.sort(result);
        cbDataset = new Combo(main, SWT.DROP_DOWN);
        cbDataset.setLayoutData(rootLayoutData);
        cbDataset.setItems(result);
        cbDataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateTable(datasetModelMap.get(cbDataset.getItem(cbDataset.getSelectionIndex())));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * get all renderable models root;
     */
    protected void getRootIteam() {
        datasetModelMap = new HashMap<String, IRenderableModel>();
        try {
            IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
            for (IModel model : projectModel.findAllModels()) {
                if (model instanceof IRenderableModel) {
                    String id = model.getName();
                    datasetModelMap.put(id, (IRenderableModel)model);
                }
            }
        } catch (AWEException e) {
            LOGGER.error("Error while getRootItems work", e);
        }

        DatabaseManagerFactory.getDatabaseManager().commitMainTransaction();
    }

    @Override
    public void createControl(Composite parent) {
    }
}
