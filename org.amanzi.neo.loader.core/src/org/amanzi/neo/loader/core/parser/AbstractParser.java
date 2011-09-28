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

package org.amanzi.neo.loader.core.parser;

import java.io.PrintStream;

import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IProgressEvent;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.geotools.util.ListenerList;

/**
 * <p>
 * Abstract Parser - which provide work with progress listeners
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public abstract class AbstractParser<T extends IDataElement, C extends IConfigurationData> implements IParser<T, C> {
    private final ListenerList listeners = new ListenerList();
    private C properties;
    private ISaver<T> saver;
    private PrintStream outputStream;

    /**
     * Gets the properties.
     * 
     * @return the properties
     */
    public C getProperties() {
        return properties;
    }

    /**
     * Gets the saver.
     * 
     * @return the saver
     */
    public ISaver<T> getSaver() {
        return saver;
    }

    @Override
    public void init(C properties, ISaver<T> saver) {
        this.properties = properties;
        this.saver = saver;
    };

    @Override
    public void addProgressListener(ILoaderProgressListener listener) {
        getListeners().add(listener);
    }

    @Override
    public void removeProgressListener(ILoaderProgressListener listener) {
        getListeners().remove(listener);
    }

    /**
     * Gets the listeners.
     * 
     * @return the listeners
     */
    protected ListenerList getListeners() {
        return listeners;
    }

    
    @Override
    public boolean fireProgressEvent(final IProgressEvent event) {
        Object[] allListeners = getListeners().getListeners();
        for (Object listener : allListeners) {
            final ILoaderProgressListener singleListener = (ILoaderProgressListener)listener;
            SafeRunner.run(new ISafeRunnable() {
                @Override
                public void run() throws Exception {
                    singleListener.updateProgress(event);
                }

                @Override
                public void handleException(Throwable exception) {
                }
            });
        }
        return event.isCanseled();
    }

    @Override
    public PrintStream getPrintStream() {
        if (outputStream == null) {
            return System.out;
        }
        return outputStream;
    }

    @Override
    public void setPrintStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    protected void println(String s) {
        getPrintStream().println(s);
    }

    protected void info(String info) {
        println(info);
    }

    protected void error(String error) {
        println(error);

    }

    protected void exception(Throwable exception) {
        exception.printStackTrace(getPrintStream());

    }

    protected void exception(String s, Throwable exception) {
        println(s);
        exception(exception);
    }
}
