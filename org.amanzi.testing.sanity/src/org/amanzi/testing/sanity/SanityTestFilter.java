/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.testing.sanity;

import java.lang.reflect.Method;
import org.amanzi.testing.LongRunning;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

/**
 * Test Filter derived from the Filter class to 
 * exclude the long running test cases based on annotations
 *
 *
 *@author Rahul Jain
 */

public class SanityTestFilter extends Filter{

	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return new String("Filters out long running test cases");
	}

	/**
	 * Filter the test cases based on annotations
	 */
	@Override
	public boolean shouldRun(Description description) {
		
		if(description.isTest()) {
			String displayName = description.getDisplayName();
			int stIndex = displayName.indexOf("(");
			int endIndex = displayName.indexOf(")", stIndex+1);
			
			if(stIndex >0 && endIndex >0 && endIndex > stIndex) {
				String testName = displayName.substring(0,stIndex);
				String className = displayName.substring(stIndex+1, endIndex);				
				try {
					Class klass = Class.forName(className);
					
				    Method[] methods = klass.getDeclaredMethods();
					for (Method method : methods) {
						if (method.getName().equals(testName)) {
							LongRunning isLongRunning = method
									.getAnnotation(LongRunning.class);
							if (isLongRunning != null) {
								// skip this test case
								return false;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

    
}