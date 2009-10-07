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

package org.amanzi.splash.job;

/**
 * An Interface for Task that will be executed by Splash Job
 *
 * @author Lagutko_N
 * @since 1.0.0
 */
public interface SplashJobTask {
	
	/**
	 * A Result of Task executing
	 * 
	 * @author Lagutko_N
	 */
	
	public enum SplashJobTaskResult {
		//job should be finished
        EXIT,
        //job should be continued
        CONTINUE
    }
    
	/**
	 * Method that will be executed by Splash Job
	 * 
	 * @return result of executing
	 */
    public SplashJobTaskResult execute();
    
}