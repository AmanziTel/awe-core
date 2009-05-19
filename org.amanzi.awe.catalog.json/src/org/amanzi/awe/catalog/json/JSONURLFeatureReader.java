package org.amanzi.awe.catalog.json;
import static org.amanzi.awe.catalog.json.JSONReader.readURL;

import java.net.URL;
import java.util.Iterator;

import net.sf.json.JSONObject;

class JSONURLFeatureReader extends JSONFeatureReader {
    private URL feature_url;

    public JSONURLFeatureReader( final URL feature_url ) {
        super(null);
        this.feature_url = feature_url;
    }
    private void setupFeatures() {
        String content = readURL(feature_url);
        JSONObject json = JSONObject.fromObject(content);

        features = json.getJSONArray("features");
    }
    /** provide an iterator reset to the first element, if any */
    public Iterator<Feature> iterator() {
        setupFeatures();
        return super.iterator();
    }
}
