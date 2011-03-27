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
import java.util.NoSuchElementException;

/**
 * <p>
 * Wrapper for iterator with additional filter
 * </p>
 * 
 * @author TsAr
 * @param <E>
 * @since 1.0.0
 */
public abstract class FilteredIterator<E> implements Iterator<E> {
    private final Iterator<E> iterator;
    private E next;
    private Boolean haveNext;

    public FilteredIterator(Iterator<E> iterator) {
        this.iterator = iterator;
        next = null;
        haveNext = null;
    }

    public abstract boolean canBeNext(E elem);

    @Override
    public boolean hasNext() {
        if (haveNext != null) {
            return haveNext;
        }
        boolean result=false;
        while (iterator.hasNext()) {
            E elem = iterator.next();
            if (canBeNext(elem)) {
                next = elem;
                result = true;
                break;
            }
        }
        haveNext = result;
        return haveNext;
    }

    @Override
    public E next() {
        try {
            if (haveNext == null) {
                hasNext();
            } 
            if (haveNext) {
                return next;
            } else {
                throw new NoSuchElementException();
            }
        } finally {
            haveNext = null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
