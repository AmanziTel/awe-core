package org.amanzi.awe.catalog.json.beans;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Class container for ExtTree. Contains root and href.
 * 
 * @author Milan Dinic
 */
public class ExtGis {
    private String title;
    private String href;

    /**
     * Constructor that parses provided json object.
     * 
     * @param elements {@link JSONArray} object
     */
    public ExtGis( final JSONArray elements ) {
        super();
        for( int i = 0; i < elements.size(); i++ ) {
            final JSONObject object = elements.getJSONObject(i);
            if (object.has("href")) {
                href = object.getString("href");
            }
            if (object.has("title")) {
                title = object.getString("title");
            }
        }

    }
    public String getHref() {
        return href;
    }
    public String getTitle() {
        return title;
    }

}
