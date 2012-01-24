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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * common parser actions
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractParser<T1 extends ISaver< ? extends IModel, T3, T2>, T2 extends IConfiguration, T3 extends IData>
        implements
            IParser<T1, T2, T3> {
    private static final Logger LOGGER = Logger.getLogger(AbstractParser.class);;

    /**
     * progress monitor listeners
     */
    private final List<ILoaderProgressListener> listeners = new ArrayList<ILoaderProgressListener>();
    protected final int PERCENTAGE_FIRE = 1;
    /**
     * temporary files instance
     */
    protected File tempFile;
    /**
     * cancel progress monitor flag
     */
    protected boolean isCanceled = false;

    public void addProgressListener(ILoaderProgressListener listener) {
        listeners.add(listener);
    }

    public void removeProgressListener(ILoaderProgressListener listener) {
        listeners.remove(listener);
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
        this.config.computeSourceFiles();
        this.savers = saver;
    }

    /**
     * Parses single IData element
     * 
     * @return next parsed IData object, or null in case if parsing finished
     */
    protected abstract T3 parseElement(IProgressMonitor monitor);

    /**
     * Parses single file
     * 
     * @param file
     * @throws AWEException
     */
    protected void parseFile(File file, IProgressMonitor monitor) throws AWEException {
        monitor.beginTask("Loading file <" + file.getName() + ">", 100);
        
        T3 element = parseElement(monitor);

        long startTime = System.currentTimeMillis();
        while (element != null) {
            for (ISaver< ? , T3, T2> saver : savers) {
                try {
                    saver.saveElement(element);
                } catch (DatabaseException e) {
                    AweConsolePlugin.error("Error while saving line ");
                    LOGGER.error("Error while saving line ", e);
                    saver.finishUp();
                    throw new DatabaseException(e);
                }
            }
            element = parseElement(monitor);
            if (isCanceled) {
                break;
            }
        }
        
        monitor.done();
        
        LOGGER.info("File " + currentFile.getName() + "  data saving finished in: " + getOperationTime(startTime));
    }

    protected long getOperationTime(long time) {
        return System.currentTimeMillis() - time;
    }

    @Override
    public void run(IProgressMonitor monitor) throws AWEException {
        
        long globalStartTime = System.currentTimeMillis();
        for (File file : config.getFilesToLoad()) {
            currentFile = file;
            long startTime = System.currentTimeMillis();
            parseFile(file, monitor);
            LOGGER.info("File " + currentFile.getName() + " Parsing/Saving data finished in: " + getOperationTime(startTime));
        }
        try {
            finishUp();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        LOGGER.info("All files Parsing/Saving finished in: " + getOperationTime(globalStartTime));
    }

    protected void finishUp() throws Exception {
        finishUpParse();
        for (ISaver< ? , T3, T2> saver : savers) {
            saver.finishUp();
        }
    }

    /**
     * finishup common parser methods;
     */
    protected abstract void finishUpParse();
    
}
