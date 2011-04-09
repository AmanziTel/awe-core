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

package org.amanzi.neo.services.utils;

import java.util.Iterator;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public abstract class ConvertIterator<E, T> implements Iterator<E> {
    private final Iterator<T> baseIterator;

    public ConvertIterator(Iterator<T> baseIterator) {
        this.baseIterator = baseIterator;
    }

    @Override
    public boolean hasNext() {
        return baseIterator.hasNext();
    }

    @Override
    public E next() {
        return convert(baseIterator.next());
    }

    protected abstract E convert(T next);

    @Override
    public void remove() {
        baseIterator.remove();
    }

}
