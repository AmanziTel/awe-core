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

package org.amanzi.neo.loader.ui.page.impl.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.view.widgets.CRSSelector.ICRSSelectorListener;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.impl.internal.AbstractConfiguration;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractLoaderPage<T extends AbstractConfiguration> extends WizardPage
        implements
            ILoaderPage<T>,
            ICRSSelectorListener {

    public static final int NUMBER_OF_COLUMNS = 4;

    // TODO: LN: 10.10.2012, make a Factory for Layouts
    private static final GridLayout STANDARD_LOADER_PAGE_LAYOUT = new GridLayout(NUMBER_OF_COLUMNS, false);

    private final List<ILoader<T, ? >> loaders = new ArrayList<ILoader<T, ? >>();

    private Composite mainComposite;

    private ILoader<T, ? > currentLoader;

    /**
     * @param pageName
     */
    protected AbstractLoaderPage(final String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(final Composite parent) {
        mainComposite = new Group(parent, SWT.NONE);
        mainComposite.setLayout(STANDARD_LOADER_PAGE_LAYOUT);

        setControl(mainComposite);
    }

    protected Composite getMainComposite() {
        return mainComposite;
    }

    @Override
    public void addLoader(final ILoader<T, ? > loader) {
        loaders.add(loader);
    }

    @Override
    public List<ILoader<T, ? >> getLoaders() {
        return loaders;
    }

    protected void update() {
        setPageComplete(checkPage());
    }

    protected void autodefineLoader() {
        ILoader<T, ? > lastUnknown = null;
        IValidationResult unkonwnResult = null;
        ILoader<T, ? > definedLoader = null;
        for (final ILoader<T, ? > loader : loaders) {
            final IValidationResult result = loader.isAppropriate(getConfiguration());
            switch (result.getResult()) {
            default:
            case FAIL:
                break;
            case UNKNOWN:
                lastUnknown = loader;
                unkonwnResult = result;
                break;
            case SUCCESS:
                definedLoader = loader;
                break;
            }

            if (definedLoader != null) {
                break;
            }
        }

        if (definedLoader != null) {
            setCurrentLoader(definedLoader);
        } else if (lastUnknown != null) {
            setCurrentLoader(lastUnknown);
            showMessage(unkonwnResult);
        }

        update();
    }

    protected boolean checkPage() {
        setErrorMessage(null);
        setMessage(null);

        if (getCurrentLoader() == null) {
            setErrorMessage(Messages.AbstractLoaderPage_LoaderNotSelectedError);
            return false;
        }

        return check(getCurrentLoader().validate(getConfiguration()));
    }

    @Override
    public ILoader<T, ? > getCurrentLoader() {
        return currentLoader;
    }

    protected void setCurrentLoader(final ILoader<T, ? > currentLoader) {
        this.currentLoader = currentLoader;
    }

    protected T getConfiguration() {
        return ((ILoaderWizard< ? >)getWizard()).getConfiguration(this);
    }

    private boolean check(final IValidationResult result) {
        if (result.getResult() != IValidationResult.Result.SUCCESS) {
            showMessage(result);
            return result.getResult() != IValidationResult.Result.FAIL;
        }

        return true;
    }

    protected void showMessage(final IValidationResult result) {
        switch (result.getResult()) {
        case FAIL:
            setErrorMessage(result.getMessages());
            break;
        case UNKNOWN:
            setMessage(result.getMessages(), WARNING);
            break;
        default:
            break;
        }
    }

    @Override
    public void onCRSSelected(final CoordinateReferenceSystem crs) {
        final T configuration = getConfiguration();

        configuration.setCRS(crs);
    }

    protected ILoader<T, ? > getLoader(final String loaderName) {
        for (final ILoader<T, ? > loader : loaders) {
            if (loader.getName().equals(loaderName)) {
                return loader;
            }
        }

        return null;
    }
}
