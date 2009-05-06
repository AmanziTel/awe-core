package org.amanzi.awe.catalog.json.beans;

import net.sf.json.JSONObject;
/**
 * Class container for awe data. Contains {@link ExtTree} object and {@link ExtGis} object.
 * 
 * @author Milan Dinic
 */
public class ExtJSON {
    private ExtTree extTree;
    private ExtGis extGis;
    /**
     * Constructor that parses provided json object.
     * 
     * @param object {@link JSONObject} object
     */
    public ExtJSON( final JSONObject object ) {
        super();
        if (object.has("tree")) {
            this.extTree = new ExtTree(object.getJSONArray("tree"));
        }
        if (object.has("gis")) {
            this.extGis = new ExtGis(object.getJSONArray("gis"));
        }
    }

    public ExtTree getExtTree() {
        return extTree;
    }

    public ExtGis getExtGis() {
        return extGis;
    }

}
