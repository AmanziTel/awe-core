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

package org.amanzi.neo.loader.ui.preferences;

import java.util.LinkedHashMap;

import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.dialogs.CRSdialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * Preference page: Common CRS
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class CommonCRSPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Composite mainFrame;
    private List commonCrs;
    private LinkedHashMap<String, CoordinateReferenceSystem> crsList = new LinkedHashMap<String, CoordinateReferenceSystem>();
    private String subtitle = null;
    private CoordinateReferenceSystem selectedCRS = null;

    @Override
    protected Control createContents(Composite parent) {
        mainFrame = new Composite(parent, SWT.NONE);

        GridLayout mainLayout = new GridLayout(2, false);

        mainFrame.setLayout(mainLayout);
        if (subtitle != null) {
            Label label = new Label(mainFrame, SWT.NONE);
            GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            label.setLayoutData(gridData);
            label.setText(subtitle);
        }
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        commonCrs = new List(mainFrame, SWT.DEFAULT);
        String[] arrayCRS = crsList.keySet().toArray(new String[0]);
        commonCrs.setItems(arrayCRS);
        selectCRS();
        gridData = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        gridData.verticalSpan = 10;
        commonCrs.setLayoutData(gridData);
        commonCrs.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selInd = commonCrs.getSelectionIndex();
                if (selInd < 0) {
                    selectedCRS = null;
                } else {
                    selectedCRS = crsList.get(commonCrs.getItem(selInd));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        Button btnAdd = new Button(mainFrame, SWT.PUSH);
        btnAdd.setText(NeoLoaderPluginMessages.CommonCRSPreference_button_ADD);
        GridData gdBtnSave = new GridData();
        gdBtnSave.horizontalAlignment = GridData.CENTER;
        btnAdd.setLayoutData(gdBtnSave);
        btnAdd.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addCRSToList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        Button btnDelete = new Button(mainFrame, SWT.PUSH);
        btnDelete.setText(NeoLoaderPluginMessages.CommonCRSPreference_button_REMOVE);
        GridData gdBtnCancel = new GridData();
        gdBtnCancel.horizontalAlignment = GridData.CENTER;
        btnDelete.setLayoutData(gdBtnCancel);
        btnDelete.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removeCRSFromList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return mainFrame;
    }

    /**
     */
    private void selectCRS() {
        if (getCRS() != null) {
            int id = -1;
            String[] arrayCRS = commonCrs.getItems();
            for (int i = 0; i < arrayCRS.length; i++) {
                if (getCRS().equals(crsList.get(arrayCRS[i]))) {
                    id = i;
                    break;
                }
            }
            if (id >= 0) {
                commonCrs.select(id);
            }
        }
    }

    @Override
    protected Point doComputeSize() {
        return new Point(200, 300);
    }

    /**
     *remove CRS from list
     */
    protected void removeCRSFromList() {
        int id = commonCrs.getSelectionIndex();
        if (id >= 0) {
            crsList.remove(id);
            commonCrs.setItems(crsList.keySet().toArray(new String[0]));
        }
    }

    /**
     *
     */
    protected void addCRSToList() {
        CRSdialog dlg = new CRSdialog(mainFrame.getShell(), getCRS());
        if (dlg.open() == SWT.OK || true) {
            CoordinateReferenceSystem crs = dlg.getCRS();
            if (crs != null) {
                setSelectedCRS(crs);
                if (!crsList.values().contains(crs)) {
                    crsList.put(crs.getName().toString(), crs);
                    commonCrs.setItems(crsList.keySet().toArray(new String[0]));
                    selectCRS();
                }
            }

        }

    }

    /**
     * @return
     */
    public CoordinateReferenceSystem getCRS() {
        return selectedCRS;
    }

    @Override
    public void init(IWorkbench workbench) {
        crsList.clear();
        String crsListStr = getPreferenceStore().getString(DataLoadPreferences.COMMON_CRS_LIST);
        if (crsListStr != null && !crsListStr.isEmpty()) {
            for (String wkt : crsListStr.split(DataLoadPreferences.CRS_DELIMETERS)) {
                CoordinateReferenceSystem crs;
                try {
                    crs = CRS.parseWKT(wkt);
                } catch (FactoryException e) {
                    
                    continue;
                }
                crsList.put(crs.getName().toString(), crs);
            }
        }

    }

    @Override
    public boolean performOk() {
        StringBuilder sb = new StringBuilder();
        for (CoordinateReferenceSystem crs : crsList.values()) {
            sb.append(DataLoadPreferences.CRS_DELIMETERS).append(crs.toWKT());
        }
        getPreferenceStore().setValue(DataLoadPreferences.COMMON_CRS_LIST, sb.length() > 0 ? sb.substring(DataLoadPreferences.CRS_DELIMETERS.length()) : "");
        return super.performOk();
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

    /**
     * @param string
     */
    public void setSubTitle(String subTitle) {
        subtitle = subTitle;
    }

    /**
     * set CRS
     * 
     * @param decode selected CRS
     */
    public void setSelectedCRS(CoordinateReferenceSystem decode) {
        selectedCRS = decode;
    }

}
