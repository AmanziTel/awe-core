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

import org.amanzi.neo.geoptima.loader.core.IRemoteSupportConfiguration;
import org.amanzi.neo.geoptima.loader.impl.core.RemoteSupportConfiguration;
import org.amanzi.neo.geoptima.loader.ui.wizard.impl.GeoptimaLoaderWizard;
import org.amanzi.neo.loader.core.ILoader;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractConfigurationPage extends WizardPage {
    /**
     * @param pageName
     */
    protected AbstractConfigurationPage(final String pageName) {
        super(pageName);
        setTitle(pageName);
    }

    private static final GridLayout STANDARD_LOADER_PAGE_LAYOUT = new GridLayout(1, false);

    private Composite mainComposite;

    private IRemoteSupportConfiguration configuration;

    private ILoader<RemoteSupportConfiguration, ? > loader;

    @Override
    public void createControl(final Composite parent) {
        mainComposite = new Group(parent, SWT.NONE);
        mainComposite.setLayout(STANDARD_LOADER_PAGE_LAYOUT);
        this.configuration = ((GeoptimaLoaderWizard)getWizard()).getWizardConfiguration();
        this.loader = ((GeoptimaLoaderTypeSelectionPage)getWizard().getPages()[0]).getCurrentLoader();
        setControl(mainComposite);
    }

    /**
     * @return Returns the mainComposite.
     */
    public Composite getMainComposite() {
        return mainComposite;
    }

    /**
     * @return Returns the configuration.
     */
    public IRemoteSupportConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @return Returns the loader.
     */
    public ILoader<RemoteSupportConfiguration, ? > getLoader() {
        return loader;
    }
}
