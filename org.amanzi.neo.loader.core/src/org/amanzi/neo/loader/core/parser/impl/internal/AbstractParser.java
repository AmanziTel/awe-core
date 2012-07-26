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
import org.amanzi.neo.loader.core.exception.LoaderException;
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

    boolean parsingStarted = false;

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

    protected D parseToNextElement() throws LoaderException {
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
    public void init(final C configuration) throws LoaderException {
        this.configuration = configuration;
    }

    @Override
    public void setProgressMonitor(final IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    protected C getConfiguration() {
        return configuration;
    }

    @Override
    public void finishUp() {
        monitor.done();
    }

    private void onNewFileParsingStarted(final File file) {
        for (IFileParsingStartedListener listener : listeners) {
            listener.onFileParsingStarted(file);
        }
    }

    @Override
    public void addFileParsingListener(final IFileParsingStartedListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeFileParsingListener(final IFileParsingStartedListener listener) {
        listeners.remove(listener);
    }
}
