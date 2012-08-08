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

package org.amanzi.neo.loader.core.parser.impl.internal;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.exception.impl.GeneralParsingException;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.parser.IParser;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractParser<C extends IConfiguration, D extends IData> implements IParser<C, D> {

    private C configuration;

    private IProgressMonitor monitor;

    private D nextElement;

    private boolean actual = false;

    private final Set<IFileParsingStartedListener> listeners = new HashSet<IFileParsingStartedListener>();

    private boolean parsingStarted = false;

    private int previousWork = 0;

    @Override
    public boolean hasNext() {
        if (!actual) {
            nextElement = parseToNextElement();
            actual = true;

            fireFileParsingEvent();
        }
        return nextElement != null;
    }

    @Override
    public D next() {
        if (!actual) {
            nextElement = parseToNextElement();

            fireFileParsingEvent();
        }
        actual = false;

        if (monitor.isCanceled()) {
            nextElement = null;
        }

        return nextElement;
    }

    private void fireFileParsingEvent() {
        if (!parsingStarted) {
            File file = getFileFromConfiguration(configuration);
            if (file != null) {
                onNewFileParsingStarted(file);
            }
            parsingStarted = true;
        }
    }

    protected abstract File getFileFromConfiguration(C configuration);

    protected D parseToNextElement() {
        try {
            return parseNextElement();
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            throw new GeneralParsingException(e);
        }
    }

    protected abstract D parseNextElement() throws IOException;

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(final C configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setProgressMonitor(final String monitorName, final IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    protected IProgressMonitor getProgressMonitor() {
        return monitor;
    }

    protected void work(final int ticks) {
        int work = ticks - previousWork;
        previousWork = ticks;

        monitor.worked(work);
    }

    protected C getConfiguration() {
        return configuration;
    }

    @Override
    public void finishUp() {
        monitor.done();
        listeners.clear();
    }

    protected void onNewFileParsingStarted(final File file) {
        for (IFileParsingStartedListener listener : listeners) {
            listener.onFileParsingStarted(file);
        }
    }

    @Override
    public void addFileParsingListener(final IFileParsingStartedListener listener) {
        listeners.add(listener);
    }
}
