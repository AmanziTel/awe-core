package org.amanzi.awe.catalog.json.beans;

import net.sf.json.JSONObject;
/**
 * Class container for ExtTree. Contains root and href.
 * 
 * @author Milan Dinic
 */
public class GisFeatureSource {
    private String type;
    private String href;
    /**
     * Constructor that parses provided json object.
     * 
     * @param object {@link JSONObject} object
     */
    public GisFeatureSource( final JSONObject object ) {
        super();
        if (object.has("href")) {
            href = object.getString("href");
        }
        if (object.has("type")) {
            type = object.getString("type");
        }
    }
    public String getType() {
        return type;
    }
    public String getHref() {
        return href;
    }

}
