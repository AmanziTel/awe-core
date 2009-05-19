package org.amanzi.awe.views.network.beans;

import net.sf.json.JSONObject;
/**
 * Class container for ExtTreeNode. Contains id, href, text, className and leaf indicator.
 * 
 * @author Milan Dinic
 */
public class ExtTreeNode {

    private String id;
    private String href;
    private String text;
    private boolean leaf;
    private String className;

    public ExtTreeNode( final String text, final String href ) {
        super();
        this.text = text;
        this.leaf = false;
        this.href = href;
    }
    /**
     * Constructor that parses provided json object.
     * 
     * @param object {@link JSONObject} object
     */
    public ExtTreeNode( final JSONObject object ) {
        super();
        if (object.has("id")) {
            id = object.getString("id");
        }
        if (object.has("href")) {
            href = object.getString("href");
        }
        if (object.has("text")) {
            text = object.getString("text");
        }
        if (object.has("className")) {
            className = object.getString("className");
        }
        if (object.has("leaf")) {
            leaf = object.getBoolean("leaf");
        }
    }
    public String getId() {
        return id;
    }
    public String getHref() {
        return href;
    }
    public String getText() {
        return text;
    }
    public boolean isLeaf() {
        return leaf;
    }
    public String getClassName() {
        return className;
    }

}
