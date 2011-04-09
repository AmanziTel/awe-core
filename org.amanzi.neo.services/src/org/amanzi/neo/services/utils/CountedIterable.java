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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Counted iterable wrapper
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CountedIterable<T> implements Iterable<T> {

    private final Iterable<T> baseIterable;
    private CountedIteratorWr<T> createdIter;
    private Integer elementCount = null;
    public CountedIterable(Iterable<T> baseIterable) {
        this.baseIterable = baseIterable;
        createdIter = (CountedIteratorWr<T>)iterator();

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Iterator<T> iterator() {
        return new CountedIteratorWr(baseIterable.iterator());
    }

    public int getElementCount(boolean useCache) {
        if (useCache && elementCount != null) {
            return elementCount;
        }
        int count = 0;
        Iterator<T> it = baseIterable.iterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        elementCount = count;
        return elementCount;
    }
    public T getElement(final int i) {
        if (i >= createdIter.getCacheMinIndex() && i < createdIter.getCachedMax()) {
            return createdIter.getCashedValue(i);
        }
        if (createdIter.getIndex() - 1 > i) {
            createdIter = (CountedIteratorWr<T>)iterator();
        }
        T res = null;
        while (createdIter.hasNext()) {
            res = createdIter.next();
            if (createdIter.getIndex() - 1 == i) {
                return res;
            }
        }
        return null;
    }

    public static class CountedIteratorWr<M> implements Iterator<M> {
        private static final int CACHE_SIZE = 100;
        List<M> cachedList = new ArrayList<M>(CACHE_SIZE);
        private final Iterator<M> baseIterator;
        private int index;
        private int startId;

        public CountedIteratorWr(Iterator<M> baseIterator) {
            this.baseIterator = baseIterator;
            index = 0;
            startId = 0;

        }

        public int getCachedMax() {
            return startId + cachedList.size();
        }

        @Override
        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        public int getCacheMinIndex() {
            return startId;
        }

        public M getCashedValue(int index) {
            return cachedList.get(index - startId);
        }

        @Override
        public M next() {
            final M result = baseIterator.next();
            cachedList.add(result);
            if (cachedList.size() >= CACHE_SIZE) {
                cachedList.remove(0);
                startId++;
            }
            index++;
            return result;
        }

        @Override
        public void remove() {
            baseIterator.remove();
        }

        public int getIndex() {
            return index;
        }

    }

}
