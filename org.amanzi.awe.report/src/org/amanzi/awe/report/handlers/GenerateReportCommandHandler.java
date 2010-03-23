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

package org.amanzi.awe.report.handlers;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.report.ReportPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public class GenerateReportCommandHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
            final IMap activeMap = ApplicationGIS.getActiveMap();
//            ApplicationGIS.getOpenMaps().iterator().hasNext()
            if (activeMap!=ApplicationGIS.NO_MAP){
                try {
                final Ruby ruby = ReportPlugin.getDefault().getRubyRuntime();
                String script = "report 'report0' do\n  map 'map0' do |map|\n map.active=true\n  end\nend";
//            String script = "report";
                System.out.println("script: "+script);
                final IRubyObject evalScriptlet = ruby.evalScriptlet(script);
//            System.out.println("[DEBUG] result="+evalScriptlet);
            }catch (Exception e){
                e.printStackTrace();
            }
                
            }else{
                //show message
                System.out.println("There is no active map!");
            }
        return null;
    }

}
