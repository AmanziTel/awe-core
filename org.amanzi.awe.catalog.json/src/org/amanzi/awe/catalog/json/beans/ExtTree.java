package org.amanzi.awe.catalog.json.beans;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Class container for ExtTree. Contains title and href.
 * 
 * @author Milan Dinic
 */
public class ExtTree {
    private String root;
    private String href;
    /**
     * Constructor that parses provided json object.
     * 
     * @param elements {@link JSONArray} object
     */
    public ExtTree( final JSONArray elements ) {
        super();

        for( int i = 0; i < elements.size(); i++ ) {
            final JSONObject object = elements.getJSONObject(i);
            if (object.has("href")) {
                href = object.getString("href");
            }
            if (object.has("root")) {
                root = object.getString("root");
            }
        }

    }
    public String getRoot() {
        return root;
    }

    public String getHref() {
        return href;
    }

}
