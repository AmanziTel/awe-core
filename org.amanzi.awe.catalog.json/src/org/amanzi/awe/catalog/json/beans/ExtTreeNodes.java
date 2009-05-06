package org.amanzi.awe.catalog.json.beans;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
/**
 * Class container for ExtTreeNodes. Contains list of {@link ExtTreeNode} object.
 * 
 * @author Milan Dinic
 */
public class ExtTreeNodes {
    private List<ExtTreeNode> list;
    /**
     * Constructor that parses provided json object.
     * 
     * @param string {@link String} object that represent array of {@link ExtTreeNode} json objects
     */
    public ExtTreeNodes( String string ) {
        this();
        final JSONArray array = JSONArray.fromObject(string);
        for( int i = 0; i < array.size(); i++ ) {
            list.add(new ExtTreeNode(array.getJSONObject(i)));
        }
    }

    public ExtTreeNodes() {
        super();
        list = new ArrayList<ExtTreeNode>();
    }

    public List<ExtTreeNode> getList() {
        return list;
    }

}
