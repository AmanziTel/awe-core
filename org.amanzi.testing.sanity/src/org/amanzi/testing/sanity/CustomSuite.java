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

import org.junit.internal.runners.InitializationError;
import org.junit.runners.Suite;

/**
 * Custom Suite derived from the Suite class.
 * Uses SanityTestFilter to exclude the long running tests
 *
 *
 *@author Rahul Jain
 */

public class CustomSuite extends Suite {
	
	public CustomSuite(Class<?> klass) throws InitializationError {
		super(klass);
		try {
		this.filter(new SanityTestFilter());
		} catch (Exception e) {
			
		}
	}
	
	protected CustomSuite(Class<?> klass, Class<?>[] annotatedClasses) throws InitializationError {
		super(klass, annotatedClasses);
		try {
			this.filter(new SanityTestFilter());
		} catch (Exception e) {
			
		}
				
			
	}

}