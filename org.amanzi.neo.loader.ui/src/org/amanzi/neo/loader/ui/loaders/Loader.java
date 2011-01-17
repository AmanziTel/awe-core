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

package org.amanzi.neo.loader.ui.loaders;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.amanzi.awe.catalog.neo.NeoService;
import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * <p>
 * Common loader for starting parse/save mechanism
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class Loader<T extends IDataElement, T2 extends IConfigurationData> implements ILoader<T, T2> {
    private final ILoaderInputValidator<T2> fakeValidator = new ILoaderInputValidator<T2>() {

        private IValidateResult unknownResult = new ValidateResultImpl(Result.UNKNOWN, NeoLoaderPluginMessages.Loader_fake_validator);

        @Override
        public IValidateResult validate(T2 data) {
            return unknownResult;
        }

        @Override
        public void filter(T2 data) {
            // do nothing
        }

    };
    private IParser<T, T2> parser;
    private ISaver<T> saver;
    private String description = "";
    private T2 data;
    private DatabaseAccessType accessType;
    private ILoaderInputValidator<T2> validator;
    private PrintStream outputStream;

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setup(DatabaseAccessType accessType, T2 data) {
        this.accessType = accessType;
        this.data = data;
        getValidator().filter(data);
    }

    @Override
    public void setParser(IParser<T, T2> parser) {
        this.parser = parser;

    }

    @Override
    public void setSaver(ISaver<T> saver) {
        this.saver = saver;
    }

    @Override
    public void addProgressListener(ILoaderProgressListener listener) {
        parser.addProgressListener(listener);
    }

    @Override
    public void removeProgressListener(ILoaderProgressListener listener) {
        parser.removeProgressListener(listener);
    }

    @Override
    public void load() {
        try {
            if (getPrintStream() != null) {
                parser.setPrintStream(getPrintStream());
                saver.setPrintStream(getPrintStream());
            }
            parser.init(data, saver);
            parser.parce();
        } finally {
            finishup();
        }
    }

    /**
     * Finishup.
     */
    protected void finishup() {
        updateCatalog();
        NeoServicesUiPlugin.getDefault().getUpdateViewManager().fireUpdateView(new UpdateDatabaseEvent(UpdateViewEventType.GIS));
        // DatabaseManager.getInstance().setDatabaseAccessType(DatabaseAccessType.DEFAULT);

    }

    public static void updateCatalog() {
        try {
            NeoServiceProviderUi neoProvider = NeoServiceProviderUi.getProvider();
            if (neoProvider != null) {
                String databaseLocation = neoProvider.getDefaultDatabaseLocation();

                ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
                URL url;
                url = new URL("file://" + databaseLocation);

                List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
                for (IService service : services) {
                    NeoService serviceold = catalog.getById(NeoService.class, service.getIdentifier(), new NullProgressMonitor());
                    if (serviceold != null) {
                        //TODO refactor for working in 2 database modes
//                        serviceold.updateResource();
                        catalog.replace(service.getIdentifier(), service);
                    } else {
                        catalog.add(service);
                    }
                }

            }
        } catch (MalformedURLException e) {
            //TODO handle exception
            e.printStackTrace();
        }
    }

    public static void sendUpdateEvent(UpdateViewEventType aType) {
        NeoServicesUiPlugin.getDefault().getUpdateViewManager().fireUpdateView(new UpdateDatabaseEvent(aType));
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setValidator(ILoaderInputValidator<T2> validator) {
        this.validator = validator;
    }

    @Override
    public ILoaderInputValidator<T2> getValidator() {
        if (validator == null) {
            // return fake validator
            return fakeValidator;
        }
        return validator;
    }

    @Override
    public PrintStream getPrintStream() {
        return outputStream;
    }

    @Override
    public void setPrintStream(PrintStream outputStream) {
        this.outputStream = outputStream;

    }
}
