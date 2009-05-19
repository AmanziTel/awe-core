package org.amanzi.awe.catalog.json;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONFeatureReader extends FeatureIterator {
    protected JSONArray features;
    private int index;
    public JSONFeatureReader( final JSONArray features ) {
        this.index = 0;
        this.features = features;
    }
    private Feature getFeature() {
        try {
            JSONObject feature = features.getJSONObject(index++);
            return new JSONFeature(feature);
        } catch (Throwable e) {
            return null;
        }
    }
    /** provide an iterator reset to the first element, if any */
    public Iterator<Feature> iterator() {
        index = 0;
        return new Iterator<Feature>(){
            public boolean hasNext() {
                return features != null && features.size() > index;
            }
            public Feature next() {
                return getFeature();
            }
            public void remove() {
            }
        };
    }
}
