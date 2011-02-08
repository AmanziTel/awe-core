package org.amanzi.awe.afp.filters;

import java.util.HashMap;

import org.neo4j.graphdb.Node;

public class AfpRowFilter {
	
	static final private String row_delimiter = ":";
	
//	private ArrayList<AfpColumnFilter> colFilters;
	private HashMap<String, AfpColumnFilter> colFilters;
	
	public AfpRowFilter() {
		this.colFilters = new HashMap<String, AfpColumnFilter>();
	}
	
	public AfpRowFilter(HashMap<String, AfpColumnFilter> colFilters) {
		this.colFilters = colFilters;
	}

	public void addColumn(AfpColumnFilter colFilter) {
		if (!(colFilter.getValues().isEmpty() || colFilter.getValues() == null))
			colFilters.put(colFilter.getFilterName(), colFilter);
	}
	
	public void removeColumn(AfpColumnFilter colFilter) {
		colFilters.remove(colFilter.getFilterName());
	}
	
	public static AfpRowFilter getFilter(String filterstring) {
		String[] tokens = filterstring.split(row_delimiter);
		AfpRowFilter ret = new AfpRowFilter();
		
		if(tokens.length > 0) {
			for(int i=1; i < tokens.length; i++)
				ret.addColumn(AfpColumnFilter.getFilter(tokens[i]));
			return ret;
		}
		return null;
	}
	
	public AfpColumnFilter getColFilter(String colName, String nodeType){
		if (colFilters.containsKey(nodeType + AfpColumnFilter.col_delimiter + colName)){
			return colFilters.get(nodeType + AfpColumnFilter.col_delimiter + colName);
		}
		
		AfpColumnFilter colFilter = new AfpColumnFilter(colName, nodeType);
		colFilters.put(colName, colFilter);
		return colFilter;
	}
	
	public void clear(){
		colFilters.clear();
	}
	
	/**
	 * AND the column conditions. If any of column criterion is not met, return false
	 * @param n
	 * @return
	 */
	public boolean equal(Node n){
		
		for(AfpColumnFilter filter : colFilters.values()){
			if(!filter.equal(n))
				return false; 
		}
		
		
		return true;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("frow");
		for (AfpColumnFilter filter : colFilters.values()){
			sb.append(row_delimiter);
			sb.append(filter.toString());
		}
		
		return sb.toString();
	}

}
