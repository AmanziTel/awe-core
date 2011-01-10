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

package org.amanzi.awe.afp.ericsson.ui;

import java.io.File;
import java.util.Collection;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.FileSelection;
import org.amanzi.neo.loader.ui.wizards.LoaderPage;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * <p>
 *Additional page for Ericsson network config loader
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class EricssonNetworkGui2 extends LoaderPage<CommonConfigData> {

    private Group main;
    private FileSelection viewer;

    /**
     * Instantiates a new ericsson network gui2.
     */
    public EricssonNetworkGui2() {
        super("additionalEricssonNetworkGuiPage");
        setTitle("Import a files containing network BSN data");
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(1, false));
        viewer = new FileSelection();
        viewer.createPartControl(main);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        viewer.getTreeViewer().getTree().setLayoutData(gridData);
        viewer.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                fileSelectionChanged(event);
            }
        });
        setControl(main);
        update();
    }


    /**
     * File selection changed.
     *
     * @param event the event
     */
    protected void fileSelectionChanged(SelectionChangedEvent event) {
        update();
    }

    @Override
    protected boolean validateConfigData(CommonConfigData configurationData) {
        Collection<File> files = viewer.getSelectedFiles(null);
        
         if (files.isEmpty()){
             setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE,DialogPage.ERROR); 
             return false;
         }
         viewer.storeDefSelection(null);
         configurationData.getAdditionalProperties().put("BSM_FILES", files);
         ILoaderInputValidator<CommonConfigData> validator = new EricssonBSMValidator();
         validator.filter(configurationData);
         if (files.size()!=configurationData.getAllLoadedFiles().size()){
             viewer.getTreeViewer().setSelection(new StructuredSelection(configurationData.getAllLoadedFiles().toArray()), false);
             return validateConfigData(configurationData);
         }
         if (configurationData.getAllLoadedFiles().isEmpty()){
             setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE,DialogPage.ERROR); 
             return false;            
         }
         IValidateResult result = validator.validate(configurationData);
         if (result.getResult()==Result.FAIL){
             setMessage(String.format(result.getMessages(), getSelectedLoader().getDescription()),DialogPage.ERROR); 
             return false;          
         }else if (result.getResult()==Result.UNKNOWN){
             setMessage(String.format(result.getMessages(), getSelectedLoader().getDescription()),DialogPage.WARNING); 
         }else{
             setMessage(""); //$NON-NLS-1$
         }
         return true;
    }
    

}
