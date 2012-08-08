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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.IMultiFileConfiguration;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.parser.IParser.IFileParsingStartedListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class MultiStreamParser<S extends ISingleFileConfiguration, P extends IParser<S, D>, C extends IMultiFileConfiguration, D extends IData>
        extends
            AbstractParser<C, D> implements IFileParsingStartedListener {

    private final class ParserIterator implements Iterator<P> {

        private final Iterator<File> fileIterator;

        public ParserIterator(final Iterator<File> fileIterator) {
            this.fileIterator = fileIterator;
        }

        @Override
        public boolean hasNext() {
            return fileIterator.hasNext();
        }

        @Override
        public P next() {
            return createParser(fileIterator.next());
        }

        @Override
        public void remove() {
            fileIterator.remove();
        }

    }

    private ParserIterator parserIterator;

    private P currentParser;

    @Override
    public void init(final C configuration) {
        super.init(configuration);

        parserIterator = new ParserIterator(configuration.getFileIterator());
    }

    @Override
    protected D parseNextElement() throws IOException {
        if ((currentParser != null) && currentParser.hasNext()) {
            return currentParser.next();
        } else {
            if (parserIterator.hasNext()) {
                parserIterator.next();
                return parseNextElement();
            }
        }
        return null;
    }

    private P createParser(final File file) {
        currentParser = createParserInstance();
        S configuration = createSingleFileConfiguration(file, getConfiguration());
        currentParser.init(configuration);
        currentParser.addFileParsingListener(this);

        SubProgressMonitor monitor = new SubProgressMonitor(getProgressMonitor(), 1);
        currentParser.setProgressMonitor(file.getName(), monitor);

        work(1);

        return currentParser;
    }

    protected abstract P createParserInstance();

    protected abstract S createSingleFileConfiguration(File file, C configuration);

    @Override
    public void onFileParsingStarted(final File file) {
        onNewFileParsingStarted(file);
    }

    @Override
    public void setProgressMonitor(final String monitorName, final IProgressMonitor monitor) {
        monitor.beginTask(monitorName, getConfiguration().getFileCount());

        super.setProgressMonitor(monitorName, monitor);
    }

    @Override
    protected File getFileFromConfiguration(final IMultiFileConfiguration configuration) {
        return null;
    }

}
