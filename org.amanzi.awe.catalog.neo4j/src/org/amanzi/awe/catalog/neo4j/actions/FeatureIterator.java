package org.amanzi.awe.catalog.neo4j.actions;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class provides the API for various anonymous inner classes that can produce a stream of JSON
 * features. We implement both the Iterator and the Enumeration interfaces to be friendly to both
 * the java5-style 'for loop' and the JRuby 'each' method. This allows, for example, the JSON to
 * contain the features directly as well as reference another data source, like a file or another
 * URL, that will provide the necessary data.
 * 
 * @author craig
 */
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