package org.amanzi.awe.catalog.json.beans;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Class container for GisGeo. Contains bbox, count, {@link GisCrs}, description, name,
 * {@link GisFeatureSource}.
 * 
 * @author Milan Dinic
 */
public class GisGeo {

    private double[] bbox;
    private int count;
    private GisCrs crs;
    private String description;
    private String name;
    private GisFeatureSource featureSource;
    /**
     * Constructor that parses provided json object.
     * 
     * @param object {@link JSONObject} object
     */
    public GisGeo( final JSONObject object ) {
        super();
        if (object.has("description")) {
            description = object.getString("description");
        }
        if (object.has("name")) {
            name = object.getString("name");
        }

        if (object.has("crs")) {
            crs = new GisCrs(object.getJSONObject("crs"));
        }

        if (object.has("count")) {
            count = object.getInt("count");
        }

        if (object.has("bbox")) {
            final JSONArray array = object.getJSONArray("bbox");
            bbox = new double[array.size()];
            for( int i = 0; i < array.size(); i++ ) {
                bbox[i] = array.getDouble(i);
            }
        }

        if (object.has("feature_source")) {
            featureSource = new GisFeatureSource(object.getJSONObject("feature_source"));
        }
    }
    public double[] getBbox() {
        return bbox;
    }
    public int getCount() {
        return count;
    }
    public GisCrs getCrs() {
        return crs;
    }
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public GisFeatureSource getFeatureSource() {
        return featureSource;
    }

}
