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

package org.amanzi.neo.preferences;

import java.util.LinkedHashMap;

import org.amanzi.neo.loader.dialogs.CRSdialog;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class CommonCRSPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Composite mainFrame;
    private List commonCrs;
    private LinkedHashMap<String, CoordinateReferenceSystem> crsList = new LinkedHashMap<String, CoordinateReferenceSystem>();
    @Override
    protected Control createContents(Composite parent) {
        mainFrame = new Composite(parent, SWT.FILL);
        Layout mainLayout = new GridLayout(2, true);
        mainFrame.setLayout(mainLayout);
        Label label = new Label(mainFrame, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        label.setText("Common CRS:");
        commonCrs = new List(mainFrame, SWT.DEFAULT);
        commonCrs.setItems(crsList.keySet().toArray(new String[0]));
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gridData.horizontalSpan = 2;
        gridData.verticalSpan = 10;
        commonCrs.setLayoutData(gridData);

        Button btnAdd = new Button(mainFrame, SWT.PUSH);
        btnAdd.setText("Add");
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
        btnDelete.setText("Delete");
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
     *
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
        CRSdialog dlg = new CRSdialog(mainFrame.getShell(),getCRS());
        if (dlg.open() == SWT.OK || true) {
            CoordinateReferenceSystem crs = dlg.getCRS();
            if (crs != null) {
                if (!crsList.values().contains(crs)) {
                    crsList.put(crs.getName().toString(), crs);
                    commonCrs.setItems(crsList.keySet().toArray(new String[0]));
                }
            }

        }

    }

    /**
     * @return
     */
    private CoordinateReferenceSystem getCRS() {
        return null;
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
                    NeoLoaderPlugin.exception(e);
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

}
