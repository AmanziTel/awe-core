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

package org.amanzi.awe.scripting.testing;

import org.amanzi.awe.scripting.AbstractScriptingPlugin;

/**
 * Fake activator for testing
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TestActivator extends AbstractScriptingPlugin {
    final static String SCRIPT_PATH = "ruby/netview/";
    public final static String ID = "org.amanzi.awe.scripting.testing";

    @Override
    public String getScriptPath() {
        return SCRIPT_PATH;
    }
}
