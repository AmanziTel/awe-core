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

import org.amanzi.neo.geoptima.loader.impl.core.RemoteSupportConfiguration;
import org.amanzi.neo.geoptima.loader.ui.internal.Messages;
import org.amanzi.neo.geoptima.loader.ui.widgets.impl.GeoptimaLoaderTypeSelectorWidget;
import org.amanzi.neo.geoptima.loader.ui.widgets.impl.GeoptimaLoaderTypeSelectorWidget.IGeoptimaLoaderTypeChanged;
import org.amanzi.neo.loader.ui.page.impl.internal.AbstractLoaderPage;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class GeoptimaLoaderTypeSelectionPage extends AbstractLoaderPage<RemoteSupportConfiguration>
        implements
            IGeoptimaLoaderTypeChanged {

    private GeoptimaLoaderTypeSelectorWidget<RemoteSupportConfiguration> loaderList;

    private static final String FTP_MATHCES_PATTERN = ".* ftp .*";

    private static final String WEB_MATHCES_PATTERN = ".* web .*";

    /**
     * @param pageName
     */
    public GeoptimaLoaderTypeSelectionPage() {
        super(Messages.LoadGeoptimaPage_PageName);
    }

    @Override
    public IWizardPage getNextPage() {
        String name = loaderList.getSelectedLoader();
        IWizardPage page;
        if (name.matches(FTP_MATHCES_PATTERN)) {
            page = getWizardPage(Messages.selectFtpSource_PageName, new SelectFtpDataPage());

        } else if (name.matches(WEB_MATHCES_PATTERN)) {
            page = getWizardPage(Messages.selectWebSource_PageName, new SelectWebDataPage());
        } else {
            page = getWizardPage(Messages.selectLocalCatalSource_PageName, new SelectLocalDataPage());
        }
        getConfiguration();
        return page;
    }

    /**
     * @param pageName
     */
    private IWizardPage getWizardPage(final String pageName, final IWizardPage newPageInstance) {
        IWizardPage page = getWizard().getPage(pageName);
        if (page == null) {
            ((Wizard)getWizard()).addPage(newPageInstance);
        } else {
            return page;
        }
        return getWizard().getPage(pageName);
    }

    @Override
    public IWizardPage getPreviousPage() {
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);
        loaderList = new GeoptimaLoaderTypeSelectorWidget(getMainComposite(), this, null, getLoaders());
        loaderList.initializeWidget();
        onLoaderChanged();
        update();
    }

    @Override
    public void onLoaderChanged() {
        if (loaderList != null) {
            setCurrentLoader(getLoader(loaderList.getSelectedLoader()));
            update();
        }
    }

    @Override
    protected void update() {
        if (StringUtils.isEmpty(loaderList.getSelectedLoader())) {
            super.update();
            return;
        }
        setErrorMessage(null);
        setMessage(null);
        setPageComplete(true);
    }
}
