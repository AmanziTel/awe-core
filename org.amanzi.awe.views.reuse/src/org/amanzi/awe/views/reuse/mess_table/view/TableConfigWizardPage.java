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

package org.amanzi.awe.views.reuse.mess_table.view;

import org.amanzi.awe.views.reuse.Messages;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard page for configure table columns visibility. 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class TableConfigWizardPage  extends WizardPage {
    
    private TableConfigDialog dialog;
    private String[] visible;
    private String[] invisible;

    /**
     * @param pageName
     */
    protected TableConfigWizardPage(String pageTitle, String pageDescr,String[] visibleArr, String[] invisibleArr) {
        super(pageTitle);
        setTitle(pageTitle);
        setDescription(pageDescr);
        visible = visibleArr;
        invisible = invisibleArr;
    }

    @Override
    public void createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.FILL);
        dialog = new TableConfigDialog(main);
        setControl(main);
    }
    
    /**
     * Returns configure result (visible columns).
     *
     * @return String[]
     */
    public String[] getVisibleProperties(){
        return dialog.lVisible.getItems();
    }
    
    /**
     * Returns configure result (invisible columns).
     *
     * @return String[]
     */
    public String[] getInvisibleProperties(){
        return dialog.lInvisible.getItems();
    }
    
    /**
     * Dialog for configure.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class TableConfigDialog{
        
        private Shell dialogShell;
        
        private List lVisible;
        private List lInvisible;
        private Button add;
        private Button addAll;
        private Button remove;
        private Button removeAll;
        
        /**
         * Constructor.
         * @param parent
         */
        public TableConfigDialog(Composite parent) {            
            dialogShell = parent.getShell();
            createControlForDialog(parent);
            addListeners(dialogShell);
        }

        /**
         * Add listeners to components
         * @param dialogShell
         */
        private void addListeners(Shell dialogShell) {
            add.addSelectionListener(new SelectionListener() {
                
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String[] selected = lVisible.getSelection();
                    if(selected==null || selected.length==0){
                        return;
                    }
                    for(String current : selected){
                        lVisible.remove(current);
                        lInvisible.add(current);
                    }
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            addAll.addSelectionListener(new SelectionListener() {
                
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String[] all = lVisible.getItems();
                    if(all==null || all.length==0){
                        return;
                    }
                    for(String current : all){
                        lVisible.remove(current);
                        lInvisible.add(current);
                    }
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            remove.addSelectionListener(new SelectionListener() {
                
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String[] selected = lInvisible.getSelection();
                    if(selected==null || selected.length==0){
                        return;
                    }
                    for(String current : selected){
                        lInvisible.remove(current);
                        lVisible.add(current);
                    }
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            removeAll.addSelectionListener(new SelectionListener() {
                
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String[] all = lInvisible.getItems();
                    if(all==null || all.length==0){
                        return;
                    }
                    for(String current : all){
                        lInvisible.remove(current);
                        lVisible.add(current);
                    }
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
        }

        /**
         * Create components.
         * @param parent
         */
        private void createControlForDialog(Composite parent) {
            GridLayout layout = new GridLayout(3, false);
            parent.setLayout(layout);
            parent.setLayoutData(new GridData(SWT.FILL));
            
            lVisible = createList(parent,Messages.TableConfigWizard_label_visible);
            lVisible.setItems(visible);
            
            Composite actionPanel = new Composite(parent, SWT.NONE);
            actionPanel.setLayout(new GridLayout(1, false));
            GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, true);
            layoutData.minimumWidth = 50;
            actionPanel.setLayoutData(layoutData);
            
            add = createButton(actionPanel,">");            
            addAll = createButton(actionPanel,">>");            
            remove = createButton(actionPanel,"<");
            removeAll = createButton(actionPanel,"<<");
                
            lInvisible = createList(parent, Messages.TableConfigWizard_label_invisible);
            lInvisible.setItems(invisible);
        }

        /**
         * Create button.
         *
         * @param actionPanel
         * @param labelText
         * @return Button
         */
        private Button createButton(Composite actionPanel, String labelText) {
            Button button = new Button(actionPanel, SWT.NONE);
            button.setText(labelText);
            button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
            return button;
        }

        /**
         * Create List.
         *
         * @param parent
         * @param labelText
         * @return List
         */
        private List createList(Composite parent, String labelText) {
            Composite panel = new Composite(parent, SWT.NONE);
            panel.setLayout(new GridLayout(1, false));      
            panel.setLayoutData(new GridData(180,300));
            
            Label listLabel = new Label(panel, SWT.NONE);
            listLabel.setText(labelText);
            listLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
            
            List list = new List(panel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.minimumWidth = 170;
            list.setLayoutData(gridData);            
            return list;
        }

        
    }

}
