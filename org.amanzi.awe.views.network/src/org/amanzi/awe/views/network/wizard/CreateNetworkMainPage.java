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

package org.amanzi.awe.views.network.wizard;

import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

// TODO: Auto-generated Javadoc
/**
 * TODO Purpose of
 * <p>
 * 
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CreateNetworkMainPage extends WizardPage {


    private Button selectCRS;
    private CoordinateReferenceSystem selectedCRS;

    /**
     * Instantiates a new creates the network main page.
     *
     * @param pageName the page name
     */
    public CreateNetworkMainPage(String pageName) {
        super(pageName);
        setDescription("Create network structure");
        
       
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        final Group main = new Group(parent, SWT.FILL);
        main.setLayout(new GridLayout(2, false));
        Label networklb=new Label(main, SWT.LEFT);
        networklb.setText("Network:");
        Text network=new  Text(main, SWT.FILL|SWT.BORDER);
        network.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateNetworkName();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        selectCRS=new Button(main, SWT.PUSH);
        selectCRS.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectCRS();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        updateButtonLabel();
        init();
        setControl(main);
    }

    /**
     *
     */
    protected void selectCRS() {
        CoordinateReferenceSystem result = ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<CoordinateReferenceSystem>() {

            private CoordinateReferenceSystem result;

            @Override
            public CoordinateReferenceSystem getValue() {
                return result;
            }

            @Override
            public void run() {
//                result = null;
//                PreferencePage page = new CommonCRSPreferencePage();
//                try {
//                    page.setSelectedCRS(getSelectedCRS());
//                } catch (NoSuchAuthorityCodeException e) {
//                    result = null;
//                    return;
//                }
//                page.setTitle("Select Coordinate Reference System");
//                page.setSubTitle("Select the coordinate reference system from the list of commonly used CRS's, or add a new one with the Add button");
//                page.init(PlatformUI.getWorkbench());
//                PreferenceManager mgr = new PreferenceManager();
//                IPreferenceNode node = new PreferenceNode("1", page); //$NON-NLS-1$
//                mgr.addToRoot(node);
//                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//                PreferenceDialog pdialog = new PreferenceDialog(shell, mgr);;
//                if (pdialog.open() == PreferenceDialog.OK) {
//                    page.performOk();
//                    result = page.getCRS();
//                }

            }
        });

    }

    /**
     *
     */
    private void updateButtonLabel() {
        CoordinateReferenceSystem crs=getSelectedCRS();
    }

    /**
     *
     * @return
     */
    private CoordinateReferenceSystem getSelectedCRS() {
        return selectedCRS==null?getDefaultCRS():selectedCRS;
    }



    /**
     *
     * @return
     */
    private CoordinateReferenceSystem getDefaultCRS() {
        return null;
    }

    /**
     *
     */
    protected void updateNetworkName() {
    }

    /**
     *
     */
    private void init() {
    }


}
