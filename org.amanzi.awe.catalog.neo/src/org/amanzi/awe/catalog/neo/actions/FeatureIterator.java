package org.amanzi.awe.catalog.neo.actions;

import java.util.Enumeration;
import java.util.Iterator;


public abstract class FeatureIterator implements Iterable<Feature>, Enumeration<Feature> {
    private Iterator<Feature> iter = null;
    public final boolean hasMoreElements() {
        if (iter == null) {
            iter = iterator();
        }
        return iter.hasNext();
    }
    public final Feature nextElement() {
        return hasMoreElements() ? iter.next() : null;
    }
}