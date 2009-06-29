package org.amanzi.awe.catalog.neo.actions;


import java.util.Iterator;



public class NeoFeatureReader extends FeatureIterator {
    protected Object[] features;
    private int index;
    public NeoFeatureReader( final Object[] features ) {
        this.index = 0;
        this.features = features;
    }
    private Feature getFeature() {
        try {
            NeoFeature feature = (NeoFeature)features[index++];
            return feature;
        } catch (Throwable e) {
            return null;
        }
    }
    /** provide an iterator reset to the first element, if any */
    public Iterator<Feature> iterator() {
        index = 0;
        return new Iterator<Feature>(){
            public boolean hasNext() {
                return features != null && features.length > index;
            }
            public Feature next() {
                return getFeature();
            }
            public void remove() {
            }
        };
    }
}
