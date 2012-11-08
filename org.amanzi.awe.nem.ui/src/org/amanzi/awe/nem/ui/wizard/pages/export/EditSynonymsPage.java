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

package org.amanzi.awe.nem.ui.wizard.pages.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.nem.export.ExportedDataItems;
import org.amanzi.awe.nem.export.SynonymsWrapper;
import org.amanzi.awe.nem.ui.widgets.SynonymsEditorTable;
import org.amanzi.awe.nem.ui.widgets.SynonymsEditorTable.ISynonymsTableListener;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class EditSynonymsPage extends WizardPage implements INetworkExportPage, ISynonymsTableListener {

    private static final Layout ONE_COLUMN_LAYOUT = new GridLayout(1, false);

    private final ExportedDataItems pageType;

    private INetworkModel model;

    private List<SynonymsWrapper> properties;

    private Composite main;

    private SynonymsEditorTable tableWiget;

    /**
     * @param pageName
     */
    public EditSynonymsPage(final ExportedDataItems item) {
        super(item.getName());
        this.pageType = item;
    }

    @Override
    public void createControl(final Composite parent) {
        this.main = new Composite(parent, SWT.NONE);
        main.setLayout(ONE_COLUMN_LAYOUT);
        main.setLayoutData(new GridData(GridData.FILL_BOTH));
        tableWiget = new SynonymsEditorTable(main, this, properties);
        tableWiget.initializeWidget();
        setControl(main);
    }

    /**
     * @return Returns the pageType.
     */
    public ExportedDataItems getPageType() {
        return pageType;
    }

    /**
     * @return Returns the properties.
     */
    public List<SynonymsWrapper> getProperties() {
        return properties;
    }

    private void initProperties() {
        switch (pageType) {
        case EXPORT_NETWORK_DATA:
            intiWrappers(model.getSynonyms());
            break;
        default:
            break;
        }
    }

    /**
     * @param synonyms
     * @return
     */
    private void intiWrappers(final Map<String, String> synonyms) {
        properties = new ArrayList<SynonymsWrapper>();
        for (Entry<String, String> property : synonyms.entrySet()) {
            String[] key = property.getKey().split("\\.");
            properties.add(new SynonymsWrapper(key[0], key[1], property.getValue()));
        }
        Collections.sort(properties);
    }

    @Override
    public void isValid() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUpNetwork(final INetworkModel model) {
        if (!model.equals(this.model)) {
            this.model = model;
            initProperties();
            setTitle(pageType.getName());
        }

    }

}
