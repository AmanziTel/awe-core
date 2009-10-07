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

import org.jruby.Ruby;

/**
 * Task that initializes Splash in Ruby
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class InitializeSplashTask implements SplashJobTask {
    
	//script to run
    private String script;
    
    //Ruby runtime
    private Ruby runtime;
    
    /**
     * Constructor.
     *  
     * @param runtime runtime 
     * @param script script to run
     */
    public InitializeSplashTask(Ruby runtime, String script) {
        this.runtime = runtime;
        this.script = script;
    }

    @Override
    public SplashJobTaskResult execute() {
        runtime.evalScriptlet(script);
        
        return SplashJobTaskResult.CONTINUE;
    }
}
