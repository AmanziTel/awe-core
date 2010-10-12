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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Traverser;


/**
 * <p>
 * Wizard page for import dataset from url
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class DatasetImportUrlWizardPage extends WizardPage {

    /** The c dataset. */
    private Combo cDataset;

    /** The url. */
    private Text fUrl;

    /** The url. */
    private String url;

    /** The l url. */
    private Label lUrl;
    
    private Shell shell;
    
    private String imsi;
    private String imei;
    
    public LinkedHashMap<String, Node> dataset = new LinkedHashMap<String, Node>();
    


    /**
     * Instantiates a new dataset import url wizard page.
     * 
     * @param pageTitle the page title
     * @param pageDescr the page descr
     */
    public DatasetImportUrlWizardPage(String pageTitle, String pageDescr) {
        super(pageTitle);
        setTitle(pageTitle);
        setDescription(pageDescr);
    }

    /**
     * Creates the control.
     * 
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
    	shell = parent.getShell();
    	imei = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(DataLoadPreferences.USER_IMEI);
    	imsi = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(DataLoadPreferences.USER_IMSI);
    	
        Composite main = new Composite(parent, SWT.FILL);
        main.setLayout(new GridLayout(2, false));

        lUrl = new Label(main, SWT.NONE);
        lUrl.setText("URL:");
        lUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        fUrl = new Text(main, SWT.NONE);
        fUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        fUrl.setEditable(true);

        loadUrl();
        if (url != null && !url.isEmpty())
            fUrl.setText(url);
        else
            fUrl.setText("not specified");

        fUrl.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				url = fUrl.getText();
				validateFinish();
			}
        	
        });
        
        Label ldataset = new Label(main, SWT.NONE);
        ldataset.setText(NeoLoaderPluginMessages.DriveDialog_DatasetLabel);
        ldataset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        cDataset = new Combo(main, SWT.NONE);
        GridData lData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        lData.widthHint = 150;
        cDataset.setLayoutData(lData);

        Traverser allDatasetTraverser = NeoCorePlugin.getDefault().getProjectService().getAllDatasetTraverser(
                NeoServiceProvider.getProvider().getService().getReferenceNode());
        

        for (Node node : allDatasetTraverser) {
            dataset.put((String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME), node);
        }
        
        String[] items = dataset.keySet().toArray(new String[0]);
        
        	
        Arrays.sort(items);
        cDataset.setItems(items);
        

        setControl(main);
        validateFinish();
    }

    /**
     * Load url.
     */
    private void loadUrl() {
        url = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(DataLoadPreferences.REMOTE_SERVER_URL);
    }

    /**
     * Validate finish.
     */
    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    private boolean isValidPage() {
    	if (imei == null || imei.trim().isEmpty() || imsi == null || imsi.trim().isEmpty()){
    		MessageDialog.openInformation(shell, "Imei/Imsi not set", "Please set your imei and imsi in the preferences");
    		return false;
    	}
    	
    	
        if (url != null && !url.isEmpty()) {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                lUrl.setForeground(new Color(null, 255, 0, 0));
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl() {
    	
    	String completeUrl = url + "/event/extract.csv?dataset=" + imsi.substring(0, 5) + "&imsi=" + imsi + "&imei=" + imei;
        return completeUrl;
    }

    /**
     * Gets the dataset.
     * 
     * @return the dataset
     */
    public String getDataset() {
        return cDataset.getText();
    }
    
    public Node getDatasetNode(String datasetName){
    	if (!dataset.containsKey(datasetName)){
    		return null;
    	}
    	
    	return dataset.get(datasetName);
    }

}
