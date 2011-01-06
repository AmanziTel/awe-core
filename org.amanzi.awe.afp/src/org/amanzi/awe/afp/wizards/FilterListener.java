package org.amanzi.awe.afp.wizards;

import java.util.ArrayList;


public interface FilterListener {
	
	void onFilterSelected(String columnName, ArrayList<String> selectedValues);

}
