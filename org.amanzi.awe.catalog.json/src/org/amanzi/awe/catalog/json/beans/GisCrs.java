package org.amanzi.awe.catalog.json.beans;

import net.sf.json.JSONObject;
/**
 * Class container for GisCrs. Contains type and name.
 * 
 * @author Milan Dinic
 */
public class GisCrs {

    private String type;
    private String name;
    /**
     * Constructor that parses provided json object.
     * 
     * @param object {@link JSONObject} object
     */
    public GisCrs( final JSONObject object ) {
        super();
        if (object.has("type")) {
            type = object.getString("type");
        }
        if (object.has("properties")) {
            final JSONObject property = object.getJSONObject("properties");
            if (property.has("name")) {
                name = property.getString("name");
            }
        }
    }
    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

}
