package org.amanzi.awe.afp.filters;

import java.util.HashMap;

import org.neo4j.graphdb.Node;

public class AfpFilter {
	static final private String delimiter = "--";
	static public final String FILTER_LIKE ="flike";
	static public final String FILTER_NOT_LIKE ="fnotlike";
	
	HashMap<String, String> filters = new HashMap<String,String>();

	public void addFilter(String type, String propName, String regExp) {
		filters.put(type+delimiter+propName, regExp);
	}
	public void removeFilter(String type, String propName, String regExp) {
		if(filters.containsKey(type+delimiter+propName)) {
			filters.remove(type+delimiter+propName);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(String k: filters.keySet()) {
			String val = filters.get(k);
			sb.append(k);
			sb.append(delimiter);
			sb.append(val);
		}
		return sb.toString();
	}
	
	public static AfpFilter getFilter(String filterstring) {
		String[] tokens = filterstring.split(delimiter);
		AfpFilter ret = new AfpFilter();
		
		if(tokens.length > 0 && (tokens.length % 3) ==0) {
			for(int i=0; i < tokens.length; i+=3)
				ret.addFilter(tokens[0], tokens[1], tokens[2]);
			return ret;
		}
		return null;
	}
	
	public boolean like(Node n) {
		boolean ret = true;
		for(String fkey:filters.keySet()) {
			String t[] = fkey.split(delimiter);
			
			String filter = t[0];
			String col = t[1];
			String exp = filters.get(fkey);
			
			if(!ret) return false;
			
			try {
				Object v = n.getProperty(col);
				if(v == null) {
					ret = ret && false;
					continue;
				}

				if(FILTER_LIKE.compareTo(filter) == 0) {
					if(v instanceof String) {
						ret = ret && like((String)v, exp);
					}
					continue;
				}
				if(FILTER_NOT_LIKE.compareTo(filter) == 0) {
					if(v instanceof String) {
						ret = ret && notlike((String)v, exp);
					}
					continue;
				}
				
			} catch(Exception e) {
				// ignore and continue
			}
		}
		return ret;
	}
	
	protected boolean like(String a, String exp) {
		return a.matches(exp);
	}
	protected boolean notlike(String a, String exp) {
		return !a.matches(exp);
	}
}
