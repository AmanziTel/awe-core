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

package org.amanzi.neo.wizards;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 * Import drive data from url wizard
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class DatasetImportUrlWizardPage extends WizardPage {

    private Button bLoad;
    private Combo cDataset;
    private Text fUrl;
    private String url;

    /**
     * Constructor
     * 
     * @param pageTitle
     * @param pageDescr
     */
    public DatasetImportUrlWizardPage(String pageTitle, String pageDescr) {
        super(pageTitle);
        setTitle(pageTitle);
        setDescription(pageDescr);
    }

    @Override
    public void createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.FILL);
        main.setLayout(new GridLayout(2, false));

        Label lUrl = new Label(main, SWT.NONE);
        lUrl.setText("URL:");
        lUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        fUrl = new Text(main, SWT.NONE);
        fUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        fUrl.setEditable(false);

        loadUrl();
        if (url != null && !url.isEmpty())
            fUrl.setText(url);
        else
            fUrl.setText("not specified");

        Label ldataset = new Label(main, SWT.NONE);
        ldataset.setText(NeoLoaderPluginMessages.DriveDialog_DatasetLabel);
        ldataset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        cDataset = new Combo(main, SWT.NONE);
        GridData lData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        lData.widthHint = 150;
        cDataset.setLayoutData(lData);

        Traverser allDatasetTraverser = NeoCorePlugin.getDefault().getProjectService().getAllDatasetTraverser(
                NeoServiceProvider.getProvider().getService().getReferenceNode());
        LinkedHashMap<String, Node> dataset = new LinkedHashMap<String, Node>();

        for (Node node : allDatasetTraverser) {
            dataset.put((String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME), node);
        }
        String[] items = dataset.keySet().toArray(new String[0]);
        Arrays.sort(items);
        cDataset.setItems(items);

        // bLoad = createButton(main, "Load");

        setControl(main);
        validateFinish();
    }

    private void loadUrl() {
        url = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(DataLoadPreferences.REMOTE_SERVER_URL);
    }

    /**
     * Create button
     * 
     * @param parent parent composite
     * @param name visible name
     * @return Button
     */
    private Button createButton(Composite parent, String name) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(name);
        button.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        return button;
    }

    /**
     *check correct input
     */
    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    /**
     * @return
     */
    private boolean isValidPage() {
        // return true;
        return url != null && !url.isEmpty();
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }


}
