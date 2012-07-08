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

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.exception.LoaderException;
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

    @Override
    public boolean hasNext() {
        prepareNextElement();
        return nextElement != null;
    }

    @Override
    public D next() {
        prepareNextElement();
        return nextElement;
    }

    private void prepareNextElement() {
        if (nextElement == null) {
            nextElement = parseNextElement();
        }
    }

    protected abstract D parseNextElement();

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(C configuration) throws LoaderException {
        this.configuration = configuration;
    }

    @Override
    public void setProgressMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    protected C getConfiguration() {
        return configuration;
    }

    protected IProgressMonitor getProgressMonitor() {
        return monitor;
    }
}
