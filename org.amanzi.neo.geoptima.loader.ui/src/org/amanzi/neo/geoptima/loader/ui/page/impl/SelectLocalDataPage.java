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

package org.amanzi.neo.geoptima.loader.ui.page.impl;

import java.io.File;
import java.util.Collection;

import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.CRSSelector.ICRSSelectorListener;
import org.amanzi.neo.geoptima.loader.ui.internal.Messages;
import org.amanzi.neo.loader.core.impl.MultiFileConfiguration;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget.ISelectDriveResourceListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.WizardFactory;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.swt.widgets.Composite;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SelectLocalDataPage extends AbstractLocationDataPage implements

ICRSSelectorListener, ISelectDriveResourceListener {
    private SelectDriveResourcesWidget driveResource;

    /**
     * @param pageName
     */
    public SelectLocalDataPage() {
        super(Messages.selectLocalCatalSource_PageName);
        setTitle(Messages.selectLocalCatalSource_PageName);
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);
        driveResource = WizardFactory.getInstance().addDriveResourceSelector(getMainComposite(), this, getFileFilter());
        AWEWidgetFactory.getFactory().addCRSSelectorWidget(this, getMainComposite());
    }

    private IOFileFilter getFileFilter() {
        return getLoader() != null ? getLoader().getFileFilter() : FileFilterUtils.trueFileFilter();
    }

    @Override
    public void onDirectorySelected(final String directoryName) {

    }

    @Override
    public void onResourcesSelected(final Collection<File> files) {
        MultiFileConfiguration configuration = (MultiFileConfiguration)getConfiguration();

        configuration.addFiles(files);
        update();

    }

    @Override
    public void onCRSSelected(final CoordinateReferenceSystem crs) {
        // TODO Auto-generated method stub

    }

    /**
     *
     */
    private void update() {
        if (getConfiguration().getFileCount() > 0) {
            setPageComplete(true);
            setErrorMessage(null);
        } else {
            setPageComplete(false);
            setErrorMessage(Messages.selectFilesToUploadMessage);
        }
        if (driveResource != null) {
            driveResource.updateFilter(getFileFilter());
        }

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
