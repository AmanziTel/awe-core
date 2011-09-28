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

package org.amanzi.neo.loader.core.newparser;

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IProgressEvent;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.geotools.util.ListenerList;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractParser<T1 extends ISaver< ? extends IModel, T3, T2>, T2 extends IConfiguration, T3 extends IData>
        implements
            IParser<T1, T2, T3> {
    protected static Logger LOGGER;
    //TODO: LN: do not use ListenerList - use List of Listeners
    private final ListenerList listeners = new ListenerList();
    protected final int PERCENTAGE_FIRE = 2;
    private int percentage = 0;

    @Override
    public void addProgressListener(ILoaderProgressListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeProgressListener(ILoaderProgressListener listener) {
        listeners.remove(listener);
    }

    //TODO: LN: does it make sense to have this method? 
    //listeners used only in AbstractParser so we have direct access to this field
    protected ListenerList getListeners() {
        return listeners;
    }

    /*
     * Configuraton data of Parser
     */
    protected T2 config;

    /*
     * Savers for Data
     */
    protected List<T1> savers;

    /*
     * Currently parsed file
     */
    protected File currentFile;

    @Override
    public void init(T2 configuration, List<T1> saver) {
        this.config = configuration;
        this.savers = saver;
    }

    /**
     * Parses single IData element
     * 
     * @return next parsed IData object, or null in case if parsing finished
     */
    protected abstract T3 parseElement();

    /**
     * Parses single file
     * 
     * @param file
     */
    protected void parseFile(File file) {
        currentFile = file;
        T3 element = parseElement();
        long startTime = System.currentTimeMillis();
        while (element != null) {
            for (ISaver< ? , T3, T2> saver : savers) {
                saver.saveElement(element);
            }
            element = parseElement();
        }
        for (ISaver< ? , T3, T2> saver : savers) {
            saver.finishUp();
            //TODO: LN: bad log info - "Saving data", but not mentioned that data from one file
            //also directly methods for output of time can be organized in one method that 
            //will be used everywhere
            LOGGER.info("Saving data finished in: " + (System.currentTimeMillis() - startTime) + ": file " + currentFile.getName());
        }
    }

    /**
     * Fire sub progress event.
     * 
     * @param element the element
     * @param event the event
     */
    protected boolean fireSubProgressEvent(File element, final IProgressEvent event) {
        return fireProgressEvent(new ProgressEventImpl(event.getProcessName(), ((percentage + event.getPercentage()) / 100)
                / config.getFilesToLoad().size()));
    }

    @Override
    public void run() {
        //TODO: LN: no time logging
        for (File file : config.getFilesToLoad()) {
            parseFile(file);
        }
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
                    //TODO: LN: where is exception handling? 
                }
            });
        }

        return event.isCanseled();
    }
}
