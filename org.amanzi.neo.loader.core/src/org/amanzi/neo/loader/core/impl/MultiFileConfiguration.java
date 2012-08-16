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

package org.amanzi.neo.loader.core.impl;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.neo.loader.core.IMultiFileConfiguration;
import org.amanzi.neo.loader.core.impl.internal.AbstractConfiguration;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class MultiFileConfiguration extends AbstractConfiguration implements IMultiFileConfiguration {

    private final Set<File> files = new LinkedHashSet<File>();

    public void addFile(final File file) {
        files.add(file);
    }

    public void addFiles(final Collection<File> files) {
        this.files.addAll(files);
    }

    @Override
    public Iterator<File> getFileIterator() {
        return files.iterator();
    }

    @Override
    public int getFileCount() {
        return files.size();
    }

}
